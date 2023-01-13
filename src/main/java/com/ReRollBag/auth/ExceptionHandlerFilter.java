package com.ReRollBag.auth;

import com.ReRollBag.exceptions.ErrorCode;
import com.ReRollBag.exceptions.ErrorJson;
import com.ReRollBag.exceptions.tokenExceptions.TokenIsNullException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }
        catch (TokenIsNullException e) {
            e.printStackTrace();
            setErrorResponse(HttpStatus.ACCEPTED, response, "TokenIsNullException", ErrorCode.TokenIsNullException);
        }
        catch (Exception e) {
            e.printStackTrace();
            setErrorResponse(HttpStatus.ACCEPTED, response, e.getMessage(), ErrorCode.UnknownException);
        }
    }

    public void setErrorResponse (HttpStatus httpStatus, HttpServletResponse response, String message, ErrorCode errorCode) {
        ErrorJson errorJson = ErrorJson.builder()
                .message(message)
                .errorCode(errorCode.getErrorCode())
                .build();

        response.setStatus(httpStatus.value());
        response.setContentType("application/json");

        try {
            String json = errorJson.convertToJson();
            response.getWriter().write(json);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
