// ============================================================
// SE 3242: Android Application Development
// Week 1 – Kotlin Essentials
// Exercise 3: Filtering and Transforming Collections
// ============================================================

fun main() {

    val numbers: List<Int?> = listOf(1, null, 3, null, 5, 6, null, 8)

    println("===== Step-by-Step Approach =====\n")

    // ✅ Step 1: Filter out nulls, keep only non-null values
    val nonNulls = numbers.filterNotNull()
    println("Original list        : $numbers")
    println("After filterNotNull(): $nonNulls")

    // ✅ Step 2: Double each remaining number
    val doubled = nonNulls.map { it * 2 }
    println("After map { it * 2 } : $doubled")

    // ✅ Step 3: Sum the doubled values
    val total = doubled.sum()
    println("Sum of doubled values: $total")

    println()
    println("===== One-Liner Challenge =====\n")

    // ✅ One-liner: filterNotNull() → map() → sum() chained together
    val result = numbers.filterNotNull().map { it * 2 }.sum()
    println("Original list : $numbers")
    println("One-liner result (filter → double → sum): $result")
}