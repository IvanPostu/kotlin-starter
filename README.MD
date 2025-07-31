# Kotlin Starter

142

## Description

A starter Kotlin project.

## Prerequisites

- IntelliJ **detekt** plugin
  - Enable: Settings -> Tools -> detekt
  - <img style="width: 650px" src="./forReadme/img1.png" />
- Disable wildcard imports: Settings -> Editor -> Code Style -> Kotlin
- New line at the end of the file: Settings -> Editor -> General -> Ensure every saved file ends with a line break

## Shell

```sh
# switch java version (available only in nix shell ./app.nix)
switch_java 8|11|17|21

./gradlew :app:run
./gradlew run

# automatically part of the check step
./gradlew detekt

./gradlew ktlintFormat
./gradlew ktlintCheck

# build and run
./gradlew clean build --no-daemon
./gradlew build --no-daemon
java -jar ./app/build/libs/app-1.0-SNAPSHOT-all.jar

# setup env vars
- Set env variable:
$Env:KOTLINBOOK_ENV = production # windows
export KOTLINBOOK_ENV production # linux

docker build -f Dockerfile -t kotlinapp:latest .
docker run -p 9000:8081 --name kotlinapp kotlinapp:latest
docker run -p 9000:8081 --restart=always -d --name kotlinapp kotlinapp:latest
docker rm -f kotlinapp
```

## Info

- To handle manually versions of JDKs on Windows, use <https://github.com/shyiko/jabba>
- If the main method isn't inside a class, the `Kt` suffix is required, e.g., `mainClass.set("com.iv127.kotlin.starter.MainKt");` where `Main` is the name of the source file. Otherwise, if the `main` method is inside the `Main` class, then `mainClass.set("com.iv127.kotlin.starter.Main")`
- run via gradle: `./gradlew run` which will use `main` defined in `application`, see `build.gradle.kts`
- **Platform type** - type that potentially can be null, but bypasses compile-time null-check, e.g. calling java method that returns ref to an object
  - Platform types essentially mean “I'll allow you to pass this thing, which might be
null, to a non-nullable type.”
  - **Kotlin will fail early here as well. Kotlin adds runtime checks to all
variable and property assignments where the type is non-nullable.** Kotlin will throw an error on assignment, so in the end, the variable will indeed be non-nullable.
- **Elvis operator** is a convenient way to specify a default value
when something is `null` or false, e.g. `var test = nullable ?: default`
- For `prod` env if env variable `APPLICATION_HTTP_PORT` is not specified, `com.typesafe.config` will fail
- Nix shell with all settings and env variables; `nix-shell ./app.nix --run bash`
- Prefixing application specific environment variables is a good practice
- Data classes has built-in copy functions
- Kotlin HOFs (high order functions):
  - `also` works as `Stream.map`, but it returns same type and value that it takes
  - `let` method woks like `Stream.map` in java (see `KotlinLetUsageTest`). On nullable is executed ONLY if value is present
  - `fold` works like `reduce` in js or `collect` in java, accumulator can be of any type
  - `use` for classes that implement `Closeable`, in the end calls `close` even if exception is thrown in the block
  - `apply` runs initialization logic after object was created
- A lambda with receiver is a special kind of lambda where you can call methods and access properties of a receiver object directly inside the lambda, without specifying it explicitly, see `com.iv127.kotlin.starter.StringExtensionFunctionTest`
- > benchmarks and real-world
experience have shown that in some cases, you'll get more performance out of your
database if you limit the maximum allowed number of simultaneous connections to
about half the maximum of what the database supports*
- `javax.sql.DataSource` is an abstraction over connection pool
- DB interaction, briefly:
  - create connection or get connection from `DataSource`
  - create statement
  - statement.executeSql
  - process result set
- `Connection` is closeable
  - If the connection was created manually, `close` disconnects it.
  - If the connection was obtained from the connection pool, `close` returns it to the pool.
