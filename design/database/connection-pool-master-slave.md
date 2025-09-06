_Ahem_ Xin chào mọi người! Hôm nay chúng ta sẽ nói về MySQL Pool Cluster - một topic mà tôi tin chắc 80% các bạn đã từng gặp performance issues với nó rồi đúng không? 😄

## **MySQL Pool Cluster - The Real Talk**

### **Connection Pool là gì và tại sao chúng ta cần nó?**

Hãy tưởng tượng bạn có một cái bar, mỗi lần có khách đến uống, bạn phải:

1. Mở cửa cho khách vào (TCP handshake)
2. Kiểm tra ID (authentication)
3. Dọn bàn (prepare connection)
4. Phục vụ đồ uống (execute query)
5. Dọn dẹp và đóng cửa (close connection)

**Connection pool** giống như bạn có sẵn 10-20 bàn đã setup sẵn, khách đến chỉ cần ngồi và uống thôi. Boom! Performance tăng vọt.

### **Connection Pool vs Connection Pool Cluster**

```javascript
// Single Pool - Như có 1 bar duy nhất
const pool = mysql.createPool({
  host: "localhost",
  user: "root",
  password: "password",
  database: "mydb",
  connectionLimit: 10,
});

// Pool Cluster - Như có network các bar khắp thành phố
const poolCluster = mysql.createPoolCluster();
poolCluster.add("MASTER", masterConfig);
poolCluster.add("SLAVE1", slave1Config);
poolCluster.add("SLAVE2", slave2Config);
```

## **Kiến trúc MySQL Pool Cluster**

Các bạn nhìn diagram này nhé:

```
                    Application Layer
                          |
                    Pool Cluster
                          |
        ┌─────────────────┼─────────────────┐
        |                 |                 |
   MASTER Pool        SLAVE1 Pool      SLAVE2 Pool
   (Write/Read)       (Read Only)      (Read Only)
        |                 |                 |
   MySQL Master      MySQL Slave1     MySQL Slave2
```

### **Cluster Strategies - Chiến thuật phân tải**

**1. Round Robin (RR)**

```javascript
// Quay vòng như bánh xe may mắn
poolCluster.add("SLAVE*", slaveConfig);
poolCluster.getConnection("SLAVE*", "RR", callback);
```

**2. Random**

```javascript
// Random như xổ số - đôi khi may, đôi khi không
poolCluster.getConnection("SLAVE*", "RANDOM", callback);
```

**3. Order (Sequential)**

```javascript
// Theo thứ tự - slave1 chết mới chuyển slave2
poolCluster.getConnection("SLAVE*", "ORDER", callback);
```

## **Real-world Implementation với NestJS**

Đây là cách tôi thường setup trong production:

```typescript
// database.config.ts
export class DatabaseConfig {
  static createPoolCluster() {
    const poolCluster = mysql.createPoolCluster({
      restoreNodeTimeout: 20000, // 20s để thử reconnect node chết
      defaultSelector: "RR", // Round robin default
      canRetry: true,
      removeNodeErrorCount: 5, // Remove node sau 5 lỗi liên tiếp
    });

    // Master - Chỉ để write và critical reads
    poolCluster.add("MASTER", {
      host: process.env.DB_MASTER_HOST,
      user: process.env.DB_USER,
      password: process.env.DB_PASSWORD,
      database: process.env.DB_NAME,
      connectionLimit: 20, // Master cần nhiều connection hơn
      acquireTimeout: 60000,
      timezone: "+07:00",
    });

    // Read replicas - Load balance reads
    poolCluster.add("SLAVE1", {
      host: process.env.DB_SLAVE1_HOST,
      // ... config tương tự nhưng connectionLimit thấp hơn
      connectionLimit: 10,
    });

    poolCluster.add("SLAVE2", {
      host: process.env.DB_SLAVE2_HOST,
      connectionLimit: 10,
    });

    return poolCluster;
  }
}
```

### **Service Layer - Separation of Concerns**

