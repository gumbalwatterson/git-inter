package com.gum.git.inter.exception;

import org.springframework.http.HttpStatusCode;

public class GithubApiClientException extends GithubApiException {

    public GithubApiClientException(String body, HttpStatusCode statusCode) {
        super(body, statusCode);
    }
}
