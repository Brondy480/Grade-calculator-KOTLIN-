package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────────────────────────────────────
// REQUIREMENT 1: data class
// Automatically generates equals(), hashCode(), toString(), copy()
// ─────────────────────────────────────────────────────────────────────────────

data class Student(
    val name: String,
    val subject1: Double?,   // REQUIREMENT 2: nullable — mark may not be entered
    val subject2: Double?,
    val subject3: Double?
)

data class GradeResult(
    val student: Student,
    val average: Double?,    // nullable — null when inputs are missing/invalid
    val grade: String?,      // nullable — null when average cannot be computed
    val gradeColor: Color,
    val gradeLabel: String
)

// ─────────────────────────────────────────────────────────────────────────────
// REQUIREMENT 3: when expression for grade logic
// ─────────────────────────────────────────────────────────────────────────────

fun getGradeLetter(average: Double?): String {
    return when {
        average == null  -> "N/A"
        average >= 90.0  -> "A"
        average >= 80.0  -> "B"
        average >= 70.0  -> "C"
        average >= 60.0  -> "D"
        else             -> "F"
    }
}

fun getGradeColor(grade: String?): Color {
    return when (grade) {
        "A"  -> Color(0xFF00E5A0)
        "B"  -> Color(0xFF4FC3F7)
        "C"  -> Color(0xFFFFD54F)
        "D"  -> Color(0xFFFF8A65)
        "F"  -> Color(0xFFFF5252)
        else -> Color(0xFF888888)
    }
}

