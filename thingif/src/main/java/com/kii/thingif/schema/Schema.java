package com.kii.thingif.schema;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.kii.thingif.core.TargetState;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.ActionResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Object represents Schema.
 */
public class Schema implements Parcelable {

    private final String thingType;
    private final String schemaName;
    private final int schemaVersion;
    private final List<Class<? extends Action>> actionClasses;
    private final List<Class<? extends ActionResult>> actionResultClasses;
    private final Class<? extends TargetState> stateClass;
    /**
     * index mapping for search Action/ActionResult by name
     */
    private Map<String, Pair<Class<? extends Action>, Class<? extends ActionResult>>> name2ActionClassMap = null;

    Schema(@NonNull String thingType,
           @NonNull String schemaName,
           @NonNull int schemaVersion,
           @NonNull List<Class<? extends Action>> actionClasses,
           @NonNull List<Class<? extends ActionResult>> actionResultClasses,
           @NonNull Class<? extends TargetState> stateClass) {
        this.thingType = thingType;
        this.schemaName = schemaName;
        this.schemaVersion = schemaVersion;
        this.actionClasses = actionClasses;
        this.actionResultClasses = actionResultClasses;
        this.stateClass = stateClass;
        this.initializeName2ActionClassMap();
    }

    public String getThingType() {
        return this.thingType;
    }

    public String getSchemaName() {
        return this.schemaName;
    }

    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    public List<Class<? extends Action>> getActionClasses() {
        return this.actionClasses;
    }
    public List<Class<? extends ActionResult>> getActionResultClasses() {
        return this.actionResultClasses;
    }
    public Class<? extends TargetState> getStateClass() {
        return this.stateClass;
    }

    public Class<? extends Action> getActionClass(@NonNull String actionName) {
        if (actionName == null) {
            throw new IllegalArgumentException("actionName is null");
        }
        Pair<Class<? extends Action>, Class<? extends ActionResult>> pair = this.name2ActionClassMap.get(actionName);
        if (pair != null) {
            return pair.first;
        }
        return null;
    }
    public Class<? extends ActionResult> getActionResultClass(@NonNull String actionName) {
        if (actionName == null) {
            throw new IllegalArgumentException("actionName is null");
        }
        Pair<Class<? extends Action>, Class<? extends ActionResult>> pair = this.name2ActionClassMap.get(actionName);
        if (pair != null) {
            return pair.second;
        }
        return null;
    }
    private synchronized void initializeName2ActionClassMap() {
        if (this.name2ActionClassMap == null) {
            this.name2ActionClassMap = Collections.synchronizedMap(new HashMap<String, Pair<Class<? extends Action>, Class<? extends ActionResult>>>());
            if (this.name2ActionClassMap.isEmpty()) {
                for (int i = 0; i < this.actionClasses.size(); i++) {
                    Class<? extends Action> actionClass = this.actionClasses.get(i);
                    Class<? extends ActionResult> actionResultClass = this.actionResultClasses.get(i);
                    try {
                        this.name2ActionClassMap.put(actionClass.newInstance().getActionName(),
                                new Pair<Class<? extends Action>, Class<? extends ActionResult>>(actionClass, actionResultClass));
                    } catch (Exception ignore) {
                        // Won’t happen
                    }
                }
            }
        }
    }

    // Implementation of Parcelable
    protected Schema(Parcel in) {
        this.thingType = in.readString();
        this.schemaName = in.readString();
        this.schemaVersion = in.readInt();
        this.actionClasses = new ArrayList<Class<? extends Action>>();
        in.readList(this.actionClasses, Schema.class.getClassLoader());
        this.actionResultClasses = new ArrayList<Class<? extends ActionResult>>();
        in.readList(this.actionResultClasses, Schema.class.getClassLoader());
        this.stateClass = (Class<? extends TargetState>)in.readSerializable();
        this.initializeName2ActionClassMap();
    }
    public static final Creator<Schema> CREATOR = new Creator<Schema>() {
        @Override
        public Schema createFromParcel(Parcel in) {
            return new Schema(in);
        }

        @Override
        public Schema[] newArray(int size) {
            return new Schema[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.thingType);
        dest.writeString(this.schemaName);
        dest.writeInt(this.schemaVersion);
        dest.writeList(this.actionClasses);
        dest.writeList(this.actionResultClasses);
        dest.writeSerializable(this.stateClass);
    }
}
