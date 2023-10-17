package org.stevi.server.http;

import com.google.gson.Gson;
import org.stevi.server.http.enumeration.HttpVersion;
import org.stevi.server.http.model.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpResponseWriter {

    private final Gson gson = new Gson();

    public void writeResponse(OutputStream outputStream, HttpResponse response) {
        try {

            String httpResponseInfo = "%s %s %s".formatted(
                    HttpVersion.HTTP_1_1.getValue(),
                    response.getStatusCode().getCode(),
                    response.getStatusCode().getMessage());

            outputStream.write(httpResponseInfo.getBytes());

            List<String> responseHeaders = buildHeaderStrings(response.getResponseHeaders());
            if (!responseHeaders.isEmpty()) {
                outputStream.write("\r\n".getBytes());
                for (var header : responseHeaders) {
                    outputStream.write(header.getBytes());
                }
            }

            if (response.getEntity() != null) {
                outputStream.write("\r\n".getBytes());
                String json = gson.toJson(response.getEntity());
                outputStream.write(json.getBytes());
            } else if (response.getResponseBodyStream() != null) {
                outputStream.write("\r\n".getBytes());
                response.getResponseBodyStream().transferTo(outputStream);
            } else {
                outputStream.write("\r\n".getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> buildHeaderStrings(Map<String, List<String>> responseHeaders) {
        List<String> responseHeadersList = new ArrayList<>();

        responseHeaders.forEach((name, values) -> {
            final StringBuilder valuesCombined = new StringBuilder();
            values.forEach(valuesCombined::append);
            valuesCombined.append(";");

            responseHeadersList.add(name + ": " + valuesCombined + "\r\n");
        });

        return responseHeadersList;
    }
}
