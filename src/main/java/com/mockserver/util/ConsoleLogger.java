package com.mockserver.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

public final class ConsoleLogger {

    private ConsoleLogger() {
    }

    public static void log(HttpServletRequest request,
                           String body,
                           long elapsed) {

        System.out.println();
        System.out.println("============================================================");
        System.out.printf("%s %s%n",
                request.getMethod(),
                request.getRequestURI());

        System.out.println();
        System.out.println("Headers");
        System.out.println("------------------------------------------------------------");

        Collections.list(request.getHeaderNames())
                .forEach(name ->
                        System.out.printf("%s : %s%n",
                                name,
                                request.getHeader(name)));

        System.out.println();

        System.out.println("Query Parameters");
        System.out.println("------------------------------------------------------------");

        request.getParameterMap()
                .forEach((k, v) ->
                        System.out.printf("%s = %s%n",
                                k,
                                String.join(",", v)));

        System.out.println();

        System.out.println("Body");
        System.out.println("------------------------------------------------------------");

        System.out.println(JsonUtils.pretty(body));

        System.out.println();

        System.out.printf("Elapsed : %d ms%n", elapsed);

        System.out.println("============================================================");
        System.out.println();
    }

}