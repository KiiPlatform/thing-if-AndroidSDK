package com.kii.thingif.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.TraitAlias;
import com.kii.thingif.trigger.clause.Clause;

public class HistoryStatesQuery {

    private @NonNull TraitAlias alias;
    private @Nullable Clause clause;
    private @Nullable String firmwareVersion;
    private @Nullable Integer bestEffortLimit;
    private @Nullable String nextPaginationKey;

    private HistoryStatesQuery(
            @NonNull  TraitAlias alias,
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

        private @NonNull TraitAlias alias;
        private @Nullable Clause clause;
        private @Nullable String firmwareVersion;
        private @Nullable Integer bestEffortLimit;
        private @Nullable String nextPaginationKey;

        public Builder(@NonNull TraitAlias alias){
            this.alias = alias;
        }
        public static Builder newBuilder(@NonNull TraitAlias alias){
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
    public TraitAlias getAlias() {
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
