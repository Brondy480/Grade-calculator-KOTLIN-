# 📊 Student Grade Calculator
> **SE 3242 – Android Application Development**  
> Assignment 1 | ICT University, Yaoundé, Cameroon  
> **Author:** Brondy Noumsi | **Partner:** Eskil16

---

## 📋 Table of Contents
1. [Project Overview](#-project-overview)
2. [Features](#-features)
3. [Screenshots](#-screenshots)
4. [Architecture Diagram](#-architecture-diagram)
5. [Kotlin Concepts Used](#-kotlin-concepts-used)
6. [Code Documentation](#-code-documentation)
7. [Setup Instructions](#-setup-instructions)
8. [User Guide](#-user-guide)
9. [Project Structure](#-project-structure)

---

## 🎯 Project Overview

The **Student Grade Calculator** is a Kotlin-based Android application built with **Jetpack Compose**. It allows students to enter their subject marks and automatically calculates their average score, assigns a letter grade (A–F), and displays a visual result with color-coded feedback.

This project demonstrates core Kotlin and Android development concepts including:
- `data class` for structured data modelling
- **Nullable inputs** handled with safe calls and Elvis operator
- `when` expressions for grade logic
- **Jetpack Compose** for modern declarative UI

---

## ✨ Features

| Feature | Description |
|--------|-------------|
| 📝 Dynamic subjects | Add up to 8 subjects with custom names |
| 🔢 Nullable inputs | Handles missing or invalid marks gracefully |
| 📊 Grade calculation | Computes average and assigns A/B/C/D/F grade |
| 🎨 Color-coded result | Green for pass, red for fail, animated display |
| 📜 History | Keeps last 5 calculations with timestamps |
| ✅ Live validation | Real-time feedback on invalid inputs |

---

## 📸 Screenshots

> **How to add screenshots:**
> 1. Run the app on your emulator or phone
> 2. Take a screenshot (in emulator: camera icon on the side panel)
> 3. Save to a `screenshots/` folder in this repo
> 4. Replace the placeholder paths below with your actual images

| Home Screen | Result Screen | History Screen |
|-------------|---------------|----------------|
| ![Home](screenshots/home.png) | ![Result](screenshots/result.png) | ![History](screenshots/history.png) |

> 📌 *To add real screenshots: create a `screenshots/` folder in Assignment1/, drop your images there, then update the paths above.*

---

## 🏗️ Architecture Diagram

```
┌─────────────────────────────────────────────────┐
│                  MainActivity.kt                 │
│         (Entry point – hosts Compose UI)         │
└─────────────────────┬───────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│              GradeCalculatorScreen               │
│         (Jetpack Compose UI – Stateful)          │
│                                                  │
│  ┌─────────────┐        ┌────────────────────┐  │
│  │ SubjectInput│        │   ResultDisplay    │  │
│  │  Component  │        │    Component       │  │
│  └──────┬──────┘        └────────┬───────────┘  │
└─────────┼──────────────────────┼────────────────┘
          │                      │
          ▼                      ▼
┌─────────────────┐   ┌──────────────────────────┐
│  data class     │   │   data class             │
│  Student        │   │   GradeResult            │
│                 │   │                          │
│  - name: String │   │  - average: Double?      │
│  - subject1:    │   │  - grade: String?        │
│    Double?      │   │  - color: Color          │
│  - subject2:    │   │  - label: String         │
│    Double?      │   └──────────────────────────┘
│  - subject3:    │
│    Double?      │             ▼
└─────────────────┘
                      ┌──────────────────────────┐
                      │     Grade Logic          │
                      │   (when expression)      │
                      │                          │
                      │  score >= 90 → "A"       │
                      │  score >= 80 → "B"       │
                      │  score >= 70 → "C"       │
                      │  score >= 60 → "D"       │
                      │  else       → "F"        │
                      └──────────────────────────┘
```

### Data Flow
```
User Input (marks)
      │
      ▼
toDoubleOrNull()  ──── null? ──── show error
      │
      ▼
Student data class (nullable marks)
      │
      ▼
calculateAverage() using Elvis ?: 0.0
      │
      ▼
when(average) → GradeResult
      │
      ▼
Compose UI recomposes with result
```

---

## 🧠 Kotlin Concepts Used

### 1. `data class`
```kotlin
// Automatically generates equals(), hashCode(), toString(), copy()
data class Student(
    val name: String,
    val subject1: Double?,   // nullable mark
    val subject2: Double?,
    val subject3: Double?
)

data class GradeResult(
    val average: Double?,    // nullable — null if no valid marks
    val grade: String?,
    val color: Color,
    val label: String
)
```

### 2. Nullable Types & Safe Calls `?.`
```kotlin
// toDoubleOrNull() returns null if input is not a valid number
val mark = textInput.toDoubleOrNull()

// Safe call: only accesses .let if score is not null
student.subject1?.let { score ->
    validMarks.add(score)
}
```

### 3. Elvis Operator `?:`
```kotlin
// If average is null, display "N/A" instead of crashing
val display = result.average?.toString() ?: "N/A"

// If grade is null, show fallback
val gradeDisplay = result.grade ?: "N/A"
```

### 4. `when` Expression for Grades
```kotlin
// when used as an expression — returns a value
fun getGradeLetter(average: Double): String = when {
    average >= 90 -> "A"
    average >= 80 -> "B"
    average >= 70 -> "C"
    average >= 60 -> "D"
    else          -> "F"
}

// when also used for color coding
fun getGradeColor(grade: String): Color = when (grade) {
    "A"  -> Color(0xFF4CAF50)   // Green
    "B"  -> Color(0xFF8BC34A)   // Light Green
    "C"  -> Color(0xFFFFC107)   // Amber
    "D"  -> Color(0xFFFF9800)   // Orange
    else -> Color(0xFFF44336)   // Red
}
```

### 5. Edge Case Handling
```kotlin
// Elvis handles the case where no valid marks were entered
val average = if (validMarks.isEmpty()) null
              else validMarks.average()

// Safe display using let
result.average?.let { avg ->
    Text("Average: ${"%.1f".format(avg)}%")
} ?: Text("No valid marks entered")
```

---

## 📖 Code Documentation

### `MainActivity.kt`
| Responsibility | Details |
|---------------|---------|
| Entry point | Hosts the Jetpack Compose content |
| Theme setup | Applies `CampusMarketTheme` |
| Screen host | Renders `GradeCalculatorScreen()` |

### `data class Student`
| Property | Type | Description |
|----------|------|-------------|
| `name` | `String` | Student's full name |
| `subject1` | `Double?` | Mark for subject 1 (nullable) |
| `subject2` | `Double?` | Mark for subject 2 (nullable) |
| `subject3` | `Double?` | Mark for subject 3 (nullable) |

### `data class GradeResult`
| Property | Type | Description |
|----------|------|-------------|
| `average` | `Double?` | Computed average (null if no valid marks) |
| `grade` | `String?` | Letter grade A–F (null if no result) |
| `color` | `Color` | UI color representing performance |
| `label` | `String` | Text label e.g. "Excellent", "Failing" |

### Key Functions
| Function | Returns | Description |
|----------|---------|-------------|
| `getGradeLetter(avg)` | `String` | Maps average to A/B/C/D/F |
| `getGradeColor(grade)` | `Color` | Maps grade to a display color |
| `getGradeLabel(grade)` | `String` | Maps grade to descriptive label |
| `calculateResult(student)` | `GradeResult` | Full grade computation |

---

## ⚙️ Setup Instructions

### Prerequisites
| Tool | Version |
|------|---------|
| Android Studio | Hedgehog or later |
| Kotlin | 1.9+ |
| Min SDK | API 24 (Android 7.0) |
| Target SDK | API 35 |
| JDK | 11 or 17 |

### Steps to Run

**1. Clone the repository**
```bash
git clone https://github.com/Brondy480/Grade-calculator-KOTLIN-.git
cd Grade-calculator-KOTLIN-
```

**2. Open in Android Studio**
```
File → Open → navigate to Assignment1/GradeCalculator
```

**3. Sync Gradle**
```
Click "Sync Now" in the yellow banner
Wait for BUILD SUCCESSFUL
```

**4. Run the app**
```
Connect a device or start an emulator
Press the ▶️ Run button (Shift+F10)
```

---

## 📱 User Guide

### How to calculate a grade

**Step 1 — Enter student name**
- Type the student's full name in the Name field

**Step 2 — Enter subject marks**
- Enter marks for each subject (0–100)
- Marks must be valid numbers
- Leave a field empty if a subject has no mark — it will be ignored automatically

**Step 3 — Tap "Calculate"**
- The app computes the average of all valid marks
- A letter grade is assigned based on the scale below

**Step 4 — Read the result**
- The result card shows average, letter grade, and a label
- Color indicates performance level

### Grade Scale

| Score Range | Grade | Label |
|------------|-------|-------|
| 90 – 100 | **A** | Excellent |
| 80 – 89 | **B** | Good |
| 70 – 79 | **C** | Average |
| 60 – 69 | **D** | Below Average |
| Below 60 | **F** | Failing |

### Edge Cases Handled
| Situation | App Behaviour |
|-----------|--------------|
| Empty mark field | Field is ignored, not counted |
| Non-numeric input | Shown as invalid, not counted |
| All fields empty | Displays "No valid marks entered" |
| Single valid mark | Uses that mark as the average |

---

## 📁 Project Structure

```
Assignment1/
├── README.md                          ← this file
├── screenshots/                       ← add your screenshots here
│   ├── home.png
│   ├── result.png
│   └── history.png
└── GradeCalculator/
    └── app/src/main/java/
        └── com/example/gradecalculator/
            ├── MainActivity.kt        ← entry point
            └── ui/
                └── GradeCalculatorScreen.kt  ← all UI + logic
```

---

## 👥 Team

| Member | Role | Repository |
|--------|------|-----------|
| Brondy Noumsi | Kotlin / Android | [Grade-calculator-KOTLIN-](https://github.com/Brondy480/Grade-calculator-KOTLIN-) |
| Eskil16 | Dart / Flutter | *(partner repo link)* |

---

## 📄 License
This project is submitted as part of **SE 3242 – Android Application Development** at **ICT University, Cameroon**.

---
*Last updated: March 2026*
