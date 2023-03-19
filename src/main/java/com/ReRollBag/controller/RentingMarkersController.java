package com.ReRollBag.controller;

import com.ReRollBag.domain.dto.RentingMarkers.RentingMarkersSaveRequestDto;
import com.ReRollBag.service.RentingMarkersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RentingMarkersController extends BaseController {
    private final RentingMarkersService rentingMarkersService;

    @PostMapping("api/v3/markers/renting/save")
    public ResponseEntity<?> save(@RequestBody RentingMarkersSaveRequestDto requestDto) throws Exception {
        return sendResponseHttpByJson(rentingMarkersService.save(requestDto));
    }

    @GetMapping("api/v1/markers/renting/findAll")
    public ResponseEntity<?> findAll() {
        return sendResponseHttpByJson(rentingMarkersService.findAll());
    }

}
