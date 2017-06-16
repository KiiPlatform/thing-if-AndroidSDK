package com.kii.thing_if.mock_test.utils;

import com.kii.thing_if.Target;

public class EquableTarget extends EquableObject<Target> {


    public EquableTarget(Target obj) {
        super(obj);
    }

    @Override
    public boolean equalsObjField(Target field) {
        if (field == null) {
            return false;
        }
        if (obj.getClass() != field.getClass()) {
            return false;
        }
        if (!obj.getTypedID().equals(field.getTypedID())) {
            return false;
        }
        if (obj.getAccessToken() == null) {
            if (field.getAccessToken() == null) {
                return true;
            }
            return false;
        }
        return obj.getAccessToken().equals(field.getAccessToken());
    }
}
