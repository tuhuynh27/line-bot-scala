# LINE Bot Webhook Server

## Introduce

Simple LINE Bot, for LINE VN HR

Written with [com.jinyframework](https://github.com/huynhminhtufu/jiny) and [keva-DB](https://github.com/tuhuynh27/jiny/tree/master/keva)

## Run

### Environment variable

- PORT: 1234
- TOKEN: LINE_TOKEN

## Build

```bash
./gradlew build
PORT=1234 TOKEN=YOUR_LINE_TOKEN java -jar ./build/libs/linebot-1.0-SNAPSHOT-fat.jar
```
