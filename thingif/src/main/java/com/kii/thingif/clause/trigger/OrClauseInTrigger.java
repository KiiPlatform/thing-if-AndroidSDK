package com.kii.thingif.clause.trigger;

import android.os.Parcel;

import com.kii.thingif.clause.base.BaseOr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrClauseInTrigger implements BaseOr<TriggerClause>, TriggerClause {
    private List<TriggerClause> clauses = new ArrayList<>();

    public OrClauseInTrigger(TriggerClause ...clauses){
        if(clauses != null) {
            for(TriggerClause clause : clauses) {
                this.clauses.add(clause);
            }
        }
    }

    @Override
    public List<TriggerClause> getClauses() {
        return new ArrayList<>(this.clauses);
    }

    @Override
    public OrClauseInTrigger addClause(TriggerClause clause) {
        this.clauses.add(clause);
        return this;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject ret = new JSONObject();
        JSONArray clauses = new JSONArray();
        try{
            ret.put("type", "or");
            for (TriggerClause clause : this.clauses) {
                clauses.put(clause.toJSONObject());
            }
            ret.put("clauses", clauses);
            return ret;
        }catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.clauses.size());
        for (TriggerClause clause: this.clauses){
            dest.writeParcelable(clause, flags);
        }
    }

    private OrClauseInTrigger(Parcel in) {
        this.clauses = new ArrayList<>();
        int size = in.readInt();
        for (int i= 0; i<size; i++) {
            TriggerClause clause = in.readParcelable(TriggerClause.class.getClassLoader());
            this.clauses.add(clause);
        }
    }

    public static final Creator<OrClauseInTrigger> CREATOR = new Creator<OrClauseInTrigger>() {
        @Override
        public OrClauseInTrigger createFromParcel(Parcel source) {
            return new OrClauseInTrigger(source);
        }

        @Override
        public OrClauseInTrigger[] newArray(int size) {
            return new OrClauseInTrigger[size];
        }
    };
}
