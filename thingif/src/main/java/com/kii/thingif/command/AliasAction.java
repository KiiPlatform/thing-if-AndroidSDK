package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represent Action of alias
 */
public class AliasAction implements Parcelable{
    @NonNull private String alias;
    @NonNull private List<Action> actions;

    private transient volatile int hashCode; // cached hashcode for performance

    /**
     * Initialize AliasAction instance.
     * @param alias alias name.
     * @param actions list of concrete Action instances.
     */
    public AliasAction(
            @NonNull String alias,
            @NonNull List<Action> actions) {
        if (TextUtils.isEmpty(alias)) {
            throw new IllegalArgumentException("alias is empty or null");
        }

        if (actions == null) {
            throw new IllegalArgumentException("actions is null");
        }
        this.alias = alias;
        this.actions = actions;
    }

    @NonNull
    public String getAlias() {
        return alias;
    }

    @NonNull
    public List<Action> getActions() {
        return actions;
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
        AliasAction other = (AliasAction)o;
        return Arrays.equals(this.actions.toArray(), other.actions.toArray()) &&
                this.alias.equals(other.getAlias());
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = 17;
            result = 31 * result + this.alias.hashCode();
            result = 31 * result + this.actions.hashCode();
            this.hashCode = result;
        }
        return result;
    }
}
