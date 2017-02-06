package com.kii.thingif.command;

import android.content.Context;

import com.kii.thingif.KiiApp;
import com.kii.thingif.Owner;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.TriggerOptions;
import com.kii.thingif.trigger.TriggeredCommandForm;

import java.util.Map;

/**
 * Marks a class as group of single actions of command. The class must implement this interface and
 * define single actions as fields.
 * <br>
 * SDK serializes Action objects using
 * <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Object-Examples">Gson </a>,
 * when calling the following APIs:
 * <ul>
 * <li>{@link com.kii.thingif.ThingIFAPI#postNewCommand(CommandForm)}
 * <li>{@link com.kii.thingif.ThingIFAPI#postNewTrigger(TriggeredCommandForm, Predicate, TriggerOptions)}
 * </ul>
 * Null value of field is not included in serialized json.
 * <br><br>
 * When parsing json formatted action from kii cloud server, SDK uses Gson too. You must register
 * class of Action to ThingIFAPI instance when constructed by the following 2 APIs:
 * <ul>
 * <li>{@link com.kii.thingif.ThingIFAPI.Builder#newBuilder(Context, KiiApp, Owner, Map, Map)}
 * <li>{@link com.kii.thingif.ThingIFAPI.Builder#registerActions(String, Class)}.
 * </ul>
 *
 */
public interface Action {
}
