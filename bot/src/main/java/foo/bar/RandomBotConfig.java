package foo.bar;

public enum RandomBotConfig {
    QUICK_TEST(
            1_000,
            10,
            100,
            10
    ),

    WEBSOCKET_STRESS(
            1_000,
            10,
            100,
            10_000
    ),

    UI_STRESS(
            1_000_000,
            3,
            100,
            10
    ),

    IMAGE(
            10_000,
            5,
            1000,
            1
    ),

    INFINITE(
            Integer.MAX_VALUE,
            1,
            10,
            10_000
    ),

    INFINITE_LOW_POWER(
            Integer.MAX_VALUE,
            1,
            100,
            5
    );

    private final int maxRequests;
    private final int requesterThreads;
    private final int maxRequestsPerSecondPerRequesterThread;
    private final int clientThreads;


    RandomBotConfig(int maxRequests, int requesterThreads, int maxRequestsPerSecondPerRequesterThread, int clientThreads) {
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
