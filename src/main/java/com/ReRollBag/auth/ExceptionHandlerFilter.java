package com.ReRollBag.auth;

import com.ReRollBag.exceptions.ErrorCode;
import com.ReRollBag.exceptions.ErrorJson;
import com.fasterxml.jackson.core.JsonProcessingException;
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
        try{
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            setErrorResponse(HttpStatus.ACCEPTED, response, e, ErrorCode.UnknownException);
        }
    }

    public void setErrorResponse (HttpStatus httpStatus, HttpServletResponse response, Throwable e, ErrorCode errorCode) {
        ErrorJson errorJson = ErrorJson.builder()
                .message(e.getMessage())
                .errorCode(ErrorCode.UnknownException)
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
