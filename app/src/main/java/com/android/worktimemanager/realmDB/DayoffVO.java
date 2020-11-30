package com.android.worktimemanager.realmDB;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DayoffVO extends RealmObject {
    @PrimaryKey
    private String date;
    private int offTime;
    private String comment;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getOffTime() {
        return offTime;
    }

    public void setOffTime(int offTime) {
        this.offTime = offTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
