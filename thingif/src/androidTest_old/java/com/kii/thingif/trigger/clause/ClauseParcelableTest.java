package com.kii.thingif.trigger.clause;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.SmallTestBase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ClauseParcelableTest extends SmallTestBase {
    @Test
    public void stringNotEqualsTest() throws Exception {
        NotEquals clause = new NotEquals(new Equals("f", "value"));
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        NotEquals deserializedClause = NotEquals.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void numberNotEqualsTest() throws Exception {
        NotEquals clause = new NotEquals(new Equals("f", 5));
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        NotEquals deserializedClause = NotEquals.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void booleanNotEqualsTest() throws Exception {
        NotEquals clause = new NotEquals(new Equals("f", false));
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        NotEquals deserializedClause = NotEquals.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void rangeTest() throws Exception {
        Range clause = Range.range("f", 1, true, 10, false);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Range deserializedClause = Range.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void greaterThanTest() throws Exception {
        Range clause = Range.greaterThan("f", 1);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Range deserializedClause = Range.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void greaterThanEquals() throws Exception {
        Range clause = Range.greaterThanEquals("f", 1);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Range deserializedClause = Range.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void lessThan() throws Exception {
        Range clause = Range.lessThan("f", 1);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Range deserializedClause = Range.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void lessThanEquals() throws Exception {
        Range clause = Range.lessThanEquals("f", 1);
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Range deserializedClause = Range.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void andTest() throws Exception {
        And clause = new And(new Equals("f1", "a"), new Equals("f2", 1));
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        And deserializedClause = And.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
    @Test
    public void orTest() throws Exception {
        Or clause = new Or(new Equals("f1", "a"), new Equals("f2", 1));
        Parcel parcel = Parcel.obtain();
        clause.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Or deserializedClause = Or.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(clause, deserializedClause);
    }
}
