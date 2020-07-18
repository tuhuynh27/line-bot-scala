# LINE Bot Webhook Server

## Introduce

Simple LINE Bot

Written with [com.tuhuynh.httpserver](https://github.com/huynhminhtufu/httpserver)

## Run

### Environment variable

- PORT: 1234
- TOKEN: LINE_TOKEN

## Build

```bash
./gradle build
PORT=1234 TOKEN=YOUR_LINE_TOKEN java -jar ./build/libs/linebot-1.0-SNAPSHOT-fat.jar
```

Cost:

- Jar: 308KB
- Init: 20MB MEM