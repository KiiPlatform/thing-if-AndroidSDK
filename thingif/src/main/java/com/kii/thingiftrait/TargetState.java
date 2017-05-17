package com.kii.thingiftrait;

import android.content.Context;

import com.kii.thingiftrait.query.GroupedHistoryStatesQuery;
import com.kii.thingiftrait.query.HistoryStatesQuery;

import java.io.Serializable;
import java.util.Map;

/**
 * Marks a class as state of a target thing.
 * <br>
 * When SDK invokes following APIs:
 * <ul>
 * <li>{@link ThingIFAPI#getTargetState(String, Class)}
 * <li>{@link ThingIFAPI#getTargetState()}
 * <li>{@link ThingIFAPI#query(GroupedHistoryStatesQuery, Class)}
 * <li>{@link ThingIFAPI#query(HistoryStatesQuery, Class)}
 * </ul>
 * it receives json formatted target state from kii cloud server, then it uses
 * <a href="https://github.com/google/gson/blob/master/UserGuide.md#TOC-Object-Examples">Gson</a> to
 * parse the json to the concrete TargetState. You must register your TargetState concrete class to
 * ThingIFAPI instance when constructed by
 * {@link com.kii.thingiftrait.ThingIFAPI.Builder#newBuilder(Context, KiiApp, Owner)}
 * or {@link com.kii.thingiftrait.ThingIFAPI.Builder#registerTargetState(String, Class)}.
 *
 */
public interface TargetState extends Serializable {
}