- **function reference** e.g. `::migrateDataSource`
- Flyway migration name pattern `V{number}__{myFileName).sql`
- Flyway repeatable migration name pattern `R__{myFileName}.sql` which always runs
- > blue/green deployments, meaning that when you
deploy the latest version of your code, you keep the previous version up and running
until the process with your new code has started up and is fully initialized.
- It is important to make migrations backward compatible for blue-green deployments
- **Seed Data** - data that web app
expects to always be in the database
- **Rerunning Failed Migration**
  - Make manual changes
  - To make Flyway believe a migration never ran, update the `flyway_schema_
history` table. Assuming the
migration that failed was V5, run `DELETE FROM flyway_schema_history WHERE
version = 5` to make Flyway rerun the migration from scratch next time on app startup.
- **Manually Performing Migration**
  - Make manual changes
  - `UPDATE flyway_schema_history SET
success = true WHERE version = 5` on startup, Flyway will think that the migration was already run
- Cast from `Long?` to `Long`, e.g. `return userId!!`, if userId is null it will throw NPE
- > "black box tests"
signifying that your tests have as little knowledge as possible about the inner workings of
the system under test
- testing hierarchy: unit, integration, system, acceptance (E2E)
- **Function Type**
  - a type which defines signature of a method, e.g. `val greeter: (String) -> String = { name -> "Hello, $name!" }`
  - analogue of functional interface in java
- **Function type with receiver** is a function definition like `Application.() -> Unit`, where inside the body it is possible to use `this` of the Application type.
- **Extension Function** - invoke the
function as if it were a method defined on the class itself, see `com.iv127.kotlin.starter.StringExtensionFunctionTest`
- The difference between `kotlin.coroutines` and `kotlinx.
coroutines`
  - `kotlin.coroutines` low level kotlin api intended to work with async code and its state
  - `kotlinx.coroutines` more rich functionality for async code built on top of `kotlin.coroutines`
- Kotlin supports operator overloading, e.g. `h1 { text("Hello, World!") }` is the same as `h1 { +"Hello, World" }`
- AWS Lambda Handlers:
  - P228 example of AWS Lambda handler
  - P241 example of AWS Lambda handler, more sophisticated example
  - P244 optimize lambda function
- GraalVM, Kotlin/JS, and Kotlin Native
  - Pros: startup time (no java runtime load on startup)
  - Cons: java libraries such as `Hikari Connection Pool` can not work
- If a `lateinit var` is accessed before it has been set, Kotlin will immediately throw a `kotlin.UninitializedPropertyAccessException`
- Wiring up Spring Security in the servlet environment requires three things:
  - role hierarchy,
  - Spring Security web application context
  - Spring Security filter that's mapped to the servlet.
- `val q = by lazy {...expression that returns something}` a convenient way to defer initialization of a field until it's first accessed

  ```kt
  val value by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {// DEFAULT
      // only one thread will compute
      computeValue()
  }
  val value by lazy(LazyThreadSafetyMode.PUBLICATION) {
    // block can be executed many times by many threads, only first result will be cached
    computeValue()
  }
  val value by lazy(LazyThreadSafetyMode.NONE) {
    // no sync
    computeValue()
  }
  ```

- Inline function - Kotlin compiler copies the body of the function into every location where it is called

- Contracts are ways of telling the type system about
things it cannot infer on its own, but are nevertheless true statements, see `com.iv127.kotlin.starter.KotlinNotNullCustomContractTest`
  - it is possible to make a contract that doesn't hold true in the implementation.

- Sealed class, class hierarchy that is known on compile time
  - Can only be subclassed within the same file where it's declared
  - can be used for `Result` types or state machine implementation
  - example:

    ```kt
    sealed class Result

    data class Success(val data: String) : Result()
    data class Error(val exception: Throwable) : Result()
    object Loading : Result()
    ```

- Destructuring is a language mechanism which allows unpacking an object into multiple variables

  ```kt
  data class User(val name: String, val age: Int)

  val user = User("Alice", 30)
  val (name, age) = user
  ```

- when using locks to coordinate access to a variable, the same lock must be
used wherever that variable is accessed
- server vs. client flags:
  - `java -server -cp yourApp.jar com.example.Main` apply optimizations
  - `java -client -cp yourApp.jar com.example.Main` fast startup

## API Usage

