package com.kii.thingif.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.clause.query.QueryClause;


public class GroupedHistoryStatesQuery {

    private @NonNull String alias;
    private @Nullable QueryClause clause;
    private @Nullable String firmwareVersion;
    private @NonNull TimeRange timeRange;

    private GroupedHistoryStatesQuery(
            @NonNull String alias,
            @NonNull TimeRange timeRange,
            @Nullable  String firmwareVersion,
            @Nullable QueryClause clause) {
        this.alias = alias;
        this.clause = clause;
        this.firmwareVersion = firmwareVersion;
        this.timeRange = timeRange;
    }

    public static class Builder{
        private @NonNull String alias;
        private @Nullable QueryClause clause;
        private @Nullable String firmwareVersion;
        private @NonNull TimeRange timeRange;

        /**
         * Initialize Builder instance
         * @param alias name of alias
         * @param timeRange time range of grouped query. The range should less
         *                  than 60 data grouping intervals.
         */
        public Builder(
                @NonNull String alias,
                @NonNull TimeRange timeRange) {
            this.alias = alias;
            this.timeRange = timeRange;
        }

        /**
         * Crate Builder
         * @param alias name of alias.
         * @param timeRange time range of grouped query. The range should less
         *                  than 60 data grouping intervals.
         * @return Builder instance.
         */
        public static Builder newBuilder(
                @NonNull String alias,
                @NonNull TimeRange timeRange) {
            return new Builder(alias, timeRange);
        }

        /**
         * Set clause for query.
         * @param clause {@link QueryClause} instance
         * @return Builder instance for chaining.
         */
        public Builder setClause(@Nullable QueryClause clause) {
            this.clause = clause;
            return this;
        }

        /**
         * Set firmware version for grouped query.
         * @param firmwareVersion firmware version
         * @return Builder instance for chaining.
         */
        public Builder setFirmwareVersion(@Nullable String firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
            return this;
        }

        /**
         * Create {@link GroupedHistoryStatesQuery} instance.
         * @return {@link GroupedHistoryStatesQuery} instance.
         */
        public GroupedHistoryStatesQuery build() {
            return new GroupedHistoryStatesQuery(
                    this.alias,
                    this.timeRange,
                    this.firmwareVersion,
                    this.clause
            );
        }
    }

    @NonNull
    public String getAlias() {
        return alias;
    }

    @Nullable
    public QueryClause getClause() {
        return clause;
    }

    @Nullable
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    @NonNull
    public TimeRange getTimeRange() {
        return timeRange;
    }
}