```typescript
@Injectable()
export class DatabaseService {
  private poolCluster: any;

  constructor() {
    this.poolCluster = DatabaseConfig.createPoolCluster();
    this.setupErrorHandling();
  }

  // READ operations - Dùng slaves
  async executeRead(query: string, params: any[] = []): Promise<any> {
    return new Promise((resolve, reject) => {
      this.poolCluster.getConnection("SLAVE*", "RR", (err, connection) => {
        if (err) {
          console.error("Slave connection failed, fallback to master:", err);
          // Fallback to master nếu slaves đều chết
          return this.executeMaster(query, params, resolve, reject);
        }

        connection.query(query, params, (error, results) => {
          connection.release(); // QUAN TRỌNG: Always release!

          if (error) reject(error);
          else resolve(results);
        });
      });
    });
  }

  // WRITE operations - Chỉ dùng master
  async executeWrite(query: string, params: any[] = []): Promise<any> {
    return new Promise((resolve, reject) => {
      this.poolCluster.getConnection("MASTER", (err, connection) => {
        if (err) return reject(err);

        connection.query(query, params, (error, results) => {
          connection.release();

          if (error) reject(error);
          else resolve(results);
        });
      });
    });
  }

  // Transaction - Phải dùng master
  async executeTransaction(
    queries: Array<{ sql: string; params: any[] }>
  ): Promise<any> {
    return new Promise((resolve, reject) => {
      this.poolCluster.getConnection("MASTER", (err, connection) => {
        if (err) return reject(err);

        connection.beginTransaction(async (err) => {
          if (err) {
            connection.release();
            return reject(err);
          }

          try {
            const results = [];
            for (const query of queries) {
              const result = await this.queryPromise(
                connection,
                query.sql,
                query.params
              );
              results.push(result);
            }

            connection.commit((err) => {
              connection.release();
              if (err) reject(err);
              else resolve(results);
            });
          } catch (error) {
            connection.rollback(() => {
              connection.release();
              reject(error);
            });
          }
        });
      });
    });
  }
}
```

## **Performance Tuning - Lessons from Production**

### **1. Connection Limits - Không phải càng nhiều càng tốt**

```javascript
// ❌ WRONG - Nhiều quá sẽ kill database
connectionLimit: 1000;

// ✅ RIGHT - Tính toán dựa trên workload
connectionLimit: Math.min(20, os.cpus().length * 2);
```

**Rule of thumb:** `connectionLimit = CPU cores * 2` cho web apps thông thường.

### **2. Health Monitoring**

```typescript
class PoolMonitor {
  checkHealth() {
    setInterval(() => {
      this.poolCluster.getConnection("SLAVE*", (err, connection) => {
        if (err) {
          console.error("Slave health check failed:", err);
          // Alert to Slack/Discord
          this.alertTeam("Slave connection failed");
        } else {
          connection.query("SELECT 1", () => connection.release());
        }
      });
    }, 30000); // Check every 30s
  }
}
```

### **3. Query Routing Logic**

```typescript
// Smart routing dựa trên query type
routeQuery(sql: string) {
  const writeKeywords = ['INSERT', 'UPDATE', 'DELETE', 'CREATE', 'ALTER', 'DROP'];
  const isWrite = writeKeywords.some(keyword =>
    sql.trim().toUpperCase().startsWith(keyword)
  );

  return isWrite ? 'MASTER' : 'SLAVE*';
}
```

## **Common Pitfalls - Những cái hố tôi đã rơi vào**

### **1. Connection Leaks**

```javascript
// ❌ NEVER DO THIS
pool.getConnection((err, connection) => {
  connection.query("SELECT * FROM users", (err, results) => {
    // Quên release() -> Connection leak -> Pool exhausted
    res.json(results);
  });
});

// ✅ ALWAYS DO THIS
pool.getConnection((err, connection) => {
  connection.query("SELECT * FROM users", (err, results) => {
    connection.release(); // ALWAYS!
    res.json(results);
  });
});
```

### **2. Read-after-Write Issues**

