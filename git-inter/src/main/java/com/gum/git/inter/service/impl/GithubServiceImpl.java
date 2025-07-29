package com.gum.git.inter.service.impl;

import com.gum.git.inter.model.*;
import com.gum.git.inter.service.GithubClientService;
import com.gum.git.inter.service.GithubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubServiceImpl implements GithubService {

    private final GithubClientService githubClientService;
    private final AsyncTaskExecutor asyncTaskExecutor;

    @Override
    public List<RepositoryDto> findNonForkRepositoriesWithBranches(String username) {
        ResponseEntity<List<GitRepos>> gitReposRes = githubClientService.getRepositories(username);
        List<GitRepos> gitRepos = gitReposRes.getBody();
        if (gitRepos != null) {
            log.info("Retrieve repositories: {} for user: {}", gitRepos.size(), username);
            List<CompletableFuture<RepositoryDto>> completableFutures = gitRepos.stream()
                    .filter(r -> !r.fork())
                    .map(r -> asyncTaskExecutor.submitCompletable(() ->
                            new RepositoryDto(username, r.name(), finBranches(r.name(), username))))
                    .toList();

            return completableFutures.stream().map(c -> {
                try {
                    return c.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }).toList();

        }
        log.info("No repositories for user: {}", username);
        return Collections.emptyList();
    }

    private List<BranchDto> finBranches(String repoName, String username) {
        ResponseEntity<List<GitBranch>> gitBranchRes = githubClientService.getBranches(username, repoName);
        List<GitBranch> gitBranches = gitBranchRes.getBody();
        return gitBranches != null ? gitBranches.stream()
                .map(b -> new BranchDto(b.name(), b.commit().sha())).toList() : Collections.emptyList();

    }
}
