package se.omegapoint.micro;

import com.netflix.appinfo.AmazonInfo;
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
public class CompetenceDayApplication {

    @Configuration
    public static class Conf {

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
                    info.get(AmazonInfo.MetaDataKey.publicIpv4));
            config.setHostname(info.get(AmazonInfo.MetaDataKey.publicHostname));
            config.setIpAddress(info.get(AmazonInfo.MetaDataKey.publicIpv4));
            config.setNonSecurePort(8080);
            return config;
        }

    }
    public static void main(String[] args) {
        SpringApplication.run(CompetenceDayApplication.class, args);
    }

}
