package com.ReRollBag.controller;

import com.ReRollBag.domain.dto.Admin.VerifyAdminRequestCertificationNumberDto;
import com.ReRollBag.exceptions.adminExceptions.CertificationSignatureException;
import com.ReRollBag.exceptions.adminExceptions.CertificationTimeExpireException;
import com.ReRollBag.exceptions.adminExceptions.UserIsNotAdminException;
import com.ReRollBag.exceptions.adminExceptions.UsersIsAlreadyAdminException;
import com.ReRollBag.service.AdminService;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AdminController extends BaseController {
    private final AdminService adminService;

    @PostMapping("api/v2/users/loginForAdmin")
    public ResponseEntity<?> loginForAdmin(@RequestHeader("token") String idToken) throws FirebaseAuthException, UserIsNotAdminException {
        return sendResponseHttpByJson(adminService.loginForAdmin(idToken));
    }

    @PostMapping("api/v1/users/requestAdmin")
    public ResponseEntity<?> requestAdmin(@RequestHeader("token") String jwtToken) throws UsersIsAlreadyAdminException {
        return sendResponseHttpByJson(adminService.requestAdmin(jwtToken));
    }

    @PutMapping("api/v1/users/verifyAdminRequestCertificationNumber")
    public ResponseEntity<?> verifyAdminRequestCertificationNumber(@RequestHeader("token") String jwtToken, @RequestBody VerifyAdminRequestCertificationNumberDto verifyAdminRequestCertificationNumberDto) throws CertificationTimeExpireException, CertificationSignatureException {
        return sendResponseHttpByJson(adminService.verifyAdminRequestCertificationNumber(jwtToken, verifyAdminRequestCertificationNumberDto.getCertificationNumber(), verifyAdminRequestCertificationNumberDto.getRegion()));
    }
}
