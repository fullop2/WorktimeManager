package com.android.worktimemanager.DB;

public class Holiday implements DAO
{
    String date;
    String comment;

    public Holiday(String date, String name)
    {
        this.date = date;
        this.comment = name;
    }

    public String getDate() {return date;}
    public String getComment(){return comment;}
    public String toString()
    {
        return date + " " + comment;
    }
    public String toSql() { return "'"+date + "', '" + comment +"'";}
}