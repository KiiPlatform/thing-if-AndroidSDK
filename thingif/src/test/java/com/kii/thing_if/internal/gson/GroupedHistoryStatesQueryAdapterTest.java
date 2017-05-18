package com.kii.thing_if.internal.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kii.thing_if.clause.query.EqualsClauseInQuery;
import com.kii.thing_if.internal.utils.JsonUtils;
import com.kii.thing_if.query.Aggregation;
import com.kii.thing_if.query.GroupedHistoryStatesQuery;
import com.kii.thing_if.query.TimeRange;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Date;

public class GroupedHistoryStatesQueryAdapterTest {

    @Test
    public void serializationBasicTest() throws Exception {
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        String expected = JsonUtils.newJson(
                "{" +
                    "\"query\":{" +
                        "\"clause\":{" +
                            "\"type\":\"withinTimeRange\"," +
                            "\"lowerLimit\":" + range.getFrom().getTime() + "," +
                            "\"upperLimit\":" + range.getTo().getTime() +
                        "}," +
                        "\"grouped\":true" +
                    "}" +
                "}").toString();
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder("dummy", range)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        GroupedHistoryStatesQuery.class,
                        new GroupedHistoryStatesQueryAdapter(null))
                .create();

        String actual = gson.toJson(query);

        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, JsonUtils.newJson(actual).toString());
    }

    @Test
    public void serializationWithClauseTest() {
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        String expected = JsonUtils.newJson(
                "{" +
                    "\"query\":{" +
                        "\"clause\":{" +
                            "\"type\":\"and\"," +
                            "\"clauses\":[" +
                                "{" +
                                    "\"type\":\"eq\"," +
                                    "\"field\":\"" + clause.getField() + "\"," +
                                    "\"value\":\"" + clause.getValue() + "\"" +
                                "}," +
                                "{" +
                                    "\"type\":\"withinTimeRange\"," +
                                    "\"lowerLimit\":" + range.getFrom().getTime() + "," +
                                    "\"upperLimit\":" + range.getTo().getTime() +
                                "}" +
                            "]" +
                        "}," +
                        "\"grouped\":true" +
                    "}" +
                "}").toString();
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder("dummy", range)
                .setClause(clause)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        GroupedHistoryStatesQuery.class,
                        new GroupedHistoryStatesQueryAdapter(null))
                .create();

        String actual = gson.toJson(query);

        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, JsonUtils.newJson(actual).toString());
    }

    @Test
    public void serializationWithFirmwareVersionTest() throws Exception {
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        String version = "v2";
        String expected = JsonUtils.newJson(
                "{" +
                    "\"query\":{" +
                        "\"clause\":{" +
                            "\"type\":\"withinTimeRange\"," +
                            "\"lowerLimit\":" + range.getFrom().getTime() + "," +
                            "\"upperLimit\":" + range.getTo().getTime() +
                        "}," +
                        "\"grouped\":true" +
                    "}," +
                    "firmwareVersion:" + version +
                "}").toString();
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder("dummy", range)
                .setFirmwareVersion(version)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        GroupedHistoryStatesQuery.class,
                        new GroupedHistoryStatesQueryAdapter(null))
                .create();

        String actual = gson.toJson(query);

        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, JsonUtils.newJson(actual).toString());
    }

    @Test
    public void serializationWithAggregationTest() {
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        Aggregation aggregation = Aggregation.newMaxAggregation("100", Aggregation.FieldType.INTEGER);
        String expected = JsonUtils.newJson(
                "{" +
                    "\"query\":{" +
                        "\"clause\":{" +
                            "\"type\":\"withinTimeRange\"," +
                            "\"lowerLimit\":" + range.getFrom().getTime() + "," +
                            "\"upperLimit\":" + range.getTo().getTime() +
                        "}," +
                        "\"grouped\":true," +
                        "\"aggregations\":[" +
                            "{" +
                                "\"type\":\"" + aggregation.getFunction().name() + "\"," +
                                "\"putAggregationInto\":\"" + aggregation.getFunction().name().toLowerCase() + "\"," +
                                "\"field\":\"" + aggregation.getField() + "\"," +
                                "\"fieldType\":\"" + aggregation.getFieldType().name() + "\"" +
                            "}" +
                        "]" +
                    "}" +
                "}").toString();
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder("dummy", range)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        GroupedHistoryStatesQuery.class,
                        new GroupedHistoryStatesQueryAdapter(aggregation))
                .create();

        String actual = gson.toJson(query);

        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, JsonUtils.newJson(actual).toString());
    }

    @Test
    public void serializationWithAllTest() {
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery clause = new EqualsClauseInQuery("dummy", "value");
        String version = "v2";
        Aggregation aggregation = Aggregation.newMaxAggregation("100", Aggregation.FieldType.INTEGER);
        String expected = JsonUtils.newJson(
                "{" +
                    "\"query\":{" +
                        "\"clause\":{" +
                            "\"type\":\"and\"," +
                            "\"clauses\":[" +
                                "{" +
                                    "\"type\":\"eq\"," +
                                    "\"field\":\"" + clause.getField() + "\"," +
                                    "\"value\":\"" + clause.getValue() + "\"" +
                                "}," +
                                "{" +
                                    "\"type\":\"withinTimeRange\"," +
                                    "\"lowerLimit\":" + range.getFrom().getTime() + "," +
                                    "\"upperLimit\":" + range.getTo().getTime() +
                                "}" +
                            "]" +
                        "}," +
                        "\"grouped\":true," +
                        "\"aggregations\":[" +
                            "{" +
                                "\"type\":\"" + aggregation.getFunction().name() + "\"," +
                                "\"putAggregationInto\":\"" + aggregation.getFunction().name().toLowerCase() + "\"," +
                                "\"field\":\"" + aggregation.getField() + "\"," +
                                "\"fieldType\":\"" + aggregation.getFieldType().name() + "\"" +
                            "}" +
                        "]" +
                    "}," +
                    "firmwareVersion:" + version +
                "}").toString();
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder("dummy", range)
                .setClause(clause)
                .setFirmwareVersion(version)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        GroupedHistoryStatesQuery.class,
                        new GroupedHistoryStatesQueryAdapter(aggregation))
                .create();

        String actual = gson.toJson(query);

        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, JsonUtils.newJson(actual).toString());
    }
}
