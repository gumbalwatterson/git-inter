package com.gum.git.inter.service;

import com.gum.git.inter.model.RepositoryDto;

import java.util.List;

public interface GithubService {
    List<RepositoryDto> findNonForkRepositoriesWithBranches(String username);
}
