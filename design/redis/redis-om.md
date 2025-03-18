I'll help you understand Redis OM (Object Mapping) with a comprehensive explanation and examples.

````markdown:/Users/loCser/Desktop/design-par/redis/redis-om.md
# Redis OM (Object Mapping)

## 1. What is Redis OM?
Redis OM is a modern object mapping library that simplifies working with Redis data models. It provides high-level abstractions for different programming languages (Node.js, Python, .NET) to interact with Redis using an intuitive, object-oriented approach.

## 2. Key Features
1. **Schema Definition**
2. **Object Mapping**
3. **Search Capabilities**
4. **Type Safety**
5. **Redis JSON Integration**

## 3. Implementation Examples

### 3.1 Node.js Implementation
```javascript:/Users/loCser/Desktop/design-par/redis/examples/node/user-model.js
import { Entity, Schema } from 'redis-om';

// Define the User Schema
class User extends Entity {}

// Create Schema with properties
const userSchema = new Schema(User, {
    firstName: { type: 'string' },
    lastName: { type: 'string' },
    email: { type: 'string' },
    age: { type: 'number' },
    address: { type: 'string' },
    isActive: { type: 'boolean' }
});
````

### 3.2 Repository Pattern Example

```javascript:/Users/loCser/Desktop/design-par/redis/examples/node/user-repository.js
import { Client } from 'redis-om';
import { User, userSchema } from './user-model';

// Create Redis client
const client = await new Client().open('redis://localhost:6379');

// Create Repository
const userRepository = client.fetchRepository(userSchema);

// Create index for search
await userRepository.createIndex();

// CRUD Operations
async function createUser(userData) {
    const user = await userRepository.createAndSave({
        firstName: userData.firstName,
        lastName: userData.lastName,
        email: userData.email,
        age: userData.age,
        address: userData.address,
        isActive: true
    });
    return user;
}

async function findUserByEmail(email) {
    return await userRepository
        .search()
        .where('email').equals(email)
        .return.first();
}

async function updateUser(id, userData) {
    const user = await userRepository.fetch(id);
    Object.assign(user, userData);
    await userRepository.save(user);
    return user;
}
```

### 3.3 Search Examples

```javascript:/Users/loCser/Desktop/design-par/redis/examples/node/search-examples.js
// Complex search operations
async function searchUsers() {
    // Find active users over 25
    const activeAdults = await userRepository
        .search()
        .where('age').gt(25)
        .and('isActive').equals(true)
        .return.all();

    // Full-text search on address
    const cityUsers = await userRepository
        .search()
        .where('address').matches('New York')
        .return.all();

    // Pagination
    const pagedUsers = await userRepository
        .search()
        .sortBy('age', 'DESC')
        .return.page(0, 10);
}
```

## 4. Real-World Application - E-commerce Product Catalog

```javascript:/Users/loCser/Desktop/design-par/redis/examples/node/product-catalog.js
import { Entity, Schema } from 'redis-om';

// Product Entity
class Product extends Entity {}

// Product Schema
const productSchema = new Schema(Product, {
    name: { type: 'string', textSearch: true },
    description: { type: 'string', textSearch: true },
    price: { type: 'number' },
    category: { type: 'string' },
    tags: { type: 'string[]' },
    inStock: { type: 'boolean' },
    ratings: { type: 'number[]' }
});

// Product Repository
class ProductRepository {
    constructor(client) {
        this.repository = client.fetchRepository(productSchema);
    }

    async searchProducts(query) {
        return await this.repository
            .search()
            .where('name').matches(query)
            .or('description').matches(query)
            .and('inStock').equals(true)
            .return.all();
    }

    async getByCategory(category, page = 0, pageSize = 20) {
        return await this.repository
            .search()
            .where('category').equals(category)
            .return.page(page, pageSize);
    }

    async getTopRated() {
        return await this.repository
            .search()
            .where('ratings')
            .return.all();
    }
}
```

## 5. Best Practices

### 5.1 Schema Design

```javascript:/Users/loCser/Desktop/design-par/redis/examples/node/schema-best-practices.js
// Use appropriate data types
const orderSchema = new Schema(Order, {
    orderId: { type: 'string' },
    amount: { type: 'number', sortable: true },
    status: { type: 'string', indexed: true },
    createdAt: { type: 'date', sortable: true },
    items: { type: 'string[]' }
});

// Enable text search where needed
const articleSchema = new Schema(Article, {
    title: { type: 'string', textSearch: true },
    content: { type: 'text', textSearch: true },
    tags: { type: 'string[]' }
});
```

## 6. Performance Considerations

1. **Indexing Strategy**

   - Index only necessary fields
   - Use appropriate index types
   - Consider memory usage

2. **Query Optimization**

   - Use specific queries instead of full-text search when possible
   - Implement pagination for large result sets
   - Cache frequently accessed data

3. **Memory Management**
   - Monitor memory usage
   - Implement data expiration policies
   - Use appropriate data structures

## 7. Common Use Cases

1. Session Management
2. Caching Layer
3. Real-time Analytics
4. Search Functionality
5. Rate Limiting
6. Job Queues

## 8. Advanced Features

### 8.1 Transactions

```javascript:/Users/loCser/Desktop/design-par/redis/examples/node/transactions.js
async function transferPoints(fromUserId, toUserId, points) {
    const multi = client.multi();

    multi.decrby(`user:${fromUserId}:points`, points);
    multi.incrby(`user:${toUserId}:points`, points);

    await multi.exec();
}
```

### 8.2 Pub/Sub Implementation

```javascript:/Users/loCser/Desktop/design-par/redis/examples/node/pubsub.js
// Publisher
async function publishOrderUpdate(orderId, status) {
    await client.publish('order-updates',
        JSON.stringify({ orderId, status }));
}

// Subscriber
const subscriber = client.duplicate();
await subscriber.subscribe('order-updates', (message) => {
    const { orderId, status } = JSON.parse(message);
    console.log(`Order ${orderId} status: ${status}`);
});
```

## 9. Error Handling and Monitoring

```javascript:/Users/loCser/Desktop/design-par/redis/examples/node/error-handling.js
class RedisService {
    async executeWithRetry(operation, retries = 3) {
        try {
            return await operation();
        } catch (error) {
            if (retries > 0 && this.isRetryableError(error)) {
                await this.delay(1000);
                return this.executeWithRetry(operation, retries - 1);
            }
            throw error;
        }
    }

    isRetryableError(error) {
        return error.message.includes('READONLY') ||
               error.message.includes('CONNECTIONCLOSED');
    }
}
```

Would you like me to explain any specific aspect of Redis OM in more detail? Or would you like to see more examples for specific use cases?
