package com.tuhuynh.linebot.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jinyframework.HttpClient;
import com.jinyframework.core.AbstractRequestBinder.Context;
import com.jinyframework.core.AbstractRequestBinder.HttpResponse;
import com.tuhuynh.linebot.entities.Event;
import com.tuhuynh.linebot.entities.ReplyObject;
import com.tuhuynh.linebot.entities.UserProfile;
import com.tuhuynh.linebot.entities.WebhookEventObject;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public final class WebhookHandler {
    private final String token;
    private final Gson gson = new Gson();
    private final LinkedList<String> dictQueue = new LinkedList<>();
    private HashMap<String, String> teachDict;
    private Event event;
    private boolean trashtalkMode;

    public WebhookHandler(final String token) throws IOException {
        this.token = token;
        val data = read();
        if (data == null || data.isEmpty()) {
            teachDict = new HashMap<>();
        } else {
            teachDict = data;
        }
    }

    public void getWebHookEventObject(final String body) throws Exception {
        val webhookEventObject = gson.fromJson(body, WebhookEventObject.class);
        if (webhookEventObject.getEvents() != null && webhookEventObject.getEvents()[0] != null) {
            event = webhookEventObject.getEvents()[0];
        } else {
            throw new Exception("Webhook Event Object not valid!");
        }
    }

    public UserProfile getProfile() throws Exception {
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

        throw new Exception("Cannot get user profile");
    }

    public void reply(final String text) throws IOException {
        log.info(text);
        val headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization",
                "Bearer " + token);
        val message = ReplyObject.Messages.builder()
                .type("text")
                .text(text).build();
        val replyObject = ReplyObject.builder().replyToken(event.getReplyToken()).messages(
                new ReplyObject.Messages[]{message}).build();
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
        log.info("Synced");
    }

    public HashMap<String, String> read() throws IOException {
        val bufferedReader = new BufferedReader(new FileReader("data.json"));
        val stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        val str = stringBuilder.toString();
        val type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        return gson.fromJson(str, type);
    }

    public String getTrashTalk(final String msg) throws IOException {
        val result = HttpClient.builder()
                .method("GET").url("https://simsumi.herokuapp.com/api?text=" + msg.replace(" ", "+") + "&lang=vi")
                .build().perform();
        val body = result.getBody();
        val simsimiObj = gson.fromJson(body, SimsimiResponse.class);
        return simsimiObj.getSuccess();
    }

    public HttpResponse handleWebhook(final Context ctx) throws Exception {
        getWebHookEventObject(ctx.getBody());

        val textOrig = event.getMessage().getText().trim();
        val text = event.getMessage().getText().trim().toLowerCase();

        if ("bot".equals(text)) {
            reply("Hihi");
            return HttpResponse.of("OK");
        }

        if (trashtalkMode) {
            if ("stop trash".equals(text)) {
                trashtalkMode = false;
                reply("Đã dừng trash talk ạ!");
                return HttpResponse.of("OK");
            }

            val trashTalkText = getTrashTalk(text);
            reply(trashTalkText);
            return HttpResponse.of("OK");
        }

        if ("trash".equals(text)) {
            trashtalkMode = true;
            reply("Đã vào trash talk mode!");
            return HttpResponse.of("OK");
        }

        if (dictQueue.isEmpty() && ("dạy bot".equals(text) || "bot dạy".equals(text) || "dạy".equals(text))) {
            val profile = getProfile();
            System.out.println(profile.getDisplayName());
            final List<String> admins = Arrays.asList("Tu Huynh (Tyler)", "Nga Le (Jade)", "Ninh",
                    "Phuong Quach", "Quynh");
            if (admins.stream().anyMatch(s -> s.equals(profile.getDisplayName()))) {
                dictQueue.addLast(profile.getDisplayName());
                reply("Bạn muốn dạy cho từ gì?");
            }
        } else if (dictQueue.size() == 1) {
            val profile = getProfile();
            if (profile != null && profile.getDisplayName().equals(dictQueue.getFirst())) {
                dictQueue.addLast(text);
                reply("Bạn muốn nó trả lời sao?");
            } else {
                dictQueue.clear();
            }
        } else if (dictQueue.size() == 2) {
            val profile = getProfile();
            if (profile != null && profile.getDisplayName().equals(dictQueue.getFirst())) {
                dictQueue.removeFirst();
                val key = dictQueue.removeFirst();
                teachDict.put(key, textOrig);
                reply("Đã dạy xong!");
                sync();
            } else {
                dictQueue.clear();
            }
        } else {
            val match = teachDict.get(text);
            if (match != null && !match.isEmpty()) {
                reply(match);
            }
        }

        return HttpResponse.of("OK");
    }

    public HttpResponse showDict(final Context context) {
        return HttpResponse.of(gson.toJson(teachDict));
    }

    public HttpResponse setDict(final Context context)
            throws FileNotFoundException {
        val body = context.getBody();
        val type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        teachDict = gson.fromJson(body, type);
        sync();
        return HttpResponse.of("Done");
    }

    @Builder
    @Getter
    @Setter
    private static class SimsimiResponse {
        private String success;
    }
}
