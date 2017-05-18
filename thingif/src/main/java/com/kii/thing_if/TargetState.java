package com.kii.thing_if;

import android.content.Context;

import com.kii.thing_if.query.GroupedHistoryStatesQuery;
import com.kii.thing_if.query.HistoryStatesQuery;

import java.io.Serializable;

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
 * {@link com.kii.thing_if.ThingIFAPI.Builder#newBuilder(Context, KiiApp, Owner)}
 * or {@link com.kii.thing_if.ThingIFAPI.Builder#registerTargetState(String, Class)}.
 *
 */
public interface TargetState extends Serializable {
}
