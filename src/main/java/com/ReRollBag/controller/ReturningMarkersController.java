package com.ReRollBag.controller;

import com.ReRollBag.domain.dto.ReturningMarkers.ReturningMarkersSaveRequestDto;
import com.ReRollBag.service.ReturningMarkersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReturningMarkersController extends BaseController {
    private final ReturningMarkersService returningMarkersService;

    @PostMapping("api/v3/markers/returning/save")
    public ResponseEntity<?> save(@RequestBody ReturningMarkersSaveRequestDto requestDto) throws Exception {
        return sendResponseHttpByJson(returningMarkersService.save(requestDto));
    }

    @GetMapping("api/v1/markers/returning/findAll")
    public ResponseEntity<?> findAll() {
        return sendResponseHttpByJson(returningMarkersService.findAll());
    }

}
