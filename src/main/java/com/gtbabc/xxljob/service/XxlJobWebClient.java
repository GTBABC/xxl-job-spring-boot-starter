package com.gtbabc.xxljob.service;

import com.gtbabc.xxljob.config.XxlJobProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * xxl-job login service
 */
@Slf4j
@Service
public class XxlJobWebClient {
    private final XxlJobProperties xxlJobProperties;

    public final WebClient webClient;

    public XxlJobWebClient(WebClient.Builder webClientBuilder, XxlJobProperties xxlJobProperties) {
        this.xxlJobProperties = xxlJobProperties;
        this.webClient = webClientBuilder.baseUrl(xxlJobProperties.getAdmin().getAddresses()).build();
    }

    private final static String XXL_JOB_LOGIN_IDENTITY = "xxl_job_login_token";

    private String LOGIN_COOKIE = null;

    public String get(String url, Map<String, String> queryParams) {
        return webClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path(url);
                    // 添加查询参数
                    queryParams.forEach(builder::queryParam);
                    return builder.build();
                })
                .cookie(XXL_JOB_LOGIN_IDENTITY, getLoginCookie())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String post(String url, Object requestBody, Map<String, String> headers, MediaType contentType) {
        return webClient.post()
                .uri(url)
                .contentType(contentType)
                .bodyValue(requestBody)
                .headers(httpHeaders -> headers.forEach(httpHeaders::set))
                .cookie(XXL_JOB_LOGIN_IDENTITY, getLoginCookie())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String login() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("userName", xxlJobProperties.getAdmin().getUsername());
        formData.add("password", xxlJobProperties.getAdmin().getPassword());

        Mono<List<String>> cookiesMono = webClient.post()
                .uri("/auth/doLogin")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .toEntity(String.class)
                .mapNotNull(response -> response.getHeaders().get(HttpHeaders.SET_COOKIE));

        return cookiesMono
                .flatMap(cookies -> cookies.stream()
                        .filter(cookie -> cookie.contains(XXL_JOB_LOGIN_IDENTITY))
                        .findFirst()
                        .map(Mono::just)
                        .orElse(Mono.error(new RuntimeException("Failed to get XXL_JOB_LOGIN_IDENTITY cookie"))))
                .map(cookie -> cookie.split(";")[0])
                .block();
    }

    public String getLoginCookie() {
        if (StringUtils.hasText(LOGIN_COOKIE)) {
            return LOGIN_COOKIE.split("=")[1];
        }

        for (int i = 0; i < 3; i++) {
            String loginIdentity = login();
            if (StringUtils.hasText(loginIdentity)) {
                LOGIN_COOKIE = loginIdentity;
                return LOGIN_COOKIE.split("=")[1];
            }
        }
        throw new RuntimeException("Failed to login xxl-job");
    }
}
