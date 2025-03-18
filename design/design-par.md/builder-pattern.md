I'll help you understand the Builder Pattern with detailed examples and explanations.

````markdown:/Users/loCser/Desktop/design-par/design-par.md/builder-pattern.md
# Builder Pattern

## 1. What is Builder Pattern? (Extended)
The Builder Pattern is a creational design pattern that:
- Separates the construction of a complex object from its representation
- Allows the same construction process to create different representations
- Solves the "telescoping constructor" problem
- Particularly useful when dealing with objects that require multiple steps in their construction

## 2. Key Components (Detailed)

### 2.1 Builder Interface/Abstract Class
- Declares creation steps common to all builders
- Usually includes methods for building parts
- Declares method to retrieve the final product

### 2.2 Concrete Builder
- Provides specific implementation of construction steps
- Keeps track of the product being built
- Implements a method to retrieve the product
- Can provide additional methods specific to particular product

### 2.3 Director
- Constructs object using the builder pattern
- Works with any builder that follows the interface
- Hides construction details from the client
- Optional but useful for reusing construction code

### 2.4 Product
- Complex object being built
- Can have different representations depending on builder
- Should not expose setter methods ideally
- Can be mutable or immutable


## 3. Implementation Examples

### 3.1 Basic Computer Builder
```java:/Users/loCser/Desktop/design-par/design-par.md/computer/ComputerBuilder.java
// Product
class Computer {
    private String CPU;
    private String RAM;
    private String storage;
    private String GPU;

    public void setCPU(String cpu) { this.CPU = cpu; }
    public void setRAM(String ram) { this.RAM = ram; }
    public void setStorage(String storage) { this.storage = storage; }
    public void setGPU(String gpu) { this.GPU = gpu; }

    @Override
    public String toString() {
        return "Computer [CPU=" + CPU + ", RAM=" + RAM +
               ", Storage=" + storage + ", GPU=" + GPU + "]";
    }
}

// Builder Interface
interface ComputerBuilder {
    void buildCPU(String cpu);
    void buildRAM(String ram);
    void buildStorage(String storage);
    void buildGPU(String gpu);
    Computer getResult();
}

// Concrete Builder
class GamingComputerBuilder implements ComputerBuilder {
    private Computer computer;

    public GamingComputerBuilder() {
        computer = new Computer();
    }

    public void buildCPU(String cpu) {
        computer.setCPU(cpu);
    }

    public void buildRAM(String ram) {
        computer.setRAM(ram);
    }

    public void buildStorage(String storage) {
        computer.setStorage(storage);
    }

    public void buildGPU(String gpu) {
        computer.setGPU(gpu);
    }

    public Computer getResult() {
        return computer;
    }
}

// Director
class ComputerAssembler {
    private ComputerBuilder builder;

    public ComputerAssembler(ComputerBuilder builder) {
        this.builder = builder;
    }

    public void constructGamingPC() {
        builder.buildCPU("Intel i9");
        builder.buildRAM("32GB RGB");
        builder.buildStorage("2TB NVMe SSD");
        builder.buildGPU("RTX 4080");
    }
}
````

### 3.2 Modern Builder with Method Chaining

```java:/Users/loCser/Desktop/design-par/design-par.md/modern/ModernBuilder.java
public class Pizza {
    private final String dough;
    private final String sauce;
    private final String topping;

    private Pizza(PizzaBuilder builder) {
        this.dough = builder.dough;
        this.sauce = builder.sauce;
        this.topping = builder.topping;
    }

    public static class PizzaBuilder {
        private String dough;
        private String sauce;
        private String topping;

        public PizzaBuilder() {}

        public PizzaBuilder dough(String dough) {
            this.dough = dough;
            return this;
        }

        public PizzaBuilder sauce(String sauce) {
            this.sauce = sauce;
            return this;
        }

        public PizzaBuilder topping(String topping) {
            this.topping = topping;
            return this;
        }

        public Pizza build() {
            return new Pizza(this);
        }
    }
}
```

## 4. Real-World Example - Document Builder

```java:/Users/loCser/Desktop/design-par/design-par.md/document/DocumentBuilder.java
public class Document {
    private String header;
    private String content;
    private String footer;
    private List<String> styles;
    private Map<String, String> metadata;

    private Document(DocumentBuilder builder) {
        this.header = builder.header;
        this.content = builder.content;
        this.footer = builder.footer;
        this.styles = builder.styles;
        this.metadata = builder.metadata;
    }

