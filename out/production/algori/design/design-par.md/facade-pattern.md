Would you like me to explain any specific aspect of the Facade Pattern in more detail? Or would you like to see more examples in different contexts?

# Facade Pattern

## 1. What is Facade Pattern?

The Facade Pattern is a structural design pattern that provides a simplified interface to a complex system of classes, library, or framework. It acts as a "front-facing" interface masking more complex underlying code.

## 2. Real-Life Analogy

Think of these real-world examples:

- A restaurant waiter (facade) who simplifies your interaction with the complex kitchen system
- Car ignition button (facade) that hides complex engine startup processes
- Computer power button (facade) masking complex boot sequence

## 3. Structure

- **Facade**: Provides simplified interface
- **Complex Subsystem**: Set of complex classes
- **Client**: Uses the facade to interact with subsystem

## 4. Example Implementation

### 4.1 Basic Example - Home Theater System

```HomeTheater.java
// Complex subsystem classes
class TV {
    public void turnOn() {
        System.out.println("TV is ON");
    }

    public void setInput(String input) {
        System.out.println("Setting TV input to: " + input);
    }
}

class SoundSystem {
    public void turnOn() {
        System.out.println("Sound System is ON");
    }

    public void setVolume(int volume) {
        System.out.println("Setting volume to: " + volume);
    }
}

class StreamingPlayer {
    public void turnOn() {
        System.out.println("Streaming Player is ON");
    }

    public void play(String movie) {
        System.out.println("Playing: " + movie);
    }
}

// Facade
class HomeTheaterFacade {
    private TV tv;
    private SoundSystem sound;
    private StreamingPlayer player;

    public HomeTheaterFacade() {
        tv = new TV();
        sound = new SoundSystem();
        player = new StreamingPlayer();
    }

    public void watchMovie(String movie) {
        System.out.println("=== Starting movie night ===");
        tv.turnOn();
        tv.setInput("HDMI");
        sound.turnOn();
        sound.setVolume(20);
        player.turnOn();
        player.play(movie);
    }
}
```

### 4.2 Usage Example

```java:/Users/loCser/Desktop/design-par/design-par.md/Main.java
public class Main {
    public static void main(String[] args) {
        HomeTheaterFacade homeTheater = new HomeTheaterFacade();
        // Simple one-line call instead of multiple complex operations
        homeTheater.watchMovie("The Matrix");
    }
}
```

## 5. Real-World Application - Computer System

```java:/Users/loCser/Desktop/design-par/design-par.md/ComputerSystem.java
// Complex subsystem classes
class CPU {
    public void freeze() { System.out.println("CPU: Freezing..."); }
    public void jump(long position) { System.out.println("CPU: Jumping to position " + position); }
    public void execute() { System.out.println("CPU: Executing..."); }
}

class Memory {
    public void load(long position, String data) {
        System.out.println("Memory: Loading data at position " + position);
    }
}

class HardDrive {
    public String read(long lba, int size) {
        System.out.println("HardDrive: Reading data from sector " + lba);
        return "data";
    }
}

// Facade
class ComputerFacade {
    private CPU cpu;
    private Memory memory;
    private HardDrive hardDrive;

    public ComputerFacade() {
        this.cpu = new CPU();
        this.memory = new Memory();
        this.hardDrive = new HardDrive();
    }

    public void start() {
        System.out.println("=== Starting Computer ===");
        cpu.freeze();
        memory.load(0, hardDrive.read(0, 1024));
        cpu.jump(0);
        cpu.execute();
    }
}
```

## 6. When to Use Facade Pattern

- When you need to provide a simple interface to a complex subsystem
- When you want to layer your subsystems
- When there are many dependencies between clients and implementation classes
- When you want to structure a subsystem into layers

## 7. Advantages

1. Isolates clients from subsystem components
2. Promotes weak coupling
3. Simplifies complex systems
4. Provides a unified interface to a set of interfaces

## 8. Disadvantages

1. Facade can become a god object coupled to all classes
2. May introduce unnecessary abstraction
3. Can hide useful complexity that some clients might need

## 9. Common Use Cases in Modern Development

1. SDK and API wrappers
2. Database access layers
3. Complex system configurations
4. Service integration layers
5. Legacy system modernization

```

```
