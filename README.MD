# Kotlin Starter

## Tips and Tricks

- To handle manually versions of JDKs on Windows, use <https://github.com/shyiko/jabba>
- If the main method isn't inside a class, the `Kt` suffix is required, e.g., `mainClass.set("com.iv127.kotlin.starter.MainKt");` otherwise, if the `main` method is inside the `Main` class, then `mainClass.set("com.iv127.kotlin.starter.Main")`
- run via gradle: `./gradlew run` which will use `main` defined in `application`, see `build.gradle.kts`

## References

- Lilleaas, A. (2023). *Pro Kotlin Web Apps from Scratch*.
