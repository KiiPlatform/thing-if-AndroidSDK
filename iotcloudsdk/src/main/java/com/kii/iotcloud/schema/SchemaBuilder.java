package com.kii.iotcloud.schema;

import android.support.annotation.NonNull;

import com.kii.iotcloud.TargetState;
import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.ActionResult;

public class SchemaBuilder {

    private SchemaBuilder() {}

    /** Instantiate new SchemaBuilder.
     * @param thingType type of the thing to which schema is bound.
     * @param schemaName name of the schema.
     * @param schemaVersion version of schema.
     * @param stateClass State class defines target state in this schema.
     */
    public static SchemaBuilder newSchemaBuilder(
            @NonNull String thingType,
            @NonNull String schemaName,
            int schemaVersion,
            @NonNull Class<? extends TargetState> stateClass) {
        // TODO: implement it.
        return null;
    }

    /** Add action class to the schema
     * @param actionName name of the action defined in schema.
     * @param actionClass action defined in schema is serialized in this
     *                    class.
     * @param actionResultClass action result defined in the schema is serialized
     *                          in this class.
     * @return SchemaBuilder instance for method chaining.
     */
    public SchemaBuilder addActionClass(@NonNull String actionName,
                                        @NonNull Class<? extends Action> actionClass,
                                        @NonNull Class<? extends ActionResult> actionResultClass) {
        // TODO: implement it.
        return null;
    }

    /** Build schema object.
     * @return Schema instance.
     */
    public Schema build() {
        // TODO: implement it.
        return null;
    }
}
