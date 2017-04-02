package models;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by shrralis on 3/15/17.
 */
public class Fish extends Owner {
    public String type = null;
    public String row = null;
    public String area = null;
    public String description = null;
    public Double max_weight = null;
    public String other = null;

    public Fish() {}
    @SuppressWarnings("unused")
    public Fish(ResultSet from) {
        parse(from);
    }
    @Override
    public Fish parse(ResultSet from) {
        super.parse(from);

        try {
            type = from.getString("type");
            row = from.getString("row");
            area = from.getString("area");
            description = from.getString("description");
            max_weight = from.getDouble("max_weight");
            other = from.getString("other");
        } catch (SQLException ignored) {}
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMax_weight() {
        return max_weight;
    }

    public void setMax_weight(Double max_weight) {
        this.max_weight = max_weight;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
