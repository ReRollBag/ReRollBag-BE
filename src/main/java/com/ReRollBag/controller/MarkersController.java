package com.ReRollBag.controller;

import com.ReRollBag.domain.dto.Markers.MarkersSaveRequestDto;
import com.ReRollBag.service.MarkersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MarkersController extends BaseController {
    private final MarkersService markersService;

    @PostMapping("api/v3/markers/save")
    public ResponseEntity<?> save(@RequestBody MarkersSaveRequestDto requestDto) {
        return sendResponseHttpByJson(markersService.save(requestDto));
    }
}
