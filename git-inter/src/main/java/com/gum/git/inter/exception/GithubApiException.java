package com.gum.git.inter.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class GithubApiException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final String body;

    public GithubApiException(String body, HttpStatusCode statusCode) {
        super(body);
        this.statusCode = statusCode;
        this.body = body;
    }
}
