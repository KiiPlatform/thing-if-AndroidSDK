package com.kii.thingif.clause.trigger;

import android.os.Parcel;

import com.kii.thingif.clause.base.BaseNotEquals;
import com.kii.thingif.clause.query.NotEqualsClauseInQuery;

import org.json.JSONException;
import org.json.JSONObject;

public class NotEqualsClauseInTrigger implements BaseNotEquals, TriggerClause {
    private EqualsClauseInTrigger equals;

    private volatile int hashCode; // cached hashcode for performance

    public NotEqualsClauseInTrigger(EqualsClauseInTrigger equals) {
        this.equals = equals;
    }

    @Override
    public EqualsClauseInTrigger getEquals() {
        return this.equals;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        try{
            ret.put("type", "not");
            ret.put("clause", this.equals.toJSONObject());
        }catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
        return ret;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.equals, flags);
    }

    private NotEqualsClauseInTrigger(Parcel in) {
        this.equals = in.readParcelable(EqualsClauseInTrigger.class.getClassLoader());
    }

    public static final Creator<NotEqualsClauseInTrigger> CREATOR = new Creator<NotEqualsClauseInTrigger>() {
        @Override
        public NotEqualsClauseInTrigger createFromParcel(Parcel source) {
            return new NotEqualsClauseInTrigger(source);
        }

        @Override
        public NotEqualsClauseInTrigger[] newArray(int size) {
            return new NotEqualsClauseInTrigger[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof NotEqualsClauseInTrigger)) return false;
        NotEqualsClauseInTrigger notEquals = (NotEqualsClauseInTrigger) o;
        return this.equals.equals(notEquals.equals);
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if(result == 0) {
            result = 17;
            result = 31 * result + this.equals.hashCode();
            this.hashCode = result;
        }
        return result;
    }
}
