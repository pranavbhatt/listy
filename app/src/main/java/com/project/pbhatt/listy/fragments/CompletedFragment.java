package com.project.pbhatt.listy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.pbhatt.listy.R;
import com.project.pbhatt.listy.adapters.TodoItemsAdapter;
import com.project.pbhatt.listy.models.TodoItem;

import java.util.List;

/**
 * Created by pbhatt on 12/2/17.
 */

public class CompletedFragment extends TodoFragment {
    private static final String TAG = CompletedFragment.class.getName();

    protected List<TodoItem> getTodoItems(){
        return TodoItem.getCompletedTodoItemsList();
    }

    protected void setupView() {
        Log.d(TAG, " >>> setupViewCalled");
        rvTodoItemsList = mRootView.findViewById(R.id.rvTodoItemsList);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        rvTodoItemsList.addItemDecoration(itemDecoration);
        rvTodoItemsList.setAdapter(mTodoItemsAdapter);
        rvTodoItemsList.setLayoutManager(new LinearLayoutManager(mContext));
        FloatingActionButton myFab = mRootView.findViewById(R.id.fabAddTaskButton);
        myFab.setVisibility(View.INVISIBLE);
        setUpItemTouchHelper();
    }
}
