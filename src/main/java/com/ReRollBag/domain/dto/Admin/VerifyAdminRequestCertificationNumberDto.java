package com.ReRollBag.domain.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class VerifyAdminRequestCertificationNumberDto {
    private String region;
    private int certificationNumber;
}
