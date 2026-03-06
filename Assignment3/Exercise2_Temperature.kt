// ============================================================
// SE 3242: Android Application Development
// Week 1 – Kotlin Essentials
// Exercise 2: Temperature Descriptions
// ============================================================

// ✅ Main function: returns a description based on temperature
// Parameter is nullable (Int?) to handle missing data
fun describeTemperature(temp: Int?): String = when {
    temp == null  -> "No data"    // null check FIRST, before any comparison
    temp <= 0     -> "Freezing"
    temp in 1..15 -> "Cold"
    temp in 16..25 -> "Mild"
    temp in 26..35 -> "Warm"
    temp in 36..45 -> "Hot"
    else          -> "Extreme"   // temp > 45
}

fun main() {

    println("===== Single Value Tests =====\n")

    // Testing individual values
    println("Temp:  null -> ${describeTemperature(null)}")
    println("Temp:    -5 -> ${describeTemperature(-5)}")
    println("Temp:     0 -> ${describeTemperature(0)}")
    println("Temp:    10 -> ${describeTemperature(10)}")
    println("Temp:    20 -> ${describeTemperature(20)}")
    println("Temp:    30 -> ${describeTemperature(30)}")
    println("Temp:    40 -> ${describeTemperature(40)}")
    println("Temp:    50 -> ${describeTemperature(50)}")

    println()

    // ✅ BONUS: List of temperatures (some null), loop + when without argument
    println("===== Bonus: Temperature List =====\n")

    val temperatures: List<Int?> = listOf(0, 10, null, 20, 30, null, 40, 50, -3)

    temperatures.forEach { temp ->
        // Displays "null" cleanly when temp is null using ?:
        val label = temp?.toString() ?: "null"
        println("Temp: ${label.padStart(4)} -> ${describeTemperature(temp)}")
    }
}