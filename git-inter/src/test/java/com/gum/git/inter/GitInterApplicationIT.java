package com.gum.git.inter;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.gum.git.inter.model.BranchDto;
import com.gum.git.inter.model.RepositoryDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.io.IOException;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableWireMock
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"github.api.base-url=http://localhost:${wiremock.server.port}"},
        locations = "classpath:application-test.properties")
public class GitInterApplicationIT {

    @InjectWireMock
    private WireMockServer wireMockServer;
    @Autowired
    private TestRestTemplate testRestTemplate;

    private static final int GITHUB_API_REPOS_RESPONSE_TIME = 3000;
    private static final int GITHUB_API_BRANCHES_RESPONSE_TIME = 1000;
    private static final int EXPECTED_REPOS_NUMBER = 4;
    private static final int EXPECTED_BRANCH_NUMBER = 12;
    private static final int EXPECTED_EXECUTION_TIME = 5000;

    @Test
    void contextLoads() throws IOException {
        String testUser = "testUser";
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/users/%s/repos".formatted(testUser)))
                .willReturn(WireMock.aResponse()
                        .withFixedDelay(GITHUB_API_REPOS_RESPONSE_TIME)
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new ClassPathResource("json-responses/githubApiReposRes.json")
                                .getContentAsString(UTF_8))));

        List<String> repoNames = getListOfReposNames();
        for (String repoName : repoNames) {
            wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo(
                            "/repos/%s/%s/branches".formatted(testUser, repoName)))
                    .willReturn(WireMock.aResponse()
                            .withFixedDelay(GITHUB_API_BRANCHES_RESPONSE_TIME)
                            .withStatus(HttpStatus.OK.value())
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .withBody(new ClassPathResource("json-responses/githubApiBranchRes.json")
                                    .getContentAsString(UTF_8))));
        }

        String nonForkReposUri = "/api/nonfork/repos/%s".formatted(testUser);


        long startTime = System.currentTimeMillis();
        ResponseEntity<List<RepositoryDto>> response = testRestTemplate.exchange(
                nonForkReposUri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });
        long endTime = System.currentTimeMillis();
        long timeResult = endTime - startTime;
        List<RepositoryDto> repositoryDtos = response.getBody();
        assertNotNull(repositoryDtos);
        List<BranchDto> branchDtos = extractResultBranches(repositoryDtos);
        List<String> extractedRepoNames = extractResultRepositoriesNames(repositoryDtos);
        assertTrue(extractedRepoNames.containsAll(expectedListOfReposNames()));
        assertEquals(EXPECTED_BRANCH_NUMBER, branchDtos.size());
        assertEquals(EXPECTED_REPOS_NUMBER, repositoryDtos.size());
        assertTrue(EXPECTED_EXECUTION_TIME > timeResult);
    }

    private static List<String> getListOfReposNames() {
        return List.of("actuator-dashboard", "admin-dashboard", "admin-test",
                "admin-per", "test-repo-five");
    }

    private static List<String> expectedListOfReposNames() {
        return List.of("actuator-dashboard", "admin-dashboard", "admin-test", "test-repo-five");
    }

    private List<BranchDto> extractResultBranches(List<RepositoryDto> repositoryDtos) {
        return repositoryDtos.stream().flatMap(dto -> dto.branches().stream()).toList();
    }

    private List<String> extractResultRepositoriesNames(List<RepositoryDto> repositoryDtos) {
        return repositoryDtos.stream().map(RepositoryDto::repositoryName).toList();
    }
}
