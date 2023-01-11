package com.ReRollBag.exceptions;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorJson {
    public String message;
    public CustomExceptions errorCode;
}
