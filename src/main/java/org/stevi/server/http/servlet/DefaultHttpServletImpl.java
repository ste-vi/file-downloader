package org.stevi.server.http.servlet;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.stevi.server.http.model.HttpRequest;
import org.stevi.server.http.model.HttpResponse;

import java.io.InputStream;

public class DefaultHttpServletImpl implements HttpServlet {

    private final Gson gson = new Gson();

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        System.out.println("Calling do get");
        response.setEntity("Hello world");
    }


    @SneakyThrows
    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        System.out.println("Calling do post");


        int bytesRead;
        InputStream inputStream = request.getBodyInputStream();

        byte[] buffer = new byte[1024];


        StringBuilder requestBody = new StringBuilder();

        String contentLenghtString = request.getRequestHeaders()
                .get("Content-Length")
                .getFirst()
                .trim();

        int contentLength = Integer.parseInt(contentLenghtString);

            if (contentLength > 0) {
                while (inputStream.available() > 0) {
                    bytesRead = inputStream.read(buffer);
                    String chunk = new String(buffer, 0, bytesRead);
                    requestBody.append(chunk);
                    contentLength -= bytesRead;
                }
        }


        System.out.println(requestBody);


    }
}
