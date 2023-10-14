package org.stevi.server.http;

import org.stevi.server.http.model.HttpResponse;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpResponseWriter {

    public static void writeResponse(OutputStream outputStream, HttpResponse response) {
        try {
            var objectOutputStream = new ObjectOutputStream(outputStream);

            String httpResponseInfo = "HTTP/1.1 " + response.getStatusCode() + " " + "Ok" + "\r\n";
            objectOutputStream.write(httpResponseInfo.getBytes());

            List<String> responseHeaders = buildHeaderStrings(response.getResponseHeaders());
            for (String header : responseHeaders) {
                objectOutputStream.writeChars(header);
            }

            if (response.getEntity() != null) {
                objectOutputStream.writeChars("\r\n");
                objectOutputStream.writeObject(response);
            } else {
                objectOutputStream.writeChars("\r\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> buildHeaderStrings(Map<String, List<String>> responseHeaders) {
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
