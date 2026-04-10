<p align="center">
  <img src="https://github.com/cxinmayy/Markly/blob/main/logo.png" width="120" style="border-radius: 20px;" />
</p>

<h1 align="center">Markly</h1>

<p align="center">
  <b>Elegant Attendance Management for Teachers and Educators</b><br>
  <sub>Classes • Students • Attendance • Simplicity</sub>
</p>

# Markly

### Premium Attendance Tracker for Teachers

Markly is a refined Android application designed for teachers and educators to manage classes, students, and attendance with clarity, precision, and ease. Built with a modern “Digital Curator” design philosophy, it emphasizes simplicity, elegance, and efficiency.

## Overview

> A structured and intuitive system that simplifies everyday attendance workflows.

Markly enables educators to stay focused while managing classes and tracking student attendance effortlessly.

## Screenshots

> Key views and interactions from the application

### Home Screen

<p align="center">
  <img src="https://raw.githubusercontent.com/cxinmayy/Markly/main/screenshots/dashboard.jpeg" width="250"/>
</p>

### Attendance Screen

<p align="center">
  <img src="https://raw.githubusercontent.com/cxinmayy/Markly/main/screenshots/attendance.jpeg" width="250"/>
</p>

### Swipe Action (Present / Absent)

> Gesture-based attendance marking with immediate visual feedback

<p align="center">
  <img src="https://raw.githubusercontent.com/cxinmayy/Markly/main/screenshots/swipe0.jpeg" width="250"/>
  <img src="https://raw.githubusercontent.com/cxinmayy/Markly/main/screenshots/swipe%20absent.jpeg" width="250"/>
</p>

### PDF Export Interface

<p align="center">
  <img src="https://raw.githubusercontent.com/cxinmayy/Markly/main/screenshots/attendancereport.jpeg" width="250"/>
</p>

### Sidebar Navigation

<p align="center">
  <img src="https://raw.githubusercontent.com/cxinmayy/Markly/main/screenshots/sidebar.jpeg" width="250"/>
</p>

## Core Features

### Class Management

- Create and organize classes with department, section, and subject  
- Clean and structured class cards  
- Instant search across all classes  
- Safe deletion with associated data handling  

### Attendance System

- Swipe-based attendance marking  
- Smooth date navigation across months  
- Real-time status updates  
- Local persistence using SQLite  

### Student Management

- Add, edit, and delete student records  
- Search by name or registration number  

### Insights

- Automatic attendance percentage calculation  
- Visual indication for low attendance  

### Export

- Generate A4 PDF reports  
- Share reports easily  
- Local file storage

### Backup and Restore

> Securely save and recover your data when needed.

- Backup all classes, students, and attendance records  
- Restore data seamlessly without loss  
- Designed for reliability and easy migration  

## Design System

> Built with clarity, minimalism, and consistency at its core.

- Material Design 3 principles  
- Smooth animations and interaction feedback  
- Clean typography and layout structure  

## Database Structure (SQLite)

| Table      | Fields                                   |
|------------|------------------------------------------|
| section    | id, department, section, subject         |
| student    | id, name, reg_no, section_id             |
| attendance | id, student_id, section_id, date, status |

> Optimized structure with safe relationships and efficient data handling

## Tech Stack

- Android (Java/Kotlin)  
- SQLite Database  
- Material Design 3  

## Why Markly

> Designed to feel simple, reliable, and effortless in everyday use.

Markly focuses on usability and structure, helping educators manage attendance without unnecessary complexity.

## Status

> Actively maintained  
> Last updated: April 2026  

## Support

<p align="center">
  <a href="https://buymeachai.ezee.li/cxinmayy">
    <img src="https://buymeachai.ezee.li/assets/images/buymeachai-button.png" />
  </a>
</p>
