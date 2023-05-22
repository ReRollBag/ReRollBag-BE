package com.ReRollBag.service;

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
}
