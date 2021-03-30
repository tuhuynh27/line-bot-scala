package com.tuhuynh.linebot;

import java.io.IOException;
import java.util.TreeMap;

import com.jinyframework.HttpServer;
import com.jinyframework.core.AbstractRequestBinder.HttpResponse;
import com.tuhuynh.linebot.handlers.WebhookHandler;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public final class Main {
    public static void main(String[] args) throws IOException {
        val env = System.getenv();
        val token = env.get("TOKEN");
        if (token == null) {
            log.error("Missing token env");
            System.exit(1);
        }
        val port = env.get("PORT") == null ? "1234" : env.get("PORT");

        val server = HttpServer.port(Integer.parseInt(port));
        val responseHeaders = new TreeMap<String, String>();
        responseHeaders.put("content-type", "application/json; charset=utf-8");
        server.useResponseHeaders(responseHeaders);

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        log.info("Added shutdown hook");

        val webhookHandler = new WebhookHandler(token);
        server.get("/", ctx -> HttpResponse.of("Hello World!"));
        server.post("/webhook", webhookHandler::handleWebhook);
        server.get("/dict", webhookHandler::showDict);
        server.post("/dict", ctx -> {
            val authorization = ctx.headerParam("authorization");
            val bearerToken = "Bearer " + token;
            if (bearerToken.equals(authorization)) {
                return HttpResponse.next();
            }
            return HttpResponse.reject("Unauthorized").status(401);
        }, webhookHandler::setDict);
        server.start();
    }
}
