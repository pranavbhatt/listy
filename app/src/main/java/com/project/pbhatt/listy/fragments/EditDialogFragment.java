package com.project.pbhatt.listy.fragments;

/**
 * Created by pbhatt on 12/4/17.
 */


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.project.pbhatt.listy.R;
import com.project.pbhatt.listy.models.TodoItem;
import com.project.pbhatt.listy.utils.DateUtil;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.project.pbhatt.listy.R.id.spPriority;


public class EditDialogFragment extends DialogFragment {
    public enum Action {
        Create("Create New Task"), Edit("Edit Task");

        private String description;

        Action(String description) {
            this.description = description;
        }

        public String getDescription() {
            return this.description;
        }
    }

    private EditText mEditText;
    private Button mSaveButton;
    private Button mCancelButton;
    private Spinner mSpinner;
    private TextView etDueDate;
    private Boolean isEditDialog;
    private TodoItem mEditItem;

    public EditDialogFragment() {
    }

    public interface EditTodoItemListener {
        void onSaveNewItem(String taskDescription, String dueDate, String priority);

        void onSaveEditItem();
    }

    public static EditDialogFragment newInstance(final TodoItem item, final Action action) {
        EditDialogFragment frag = new EditDialogFragment();
        frag.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);

        frag.isEditDialog = true;
        if (Action.Create == action) {
            frag.isEditDialog = false;
        }
        Bundle args = new Bundle();
        args.putString("title", action.getDescription());
        if (item != null) {
            args.putParcelable("todoItem", Parcels.wrap(item));
        }
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(true);
        return inflater.inflate(R.layout.fragement_edit_todo_item, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mEditItem = Parcels.unwrap(getArguments().getParcelable("todoItem"));
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Task");
        getDialog().setTitle(title);
        setupView(view);
        if (mEditItem != null) {
            setupEditScreen(mEditItem);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupSaveButton() {
        mSaveButton.setOnClickListener((View v) -> {
                    EditTodoItemListener listener = (EditTodoItemListener) getTargetFragment();
                    final String dueDate = etDueDate.getText().toString();
                    final String taskDescription = mEditText.getText().toString();
                    final String priority = mSpinner.getSelectedItem().toString();

                    if (isEditDialog) {
                        mEditItem.setDescription(taskDescription);
                        mEditItem.setPriority(priority);
                        mEditItem.setDueDate(DateUtil.parseDate(dueDate));
                        mEditItem.save();
                        listener.onSaveEditItem();
                    } else {
                        listener.onSaveNewItem(taskDescription, dueDate, priority);
                    }
                    dismiss();
                }
        );
    }

    private void setupEditScreen(TodoItem item) {
        mEditText.setText(item.getDescription());
        mEditText.setSelection(mEditText.getText().length());
        etDueDate.setText(item.getFormattedDueDate());
        mSpinner.setSelection(item.getPriority().getValue());
    }


    private void setupView(View view) {
        mEditText = view.findViewById(R.id.etTaskDescription);
        mSaveButton = view.findViewById(R.id.btSave);
        mCancelButton = view.findViewById(R.id.btnCancel);
        mCancelButton.setOnClickListener((View v) -> dismiss());
        etDueDate = view.findViewById(R.id.etDueDate);
        mSpinner = view.findViewById(spPriority);
        mEditText.requestFocus();
        setupDatePickerDialog();
        setupSaveButton();
        setupSpinner();
    }

    private void setupDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        etDueDate.setHint("Choose due date");
        etDueDate.setOnClickListener((View view) -> {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                            (DatePicker datePicker, int y, int m, int d) -> {
                                etDueDate.setText(DateUtil.getFormattedDate(y, m, d));
                            }, year, month, day);
                    datePickerDialog.show();
                }
        );
    }

    private void setupSpinner() {
        List<String> priorities = Arrays.asList(getResources().getStringArray(R.array.priority_array));
        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(getContext(), R.layout.priority_spinner, priorities) {
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                setTextColor(tv, position);
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                setTextColor(tv, position);
                return view;
            }

            private void setTextColor(final TextView tv, final int position) {
                switch (position) {
                    case 0:
                        tv.setTextColor(Color.GRAY);
                        break;
                    case 1:
                        tv.setTextColor(getResources().getColor(R.color.colorAccent));
                        break;
                    case 2:
                        tv.setTextColor(getResources().getColor(R.color.medium_priority));
                        break;
                    case 3:
                        tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                        break;
                }
            }
        };
        adapter_state.setDropDownViewResource(R.layout.priority_spinner);
        mSpinner.setAdapter(adapter_state);
    }
}
