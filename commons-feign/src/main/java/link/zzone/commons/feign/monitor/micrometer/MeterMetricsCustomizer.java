package link.zzone.commons.feign.monitor.micrometer;

import feign.Request;
import feign.Response;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * @author chrischen
 */
@FunctionalInterface
public interface MeterMetricsCustomizer {

    /**
     * meter metrics apply
     * @param meterRegistry meterRegistry, like PrometheusMeterRegistry
     * @param request feign request
     * @param options feign request options
     * @param response feign response
     * @param timerSample a timing sample
     */
    void apply(MeterRegistry meterRegistry, Request request, Request.Options options,
               Response response, Timer.Sample timerSample);


}
