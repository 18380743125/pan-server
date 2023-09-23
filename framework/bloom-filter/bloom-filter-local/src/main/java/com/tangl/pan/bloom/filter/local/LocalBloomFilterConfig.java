package com.tangl.pan.bloom.filter.local;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author tangl
 * @description
 * @create 2023-09-23 21:18
 */
@Component
@ConfigurationProperties(value = "com.tangl.pan.bloom.filter.local")
@Data
public class LocalBloomFilterConfig {

    private List<LocalBloomFilterConfigItem> items;

}
