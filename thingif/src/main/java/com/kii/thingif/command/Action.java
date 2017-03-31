package com.kii.thingif.command;

import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.TriggerOptions;
import com.kii.thingif.trigger.TriggeredCommandForm;

/**
 * Marks a class as a single action. The action class should implement this interface and has one
 * field to hold value of the action. You either make name of this field same as action name or
 * use {@link com.google.gson.annotations.SerializedName} annotation and make value of serializedName
 * same as action name.
 * <br>
 * SDK serializes Action objects using
 * <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Object-Examples">Gson </a>,
 * when calling the following APIs:
 * <ul>
 * <li>{@link com.kii.thingif.ThingIFAPI#postNewCommand(CommandForm)}
 * <li>{@link com.kii.thingif.ThingIFAPI#postNewTrigger(TriggeredCommandForm, Predicate, TriggerOptions)}
 * </ul>
 * <br><br>
 * When parsing json formatted action from kii cloud server, SDK uses Gson too. You must register
 * class of Action to ThingIFAPI instance when constructed API:
 * <ul>
 * <li>{@link com.kii.thingif.ThingIFAPI.Builder#registerAction(String, String, Class)} (String, Class)}.
 * </ul>
 *
 */
public interface Action {
}
