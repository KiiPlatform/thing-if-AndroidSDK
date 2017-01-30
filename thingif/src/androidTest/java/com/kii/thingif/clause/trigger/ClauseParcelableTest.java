package com.kii.thingif.clause.trigger;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.SmallTestBase;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ClauseParcelableTest extends SmallTestBase{
    @Test
    public void stringEqualsTest() throws Exception {
        EqualsClauseInTrigger clause = new EqualsClauseInTrigger("alias", "f", "value");
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, clause.describeContents());
        parcel.setDataPosition(0);
        EqualsClauseInTrigger deserializedClause = EqualsClauseInTrigger.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }

    @Test
    public void numberEqualsTest() throws Exception {
        EqualsClauseInTrigger clause = new EqualsClauseInTrigger("alias", "f", 1);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        EqualsClauseInTrigger deserializedClause = EqualsClauseInTrigger.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }

    @Test
    public void booleanEqualsTest() throws Exception {
        EqualsClauseInTrigger clause = new EqualsClauseInTrigger("alias", "f", true);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        EqualsClauseInTrigger deserializedClause = EqualsClauseInTrigger.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void stringNotEqualsTest() throws Exception {
        NotEqualsClauseInTrigger clause = new NotEqualsClauseInTrigger(
                new EqualsClauseInTrigger("alias", "f", "value"));
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        NotEqualsClauseInTrigger deserializedClause = NotEqualsClauseInTrigger.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void numberNotEqualsTest() throws Exception {
        NotEqualsClauseInTrigger clause = new NotEqualsClauseInTrigger(
                new EqualsClauseInTrigger("alias", "f", 5));
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        NotEqualsClauseInTrigger deserializedClause = NotEqualsClauseInTrigger.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void booleanNotEqualsTest() throws Exception {
        NotEqualsClauseInTrigger clause = new NotEqualsClauseInTrigger(
                new EqualsClauseInTrigger("alias", "f", false));
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        NotEqualsClauseInTrigger deserializedClause = NotEqualsClauseInTrigger.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void rangeTest() throws Exception {
        RangeClauseInTrigger clause = RangeClauseInTrigger.range("alias", "f", 1, true, 10, false);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        RangeClauseInTrigger deserializedClause = RangeClauseInTrigger.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void greaterThanTest() throws Exception {
        RangeClauseInTrigger clause = RangeClauseInTrigger.greaterThan("alias", "f", 1);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        RangeClauseInTrigger deserializedClause = RangeClauseInTrigger.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void greaterThanEquals() throws Exception {
        RangeClauseInTrigger clause = RangeClauseInTrigger.greaterThanOrEqualTo("alias", "f", 1);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        RangeClauseInTrigger deserializedClause = RangeClauseInTrigger.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void lessThan() throws Exception {
        RangeClauseInTrigger clause = RangeClauseInTrigger.lessThan("alias", "f", 1);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        RangeClauseInTrigger deserializedClause = RangeClauseInTrigger.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void lessThanEquals() throws Exception {
        RangeClauseInTrigger clause = RangeClauseInTrigger.lessThanOrEqualTo("alias", "f", 1);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        RangeClauseInTrigger deserializedClause = RangeClauseInTrigger.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
}
