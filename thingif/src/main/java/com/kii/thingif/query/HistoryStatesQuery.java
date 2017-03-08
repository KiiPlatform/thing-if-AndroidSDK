package com.kii.thingif.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.kii.thingif.clause.query.QueryClause;

public class HistoryStatesQuery {

    private transient @NonNull String alias;
    private transient @NonNull QueryClause clause;
    private @Nullable String firmwareVersion;
    private @Nullable Integer bestEffortLimit;
    @SerializedName("paginationKey")
    private @Nullable String nextPaginationKey;

    private transient volatile int hashCode; // cached hashcode for performance

    private HistoryStatesQuery(
            @NonNull  String alias,
            @NonNull QueryClause clause,
            @Nullable String firmwareVersion,
            @Nullable Integer bestEffortLimit,
            @Nullable String nextPaginationKey) {
        this.alias = alias;
        this.clause = clause;
        this.firmwareVersion = firmwareVersion;
        this.bestEffortLimit = bestEffortLimit;
        this.nextPaginationKey = nextPaginationKey;
    }

    public static class Builder {

        private @NonNull String alias;
        private @NonNull QueryClause clause;
        private @Nullable String firmwareVersion;
        private @Nullable Integer bestEffortLimit;
        private @Nullable String nextPaginationKey;

        private Builder(
                @NonNull String alias,
                @NonNull QueryClause clause){
            this.alias = alias;
            this.clause = clause;
        }
        public static Builder newBuilder(
                @NonNull String alias,
                @NonNull QueryClause clause){
            return new Builder(alias, clause);
        }

        public Builder setFirmwareVersion(@Nullable String firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
            return this;
        }

        public Builder setBestEffortLimit(@Nullable Integer bestEffortLimit) {
            this.bestEffortLimit = bestEffortLimit;
            return this;
        }

        public Builder setNextPaginationKey(@Nullable String nextPaginationKey) {
            this.nextPaginationKey = nextPaginationKey;
            return this;
        }

        public HistoryStatesQuery build() {
            if (TextUtils.isEmpty(this.alias)) {
                throw new IllegalArgumentException("alias is null or empty");
            }
            if (this.clause == null) {
                throw new IllegalArgumentException("clause is null or empty");
            }
            return new HistoryStatesQuery(
                    this.alias,
                    this.clause,
                    this.firmwareVersion,
                    this.bestEffortLimit,
                    this.nextPaginationKey
            );
        }
    }

    @NonNull
    public String getAlias() {
        return this.alias;
    }

    @NonNull
    public QueryClause getClause() {
        return this.clause;
    }

    @Nullable
    public String getFirmwareVersion() {
        return this.firmwareVersion;
    }

    @Nullable
    public Integer getBestEffortLimit() {
        return this.bestEffortLimit;
    }

    @Nullable
    public String getNextPaginationKey() {
        return this.nextPaginationKey;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof HistoryStatesQuery)) {
            return false;
        }
        HistoryStatesQuery other = (HistoryStatesQuery) o;

        if (this.firmwareVersion != null) {
            if (!this.firmwareVersion.equals(other.firmwareVersion)) {
                return false;
            }
        } else if (other.firmwareVersion != null) {
            return false;
        }

        if (this.bestEffortLimit != null) {
            if (!this.bestEffortLimit.equals(other.bestEffortLimit)) {
                return false;
            }
        } else if (other.bestEffortLimit != null) {
            return false;
        }

        if (this.nextPaginationKey != null) {
            if (!this.nextPaginationKey.equals(other.nextPaginationKey)) {
                return false;
            }
        } else if (other.nextPaginationKey != null) {
            return false;
        }

        return this.alias.equals(other.alias) && this.clause.equals(other.clause);
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = 17;
            result = 31 * result + this.alias.hashCode();
            result = 31 * result + this.clause.hashCode();
            result = 31 * result + (this.firmwareVersion != null ? this.firmwareVersion.hashCode() : 0);
            result = 31 * result + (this.bestEffortLimit != null ? this.bestEffortLimit.hashCode() : 0);
            result = 31 * result + (this.nextPaginationKey != null ? this.nextPaginationKey.hashCode() : 0);
            this.hashCode = result;
        }
        return result;
    }
}
