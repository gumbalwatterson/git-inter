package com.gum.git.inter.exception;

import org.springframework.http.HttpStatusCode;

public class GithubApiServerException extends GithubApiException {

    public GithubApiServerException(String body, HttpStatusCode statusCode) {
        super(body, statusCode);
    }
}
