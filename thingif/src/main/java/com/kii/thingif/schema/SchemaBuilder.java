package com.kii.thingif.schema;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.kii.thingif.TargetState;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.ActionResult;

import java.util.ArrayList;
import java.util.List;

public class SchemaBuilder {

    private final String thingType;
    private final String schemaName;
    private final int schemaVersion;
    private final List<Class<? extends Action>> actionClasses = new ArrayList<Class<? extends Action>>();
    private final List<Class<? extends ActionResult>> actionResultClasses = new ArrayList<Class<? extends ActionResult>>();
    private final Class<? extends TargetState> stateClass;

    private SchemaBuilder(
            @NonNull String thingType,
            @NonNull String schemaName,
            int schemaVersion,
            @NonNull Class<? extends TargetState> stateClass) {
        this.thingType = thingType;
        this.schemaName = schemaName;
        this.schemaVersion = schemaVersion;
        this.stateClass = stateClass;
    }

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
        if (TextUtils.isEmpty(thingType)) {
            throw new IllegalArgumentException("thingType is null or empty");
        }
        if (TextUtils.isEmpty(schemaName)) {
            throw new IllegalArgumentException("schemaName is null or empty");
        }
        if (schemaVersion < 0) {
            throw new IllegalArgumentException("schemaVersion is negative value");
        }
        if (stateClass == null) {
            throw new IllegalArgumentException("stateClass is null");
        }
        return new SchemaBuilder(thingType, schemaName, schemaVersion, stateClass);
    }

    /** Add action class to the schema
     * @param actionClass action defined in schema is serialized in this
     *                    class.
     * @param actionResultClass action result defined in the schema is serialized
     *                          in this class.
     * @return SchemaBuilder instance for method chaining.
     */
    public SchemaBuilder addActionClass(@NonNull Class<? extends Action> actionClass,
                                        @NonNull Class<? extends ActionResult> actionResultClass) {
        if (actionClass == null) {
            throw new IllegalArgumentException("actionClass is null");
        }
        if (this.actionClasses.contains(actionClass)) {
            throw new IllegalArgumentException(actionClass.getName() + " already contains in this schema");
        }
        if (actionResultClass == null) {
            throw new IllegalArgumentException("actionResultClass is null");
        }
        if (this.actionClasses.contains(actionResultClass)) {
            throw new IllegalArgumentException(actionResultClass.getName() + " already contains in this schema");
        }
        try {
            Action action = actionClass.newInstance();
            ActionResult actionResult = actionResultClass.newInstance();
            if (!TextUtils.equals(action.getActionName(), actionResult.getActionName())) {
                throw new IllegalArgumentException("Action.actionName different from ActionResult.actionName");
            }
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }

        this.actionClasses.add(actionClass);
        this.actionResultClasses.add(actionResultClass);
        return this;
    }

    /** Build schema object.
     * @return Schema instance.
     */
    public Schema build() {
        return new Schema(this.thingType, this.schemaName,
                this.schemaVersion, this.actionClasses,
                this.actionResultClasses, this.stateClass);
    }
}
