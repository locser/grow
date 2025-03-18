Would you like me to explain any specific aspect of the Simple Factory Pattern in more detail? Or would you like to see more examples in different contexts?

# Simple Factory Pattern

## 1. What is Simple Factory Pattern?

The Simple Factory Pattern is a creational pattern that provides an interface for creating objects without exposing the object creation logic to the client. It encapsulates the object creation process in a single class.

## 2. Real-Life Analogy

Think of these real-world examples:

- A restaurant kitchen receiving orders and creating different dishes
- A car dealership creating different types of vehicles
- A document generator creating different file formats (PDF, DOC, TXT)

## 3. Structure

- **Factory**: Creates objects based on input parameters
- **Products**: Objects created by the factory
- **Client**: Uses the factory to create objects

## 4. Example Implementation

### 4.1 Basic Example - Document Generator

```java:/Users/loCser/Desktop/design-par/design-par.md/DocumentFactory.java
// Product Interface
interface Document {
    void open();
    void save();
}

// Concrete Products
class PDFDocument implements Document {
    public void open() {
        System.out.println("Opening PDF document");
    }

    public void save() {
        System.out.println("Saving PDF document");
    }
}

class WordDocument implements Document {
    public void open() {
        System.out.println("Opening Word document");
    }

    public void save() {
        System.out.println("Saving Word document");
    }
}

// Simple Factory
class DocumentFactory {
    public static Document createDocument(String type) {
        switch (type.toLowerCase()) {
            case "pdf":
                return new PDFDocument();
            case "word":
                return new WordDocument();
            default:
                throw new IllegalArgumentException("Unknown document type");
        }
    }
}
```

### 4.2 Usage Example

```java:/Users/loCser/Desktop/design-par/design-par.md/Main.java
public class Main {
    public static void main(String[] args) {
        // Create PDF document
        Document pdfDoc = DocumentFactory.createDocument("pdf");
        pdfDoc.open();
        pdfDoc.save();

        // Create Word document
        Document wordDoc = DocumentFactory.createDocument("word");
        wordDoc.open();
        wordDoc.save();
    }
}
```

## 5. Real-World Application - Payment Method Factory

```java:/Users/loCser/Desktop/design-par/design-par.md/PaymentFactory.java
// Payment Interface
interface PaymentMethod {
    void processPayment(double amount);
    void refund(double amount);
}

// Concrete Payment Methods
class CreditCardPayment implements PaymentMethod {
    public void processPayment(double amount) {
        System.out.println("Processing credit card payment: $" + amount);
    }

    public void refund(double amount) {
        System.out.println("Refunding to credit card: $" + amount);
    }
}

class PayPalPayment implements PaymentMethod {
    public void processPayment(double amount) {
        System.out.println("Processing PayPal payment: $" + amount);
    }

    public void refund(double amount) {
        System.out.println("Refunding to PayPal: $" + amount);
    }
}

class CryptoPayment implements PaymentMethod {
    public void processPayment(double amount) {
        System.out.println("Processing crypto payment: $" + amount);
    }

    public void refund(double amount) {
        System.out.println("Refunding to crypto wallet: $" + amount);
    }
}

// Payment Factory
class PaymentFactory {
    public static PaymentMethod createPaymentMethod(String type) {
        switch (type.toLowerCase()) {
            case "credit":
                return new CreditCardPayment();
            case "paypal":
                return new PayPalPayment();
            case "crypto":
                return new CryptoPayment();
            default:
                throw new IllegalArgumentException("Unknown payment type");
        }
    }
}
```

## 6. When to Use Simple Factory Pattern

- When object creation involves complex logic
- When you want to encapsulate object creation in one place
- When you need to create different objects based on conditions
- When you want to decouple object creation from object usage

## 7. Advantages

1. Encapsulation of object creation logic
2. Separation of concerns
3. Easy to extend with new product types
4. Centralized control over object creation

## 8. Disadvantages

1. Single Responsibility Principle violation (if factory grows too large)
2. Can't change object creation behavior without modifying factory
3. May lead to large conditional statements

## 9. Common Use Cases

1. Database connection factories
2. UI element creators
3. File format handlers
4. Payment processing systems
5. Configuration object creators

## 10. Best Practices Example

```java:/Users/loCser/Desktop/design-par/design-par.md/BestPracticesFactory.java
// Using Enum for type safety
enum DocumentType {
    PDF, WORD, EXCEL
}

class ModernDocumentFactory {
    private static final Map<DocumentType, Supplier<Document>> CREATORS = new HashMap<>();

    static {
        CREATORS.put(DocumentType.PDF, PDFDocument::new);
        CREATORS.put(DocumentType.WORD, WordDocument::new);
        CREATORS.put(DocumentType.EXCEL, ExcelDocument::new);
    }

    public static Document createDocument(DocumentType type) {
        Supplier<Document> creator = CREATORS.get(type);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown document type");
        }
        return creator.get();
    }
}
```
