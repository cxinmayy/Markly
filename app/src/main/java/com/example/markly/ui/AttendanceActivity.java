package com.example.markly.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.markly.R;
import com.example.markly.adapter.DateAdapter;
import com.example.markly.adapter.SimpleStudentAdapter;
import com.example.markly.adapter.StudentAdapter;
import com.example.markly.database.DatabaseHelper;
import com.example.markly.model.Student;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.markly.database.ExcelExporter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.example.markly.database.PdfExporter;

public class AttendanceActivity extends AppCompatActivity {

    RecyclerView recyclerStudents, recyclerViewDates;
    FloatingActionButton fabAddStudent;
    TextView tvMonthYear, tvTitle, tvSubtitle;

    DatabaseHelper db;
    ArrayList<Student> list;
    int sectionId;
    String selectedDate;

    private Calendar currentMonth;
    private DateAdapter dateAdapter;
    private List<DateAdapter.DateItem> dateItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        // Views
        recyclerStudents = findViewById(R.id.recyclerStudents);
        recyclerViewDates = findViewById(R.id.recyclerViewDates);
        fabAddStudent = findViewById(R.id.fabAddStudent);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);

        // Back button
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // DB & section
        db = new DatabaseHelper(this);
        list = new ArrayList<>();
        sectionId = getIntent().getIntExtra("section_id", -1);

        // Title from intent
        String title = getIntent().getStringExtra("title");
        String subtitle = getIntent().getStringExtra("subtitle");
        if (tvTitle != null && title != null) tvTitle.setText(title);
        if (tvSubtitle != null && subtitle != null) tvSubtitle.setText(subtitle);

        // Student list
        recyclerStudents.setLayoutManager(new LinearLayoutManager(this));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            final android.graphics.Paint paintBg = new android.graphics.Paint();

            @Override
            public boolean onMove(RecyclerView rv, RecyclerView.ViewHolder vh, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(android.graphics.Canvas c,
                                    RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;

                if (dX > 0) {
                    // Swipe Right → Present (Green tint)
                    paintBg.setColor(android.graphics.Color.parseColor("#2D6A4F"));
                    paintBg.setAlpha((int) Math.min(200, Math.abs(dX) / itemView.getWidth() * 255));
                    c.drawRoundRect(
                        itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + dX, itemView.getBottom(),
                        32, 32, paintBg
                    );
                } else if (dX < 0) {
                    // Swipe Left → Absent (Red tint)
                    paintBg.setColor(android.graphics.Color.parseColor("#BA1A1A"));
                    paintBg.setAlpha((int) Math.min(200, Math.abs(dX) / itemView.getWidth() * 255));
                    c.drawRoundRect(
                        itemView.getRight() + dX, itemView.getTop(),
                        itemView.getRight(), itemView.getBottom(),
                        32, 32, paintBg
                    );
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Student s = list.get(position);
                int status = (direction == ItemTouchHelper.RIGHT) ? 1 : 0;
                db.saveAttendance(s.getId(), sectionId, selectedDate, status);
                recyclerStudents.getAdapter().notifyItemChanged(position);
            }
        }).attachToRecyclerView(recyclerStudents);

        // Init calendar to today
        currentMonth = Calendar.getInstance();
        selectedDate = getToday();

        // Build date strip
        buildDateStrip();

        // Month nav buttons (find by position in layout)
        findMonthNavButtons();

        loadStudents();

        fabAddStudent.setOnClickListener(v -> showAddStudentDialog());

        View btnExport = findViewById(R.id.btnExport);
        View btnSearch = findViewById(R.id.btnSearch);
        TextInputEditText etSearch = findViewById(R.id.etSearch);

        btnSearch.setOnClickListener(v -> {
            if (etSearch.getVisibility() == View.GONE) {
                etSearch.setVisibility(View.VISIBLE);
                etSearch.requestFocus();
            } else {
                etSearch.setVisibility(View.GONE);
                etSearch.setText("");
            }
        });

        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                filterStudents(s.toString());
            }
        });

        btnExport.setOnClickListener(v -> {
            java.io.File pdfFile = PdfExporter.exportAttendancePDF(
                    this,
                    db,
                    sectionId,
                    tvTitle.getText().toString(),
                    selectedDate
            );

            if (pdfFile != null && pdfFile.exists()) {
                android.net.Uri uri = androidx.core.content.FileProvider.getUriForFile(
                        this,
                        getApplicationContext().getPackageName() + ".provider",
                        pdfFile
                );

                android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("application/pdf");
                shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
                shareIntent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(android.content.Intent.createChooser(shareIntent, "Share Attendance Report"));
            } else {
                android.widget.Toast.makeText(this, "Failed to export PDF", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildDateStrip() {
        dateItems = new ArrayList<>();
        String[] dayNames = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

        Calendar cal = (Calendar) currentMonth.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int todayDay = -1;
        Calendar today = Calendar.getInstance();

        // Find today's index in this month
        int selectedIdx = 0;
        for (int i = 0; i < daysInMonth; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i + 1);
            int dow = cal.get(Calendar.DAY_OF_WEEK) - 1;
            String dayNumStr = String.valueOf(i + 1);
            String fullDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

            dateItems.add(new DateAdapter.DateItem(dayNames[dow], dayNumStr, fullDate));

            if (fullDate.equals(selectedDate)) {
                selectedIdx = i;
            }
        }

        // Month/Year label
        SimpleDateFormat monthFmt = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        if (tvMonthYear != null) {
            tvMonthYear.setText(monthFmt.format(currentMonth.getTime()));
        }

        dateAdapter = new DateAdapter(dateItems, selectedIdx, (pos, date) -> {
            selectedDate = date;
            loadStudents();
        });

        recyclerViewDates.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewDates.setAdapter(dateAdapter);

        // Scroll to selected date
        final int scrollTo = selectedIdx;
        recyclerViewDates.post(() -> recyclerViewDates.scrollToPosition(scrollTo));
    }

    private void findMonthNavButtons() {
        View btnPrev = findViewById(R.id.btnPrevMonth);
        View btnNext = findViewById(R.id.btnNextMonth);

        if (btnPrev != null) {
            btnPrev.setOnClickListener(v -> {
                currentMonth.add(Calendar.MONTH, -1);
                // Select the 1st of the new month as default
                selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(getFirstOfMonth(currentMonth));
                buildDateStrip();
                loadStudents();
            });
        }

        if (btnNext != null) {
            btnNext.setOnClickListener(v -> {
                currentMonth.add(Calendar.MONTH, 1);
                selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(getFirstOfMonth(currentMonth));
                buildDateStrip();
                loadStudents();
            });
        }
    }

    private java.util.Date getFirstOfMonth(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    private void loadStudents() {
        list.clear();
        Cursor cursor = db.getStudentsBySection(sectionId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(new Student(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3)
                ));
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        
        StudentAdapter studentAdapter = new StudentAdapter(list, db, selectedDate, sectionId);
        studentAdapter.setOnStudentLongClickListener((student, position) -> showEditDeleteDialog(student));
        recyclerStudents.setAdapter(studentAdapter);
    }

    private String getToday() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudents();
    }

    private void showAddStudentDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_student, null);

        TextInputEditText etName = view.findViewById(R.id.etName);
        TextInputEditText etReg = view.findViewById(R.id.etReg);
        MaterialButton btnAdd = view.findViewById(R.id.btnAddStudent);
        MaterialButton btnDone = view.findViewById(R.id.btnDone);
        RecyclerView recycler = view.findViewById(R.id.recyclerStudents);

        ArrayList<Student> tempList = new ArrayList<>();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        SimpleStudentAdapter adapter = new SimpleStudentAdapter(tempList);
        recycler.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String reg = etReg.getText().toString();
            if (name.isEmpty() || reg.isEmpty()) return;
            db.insertStudent(name, reg, sectionId);
            tempList.add(new Student(0, name, reg, sectionId));
            adapter.notifyDataSetChanged();
            etName.setText("");
            etReg.setText("");
        });

        btnDone.setOnClickListener(v -> {
            dialog.dismiss();
            loadStudents();
        });

        dialog.show();
    }

    private void showEditDeleteDialog(Student student) {
        new AlertDialog.Builder(this)
                .setTitle("Manage Student")
                .setItems(new CharSequence[]{"Edit Details", "Delete Student"}, (dialog, which) -> {
                    if (which == 0) {
                        showEditStudentDialog(student);
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Delete Student")
                                .setMessage("Are you sure you want to delete " + student.getName() + "? All attendance for this student will be lost.")
                                .setPositiveButton("Delete", (d, w) -> {
                                    db.deleteStudent(student.getId());
                                    loadStudents();
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                })
                .show();
    }

    private void showEditStudentDialog(Student student) {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_student, null);
        TextInputEditText etName = view.findViewById(R.id.etName);
        TextInputEditText etReg = view.findViewById(R.id.etReg);
        MaterialButton btnAdd = view.findViewById(R.id.btnAddStudent);
        MaterialButton btnDone = view.findViewById(R.id.btnDone);
        RecyclerView recycler = view.findViewById(R.id.recyclerStudents);
        recycler.setVisibility(View.GONE);
        btnDone.setText("Cancel");
        btnAdd.setText("Save Changes");

        etName.setText(student.getName());
        etReg.setText(student.getRegNo());

        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String reg = etReg.getText().toString();
            if (name.isEmpty() || reg.isEmpty()) return;
            db.updateStudent(student.getId(), name, reg);
            dialog.dismiss();
            loadStudents();
        });

        btnDone.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void filterStudents(String query) {
        if (recyclerStudents.getAdapter() instanceof StudentAdapter) {
            ArrayList<Student> filtered = new ArrayList<>();
            for (Student s : list) {
                if (s.getName().toLowerCase().contains(query.toLowerCase()) || 
                    s.getRegNo().toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(s);
                }
            }
            ((StudentAdapter) recyclerStudents.getAdapter()).updateList(filtered);
        }
    }
}
