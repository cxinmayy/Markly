package com.example.markly.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markly.adapter.SimpleStudentAdapter;
import com.google.android.material.button.MaterialButton;
import com.example.markly.model.Student;
import com.example.markly.R;
import com.example.markly.adapter.SectionAdapter;
import com.example.markly.database.DatabaseHelper;
import com.example.markly.model.Section;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton fab;
    DatabaseHelper db;
    ArrayList<Section> list;
    SectionAdapter adapter;
    TextInputEditText etSearch;
    ImageView btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fabAdd);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);

        db = new DatabaseHelper(this);
        list = new ArrayList<>();

        btnSearch.setOnClickListener(v -> {
            if (etSearch.getVisibility() == View.GONE) {
                etSearch.setVisibility(View.VISIBLE);
                etSearch.requestFocus();
            } else {
                etSearch.setVisibility(View.GONE);
                etSearch.setText("");
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filterList(s.toString());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SectionAdapter(this, list);
        adapter.setOnSectionLongClickListener((section, position) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Class")
                    .setMessage("Are you sure you want to delete " + section.getSubject() + "?\n\nThis will permanently remove all associated students and their attendance records.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        db.deleteSection(section.getId());
                        list.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, list.size());
                        Toast.makeText(this, "Class deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        recyclerView.setAdapter(adapter);

        loadSections();

        fab.setOnClickListener(v -> showAddDialog());
    }

    private void loadSections() {
        list.clear();
        Cursor cursor = db.getAllSections();

        if (cursor.moveToFirst()) {
            do {
                list.add(new Section(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                ));
            } while (cursor.moveToNext());
        }

        adapter.notifyDataSetChanged();
    }

    private void filterList(String text) {
        ArrayList<Section> filteredList = new ArrayList<>();
        for (Section item : list) {
            if (item.getSubject().toLowerCase().contains(text.toLowerCase()) || 
                item.getDepartment().toLowerCase().contains(text.toLowerCase()) ||
                item.getSection().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }

    private void showAddDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_add_section, null);

        TextInputEditText etDept = view.findViewById(R.id.etDept);
        TextInputEditText etSection = view.findViewById(R.id.etSection);
        TextInputEditText etSubject = view.findViewById(R.id.etSubject);

        new AlertDialog.Builder(this)
                .setTitle("Add Section")
                .setView(view)
                .setPositiveButton("Save", (dialog, which) -> {

                    String dept = etDept.getText().toString();
                    String sec = etSection.getText().toString();
                    String sub = etSubject.getText().toString();

                    if (dept.isEmpty() || sec.isEmpty() || sub.isEmpty()) {
                        Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long id = db.insertSection(dept, sec, sub);

                    loadSections();

                    int sectionId = (int) id;

                    // Open student dialog with sectionId
                    showAddStudentDialog(sectionId);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddStudentDialog(int sectionId) {

        View view = getLayoutInflater().inflate(R.layout.dialog_add_student, null);

        TextInputEditText etName = view.findViewById(R.id.etName);
        TextInputEditText etReg = view.findViewById(R.id.etReg);
        MaterialButton btnAdd = view.findViewById(R.id.btnAddStudent);
        MaterialButton btnDone = view.findViewById(R.id.btnDone);
        RecyclerView recycler = view.findViewById(R.id.recyclerStudents);

        ArrayList<Student> tempList = new ArrayList<>();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        SimpleStudentAdapter studentAdapter = new SimpleStudentAdapter(tempList);
        recycler.setAdapter(studentAdapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String reg = etReg.getText().toString();

            if (name.isEmpty() || reg.isEmpty()) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            db.insertStudent(name, reg, sectionId);

            tempList.add(new Student(0, name, reg, sectionId));
            studentAdapter.notifyDataSetChanged();

            etName.setText("");
            etReg.setText("");
        });

        btnDone.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }
}