```javascript
// ❌ Race condition
await this.executeWrite("INSERT INTO users ...");
const user = await this.executeRead("SELECT * FROM users WHERE id = ?");
// Có thể không tìm thấy vì replication lag!

// ✅ Solution
await this.executeWrite("INSERT INTO users ...");
await new Promise((resolve) => setTimeout(resolve, 100)); // Wait 100ms
const user = await this.executeRead("SELECT * FROM users WHERE id = ?");
```

### **3. Over-engineering**

```javascript
// ❌ Không cần thiết cho small apps
const cluster = mysql.createPoolCluster();
// App chỉ có 10 users/day nhưng setup 5 slaves 😅

// ✅ Start simple
const pool = mysql.createPool(config);
// Scale khi cần thiết
```

## **Production Checklist**

- [ ] **Monitoring**: Pool size, active connections, queue length
- [ ] **Alerting**: Connection failures, high latency, pool exhaustion
- [ ] **Fallback**: Master fallback khi slaves chết
- [ ] **Health checks**: Automated slave health monitoring
- [ ] **Connection limits**: Proper sizing based on load
- [ ] **Error handling**: Graceful degradation
- [ ] **Logging**: Connection events, query performance

## **Q&A Time!**

**Q: Khi nào nên dùng Pool Cluster?**
A: Khi bạn có read replicas và muốn distribute read load. Nếu chỉ có 1 DB instance thì pool thường đủ rồi.

**Q: Connection pooling có tốn memory không?**
A: Có, mỗi connection ~8KB RAM. Nhưng cost này rất nhỏ so với benefit.

**Q: Làm sao biết pool size phù hợp?**
A: Monitor `pool._allConnections.length` và `pool._freeConnections.length`. Nếu free connections luôn = 0 thì tăng size.

Remember guys: **"Premature optimization is the root of all evil"** - Start simple, measure, then optimize! 🚀

_Clink clink_ - Okay team, đổ cà phê vào và tập trung nhé! Hôm nay chúng ta sẽ nói về **MySQL Connection Pool** - một topic mà 99% junior developers không hiểu sâu, và 50% senior vẫn config sai! 😅

## **Connection Pool - The Foundation**

### **Trước khi có Pool - The Dark Ages**

Hãy tưởng tượng bạn là chủ một nhà hàng, mỗi lần có khách đến:

```javascript
// Traditional approach - Mỗi request tạo connection mới
app.get("/users", async (req, res) => {
  const connection = mysql.createConnection({
    host: "localhost",
    user: "root",
    password: "password",
  });

  connection.connect(); // ~50-200ms handshake
  const users = await connection.query("SELECT * FROM users");
  connection.end(); // Đóng connection

  res.json(users);
});
```

**Vấn đề gì xảy ra?**

- Mỗi connection tốn **50-200ms** để establish
- TCP handshake, SSL negotiation, authentication
- Với 1000 concurrent users = **Database suicide** 💀

### **Connection Pool - The Game Changer**

```javascript
// Pool approach - Maintain sẵn connections
const pool = mysql.createPool({
  host: "localhost",
  user: "root",
  password: "password",
  database: "myapp",
  connectionLimit: 10, // Magic number này!
  acquireTimeout: 60000, // Wait 60s for available connection
  timeout: 60000, // Query timeout
  reconnect: true,
  idleTimeout: 300000, // Close idle connections after 5 minutes
});

app.get("/users", async (req, res) => {
  // Lấy connection từ pool (~1-5ms)
  pool.getConnection((err, connection) => {
    if (err) throw err;

    connection.query("SELECT * FROM users", (error, results) => {
      connection.release(); // Trả về pool, KHÔNG close!
      res.json(results);
    });
  });
});
```

## **Pool Lifecycle - Vòng đời của một Connection**

```
┌─────────────────────────────────────────────────────┐
│                    CONNECTION POOL                  │
│  ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐               │
│  │Conn│ │Conn│ │Conn│ │Conn│ │Conn│  <- Free Pool  │
│  │ #1 │ │ #2 │ │ #3 │ │ #4 │ │ #5 │               │
│  └────┘ └────┘ └────┘ └────┘ └────┘               │
│                                                     │
│  ┌────┐ ┌────┐ ┌────┐                              │
│  │Conn│ │Conn│ │Conn│              <- Active Pool  │
│  │ #6 │ │ #7 │ │ #8 │                              │
│  └────┘ └────┘ └────┘                              │
└─────────────────────────────────────────────────────┘
                        │
                   ┌────▼────┐
                   │ Request │ <- Waiting queue
                   │ Queue   │
                   └─────────┘
```

