package org.stevi.server.server;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.stevi.server.http.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
public class Server {

    private final Executor executor;
    private final ServerSocket serverSocket;
    private final Handler handler;
    private final int port;

    private volatile boolean isStarted = true;

    @SneakyThrows
    public Server(Integer port, Handler handler) {
        this.port = port;
        this.serverSocket = new ServerSocket(this.port);
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.handler = handler;
    }

    @SneakyThrows
    public Server() {
        this.port = 8080;
        this.serverSocket = new ServerSocket(this.port);
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.handler = new HttpHandler();
    }

    @SneakyThrows
    public void start() {
        log.info("Starting server on port {}", serverSocket.getLocalPort());
        while (isStarted) {
            Socket socket = this.serverSocket.accept();
            handleSocketConnection(socket);
        }
    }

    private void handleSocketConnection(Socket socket) {
        Runnable runnable = () -> {
            try (socket) {
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                handler.handle(inputStream, outputStream);

                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        executor.execute(runnable);
    }

    public void stop() {
        isStarted = false;
    }
}
