Let's start with a clear understanding of SOLID principles:

1. **S - Single Responsibility Principle (SRP)**
   - Theory: A class should have only one reason to change
   - Example of violation:

```java:src/BadExample.java
class UserManager {
    public void createUser(User user) {
        // Save user to database
        database.save(user);
        // Send welcome email
        emailService.sendWelcomeEmail(user);
        // Log the action
        logger.log("User created: " + user.getId());
    }
}
```

- Fixed version:

```java:src/GoodExample.java
class UserManager {
    private EmailService emailService;
    private Logger logger;

    public void createUser(User user) {
        database.save(user);
    }
}

class UserNotificationService {
    public void sendWelcomeEmail(User user) {
        emailService.sendWelcomeEmail(user);
    }
}

class UserActivityLogger {
    public void logUserCreation(User user) {
        logger.log("User created: " + user.getId());
    }
}
```

2. **O - Open/Closed Principle**
   - Theory: Software entities should be open for extension but closed for modification
   - Example of violation:

```java:src/BadExample.java
class PaymentProcessor {
    public void processPayment(String type, double amount) {
        if (type.equals("credit")) {
            processCreditPayment(amount);
        } else if (type.equals("debit")) {
            processDebitPayment(amount);
        }
        // Adding new payment type requires modifying this class
    }
}
```

- Fixed version:

```java:src/GoodExample.java
interface PaymentMethod {
    void processPayment(double amount);
}

class CreditPayment implements PaymentMethod {
    public void processPayment(double amount) {
        // Process credit payment
    }
}

class DebitPayment implements PaymentMethod {
    public void processPayment(double amount) {
        // Process debit payment
    }
}

// New payment methods can be added without modifying existing code
class CryptoPayment implements PaymentMethod {
    public void processPayment(double amount) {
        // Process crypto payment
    }
}
```

I'll continue explaining the remaining SOLID principles (Liskov Substitution, Interface Segregation, and Dependency Inversion) with examples.

Let me add these principles to your file:

3. **L - Liskov Substitution Principle**
   - Theory: Objects of a superclass should be replaceable with objects of its subclasses without breaking the application
   - Example of violation:

```java:src/BadExample.java
class Bird {
    public void fly() {
        // Implementation for flying
    }
}

class Penguin extends Bird {
    @Override
    public void fly() {
        throw new UnsupportedOperationException("Penguins can't fly!");
    }
}
```

- Fixed version:

```java:src/GoodExample.java
interface FlyingBird {
    void fly();
}

interface SwimmingBird {
    void swim();
}

class Sparrow implements FlyingBird {
    public void fly() {
        // Implementation for flying
    }
}

class Penguin implements SwimmingBird {
    public void swim() {
        // Implementation for swimming
    }
}
```

4. **I - Interface Segregation Principle**
   - Theory: Clients should not be forced to depend on interfaces they don't use
   - Example of violation:

```java:src/BadExample.java
interface Worker {
    void work();
    void eat();
    void sleep();
}

class Robot implements Worker {
    public void work() {
        // Robot can work
    }

    public void eat() {
        // Robots don't eat!
        throw new UnsupportedOperationException();
    }

    public void sleep() {
        // Robots don't sleep!
        throw new UnsupportedOperationException();
    }
}
```

- Fixed version:

```java:src/GoodExample.java
interface Workable {
    void work();
}

interface Eatable {
    void eat();
}

interface Sleepable {
    void sleep();
}

class Human implements Workable, Eatable, Sleepable {
    public void work() { /* implementation */ }
    public void eat() { /* implementation */ }
    public void sleep() { /* implementation */ }
}

class Robot implements Workable {
    public void work() { /* implementation */ }
}
```

5. **D - Dependency Inversion Principle**
   - Theory: High-level modules should not depend on low-level modules. Both should depend on abstractions.
   - Example of violation:

```java:src/BadExample.java
class LightBulb {
    public void turnOn() {
        // Turn on the bulb
    }

    public void turnOff() {
        // Turn off the bulb
    }
}

class Switch {
    private LightBulb bulb;

    public Switch() {
        bulb = new LightBulb(); // Direct dependency on LightBulb
    }

    public void operate() {
        // Switch logic
    }
}
```

- Fixed version:

```java:src/GoodExample.java
interface Switchable {
    void turnOn();
    void turnOff();
}

class LightBulb implements Switchable {
    public void turnOn() {
        // Turn on the bulb
    }

    public void turnOff() {
        // Turn off the bulb
    }
}

class Fan implements Switchable {
    public void turnOn() {
        // Turn on the fan
    }

    public void turnOff() {
        // Turn off the fan
    }
}

class Switch {
    private Switchable device;

    public Switch(Switchable device) {
        this.device = device; // Dependency injection
    }

    public void operate() {
        // Switch logic using device interface
    }
}
```
