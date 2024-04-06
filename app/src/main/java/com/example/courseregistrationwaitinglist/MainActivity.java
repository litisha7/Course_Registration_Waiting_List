package com.example.courseregistrationwaitinglist;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseregistrationwaitinglist.DbHandler.Student;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerView.OnItemTouchListener {
    private RegistrationListActivity mAdapter;
    private List<Student> studentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView noNamesView;
    private DbHandler db;
    private GestureDetector gestureDetector;
    private ClickListener clickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        noNamesView = findViewById(R.id.empty_items_view);
        Button myButton = findViewById(R.id.myButton);

        db = new DbHandler(this);

        studentList.addAll(db.getAllStudents());

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createStudentDialog(null, -1);
            }
        });

        mAdapter = new RegistrationListActivity(this, studentList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL)); // Add divider decoration
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        toggleEmptyItems();

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {
                    int position = recyclerView.getChildAdapterPosition(child);
                    showActionsDialog(position);
                }
            }
        });

        recyclerView.addOnItemTouchListener(this);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && gestureDetector.onTouchEvent(e)) {
            int position = rv.getChildAdapterPosition(child);
            clickListener.onClick(child, position);
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private void createStudent(String name, String priority) {
        Student student = new Student();
        student.setName(name);
        student.setPriority(priority);

        long id = db.addStudent(student);

        Student addedStudent = db.getStudent(id);

        if (addedStudent != null) {
            studentList.add(0, addedStudent);
            mAdapter.notifyDataSetChanged();
            toggleEmptyItems();
        }
    }

    private void setPriority(String name, String priority, int position) {
        Student student = studentList.get(position);
        student.setPriority(priority);

        db.updateStudent(student);

        studentList.set(position, student);
        mAdapter.notifyItemChanged(position);

        toggleEmptyItems();
    }

    private void updateStudent(String name, String priority, int position) {
        Student student = studentList.get(position);
        student.setName(name);
        student.setPriority(priority);

        db.updateStudent(student);

        studentList.set(position, student);
        mAdapter.notifyItemChanged(position);

        toggleEmptyItems();
    }

    private void deleteStudent(int position) {
        db.deleteStudent(studentList.get(position));

        studentList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyItems();
    }

    private void showActionsDialog(final int position) {
        CharSequence options[] = new CharSequence[]{"Set Priority", "Modify", "Remove"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    setPriorityDialog(studentList.get(position), position);
                } else if (which == 1) {
                    editStudentDialog(studentList.get(position), position);
                } else {
                    deleteStudent(position);
                }
            }
        });
        builder.show();
    }

    private void createStudentDialog(final Student student, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.add_student, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputName = view.findViewById(R.id.name);

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(getString(R.string.new_student));

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(inputName.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter name!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }
                createStudent(inputName.getText().toString(), "");
            }
        });
    }

    private void setPriorityDialog(final Student student, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.set_priority, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final TextView inputName = view.findViewById(R.id.name);
        final EditText inputPriority = view.findViewById(R.id.priority);

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(getString(R.string.set_priority));

        if (student != null) {
            inputName.setText(student.getName());
            inputPriority.setText(student.getPriority());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(inputPriority.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter priority!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                if (student != null) {
                    updateStudent(inputName.getText().toString(), inputPriority.getText().toString(), position);
                }
            }
        });
    }

    private void editStudentDialog(final Student student, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.edit_student, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputName = view.findViewById(R.id.name);
        final EditText inputPriority = view.findViewById(R.id.priority);

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(getString(R.string.edit_information));

        if (student != null) {
            inputName.setText(student.getName());
            inputPriority.setText(student.getPriority());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Update Changes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(inputName.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (student != null) {
                    updateStudent(inputName.getText().toString(), inputPriority.getText().toString(), position);
                    alertDialog.dismiss();  // Dismiss dialog after updating
                }
            }
        });
    }

    private void toggleEmptyItems() {
        if (studentList.size() > 0) {
            noNamesView.setVisibility(View.GONE);
        } else {
            noNamesView.setVisibility(View.VISIBLE);
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);
    }
}
