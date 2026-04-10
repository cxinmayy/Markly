package com.example.markly.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Markly.db";
    private static final int DATABASE_VERSION = 1;

    // TABLE NAMES
    public static final String TABLE_SECTION = "section";
    public static final String TABLE_STUDENT = "student";
    public static final String TABLE_ATTENDANCE = "attendance";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // SECTION TABLE
        String createSectionTable = "CREATE TABLE " + TABLE_SECTION + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "department TEXT, " +
                "section TEXT, " +
                "subject TEXT" +
                ")";
        db.execSQL(createSectionTable);

        // STUDENT TABLE
        String createStudentTable = "CREATE TABLE " + TABLE_STUDENT + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "reg_no TEXT, " +
                "section_id INTEGER" +
                ")";
        db.execSQL(createStudentTable);

        // ATTENDANCE TABLE
        String createAttendanceTable = "CREATE TABLE " + TABLE_ATTENDANCE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER, " +
                "section_id INTEGER, " +
                "date TEXT, " +
                "status INTEGER" + // 1 = Present, 0 = Absent
                ")";
        db.execSQL(createAttendanceTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SECTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        onCreate(db);
    }
    public long insertSection(String department, String section, String subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("department", department);
        values.put("section", section);
        values.put("subject", subject);

        return db.insert(TABLE_SECTION, null, values);
    }

    public Cursor getAllSections() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SECTION, null);
    }

    public long insertStudent(String name, String regNo, int sectionId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("reg_no", regNo);
        values.put("section_id", sectionId);

        return db.insert(TABLE_STUDENT, null, values);
    }

    public void updateStudent(int id, String name, String regNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("reg_no", regNo);
        db.update(TABLE_STUDENT, values, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteStudent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ATTENDANCE, "student_id=?", new String[]{String.valueOf(id)});
        db.delete(TABLE_STUDENT, "id=?", new String[]{String.valueOf(id)});
    }
    public Cursor getStudentsBySection(int sectionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_STUDENT +
                " WHERE section_id = ?", new String[]{String.valueOf(sectionId)});
    }

    public float getAttendancePercentage(int studentId, int sectionId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor total = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_ATTENDANCE +
                " WHERE student_id=? AND section_id=?",
                new String[]{String.valueOf(studentId), String.valueOf(sectionId)});

        Cursor present = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_ATTENDANCE +
                " WHERE student_id=? AND section_id=? AND status=1",
                new String[]{String.valueOf(studentId), String.valueOf(sectionId)});

        int totalCount = 0, presentCount = 0;
        if (total.moveToFirst()) totalCount = total.getInt(0);
        if (present.moveToFirst()) presentCount = present.getInt(0);
        total.close();
        present.close();

        if (totalCount == 0) return -1f; // No records yet
        return (presentCount * 100f) / totalCount;
    }

    public void saveAttendance(int studentId, int sectionId, String date, int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_ATTENDANCE +
                        " WHERE student_id=? AND date=?",
                new String[]{String.valueOf(studentId), date});

        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("section_id", sectionId);
        values.put("date", date);
        values.put("status", status);

        if (cursor.moveToFirst()) {
            db.update(TABLE_ATTENDANCE, values,
                    "student_id=? AND date=?",
                    new String[]{String.valueOf(studentId), date});
        } else {
            db.insert(TABLE_ATTENDANCE, null, values);
        }
    }

    public int getAttendanceStatus(int studentId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT status FROM " + TABLE_ATTENDANCE +
                        " WHERE student_id=? AND date=?",
                new String[]{String.valueOf(studentId), date});

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        return -1; // default unmarked
    }

    public Cursor getAttendanceForExport(int sectionId, String date) {

        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT student.name, student.reg_no, attendance.status " +
                        "FROM " + TABLE_STUDENT + " student " +
                        "LEFT JOIN " + TABLE_ATTENDANCE + " attendance " +
                        "ON student.id = attendance.student_id AND attendance.date=? " +
                        "WHERE student.section_id=?";

        return db.rawQuery(query, new String[]{date, String.valueOf(sectionId)});
    }

    public void deleteSection(int sectionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete all attendance records for students in this section
        db.delete(TABLE_ATTENDANCE, "section_id=?", new String[]{String.valueOf(sectionId)});
        
        // Delete all students in this section
        db.delete(TABLE_STUDENT, "section_id=?", new String[]{String.valueOf(sectionId)});
        
        // Delete the section itself
        db.delete(TABLE_SECTION, "id=?", new String[]{String.valueOf(sectionId)});
    }
}