I'll help you understand the Singleton Pattern with detailed examples and explanations.

# Singleton Pattern

## 1. What is Singleton Pattern?

Singleton is a creational design pattern that ensures a class has only one instance and provides a global point of access to that instance.

## 2. Key Characteristics

- Private constructor to prevent direct instantiation
- Private static instance of the class
- Public static method to get the instance
- Thread-safe consideration (for multi-threaded environments)

## 3. Implementation Examples

### 3.1 Basic Singleton (Not Thread-Safe)

```java:/Users/loCser/Desktop/design-par/design-par.md/basic/BasicSingleton.java
public class BasicSingleton {
    private static BasicSingleton instance;

    // Private constructor
    private BasicSingleton() {}

    public static BasicSingleton getInstance() {
        if (instance == null) {
            instance = new BasicSingleton();
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("Hello from Singleton!");
    }
}
```

### 3.2 Thread-Safe Singleton (Double-Checked Locking)

```java:/Users/loCser/Desktop/design-par/design-par.md/threadsafe/ThreadSafeSingleton.java
public class ThreadSafeSingleton {
    private static volatile ThreadSafeSingleton instance;

    private ThreadSafeSingleton() {}

    public static ThreadSafeSingleton getInstance() {
        if (instance == null) {
            synchronized (ThreadSafeSingleton.class) {
                if (instance == null) {
                    instance = new ThreadSafeSingleton();
                }
            }
        }
        return instance;
    }
}
```

### 3.3 Eager Initialization

```java:/Users/loCser/Desktop/design-par/design-par.md/eager/EagerSingleton.java
public class EagerSingleton {
    private static final EagerSingleton instance = new EagerSingleton();

    private EagerSingleton() {}

    public static EagerSingleton getInstance() {
        return instance;
    }
}
```

## 4. Real-World Example - Database Connection Manager

```java:/Users/loCser/Desktop/design-par/design-par.md/database/DatabaseConnection.java
public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            // Initialize database connection
            String url = "jdbc:mysql://localhost:3306/mydb";
            String username = "user";
            String password = "password";
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database");
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
```

## 5. Common Use Cases

1. Database connections
2. Configuration managers
3. Logging services
4. Thread pools
5. Caching
6. Device drivers

## 6. Best Practices

### 6.1 Enum Singleton (Java)

```java:/Users/loCser/Desktop/design-par/design-par.md/enum/EnumSingleton.java
public enum EnumSingleton {
    INSTANCE;

    private Connection dbConnection;

    EnumSingleton() {
        // Initialize resources
    }

    public void doSomething() {
        // Singleton behavior
    }
}
```

### 6.2 Static Block Initialization

```java:/Users/loCser/Desktop/design-par/design-par.md/static/StaticSingleton.java
public class StaticSingleton {
    private static StaticSingleton instance;

    private StaticSingleton() {}

    static {
        try {
            instance = new StaticSingleton();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating singleton instance");
        }
    }

    public static StaticSingleton getInstance() {
        return instance;
    }
}
```

## 7. Advantages

1. Ensures single instance
2. Global access point
3. Lazy initialization option
4. Easy to implement

## 8. Disadvantages

1. Can make unit testing difficult
2. Can be seen as an anti-pattern
3. Global state can be dangerous
4. Multi-threading concerns

## 9. When to Use

- When exactly one instance is needed
- When global state is required
- When resource sharing is needed
- When you need strict control over global variables

## 10. Testing Example

```java:/Users/loCser/Desktop/design-par/design-par.md/test/SingletonTest.java
public class SingletonTest {
    public static void main(String[] args) {
        // Test thread-safe singleton
        Runnable task = () -> {
            ThreadSafeSingleton singleton = ThreadSafeSingleton.getInstance();
            System.out.println("Singleton hash: " + singleton.hashCode());
        };

        // Create multiple threads
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();
    }
}
```
