I'll help you understand the Prototype Pattern with detailed examples and explanations.

````markdown:/Users/loCser/Desktop/design-par/design-par.md/prototype-pattern.md
# Prototype Pattern

## 1. What is Prototype Pattern?
The Prototype Pattern is a creational design pattern that allows you to create new objects by cloning an existing object (prototype) instead of creating new instances from scratch. This pattern is particularly useful when object creation is expensive or complex.

## 2. Key Components
- **Prototype**: Interface declaring the cloning method
- **Concrete Prototype**: Class implementing the cloning operation
- **Client**: Creates new objects by asking a prototype to clone itself

## 3. Implementation Examples

### 3.1 Basic Shape Prototype
```java:/Users/loCser/Desktop/design-par/design-par.md/shape/Shape.java
// Prototype Interface
public interface Shape extends Cloneable {
    Shape clone();
    void draw();
}

// Concrete Prototypes
public class Circle implements Shape {
    private int x;
    private int y;
    private String color;

    public Circle(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public Shape clone() {
        return new Circle(x, y, color);
    }

    @Override
    public void draw() {
        System.out.println("Drawing Circle at (" + x + "," + y + ") with color " + color);
    }
}

public class Rectangle implements Shape {
    private int width;
    private int height;
    private String color;

    public Rectangle(int width, int height, String color) {
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public Shape clone() {
        return new Rectangle(width, height, color);
    }

    @Override
    public void draw() {
        System.out.println("Drawing Rectangle with width=" + width + " height=" + height);
    }
}
````

### 3.2 Deep Clone Example

```java:/Users/loCser/Desktop/design-par/design-par.md/document/Document.java
public class Document implements Cloneable {
    private String content;
    private ArrayList<String> comments;

    public Document(String content) {
        this.content = content;
        this.comments = new ArrayList<>();
    }

    @Override
    public Document clone() {
        Document clone = null;
        try {
            clone = (Document) super.clone();
            // Deep clone of mutable state
            clone.comments = new ArrayList<>(this.comments);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
}
```

## 4. Real-World Example - Document Template System

```java:/Users/loCser/Desktop/design-par/design-par.md/template/DocumentTemplate.java
public class DocumentTemplate implements Cloneable {
    private String header;
    private String footer;
    private List<String> sections;
    private Map<String, String> styles;

    // Constructor for template initialization
    public DocumentTemplate() {
        this.sections = new ArrayList<>();
        this.styles = new HashMap<>();
    }

    @Override
    public DocumentTemplate clone() {
        DocumentTemplate clone = null;
        try {
            clone = (DocumentTemplate) super.clone();
            // Deep clone collections
            clone.sections = new ArrayList<>(this.sections);
            clone.styles = new HashMap<>(this.styles);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

    // Template registry for managing different templates
    public static class TemplateRegistry {
        private Map<String, DocumentTemplate> templates = new HashMap<>();

        public void addTemplate(String type, DocumentTemplate template) {
            templates.put(type, template);
        }

        public DocumentTemplate createDocument(String type) {
            DocumentTemplate template = templates.get(type);
            return template != null ? template.clone() : null;
        }
    }
}
```

## 5. When to Use Prototype Pattern

- When creating objects is more expensive than cloning
- When you need to create objects at runtime
- When you want to reduce the number of classes
- When objects have few varying states

## 6. Advantages

1. Reduces the need for creating subclasses
2. Hides complexities of creating objects
3. Provides great flexibility in creating objects
4. Reduces cost of object creation

## 7. Disadvantages

1. Cloning complex objects with circular references might be difficult
2. Implementing clone method can be challenging
3. Deep copy vs shallow copy considerations

## 8. Usage Example

```java:/Users/loCser/Desktop/design-par/design-par.md/example/PrototypeExample.java
public class PrototypeExample {
    public static void main(String[] args) {
        // Create prototype templates
        DocumentTemplate reportTemplate = new DocumentTemplate();
        reportTemplate.setHeader("Company Report");

        DocumentTemplate.TemplateRegistry registry = new DocumentTemplate.TemplateRegistry();
        registry.addTemplate("report", reportTemplate);

        // Create new documents from template
        DocumentTemplate report1 = registry.createDocument("report");
        DocumentTemplate report2 = registry.createDocument("report");

        // Now report1 and report2 are independent copies
    }
}
```

## 9. Best Practices

1. Implement clone() method properly
2. Consider using copy constructors for simple cases
3. Be careful with deep vs shallow copying
4. Use prototype registry for managing prototypes
5. Document cloning behavior clearly
