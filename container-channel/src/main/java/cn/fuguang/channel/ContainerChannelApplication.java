package cn.fuguang.channel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties
@EnableFeignClients(basePackages = {"cn.fuguang.api"})
@MapperScan("cn.fuguang.channel.mapper")
public class ContainerChannelApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContainerChannelApplication.class);
    }
}
