package foo.bar.monitoring;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class Monitoring {
    private static final Logger LOGGER = LoggerFactory.getLogger(Monitoring.class);

    public static final MetricRegistry registry = new MetricRegistry();
    static {
        final JmxReporter reporter = JmxReporter.forRegistry(registry).build();
        reporter.start();
        final Slf4jReporter slf4jReporter = Slf4jReporter.forRegistry(registry)
                .outputTo(LoggerFactory.getLogger(Monitoring.class))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        slf4jReporter.start(1, TimeUnit.MINUTES);
        initGraphiteReporting();

    }

    private static void initGraphiteReporting() {
        try {
            final Graphite graphite = new Graphite(new InetSocketAddress("localhost", 2003));
            final GraphiteReporter graphiteReporter = GraphiteReporter.forRegistry(registry)
                    .prefixedWith("place")
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .filter(MetricFilter.ALL)
                    .build(graphite);
            graphiteReporter.start(5, TimeUnit.SECONDS);
        } catch(Exception e) {
            LOGGER.warn("Could not initalize graphite reporting", e);
        }
    }
}
