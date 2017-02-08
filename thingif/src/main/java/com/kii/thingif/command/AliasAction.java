package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;

/**
 * Represent Action of alias
 * @param <T> Action class
 */
public class AliasAction<T extends Action> implements Parcelable{
    @NonNull private String alias;
    @NonNull private T action;

    private volatile int hashCode; // cached hashcode for performance

    /**
     * Initialize AliasAction instance.
     * @param alias alias name.
     * @param action instance of concrete Action class.
     */
    public AliasAction(
            @NonNull String alias,
            @NonNull T action) {
        if (TextUtils.isEmpty(alias)) {
            throw new IllegalArgumentException("alias is empty or null");
        }

        if (action == null) {
            throw new IllegalArgumentException("action is null");
        }
        this.alias = alias;
        this.action = action;
    }

    @NonNull
    public String getAlias() {
        return alias;
    }

    @NonNull
    public T getAction() {
        return action;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.alias);
        dest.writeSerializable(this.action.getClass());
        dest.writeString(new Gson().toJson(this.action));
    }

    public AliasAction(Parcel in) {
        this.alias = in.readString();
        Class<T> actionType = (Class<T>)in.readSerializable();
        String jsonString = in.readString();
        this.action = new Gson().fromJson(jsonString, actionType);
    }

    public static final Creator<AliasAction> CREATOR = new Creator<AliasAction>() {
        @Override
        public AliasAction createFromParcel(Parcel source) {
            return new AliasAction(source);
        }

        @Override
        public AliasAction[] newArray(int size) {
            return new AliasAction[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof AliasAction)) return false;
        if (!((AliasAction) o).getAction().getClass().equals(this.action.getClass())) return false;
        T action = (T)((AliasAction) o).getAction();
        return this.action.equals(action) &&
                this.alias.equals(((AliasAction) o).getAlias());
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = 17;
            result = 31 * result + this.alias.hashCode();
            result = 31 * result + this.action.hashCode();
            this.hashCode = result;
        }
        return result;
    }
}
