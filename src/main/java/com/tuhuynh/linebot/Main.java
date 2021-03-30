package com.tuhuynh.linebot;

import com.jinyframework.HttpServer;
import com.jinyframework.core.AbstractRequestBinder.HttpResponse;
import com.tuhuynh.linebot.handlers.WebhookHandler;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.util.Map;

@Slf4j
public final class Main {
    public static void main(String[] args) throws IOException {
        final Map<String, String> env = System.getenv();
        final String token = env.get("TOKEN");
        if (token == null) {
            log.error("Missing token env");
            System.exit(1);
        }
        final String port = env.get("PORT") == null ? "1234" : env.get("PORT");

        val server = HttpServer.port(Integer.parseInt(port));

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        log.info("Added shutdown hook");

        val webhookHandler = new WebhookHandler(token);
        server.get("/", ctx -> HttpResponse.of("Hello World!"));
        server.post("/webhook", webhookHandler::handleWebhook);
        server.get("/dict", webhookHandler::showDict);
        server.post("/dict", ctx -> {
            val authorization = ctx.headerParam("authorization");
            if ("Bearer tu.huynh@linecorp.com".equals(authorization)) {
                return HttpResponse.next();
            }
            return HttpResponse.reject("Unauthorized").status(401);
        }, webhookHandler::setDict);
        server.start();
    }
}
