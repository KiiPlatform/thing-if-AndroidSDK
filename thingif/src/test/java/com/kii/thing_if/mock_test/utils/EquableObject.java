package com.kii.thing_if.mock_test.utils;

import com.google.gson.Gson;

abstract class EquableObject<T> {

    protected final T obj;

    protected EquableObject(T obj) {
        this.obj = obj;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        return equalsObjField((T) ((EquableObject)o).obj);
    }

    protected abstract boolean equalsObjField(T field);

    @Override
    public String toString() {
        return new Gson().toJson(this).toString();
    }
}
