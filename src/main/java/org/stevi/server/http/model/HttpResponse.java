package org.stevi.server.http.model;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class HttpResponse implements Serializable {

    private final int statusCode;
    private final Object entity;
    private final Map<String, List<String>> responseHeaders = Map.of();
}