```sh
# main app
curl -i -X GET http://0.0.0.0:8081/ ; echo -e '\n'
curl -i -X GET http://0.0.0.0:8081/public/app.css ; echo -e '\n'
curl -i -X GET http://0.0.0.0:8081/param_test?foo=abc123 ; echo -e '\n'
curl -i -X GET http://0.0.0.0:8081/json_test ; echo -e '\n'
curl -i -X GET http://0.0.0.0:8081/json_test_with_header ; echo -e '\n'
curl -i -X GET http://0.0.0.0:8081/err ; echo -e '\n'
curl -i -X GET http://0.0.0.0:8081/db_test1 ; echo -e '\n'
curl -i -X GET http://0.0.0.0:8081/db_test2 ; echo -e '\n'
curl -i -X GET http://0.0.0.0:8081/single_user ; echo -e '\n'
curl -i -X GET http://0.0.0.0:8081/html_test ; echo -e '\n'
curl -i -X POST http://0.0.0.0:8081/test_json \
  -H "Content-Type: application/json" \
  -d '{"email": "test@gmail.com", "password": "test1234"}' \
  ; echo -e '\n'
curl -i -X POST http://0.0.0.0:8081/jwt_login \
  -d '{"username": "testuser1@gmail.com", "password": "1234"}' \
  ; echo -e '\n' 
curl -i -X GET http://0.0.0.0:8081/jwt_secret \
  -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJteUFwcCIsImlzcyI6Imh0dHA6Ly8wLjAuMC4wOjQyMDciLCJleHAiOjE3NTQ5NTMxMzEsInVzZXJJZCI6MX0.l9-27G2eeqieL5ENkOvorasuIrMZ6wDr7hWPL_NwLFo" \
  ; echo -e '\n'

# fake server
curl -i -X GET http://0.0.0.0:9876/random_number ; echo -e '\n'
curl -i -X GET http://0.0.0.0:9876/ping ; echo -e '\n'
curl -i -X POST http://0.0.0.0:9876/reverse \
  -H "Content-Type: text/plain" \
  -d "This is the raw string body" \
  ; echo -e '\n'

# jooby server
curl -i -X POST http://0.0.0.0:8081/db_test \
  -H "Content-Type: text/plain" \
  -d '{"email": "testuser1@gmail.com"}' \
  ; echo -e '\n'

```

## Observation

- **Thread per request problem**: idle threads, e.g. a request thread is sleeping while it is waiting for a response from the database server or external service
  - **context switch** happens and this thread gets CPU time, but because it is waiting, it will be wasted
  - **thread scheduler** which performs **context switch**, gives CPU time even if the thread is waiting or sleeping

- To fix the idle threads problem, Kotlin has a concept called **suspension**
  - suspends execution of the
code at specific point
  - this thread is used for other tasks
  - when the suspended code is ready to continue running, execution will
continue, potentially on another thread

**Conclusion**: Coroutine is a mechanism which let Kotlin
runtime take control of thread management

- **suspend** can be called from:
  - coroutine context
  - suspend function
- calling `.await()` on `Deferred<T>` type suspends execution, not block

- Coroutines shouldn't contain blocking code
- For blocking logic such as JDBC use `withContext(Dispatchers.IO)` which executes blocking logic in a separate thread pool

- `kotlinx.coroutines` is a library which uses `kotlin.coroutines`, it introduces `suspend` which causes compile time error if you try to call suspend function from a non-coroutine context

- Cross origin request: When JavaScript performs a request to a different domain name, it first sends an
OPTIONS request to that server. If that OPTIONS request responds with the correct CORS headers, the browser will
continue and perform the actual request that was initiated. If not, an error will be raised.

- Example of usage of login api:

  ```js
  fetch("/login", {
    credentials: "include", // without this, it won't receive and send any cookies alongside your request
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded"
    },
    body: new URLSearchParams({
      username: ...,
      password: ...
    })
  })
  ```

- Serverless - cloud computing model where the main unit is a handler or function, a piece that has logic which takes data, process it and returns as a result
  - (like AWS Lambda, Azure Functions, Google Cloud Functions)
  - No server management & automatic scaling
  - Limited execution time
  - **initialization**/**execution**
  - cost is only related to **execution** time
  - **cold**/**warm** start

