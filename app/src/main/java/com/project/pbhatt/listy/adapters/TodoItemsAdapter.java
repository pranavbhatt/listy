package com.project.pbhatt.listy.adapters;

import android.content.Context;
import android.content.res.ColorStateList;


import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.project.pbhatt.listy.R;
import com.project.pbhatt.listy.models.TodoItem;

import java.util.List;

/**
 * Created by pbhatt on 12/3/17.
 */
public class TodoItemsAdapter extends RecyclerView.Adapter<TodoItemsAdapter.ViewHolder> {
    private StatusChangeListener mCallback;
    private Context mContext;

    public interface RecyclerViewListener {
        void onClick(View view, int position);
    }

    public interface StatusChangeListener {
        void onStatusChange();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView taskDescriptionTextView;
        private ImageButton iBtnPriority;
        private ImageButton iBtnDateIcon;
        private TextView dueDateTextView;
        private RadioButton chkStatus;
        private RecyclerViewListener mListener;


        public ViewHolder(View itemView, RecyclerViewListener listener) {
            super(itemView);
            taskDescriptionTextView = itemView.findViewById(R.id.tvTaskDescription);
            dueDateTextView = itemView.findViewById(R.id.tvDueDate);
            iBtnPriority = itemView.findViewById(R.id.iBtnPriority);
            iBtnDateIcon = itemView.findViewById(R.id.iBtnDateIcon);
            chkStatus = itemView.findViewById(R.id.cbStatus);

            mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }


    private List<TodoItem> mTodoItems;
    private final RecyclerViewListener mClickListener;


    @Override
    public TodoItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View todoItemView = inflater.inflate(R.layout.todo_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(todoItemView, mClickListener);
        return viewHolder;
    }

    public TodoItemsAdapter(Context context, StatusChangeListener callback, List<TodoItem> todoItems, RecyclerViewListener clickListener) {
        this.mContext = context;
        this.mTodoItems = todoItems;
        this.mClickListener = clickListener;
        this.mCallback = callback;
    }

    @Override
    public void onBindViewHolder(TodoItemsAdapter.ViewHolder viewHolder, final int position) {
        final TodoItem todoItem = mTodoItems.get(position);

        setupImageButtons(viewHolder.iBtnPriority,viewHolder.iBtnDateIcon, todoItem.getPriority());
        viewHolder.taskDescriptionTextView.setText(todoItem.getDescription());
        viewHolder.dueDateTextView.setText(todoItem.getFormattedDueDate());

        final RadioButton chkStatus = viewHolder.chkStatus;
        setRadioButtonTint(chkStatus, todoItem.getPriority());
        chkStatus.setChecked(todoItem.isComplete());
        chkStatus.setOnClickListener((View view) ->{
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                        String toastText = "";
                        if (todoItem.isComplete()) {
                            todoItem.setComplete(false);
                            toastText = "Item moved to task list";
                        } else {
                            todoItem.setComplete(true);
                            toastText = "Item moved to complete";
                        }
                        todoItem.save();
                        mTodoItems.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(mContext, toastText, Toast.LENGTH_LONG).show();
                        mCallback.onStatusChange();
                    }, 300);
        });
    }


    public void removeItem(int position) {
        mTodoItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mTodoItems.size());
        Toast.makeText(mContext, "Item deleted successfully", Toast.LENGTH_LONG).show();
    }


    protected void setupImageButtons(final ImageButton iBtnPriority,final ImageButton iBtnDateIcon, TodoItem.Priority priority) {
        switch (priority) {
            case HIGH:
                iBtnPriority.setImageResource(R.drawable.ic_flag_black_24dp);
                iBtnPriority.setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark));
                iBtnDateIcon.setImageResource(R.drawable.ic_today_black_24dp);
                iBtnDateIcon.setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark));
                break;
            case MEDIUM:
                iBtnPriority.setImageResource(R.drawable.ic_flag_black_24dp);
                iBtnPriority.setColorFilter(mContext.getResources().getColor(R.color.medium_priority));
                iBtnDateIcon.setImageResource(R.drawable.ic_today_black_24dp);
                iBtnDateIcon.setColorFilter(mContext.getResources().getColor(R.color.medium_priority));
                break;
            case LOW:
                iBtnPriority.setImageResource(R.drawable.ic_flag_black_24dp);
                iBtnPriority.setColorFilter(mContext.getResources().getColor(R.color.colorAccent));
                iBtnDateIcon.setImageResource(R.drawable.ic_today_black_24dp);
                iBtnDateIcon.setColorFilter(mContext.getResources().getColor(R.color.colorAccent));
                break;
            default:
                iBtnPriority.setImageResource(R.drawable.ic_flag_black_24dp);
                iBtnPriority.setColorFilter(mContext.getResources().getColor(R.color.secondary_text));
                iBtnDateIcon.setImageResource(R.drawable.ic_today_black_24dp);
                iBtnDateIcon.setColorFilter(mContext.getResources().getColor(R.color.secondary_text));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mTodoItems.size();
    }

    protected void setRadioButtonTint(RadioButton chk, TodoItem.Priority priority) {
        ColorStateList colorStateList = null;
        switch (priority) {
            case HIGH:
                colorStateList = new ColorStateList(getCheckBoxStates(), getHighPriorityColoredList());
                break;
            case MEDIUM:
                colorStateList = new ColorStateList(getCheckBoxStates(), getMediumPriorityColoredList());
                break;
            case LOW:
                colorStateList = new ColorStateList(getCheckBoxStates(), getLowPriorityColoredList());
                break;
            case NONE:
                colorStateList = new ColorStateList(getCheckBoxStates(), getNonePriorityColoredList());
                break;
        }
        CompoundButtonCompat.setButtonTintList(chk, colorStateList);
    }

    protected int[][] getCheckBoxStates() {
        return new int[][]{
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_checked}, // checked
        };
    }

    protected int[] getHighPriorityColoredList() {
        return new int[]{
                mContext.getResources().getColor(R.color.colorPrimaryDark),
                mContext.getResources().getColor(R.color.colorPrimaryDark),
        };
    }

    protected int[] getMediumPriorityColoredList() {
        return new int[]{
                mContext.getResources().getColor(R.color.medium_priority),
                mContext.getResources().getColor(R.color.medium_priority),
        };
    }

    protected int[] getLowPriorityColoredList() {
        return new int[]{
                mContext.getResources().getColor(R.color.colorAccent),
                mContext.getResources().getColor(R.color.colorAccent),
        };
    }

    protected int[] getNonePriorityColoredList() {
        return new int[]{
                mContext.getResources().getColor(R.color.secondary_text),
                mContext.getResources().getColor(R.color.secondary_text),
        };
    }
}
