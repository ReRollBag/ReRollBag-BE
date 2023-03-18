package com.ReRollBag.controller;

import com.ReRollBag.domain.dto.Users.UsersSaveRequestDto;
import com.ReRollBag.enums.BagsListType;
import com.ReRollBag.exceptions.usersExceptions.UsersIdAlreadyExistException;
import com.ReRollBag.exceptions.usersExceptions.UsersIdOrPasswordInvalidException;
import com.ReRollBag.service.UsersService;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class UsersController extends BaseController {
    private final UsersService usersService;

    @PostMapping("api/v2/users/save")
    public ResponseEntity<?> save(@RequestBody UsersSaveRequestDto requestDto) throws UsersIdOrPasswordInvalidException, FirebaseAuthException {
        return sendResponseHttpByJson(usersService.save(requestDto));
    }

    @PostMapping("api/v2/users/login")
    public ResponseEntity<?> login(@RequestHeader("token") String idToken) throws UsersIdOrPasswordInvalidException, FirebaseAuthException {
        return sendResponseHttpByJson(usersService.login(idToken));
    }

    @GetMapping("api/v2/users/checkUserExist/{usersId}")
    public ResponseEntity<?> checkUserExist(@PathVariable String usersId) throws UsersIdAlreadyExistException {
        return sendResponseHttpByJson(usersService.checkUserExist(usersId));
    }

    @GetMapping("api/v1/users/dummyMethod")
    public ResponseEntity<?> dummyMethod() {
        return sendResponseHttpByJson(usersService.dummyMethod());
    }

    @GetMapping("api/v3/users/dummyMethod")
    public ResponseEntity<?> dummyMethodForV3() {
        return sendResponseHttpByJson(usersService.dummyMethod());
    }

    @PostMapping("api/v2/users/reIssue")
    public ResponseEntity<?> reIssue(HttpServletRequest request) {
        return sendResponseHttpByJson(usersService.reIssue(request));
    }

    @DeleteMapping("api/v2/users/delete/{usersId}")
    public ResponseEntity<?> deleteDummy(@PathVariable String usersId) {
        return sendResponseHttpByJson(usersService.deleteDummy(usersId));
    }

    @GetMapping("api/v1/users/getRentingBagsList")
    public ResponseEntity<?> getRentingBagsList(@RequestHeader("token") String token) {
        return sendResponseHttpByJson(usersService.getBagsList(token, BagsListType.RentingBagsList));
    }

    @GetMapping("api/v1/users/getReturningBagsList")
    public ResponseEntity<?> getReturningBagsList(@RequestHeader("token") String token) {
        return sendResponseHttpByJson(usersService.getBagsList(token, BagsListType.ReturningBagsList));
    }

    @GetMapping("api/v1/users/getReturnedBagsList")
    public ResponseEntity<?> getReturnedBagsList(@RequestHeader("token") String token) {
        return sendResponseHttpByJson(usersService.getReturnedHistoryList(token));
    }

    @GetMapping("api/v1/users/getUsersInfo")
    public ResponseEntity<?> getUsersInfo(@RequestHeader("token") String token) {
        return sendResponseHttpByJson(usersService.getUsersInfo(token));

    }
}
