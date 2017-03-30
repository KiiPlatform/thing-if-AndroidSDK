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
    @NonNull private List<Action> actions;

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
            throw new IllegalArgumentException("actions is null");
        }
        this.alias = alias;
        this.actions = new ArrayList<>();
        for (Action action: actions) {
            this.actions.add(action);
        }
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
        dest.writeInt(this.actions.size());
        Gson gson = new Gson();
        for (Action action : this.actions) {
            dest.writeSerializable(action.getClass());
            dest.writeString(gson.toJson(action));
        }
    }

    public AliasAction(Parcel in) {
        this.alias = in.readString();
        List<Action> actions = new ArrayList<>();
        int size = in.readInt();
        Gson gson = new Gson();
        for (int i=0 ; i < size; i++) {
            Class<Action> actionClass = (Class<Action>) in.readSerializable();
            Action action = gson.fromJson(in.readString(), actionClass);
            actions.add(action);
        }
        this.actions = actions;
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
        return o != null &&
                o instanceof AliasAction &&
                this.actions.equals(((AliasAction) o).getActions()) &&
                this.alias.equals(((AliasAction) o).getAlias());
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

    /**
     * Retrieve actions by Class of Action
     * @param classOfT class of Action
     * @param <T> Action Type
     * @return list of Action instance.
     */
    @NonNull
    public <T extends Action> List<T> getActions(Class<T> classOfT) {
        List<T> foundActions = new ArrayList<>();
        for (Action action: this.actions) {
            if (action.getClass().equals(classOfT)) {
                foundActions.add(classOfT.cast(action));
            }
        }
        return foundActions;
    }
}
