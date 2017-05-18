package com.kii.thing_if.clause.query;

import android.os.Parcel;

import com.kii.thing_if.clause.base.BaseOr;

import java.util.ArrayList;
import java.util.List;

public class OrClauseInQuery implements BaseOr<QueryClause>, QueryClause {
    private List<QueryClause> clauses = new ArrayList<>();

    public OrClauseInQuery(QueryClause ...clauses){
        if(clauses != null) {
            for(QueryClause clause : clauses) {
                this.clauses.add(clause);
            }
        }
    }

    @Override
    public List<QueryClause> getClauses() {
        return this.clauses;
    }

    @Override
    public OrClauseInQuery addClause(QueryClause clause) {
        this.clauses.add(clause);
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.clauses.size());
        for (QueryClause clause: this.clauses){
            dest.writeParcelable(clause, flags);
        }
    }

    private OrClauseInQuery(Parcel in) {
        this.clauses = new ArrayList<>();
        int size = in.readInt();
        for (int i= 0; i<size; i++) {
            QueryClause clause = in.readParcelable(QueryClause.class.getClassLoader());
            this.clauses.add(clause);
        }
    }

    public static final Creator<OrClauseInQuery> CREATOR = new Creator<OrClauseInQuery>() {
        @Override
        public OrClauseInQuery createFromParcel(Parcel source) {
            return new OrClauseInQuery(source);
        }

        @Override
        public OrClauseInQuery[] newArray(int size) {
            return new OrClauseInQuery[size];
        }
    };
}
