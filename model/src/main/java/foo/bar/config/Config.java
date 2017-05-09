package foo.bar.config;

public class Config {
    public static String getTargetHost() {
        String targetHost = System.getenv("TARGET_HOST");
        if (targetHost == null || "".equals(targetHost))
            targetHost = "localhost";
        return targetHost;
    }
}