### **States của Connection:**

1. **Free**: Sẵn sàng được sử dụng
2. **Active**: Đang execute query
3. **Connecting**: Đang establish connection
4. **Destroyed**: Bị lỗi hoặc timeout

## **Configuration Deep Dive**

### **Connection Limit - Số ma thuật**

```javascript
const pool = mysql.createPool({
  connectionLimit: 10, // Tại sao lại là 10? 🤔
});
```

**Câu trả lời:**

- **MySQL default max_connections = 151**
- **Typical web server = 4-8 CPU cores**
- **Rule of thumb: CPU cores × 2 = connection limit**

```javascript
// Smart configuration
const os = require("os");
const cpuCount = os.cpus().length;

const pool = mysql.createPool({
  connectionLimit: Math.min(cpuCount * 2, 20), // Max 20 connections
  acquireTimeout: 60000,
  timeout: 60000,
});
```

### **Timeout Settings - The Trinity**

```javascript
{
  acquireTimeout: 60000,  // Wait time to get connection from pool
  timeout: 60000,         // Query execution timeout
  idleTimeout: 300000     // Close idle connections after 5min
}
```

**Real-world example:**

```javascript
// E-commerce site config
const ecommercePool = mysql.createPool({
  connectionLimit: 15,
  acquireTimeout: 10000, // Fast fail for high traffic
  timeout: 30000, // Queries should be fast
  idleTimeout: 180000, // 3 minutes idle timeout
  reconnect: true,
  charset: "utf8mb4", // Support emoji 😊
});
```

## **Pool Patterns trong Production**

### **Pattern 1: Service-based Pooling**

```typescript
// database.service.ts
@Injectable()
export class DatabaseService {
  private pool: mysql.Pool;

  constructor() {
    this.pool = mysql.createPool({
      host: process.env.DB_HOST,
      user: process.env.DB_USER,
      password: process.env.DB_PASSWORD,
      database: process.env.DB_NAME,
      connectionLimit: parseInt(process.env.DB_CONNECTION_LIMIT) || 10,
      acquireTimeout: 60000,
      timeout: 60000,
      reconnect: true,
    });

    this.setupMonitoring();
  }

  async query(sql: string, params: any[] = []): Promise<any> {
    return new Promise((resolve, reject) => {
      this.pool.getConnection((err, connection) => {
        if (err) {
          console.error("Pool connection error:", err);
          return reject(err);
        }

        const startTime = Date.now();
        connection.query(sql, params, (error, results) => {
          const queryTime = Date.now() - startTime;

          // Performance monitoring
          if (queryTime > 1000) {
            console.warn(`Slow query (${queryTime}ms):`, sql);
          }

          connection.release(); // Crucial! Return to pool

          if (error) reject(error);
          else resolve(results);
        });
      });
    });
  }

  // Health check endpoint
  async checkHealth(): Promise<boolean> {
    try {
      await this.query("SELECT 1");
      return true;
    } catch (error) {
      console.error("Database health check failed:", error);
      return false;
    }
  }
}
```

### **Pattern 2: Promise-based với async/await**

```typescript
// Modern approach with util.promisify
import { promisify } from "util";

class ModernDatabaseService {
  private pool: mysql.Pool;
  private queryAsync: any;

  constructor() {
    this.pool = mysql.createPool(config);

    // Promisify pool.query for async/await
    this.queryAsync = promisify(this.pool.query).bind(this.pool);
  }

  async findUser(id: number): Promise<any> {
    try {
      const results = await this.queryAsync(
        "SELECT * FROM users WHERE id = ?",
        [id]
      );
      return results[0];
    } catch (error) {
      console.error("Query failed:", error);
      throw error;
    }
  }

  // Transaction support
  async createUserWithProfile(userData: any, profileData: any): Promise<void> {
    const connection = await this.getConnectionAsync();

    try {
      await this.beginTransactionAsync(connection);

      const userResult = await this.queryAsync("INSERT INTO users SET ?", [
        userData,
      ]);

      await this.queryAsync("INSERT INTO profiles SET ?", [
        { ...profileData, user_id: userResult.insertId },
      ]);

      await this.commitAsync(connection);
    } catch (error) {
      await this.rollbackAsync(connection);
      throw error;
    } finally {
      connection.release(); // Always release!
    }
  }
}
```

