package com.kii.thingif.command;

import android.content.Context;

import com.kii.thingif.KiiApp;
import com.kii.thingif.Owner;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.TriggerOptions;
import com.kii.thingif.trigger.TriggeredCommandForm;

import java.util.Map;

/**
 * Marks a class as a single action. The action class should implement this interface and have only
 * one field as value of the action.
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
    String getActionName();
}
