package com.kii.thingif.query;

import android.os.Parcel;

import com.kii.thingif.clause.query.EqualsClauseInQuery;
import com.kii.thingif.clause.query.NotEqualsClauseInQuery;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class HistoryStatesQueryTest {

    @Test
    public void equals_hashCode_AliasTest() {
        HistoryStatesQuery target = HistoryStatesQuery.Builder.newBuilder("alias").build();
        HistoryStatesQuery sameOne = HistoryStatesQuery.Builder.newBuilder("alias").build();
        HistoryStatesQuery differentOne = HistoryStatesQuery.Builder.newBuilder("different").build();

        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(differentOne));
        Assert.assertNotSame(target.hashCode(), differentOne.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals((Object)new EqualsClauseInQuery("dummy", "value")));
    }

    @Test
    public void equals_hashCode_ClauseKeyTest() {
        EqualsClauseInQuery query = new EqualsClauseInQuery("dummy", "value");
        HistoryStatesQuery target = HistoryStatesQuery.Builder.newBuilder("alias")
                .setClause(query)
                .build();
        HistoryStatesQuery sameOne = HistoryStatesQuery.Builder.newBuilder("alias")
                .setClause(query)
                .build();
        HistoryStatesQuery differentOne = HistoryStatesQuery.Builder.newBuilder("alias")
                .setClause(new NotEqualsClauseInQuery(query))
                .build();
        HistoryStatesQuery differentNull = HistoryStatesQuery.Builder.newBuilder("alias")
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
    public void equals_hashCode_FirmwareVersionTest() {
        EqualsClauseInQuery query = new EqualsClauseInQuery("dummy", "value");
        HistoryStatesQuery target = HistoryStatesQuery.Builder.newBuilder("alias")
                .setClause(query)
                .setFirmwareVersion("version")
                .build();
        HistoryStatesQuery sameOne = HistoryStatesQuery.Builder.newBuilder("alias")
                .setClause(query)
                .setFirmwareVersion("version")
                .build();
        HistoryStatesQuery differentOne = HistoryStatesQuery.Builder.newBuilder("alias")
                .setClause(query)
                .setFirmwareVersion("different")
                .build();
        HistoryStatesQuery differentNull = HistoryStatesQuery.Builder.newBuilder("alias")
                .setClause(query)
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
        HistoryStatesQuery target = HistoryStatesQuery.Builder.newBuilder("alias")
                .setClause(query)
                .setFirmwareVersion("version")
                .setBestEffortLimit(10)
                .build();
        HistoryStatesQuery sameOne = HistoryStatesQuery.Builder.newBuilder("alias")
                .setClause(query)
                .setFirmwareVersion("version")
                .setBestEffortLimit(10)
                .build();
        HistoryStatesQuery differentOne = HistoryStatesQuery.Builder.newBuilder("alias")
                .setClause(query)
                .setFirmwareVersion("version")
                .setBestEffortLimit(200)
                .build();
        HistoryStatesQuery differentNull = HistoryStatesQuery.Builder.newBuilder("alias")
                .setClause(query)
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
        HistoryStatesQuery target = HistoryStatesQuery.Builder.newBuilder("alias")
                .setClause(query)
                .setFirmwareVersion("version")
                .setBestEffortLimit(10)
                .setNextPaginationKey("key")
                .build();
        HistoryStatesQuery sameOne = HistoryStatesQuery.Builder.newBuilder("alias")
                .setClause(query)
                .setFirmwareVersion("version")
                .setBestEffortLimit(10)
                .setNextPaginationKey("key")
                .build();
        HistoryStatesQuery differentOne = HistoryStatesQuery.Builder.newBuilder("alias")
                .setClause(query)
                .setFirmwareVersion("version")
                .setBestEffortLimit(10)
                .setNextPaginationKey("different")
                .build();
        HistoryStatesQuery differentNull = HistoryStatesQuery.Builder.newBuilder("alias")
                .setClause(query)
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
    public void parcelableTest() {
        EqualsClauseInQuery query = new EqualsClauseInQuery("dummy", "value");
        HistoryStatesQuery src = HistoryStatesQuery.Builder.newBuilder("alias")
                .setClause(query)
                .setFirmwareVersion("version")
                .setBestEffortLimit(10)
                .setNextPaginationKey("key")
                .build();

        Assert.assertNotNull(src);
        Assert.assertEquals("alias", src.getAlias());
        Assert.assertEquals(query, src.getClause());
        Assert.assertEquals("version", src.getFirmwareVersion());
        Assert.assertEquals(Integer.valueOf(10), src.getBestEffortLimit());
        Assert.assertEquals("key", src.getNextPaginationKey());

        Parcel parcel = Parcel.obtain();
        src.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        HistoryStatesQuery dest = HistoryStatesQuery.CREATOR.createFromParcel(parcel);

        Assert.assertNotNull(dest);
        Assert.assertTrue(src.equals(dest));
    }

    @Test
    public void parcelableNullableTest() {
        HistoryStatesQuery src = HistoryStatesQuery.Builder.newBuilder("alias").build();

        Assert.assertNotNull(src);
        Assert.assertEquals("alias", src.getAlias());
        Assert.assertNull(src.getClause());
        Assert.assertNull(src.getFirmwareVersion());
        Assert.assertNull(src.getBestEffortLimit());
        Assert.assertNull(src.getNextPaginationKey());

        Parcel parcel = Parcel.obtain();
        src.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        HistoryStatesQuery dest = HistoryStatesQuery.CREATOR.createFromParcel(parcel);

        Assert.assertNotNull(dest);
        Assert.assertTrue(src.equals(dest));
    }
}
