package com.kii.iotcloud.model;

import com.kii.iotcloud.TargetState;

public class LightState extends TargetState {
    public boolean power;
    public int brightness;
    public int[] color = new int[3];
    public int colorTemperature;
}
