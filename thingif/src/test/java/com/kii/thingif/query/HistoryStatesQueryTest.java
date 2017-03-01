package com.kii.thingif.query;

import com.kii.thingif.clause.query.EqualsClauseInQuery;
import com.kii.thingif.clause.query.NotEqualsClauseInQuery;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class HistoryStatesQueryTest {

    @Test
    public void baseTest() {
        EqualsClauseInQuery query = new EqualsClauseInQuery("dummy", "value");
        HistoryStatesQuery target = HistoryStatesQuery.Builder.newBuilder("alias", query)
                .build();
        HistoryStatesQuery sameOne = HistoryStatesQuery.Builder.newBuilder("alias", query)
                .build();
        HistoryStatesQuery differentOne = HistoryStatesQuery.Builder
                .newBuilder(
                        "alias",
                        new NotEqualsClauseInQuery(query))
                .build();

        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(differentOne));
        Assert.assertNotSame(target.hashCode(), differentOne.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals((Object)query));
    }

    @Test
    public void equals_hashCode_FirmwareVersionTest() {
        EqualsClauseInQuery query = new EqualsClauseInQuery("dummy", "value");
        HistoryStatesQuery target = HistoryStatesQuery.Builder.newBuilder("alias", query)
                .setFirmwareVersion("version")
                .build();
        HistoryStatesQuery sameOne = HistoryStatesQuery.Builder.newBuilder("alias", query)
                .setFirmwareVersion("version")
                .build();
        HistoryStatesQuery differentOne = HistoryStatesQuery.Builder.newBuilder("alias", query)
                .setFirmwareVersion("different")
                .build();
        HistoryStatesQuery differentNull = HistoryStatesQuery.Builder.newBuilder("alias", query)
                .build();

        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(differentOne));
        Assert.assertNotSame(target.hashCode(), differentOne.hashCode());
        Assert.assertFalse(target.equals(differentNull));
        Assert.assertNotSame(target.hashCode(), differentNull.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals((Object)query));
    }

    @Test
    public void equals_hashCode_BestEffortLimitTest() {
        EqualsClauseInQuery query = new EqualsClauseInQuery("dummy", "value");
        HistoryStatesQuery target = HistoryStatesQuery.Builder.newBuilder("alias", query)
                .setFirmwareVersion("version")
                .setBestEffortLimit(10)
                .build();
        HistoryStatesQuery sameOne = HistoryStatesQuery.Builder.newBuilder("alias", query)
                .setFirmwareVersion("version")
                .setBestEffortLimit(10)
                .build();
        HistoryStatesQuery differentOne = HistoryStatesQuery.Builder.newBuilder("alias", query)
                .setFirmwareVersion("version")
                .setBestEffortLimit(200)
                .build();
        HistoryStatesQuery differentNull = HistoryStatesQuery.Builder.newBuilder("alias", query)
                .setFirmwareVersion("version")
                .build();

        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(differentOne));
        Assert.assertNotSame(target.hashCode(), differentOne.hashCode());
        Assert.assertFalse(target.equals(differentNull));
        Assert.assertNotSame(target.hashCode(), differentNull.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals((Object)query));
    }

    @Test
    public void equals_hashCode_NextPaginationKeyTest() {
        EqualsClauseInQuery query = new EqualsClauseInQuery("dummy", "value");
        HistoryStatesQuery target = HistoryStatesQuery.Builder.newBuilder("alias", query)
                .setFirmwareVersion("version")
                .setBestEffortLimit(10)
                .setNextPaginationKey("key")
                .build();
        HistoryStatesQuery sameOne = HistoryStatesQuery.Builder.newBuilder("alias", query)
                .setFirmwareVersion("version")
                .setBestEffortLimit(10)
                .setNextPaginationKey("key")
                .build();
        HistoryStatesQuery differentOne = HistoryStatesQuery.Builder.newBuilder("alias", query)
                .setFirmwareVersion("version")
                .setBestEffortLimit(10)
                .setNextPaginationKey("different")
                .build();
        HistoryStatesQuery differentNull = HistoryStatesQuery.Builder.newBuilder("alias", query)
                .setFirmwareVersion("version")
                .setBestEffortLimit(10)
                .build();

        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(differentOne));
        Assert.assertNotSame(target.hashCode(), differentOne.hashCode());
        Assert.assertFalse(target.equals(differentNull));
        Assert.assertNotSame(target.hashCode(), differentNull.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals((Object)query));
    }
}
