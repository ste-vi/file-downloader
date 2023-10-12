package org.stevi.server;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Optional;

public class HttpHandler {

    public void handle(InputStream inputStream, OutputStream outputStream) {
        var bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

        Optional<HttpRequest> httpRequest = HttpDecoder.decode(inputStream);

        HttpRequest request = httpRequest.get();
        request.getUri();


    }
}
