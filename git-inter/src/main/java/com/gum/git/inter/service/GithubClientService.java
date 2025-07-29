package com.gum.git.inter.service;

import com.gum.git.inter.model.GitBranch;
import com.gum.git.inter.model.GitRepos;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

import static com.gum.git.inter.constant.GithubConstant.GITHUB_API_BRANCHES;
import static com.gum.git.inter.constant.GithubConstant.GITHUB_API_REPOS;

@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE)
public interface GithubClientService {

    @GetExchange(url = GITHUB_API_REPOS)
    ResponseEntity<List<GitRepos>> getRepositories(@PathVariable String username);

    @GetExchange(url = GITHUB_API_BRANCHES)
    ResponseEntity<List<GitBranch>> getBranches(@PathVariable String username, @PathVariable String repository);

}
