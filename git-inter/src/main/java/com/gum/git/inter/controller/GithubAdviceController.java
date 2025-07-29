package com.gum.git.inter.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gum.git.inter.exception.GithubApiClientException;
import com.gum.git.inter.exception.GithubApiException;
import com.gum.git.inter.exception.GithubApiServerException;
import com.gum.git.inter.model.GithubApiError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GithubAdviceController {

    private final ObjectMapper mapper;

    @ExceptionHandler(GithubApiClientException.class)
    public ResponseEntity<GithubApiError> handleClientError(GithubApiException ex) {
        GithubApiError error = handlerResponseCreate(ex);
        return new ResponseEntity<>(error, HttpStatus.valueOf(ex.getStatusCode().value()));
    }

    @ExceptionHandler(GithubApiServerException.class)
    public ResponseEntity<GithubApiError> handleServerError(GithubApiException ex) {
        GithubApiError error = handlerResponseCreate(ex);
        return new ResponseEntity<>(error, HttpStatus.valueOf(ex.getStatusCode().value()));
    }

    private GithubApiError handlerResponseCreate(GithubApiException ex) {
        GithubApiError error;
        try {
            error = mapper.readValue(ex.getBody(), GithubApiError.class);
        } catch (JsonProcessingException e) {
            log.error("Problem while parsing, Message: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        if (error.getStatus() == null) error.setStatus(String.valueOf(ex.getStatusCode()));
        return error;
    }
}
