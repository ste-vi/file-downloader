package org.stevi.server.http;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.stevi.server.http.enumeration.HttpStatusCode;
import org.stevi.server.http.model.HttpRequest;
import org.stevi.server.http.model.HttpResponse;
import org.stevi.server.server.Handler;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class HttpHandler implements Handler {

    private final HttpRequestReader requestReader = new HttpRequestReader();
    private final HttpResponseWriter responseWriter = new HttpResponseWriter();

    @SneakyThrows
    public void handle(InputStream inputStream, OutputStream outputStream) {

        getRequest(inputStream);

        writeResponse(outputStream);

        outputStream.close();
        inputStream.close();
    }

    private void getRequest(InputStream inputStream) {
        Optional<HttpRequest> optRequest = requestReader.decodeRequest(inputStream);
        HttpRequest request = optRequest.orElseThrow(() -> new RuntimeException("Cannot consume http request"));

        log.info("Incoming http {} request {}", request.getHttpMethod(), request.getUri());
    }

    private void writeResponse(OutputStream outputStream) {
        String entity = "Hello world";
        HttpResponse response = HttpResponse.builder()
                .statusCode(HttpStatusCode.OK)
                .entity(entity)
                .responseHeaders(Map.of("Content-Length", List.of(String.valueOf(entity.length()))))
                .responseHeaders(Map.of("Content-Type", List.of("application/json")))
                .build();

        responseWriter.writeResponse(outputStream, response);
    }
}
