package com.project.pbhatt.listy.models;

import com.project.pbhatt.listy.database.TodoItemDatabase;
import com.project.pbhatt.listy.utils.DateUtil;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

/**
 * Created by pbhatt on 12/2/17.
 */
@Table(database = TodoItemDatabase.class)
@Parcel(analyze = {TodoItem.class})
public class TodoItem extends BaseModel {
    public enum Priority{
        NONE(0), LOW(1), MEDIUM(2), HIGH(3);

        private int value;

        private Priority(int value){
            this.value = value;
        }

        public int getValue(){
            return this.value;
        }
    }

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
     String description;

    @Column
     Integer priority;

    @Column
     Boolean complete;

    @Column
     Date dueDate;

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setPriority(String priority) {
        this.priority = Priority.valueOf(priority).getValue();
    }

    public Priority getPriority() {
        return Priority.values()[this.priority];
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void setDueDate(Date date) {
        this.dueDate = date;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public String getFormattedDueDate() {
        if(this.dueDate == null){
            return "";
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.dueDate);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        return DateUtil.getFormattedDate(year, month, day);
    }

    public static List<TodoItem> getIncompleteTodoItemsList() {
        return SQLite.select().
                from(TodoItem.class).
                where(TodoItem_Table.complete.eq(false)).
                queryList();
    }

    public static List<TodoItem> getCompletedTodoItemsList() {
        return SQLite.select().
                from(TodoItem.class).
                where(TodoItem_Table.complete.eq(true)).
                queryList();
    }


    public int comparePriorityTo(TodoItem other){
        final int priorityVal = this.getPriority().getValue();
        final int otherPriorityVal = other.getPriority().getValue();
        if(priorityVal > otherPriorityVal){
            return -1;
        }else if(priorityVal < otherPriorityVal){
            return 1;
        }
        return 0;
    }

    public int compareDueDateTo(TodoItem other){
        final Date dueDate = this.getDueDate();
        final Date otherDueDate = other.getDueDate();
        if(dueDate  == null){
            return 1;
        }
        if(otherDueDate == null){
            return -1;
        }
        return dueDate.compareTo(otherDueDate);
    }
}