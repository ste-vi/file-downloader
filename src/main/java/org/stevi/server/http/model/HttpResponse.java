package org.stevi.server.http.model;

import lombok.Builder;
import lombok.Getter;
import org.stevi.server.http.enumeration.HttpStatusCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class HttpResponse implements Serializable {

    private final HttpStatusCode statusCode;
    private final Object entity;
    private final Map<String, List<String>> responseHeaders;
}
