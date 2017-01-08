package se.omegapoint.micro;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;
import com.netflix.appinfo.AmazonInfo;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableEurekaClient
public class GalaxyApplication {

    @Configuration
    public static class Conf {

        @Value("${server.port}")
        public int serverPort;

        @Bean
        public RestTemplate restTemplate(LoadBalancerClient loadBalancerClient) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(new LoadBalancerInterceptor(loadBalancerClient));
            return restTemplate;
        }

        @Bean
        public List<RestTemplate> templates(RestTemplate restTemplate) {
            return Arrays.asList(restTemplate);

        }

        /**
         * Uses the public ip of the host machine when registering to Eureka.
         * This is required when running application with docker
         */
        @Bean
        @Profile("prod")
        public EurekaInstanceConfigBean eurekaInstanceConfig() {

            EurekaInstanceConfigBean config = new EurekaInstanceConfigBean(new InetUtils(new InetUtilsProperties()));
            AmazonInfo info = AmazonInfo.Builder.newBuilder().autoBuild("eureka");
            config.setDataCenterInfo(info);
            info.getMetadata().put(AmazonInfo.MetaDataKey.publicHostname.getName(),
                    info.get(AmazonInfo.MetaDataKey.publicHostname));
            config.setHostname(info.get(AmazonInfo.MetaDataKey.publicHostname));
            config.setIpAddress(info.get(AmazonInfo.MetaDataKey.publicHostname));
            config.setNonSecurePort(serverPort);
            return config;
        }

        @Value("${spring.application.name}")
        public String applicationName;

        @Bean
        public Filter requestLoggingFilter() {
            return new MDCInsertingServletFilter();
        }

        @Bean
        public Filter customLoggingFilter() {
            return new Filter() {
                @Override
                public void init(FilterConfig filterConfig) throws
                                                            ServletException {}

                @Override
                public void destroy() {}

                @Override
                public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
                                                                                                                              IOException, ServletException {
                    MDC.put("application", applicationName);
                    filterChain.doFilter(servletRequest, servletResponse);
                }
            };
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(GalaxyApplication.class, args);
    }

}
