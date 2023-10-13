package org.stevi.server.http.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Builder
public class HttpResponse {

    private final Map<String, List<String>> responseHeaders;
    private final int statusCode;
    private final Optional<Object> entity;
}
