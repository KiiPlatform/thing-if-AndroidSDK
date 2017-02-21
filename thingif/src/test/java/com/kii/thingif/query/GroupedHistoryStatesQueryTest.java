package com.kii.thingif.query;

import android.os.Parcel;

import com.kii.thingif.clause.query.EqualsClauseInQuery;
import com.kii.thingif.clause.query.NotEqualsClauseInQuery;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

@RunWith(RobolectricTestRunner.class)
public class GroupedHistoryStatesQueryTest {

    @Test
    public void equals_hashCode_AliasTest() {
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        GroupedHistoryStatesQuery target = GroupedHistoryStatesQuery.Builder
                .newBuilder("alias", range).build();
        GroupedHistoryStatesQuery sameOne = GroupedHistoryStatesQuery.Builder
                .newBuilder("alias", range).build();
        GroupedHistoryStatesQuery differentOne = GroupedHistoryStatesQuery.Builder
                .newBuilder("different", range).build();

        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(differentOne));
        Assert.assertNotSame(target.hashCode(), differentOne.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals((Object)range));
    }

    @Test
    public void equals_hashCode_ClauseKeyTest() {
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery query = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery target = GroupedHistoryStatesQuery.Builder
                .newBuilder("alias", range)
                .setClause(query)
                .build();
        GroupedHistoryStatesQuery sameOne = GroupedHistoryStatesQuery.Builder
                .newBuilder("alias", range)
                .setClause(query)
                .build();
        GroupedHistoryStatesQuery differentOne = GroupedHistoryStatesQuery.Builder
                .newBuilder("alias", range)
                .setClause(new NotEqualsClauseInQuery(query))
                .build();
        GroupedHistoryStatesQuery differentNull = GroupedHistoryStatesQuery.Builder
                .newBuilder("alias", range)
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
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery query = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery target = GroupedHistoryStatesQuery.Builder
                .newBuilder("alias", range)
                .setClause(query)
                .setFirmwareVersion("version")
                .build();
        GroupedHistoryStatesQuery sameOne = GroupedHistoryStatesQuery.Builder
                .newBuilder("alias", range)
                .setClause(query)
                .setFirmwareVersion("version")
                .build();
        GroupedHistoryStatesQuery differentOne = GroupedHistoryStatesQuery.Builder
                .newBuilder("alias", range)
                .setClause(query)
                .setFirmwareVersion("different")
                .build();
        GroupedHistoryStatesQuery differentNull = GroupedHistoryStatesQuery.Builder
                .newBuilder("alias", range)
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
    public void equals_hashCode_TimeRangeTest() {
        TimeRange range1 = new TimeRange(new Date(1), new Date(100));
        TimeRange range2 = new TimeRange(new Date(200), new Date(2000));
        GroupedHistoryStatesQuery target = GroupedHistoryStatesQuery.Builder
                .newBuilder("alias", range1)
                .build();
        GroupedHistoryStatesQuery sameOne = GroupedHistoryStatesQuery.Builder
                .newBuilder("alias", range1)
                .build();
        GroupedHistoryStatesQuery differentOne = GroupedHistoryStatesQuery.Builder
                .newBuilder("alias", range2)
                .build();

        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(differentOne));
        Assert.assertNotSame(target.hashCode(), differentOne.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals((Object)range2));
    }

    @Test
    public void parcelableTest() {
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        EqualsClauseInQuery query = new EqualsClauseInQuery("dummy", "value");
        GroupedHistoryStatesQuery src = GroupedHistoryStatesQuery.Builder
                .newBuilder("alias", range)
                .setClause(query)
                .setFirmwareVersion("version")
                .build();

        Assert.assertNotNull(src);
        Assert.assertEquals("alias", src.getAlias());
        Assert.assertEquals(query, src.getClause());
        Assert.assertEquals("version", src.getFirmwareVersion());
        Assert.assertEquals(range, src.getTimeRange());

        Parcel parcel = Parcel.obtain();
        src.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        GroupedHistoryStatesQuery dest = GroupedHistoryStatesQuery.CREATOR.createFromParcel(parcel);

        Assert.assertNotNull(dest);
        Assert.assertTrue(src.equals(dest));
    }

    @Test
    public void parcelableNullableTest() {
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        GroupedHistoryStatesQuery src = GroupedHistoryStatesQuery.Builder
                .newBuilder("alias", range)
                .build();

        Assert.assertNotNull(src);
        Assert.assertEquals("alias", src.getAlias());
        Assert.assertNull(src.getClause());
        Assert.assertNull(src.getFirmwareVersion());
        Assert.assertEquals(range, src.getTimeRange());

        Parcel parcel = Parcel.obtain();
        src.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        GroupedHistoryStatesQuery dest = GroupedHistoryStatesQuery.CREATOR.createFromParcel(parcel);

        Assert.assertNotNull(dest);
        Assert.assertTrue(src.equals(dest));
    }
}
