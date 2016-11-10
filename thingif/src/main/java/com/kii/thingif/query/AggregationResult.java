package com.kii.thingif.query;

public class AggregationResult {
    private String name;
    private Object value;
    private Object object;

    public AggregationResult(String name, Object value, Object object) {
        this.name = name;
        this.value = value;
        this.object = object;
    }
}