## **Monitoring & Debugging - The Pro Stuff**

### **Pool Statistics**

```javascript
class PoolMonitor {
  constructor(pool) {
    this.pool = pool;
    this.startMonitoring();
  }

  getPoolStats() {
    return {
      totalConnections: this.pool._allConnections.length,
      freeConnections: this.pool._freeConnections.length,
      activeConnections:
        this.pool._allConnections.length - this.pool._freeConnections.length,
      queuedRequests: this.pool._connectionQueue.length,
    };
  }

  startMonitoring() {
    setInterval(() => {
      const stats = this.getPoolStats();
      console.log("Pool Stats:", stats);

      // Alert if pool is exhausted
      if (stats.freeConnections === 0 && stats.queuedRequests > 0) {
        console.warn("⚠️  Pool exhausted! Consider increasing connectionLimit");
        // Send alert to Slack/Discord
        this.alertTeam("Database pool exhausted", stats);
      }

      // Alert for too many idle connections
      if (stats.freeConnections > stats.totalConnections * 0.8) {
        console.info(
          "💡 Too many idle connections, consider reducing connectionLimit"
        );
      }
    }, 30000); // Check every 30 seconds
  }
}
```

### **Performance Metrics**

```javascript
// Query performance tracking
class QueryTracker {
  constructor() {
    this.slowQueries = [];
    this.queryCount = 0;
    this.totalQueryTime = 0;
  }

  trackQuery(sql, params, executionTime) {
    this.queryCount++;
    this.totalQueryTime += executionTime;

    if (executionTime > 1000) {
      // Queries > 1 second
      this.slowQueries.push({
        sql,
        params,
        executionTime,
        timestamp: new Date(),
      });
    }
  }

  getMetrics() {
    return {
      totalQueries: this.queryCount,
      averageQueryTime: this.totalQueryTime / this.queryCount,
      slowQueries: this.slowQueries.slice(-10), // Last 10 slow queries
      qps: (this.queryCount / (Date.now() - this.startTime)) * 1000, // Queries per second
    };
  }
}
```

## **Common Mistakes - Những cái bẫy tôi đã rơi vào**

### **1. Connection Leaks - Thảm họa #1**

```javascript
// ❌ WRONG - Connection leak
pool.getConnection((err, connection) => {
  connection.query("SELECT * FROM users", (err, results) => {
    if (err) {
      // Quên release() khi có lỗi!
      return res.status(500).json({ error: err.message });
    }

    connection.release(); // Chỉ release khi success
    res.json(results);
  });
});

// ✅ CORRECT - Always release
pool.getConnection((err, connection) => {
  if (err) return res.status(500).json({ error: err.message });

  connection.query("SELECT * FROM users", (err, results) => {
    connection.release(); // ALWAYS release first!

    if (err) {
      return res.status(500).json({ error: err.message });
    }

    res.json(results);
  });
});
```

### **2. Over-pooling - Hậu quả khôn lường**

```javascript
// ❌ WRONG - Too many connections
const pool = mysql.createPool({
  connectionLimit: 100, // MySQL chỉ handle được ~150 connections!
});

// ✅ CORRECT - Reasonable limits
const pool = mysql.createPool({
  connectionLimit: Math.min(20, os.cpus().length * 3),
  queueLimit: 0, // Fail fast instead of queuing
});
```

### **3. Ignoring Pool Events**

