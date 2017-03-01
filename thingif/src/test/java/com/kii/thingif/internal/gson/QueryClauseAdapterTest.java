package com.kii.thingif.internal.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kii.thingif.SmallTestBase;
import com.kii.thingif.clause.query.AndClauseInQuery;
import com.kii.thingif.clause.query.EqualsClauseInQuery;
import com.kii.thingif.clause.query.NotEqualsClauseInQuery;
import com.kii.thingif.clause.query.OrClauseInQuery;
import com.kii.thingif.clause.query.QueryClause;
import com.kii.thingif.clause.query.RangeClauseInQuery;
import com.kii.thingif.utils.JsonUtil;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class QueryClauseAdapterTest extends SmallTestBase{
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(QueryClause.class, new QueryClauseAdapter())
            .create();

    @Test
    public void serializationTest() throws Exception{
        QueryClause[] clauses = {
                new EqualsClauseInQuery("power", true),
                new NotEqualsClauseInQuery(new EqualsClauseInQuery("temperature", 23)),
                RangeClauseInQuery.range("humidity", 45, true, 23, true),
                new AndClauseInQuery()
                        .addClause(new EqualsClauseInQuery("power", true))
                        .addClause(RangeClauseInQuery.lessThan("humidity", 34)),
                new OrClauseInQuery().addClause(new EqualsClauseInQuery("humidity", 45))
                        .addClause(RangeClauseInQuery.greaterThan("temperature", 23))
        };
        for (int i=0; i<clauses.length; i++) {
            QueryClause clause = clauses[i];
            String jsonString = gson.toJson(clause, QueryClause.class);
            JSONObject deserializedJson = new JSONObject(jsonString);
            assertJSONObject(
                    "failed on ["+i+"]",
                    JsonUtil.queryClauseToJson(clause),
                    deserializedJson);
        }
    }
}
