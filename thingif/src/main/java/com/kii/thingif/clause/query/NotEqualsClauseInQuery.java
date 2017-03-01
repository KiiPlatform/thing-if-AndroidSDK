package com.kii.thingif.clause.query;

import com.kii.thingif.clause.base.BaseNotEquals;

public class NotEqualsClauseInQuery implements BaseNotEquals, QueryClause {

    private EqualsClauseInQuery equals;

    private transient volatile int hashCode; // cached hashcode for performance

    public NotEqualsClauseInQuery(EqualsClauseInQuery equals) {
        this.equals = equals;
    }

    @Override
    public EqualsClauseInQuery getEquals() {
        return this.equals;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof NotEqualsClauseInQuery)) return false;
        NotEqualsClauseInQuery notEquals = (NotEqualsClauseInQuery) o;
        return this.equals.equals(notEquals.equals);
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if(result == 0) {
            result = 17;
            result = 31 * result + this.equals.hashCode();
            this.hashCode = result;
        }
        return result;
    }
}
