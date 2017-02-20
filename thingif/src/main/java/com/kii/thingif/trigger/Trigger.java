package com.kii.thingif.trigger;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.kii.thingif.TypedID;
import com.kii.thingif.command.Command;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a trigger that is fired when status of thing changed or it became at the designated time.
 */
public class Trigger implements Parcelable {

    @NonNull private String triggerID;
    @Expose @NonNull private TypedID targetID;
    @NonNull private final Predicate predicate;
    @Nullable private final Command command;
    @Nullable private final ServerCode serverCode;
    private boolean disabled;
    @Nullable private String disabledReason;
    @Nullable private String title;
    @Nullable private String description;
    @Nullable private JSONObject metadata;

    Trigger(
            @NonNull String triggerID,
            @NonNull TypedID targetID,
            @NonNull Predicate predicate,
            @Nullable Command command,
            @Nullable ServerCode serverCode,
            boolean disabled,
            @Nullable String disabledReason,
            @Nullable String title,
            @Nullable String description,
            @Nullable JSONObject metadata) {
        this.triggerID = triggerID;
        this.targetID = targetID;
        this.predicate = predicate;
        this.command = command;
        this.disabled = disabled;
        this.disabledReason = disabledReason;
        this.title = title;
        this.description = description;
        this.metadata = metadata;
        this.serverCode = serverCode;
    }

    /**
     * Get ID of the Trigger.
     * @return ID of the Trigger
     */
    @NonNull
    public String getTriggerID() {
        return this.triggerID;
    }

    /**
     * Get Target ID of the Trigger.
     * When Trigger is created with ThingIFAPI#postNewTrigger() API families,
     * Target ID is determined by target bound to ThingIFAPI.
     * @return Target ID of Trigger.
     */
    @NonNull
    public TypedID getTargetID() {
        return this.targetID;
    }

    /**
     * Indicate whether the Trigger is disabled.
     * @return true if disabled, otherwise false.
     */
    public boolean disabled() {
        return this.disabled;
    }

    /**
     * Get Predicate of the Trigger.
     * @return Predicate of the Trigger
     */
    @NonNull
    public Predicate getPredicate() {
        return this.predicate;
    }

    /**
     * Get Command bounds to the Trigger.
     * @return Command  bounds to the Trigger.
     */
    @Nullable
    public Command getCommand() {
        return this.command;
    }

    /**
     * Get Server Code bounds to the Trigger.
     * @return Server Code bounds to the Trigger.
     */
    @Nullable
    public ServerCode getServerCode() {
        return this.serverCode;
    }

    /**
     * Get enum indicates whether the Command or Server Code is triggered.
     * @return TriggersWhat enum.
     */
    public TriggersWhat getTriggersWhat() {
        if (this.command != null) {
            return TriggersWhat.COMMAND;
        }
        return TriggersWhat.SERVER_CODE;
    }


    /**
     * Get the reason of the Trigger has been disabled.
     * If #disabled is false, It returns null.
     * @return Reason of the Trigger has been disabled.
     */
    @Nullable
    public String getDisabledReason() {
        return this.disabledReason;
    }

    /**
     * Get title.
     * @return title of this trigger.
     */
    @Nullable
    public String getTitle() {
        return this.title;
    }
    /**
     * Get description.
     * @return description of this trigger.
     */
    @Nullable
    public String getDescription() {
        return this.description;
    }
    /**
     * Get meta data
     * @return meta data of this trigger.
     */
    @Nullable
    public JSONObject getMetadata() {
        return this.metadata;
    }

    // Implementation of Parcelable
    protected Trigger(Parcel in) {
        this.triggerID = in.readString();
        this.targetID = in.readParcelable(TypedID.class.getClassLoader());
        this.predicate = in.readParcelable(Predicate.class.getClassLoader());
        this.command = in.readParcelable(Command.class.getClassLoader());
        this.serverCode = in.readParcelable(Command.class.getClassLoader());
        this.disabled = (in.readByte() != 0);
        this.disabledReason = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        String metadata = in.readString();
        if (!TextUtils.isEmpty(metadata)) {
            try {
                this.metadata = new JSONObject(metadata);
            } catch (JSONException ignore) {
                // Won’t happen
            }
        }
    }

    public static final Creator<Trigger> CREATOR = new Creator<Trigger>() {
        @Override
        public Trigger createFromParcel(Parcel in) {
            return new Trigger(in);
        }

        @Override
        public Trigger[] newArray(int size) {
            return new Trigger[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.triggerID);
        dest.writeParcelable(this.targetID, flags);
        dest.writeParcelable(this.predicate, flags);
        dest.writeParcelable(this.command, flags);
        dest.writeParcelable(this.serverCode, flags);
        dest.writeByte((byte) (this.disabled ? 1 : 0));
        dest.writeString(this.disabledReason);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.metadata == null ? null : this.metadata.toString());
    }
}
