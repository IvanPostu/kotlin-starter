# Kotlin Starter

## Tips and Tricks

- To handle manually versions of JDKs on Windows, use <https://github.com/shyiko/jabba>
- If the main method isn't inside a class, the `Kt` suffix is required, e.g., `mainClass.set("com.iv127.kotlin.starter.MainKt");` otherwise, if the `main` method is inside the `Main` class, then `mainClass.set("com.iv127.kotlin.starter.Main")`
- run via gradle: `./gradlew run` which will use `main` defined in `application`, see `build.gradle.kts`
- **Platform type** - type that potentially can be null, but bypasses compile-time null-check, e.g. calling java method that returns ref to an object
  - Platform types essentially mean “I’ll allow you to pass this thing, which might be
null, to a non-nullable type.”
  - **Kotlin will fail early here as well. Kotlin adds runtime checks to all
variable and property assignments where the type is non-nullable.** Kotlin will throw an error on assignment, so in the end, the variable will indeed be non-nullable.

## References

- Lilleaas, A. (2023). *Pro Kotlin Web Apps from Scratch*.
