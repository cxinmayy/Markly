package com.example.markly.database;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class PdfExporter {

    public static File exportAttendancePDF(Context context, DatabaseHelper db, int sectionId, String sectionName, String date) {
        try {
            Cursor cursor = db.getAttendanceForExport(sectionId, date);
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 Size
            PdfDocument.Page page = document.startPage(pageInfo);
            android.graphics.Canvas canvas = page.getCanvas();
            android.graphics.Paint paint = new android.graphics.Paint();

            // Header Background
            paint.setColor(android.graphics.Color.parseColor("#1a1c1e"));
            canvas.drawRect(0, 0, 595, 120, paint);

            // Header Text
            paint.setColor(android.graphics.Color.WHITE);
            paint.setTextSize(32);
            paint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));
            canvas.drawText("Markly Attendance Report", 40, 60, paint);

            paint.setTextSize(16);
            paint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL));
            paint.setColor(android.graphics.Color.parseColor("#c3c6cf"));
            canvas.drawText("Section: " + sectionName + "    |    Date: " + date, 40, 90, paint);

            int y = 160;

            // Table Header
            paint.setColor(android.graphics.Color.parseColor("#e2e2e9"));
            canvas.drawRect(40, y-20, 555, y+10, paint);
            
            paint.setColor(android.graphics.Color.BLACK);
            paint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));
            paint.setTextSize(14);
            canvas.drawText("Reg No", 50, y, paint);
            canvas.drawText("Student Name", 180, y, paint);
            canvas.drawText("Status", 450, y, paint);

            y += 30;
            paint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL));

            boolean alternate = false;
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Zebra Striping
                    if (alternate) {
                        paint.setColor(android.graphics.Color.parseColor("#fdfcff"));
                        canvas.drawRect(40, y-20, 555, y+10, paint);
                    }
                    alternate = !alternate;

                    paint.setColor(android.graphics.Color.BLACK);
                    String name = cursor.getString(0);
                    String reg = cursor.getString(1);
                    int statusInt = cursor.getInt(2);
                    
                    String status = "Unmarked";
                    paint.setColor(android.graphics.Color.GRAY);
                    if (statusInt == 1) {
                        status = "Present";
                        paint.setColor(android.graphics.Color.parseColor("#386a20")); // Greenish
                    } else if (statusInt == 0) {
                        status = "Absent";
                        paint.setColor(android.graphics.Color.parseColor("#ba1a1a")); // Reddish
                    }

                    canvas.drawText(reg, 50, y, paint);
                    canvas.drawText(name, 180, y, paint);
                    canvas.drawText(status, 450, y, paint);

                    y += 30;

                } while (cursor.moveToNext());
                cursor.close();
            }

            document.finishPage(page);

            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "AttendanceExports");
            if (!folder.exists()) folder.mkdirs();
            File file = new File(folder, "Report_" + sectionName.replaceAll("[^a-zA-Z0-9]","_") + "_" + date + ".pdf");
            
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();

            return file;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}