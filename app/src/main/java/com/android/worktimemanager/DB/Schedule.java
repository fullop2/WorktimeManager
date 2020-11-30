package com.android.worktimemanager.DB;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Schedule implements Serializable, Comparable<Schedule>, DAO {
    private String date;
    private double time;
    private String comment;

    public Schedule(String date, double time, String comment)
    {
        this.setDate(date);
        this.setTime(time);
        this.setComment(comment);
    }


    public String toString()
    {
        return LocalDate.parse(date).format(DateTimeFormatter.ofPattern("MM/dd")) + " " + getTime() + " " + getComment();
    }
    public String toSql()
    {
        String d = LocalDate.parse(date).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return "'"+d + "', " + (int)(getTime()*1000) + ", '" + getComment()+"'";
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(double time) {
        this.time = time;
    }
    public double getTime(){return time;}
    public String getComment() {
        return comment;
    }

    public void setComment(String type) {
        this.comment = type;
    }

    @Override
    public int compareTo(Schedule schedule) {
        return (LocalDate.parse(schedule.date).isAfter(LocalDate.parse(date)) ? 1 : -1);
    }
}
