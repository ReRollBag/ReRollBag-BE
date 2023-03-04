package com.ReRollBag.controller;

import com.ReRollBag.domain.dto.Bags.BagsRentOrReturnRequestDto;
import com.ReRollBag.domain.dto.Bags.BagsSaveRequestDto;
import com.ReRollBag.service.BagsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> renting(@RequestBody BagsRentOrReturnRequestDto requestDto) {
        return sendResponseHttpByJson(bagsService.rentOrReturn(requestDto));
    }

    @PostMapping("api/v3/bags/returning")
    public ResponseEntity<?> returning(@RequestBody BagsRentOrReturnRequestDto requestDto) {
        return sendResponseHttpByJson(bagsService.rentOrReturn(requestDto));
    }


}