fun getGradeLabel(grade: String?): String {
    return when (grade) {
        "A"  -> "Excellent 🏆"
        "B"  -> "Good ⭐"
        "C"  -> "Average 📚"
        "D"  -> "Below Average ⚠️"
        "F"  -> "Failing ❌"
        else -> "Enter marks above"
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Core calculation — demonstrates Elvis (?:) and safe calls (?.)
// ─────────────────────────────────────────────────────────────────────────────

fun calculateResult(student: Student): GradeResult {

    // REQUIREMENT 4: Elvis operator (?:) — fallback to 0.0 if null
    val s1 = student.subject1 ?: 0.0
    val s2 = student.subject2 ?: 0.0
    val s3 = student.subject3 ?: 0.0

    // All three marks must be provided (non-null) and within valid range
    val allProvided = student.subject1 != null &&
            student.subject2 != null &&
            student.subject3 != null

    val allInRange = s1 in 0.0..100.0 &&
            s2 in 0.0..100.0 &&
            s3 in 0.0..100.0

    // REQUIREMENT 4: average is null if inputs are invalid (safe nullable result)
    val average: Double? = if (allProvided && allInRange) (s1 + s2 + s3) / 3.0 else null

    val grade = getGradeLetter(average)

    return GradeResult(
        student    = student,
        average    = average,
        grade      = grade,
        gradeColor = getGradeColor(grade),
        gradeLabel = getGradeLabel(grade)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Activity
// ─────────────────────────────────────────────────────────────────────────────

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF080808)) {
                    GradeCalculatorScreen()
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun GradeCalculatorScreen() {

    var studentName by remember { mutableStateOf("") }
    var input1 by remember { mutableStateOf("") }
    var input2 by remember { mutableStateOf("") }
    var input3 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<GradeResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        // REQUIREMENT 2: toDoubleOrNull() returns null if input is not a valid number
        val mark1: Double? = input1.toDoubleOrNull()
        val mark2: Double? = input2.toDoubleOrNull()
        val mark3: Double? = input3.toDoubleOrNull()

        // REQUIREMENT 4: Elvis — use "Student" if name field is blank
        val name = studentName.trim().ifEmpty { "Student" }

        // Check for out-of-range values using safe calls
        val hasRangeError = listOfNotNull(mark1, mark2, mark3).any { it !in 0.0..100.0 }

        if (hasRangeError) {
            errorMessage = "Marks must be between 0 and 100"
            result = null
            return
        }

        errorMessage = null

        // Build the data class — marks stay nullable
        val student = Student(
            name     = name,
            subject1 = mark1,   // null if user left field empty or typed letters
            subject2 = mark2,
            subject3 = mark3
        )

        result = calculateResult(student)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        // Title
        Text("Grade Calculator", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        Text(
            "data class  ·  nullable  ·  when  ·  Elvis",
            fontSize = 11.sp, color = Color(0xFF444444), letterSpacing = 1.sp
        )

        Spacer(Modifier.height(6.dp))

        // Inputs
        GradeTextField(value = studentName, onValueChange = { studentName = it },
            label = "Student Name (optional)", keyboardType = KeyboardType.Text)
        GradeTextField(value = input1, onValueChange = { input1 = it }, label = "Subject 1  (0 – 100)")
        GradeTextField(value = input2, onValueChange = { input2 = it }, label = "Subject 2  (0 – 100)")
        GradeTextField(value = input3, onValueChange = { input3 = it }, label = "Subject 3  (0 – 100)")

        // Error — REQUIREMENT 4: safe call ?.let — only shown when non-null
        errorMessage?.let {
            Text(it, color = Color(0xFFFF5252), fontSize = 13.sp)
        }

        // Calculate button
        Button(
            onClick = { calculate() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5A0))
        ) {
            Text("Calculate", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        // Result — REQUIREMENT 4: safe call ?.let — only rendered when non-null
        AnimatedVisibility(visible = result != null, enter = fadeIn() + expandVertically()) {
            result?.let { res -> ResultCard(res) }
        }

        // Grade reference bar
        GradeReferenceCard()

        Spacer(Modifier.height(16.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Result Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ResultCard(result: GradeResult) {

    val animatedAvg by animateFloatAsState(
        targetValue = result.average?.toFloat() ?: 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "avg"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = result.gradeColor.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, result.gradeColor.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // REQUIREMENT 4: Elvis — display name or fallback
            Text(
                text = result.student.name.ifEmpty { "Student" },
                color = Color(0xFF888888), fontSize = 13.sp
            )

            // Animated arc gauge
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(130.dp)) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(130.dp)) {
                    val stroke = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    drawArc(color = Color(0xFF1A1A1A), startAngle = 135f, sweepAngle = 270f, useCenter = false, style = stroke)
                    drawArc(color = result.gradeColor, startAngle = 135f, sweepAngle = 270f * (animatedAvg / 100f), useCenter = false, style = stroke)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // REQUIREMENT 4: Elvis ?: — show "N/A" when grade is null
                    Text(
                        text = result.grade ?: "N/A",
                        color = result.gradeColor, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold
                    )
                    // REQUIREMENT 4: safe call ?.let — format only when non-null, Elvis for fallback
                    Text(
                        text = result.average?.let { "%.2f%%".format(it) } ?: "Invalid input",
                        color = Color(0xFF666666), fontSize = 13.sp
                    )
                }
            }

            Text(result.gradeLabel, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

            // Subject marks row — shows "–" via Elvis when mark is null
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(
                    "Subject 1" to result.student.subject1,
                    "Subject 2" to result.student.subject2,
                    "Subject 3" to result.student.subject3
                ).forEach { (label, mark) ->
                    SubjectPill(
                        label = label,
                        // REQUIREMENT 4: Elvis ?: — show "–" if mark is null
                        value = mark?.let { "%.1f".format(it) } ?: "–",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectPill(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF111111))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(10.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color(0xFF555555), fontSize = 10.sp)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Grade Reference Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun GradeReferenceCard() {
    val grades = listOf(
        Triple("A", "≥ 90", Color(0xFF00E5A0)),
        Triple("B", "≥ 80", Color(0xFF4FC3F7)),
        Triple("C", "≥ 70", Color(0xFFFFD54F)),
        Triple("D", "≥ 60", Color(0xFFFF8A65)),
        Triple("F", "< 60", Color(0xFFFF5252)),
    )
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0D0D0D),
        border = BorderStroke(1.dp, Color(0xFF1A1A1A))
    ) {
        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            grades.forEach { (letter, range, color) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(letter, color = color, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Text(range, color = Color(0xFF444444), fontSize = 10.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable TextField
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun GradeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Number
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFF555555), fontSize = 13.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF00E5A0),
            unfocusedBorderColor = Color(0xFF222222),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color(0xFF00E5A0),
            focusedLabelColor = Color(0xFF00E5A0),
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    )
}
