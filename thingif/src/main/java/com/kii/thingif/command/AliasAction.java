package com.kii.thingif.command;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent Action of alias
 */
public class AliasAction implements Parcelable{
    @NonNull private String alias;
    @NonNull private List<? extends Action> actions;

    private transient volatile int hashCode; // cached hashcode for performance

    /**
     * Initialize AliasAction instance.
     * @param alias alias name.
     * @param actions instance of concrete Action class.
     */
    public AliasAction(
            @NonNull String alias,
            @NonNull List<? extends Action> actions) {
        if (TextUtils.isEmpty(alias)) {
            throw new IllegalArgumentException("alias is empty or null");
        }

        if (actions == null) {
            throw new IllegalArgumentException("action is null");
        }
        this.alias = alias;
        this.actions = actions;
    }

    @NonNull
    public String getAlias() {
        return alias;
    }

    @NonNull
    public List<? extends Action> getActions() {
        return actions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.alias);
        //TODO: // FIXME: 2017/03/27
//        dest.writeSerializable(this.action.getClass());
//        dest.writeString(new Gson().toJson(this.action));
    }

    public AliasAction(Parcel in) {
        this.alias = in.readString();
        //TODO: // FIXME: 2017/03/27
//        Class<T> actionType = (Class<T>)in.readSerializable();
//        String jsonString = in.readString();
//        this.action = new Gson().fromJson(jsonString, actionType);
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
        return false;
        //TODO: // FIXME: 2017/03/27
//        if (o == null) return false;
//        if (!(o instanceof AliasAction)) return false;
//        if (!((AliasAction) o).getAction().getClass().equals(this.action.getClass())) return false;
//        T action = (T)((AliasAction) o).getAction();
//        return this.action.equals(action) &&
//                this.alias.equals(((AliasAction) o).getAlias());
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = 17;
            result = 31 * result + this.alias.hashCode();
            //TODO // FIXME: 2017/03/27
//            result = 31 * result + this.action.hashCode();
            this.hashCode = result;
        }
        return result;
    }

    /**
     * Retrieve actions by Class of Action
     * @param classOfT class of Action
     * @param <T> Action Type
     * @return list of Action instance.
     */
    @NonNull
    public <T extends Action> List<T> getActions(Class<T> classOfT) {
        //TODO: // FIXME: 2017/03/27
        return new ArrayList<>();
    }
}
