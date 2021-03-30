# First, use graalvm
# jabba use graalvm@20.2.0

# Server
./gradlew build
native-image --no-server --no-fallback -H:ReflectionConfigurationFiles=native-config.json --allow-incomplete-classpath -jar ./build/libs/linebot-1.0-SNAPSHOT-fat.jar line-bot-hr

# Start by ./line-bot-hr
