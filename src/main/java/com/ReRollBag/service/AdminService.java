package com.ReRollBag.service;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.dto.Users.UsersLoginResponseDto;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import com.ReRollBag.exceptions.adminExceptions.UserIsNotAdminException;
import com.ReRollBag.repository.BagsRepository;
import com.ReRollBag.repository.CertificationNumberRepository;
import com.ReRollBag.repository.UsersRepository;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class AdminService {
    private final UsersRepository usersRepository;
    private final UsersService usersService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BagsRepository bagsRepository;
    private final BagsService bagsService;
    private final CertificationNumberRepository certificationNumberRepository;

    public UsersLoginResponseDto loginForAdmin(String idToken) throws FirebaseAuthException, UserIsNotAdminException {
        String UID = usersService.getUIDFromIdToken(idToken);
        Users users = usersRepository.findById(UID).orElseThrow(() -> new IllegalArgumentException("Cannot find users at AdminService.loginForAdmin"));
        UserRole userRole = users.getUserRole();
        String usersId = users.getUsersId();

        if (userRole.equals(UserRole.ROLE_ADMIN))
            throw new UserIsNotAdminException();

        return usersService.createToken(UID, usersId);
    }

    // 1. Find user with token
    // 2. Check if user is already admin, if already user is admin, throw `UsersIsAlreadyAdminException`
    // 3. Generate 4-digit random certificationNumber, each number range is 1-9;
    // 4. Save certificationNumber at redis
    public void requestAdmin(String token) {

    }

    // 1. Find user with token
    // 2. Find certificationNumber in redis with user.usersId
    // 2-1. If it is not able to find CertificationNumber, throw `CertificationTimeExpireException`
    // 3. Compare two certificationNumber.
    // 3-1. If both are different, throw `CertificationSignatureException`
    // 4. Change user.userRole from user to admin (upgradeUsersToAdmin)
    public void verifyAdminRequestCertificationNumber(String token, Long certificationNumber, String region) {

    }

    // 1. Find user with usersId
    // 2. Change user.userRole to ROLE_ADMIN
    // 3. Change user.region to specified region
    private void upgradeUsersToAdmin(String region, String usersId) {

    }
}
