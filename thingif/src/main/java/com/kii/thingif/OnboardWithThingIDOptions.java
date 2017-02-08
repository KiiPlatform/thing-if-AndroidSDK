package com.kii.thingif;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Optional parameters of {@link ThingIFAPI#onboard(String, String, OnboardWithThingIDOptions)}.
 */
public class OnboardWithThingIDOptions {
    private final LayoutPosition layoutPosition;

    private OnboardWithThingIDOptions(
            @Nullable LayoutPosition position) {
        this.layoutPosition = position;
    }

    @Nullable
    public LayoutPosition getLayoutPosition() {
        return this.layoutPosition;
    }

    public static class Builder {
        private LayoutPosition layoutPosition;

        /**
         * set layout position.
         * @param layoutPosition GATEWAY | STANDALONE | ENDNODE.
         * @return this.
         */
        @NonNull
        public Builder setLayoutPosition(@Nullable  LayoutPosition layoutPosition) {
            this.layoutPosition = layoutPosition;
            return this;
        }

        /**
         * get layout position.
         * @return layout position.
         */
        @Nullable
        public LayoutPosition getLayoutPosition() { return this.layoutPosition; }

        /**
         * build option object.
         * @return OnboardWithThingIDOptions object.
         */
        @NonNull
        public OnboardWithThingIDOptions build() {
            return new OnboardWithThingIDOptions(this.layoutPosition);
        }
    }
}
