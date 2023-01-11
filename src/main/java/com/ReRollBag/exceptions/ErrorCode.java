package com.ReRollBag.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    UsersIdAlreadyExistException(1000),
    UsersIdOrPasswordInvalidException(1001),
    AccessTokenExpiredException(2000),
    RefreshTokenExpiredException(2001),
    UnknownException(5000);

    private final int errorCode;

}
