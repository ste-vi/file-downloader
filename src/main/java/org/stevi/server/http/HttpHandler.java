package org.stevi.server.http;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.stevi.server.http.enumeration.HttpStatusCode;
import org.stevi.server.http.model.HttpRequest;
import org.stevi.server.http.model.HttpResponse;
import org.stevi.server.http.servlet.DefaultHttpServletImpl;
import org.stevi.server.http.servlet.HttpServlet;
import org.stevi.server.server.Handler;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class HttpHandler implements Handler {

    private final HttpRequestReader requestReader = new HttpRequestReader();
    private final HttpResponseWriter responseWriter = new HttpResponseWriter();
    private final List<HttpServlet> servletImpls = new ArrayList<>();

    public HttpHandler() {
        servletImpls.add(new DefaultHttpServletImpl());
    }

    @SneakyThrows
    public void handle(InputStream inputStream, OutputStream outputStream) {
        Optional<HttpRequest> optRequest = requestReader.decodeRequest(inputStream);

        if (optRequest.isEmpty()) {
            //writeNotFoundResponse(outputStream);
            return;
        }

        HttpRequest request = optRequest.get();

        log.info("Incoming http {} request {}", request.getHttpMethod(), request.getUri());

        HttpResponse response = HttpResponse.builder()
                .statusCode(HttpStatusCode.OK)
                .responseHeaders(Map.of("Content-Type", List.of("application/json")))
                .build();

        servletImpls.forEach(httpServlet -> {
            switch (request.getHttpMethod()) {
                case GET -> httpServlet.doGet(request, response);
                case POST -> httpServlet.doPost(request, response);
            }
        });

        responseWriter.writeResponse(outputStream, response);
    }

    private void getRequest(InputStream inputStream) {

    }

    private void writeNotFoundResponse(OutputStream outputStream) {
        HttpResponse response = HttpResponse.builder()
                .statusCode(HttpStatusCode.NOT_FOUND)
                .responseHeaders(Map.of("Content-Type", List.of("application/json")))
                .build();

        responseWriter.writeResponse(outputStream, response);
    }
}
