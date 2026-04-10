# Markly — Feature List

A premium Android attendance tracking application designed around the "Digital Curator" design system for educators and teachers.

---

## 🏠 Home Screen (Class Overview)

### Class Cards
- Displays all created classes as rich, editorial-style cards.
- Each card shows the **Department**, **Section**, and **Subject** name.
- Auto-generates an **avatar initial** (first letter of department) inside a squircle tile.

### Add Class
- Floating Action Button (FAB) at the bottom right to add a new class.
- Dialog collects **Department**, **Section**, and **Subject** fields.
- On save, immediately opens the **Add Student** flow for that class.

### Delete Class (Long Press)
- Long-pressing any class card triggers a confirmation dialog.
- Upon confirmation, **cascade deletes**:
  - The class itself.
  - All students in the class.
  - All attendance records tied to those students.

### Search Classes
- A magnifying glass icon in the top bar toggles a live search input.
- Instantly filters the class list by **subject name**, **department**, or **section** as you type.
- Tapping the icon again collapses the search bar.

---

## 📋 Attendance Screen (Per Class)

### Header
- Displays the **class title** (e.g. `CA - R1`) and **subject name** as subtitle.
- Back button returns to the home screen.
- Search icon and Export icon are placed together at the top right.

### Horizontal Date Strip
- A smooth horizontal scrollable strip showing all days of the current month.
- Highlights the selected date.
- Automatically scrolls to today's date on open.

### Month Navigation
- Previous (`<`) and Next (`>`) arrow buttons to navigate between months.
- Selecting a new month rebuilds the date strip and reloads student attendance.

### Student List
- Displays all students registered under the class.
- Each card shows:
  - **Avatar initial** (first letter of name) in a squircle tile.
  - **Full name**.
  - **Registration Number / ID**.
  - **Attendance percentage** (overall, across all marked dates for this class).
  - **⚠ LOW** warning badge if attendance drops below **75%**.
  - **Status Pill** on the right showing `P` (Present), `A` (Absent), or `-` (Unmarked) for the selected date.

---

## 👆 Swipe-to-Mark Attendance

- **Swipe Right** on a student card → marks **Present** for the selected date.
  - A green translucent overlay grows behind the card as you swipe.
- **Swipe Left** on a student card → marks **Absent** for the selected date.
  - A red translucent overlay grows behind the card as you swipe.
- The status pill updates instantly after the swipe completes.
- Status is saved persistently to the SQLite database.

---

## 👤 Student Management

### Add Student
- FAB in the Attendance screen opens an Add Student dialog.
- Supports adding multiple students in one session (name + reg no per entry).
- Students are listed in a live preview within the dialog as they are added.

### Edit Student (Long Press)
- Long-pressing any student card opens a **Manage Student** dialog.
- Choose **Edit Details** to modify their name or registration number.
- Changes are saved to the database and the list refreshes immediately.

### Delete Student (Long Press → Delete)
- From the Manage Student dialog, choose **Delete Student**.
- A confirmation prompt appears before permanent deletion.
- **Cascade deletes** all past attendance records for that student.

### Search Students
- Tapping the search icon in the Attendance header reveals a live search bar.
- Filters the student list in real-time by **student name** or **registration number**.

---

## 📊 Attendance Percentage & Warnings

- For each student, the app calculates their **overall attendance percentage** from all marked dates in the class.
- Percentage shown in **green** if ≥ 75%.
- Percentage shown in **red** with a **⚠ LOW** badge if < 75%.
- Displays **"No records yet"** in gray if no attendance has been marked.

---

## 📄 PDF Export & Sharing

- Tap the **export (share) icon** at the top right of the Attendance screen.
- Generates a beautifully styled **A4 PDF report** with:
  - Dark branded header showing **"Markly Attendance Report"** with class and date.
  - Column headers: **Reg No**, **Student Name**, **Status**.
  - **Zebra-striped rows** for readability.
  - Color-coded status text: **green** for Present, **red** for Absent, **gray** for Unmarked.
- Upon generation, the **native Android Share Sheet** opens immediately.
- Allows sharing via **WhatsApp, Email, Slack**, or any installed app.
- File is also saved to `Downloads/AttendanceExports/` on the device.

---

## 🎨 Design System ("Digital Curator")

- Premium editorial aesthetic with soft tonal layering and custom gradients.
- Custom typography using `sans-serif-medium`, `sans-serif-bold`, and `sans-serif-black`.
- Rounded squircle avatar tiles for classes and students.
- Material Design 3 card components with `0dp` elevation for a flat, clean look.
- Consistent color tokens: `primary`, `primary_dim`, `outline`, `surface_container_low`, etc.
- Smooth green/red swipe overlays for attendance gestures.
- Subtle bottom-fade gradient on the home list for scroll depth cues.

---

## 🗄️ Database (SQLite)

Three core tables:

| Table | Columns |
|---|---|
| `section` | `id`, `department`, `section`, `subject` |
| `student` | `id`, `name`, `reg_no`, `section_id` |
| `attendance` | `id`, `student_id`, `section_id`, `date`, `status` |

All deletions are **cascade-safe** — removing a class or student cleans up all related records.

---

## 📱 App Icon

- Custom `logo.png` set as the app launcher icon across all density buckets (`mdpi` → `xxxhdpi`).
- Applied to both standard and round icon slots.

---

*Last updated: April 2026*
