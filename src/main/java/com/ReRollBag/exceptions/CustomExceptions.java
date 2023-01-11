package com.ReRollBag.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public enum CustomExceptions {
    UsersIdAlreadyExistException(1000);

    private final int errorCode;

}
