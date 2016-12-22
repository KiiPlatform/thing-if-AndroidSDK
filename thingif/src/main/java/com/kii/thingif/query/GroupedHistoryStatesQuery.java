package com.kii.thingif.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.TraitAlias;
import com.kii.thingif.trigger.clause.Clause;

public class GroupedHistoryStatesQuery {

    private @NonNull TraitAlias alias;
    private @Nullable Clause clause;
    private @Nullable String firmwareVersion;
    private @NonNull TimeRange timeRange;

    private GroupedHistoryStatesQuery(
            @NonNull TraitAlias alias,
            @NonNull TimeRange timeRange,
            @Nullable  String firmwareVersion,
            @Nullable Clause clause) {
        this.alias = alias;
        this.clause = clause;
        this.firmwareVersion = firmwareVersion;
        this.timeRange = timeRange;
    }

    public static class Builder{
        private @NonNull TraitAlias alias;
        private @Nullable Clause clause;
        private @Nullable String firmwareVersion;
        private @NonNull TimeRange timeRange;

        public Builder(
                @NonNull TraitAlias alias,
                @NonNull TimeRange timeRange) {
            this.alias = alias;
            this.clause = clause;
        }

        public static Builder newBuilder(
                @NonNull TraitAlias alias,
                @NonNull TimeRange timeRange) {
            return new Builder(alias, timeRange);
        }

        public Builder setTimeRange(@NonNull TimeRange timeRange) {
            this.timeRange = timeRange;
            return this;
        }

        public Builder setClause(@Nullable Clause clause) {
            this.clause = clause;
            return this;
        }

        public GroupedHistoryStatesQuery build() {
            return new GroupedHistoryStatesQuery(
                    this.alias,
                    this.timeRange,
                    this.firmwareVersion,
                    this.clause
            );
        }
    }
}
