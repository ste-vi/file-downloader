package org.stevi.server.http.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HttpStatusCode {

    OK(200);

    private final int code;
}
