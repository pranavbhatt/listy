package com.project.pbhatt.listy.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by pbhatt on 12/2/17.
 */
@Database(name = TodoItemDatabase.NAME, version = TodoItemDatabase.VERSION)
public class TodoItemDatabase {
    public static final String NAME = "TodoItemDatabase";
    public static final int VERSION = 1;
}