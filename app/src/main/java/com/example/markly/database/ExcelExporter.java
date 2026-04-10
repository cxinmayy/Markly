package com.example.markly.database;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import android.graphics.pdf.PdfDocument;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.database.Cursor;
import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class ExcelExporter {

    public static void exportAttendance(Context context,
                                        DatabaseHelper db,
                                        int sectionId,
                                        String sectionName,
                                        String date) {

        try {

            Cursor cursor = db.getAttendanceForExport(sectionId, date);

            File folder = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS),
                    "AttendanceExports"
            );

            if (!folder.exists()) folder.mkdirs();

            File file = new File(folder, sectionName + "_" + date + ".csv");

            FileWriter writer = new FileWriter(file);

            writer.append("Name,Reg No,Status\n");

            if (cursor.moveToFirst()) {
                do {

                    String name = cursor.getString(0);
                    String reg = cursor.getString(1);
                    int statusInt = cursor.getInt(2);

                    String status = statusInt == 1 ? "Present" : "Absent";

                    writer.append(name + "," + reg + "," + status + "\n");

                } while (cursor.moveToNext());
            }

            writer.flush();
            writer.close();

            Toast.makeText(context,
                    "Exported to Downloads/AttendanceExports",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}