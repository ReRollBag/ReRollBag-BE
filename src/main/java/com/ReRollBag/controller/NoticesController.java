package com.ReRollBag.controller;

import com.ReRollBag.domain.dto.Notices.NoticesSaveRequestDto;
import com.ReRollBag.service.NoticesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NoticesController extends BaseController {
    private final NoticesService noticesService;

    @PostMapping("api/v3/notices/save")
    public ResponseEntity<?> save(@RequestBody NoticesSaveRequestDto requestDto) {
        return sendResponseHttpByJson(noticesService.save(requestDto));
    }

    @GetMapping("api/v1/notices/getLastNotices")
    public ResponseEntity<?> getLastNotices() {
        return sendResponseHttpByJson(noticesService.getLastNotices());
    }

    @GetMapping("api/v1/notices/getAllNotices")
    public ResponseEntity<?> getAllNotices() {
        return sendResponseHttpByJson(noticesService.getAllNotices());
    }
}
