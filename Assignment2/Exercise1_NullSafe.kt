// ============================================================
// SE 3242: Android Application Development
// Week 1 – Kotlin Essentials
// Exercise 1: Null-Safe Data Processing
// ============================================================

data class User(val name: String, val email: String?)

fun main() {

    val users = listOf(
        User("Alex", "alex@example.com"),
        User("Blake", null),
        User("Casey", "casey@work.com")
    )

    println("===== Email Report =====\n")

    // ✅ Requirement 1 & 2:
    // - Print email in UPPERCASE if available, using ?.let
    // - Print "[Name] has no email" if null, using ?:
    users.forEach { user ->
        user.email?.let { email ->
            // 'let' runs this block ONLY if email is not null
            println("${user.name}: ${email.uppercase()}")
        } ?: println("${user.name} has no email")
        //  ↑ Elvis operator: if email is null, run this instead
    }

    println()

    // ✅ Requirement 3:
    // Count users with a valid (non-null) email
    val validEmailCount = users.count { it.email != null }
    println("===== Summary =====")
    println("Users with a valid email: $validEmailCount / ${users.size}")
} 
