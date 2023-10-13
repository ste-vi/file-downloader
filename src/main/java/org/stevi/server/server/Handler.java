package org.stevi.server.server;

import java.io.InputStream;
import java.io.OutputStream;

@FunctionalInterface
public interface Handler {

    void handle(InputStream inputStream, OutputStream outputStream);
}
