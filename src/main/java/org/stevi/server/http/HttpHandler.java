package org.stevi.server.http;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.stevi.server.http.model.HttpRequest;
import org.stevi.server.http.model.HttpResponse;
import org.stevi.server.server.Handler;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Optional;

@Slf4j
public class HttpHandler implements Handler {

    @SneakyThrows
    public void handle(InputStream inputStream, OutputStream outputStream) {

        Optional<HttpRequest> httpRequest = HttpRequestReader.decode(inputStream);

        HttpRequest request = httpRequest.get();

        log.info("Incoming http {} request {}", request.getHttpMethod(), request.getUri());


        HttpResponse response = HttpResponse.builder()
                .statusCode(200)
                .entity(Optional.of("Hello world"))
                .build();

        var bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

        HttpResponseWriter.writeResponse(bufferedWriter, response);

        bufferedWriter.close();
        inputStream.close();
    }
}
