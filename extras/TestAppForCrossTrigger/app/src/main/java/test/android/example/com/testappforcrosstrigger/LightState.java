package test.android.example.com.testappforcrosstrigger;

import com.kii.thingif.TargetState;

public class LightState extends TargetState {
    public boolean power;
    public int brightness;
    public int[] color = new int[3];
    public int colorTemperature;
}
