package com.ReRollBag.service;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.Users.UsersLoginResponseDto;
import com.ReRollBag.domain.entity.CertificationNumber;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import com.ReRollBag.exceptions.adminExceptions.CertificationSignatureException;
import com.ReRollBag.exceptions.adminExceptions.CertificationTimeExpireException;
import com.ReRollBag.exceptions.adminExceptions.UserIsNotAdminException;
import com.ReRollBag.exceptions.adminExceptions.UsersIsAlreadyAdminException;
import com.ReRollBag.repository.BagsRepository;
import com.ReRollBag.repository.CertificationNumberRepository;
import com.ReRollBag.repository.UsersRepository;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {
    private final UsersRepository usersRepository;
    private final UsersService usersService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BagsRepository bagsRepository;
    private final BagsService bagsService;
    private final CertificationNumberRepository certificationNumberRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersLoginResponseDto loginForAdmin(String idToken) throws FirebaseAuthException, UserIsNotAdminException {
        String UID = usersService.getUIDFromIdToken(idToken);
        Users users = usersRepository.findById(UID).orElseThrow(() -> new IllegalArgumentException("Cannot find users at AdminService.loginForAdmin"));
        UserRole userRole = users.getUserRole();
        String usersId = users.getUsersId();

        if (userRole.equals(UserRole.ROLE_ADMIN))
            throw new UserIsNotAdminException();

        return usersService.createToken(UID, usersId);
    }

    public void requestAdmin(String token) throws UsersIsAlreadyAdminException {
        // 1. Find user with token
        String targetUsersId = jwtTokenProvider.getUsersId(token);
        Users targetUsers = usersRepository.findByUsersId(targetUsersId);

        // 2. Check if user is already admin, if already user is admin, throw `UsersIsAlreadyAdminException`
        if (targetUsers.getUserRole().equals(UserRole.ROLE_ADMIN))
            throw new UsersIsAlreadyAdminException();

        // 3. Generate 4-digit random certificationNumber, each number range is 1-9, and log number
        int randomCertificationNumber = generateRandomCertificationNumber();
        log.info("Generated Certification Number for user : `" + targetUsersId + "` is `" + randomCertificationNumber + "`");
        // 4. Encode certificationNumber
        String encryptedCertificationNumber = passwordEncoder.encode(Integer.toString(randomCertificationNumber));

        // 5. Save encryptedCertificationNumber at redis
        CertificationNumber certificationNumber = CertificationNumber.builder()
                .usersId(targetUsersId)
                .certificationNumber(encryptedCertificationNumber)
                .expiredTime(60 * 5L)
                .build();
        certificationNumberRepository.save(certificationNumber);
    }

    private int generateRandomCertificationNumber() {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        return random.nextInt(10000) % 10000;
    }

    @Transactional
    public UsersLoginResponseDto verifyAdminRequestCertificationNumber(String token, int certificationNumber, String region) throws CertificationTimeExpireException, CertificationSignatureException {
        // 1. Find user with token
        String targetUsersID = jwtTokenProvider.getUsersId(token);
        Users targetUsers = usersRepository.findByUsersId(targetUsersID);
        // 2. Find certificationNumber in redis with user.usersId
        // 2-1. If it is not able to find CertificationNumber, throw `CertificationTimeExpireException`
        CertificationNumber targetCertificationNumber = certificationNumberRepository.findById(targetUsersID).orElseThrow(() -> new CertificationTimeExpireException());
        // 3. Compare two certificationNumber. Saved cNumber is encrypted, so use passwordEncoder.matches()
        // 3-1. If both are different, throw `CertificationSignatureException`
        if (!passwordEncoder.matches(Integer.toString(certificationNumber), targetCertificationNumber.getCertificationNumber()))
            throw new CertificationSignatureException();
        // 4. Change user.userRole from user to admin (upgradeUsersToAdmin)
        targetUsers.setUserRole(UserRole.ROLE_ADMIN);
        // 5. Change user.managingRegion to specified region
        targetUsers.setManagingRegion(region);
        // 6. Return new jwtToken for Admin
        return usersService.createToken(targetUsers.getUID(), targetUsersID);
    }

}
