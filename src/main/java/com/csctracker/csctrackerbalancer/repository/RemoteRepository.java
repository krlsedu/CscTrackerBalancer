package com.csctracker.csctrackerbalancer.repository;

import com.csctracker.configs.UnAuthorized;
import com.csctracker.service.RequestInfo;
import com.csctracker.service.UserInfoRemoteService;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class RemoteRepository {

    private final UserInfoRemoteService userInfoRemoteService;

    public RemoteRepository(UserInfoRemoteService userInfoRemoteService) {
        this.userInfoRemoteService = userInfoRemoteService;
    }

    public static void checkResponse(HttpResponse<String> response) {
        if (response.getStatus() < 200 || response.getStatus() > 299) {
            switch (response.getStatus()) {
                case 401:
                case 403:
                    throw new UnAuthorized();
                case 404:
                    throw new RuntimeException("Not Found");
                default:
                    throw new RuntimeException("Internal Server Error");
            }
        }
    }

    public String dispachGet(String url) {
        log.info("Dispatching GET to " + url);
        var request = Unirest.get(url);

        var headers = RequestInfo.getHeaders();

        if (request.getHeaders().get("userName") == null || request.getHeaders().get("userName").isEmpty()) {
            request.header("userName", userInfoRemoteService.getUser().getEmail());
        }

        for (var header : headers.entrySet()) {
            switch (header.getKey().toLowerCase()) {
                case "content-type":
                case "authorization":
                    request.header(header.getKey(), header.getValue());
                    break;
                default:
                    break;
            }
        }

        var parameters = RequestInfo.getParameters();
        for (var parameter : parameters.entrySet()) {
            request.queryString(parameter.getKey(), parameter.getValue());
        }

        HttpResponse<String> response = request
                .asString();
        try {
            checkResponse(response);
        } catch (Exception e) {
            log.error("Error dispatching GET to " + url, e);
            throw e;
        }

        String body = response.getBody();
        return body;
    }

    public String dispachPost(String url) {
        log.info("Dispatching POST to " + url);
        return dispach(Unirest.post(url));
    }

    public String dispachDelete(String url) {
        log.info("Dispatching DELETE to " + url);
        return dispach(Unirest.delete(url));
    }

    public String dispach(HttpRequestWithBody request) {
        var headers = RequestInfo.getHeaders();

        if (request.getHeaders().get("userName") == null || request.getHeaders().get("userName").isEmpty()) {
            request.header("userName", userInfoRemoteService.getUser().getEmail());
        }

        for (var header : headers.entrySet()) {
            switch (header.getKey().toLowerCase()) {
                case "content-type":
                case "authorization":
                    request.header(header.getKey(), header.getValue());
                    break;
                default:
                    break;
            }
        }

        HttpResponse<String> response = request
                .body(RequestInfo.getBody())
                .asString();
        try {
            checkResponse(response);
        } catch (Exception e) {
            log.error("Error dispatching POST to " + request.getUrl(), e);
            throw e;
        }

        return response.getBody();
    }
}
