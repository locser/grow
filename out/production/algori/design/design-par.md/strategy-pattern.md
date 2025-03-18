Would you like me to explain any specific aspect of the Strategy Pattern in more detail? Or would you like to see more examples in different contexts?

# Strategy Pattern

## 1. What is Strategy Pattern?

The Strategy Pattern is a behavioral design pattern that enables selecting an algorithm's implementation at runtime. It defines a family of algorithms, encapsulates each one, and makes them interchangeable.

## 2. Real-Life Analogy

Think of these real-world examples:

- Different payment methods (Credit Card, PayPal, Cash) in an e-commerce system
- Various navigation routes (Car, Bike, Walking) in a GPS app
- Multiple sorting algorithms for different data sizes

## 3. Structure

- **Context**: The class that uses the strategy
- **Strategy**: The interface common to all strategies
- **Concrete Strategies**: Specific implementations of the strategy

## 4. Example Implementation

### 4.1 Basic Example - Payment System

```java:/Users/loCser/Desktop/design-par/design-par.md/PaymentStrategy.java
// Strategy Interface
interface PaymentStrategy {
    void pay(int amount);
}

// Concrete Strategies
class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    private String name;

    public CreditCardPayment(String cardNumber, String name) {
        this.cardNumber = cardNumber;
        this.name = name;
    }

    @Override
    public void pay(int amount) {
        System.out.println(amount + " paid with credit card " + cardNumber);
    }
}

class PayPalPayment implements PaymentStrategy {
    private String email;

    public PayPalPayment(String email) {
        this.email = email;
    }

    @Override
    public void pay(int amount) {
        System.out.println(amount + " paid using PayPal account " + email);
    }
}

// Context
class ShoppingCart {
    private PaymentStrategy paymentStrategy;

    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }

    public void checkout(int amount) {
        paymentStrategy.pay(amount);
    }
}
```

### 4.2 Usage Example

```java:/Users/loCser/Desktop/design-par/design-par.md/Main.java
public class Main {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();

        // Pay with Credit Card
        cart.setPaymentStrategy(new CreditCardPayment("1234-5678", "John Doe"));
        cart.checkout(100);

        // Pay with PayPal
        cart.setPaymentStrategy(new PayPalPayment("john@example.com"));
        cart.checkout(200);
    }
}
```

## 5. Real-World Application - Compression Strategy

```java:/Users/loCser/Desktop/design-par/design-par.md/CompressionStrategy.java
// Strategy Interface
interface CompressionStrategy {
    void compressFiles(List<File> files);
}

// Concrete Strategies
class ZipCompression implements CompressionStrategy {
    @Override
    public void compressFiles(List<File> files) {
        System.out.println("Compressing files using ZIP");
        // ZIP compression logic
    }
}

class RarCompression implements CompressionStrategy {
    @Override
    public void compressFiles(List<File> files) {
        System.out.println("Compressing files using RAR");
        // RAR compression logic
    }
}

class TarGzCompression implements CompressionStrategy {
    @Override
    public void compressFiles(List<File> files) {
        System.out.println("Compressing files using TAR.GZ");
        // TAR.GZ compression logic
    }
}

// Context
class FileCompressor {
    private CompressionStrategy strategy;

    public void setCompressionStrategy(CompressionStrategy strategy) {
        this.strategy = strategy;
    }

    public void createArchive(List<File> files) {
        strategy.compressFiles(files);
    }
}
```

## 6. When to Use Strategy Pattern

- When you need different variants of an algorithm
- When you have a lot of similar classes that only differ in their behavior
- When you need to isolate the algorithm from the code that uses the algorithm
- When a class defines many behaviors that appear as multiple conditional statements

## 7. Advantages

1. Algorithms can be changed at runtime
2. Isolation of algorithm implementation details from code that uses the algorithm
3. Promotes composition over inheritance
4. Easy to add new strategies without changing existing code

## 8. Disadvantages

1. Increased number of objects in the application
2. Clients must be aware of different strategies
3. Communication overhead between strategy and context

## 9. Common Use Cases in Modern Development

1. Data compression
2. Sorting algorithms
3. Payment processing systems
4. Authentication strategies
5. Route calculation in navigation apps
6. File export formats (PDF, Excel, CSV)

```

```
