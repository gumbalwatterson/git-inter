package com.gum.git.inter.config;

import com.gum.git.inter.exception.GithubApiClientException;
import com.gum.git.inter.exception.GithubApiServerException;
import com.gum.git.inter.service.GithubClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.http.HttpClient;
import java.util.concurrent.Executors;


@Configuration
public class SystemConfig {

    @Value("${github.api.base-url:https://api.github.com/}")
    private String baseUrl;

    @Bean
    public GithubClientService githubService() {
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(new JdkClientHttpRequestFactory(
                        HttpClient.newBuilder()
                                .executor(Executors.newVirtualThreadPerTaskExecutor()).build()))
                .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {
                    String body = new String(res.getBody().readAllBytes());
                    if (res.getStatusCode().is4xxClientError()) {
                        throw new GithubApiClientException(body, res.getStatusCode());
                    }
                    if (res.getStatusCode().is5xxServerError()) {
                        throw new GithubApiServerException(body, res.getStatusCode());
                    }

                }).build();
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory proxy = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        return proxy.createClient(GithubClientService.class);
    }

    @Bean
    public AsyncTaskExecutor asyncTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
}
