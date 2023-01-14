package com.ReRollBag.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    UsersIdAlreadyExistException(1000),
    UsersIdOrPasswordInvalidException(1001),

    ExpiredJwtException(2000),
    ReIssueBeforeAccessTokenExpiredException(2001),
    TokenIsNullException(2002),
    SignatureException(2003),

    UnknownException(5000);

    private final int errorCode;
}
