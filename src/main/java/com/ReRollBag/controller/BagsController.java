package com.ReRollBag.controller;

import com.ReRollBag.domain.dto.Bags.BagsRentOrReturnRequestDto;
import com.ReRollBag.domain.dto.Bags.BagsSaveRequestDto;
import com.ReRollBag.exceptions.bagsExceptions.AlreadyRentedException;
import com.ReRollBag.exceptions.bagsExceptions.ReturnRequestUserMismatchException;
import com.ReRollBag.service.BagsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BagsController extends BaseController {
    private final BagsService bagsService;

    @PostMapping("api/v3/bags/save")
    public ResponseEntity<?> save(@RequestBody BagsSaveRequestDto requestDto) {
        return sendResponseHttpByJson(bagsService.save(requestDto));
    }

    @PostMapping("api/v2/bags/renting")
    public ResponseEntity<?> renting(@RequestBody BagsRentOrReturnRequestDto requestDto) throws AlreadyRentedException {
        return sendResponseHttpByJson(bagsService.renting(requestDto));
    }

    @PostMapping("api/v3/bags/returning/{bagsId}")
    public ResponseEntity<?> returning(@PathVariable String bagsId) {
        return sendResponseHttpByJson(bagsService.returning(bagsId));
    }

    @PostMapping("api/v2/bags/requestReturning")
    public ResponseEntity<?> requestReturning(@RequestBody BagsRentOrReturnRequestDto requestDto) throws ReturnRequestUserMismatchException {
        return sendResponseHttpByJson(bagsService.requestReturning(requestDto));
    }

}
