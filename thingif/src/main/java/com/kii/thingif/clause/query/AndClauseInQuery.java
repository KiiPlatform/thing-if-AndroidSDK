package com.kii.thingif.clause.query;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.kii.thingif.clause.base.BaseAnd;

import java.util.ArrayList;
import java.util.List;

public class AndClauseInQuery implements BaseAnd<QueryClause>, QueryClause {
    private List<QueryClause> clauses = new ArrayList<>();

    public AndClauseInQuery(@NonNull QueryClause ...clauses){
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
    public AndClauseInQuery addClause(@NonNull QueryClause clause) {
        this.clauses.add(clause);
        return this;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.clauses.size());
        for (QueryClause clause: this.clauses){
            dest.writeParcelable(clause, flags);
        }
    }

    private AndClauseInQuery(Parcel in) {
        this.clauses = new ArrayList<>();
        int size = in.readInt();
        for (int i= 0; i<size; i++) {
            QueryClause clause = in.readParcelable(QueryClause.class.getClassLoader());
            this.clauses.add(clause);
        }
    }

    public static final Creator<AndClauseInQuery> CREATOR = new Creator<AndClauseInQuery>() {
        @Override
        public AndClauseInQuery createFromParcel(Parcel source) {
            return new AndClauseInQuery(source);
        }

        @Override
        public AndClauseInQuery[] newArray(int size) {
            return new AndClauseInQuery[size];
        }
    };
}
