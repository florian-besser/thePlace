package foo.bar.config;

public class Config {
    public static String getRedisTargetHost() {
        String targetHost = System.getenv("TARGET_HOST_REDIS");
        if (targetHost == null || "".equals(targetHost))
            targetHost = "localhost";
        return targetHost;
    }

    public static String getRabbitMqTargetHost() {
        String targetHost = System.getenv("TARGET_HOST_RABBITMQ");
        if (targetHost == null || "".equals(targetHost))
            targetHost = "localhost";
        return targetHost;
    }

    public static String getBackendTargetHost() {
        String targetHost = System.getenv("TARGET_HOST_BACKEND");
        if (targetHost == null || "".equals(targetHost))
            targetHost = "localhost";
        return targetHost;
    }

    public static String getBotConfig() {
        String config = System.getenv("BOT_CONFIG");
        if (config == null || "".equals(config))
            config = "UI_STRESS";
        return config;
    }

    public static String getBotPixelPutterConfig() {
        String config = System.getenv("BOT_PIXEL_PUTTER_CONFIG");
        if (config == null || "".equals(config))
            config = "RANDOM";
        return config;
    }
}
