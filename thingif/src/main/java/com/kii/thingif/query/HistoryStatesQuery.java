package com.kii.thingif.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.TargetState;
import com.kii.thingif.trigger.clause.Clause;

public class HistoryStatesQuery {

    private @NonNull String alias;
    private @Nullable Clause clause;
    private @Nullable String firmwareVersion;
    private @Nullable Integer bestEffortLimit;
    private @Nullable String nextPaginationKey;

    private HistoryStatesQuery(
            @NonNull  String alias,
            @Nullable Clause clause,
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
        private @Nullable Clause clause;
        private @Nullable String firmwareVersion;
        private @Nullable Integer bestEffortLimit;
        private @Nullable String nextPaginationKey;

        public Builder(@NonNull String alias){
            this.alias = alias;
        }
        public static Builder newBuilder(@NonNull String alias){
            return new Builder(alias);
        }

        public Builder setClause(@NonNull Clause clause) {
            this.clause = clause;
            return this;
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

    @Nullable
    public Clause getClause() {
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
}
