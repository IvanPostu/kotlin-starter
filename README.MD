# Kotlin Starter

## Description

A toy project that is a template for my Kotlin projects.

## Info

- To handle manually versions of JDKs on Windows, use <https://github.com/shyiko/jabba>
- If the main method isn't inside a class, the `Kt` suffix is required, e.g., `mainClass.set("com.iv127.kotlin.starter.MainKt");` where `Main` is the name of the source file. Otherwise, if the `main` method is inside the `Main` class, then `mainClass.set("com.iv127.kotlin.starter.Main")`
- run via gradle: `./gradlew run` which will use `main` defined in `application`, see `build.gradle.kts`
- **Platform type** - type that potentially can be null, but bypasses compile-time null-check, e.g. calling java method that returns ref to an object
  - Platform types essentially mean “I’ll allow you to pass this thing, which might be
null, to a non-nullable type.”
  - **Kotlin will fail early here as well. Kotlin adds runtime checks to all
variable and property assignments where the type is non-nullable.** Kotlin will throw an error on assignment, so in the end, the variable will indeed be non-nullable.
- Kotlin `let` method woks like `Stream.map` in java (see `com.iv127.kotlin.starter.KotlinLetUsageTest`)
- Kotlin `let` on nullable is executed ONLY if value is present
- **Elvis operator** is a convenient way to specify a default value
when something is `null` or false, e.g. `var test = nullable ?: default`
- For `prod` env if env variable `APPLICATION_HTTP_PORT` is not specified, `com.typesafe.config` will fail
- Nix shell with all settings and env variables; `nix-shell ./app.nix --run bash`
- Prefixing application specific environment variables is a good practice
- Data classes has built-in copy functions
- Kotlin `fold` works like `reduce` in js or `collect` in java, accumulator can be of any type
- **Extension Function** - invoke the
function as if it were a method defined on the class itself, see `com.iv127.kotlin.starter.StringExtensionFunctionTest`
- A lambda with receiver is a special kind of lambda where you can call methods and access properties of a receiver object directly inside the lambda, without specifying it explicitly, see `com.iv127.kotlin.starter.StringExtensionFunctionTest`
- > benchmarks and real-world
experience have shown that in some cases, you’ll get more performance out of your
database if you limit the maximum allowed number of simultaneous connections to
about half the maximum of what the database supports — *Lilleaas, A. (2023). Pro Kotlin Web Apps from Scratch*
- `javax.sql.DataSource` is an abstraction over connection pool
- DB interaction, briefly:
  - create connection or get connection from `DataSource`
  - create statement
  - statement.executeSql
  - process result set
- `Connection` is closeable
  - If the connection was created manually, `close` disconnects it.
  - If the connection was obtained from the connection pool, `close` returns it to the pool.
- > `use()` has the added benefit of wrapping the
whole operation in a try/catch block so that you close the connection even if control flow
breaks due to your code throwing an exception. — *Lilleaas, A. (2023). Pro Kotlin Web Apps from Scratch*

## API Usage

```sh
curl -i -X GET http://0.0.0.0:8081/ ; echo -e '\n'

curl -i -X GET http://0.0.0.0:8081/param_test?foo=abc123 ; echo -e '\n'

curl -i -X GET http://0.0.0.0:8081/json_test ; echo -e '\n'

curl -i -X GET http://0.0.0.0:8081/json_test_with_header ; echo -e '\n'

curl -i -X GET http://0.0.0.0:8081/err ; echo -e '\n'
```

## References

- Lilleaas, A. (2023). *Pro Kotlin Web Apps from Scratch*.
- <https://kotlinlang.org/docs/sealed-classes.html>
- <https://codezup.com/dockerizing-kotlin-application-step-by-step-guide/>
- <https://flywaydb.org/>
