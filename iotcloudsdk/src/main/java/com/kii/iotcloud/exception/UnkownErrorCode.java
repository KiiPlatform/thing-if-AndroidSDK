package com.kii.iotcloud.exception;

public class UnkownErrorCode implements ErrorCode {
    private final String errorCode;
    public UnkownErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    @Override
    public String getCode() {
        return this.errorCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnkownErrorCode that = (UnkownErrorCode) o;
        return !(errorCode != null ? !errorCode.equals(that.errorCode) : that.errorCode != null);
    }
    @Override
    public int hashCode() {
        return errorCode != null ? errorCode.hashCode() : 0;
    }
}