    public static class DocumentBuilder {
        private String header;
        private String content;
        private String footer;
        private List<String> styles = new ArrayList<>();
        private Map<String, String> metadata = new HashMap<>();

        public DocumentBuilder content(String content) {
            this.content = content;
            return this;
        }

        public DocumentBuilder header(String header) {
            this.header = header;
            return this;
        }

        public DocumentBuilder footer(String footer) {
            this.footer = footer;
            return this;
        }

        public DocumentBuilder addStyle(String style) {
            this.styles.add(style);
            return this;
        }

        public DocumentBuilder addMetadata(String key, String value) {
            this.metadata.put(key, value);
            return this;
        }

        public Document build() {
            return new Document(this);
        }
    }
}
```

## 5. Usage Example

```java:/Users/loCser/Desktop/design-par/design-par.md/example/BuilderExample.java
public class BuilderExample {
    public static void main(String[] args) {
        // Using the modern builder
        Pizza pizza = new Pizza.PizzaBuilder()
            .dough("thin")
            .sauce("tomato")
            .topping("cheese")
            .build();

        // Using the document builder
        Document document = new Document.DocumentBuilder()
            .header("Title")
            .content("Main content")
            .footer("Page 1")
            .addStyle("bold")
            .addMetadata("author", "John Doe")
            .build();
    }
}
```

## 6. When to Use Builder Pattern (Extended)

### 6.1 Complex Object Creation

- Object requires multiple steps to create
- Object has many optional parameters
- Need to enforce certain construction order

### 6.2 Immutability Requirements

- When building immutable objects
- When object's state shouldn't change after construction
- When thread safety is important

### 6.3 Configuration Scenarios

- Creating objects with many configuration options
- Building objects that require extensive setup
- When default values need to be handled gracefully

## 7. Advantages (Detailed)

### 7.1 Construction Control

- Step-by-step construction process
- Ability to vary internal representation
- Fine-grained control over construction

### 7.2 Code Maintainability

- Encapsulates construction code
- Promotes Single Responsibility Principle
- Makes complex construction process readable

### 7.3 Flexibility

- Same construction process for different representations
- Easy to add new features to products
- Supports product variations

### 7.4 Parameter Control

- Handles optional parameters elegantly
- Eliminates need for numerous constructors
- Makes invalid states impossible

## 8. Disadvantages (Detailed)

### 8.1 Code Complexity

- More classes and objects to maintain
- Requires separate builder for each type
- Initial setup can be verbose

### 8.2 Performance Considerations

- Creates multiple objects
- Memory overhead from builder objects
- Slight performance impact from indirect construction

### 8.3 Design Challenges

- Deciding when to use Director
- Determining appropriate granularity of steps
- Managing builder lifecycle

## 9. Best Practices (Extended)

### 9.1 Design Guidelines

- Make builder's setters return builder instance
- Use meaningful method names for building steps
- Consider making product class immutable
- Validate object state in build method

### 9.2 Implementation Tips

```java:%2FUsers%2FloCser%2FDesktop%2Fdesign-par%2Fdesign-par.md%2Fbest-practices%2FBuilderExample.java
public class Product {
    // Required parameters
    private final String required1;
    private final String required2;

    // Optional parameters
    private final String optional1;
    private final String optional2;

    private Product(Builder builder) {
        // Required parameters
        this.required1 = Objects.requireNonNull(builder.required1);
        this.required2 = Objects.requireNonNull(builder.required2);

        // Optional parameters with defaults
        this.optional1 = builder.optional1 != null ? builder.optional1 : "default1";
        this.optional2 = builder.optional2 != null ? builder.optional2 : "default2";
    }

    public static class Builder {
        // Required parameters
        private final String required1;
        private final String required2;

        // Optional parameters with defaults
        private String optional1;
        private String optional2;

        public Builder(String required1, String required2) {
            this.required1 = required1;
            this.required2 = required2;
        }

        public Builder optional1(String value) {
            optional1 = value;
            return this;
        }

        public Builder optional2(String value) {
            optional2 = value;
            return this;
        }

        public Product build() {
            // Validation logic here
            validateState();
            return new Product(this);
        }

        private void validateState() {
            // Add validation logic
            if (optional1 != null && optional1.isEmpty()) {
                throw new IllegalStateException("optional1 if set, cannot be empty");
            }
        }
    }
}
```
