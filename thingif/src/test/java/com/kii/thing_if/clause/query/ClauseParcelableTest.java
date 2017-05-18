package com.kii.thing_if.clause.query;

import android.os.Parcel;

import com.kii.thing_if.SmallTestBase;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
public class ClauseParcelableTest extends SmallTestBase{
    @Test
    public void stringEqualsTest() throws Exception {
        EqualsClauseInQuery clause = new EqualsClauseInQuery("f", "value");
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, clause.describeContents());
        parcel.setDataPosition(0);
        EqualsClauseInQuery deserializedClause = EqualsClauseInQuery.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }

    @Test
    public void numberEqualsTest() throws Exception {
        EqualsClauseInQuery clause = new EqualsClauseInQuery("f", 1);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        EqualsClauseInQuery deserializedClause = EqualsClauseInQuery.CREATOR.createFromParcel(parcel);
        Assert.assertNotNull(deserializedClause);
    }

    @Test
    public void booleanEqualsTest() throws Exception {
        EqualsClauseInQuery clause = new EqualsClauseInQuery("f", true);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        EqualsClauseInQuery deserializedClause = EqualsClauseInQuery.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void stringNotEqualsTest() throws Exception {
        NotEqualsClauseInQuery clause = new NotEqualsClauseInQuery(
                new EqualsClauseInQuery("f", "value"));
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        NotEqualsClauseInQuery deserializedClause = NotEqualsClauseInQuery.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void numberNotEqualsTest() throws Exception {
        NotEqualsClauseInQuery clause = new NotEqualsClauseInQuery(
                new EqualsClauseInQuery("f", 5));
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        NotEqualsClauseInQuery deserializedClause = NotEqualsClauseInQuery.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void booleanNotEqualsTest() throws Exception {
        NotEqualsClauseInQuery clause = new NotEqualsClauseInQuery(
                new EqualsClauseInQuery("f", false));
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        NotEqualsClauseInQuery deserializedClause = NotEqualsClauseInQuery.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void rangeTest() throws Exception {
        RangeClauseInQuery clause = RangeClauseInQuery.range("f", 1, true, 10, false);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        RangeClauseInQuery deserializedClause = RangeClauseInQuery.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void greaterThanTest() throws Exception {
        RangeClauseInQuery clause = RangeClauseInQuery.greaterThan("f", 1);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        RangeClauseInQuery deserializedClause = RangeClauseInQuery.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void greaterThanEqualsTest() throws Exception {
        RangeClauseInQuery clause = RangeClauseInQuery.greaterThanOrEqualTo("f", 1);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        RangeClauseInQuery deserializedClause = RangeClauseInQuery.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void lessThanTest() throws Exception {
        RangeClauseInQuery clause = RangeClauseInQuery.lessThan("f", 1);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        RangeClauseInQuery deserializedClause = RangeClauseInQuery.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void lessThanEqualsTest() throws Exception {
        RangeClauseInQuery clause = RangeClauseInQuery.lessThanOrEqualTo("f", 1);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        RangeClauseInQuery deserializedClause = RangeClauseInQuery.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }

    @Test
    public void andTest() throws Exception {
        AndClauseInQuery clause = new AndClauseInQuery(
                new EqualsClauseInQuery("f1", "a"),
                new NotEqualsClauseInQuery(
                        new EqualsClauseInQuery("f2", 1)),
                RangeClauseInQuery.greaterThan("f3", 23),
                RangeClauseInQuery.lessThan("f3", 230),
                RangeClauseInQuery.range("f4", 11, true, 23, false));
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        AndClauseInQuery deserializedClause = AndClauseInQuery.CREATOR.createFromParcel(parcel);
        Assert.assertTrue(Arrays.equals(
                clause.getClauses().toArray(),
                deserializedClause.getClauses().toArray()));
    }

    @Test
    public void orTest() throws Exception {
        OrClauseInQuery clause = new OrClauseInQuery(
                new EqualsClauseInQuery("f1", "a"),
                new NotEqualsClauseInQuery(
                        new EqualsClauseInQuery("f2", 1)),
                RangeClauseInQuery.greaterThan("f3", 23),
                RangeClauseInQuery.lessThan("f3", 230),
                RangeClauseInQuery.range("f4", 11, true, 23, false));
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        OrClauseInQuery deserializedClause = OrClauseInQuery.CREATOR.createFromParcel(parcel);
        Assert.assertTrue(Arrays.equals(
                clause.getClauses().toArray(),
                deserializedClause.getClauses().toArray()));
    }

    @Test
    public void allTest() throws Exception {
        AllClause clause = new AllClause();
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        AllClause deserializedClause = AllClause.CREATOR.createFromParcel(parcel);
        Assert.assertNotNull(deserializedClause);
    }
}
