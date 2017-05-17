package com.kii.thingiftrait.query;

import com.kii.thingiftrait.clause.query.EqualsClauseInQuery;
import com.kii.thingiftrait.clause.query.NotEqualsClauseInQuery;

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

    @Test
    public void basicBuilderTest() {
        EqualsClauseInQuery clause = new EqualsClauseInQuery("power", true);

        // only has required ones
        HistoryStatesQuery query = HistoryStatesQuery.Builder.newBuilder("alias1", clause).build();
        Assert.assertEquals("alias1", query.getAlias());
        Assert.assertTrue(clause.equals(query.getClause()));

        // only has one field(firmwareVersion, bestEfforLimit or nextPaginationKey)
        HistoryStatesQuery query1 = HistoryStatesQuery.Builder.newBuilder("alias1", clause)
                .setFirmwareVersion("v1").build();
        Assert.assertEquals("alias1", query1.getAlias());
        Assert.assertTrue(clause.equals(query1.getClause()));
        Assert.assertEquals("v1", query1.getFirmwareVersion());

        HistoryStatesQuery query2 = HistoryStatesQuery.Builder.newBuilder("alias1", clause)
                .setBestEffortLimit(5).build();
        Assert.assertEquals("alias1", query2.getAlias());
        Assert.assertTrue(clause.equals(query2.getClause()));
        Assert.assertNotNull(query2.getBestEffortLimit());
        Assert.assertEquals(5, query2.getBestEffortLimit().intValue());

        HistoryStatesQuery query3 = HistoryStatesQuery.Builder.newBuilder("alias1", clause)
                .setNextPaginationKey("100/2").build();
        Assert.assertEquals("alias1", query3.getAlias());
        Assert.assertTrue(clause.equals(query3.getClause()));
        Assert.assertEquals("100/2", query3.getNextPaginationKey());

        // has 2 options fields
        HistoryStatesQuery query4 = HistoryStatesQuery.Builder.newBuilder("alias1", clause)
                .setFirmwareVersion("v1")
                .setBestEffortLimit(5).build();
        Assert.assertEquals("alias1", query4.getAlias());
        Assert.assertTrue(clause.equals(query4.getClause()));
        Assert.assertEquals("v1", query4.getFirmwareVersion());
        Assert.assertNotNull(query4.getBestEffortLimit());
        Assert.assertEquals(5, query4.getBestEffortLimit().intValue());

        HistoryStatesQuery query5 = HistoryStatesQuery.Builder.newBuilder("alias1", clause)
                .setBestEffortLimit(5)
                .setNextPaginationKey("100/2").build();
        Assert.assertEquals("alias1", query5.getAlias());
        Assert.assertTrue(clause.equals(query5.getClause()));
        Assert.assertNotNull(query5.getBestEffortLimit());
        Assert.assertEquals(5, query5.getBestEffortLimit().intValue());
        Assert.assertEquals("100/2", query5.getNextPaginationKey());

        HistoryStatesQuery query6 = HistoryStatesQuery.Builder.newBuilder("alias1", clause)
                .setNextPaginationKey("100/2")
                .setFirmwareVersion("v1").build();
        Assert.assertEquals("alias1", query6.getAlias());
        Assert.assertTrue(clause.equals(query6.getClause()));
        Assert.assertEquals("100/2", query6.getNextPaginationKey());
        Assert.assertEquals("v1", query6.getFirmwareVersion());

        // have 3 fields
        HistoryStatesQuery query7 = HistoryStatesQuery.Builder.newBuilder("alias1", clause)
                .setNextPaginationKey("100/2")
                .setBestEffortLimit(5)
                .setFirmwareVersion("v1").build();
        Assert.assertEquals("alias1", query7.getAlias());
        Assert.assertTrue(clause.equals(query7.getClause()));
        Assert.assertEquals("100/2", query7.getNextPaginationKey());
        Assert.assertEquals("v1", query7.getFirmwareVersion());
        Assert.assertNotNull(query7.getBestEffortLimit());
        Assert.assertEquals(5, query7.getBestEffortLimit().intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void builder_build_with_nullAlias_Test() {
        EqualsClauseInQuery clause = new EqualsClauseInQuery("power", true);
        HistoryStatesQuery.Builder.newBuilder(null, clause).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void builder_build_with_emptyAlias_Test() {
        EqualsClauseInQuery clause = new EqualsClauseInQuery("power", true);
        HistoryStatesQuery.Builder.newBuilder("", clause).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void builder_build_with_nullClause_Test() {
        HistoryStatesQuery.Builder.newBuilder("alias1", null).build();
    }
}