- Java optimization (disable JIT)
  - set the environment variable `JAVA_TOOL_OPTIONS` to
-XX:+TieredCompilation -XX:TieredStopAtLevel=1, which disables tiered
compilation

- Spring Security requires the servlet specification because it is basically a servlet filter

- Netty doesn't support the servlet specification, so because of it I am using jetty

- Authentication management in Ktor handlers
  - best option, coroutine compatible, `request.javaSecurityPrincipal`
  - thread local approach `SecurityContextHolder.getContext().authentication`, incompatible with coroutines and async code

- Jooby - signs cookie value but doesn't encrypt it
  - Ktor - does both, signing and encryption
- Jooby stores the JWT in the session cookie and does not give control over
how to utilize the token
  - this logic can be written manually with this library for example <https://github.com/auth0/java-jwt>
- What happens if env variable used in `prod.conf` is missing
  - Typesafe lib.:

    ```sh
    APPLICATION_ENV=prod java -jar ./app/build/libs/app-1.0-SNAPSHOT-all.jar
    Exception in thread "main" com.typesafe.config.ConfigException$UnresolvedSubstitution: app-prod.conf @ jar:file:/home/iv127/Projects/kotlin-starter/app/build/libs/app-1.0-SNAPSHOT-all.jar!/app-prod.conf: 1: Could not resolve substitution to a value: ${APPLICATION_HTTP_PORT}
    ```

  - Hoplite lib.:

    ```sh
    Could not parse classpath:/app-prod.conf com.typesafe.config.ConfigException$UnresolvedSubstitution: Reader: 1: Could not resolve substitution to a value: ${APPLICATION_HTTP_PORT}
    ```

- Hoplite has support for secrets through `Masked` type which replaces the value with `****`

