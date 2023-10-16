package org.stevi.server.http.servlet;

import org.stevi.server.http.model.HttpRequest;
import org.stevi.server.http.model.HttpResponse;

public interface HttpServlet extends Servlet {

    void doGet(HttpRequest request, HttpResponse response);

    void doPost(HttpRequest request, HttpResponse response);
}
