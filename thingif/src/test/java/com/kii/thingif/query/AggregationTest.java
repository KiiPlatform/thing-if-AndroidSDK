package com.kii.thingif.query;

import android.os.Parcel;

import com.kii.thingif.query.Aggregation.FunctionType;
import com.kii.thingif.query.Aggregation.FieldType;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class AggregationTest {

    private class NewAggregationTestData {
        public String tag;
        public boolean succeed;
        public FunctionType functionType;
        public String field;
        public FieldType fieldType;

        public NewAggregationTestData(
                String tag,
                boolean succeed,
                FunctionType functionType,
                String field,
                FieldType fieldType) {
            this.tag = tag;
            this.succeed = succeed;
            this.functionType = functionType;
            this.field = field;
            this.fieldType = fieldType;
        }
    }

    private void newAggregationTestCase(NewAggregationTestData data) {
        if (data.succeed) {
            try {
                Aggregation a = Aggregation.newAggregation(data.functionType, data.field, data.fieldType);
                Assert.assertEquals(data.tag + ": functionType not equals.",
                        data.functionType, a.getFunction());
                Assert.assertEquals(data.tag + ": field not equals.", data.field, a.getField());
                Assert.assertEquals(data.tag + ": fieldType not equals.", data.fieldType, a.getFieldType());
            } catch (Exception e) {
                Assert.fail(data.tag + ": " + e.getMessage());
            }
        } else {
            try {
                Aggregation.newAggregation(data.functionType, data.field, data.fieldType);
                Assert.fail(data.tag + ": IllegalArgumentException must be thrown.");
            } catch (IllegalArgumentException e) {
                // expected.
            } catch (Exception e) {
                Assert.fail(data.tag + ": No other exception must be thrown.");
            }
        }
    }

    @Test
    public void newAggregationTest() {
        List<NewAggregationTestData> cases = new ArrayList<>();
        cases.add(new NewAggregationTestData("MAX-BOOLEAN", false, FunctionType.MAX, "dummy", FieldType.BOOLEAN));
        cases.add(new NewAggregationTestData("MAX-INTEGER", true, FunctionType.MAX, "dummy", FieldType.INTEGER));
        cases.add(new NewAggregationTestData("MAX-DECIMAL", true, FunctionType.MAX, "dummy", FieldType.DECIMAL));
        cases.add(new NewAggregationTestData("MIN-BOOLEAN", false, FunctionType.MIN, "dummy", FieldType.BOOLEAN));
        cases.add(new NewAggregationTestData("MIN-INTEGER", true, FunctionType.MIN, "dummy", FieldType.INTEGER));
        cases.add(new NewAggregationTestData("MIN-DECIMAL", true, FunctionType.MIN, "dummy", FieldType.DECIMAL));
        cases.add(new NewAggregationTestData("MEAN-BOOLEAN", false, FunctionType.MEAN, "dummy", FieldType.BOOLEAN));
        cases.add(new NewAggregationTestData("MEAN-INTEGER", true, FunctionType.MEAN, "dummy", FieldType.INTEGER));
        cases.add(new NewAggregationTestData("MEAN-DECIMAL", true, FunctionType.MEAN, "dummy", FieldType.DECIMAL));
        cases.add(new NewAggregationTestData("SUM-BOOLEAN", false, FunctionType.SUM, "dummy", FieldType.BOOLEAN));
        cases.add(new NewAggregationTestData("SUM-INTEGER", true, FunctionType.SUM, "dummy", FieldType.INTEGER));
        cases.add(new NewAggregationTestData("SUM-DECIMAL", true, FunctionType.SUM, "dummy", FieldType.DECIMAL));
        cases.add(new NewAggregationTestData("COUNT-BOOLEAN", true, FunctionType.COUNT, "dummy", FieldType.BOOLEAN));
        cases.add(new NewAggregationTestData("COUNT-INTEGER", true, FunctionType.COUNT, "dummy", FieldType.INTEGER));
        cases.add(new NewAggregationTestData("COUNT-DECIMAL", true, FunctionType.COUNT, "dummy", FieldType.DECIMAL));

        for (NewAggregationTestData data : cases) {
            newAggregationTestCase(data);
        }
    }

    private void newMaxAggregationTestCase(NewAggregationTestData data) {
        if (data.succeed) {
            try {
                Aggregation a = Aggregation.newMaxAggregation(data.field, data.fieldType);
                Assert.assertEquals(data.tag + ": functionType not equals.",
                        data.functionType, a.getFunction());
                Assert.assertEquals(data.tag + ": field not equals.", data.field, a.getField());
                Assert.assertEquals(data.tag + ": fieldType not equals.", data.fieldType, a.getFieldType());
            } catch (Exception e) {
                Assert.fail(data.tag + ": " + e.getMessage());
            }
        } else {
            try {
                Aggregation.newMaxAggregation(data.field, data.fieldType);
                Assert.fail(data.tag + ": IllegalArgumentException must be thrown.");
            } catch (IllegalArgumentException e) {
                // expected.
            } catch (Exception e) {
                Assert.fail(data.tag + ": No other exception must be thrown.");
            }
        }
    }

    @Test
    public void newMaxAggregationTest() {
        List<NewAggregationTestData> cases = new ArrayList<>();
        cases.add(new NewAggregationTestData("BOOLEAN", false, FunctionType.MAX, "dummy", FieldType.BOOLEAN));
        cases.add(new NewAggregationTestData("INTEGER", true, FunctionType.MAX, "dummy", FieldType.INTEGER));
        cases.add(new NewAggregationTestData("DECIMAL", true, FunctionType.MAX, "dummy", FieldType.DECIMAL));

        for (NewAggregationTestData data : cases) {
            newMaxAggregationTestCase(data);
        }
    }

    private void newMinAggregationTestCase(NewAggregationTestData data) {
        if (data.succeed) {
            try {
                Aggregation a = Aggregation.newMinAggregation(data.field, data.fieldType);
                Assert.assertEquals(data.tag + ": functionType not equals.",
                        data.functionType, a.getFunction());
                Assert.assertEquals(data.tag + ": field not equals.", data.field, a.getField());
                Assert.assertEquals(data.tag + ": fieldType not equals.", data.fieldType, a.getFieldType());
            } catch (Exception e) {
                Assert.fail(data.tag + ": " + e.getMessage());
            }
        } else {
            try {
                Aggregation.newMinAggregation(data.field, data.fieldType);
                Assert.fail(data.tag + ": IllegalArgumentException must be thrown.");
            } catch (IllegalArgumentException e) {
                // expected.
            } catch (Exception e) {
                Assert.fail(data.tag + ": No other exception must be thrown.");
            }
        }
    }

    @Test
    public void newMinAggregationTest() {
        List<NewAggregationTestData> cases = new ArrayList<>();
        cases.add(new NewAggregationTestData("BOOLEAN", false, FunctionType.MIN, "dummy", FieldType.BOOLEAN));
        cases.add(new NewAggregationTestData("INTEGER", true, FunctionType.MIN, "dummy", FieldType.INTEGER));
        cases.add(new NewAggregationTestData("DECIMAL", true, FunctionType.MIN, "dummy", FieldType.DECIMAL));

        for (NewAggregationTestData data : cases) {
            newMinAggregationTestCase(data);
        }
    }

    private void newMeanAggregationTestCase(NewAggregationTestData data) {
        if (data.succeed) {
            try {
                Aggregation a = Aggregation.newMeanAggregation(data.field, data.fieldType);
                Assert.assertEquals(data.tag + ": functionType not equals.",
                        data.functionType, a.getFunction());
                Assert.assertEquals(data.tag + ": field not equals.", data.field, a.getField());
                Assert.assertEquals(data.tag + ": fieldType not equals.", data.fieldType, a.getFieldType());
            } catch (Exception e) {
                Assert.fail(data.tag + ": " + e.getMessage());
            }
        } else {
            try {
                Aggregation.newMeanAggregation(data.field, data.fieldType);
                Assert.fail(data.tag + ": IllegalArgumentException must be thrown.");
            } catch (IllegalArgumentException e) {
                // expected.
            } catch (Exception e) {
                Assert.fail(data.tag + ": No other exception must be thrown.");
            }
        }
    }

    @Test
    public void newMeanAggregationTest() {
        List<NewAggregationTestData> cases = new ArrayList<>();
        cases.add(new NewAggregationTestData("BOOLEAN", false, FunctionType.MEAN, "dummy", FieldType.BOOLEAN));
        cases.add(new NewAggregationTestData("INTEGER", true, FunctionType.MEAN, "dummy", FieldType.INTEGER));
        cases.add(new NewAggregationTestData("DECIMAL", true, FunctionType.MEAN, "dummy", FieldType.DECIMAL));

        for (NewAggregationTestData data : cases) {
            newMeanAggregationTestCase(data);
        }
    }

    private void newSumAggregationTestCase(NewAggregationTestData data) {
        if (data.succeed) {
            try {
                Aggregation a = Aggregation.newSumAggregation(data.field, data.fieldType);
                Assert.assertEquals(data.tag + ": functionType not equals.",
                        data.functionType, a.getFunction());
                Assert.assertEquals(data.tag + ": field not equals.", data.field, a.getField());
                Assert.assertEquals(data.tag + ": fieldType not equals.", data.fieldType, a.getFieldType());
            } catch (Exception e) {
                Assert.fail(data.tag + ": " + e.getMessage());
            }
        } else {
            try {
                Aggregation.newSumAggregation(data.field, data.fieldType);
                Assert.fail(data.tag + ": IllegalArgumentException must be thrown.");
            } catch (IllegalArgumentException e) {
                // expected.
            } catch (Exception e) {
                Assert.fail(data.tag + ": No other exception must be thrown.");
            }
        }
    }

    @Test
    public void newSumAggregationTest() {
        List<NewAggregationTestData> cases = new ArrayList<>();
        cases.add(new NewAggregationTestData("BOOLEAN", false, FunctionType.SUM, "dummy", FieldType.BOOLEAN));
        cases.add(new NewAggregationTestData("INTEGER", true, FunctionType.SUM, "dummy", FieldType.INTEGER));
        cases.add(new NewAggregationTestData("DECIMAL", true, FunctionType.SUM, "dummy", FieldType.DECIMAL));

        for (NewAggregationTestData data : cases) {
            newSumAggregationTestCase(data);
        }
    }

    private void newCountAggregationTestCase(NewAggregationTestData data) {
        if (data.succeed) {
            try {
                Aggregation a = Aggregation.newCountAggregation(data.field, data.fieldType);
                Assert.assertEquals(data.tag + ": functionType not equals.",
                        data.functionType, a.getFunction());
                Assert.assertEquals(data.tag + ": field not equals.", data.field, a.getField());
                Assert.assertEquals(data.tag + ": fieldType not equals.", data.fieldType, a.getFieldType());
            } catch (Exception e) {
                Assert.fail(data.tag + ": " + e.getMessage());
            }
        } else {
            try {
                Aggregation.newCountAggregation(data.field, data.fieldType);
                Assert.fail(data.tag + ": IllegalArgumentException must be thrown.");
            } catch (IllegalArgumentException e) {
                // expected.
            } catch (Exception e) {
                Assert.fail(data.tag + ": No other exception must be thrown.");
            }
        }
    }

    @Test
    public void newCountAggregationTest() {
        List<NewAggregationTestData> cases = new ArrayList<>();
        cases.add(new NewAggregationTestData("BOOLEAN", true, FunctionType.COUNT, "dummy", FieldType.BOOLEAN));
        cases.add(new NewAggregationTestData("INTEGER", true, FunctionType.COUNT, "dummy", FieldType.INTEGER));
        cases.add(new NewAggregationTestData("DECIMAL", true, FunctionType.COUNT, "dummy", FieldType.DECIMAL));

        for (NewAggregationTestData data : cases) {
            newCountAggregationTestCase(data);
        }
    }

    @Test
    public void toJSONObjectTest() {
        Aggregation target = Aggregation.newAggregation(FunctionType.COUNT, "dummy", FieldType.INTEGER);
        JSONObject actual = target.toJSONObject();
        JSONObject expect = new JSONObject();
        try {
            expect.put("type", FunctionType.COUNT);
            expect.put("responseField", FunctionType.COUNT.toString().toLowerCase());
            expect.put("field", "dummy");
            expect.put("fieldType", FieldType.INTEGER);
        } catch (JSONException e) {
            // no thrown.
        }

        Assert.assertNotNull(actual);
        Assert.assertEquals(expect.toString(), actual.toString());
    }

    @Test
    public void equals_hashCodeTest() {
        Aggregation target = Aggregation.newAggregation(FunctionType.MAX, "dummy",
                FieldType.DECIMAL);
        Aggregation sameOne = Aggregation.newAggregation(FunctionType.MAX, "dummy",
                FieldType.DECIMAL);
        Aggregation differentOne = Aggregation.newAggregation(FunctionType.MIN, "dummy",
                FieldType.INTEGER);

        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(differentOne));
        Assert.assertNotSame(target.hashCode(), differentOne.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals((Object)FunctionType.COUNT));
    }

    @Test
    public void parcelableTest() {
        Aggregation src = Aggregation.newAggregation(FunctionType.SUM, "dummy",
                FieldType.INTEGER);

        Parcel parcel = Parcel.obtain();
        src.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Aggregation dest = Aggregation.CREATOR.createFromParcel(parcel);

        Assert.assertNotNull(dest);
        Assert.assertTrue(src.equals(dest));
    }
}
