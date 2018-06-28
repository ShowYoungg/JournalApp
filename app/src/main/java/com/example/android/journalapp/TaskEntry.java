package com.example.android.journalapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

// COMPLETED (2) Annotate the class with Entity. Use "task" for the table name
@Entity(tableName = "task")
public class TaskEntry {

    // COMPLETED (3) Annotate the id as PrimaryKey. Set autoGenerate to true.
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String description;
    private String header;

    // COMPLETED (4) Use the Ignore annotation so Room knows that it has to use the other constructor instead
    @Ignore
    public TaskEntry(String header, String description) {
        this.description = description;
        this.header = header;
    }

    public TaskEntry(int id, String header, String description) {
        this.description = description;
        this.header = header;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {this.header = header;}


}