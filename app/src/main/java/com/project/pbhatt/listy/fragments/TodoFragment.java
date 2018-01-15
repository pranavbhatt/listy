package com.project.pbhatt.listy.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;


import java.util.Calendar;

import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.project.pbhatt.listy.R;
import com.project.pbhatt.listy.adapters.TodoItemsAdapter;
import com.project.pbhatt.listy.models.TodoItem;
import com.project.pbhatt.listy.utils.DateUtil;


import java.util.Collections;
import java.util.List;

/**
 * Created by pbhatt on 12/2/17.
 */
public class TodoFragment extends Fragment implements EditDialogFragment.EditTodoItemListener {
    protected static final int PERMISSIONS_REQUEST_READ_WRITE_CALENDER = 1;
    private static final String TAG = TodoFragment.class.getName();
    protected List<TodoItem> mTodoItems;
    protected View mRootView;
    protected Context mContext;
    protected TodoItemsAdapter mTodoItemsAdapter;
    protected TodoItemsAdapter.StatusChangeListener mCallback;
    protected RecyclerView rvTodoItemsList;
    protected TodoItem mTodoItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mRootView = inflater.inflate(R.layout.fragement_todo, container, false);
        mContext = this.getContext();
        mTodoItems = getTodoItems();
        mTodoItemsAdapter = new TodoItemsAdapter(mContext, mCallback, mTodoItems, new TodoItemsAdapter.RecyclerViewListener() {
            @Override
            public void onClick(View view, int postion) {
                showEditDialog(mTodoItems.get(postion), EditDialogFragment.Action.Edit);
            }
        });
        setupView();
        return mRootView;
    }

    protected List<TodoItem> getTodoItems() {
        return TodoItem.getIncompleteTodoItemsList();
    }

    protected void setupView() {
        rvTodoItemsList = mRootView.findViewById(R.id.rvTodoItemsList);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        rvTodoItemsList.addItemDecoration(itemDecoration);
        rvTodoItemsList.setAdapter(mTodoItemsAdapter);
        rvTodoItemsList.setLayoutManager(new LinearLayoutManager(mContext));
        setUpItemTouchHelper();
        setUpFAB();
    }

    protected void showEditDialog(TodoItem item, EditDialogFragment.Action action) {
        FragmentManager fm = getFragmentManager();
        EditDialogFragment editTodoItemDialogFragment = EditDialogFragment.newInstance(item, action);
        editTodoItemDialogFragment.setTargetFragment(this, 300);
        editTodoItemDialogFragment.show(fm, EditDialogFragment.class.toString());
    }

    private void setUpFAB() {
        FloatingActionButton myFab = mRootView.findViewById(R.id.fabAddTaskButton);
        myFab.setOnClickListener((View v) -> {
            showEditDialog(null, EditDialogFragment.Action.Create);
        });
    }

    @Override
    public void onSaveNewItem(String taskDescription, String dueDate, String priority, Boolean addToCalendar) {
        mTodoItem = new TodoItem();
        mTodoItem.setDescription(taskDescription);
        mTodoItem.setPriority(priority);
        mTodoItem.setDueDate(DateUtil.parseDate(dueDate));
        mTodoItem.setComplete(false);
        int existingItemCount = mTodoItems.size();
        mTodoItem.save();
        mTodoItems.add(mTodoItem);
        if(addToCalendar) {
            addTaskToCalendar();
        };
        mTodoItemsAdapter.notifyItemInserted(existingItemCount);
    }

    /**
     * Show the contacts in the ListView.
     */
    private void addTaskToCalendar() {
        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setType("vnd.android.cursor.item/event");
        calIntent.putExtra(CalendarContract.Events.TITLE, mTodoItem.getDescription());
        Calendar cal = Calendar.getInstance();
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTime());
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis()+60*60);
        startActivity(calIntent);
    }

    @Override
    public void onSaveEditItem() {
        mTodoItemsAdapter.notifyDataSetChanged();
        mCallback.onStatusChange();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (TodoItemsAdapter.StatusChangeListener) context;
    }

    protected void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable mDeleteIcon;
            int mDeleteIconMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark));
                mDeleteIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_delete_black_24dp);
                mDeleteIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                mDeleteIconMargin = (int) mContext.getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int swipedPosition = viewHolder.getAdapterPosition();
                showConfirmation(swipedPosition);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                if (viewHolder.getAdapterPosition() == -1) {
                    return;
                }

                if (!initiated) {
                    init();
                }
                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = mDeleteIcon.getIntrinsicWidth();
                int intrinsicHeight = mDeleteIcon.getIntrinsicWidth();
                int xMarkLeft = itemView.getRight() - mDeleteIconMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - mDeleteIconMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                mDeleteIcon.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
                mDeleteIcon.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(rvTodoItemsList);
    }

    protected void showConfirmation(final int swipedPosition) {
        Drawable mDeleteIcon = ContextCompat.getDrawable(mContext, android.R.drawable.ic_dialog_alert);
        mDeleteIcon.setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);

        new AlertDialog.Builder(getActivity())
                .setMessage("Are you sure you want to delete this task?")
                .setTitle("Confirm")
                .setIcon(mDeleteIcon)
                .setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int whichButton) {
                                TodoItem item = mTodoItems.get(swipedPosition);
                                item.delete();
                                mTodoItemsAdapter.removeItem(swipedPosition);
                                mTodoItemsAdapter.notifyItemRemoved(swipedPosition);
                                Toast.makeText(getContext(), "Task deleted successfully.", Toast.LENGTH_LONG);
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog, int whichButton) {
                        mTodoItemsAdapter.notifyDataSetChanged();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {//reload the original state when alert is canceled
            @Override
            public void onCancel(DialogInterface dialog) {
                mTodoItemsAdapter.notifyDataSetChanged();
            }
        }).show();
    }

    protected void sortByPriority() {
        Log.d(TAG, ">>> sortByPriority called");
        Collections.sort(mTodoItems, (l1, l2) -> l1.comparePriorityTo(l2));
        mTodoItemsAdapter.notifyDataSetChanged();
    }

    protected void sortByDueDate() {
        Log.d(TAG, ">>> sortByDueDate called");
        Collections.sort(mTodoItems, (l1, l2) -> l1.compareDueDateTo(l2));
        mTodoItemsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.listy_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortPriority:
                sortByPriority();
                return true;

            case R.id.sortDueDate:
                sortByDueDate();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
