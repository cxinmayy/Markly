package com.example.markly.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markly.adapter.SimpleStudentAdapter;
import com.example.markly.database.BackupRestoreHelper;
import com.google.android.material.button.MaterialButton;
import com.example.markly.model.Student;
import com.example.markly.R;
import com.example.markly.adapter.SectionAdapter;
import com.example.markly.database.DatabaseHelper;
import com.example.markly.model.Section;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import android.view.HapticFeedbackConstants;

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
    ImageView btnMenu;
    DrawerLayout drawerLayout;

    // ── Restore file picker ──────────────────────────────────────────────
    private final ActivityResultLauncher<Intent> restoreFilePicker =
        registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri == null) return;
                    try {
                        // Close current DB before overwriting the file
                        db.close();
                        BackupRestoreHelper.restoreFromUri(this, uri);
                        // Re-open a fresh connection
                        db = new DatabaseHelper(this);
                        loadSections();
                        Toast.makeText(this,
                            "✓ Data restored successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this,
                            "Restore failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply saved theme before inflation
        boolean savedDark = getSharedPreferences("markly_prefs", MODE_PRIVATE)
            .getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
            savedDark ? AppCompatDelegate.MODE_NIGHT_YES
                      : AppCompatDelegate.MODE_NIGHT_NO
        );

        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fabAdd);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnMenu = findViewById(R.id.btnMenu);
        drawerLayout = findViewById(R.id.drawerLayout);

        // Open sidebar on menu icon tap
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(androidx.core.view.GravityCompat.START));

        // Sidebar navigation item clicks
        LinearLayout sidebarNavClasses   = findViewById(R.id.sidebarNavClasses);
        LinearLayout sidebarNavDarkMode  = findViewById(R.id.sidebarNavDarkMode);
        LinearLayout sidebarNavBackup    = findViewById(R.id.sidebarNavBackup);
        LinearLayout sidebarNavRestore   = findViewById(R.id.sidebarNavRestore);
        LinearLayout sidebarNavReportBug = findViewById(R.id.sidebarNavReportBug);
        MaterialSwitch switchDarkMode    = findViewById(R.id.switchDarkMode);

        // Set switch to reflect saved preference
        boolean isDark = getSharedPreferences("markly_prefs", MODE_PRIVATE)
            .getBoolean("dark_mode", false);
        switchDarkMode.setChecked(isDark);

        sidebarNavClasses.setOnClickListener(v -> drawerLayout.closeDrawers());

        // ── Dark Mode Toggle ─────────────────────────────────────────────
        sidebarNavDarkMode.setOnClickListener(v -> {
            boolean nowDark = !switchDarkMode.isChecked();
            switchDarkMode.setChecked(nowDark);
            getSharedPreferences("markly_prefs", MODE_PRIVATE)
                .edit()
                .putBoolean("dark_mode", nowDark)
                .apply();
            AppCompatDelegate.setDefaultNightMode(
                nowDark ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        sidebarNavBackup.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            try {
                Intent shareIntent = BackupRestoreHelper.createBackupShareIntent(this);
                startActivity(shareIntent);
            } catch (Exception e) {
                Toast.makeText(this,
                    "Backup failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // ── Restore ─────────────────────────────────────────────────────
        sidebarNavRestore.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            new AlertDialog.Builder(this)
                .setTitle("Restore Data")
                .setMessage("This will replace ALL current data with the selected backup." +
                    "\n\nThis cannot be undone. Continue?")
                .setPositiveButton("Choose File", (dialog, which) -> {
                    Intent picker = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    picker.setType("application/octet-stream");
                    picker.addCategory(Intent.CATEGORY_OPENABLE);
                    restoreFilePicker.launch(picker);
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        // ── Report Bug ──────────────────────────────────────────────────
        sidebarNavReportBug.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            String bugBody =
                "**Device:** " + android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL + "%0A" +
                "**Android Version:** " + android.os.Build.VERSION.RELEASE + "%0A" +
                "**App Version:** v1.0%0A%0A" +
                "**Describe the bug:**%0A(describe what happened)%0A%0A" +
                "**Steps to reproduce:**%0A1. %0A2. %0A%0A" +
                "**Expected behavior:**%0A%0A" +
                "**Screenshots:** (if any)";
            String url = "https://github.com/cxinmayy/Markly/issues/new"
                + "?title=%5BBug+Report%5D&body=" + bugBody;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        });

        db = new DatabaseHelper(this);
        list = new ArrayList<>();

        btnSearch.setOnClickListener(v -> {
            if (SearchAnimUtils.isVisible(etSearch)) {
                SearchAnimUtils.hide(etSearch);
            } else {
                SearchAnimUtils.show(etSearch);
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

        fab.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            showAddDialog();
        });
    }

    /**
     * Dismiss search bar (with animation) when the user taps anywhere
     * outside the search input while it is visible.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN
                && SearchAnimUtils.isVisible(etSearch)) {
            Rect rect = new Rect();
            etSearch.getGlobalVisibleRect(rect);
            if (!rect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                // Dismiss keyboard
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(
                        etSearch.getWindowToken(), 0);
                SearchAnimUtils.hide(etSearch);
            }
        }
        return super.dispatchTouchEvent(ev);
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
        AddSectionBottomSheet bottomSheet = new AddSectionBottomSheet();
        bottomSheet.setOnSectionSavedListener(id -> {
            loadSections();
            showAddStudentDialog((int) id);
        });
        bottomSheet.show(getSupportFragmentManager(), "AddSectionBottomSheet");
    }

    private void showAddStudentDialog(int sectionId) {
        AddStudentBottomSheet bottomSheet = AddStudentBottomSheet.newInstance(sectionId);
        // We only reload section summary when done is tapped, but actual student data
        // isn't shown on this screen, so we might not need to do anything. Just in case:
        bottomSheet.setOnStudentsDoneListener(this::loadSections);
        bottomSheet.show(getSupportFragmentManager(), "AddStudentBottomSheet");
    }
}