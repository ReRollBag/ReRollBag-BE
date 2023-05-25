package com.ReRollBag.service;

import com.ReRollBag.auth.JwtTokenProvider;
import com.ReRollBag.domain.entity.CertificationNumber;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import com.ReRollBag.exceptions.adminExceptions.CertificationSignatureException;
import com.ReRollBag.exceptions.adminExceptions.CertificationTimeExpireException;
import com.ReRollBag.exceptions.adminExceptions.UsersIsAlreadyAdminException;
import com.ReRollBag.repository.BagsRepository;
import com.ReRollBag.repository.CertificationNumberRepository;
import com.ReRollBag.repository.UsersRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Log4j2
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
            .certificationNumber("1234")
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
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void resetTestUsersRole() {
        testUsers.setUserRole(UserRole.ROLE_USER);
    }

    @Test
    @DisplayName("[Service] Request Admin 테스트")
    public void Service_RequestAdmin_테스트() throws UsersIsAlreadyAdminException {
        resetTestUsersRole();

        String token = "testToken";

        when(jwtTokenProvider.getUsersId(any())).thenReturn(testUsers.getUsersId());
        when(usersRepository.findByUsersId(any())).thenReturn(testUsers);
        when(certificationNumberRepository.save(any())).thenReturn(testCertificationNumber);
        when(passwordEncoder.encode(any())).thenReturn(String.valueOf(testCertificationNumber));

        adminService.requestAdmin(token);

        verify(jwtTokenProvider).getUsersId(token);
        verify(usersRepository).findByUsersId(testUsers.getUsersId());
        verify(certificationNumberRepository).save(any());
        verify(passwordEncoder).encode(any());
    }

    @Test
    @DisplayName("[Service] Request Admin : UsersIsAlreadyAdminException 테스트")
    public void Service_RequestAdmin_UsersIsAlreadyAdminException_테스트() {
        String token = "testToken";

        when(jwtTokenProvider.getUsersId(any())).thenReturn(testAdmin.getUsersId());
        when(usersRepository.findByUsersId(any())).thenReturn(testAdmin);

        assertThrows(UsersIsAlreadyAdminException.class, () -> adminService.requestAdmin(token));
    }

    @Test
    @DisplayName(("[Service] Verify Admin_Request_CertificationNumber 테스트"))
    public void Service_verifyAdminRequestCertificationNumber_테스트() throws CertificationTimeExpireException, CertificationSignatureException {
        String token = "testToken";
        int certificationNumber = 1234;
        String region = "KOR_SUWON";

        when(certificationNumberRepository.findById(any())).thenReturn(Optional.of(testCertificationNumber));
        when(jwtTokenProvider.getUsersId(any())).thenReturn(testUsers.getUsersId());
        when(usersRepository.findByUsersId(any())).thenReturn(testUsers);
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        adminService.verifyAdminRequestCertificationNumber(token, certificationNumber, region);

        verify(jwtTokenProvider).getUsersId(token);
        verify(usersRepository).findByUsersId(testUsers.getUsersId());
        verify(certificationNumberRepository).findById(testUsers.getUsersId());
        verify(passwordEncoder).matches(any(), any());

        assertThat(testUsers.getUserRole()).isEqualTo(UserRole.ROLE_ADMIN);
        assertThat(testUsers.getManagingRegion()).isEqualTo(region);
    }

    @Test
    @DisplayName(("[Service] Verify Admin_Request_CertificationNumber : CertificationTimeExpireException 테스트"))
    public void Service_verifyAdminRequestCertificationNumber_CertificationTimeExpireException_테스트() {
        String token = "testToken";
        int certificationNumber = 1234;
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
        int wrongCertificationNumber = 9999;
        String region = "KOR_SUWON";

        when(certificationNumberRepository.findById(any())).thenReturn(Optional.of(testCertificationNumber));
        when(jwtTokenProvider.getUsersId(any())).thenReturn(testUsers.getUsersId());
        when(usersRepository.findByUsersId(any())).thenReturn(testUsers);
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(CertificationSignatureException.class, () -> adminService.verifyAdminRequestCertificationNumber(token, wrongCertificationNumber, region));

        verify(passwordEncoder).matches(any(), any());
    }

}
