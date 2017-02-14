package com.kii.thingif;

import android.content.Context;

import com.kii.thingif.query.GroupedHistoryStatesQuery;
import com.kii.thingif.query.HistoryStatesQuery;

import java.io.Serializable;
import java.util.Map;

/**
 * Marks a class as state of a target thing.
 * <br>
 * When SDK invokes following APIs:
 * <ul>
 * <li>{@link ThingIFAPI#getTargetState(String)}
 * <li>{@link ThingIFAPI#getTargetState()}
 * <li>{@link ThingIFAPI#query(GroupedHistoryStatesQuery)}
 * <li>{@link ThingIFAPI#query(HistoryStatesQuery)}
 * </ul>
 * it receives json formatted target state from kii cloud server, then it uses
 * <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Object-Examples">Gson</a> to
 * parse the json to the concrete TargetState. You must register your TargetState concrete class to
 * ThingIFAPI instance when constructed by
 * {@link com.kii.thingif.ThingIFAPI.Builder#newBuilder(Context, KiiApp, Owner, Map, Map)}
 * or {@link com.kii.thingif.ThingIFAPI.Builder#registerTargetState(String, Class)}.
 *
 */
public interface TargetState extends Serializable {
}
