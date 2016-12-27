package com.kii.thingif.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.trigger.clause.Clause;

public class GroupedHistoryStatesQuery {

    private @NonNull String alias;
    private @Nullable Clause clause;
    private @Nullable String firmwareVersion;
    private @NonNull TimeRange timeRange;

    private GroupedHistoryStatesQuery(
            @NonNull String alias,
            @NonNull TimeRange timeRange,
            @Nullable  String firmwareVersion,
            @Nullable Clause clause) {
        this.alias = alias;
        this.clause = clause;
        this.firmwareVersion = firmwareVersion;
        this.timeRange = timeRange;
    }

    public static class Builder{
        private @NonNull String alias;
        private @Nullable Clause clause;
        private @Nullable String firmwareVersion;
        private @NonNull TimeRange timeRange;

        public Builder(
                @NonNull String alias,
                @NonNull TimeRange timeRange) {
            this.alias = alias;
            this.timeRange = timeRange;
        }

        public static Builder newBuilder(
                @NonNull String alias,
                @NonNull TimeRange timeRange) {
            return new Builder(alias, timeRange);
        }

        public Builder setClause(@Nullable Clause clause) {
            this.clause = clause;
            return this;
        }

        public Builder setFirmwareVersion(@Nullable String firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
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
