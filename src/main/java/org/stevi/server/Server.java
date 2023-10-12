package org.stevi.server;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {

    private final Executor executor;
    private final ServerSocket serverSocket;
    private final HttpHandler httpHandler;

    private volatile boolean isStarted = true;

    @SneakyThrows
    public Server(int port) {
        this.serverSocket = new ServerSocket(port);
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.httpHandler = new HttpHandler();
    }

    @SneakyThrows
    public void start() {
        while (isStarted) {
            Socket socket = this.serverSocket.accept();
            handleSocketConnection(socket);
        }
    }

    public void stop() {
        isStarted = false;
    }

    private void handleSocketConnection(Socket socket) {
        Runnable runnable = () -> {
            try {
                httpHandler.handle(socket.getInputStream(), socket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        executor.execute(runnable);
    }
}
