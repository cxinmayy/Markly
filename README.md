<p align="center">
  <img src="https://github.com/cxinmayy/Markly/blob/main/logo.png" width="120" style="border-radius: 20px;" />
</p>

<h1 align="center">Markly</h1>

<p align="center">
  <b>Elegant Attendance Management for Teachers & Educators</b><br>
  <sub>Classes • Students • Attendance • Simplicity</sub>
</p>


# Markly

### *Premium Attendance Tracker for Teachers*

Markly is a refined Android app designed for **teachers and educators** to manage **classes, students, and attendance** with clarity, precision, and ease. Built with a modern “Digital Curator” design philosophy, it focuses on simplicity, elegance, and efficiency.

---

## ✨ Overview

Markly transforms everyday attendance workflows into a smooth and organized experience. With thoughtful design and a structured system, it helps educators stay focused while managing classes and tracking student attendance effortlessly.

---

## 📸 Screenshots

> *(Add your screenshots inside a `/screenshots` folder in your repo)*

### 🏠 Home Screen

![Home Screen](screenshots/home.png)

### 📋 Attendance Screen

![Attendance Screen](screenshots/attendance.png)

### 👤 Student Management

![Student Management](screenshots/student.png)

### 📄 PDF Export

![PDF Export](screenshots/pdf.png)

---

## 🧩 Core Features

### 🏠 Class Management

* Create and organize classes with department, section, and subject
* Smart class cards with clean visual hierarchy
* Instant search across all classes
* Cascade-safe deletion (removes students + attendance)

---

### 📋 Attendance System

* Mark attendance with intuitive swipe gestures
* Monthly date navigation with smooth scrolling
* Real-time status updates (Present / Absent / Unmarked)
* Persistent storage using SQLite

---

### 👤 Student Management

* Add multiple students quickly
* Edit or delete student records anytime
* Live search by name or registration number

---

### 📊 Insights & Warnings

* Automatic attendance percentage calculation
* Visual indicators for low attendance (<75%)

---

### 📄 Export & Sharing

* Generate beautifully formatted A4 PDF reports
* Share instantly via any app
* Files saved locally on device

---

## 🎨 Design System — *Digital Curator*

* Minimal, editorial-style interface
* Smooth animations and swipe feedback
* Material Design 3 with soft tonal layering
* Squircle avatars and clean typography

---

## 🗄️ Database Structure (SQLite)

| Table        | Fields                                   |
| ------------ | ---------------------------------------- |
| `section`    | id, department, section, subject         |
| `student`    | id, name, reg_no, section_id             |
| `attendance` | id, student_id, section_id, date, status |

✔ Cascade-safe relationships
✔ Optimized for performance

---

## 📱 Tech Stack

* **Android (Java/Kotlin)**
* **SQLite Database**
* **Material Design 3**

---

## 🚀 Why Markly?

Markly is not just about tracking attendance — it’s about creating a system that feels effortless, reliable, and thoughtfully designed for educators.

---

## 📌 Status

> Actively maintained
> Last updated: April 2026

---



