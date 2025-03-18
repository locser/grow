Would you like me to explain any specific aspect of the Proxy Pattern in more detail? Or would you like to see more examples in different contexts?

# Proxy Pattern

## 1. What is Proxy Pattern?

The Proxy Pattern provides a surrogate or placeholder for another object to control access to it. It creates a representative object that controls access to another object, which may be remote, expensive to create, or in need of securing.

## 2. Real-Life Analogy

Think of these real-world examples:

- A credit card is a proxy for your bank account
- A check is a proxy for cash
- A security guard is a proxy for access control to a building

## 3. Types of Proxies

1. **Virtual Proxy**: Delays creation of expensive objects
2. **Protection Proxy**: Controls access to the original object
3. **Remote Proxy**: Represents objects in different address spaces
4. **Cache Proxy**: Stores results of expensive operations

## 4. Example Implementation

### 4.1 Virtual Proxy - Image Loading Example

```java:/Users/loCser/Desktop/design-par/design-par.md/ImageProxy.java
// Subject Interface
interface Image {
    void display();
}

// Real Subject
class RealImage implements Image {
    private String filename;

    public RealImage(String filename) {
        this.filename = filename;
        loadImageFromDisk();
    }

    private void loadImageFromDisk() {
        System.out.println("Loading image: " + filename);
    }

    public void display() {
        System.out.println("Displaying image: " + filename);
    }
}

// Proxy
class ImageProxy implements Image {
    private RealImage realImage;
    private String filename;

    public ImageProxy(String filename) {
        this.filename = filename;
    }

    public void display() {
        if (realImage == null) {
            realImage = new RealImage(filename);
        }
        realImage.display();
    }
}
```

### 4.2 Protection Proxy - Document Access Example

```java:/Users/loCser/Desktop/design-par/design-par.md/DocumentProxy.java
// Subject Interface
interface Document {
    void view();
    void edit();
}

// Real Subject
class RealDocument implements Document {
    private String content;

    public void view() {
        System.out.println("Viewing document");
    }

    public void edit() {
        System.out.println("Editing document");
    }
}

// Protection Proxy
class DocumentProxy implements Document {
    private RealDocument document;
    private String userRole;

    public DocumentProxy(String userRole) {
        this.document = new RealDocument();
        this.userRole = userRole;
    }

    public void view() {
        document.view();
    }

    public void edit() {
        if (userRole.equals("ADMIN")) {
            document.edit();
        } else {
            System.out.println("Access denied: Editing requires admin privileges");
        }
    }
}
```

## 5. Real-World Application - Database Connection Proxy

```java:/Users/loCser/Desktop/design-par/design-par.md/DatabaseProxy.java
// Subject Interface
interface Database {
    void query(String sql);
    void connect();
}

// Real Subject
class RealDatabase implements Database {
    public void connect() {
        System.out.println("Connecting to database...");
    }

    public void query(String sql) {
        System.out.println("Executing query: " + sql);
    }
}

// Cache Proxy
class DatabaseProxy implements Database {
    private RealDatabase database;
    private Map<String, Object> queryCache;

    public DatabaseProxy() {
        this.queryCache = new HashMap<>();
    }

    public void connect() {
        if (database == null) {
            database = new RealDatabase();
            database.connect();
        }
    }

    public void query(String sql) {
        if (queryCache.containsKey(sql)) {
            System.out.println("Returning cached result for: " + sql);
            return;
        }

        connect();
        database.query(sql);
        // Cache the result
        queryCache.put(sql, "result");
    }
}
```

## 6. When to Use Proxy Pattern

- When you need lazy initialization (Virtual Proxy)
- When you need access control (Protection Proxy)
- When you need local execution of a remote service (Remote Proxy)
- When you need to add logging, caching, or request validation

## 7. Advantages

1. Security through controlled access
2. Improved performance through caching
3. Reduced memory usage through lazy loading
4. Separation of concerns

## 8. Disadvantages

1. Increased complexity
2. Possible response delays
3. Additional layer of indirection

## 9. Common Use Cases in Modern Development

1. Object-Relational Mapping (ORM) systems
2. Spring AOP (Aspect-Oriented Programming)
3. Web Service proxies
4. Lazy loading in frameworks
5. Caching systems
6. Access control systems

```

```
