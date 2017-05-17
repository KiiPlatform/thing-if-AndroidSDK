package com.kii.thingiftrait.query;

import com.kii.thingiftrait.query.Aggregation.FunctionType;
import com.kii.thingiftrait.query.Aggregation.FieldType;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class AggregationTest {

    private static class TestData {
        String tag;
        boolean succeed;
        FunctionType functionType;
        String field;
        FieldType fieldType;

        TestData(
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

    private void executeNewAggregationTestCase(TestData data) {
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
        List<TestData> cases = new ArrayList<>();
        cases.add(new TestData("MAX-BOOLEAN", false, FunctionType.MAX, "dummy", FieldType.BOOLEAN));
        cases.add(new TestData("MAX-INTEGER", true, FunctionType.MAX, "dummy", FieldType.INTEGER));
        cases.add(new TestData("MAX-DECIMAL", true, FunctionType.MAX, "dummy", FieldType.DECIMAL));
        cases.add(new TestData("MAX-OBJECT", false, FunctionType.MAX, "dummy", FieldType.OBJECT));
        cases.add(new TestData("MAX-ARRAY", false, FunctionType.MAX, "dummy", FieldType.ARRAY));
        cases.add(new TestData("MIN-BOOLEAN", false, FunctionType.MIN, "dummy", FieldType.BOOLEAN));
        cases.add(new TestData("MIN-INTEGER", true, FunctionType.MIN, "dummy", FieldType.INTEGER));
        cases.add(new TestData("MIN-DECIMAL", true, FunctionType.MIN, "dummy", FieldType.DECIMAL));
        cases.add(new TestData("MIN-OBJECT", false, FunctionType.MIN, "dummy", FieldType.OBJECT));
        cases.add(new TestData("MIN-ARRAY", false, FunctionType.MIN, "dummy", FieldType.ARRAY));
        cases.add(new TestData("MEAN-BOOLEAN", false, FunctionType.MEAN, "dummy", FieldType.BOOLEAN));
        cases.add(new TestData("MEAN-INTEGER", true, FunctionType.MEAN, "dummy", FieldType.INTEGER));
        cases.add(new TestData("MEAN-DECIMAL", true, FunctionType.MEAN, "dummy", FieldType.DECIMAL));
        cases.add(new TestData("MEAN-OBJECT", false, FunctionType.MEAN, "dummy", FieldType.OBJECT));
        cases.add(new TestData("MEAN-ARRAY", false, FunctionType.MEAN, "dummy", FieldType.ARRAY));
        cases.add(new TestData("SUM-BOOLEAN", false, FunctionType.SUM, "dummy", FieldType.BOOLEAN));
        cases.add(new TestData("SUM-INTEGER", true, FunctionType.SUM, "dummy", FieldType.INTEGER));
        cases.add(new TestData("SUM-DECIMAL", true, FunctionType.SUM, "dummy", FieldType.DECIMAL));
        cases.add(new TestData("SUM-OBJECT", false, FunctionType.SUM, "dummy", FieldType.OBJECT));
        cases.add(new TestData("SUM-ARRAY", false, FunctionType.SUM, "dummy", FieldType.ARRAY));
        cases.add(new TestData("COUNT-BOOLEAN", true, FunctionType.COUNT, "dummy", FieldType.BOOLEAN));
        cases.add(new TestData("COUNT-INTEGER", true, FunctionType.COUNT, "dummy", FieldType.INTEGER));
        cases.add(new TestData("COUNT-DECIMAL", true, FunctionType.COUNT, "dummy", FieldType.DECIMAL));
        cases.add(new TestData("COUNT-OBJECT", true, FunctionType.COUNT, "dummy", FieldType.OBJECT));
        cases.add(new TestData("COUNT-ARRAY", true, FunctionType.COUNT, "dummy", FieldType.ARRAY));

        for (TestData data : cases) {
            executeNewAggregationTestCase(data);
        }
    }

    private void executeNewMaxAggregationTestCase(TestData data) {
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
        List<TestData> cases = new ArrayList<>();
        cases.add(new TestData("BOOLEAN", false, FunctionType.MAX, "dummy", FieldType.BOOLEAN));
        cases.add(new TestData("INTEGER", true, FunctionType.MAX, "dummy", FieldType.INTEGER));
        cases.add(new TestData("DECIMAL", true, FunctionType.MAX, "dummy", FieldType.DECIMAL));
        cases.add(new TestData("OBJECT", false, FunctionType.COUNT, "dummy", FieldType.OBJECT));
        cases.add(new TestData("ARRAY", false, FunctionType.COUNT, "dummy", FieldType.ARRAY));

        for (TestData data : cases) {
            executeNewMaxAggregationTestCase(data);
        }
    }

    private void executeNewMinAggregationTestCase(TestData data) {
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
        List<TestData> cases = new ArrayList<>();
        cases.add(new TestData("BOOLEAN", false, FunctionType.MIN, "dummy", FieldType.BOOLEAN));
        cases.add(new TestData("INTEGER", true, FunctionType.MIN, "dummy", FieldType.INTEGER));
        cases.add(new TestData("DECIMAL", true, FunctionType.MIN, "dummy", FieldType.DECIMAL));
        cases.add(new TestData("OBJECT", false, FunctionType.COUNT, "dummy", FieldType.OBJECT));
        cases.add(new TestData("ARRAY", false, FunctionType.COUNT, "dummy", FieldType.ARRAY));

        for (TestData data : cases) {
            executeNewMinAggregationTestCase(data);
        }
    }

    private void executeNewMeanAggregationTestCase(TestData data) {
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
        List<TestData> cases = new ArrayList<>();
        cases.add(new TestData("BOOLEAN", false, FunctionType.MEAN, "dummy", FieldType.BOOLEAN));
        cases.add(new TestData("INTEGER", true, FunctionType.MEAN, "dummy", FieldType.INTEGER));
        cases.add(new TestData("DECIMAL", true, FunctionType.MEAN, "dummy", FieldType.DECIMAL));
        cases.add(new TestData("OBJECT", false, FunctionType.COUNT, "dummy", FieldType.OBJECT));
        cases.add(new TestData("ARRAY", false, FunctionType.COUNT, "dummy", FieldType.ARRAY));

        for (TestData data : cases) {
            executeNewMeanAggregationTestCase(data);
        }
    }

    private void executeNewSumAggregationTestCase(TestData data) {
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
        List<TestData> cases = new ArrayList<>();
        cases.add(new TestData("BOOLEAN", false, FunctionType.SUM, "dummy", FieldType.BOOLEAN));
        cases.add(new TestData("INTEGER", true, FunctionType.SUM, "dummy", FieldType.INTEGER));
        cases.add(new TestData("DECIMAL", true, FunctionType.SUM, "dummy", FieldType.DECIMAL));
        cases.add(new TestData("OBJECT", false, FunctionType.COUNT, "dummy", FieldType.OBJECT));
        cases.add(new TestData("ARRAY", false, FunctionType.COUNT, "dummy", FieldType.ARRAY));

        for (TestData data : cases) {
            executeNewSumAggregationTestCase(data);
        }
    }

    private void executeNewCountAggregationTestCase(TestData data) {
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
        List<TestData> cases = new ArrayList<>();
        cases.add(new TestData("BOOLEAN", true, FunctionType.COUNT, "dummy", FieldType.BOOLEAN));
        cases.add(new TestData("INTEGER", true, FunctionType.COUNT, "dummy", FieldType.INTEGER));
        cases.add(new TestData("DECIMAL", true, FunctionType.COUNT, "dummy", FieldType.DECIMAL));
        cases.add(new TestData("OBJECT", true, FunctionType.COUNT, "dummy", FieldType.OBJECT));
        cases.add(new TestData("ARRAY", true, FunctionType.COUNT, "dummy", FieldType.ARRAY));

        for (TestData data : cases) {
            executeNewCountAggregationTestCase(data);
        }
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
}
