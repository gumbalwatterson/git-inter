package com.gum.git.inter.model;

import java.util.List;

public record RepositoryDto(String owner, String repositoryName, List<BranchDto> branches) {
}
