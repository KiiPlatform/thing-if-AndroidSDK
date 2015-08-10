package com.kii.iotcloud.schema;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.kii.iotcloud.TargetState;
import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.ActionResult;

import java.util.ArrayList;
import java.util.List;

/** Object represents Schema.
 */
public class Schema implements Parcelable {

    private final String thingType;
    private final String schemaName;
    private final int schemaVersion;
    private final List<Class<? extends Action>> actionClasses;
    private final List<Class<? extends ActionResult>> actionResultClasses;
    private final Class<? extends TargetState> stateClass;

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
    }

    public String getSchemaName() {
        return this.schemaName;
    }

    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    protected Schema(Parcel in) {
        this.thingType = in.readString();
        this.schemaName = in.readString();
        this.schemaVersion = in.readInt();
        this.actionClasses = new ArrayList<Class<? extends Action>>();
        in.readList(this.actionClasses, Schema.class.getClassLoader());
        this.actionResultClasses = new ArrayList<Class<? extends ActionResult>>();
        in.readList(this.actionResultClasses, Schema.class.getClassLoader());
        this.stateClass = (Class<? extends TargetState>)in.readSerializable();
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
