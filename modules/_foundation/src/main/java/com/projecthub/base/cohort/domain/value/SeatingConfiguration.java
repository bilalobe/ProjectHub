package com.projecthub.base.cohort.domain.value;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Value object representing seating arrangement configuration for a cohort.
 * Supports different layout types, seating assignment strategies, and IoT device management.
 */
@Embeddable
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class SeatingConfiguration {
    @Min(1) int rows;
    @Min(1) int columns;
    String layoutType; // "CLASSROOM", "LAB", "CONFERENCE", "CUSTOM", etc.
    String assignmentStrategy; // "FIFO", "CONFIDENCE_BASED", "MANUAL", "RANDOM"
    
    @ElementCollection
    @CollectionTable(name = "cohort_custom_layouts", joinColumns = @JoinColumn(name = "cohort_id"))
    Map<String, List<Integer>> customLayout; // For custom layouts with irregular seat arrangements
    
    @ElementCollection
    @CollectionTable(name = "cohort_iot_devices", joinColumns = @JoinColumn(name = "cohort_id"))
    Map<String, PositionedDevice> iotDevices; // Map of device IDs to their positions
    
    /**
     * Creates a seating configuration with standard grid layout.
     */
    public SeatingConfiguration(int rows, int columns, String layoutType, String assignmentStrategy) {
        this(rows, columns, layoutType, assignmentStrategy, null, new HashMap<>());
    }
    
    /**
     * Creates a seating configuration with custom layout and IoT devices.
     */
    public SeatingConfiguration(int rows, int columns, String layoutType, String assignmentStrategy, 
                               Map<String, List<Integer>> customLayout, Map<String, PositionedDevice> iotDevices) {
        this.rows = rows;
        this.columns = columns;
        this.layoutType = layoutType;
        this.assignmentStrategy = assignmentStrategy;
        this.customLayout = customLayout;
        this.iotDevices = iotDevices != null ? iotDevices : new HashMap<>();
    }
    
    /**
     * Creates a standard seating configuration with MANUAL assignment strategy.
     */
    public static SeatingConfiguration create(int rows, int columns, String layoutType) {
        return new SeatingConfiguration(rows, columns, layoutType, "MANUAL");
    }

    /**
     * Creates a standard seating configuration with specified assignment strategy.
     */
    public static SeatingConfiguration create(int rows, int columns, String layoutType, String assignmentStrategy) {
        return new SeatingConfiguration(rows, columns, layoutType, assignmentStrategy);
    }
    
    /**
     * Creates a custom seating layout with specified rows/columns and configuration.
     */
    public static SeatingConfiguration createCustom(int rows, int columns, String layoutType, 
                                                  String assignmentStrategy, Map<String, List<Integer>> customLayout) {
        return new SeatingConfiguration(rows, columns, layoutType, assignmentStrategy, customLayout, new HashMap<>());
    }
    
    /**
     * Gets the total seating capacity.
     */
    public int getTotalCapacity() {
        if (customLayout != null && !customLayout.isEmpty()) {
            // For custom layouts, count the total number of positions
            return customLayout.values().stream()
                .mapToInt(List::size)
                .sum();
        }
        return rows * columns;
    }
    
    /**
     * Checks if a position is valid within this configuration.
     * For custom layouts, checks against the defined layout.
     */
    public boolean isPositionValid(int row, int column) {
        if (customLayout != null && !customLayout.isEmpty()) {
            // For custom layouts, check if the position exists in the custom layout
            String rowKey = String.valueOf(row);
            List<Integer> columns = customLayout.get(rowKey);
            return columns != null && columns.contains(column);
        }
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }
    
    /**
     * Gets all IoT devices of a specific type.
     * 
     * @param deviceType The type of device to filter by
     * @return Map of device IDs to devices of the specified type
     */
    public Map<String, PositionedDevice> getIotDevicesByType(String deviceType) {
        Map<String, PositionedDevice> result = new HashMap<>();
        
        if (iotDevices != null) {
            iotDevices.forEach((id, device) -> {
                if (device.getDeviceType().equals(deviceType)) {
                    result.put(id, device);
                }
            });
        }
        
        return result;
    }
    
    /**
     * Gets IoT devices near a specific position.
     * 
     * @param row Row coordinate
     * @param column Column coordinate
     * @param deviceType Optional device type filter (null for all types)
     * @return Map of device IDs to nearby devices
     */
    public Map<String, PositionedDevice> getNearbyIotDevices(int row, int column, String deviceType) {
        Map<String, PositionedDevice> result = new HashMap<>();
        
        if (iotDevices != null) {
            iotDevices.forEach((id, device) -> {
                if ((deviceType == null || device.getDeviceType().equals(deviceType)) &&
                    device.isNearPosition(row, column)) {
                    result.put(id, device);
                }
            });
        }
        
        return result;
    }
    
    /**
     * Sets the IoT devices for this configuration.
     */
    public void setIotDevices(Map<String, PositionedDevice> devices) {
        if (iotDevices == null) {
            iotDevices = new HashMap<>();
        } else {
            iotDevices.clear();
        }
        
        if (devices != null) {
            iotDevices.putAll(devices);
        }
    }
    
    /**
     * Adds an IoT device to this configuration.
     */
    public void addIotDevice(String deviceId, PositionedDevice device) {
        if (iotDevices == null) {
            iotDevices = new HashMap<>();
        }
        iotDevices.put(deviceId, device);
    }
    
    /**
     * Removes an IoT device from this configuration.
     */
    public void removeIotDevice(String deviceId) {
        if (iotDevices != null) {
            iotDevices.remove(deviceId);
        }
    }
    
    /**
     * Gets data as a map for API responses.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("rows", rows);
        map.put("columns", columns);
        map.put("layoutType", layoutType);
        map.put("assignmentStrategy", assignmentStrategy);
        map.put("totalCapacity", getTotalCapacity());
        map.put("isCustomLayout", customLayout != null && !customLayout.isEmpty());
        map.put("customLayout", customLayout);
        
        // Count devices by type
        Map<String, Integer> deviceCounts = new HashMap<>();
        if (iotDevices != null) {
            iotDevices.values().forEach(device -> {
                String type = device.getDeviceType();
                deviceCounts.put(type, deviceCounts.getOrDefault(type, 0) + 1);
            });
        }
        map.put("deviceCounts", deviceCounts);
        
        return map;
    }
}