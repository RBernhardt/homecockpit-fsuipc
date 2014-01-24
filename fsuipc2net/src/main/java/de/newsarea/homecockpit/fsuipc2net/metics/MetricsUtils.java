package de.newsarea.homecockpit.fsuipc2net.metics;

import com.codahale.metrics.MetricRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsUtils {

    private static Logger log = LoggerFactory.getLogger(MetricsUtils.class);

    private static MetricRegistry metricRegistry;

    public static MetricRegistry getInstance() {
        if(metricRegistry == null) {
            metricRegistry = new MetricRegistry();
        }
        return metricRegistry;
    }

}
