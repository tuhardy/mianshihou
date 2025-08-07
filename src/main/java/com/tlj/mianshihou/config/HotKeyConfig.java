package com.tlj.mianshihou.config;

import com.jd.platform.hotkey.client.ClientStarter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "hotkey")
@Data
@Slf4j
public class HotKeyConfig {

    /**
     * Etcd 服务器完整地址
     */
    private String etcdServer;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 本地缓存最大数量
     */
    private int caffeineSize;

    /**
     * 批量推送 key 的间隔时间
     */
    private long pushPeriod;

    @PostConstruct
    public void initHotkey() {
        log.info("etcdServer:{}, appName:{}, caffeineSize:{}, pushPeriod:{}",etcdServer, appName, caffeineSize, pushPeriod);
        ClientStarter.Builder builder = new ClientStarter.Builder();
        ClientStarter starter = builder.setAppName(appName)
                .setCaffeineSize(caffeineSize)
                .setPushPeriod(pushPeriod)
                .setEtcdServer(etcdServer)
                .build();
        starter.startPipeline();
    }
}
