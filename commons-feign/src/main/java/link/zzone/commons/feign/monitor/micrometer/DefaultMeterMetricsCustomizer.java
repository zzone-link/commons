package link.zzone.commons.feign.monitor.micrometer;

import feign.Request;
import feign.Response;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.util.Optional;


/**
 * @author chrischen
 */
@Getter
public class DefaultMeterMetricsCustomizer implements MeterMetricsCustomizer {

    private final Environment environment;

    public DefaultMeterMetricsCustomizer(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void apply(MeterRegistry meterRegistry, Request request, Request.Options options,
                      Response response, Timer.Sample timerSample) {
        //Counter
        meterRegistry.counter("feign_http_requests_total", Tags.of(
                "app", environment.getProperty("spring.application.name", "default"),
                "method", request.httpMethod().toString(),
                "path", URI.create(request.url()).getPath(),
                "status", Optional.ofNullable(response).map(Response::status).orElse(-1).toString()
        )).increment();
        //Timer
        Timer timer = meterRegistry.timer("feign_http_request_duration", Tags.of(
                "app", environment.getProperty("spring.application.name", "default"),
                "method", request.httpMethod().toString(),
                "path", URI.create(request.url()).getPath()));
        timerSample.stop(timer);
    }
}
