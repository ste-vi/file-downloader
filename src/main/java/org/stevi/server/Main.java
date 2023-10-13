package org.stevi.server;

import org.stevi.server.http.HttpHandler;
import org.stevi.server.server.Server;

public class Main {

    public static void main(String[] args) {
        Server server = new Server(8080, new HttpHandler());
        server.start();
    }
}