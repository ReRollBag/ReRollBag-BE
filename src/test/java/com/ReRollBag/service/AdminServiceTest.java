package com.ReRollBag.service;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.entity.CertificationNumber;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import com.ReRollBag.exceptions.adminExceptions.CertificationTimeExpireException;
import com.ReRollBag.exceptions.adminExceptions.UsersIsAlreadyAdminException;
import com.ReRollBag.repository.BagsRepository;
import com.ReRollBag.repository.CertificationNumberRepository;
import com.ReRollBag.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    private static final Users testUsers = Users.builder()
            .usersId("test@gmail.com")
            .name("testUser")
            .userRole(UserRole.ROLE_USER)
            .UID("testUID")
            .build();
    private static final Users testAdmin = Users.builder()
            .usersId("admin@gmail.com")
            .name("admin")
            .userRole(UserRole.ROLE_ADMIN)
            .UID("adminUID")
            .build();
    private static final CertificationNumber testCertificationNumber = CertificationNumber.builder()
            .certificationNumber(1234)
            .expiredTime(5L)
            .usersId(testUsers.getUsersId())
            .build();
    @InjectMocks
    private AdminService adminService;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private UsersService usersService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private BagsRepository bagsRepository;
    @Mock
    private BagsService bagsService;
    @Mock
    private CertificationNumberRepository certificationNumberRepository;

    @Test
    @DisplayName("[Service] Request Admin 테스트")
    public void Service_RequestAdmin_테스트() throws UsersIsAlreadyAdminException {
        String token = "testToken";

        when(jwtTokenProvider.getUsersId(any())).thenReturn(testUsers.getUsersId());
        when(usersRepository.findByUsersId(any())).thenReturn(testUsers);
        when(certificationNumberRepository.save(any())).thenReturn(testCertificationNumber);

        verify(adminService).requestAdmin(token);
    }

    @Test
    @DisplayName("[Service] Request Admin : UsersIsAlreadyAdminException 테스트")
    public void Service_RequestAdmin_UsersIsAlreadyAdminException_테스트() {
        String token = "testToken";

        when(jwtTokenProvider.getUsersId(any())).thenReturn(testAdmin.getUsersId());
        when(usersRepository.findByUsersId(any())).thenReturn(testAdmin);
        when(certificationNumberRepository.save(any())).thenReturn(testCertificationNumber);

        assertThrows(UsersIsAlreadyAdminException.class, () -> adminService.requestAdmin(token));
    }

    @Test
    @DisplayName(("[Service] Verify Admin_Request_CertificationNumber 테스트"))
    public void Service_verifyAdminRequestCertificationNumber_테스트() {
        String token = "testToken";
        Long certificationNumber = 1234L;
        String region = "KOR_SUWON";

        when(certificationNumberRepository.findById(any())).thenReturn(Optional.of(testCertificationNumber));
        when(jwtTokenProvider.getUsersId(any())).thenReturn(testUsers.getUsersId());
        when(usersRepository.findByUsersId(any())).thenReturn(testUsers);

        verify(adminService).verifyAdminRequestCertificationNumber(token, certificationNumber, region);
    }

    @Test
    @DisplayName(("[Service] Verify Admin_Request_CertificationNumber : CertificationTimeExpireException 테스트"))
    public void Service_verifyAdminRequestCertificationNumber_CertificationTimeExpireException_테스트() {
        String token = "testToken";
        Long certificationNumber = 1234L;
        String region = "KOR_SUWON";

        when(certificationNumberRepository.findById(any())).thenReturn(Optional.empty());
        when(jwtTokenProvider.getUsersId(any())).thenReturn(testUsers.getUsersId());
        when(usersRepository.findByUsersId(any())).thenReturn(testUsers);

        assertThrows(CertificationTimeExpireException.class, () -> adminService.verifyAdminRequestCertificationNumber(token, certificationNumber, region));
    }

    @Test
    @DisplayName(("[Service] Verify Admin_Request_CertificationNumber : CertificationSignatureException 테스트"))
    public void Service_verifyAdminRequestCertificationNumber_CertificationSignatureException_테스트() {
        String token = "testToken";
        Long wrongCertificationNumber = 9999L;
        String region = "KOR_SUWON";

        when(certificationNumberRepository.findById(any())).thenReturn(Optional.of(testCertificationNumber));
        when(jwtTokenProvider.getUsersId(any())).thenReturn(testUsers.getUsersId());
        when(usersRepository.findByUsersId(any())).thenReturn(testUsers);

        assertThrows(CertificationTimeExpireException.class, () -> adminService.verifyAdminRequestCertificationNumber(token, wrongCertificationNumber, region));
    }
}
