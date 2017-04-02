package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

/**
 * Created by shrralis on 3/15/17.
 */
public class FishingCondition extends Owner {
    public String general_description = null;
    public Time time = null;
    public String place = null;
    public String tackle = null;
    public String other = null;

    public FishingCondition() {}
    @SuppressWarnings("unused")
    public FishingCondition(ResultSet from) {
        parse(from);
    }

    @Override
    public FishingCondition parse(ResultSet from) {
        super.parse(from);

        try {
            general_description = from.getString("general_description");
            time = DateWorker.convertToTime(from.getString("time"));
            place = from.getString("place");
            tackle = from.getString("tackle");
            other = from.getString("other");
        } catch (SQLException ignored) {}
        return this;
    }

    public String getGeneral_description() {
        return general_description;
    }

    public void setGeneral_description(String general_description) {
        this.general_description = general_description;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTackle() {
        return tackle;
    }

    public void setTackle(String tackle) {
        this.tackle = tackle;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
