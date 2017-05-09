package foo.bar;

public class RandomBotConfig {

    public static RandomBotConfig WEBSOCKET_STRESS = new RandomBotConfig(
            1_000,
            10,
            100,
            10_000
    );

    public static RandomBotConfig UI_STRESS = new RandomBotConfig(
            1_000_000,
            10,
            1000,
            10
    );

    private final int maxRequests;
    private final int requesterThreads;
    private final int maxRequestsPerSecondPerRequesterThread;
    private final int clientThreads;


    public RandomBotConfig(int maxRequests, int requesterThreads, int maxRequestsPerSecondPerRequesterThread, int clientThreads) {
        this.maxRequests = maxRequests;
        this.requesterThreads = requesterThreads;
        this.maxRequestsPerSecondPerRequesterThread = maxRequestsPerSecondPerRequesterThread;
        this.clientThreads = clientThreads;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public int getRequesterThreads() {
        return requesterThreads;
    }

    public int getMaxRequestsPerSecondPerRequesterThread() {
        return maxRequestsPerSecondPerRequesterThread;
    }

    public int getClientThreads() {
        return clientThreads;
    }
}
