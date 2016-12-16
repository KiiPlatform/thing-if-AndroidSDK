package com.kii.thingif.query;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.kii.thingif.trigger.clause.Clause;

public class HistoryStateQuery implements Parcelable{

    public static class Builder{
        private Clause clause;
        private String firmwareVersion;
        private String alias;

        private Builder(Clause clause){
            this.clause = clause;
        }
        public static Builder newBuilder(@NonNull Clause clause){
            return new Builder(clause);
        }

        public Builder setFirmwareVersion(@NonNull String firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
            return this;
        }

        public Builder setAlias(@NonNull String alias) {
            this.alias = alias;
            return this;
        }

        public HistoryStateQuery build() {

            HistoryStateQuery query = new HistoryStateQuery(this.clause);
            query.firmwareVersion = this.firmwareVersion;
            query.alias = this.alias;

            return query;
        }
    }

    private Clause clause;
    private String firmwareVersion;
    private String alias;

    private HistoryStateQuery(Clause clause){
        this.clause = clause;
    }

    public Clause getClause() {
        return clause;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public String getAlias() {
        return alias;
    }


    private HistoryStateQuery(Parcel in) {
        this.clause = in.readParcelable(Clause.class.getClassLoader());
        this.alias = in.readString();
        this.firmwareVersion = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.clause, flags);
        dest.writeString(this.alias);
        dest.writeString(this.firmwareVersion);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HistoryStateQuery> CREATOR =
            new Creator<HistoryStateQuery>() {
                @Override
                public HistoryStateQuery createFromParcel(Parcel in) {
                    return new HistoryStateQuery(in);
                }

                @Override
                public HistoryStateQuery[] newArray(int size) {
                    return new HistoryStateQuery[size];
                }
            };

}
