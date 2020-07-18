package com.tuhuynh.linebot.handlers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.activation.UnknownObjectException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuhuynh.httpserver.HttpClient;
import com.tuhuynh.httpserver.core.RequestBinder.HttpResponse;
import com.tuhuynh.httpserver.core.RequestBinder.RequestContext;
import com.tuhuynh.linebot.entities.Event;
import com.tuhuynh.linebot.entities.ReplyObject;
import com.tuhuynh.linebot.entities.UserProfile;
import com.tuhuynh.linebot.entities.WebhookEventObject;

import lombok.val;

public class WebhookHandler {
    private final String token;
    private final Gson gson = new Gson();
    private final LinkedList<String> dictQueue = new LinkedList<>();
    private HashMap<String, String> teachDict;
    private Event event;

    public WebhookHandler(final String token) throws IOException {
        this.token = token;
        val data = read();
        if (data == null || data.isEmpty()) {
            teachDict = new HashMap<>();
        } else {
            teachDict = data;
        }
    }

    public void getWebHookEventObject(final String body) throws UnknownObjectException {
        val webhookEventObject = gson.fromJson(body, WebhookEventObject.class);
        if (webhookEventObject.getEvents() != null && webhookEventObject.getEvents()[0] != null) {
            event = webhookEventObject.getEvents()[0];
        } else {
            throw new UnknownObjectException("Webhook Event Object not valid!");
        }
    }

    public UserProfile getProfile() throws IOException, UnknownObjectException {
        val headers = new HashMap<String, String>();
        headers.put("Authorization",
                    "Bearer " + token);
        val response = HttpClient.builder()
                                 .method("GET")
                                 .url("https://api.line.me/v2/bot/profile/" + event.getSource().getUserId())
                                 .headers(headers)
                                 .build().perform();
        if (response.getStatus() == 200) {
            return gson.fromJson(response.getBody(), UserProfile.class);
        }

        throw new UnknownObjectException("Cannot get user profile");
    }

    public void reply(final String text) throws IOException {
        System.out.println(text);
        val headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization",
                    "Bearer " + token);
        val message = ReplyObject.Messages.builder()
                                          .type("text")
                                          .text(text).build();
        val replyObject = ReplyObject.builder().replyToken(event.getReplyToken()).messages(
                new ReplyObject.Messages[] { message }).build();
        val replyObjectJson = gson.toJson(replyObject);

        HttpClient.builder()
                  .method("POST").url("https://api.line.me/v2/bot/message/reply")
                  .headers(headers)
                  .body(replyObjectJson)
                  .build().perform();
    }

    public void sync() throws FileNotFoundException {
        val out = new PrintWriter("data.json");
        out.print(gson.toJson(teachDict));
        out.flush();
        out.close();
        System.out.println("Synced");
    }

    public HashMap<String, String> read() throws IOException {
        val bufferedReader = new BufferedReader(new FileReader("data.json"));
        val stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        val str = stringBuilder.toString();
        val type = new TypeToken<HashMap<String, String>>() {}.getType();
        return gson.fromJson(str, type);
    }

    public HttpResponse handleWebhook(final RequestContext ctx) throws UnknownObjectException, IOException {
        getWebHookEventObject(ctx.getBody());

        val textOrig = event.getMessage().getText().trim();
        val text = event.getMessage().getText().trim().toLowerCase();

        if (dictQueue.isEmpty() && ("dạy bot".equals(text) || "bot dạy".equals(text) || "dạy".equals(text))) {
            val profile = getProfile();
            final List<String> admins = Arrays.asList("Tu Huynh (Tyler)", "Nga Le (Jade)", "Ninh",
                                                      "Phuong Quach");
            if (profile != null && admins.stream().anyMatch(s -> s.equals(profile.getDisplayName()))) {
                dictQueue.addLast("dict");
                reply("Bạn muốn dạy cho từ gì?");
            }
        } else if (dictQueue.size() == 1) {
            dictQueue.addLast(text);
            reply("Bạn muốn nó trả lời sao?");
        } else if (dictQueue.size() == 2) {
            dictQueue.removeFirst();
            val key = dictQueue.removeFirst();
            teachDict.put(key, textOrig);
            reply("Đã dạy xong!");
            sync();
        } else {
            val match = teachDict.get(text);
            if (match != null && !match.isEmpty()) {
                reply(match);
            }
        }

        return HttpResponse.of("OK");
    }

    public HttpResponse showDict(final RequestContext context) {
        return HttpResponse.of(gson.toJson(teachDict));
    }

    public HttpResponse setDict(final RequestContext context) throws FileNotFoundException {
        val body = context.getBody();
        val type = new TypeToken<HashMap<String, String>>() {}.getType();
        teachDict = gson.fromJson(body, type);
        sync();
        return HttpResponse.of("Done");
    }
}
