package com.gum.git.inter.controller;

import com.gum.git.inter.model.RepositoryDto;
import com.gum.git.inter.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.gum.git.inter.constant.GithubConstant.NON_FORK_REPOS;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GithubIntegrationController {

    private final GithubService githubService;

    @GetMapping(value = NON_FORK_REPOS, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RepositoryDto> getNonForkUserRepositories(@PathVariable String username) {
        return githubService.findNonForkRepositoriesWithBranches(username);
    }
}
