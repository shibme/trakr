package me.shib.lib.trakr;

import java.util.ArrayList;
import java.util.List;

public final class TrakrQuery {

    private final List<TrakrQueryItem> queryItems;

    public TrakrQuery() {
        this.queryItems = new ArrayList<>();
    }

    public TrakrQuery(Condition condition, Operator operator, List<String> values) {
        this();
        add(condition, operator, values);
    }

    public TrakrQuery(Condition condition, Operator operator, String value) {
        this();
        add(condition, operator, value);
    }

    public TrakrQuery add(Condition condition, Operator operator, List<String> values) {
        queryItems.add(new TrakrQueryItem(condition, operator, values));
        return this;
    }

    public TrakrQuery add(Condition condition, Operator operator, String value) {
        List<String> values = new ArrayList<>();
        values.add(value);
        add(condition, operator, values);
        return this;
    }

    public List<TrakrQueryItem> getQueryItems() {
        return queryItems;
    }

    public enum Condition {
        project, status, label, type
    }

    public enum Operator {
        matching, not_matching
    }

    public class TrakrQueryItem {
        private final Condition condition;
        private final Operator operator;
        private final List<String> values;

        private TrakrQueryItem(Condition condition, Operator operator, List<String> values) {
            this.condition = condition;
            this.operator = operator;
            this.values = values;
        }

        public Condition getCondition() {
            return condition;
        }

        public Operator getOperator() {
            return operator;
        }

        public List<String> getValues() {
            return values;
        }
    }
}