```javascript
// ✅ Listen to pool events
pool.on("connection", (connection) => {
  console.log("New connection established as id " + connection.threadId);
});

pool.on("error", (err) => {
  console.error("Pool error:", err);
  if (err.code === "PROTOCOL_CONNECTION_LOST") {
    // Handle connection lost
    console.log("Database connection lost, attempting to reconnect...");
  }
});

pool.on("release", (connection) => {
  console.log("Connection %d released", connection.threadId);
});
```

## **Environment-specific Configurations**

### **Development**

```javascript
const devPool = mysql.createPool({
  connectionLimit: 5, // Small pool for dev
  acquireTimeout: 60000,
  timeout: 60000,
  debug: true, // Enable SQL logging
  multipleStatements: true, // Useful for seeding
});
```

### **Production**

```javascript
const prodPool = mysql.createPool({
  connectionLimit: 20,
  acquireTimeout: 10000, // Fail fast in production
  timeout: 30000, // Strict timeout
  reconnect: true,
  charset: "utf8mb4",
  ssl: {
    rejectUnauthorized: false, // For cloud databases
  },
});
```

### **Load Testing**

```javascript
const loadTestPool = mysql.createPool({
  connectionLimit: 50, // Higher for load testing
  acquireTimeout: 5000, // Very fast fail
  timeout: 15000,
  queueLimit: 0, // No queuing
});
```

## **Pro Tips từ Production Experience**

### **1. Graceful Shutdown**

```javascript
process.on("SIGTERM", () => {
  console.log("Received SIGTERM, closing pool...");
  pool.end(() => {
    console.log("Pool closed gracefully");
    process.exit(0);
  });
});
```

### **2. Health Check Endpoint**

```javascript
app.get("/health", async (req, res) => {
  try {
    const startTime = Date.now();
    await pool.query("SELECT 1");
    const responseTime = Date.now() - startTime;

    const poolStats = {
      total: pool._allConnections.length,
      free: pool._freeConnections.length,
      used: pool._allConnections.length - pool._freeConnections.length,
    };

    res.json({
      status: "healthy",
      database: {
        responseTime: `${responseTime}ms`,
        pool: poolStats,
      },
    });
  } catch (error) {
    res.status(503).json({
      status: "unhealthy",
      error: error.message,
    });
  }
});
```

### **3. Circuit Breaker Pattern**

```javascript
class DatabaseCircuitBreaker {
  constructor(pool, threshold = 5, timeout = 60000) {
    this.pool = pool;
    this.failureCount = 0;
    this.threshold = threshold;
    this.timeout = timeout;
    this.state = "CLOSED"; // CLOSED, OPEN, HALF_OPEN
    this.nextAttempt = Date.now();
  }

  async query(sql, params) {
    if (this.state === "OPEN") {
      if (Date.now() < this.nextAttempt) {
        throw new Error("Circuit breaker is OPEN");
      }
      this.state = "HALF_OPEN";
    }

    try {
      const result = await this.pool.query(sql, params);
      this.onSuccess();
      return result;
    } catch (error) {
      this.onFailure();
      throw error;
    }
  }

  onSuccess() {
    this.failureCount = 0;
    this.state = "CLOSED";
  }

  onFailure() {
    this.failureCount++;
    if (this.failureCount >= this.threshold) {
      this.state = "OPEN";
      this.nextAttempt = Date.now() + this.timeout;
    }
  }
}
```

## **Checklist cho Production**

- [ ] **Connection limits** phù hợp với server capacity
- [ ] **Timeout settings** đã được tune
- [ ] **Error handling** và connection release trong mọi case
- [ ] **Monitoring** pool statistics
- [ ] **Health checks** endpoint
- [ ] **Graceful shutdown** handling
- [ ] **Environment-specific** configurations
- [ ] **SSL/TLS** setup cho production
- [ ] **Logging** slow queries và errors
- [ ] **Circuit breaker** cho fault tolerance

## **Final Words**

Remember team: **Connection pooling is not about having more connections, it's about reusing them efficiently!**

Start with conservative settings, monitor in production, và scale based on actual metrics - không phải gut feeling!

Questions? Fire away! 🚀

_Ai muốn coffee break? Chúng ta sẽ thực hành setup pool cluster sau 15 phút nhé!_ ☕
