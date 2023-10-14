package org.stevi.server.http;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.stevi.server.http.model.HttpRequest;
import org.stevi.server.http.model.HttpResponse;
import org.stevi.server.server.Handler;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

@Slf4j
public class HttpHandler implements Handler {

    @SneakyThrows
    public void handle(InputStream inputStream, OutputStream outputStream) {

        Optional<HttpRequest> optRequest = HttpRequestReader.decodeRequest(inputStream);
        HttpRequest request = optRequest.orElseThrow(() -> new RuntimeException("Cannot consume http request"));

        log.info("Incoming http {} request {}", request.getHttpMethod(), request.getUri());

        HttpResponse response = HttpResponse.builder()
                .statusCode(200)
                .entity("Hello world")
                .build();

        HttpResponseWriter.writeResponse(outputStream, response);

        //outputStream.close();
        //inputStream.close();
    }
}
