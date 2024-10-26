package link.zzone.commons.feign.monitor;

import link.zzone.commons.feign.monitor.micrometer.DefaultMeterMetricsCustomizer;
import link.zzone.commons.feign.monitor.micrometer.MeterMetricsCustomizer;
import link.zzone.commons.feign.monitor.micrometer.MeteredClient;
import feign.Client;
import feign.httpclient.ApacheHttpClient;
import feign.okhttp.OkHttpClient;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * @author chrischen
 * Currently only support ribbon
 */
@Configuration
@ConditionalOnClass(name = {"feign.Client", "org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient"})
@ConditionalOnProperty(prefix = "feign.client.monitor", name = "enabled", havingValue = "true")
public class FeignClientMonitorConfiguration {

    @Configuration
    @ConditionalOnClass(ApacheHttpClient.class)
    @ConditionalOnProperty(value = "feign.httpclient.enabled", matchIfMissing = true)
    static class HttpClientFeignClientMonitorConfiguration {

        @Bean
        @ConditionalOnMissingBean(Client.class)
        public Client feignClient(CachingSpringLoadBalancerFactory cachingFactory,
                                  SpringClientFactory clientFactory, HttpClient httpClient,
                                  ListableBeanFactory beanFactory, Environment environment) {
            ApacheHttpClient delegate = new ApacheHttpClient(httpClient);
            return assembleFeignClient(delegate, cachingFactory, clientFactory, beanFactory, environment);
        }
    }

    @Configuration
    @ConditionalOnClass(OkHttpClient.class)
    @ConditionalOnProperty(value = "feign.okhttp.enabled")
    static class OkHttpFeignClientMonitorConfiguration {

        @Bean
        @ConditionalOnMissingBean(Client.class)
        public Client feignClient(CachingSpringLoadBalancerFactory cachingFactory,
                                  SpringClientFactory clientFactory, okhttp3.OkHttpClient okHttpClient,
                                  ListableBeanFactory beanFactory, Environment environment) {
            OkHttpClient delegate = new OkHttpClient(okHttpClient);
            return assembleFeignClient(delegate, cachingFactory, clientFactory, beanFactory, environment);
        }
    }

    @Configuration
    @ConditionalOnMissingClass(value = {"feign.httpclient.ApacheHttpClient", "feign.okhttp.OkHttpClient"})
    static class DefaultFeignClientMonitorConfiguration {

        @Bean
        @ConditionalOnMissingBean(Client.class)
        public Client feignClient(CachingSpringLoadBalancerFactory cachingFactory, SpringClientFactory clientFactory,
                                  ListableBeanFactory beanFactory, Environment environment) {
            Client client = new Client.Default(null, null);
            return assembleFeignClient(client, cachingFactory, clientFactory, beanFactory, environment);
        }
    }

    private static Client assembleFeignClient(Client client, CachingSpringLoadBalancerFactory cachingFactory, SpringClientFactory clientFactory,
                                              ListableBeanFactory beanFactory, Environment environment) {
        Map<String, MeterRegistry> meterRegistries = beanFactory.getBeansOfType(MeterRegistry.class);
        Map<String, MeterMetricsCustomizer> meterMetricsCustomizers = beanFactory.getBeansOfType(MeterMetricsCustomizer.class);
        if (meterMetricsCustomizers.size() > 1) {
            throw new NoUniqueBeanDefinitionException(MeterMetricsCustomizer.class, meterMetricsCustomizers.keySet());
        }
        for (MeterRegistry meterRegistry : meterRegistries.values()) {
            if (meterMetricsCustomizers.isEmpty()) {
                client = new MeteredClient(client, meterRegistry, new DefaultMeterMetricsCustomizer(environment));
            } else {
                client = new MeteredClient(client, meterRegistry, meterMetricsCustomizers.values().iterator().next());
            }
        }
        return new LoadBalancerFeignClient(client, cachingFactory, clientFactory);
    }

}
