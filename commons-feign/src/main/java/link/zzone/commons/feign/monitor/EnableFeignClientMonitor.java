package link.zzone.commons.feign.monitor;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author chrischen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(FeignClientMonitorConfiguration.class)
public @interface EnableFeignClientMonitor {
}