- Junit5 alternatives: Spek, Kotest
- Spek framework has intellij plugin (isn't working)
  - Spek is dead, see <https://github.com/spekframework/spek/> **:(**

- Java intrinsic locks aren't reentrant

## TODO

- Tim Lavers (Apress, 2021). *Learn to Program with Kotlin*

- Jib for layered docker images - <https://github.com/GoogleContainerTools/jib>

- manage secrets in k8s <https://github.com/jkroepke/helm-secretsu>

- increase severity for rules in `.editorconfig`

## Nested transaction using kotliquery

Kotliquery doesn't support nested transactions, but it can be achieved through save-points

```kt
private fun webResponseTx(
    dataSource: DataSource,
    handler: suspend PipelineContext<Unit, ApplicationCall>.(
        dbSess: TransactionalSession
    ) -> WebResponse
) = webResponseDb(dataSource) { dbSess ->
    dbSess.transaction { txSess ->
        handler(txSess)
    }
}

fun <A>dbSavePoint(dbSess: Session, body: () -> A): A {
    val sp = dbSess.connection.underlying.setSavepoint()
    return try {
        body().also {
            dbSess.connection.underlying.releaseSavepoint(sp)
        }
    } catch (e: Exception) {
        dbSess.connection.underlying.rollback(sp)
        throw e
    }
}

fun test() {
  sessionOf(dataSource).use { dbSess ->
      dbSess.transaction { txSess ->
          dbSavePoint(txSess) {
              txSess.update(queryOf("INSERT INTO ...")) // main transaction won't fail if this piece fails
          }
      }
  }
}
```

## Access modifiers (Java vs. Kotlin)

| Java           | Kotlin         |
|----------------|----------------|
| public         | public (default) |
| protected      | protected      |
| (no modifier)  | internal       |
| private        | private        |

## Concurrency tips

- Specification - contract
- Invariants - constraints
- Post-condition - effect of an operation
- Thread-safety - situation when a class continues to behave correctly when accessed
from multiple threads
- Raise condition - unpredictable result after execution of a section of code by many threads, e.g.
  - `check-then-act` (e.g. if file doesn't exist, create and write to it)
  - `data-race`(Read-modify-write ) - increment of an int
- Nonatomic 64-bit operations - `Out-of-thin-air safety` - double and long, it is therefore possible to read a nonvolatile long and
get back the high 32 bits of one value and the low 32 bits of another.
- Locking and visibility - `synchronize` guarantees visibility of changes made by thread that entered this block to other thread
  - Only synchronized and volatile guarantees it
- volatile can be used for flags like `isInitialized` or `osShutdown`
- Locking can guarantee both visibility and atomicity; volatile variables can
only guarantee visibility.
- `thread confinement` - If data is only accessed from a single thread,
no synchronization is needed (To make this
easier, Swing provides the invokeLater mechanism to schedule a Runnable for
execution in the event thread.)
  - Another example of thread confinement is jdbc connection pool
  - and `ThreadLocal`
  - local variables (primitives) + copy for reference types
  - Another reason to make a subsystem single-threaded (to use thread confinement) is deadlock avoidance; this is one of the
primary reasons most GUI frameworks are single-threaded
- static assignment and initialization is synchronized by the JVM
- `ConcurrentHashMap` replacement for synchronized `HashMap`
- `ConcurrentSkipListMap` replacement for synchronized `TreeMap`
- `ConcurrentSkipListSet` replacement for synchronized `TreeSet`
- `CopyOnWriteArraySet` replacement for synchronized `Set`
- `CopyOnWriteArrayList` replacement for synchronized `List`
- Why synchronized collections are bad?
  - `HashMap.get` or `List.contains` can be time-consuming, and will force other threads to wait during the operation
- Concurrent collections offer higher throughput with little performance penalty for single-threaded access
- Concurrent collection iterator doesn't throw `ConcurrentModificationException`, it traverses elements as they existed when the iterator was
constructed, and may (but is not guaranteed to) reflect modifications to the collection after the construction of the iterator
- Concurrent collection `isEmpty` and `size()` are not precise
- Blocking queues provide blocking put and take methods as well as the timed
equivalents offer and poll. If the queue is full, put blocks until space becomes
available; if the queue is empty, take blocks until an element is available.
- Bounded(fixed size) vs. unbounded(unlimited) blocking queues
- **`consumer-producer`** pattern - producer threads put items into a **queue** and consumer threads process them
- **`work stealing`** pattern - if a consumer exhausts the work in its own **deque**, it can steal work from the tail of someone else’s **deque**
  - producers can also be consumers
- dequeues: `ArrayDeque`, `LinkedBlockingDeque`
- BlockingQueueS: `LinkedBlockingQueue`, `ArrayBlockingQueue`(FIFO)
  - `PriorityBlockingQueue` (ordered by `Comparable` or `Comparator`, non FIFO)
  - `SynchronousQueue` hand off work, `put` must wait for a `take`
- Interruption
  - **Propagate the `InterruptedException`**
  - **Restore the interrupt** - calling
interrupt on the current thread, so that code higher up the call stack can
see that an interrupt was issued

    ```java
    public class TaskRunnable implements Runnable {
        BlockingQueue<Task> queue;
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Task task = queue.take();
                    processTask(task);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // allow loop condition to become true
                }
            }

            // Now we can clean up and exit gracefully
            System.out.println("Thread was interrupted. Exiting...");
        }
    }
    ```

- Latch is an object which blocks all threads until its final state has been achieved, it works like a gate for threads, once it opens, it can't return any other state
  - `CountDownLatch`

    ```java
    public class TestHarness {
        public long timeTasks(int nThreads, final Runnable task)
                throws InterruptedException {
            final CountDownLatch startGate = new CountDownLatch(1);
            final CountDownLatch endGate = new CountDownLatch(nThreads);
            for (int i = 0; i < nThreads; i++) {
                Thread t = new Thread() {
                    public void run() {
                        try {
                            startGate.await();
                            try {
                                task.run();
                            } finally {
                                endGate.countDown();
                            }
                        } catch (InterruptedException ignored) { }
                    }
                };
                t.start();
            }
            long start = System.nanoTime();
            startGate.countDown();
            endGate.await();
            long end = System.nanoTime();
            return end-start;
        }
    }
    ```

- Preloader example

  ```java
    public class Preloader {
        private final FutureTask<ProductInfo> future =
                new FutureTask<ProductInfo>(new Callable<ProductInfo>() {
                    public ProductInfo call() throws DataLoadException {
                        return loadProductInfo();
                    }
                });
        private final Thread thread = new Thread(future);
        public void start() { thread.start(); }
        public ProductInfo get()
                throws DataLoadException, InterruptedException {
            try {
                return future.get();
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof DataLoadException)
                    throw (DataLoadException) cause;
                else
                    throw launderThrowable(cause);
            }
        }
    }

    /** If the Throwable is an Error, throw it; if it is a
     * RuntimeException return it, otherwise throw IllegalStateException
    */
    public static RuntimeException launderThrowable(Throwable t) {
        if (t instanceof RuntimeException)
            return (RuntimeException) t;
        else if (t instanceof Error)
            throw (Error) t;
        else
            throw new IllegalStateException("Not unchecked", t);
    }
  ```

- `CyclicBarrier` allows a fixed number of threads to rendezvous repeatedly at
a barrier point
  - If a call to await times out or a thread blocked in
await is interrupted, then the barrier is considered broken and all outstanding calls
to await terminate with BrokenBarrierException.
  - `barrier action` a Runnable that is
executed (in one of the subtask threads) when the barrier is successfully passed
but before the blocked threads are released

- Bounded hash set example, works similar to `BlockingQueue`

  ```java
  public class BoundedHashSet<T> {
      private final Set<T> set;
      private final Semaphore sem;
      public BoundedHashSet(int bound) {
          this.set = Collections.synchronizedSet(new HashSet<T>());
          sem = new Semaphore(bound);
      }
      public boolean add(T o) throws InterruptedException {
          sem.acquire();
          boolean wasAdded = false;
          try {
              wasAdded = set.add(o);
              return wasAdded;
          }
          finally {
              if (!wasAdded)
                  sem.release();
          }
      }
      public boolean remove(Object o) {
          boolean wasRemoved = set.remove(o);
          if (wasRemoved)
              sem.release();
          return wasRemoved;
      }
  }
  ```

- Barier is useful is situations when one step can
be done in parallel but all the work associated with a given step must complete
before advancing to the next step

- `Exchanger` - class used to publish safely objects (buffers) between threads

- **memoization** - wrapper that remembers the results of previous computations and encapsulates
the caching process, good example is `LazyLoadingSupplier`

  ```java
  public class Memoizer<A, V> implements Computable<A, V> {
      private final ConcurrentMap<A, Future<V>> cache
              = new ConcurrentHashMap<A, Future<V>>();
      private final Computable<A, V> c;
      public Memoizer(Computable<A, V> c) { this.c = c; }
      public V compute(final A arg) throws InterruptedException {
          while (true) {
              Future<V> f = cache.get(arg);
              if (f == null) {
                  Callable<V> eval = new Callable<V>() {
                      public V call() throws InterruptedException {
                          return c.compute(arg);
                      }
                  };
                  FutureTask<V> ft = new FutureTask<V>(eval);
                  f = cache.putIfAbsent(arg, ft);
                  if (f == null) { f = ft; ft.run(); }
              }
              try {
                  return f.get();
              } catch (CancellationException e) {
                  cache.remove(arg, f);
              } catch (ExecutionException e) {
                  throw launderThrowable(e.getCause());
              }
          }
      }
  }
  ```

- **Network congestion** - a traffic jam, but for data which happens when too many devices or users are trying to send data through a network at the same time

- `-Xss` jvm flag, stack size of a thread default value vary between 512KB and 1MB depending on java version (ignoring OS thread limitation the less this value is the more threads we can crete)
  - `-Xss512k` 512 KB
  - `-Xss2m` 2 MB
- Two main factors in web applications
  - Responsiveness
  - Throughput

- Any thread per task approach should be replaced with thread pool usage

## References

- Lilleaas, August (2023). *Pro Kotlin Web Apps from Scratch*.
- Tim Lavers (Apress, 2021). *Learn to Program with Kotlin*
- <https://codezup.com/dockerizing-kotlin-application-step-by-step-guide/>
- <https://flywaydb.org/>
- <https://www.baeldung.com/kotlin/detekt-static-code-analysis>
- <https://hofstede-matheus.medium.com/improve-code-quality-with-ktlint-detekt-and-git-hooks-d173722594e4>
- <https://jwt.io/>
