package link.zzone.commons.feign.monitor.micrometer;

import feign.Client;
import feign.Request;
import feign.Response;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;

/**
 * @author chrischen
 */
@Slf4j
public class MeteredClient implements Client {

    private final Client delegate;
    private final MeterRegistry meterRegistry;
    private final MeterMetricsCustomizer meterMetricsCustomizer;

    public MeteredClient(Client delegate, MeterRegistry meterRegistry, MeterMetricsCustomizer meterMetricsCustomizer) {
        this.delegate = delegate;
        this.meterRegistry = meterRegistry;
        this.meterMetricsCustomizer = meterMetricsCustomizer;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        Response response = null;
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            response = delegate.execute(request, options);
        } finally {
            try {
                meterMetricsCustomizer.apply(meterRegistry, request, options, response, sample);
            } catch (Exception e) {
                log.error("error in applying meter metrics operation", e);
            }
        }
        return response;
    }
}
