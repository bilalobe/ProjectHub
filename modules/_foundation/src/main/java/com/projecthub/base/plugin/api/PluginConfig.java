package com.projecthub.base.plugin.api;

import java.util.Map;

/**
 * Configuration settings for environmental plugins
 */
public record PluginConfig(
    String pluginId,
    Map<String, Object> settings,
    int samplingRateMs,
    Map<String, Double> thresholds,
    boolean enableAlerts
) {}