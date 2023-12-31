package org.stevi.server.http.servlet;

import lombok.SneakyThrows;
import org.stevi.server.http.model.HttpRequest;
import org.stevi.server.http.model.HttpResponse;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultHttpServletImpl implements HttpServlet {

    private final AtomicInteger visitCounter = new AtomicInteger(0);
    private final ConcurrentMap<String, Integer> visitorMap = new ConcurrentHashMap<>();

    @SneakyThrows
    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        System.out.println("Calling do get");

        if (request.getUri().getPath().equals("/file")) {
            String[] params = request.getUri().getQuery().split("=");
            String fileNameParam = params[0];
            String fileNameValue = params[1];

            var fileChannel = FileChannel.open(Path.of("files/" + fileNameValue + ".zip"), StandardOpenOption.READ);
            var inputStream = Channels.newInputStream(fileChannel);

            response.setResponseBodyStream(inputStream);
            response.getResponseHeaders().put("Content-Type", List.of("application/zip"));
            response.getResponseHeaders().put("Content-Length", List.of(String.valueOf(inputStream.available())));
        } else if (request.getUri().getPath().equals("/count")) {
            Optional<String> sessionIdCookie = request.getCookie("sessionId");
            if (sessionIdCookie.isPresent()) {
                if (visitorMap.get(sessionIdCookie.get()) != null) {
                    visitorMap.replace(sessionIdCookie.get(), visitorMap.get(sessionIdCookie.get()) + 1);
                } else {
                    visitCounter.incrementAndGet();
                    visitorMap.putIfAbsent(sessionIdCookie.get(), 1);
                }
            } else {
                visitCounter.incrementAndGet();
            }
        } else if (request.getUri().getPath().equals("/home")) {
            response.setEntity("This endpoint was visited %s times".formatted(visitCounter.get()));
        } else {
            response.setEntity("Hello world");
        }
    }

    @SneakyThrows
    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        System.out.println("Calling do post");

        String contentLenghtString = request.getRequestHeader("Content-Length").orElse("");
        int contentLength = Integer.parseInt(contentLenghtString);

        InputStream inputStream = request.getBodyInputStream();

        String contentType = request.getRequestHeaderOrThrow("Content-Type");

        switch (contentType) {
            case "application/json": {
                byte[] buffer = new byte[1024];
                StringBuilder requestBody = new StringBuilder();
                int bytesRead;

                if (contentLength > 0) {
                    while (contentLength > 0 || inputStream.available() > 0) {
                        bytesRead = inputStream.read(buffer);
                        String chunk = new String(buffer, 0, bytesRead);
                        requestBody.append(chunk);
                        contentLength -= bytesRead;
                    }
                }

                System.out.println("Request Body:");
                System.out.println(requestBody);
            }
            case "image/jpeg": {
                String fileName = request
                        .getRequestHeader("File-Name")
                        .map(name -> name + ".jpg")
                        .orElseGet(() -> UUID.randomUUID() + ".jpg");

                try (var fileOutputStream = new FileOutputStream("files/" + fileName)) {
                    byte[] buffer = new byte[1024];

                    int bytesRead;

                    while (contentLength > 0 || inputStream.available() > 0) {
                        bytesRead = inputStream.read(buffer);
                        fileOutputStream.write(buffer, 0, bytesRead);
                        contentLength -= bytesRead;
                    }
                }

            }
        }

    }
}
