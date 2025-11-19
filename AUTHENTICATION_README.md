# JWT Authentication Implementation - Hot Properties

## Overview

This document describes the JWT-based authentication and authorization system implemented for the Hot Properties real estate application.

## Implemented Features

### 1. Three User Roles
- **BUYER**: Can register themselves, browse properties, save favorites, and message agents
- **AGENT**: Created by admins, can manage property listings and respond to messages  
- **ADMIN**: Created by other admins, can manage users and create agent accounts

### 2. Authentication & Security
- JWT token-based authentication
- Tokens stored in HTTP-only cookies for security
- BCrypt password hashing
- Role-based access control (RBAC)

## Project Structure

```
src/main/java/com/finalproject/HotProperties/
├── config/
│   └── SecurityConfig.java              # Spring Security configuration
├── controllers/
│   ├── AuthController.java              # REST API endpoints for auth
│   ├── LoginController.java             # Web login page controller
│   ├── RegisterController.java          # Web registration controller
│   ├── AdminController.java             # Admin-only endpoints
│   └── DashboardController.java         # Temporary dashboard
├── dto/
│   ├── LoginRequest.java                # Login request DTO
│   ├── RegisterRequest.java             # Registration request DTO
│   └── AuthResponse.java                # Authentication response DTO
├── models/
│   ├── User.java                        # User entity (JPA)
│   └── Role.java                        # Role enum
├── repositories/
│   └── UserRepository.java              # User database operations
├── security/
│   ├── JwtUtils.java                    # JWT token utilities
│   ├── JwtAuthenticationFilter.java     # JWT filter for requests
│   ├── CustomUserDetailsService.java    # User loading service
│   └── UserDetailsImpl.java             # UserDetails implementation
└── services/
    └── UserService.java                 # User business logic
```

## Database Configuration

**MySQL Database:** `hotproperties`

Update `application.properties` with your MySQL credentials:
```properties
spring.datasource.username=root
spring.datasource.password=your_password
```

The application will automatically create the `users` table with the following schema:

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

## API Endpoints

### Public Endpoints (No Authentication Required)

#### Register New Buyer
```
POST /api/auth/register
Content-Type: application/json

{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "password123"
}
```

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
    "email": "john@example.com",
    "password": "password123"
}

Response: Sets JWT cookie, returns user info
```

#### Logout
```
POST /api/auth/logout

Response: Clears JWT cookie
```

### Admin-Only Endpoints (Requires ADMIN role)

#### Create Agent Account
```
POST /api/admin/create-agent
Content-Type: application/json
Cookie: hotproperties_jwt=<jwt_token>

{
    "firstName": "Jane",
    "lastName": "Agent",
    "email": "jane@agent.com",
    "password": "password123"
}
```

#### Create Admin Account
```
POST /api/admin/create-admin
Content-Type: application/json
Cookie: hotproperties_jwt=<jwt_token>

{
    "firstName": "Bob",
    "lastName": "Admin",
    "email": "bob@admin.com",
    "password": "password123"
}
```

## Web Pages

### Login Page
- **URL:** `/login`
- **Method:** GET (show form), POST (submit)
- **Features:** Email/password login, redirects to dashboard on success

### Register Page
- **URL:** `/register`
- **Method:** GET (show form), POST (submit)
- **Features:** New BUYER user registration with validation

### Dashboard
- **URL:** `/dashboard`
- **Method:** GET
- **Authentication:** Required
- **Features:** Shows user email and role (temporary - will be replaced with role-specific dashboards)

## Testing the Implementation

### 1. Start MySQL
Ensure MySQL is running with a database named `hotproperties` (it will be created automatically on first run).

### 2. Run the Application
```bash
./mvnw spring-boot:run
```

### 3. Test Registration (Web)
1. Navigate to `http://localhost:8080/register`
2. Fill in the form with:
   - First Name: Test
   - Last Name: Buyer
   - Email: buyer@test.com
   - Password: password123
3. Click "Register"
4. You should be redirected to login with a success message

### 4. Test Login (Web)
1. Navigate to `http://localhost:8080/login`
2. Enter credentials:
   - Email: buyer@test.com
   - Password: password123
3. Click "Login"
4. You should be redirected to the dashboard

### 5. Test API Endpoints (Using curl or Postman)

**Register a buyer:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"API","lastName":"User","email":"api@test.com","password":"password123"}'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"api@test.com","password":"password123"}' \
  -c cookies.txt
```

**Access protected endpoint:**
```bash
curl -X GET http://localhost:8080/api/buyer/test \
  -b cookies.txt
```

### 6. Create an Admin User Manually (for testing)

Since the first admin must be created manually, run this SQL in your MySQL database:

```sql
INSERT INTO users (first_name, last_name, email, password, role, created_at)
VALUES ('Admin', 'User', 'admin@test.com', 
        '$2a$10$dummyBCryptHashHere', 
        'ADMIN', NOW());
```

Or use this Java code in a temporary controller/test:
```java
// After injecting UserService and PasswordEncoder
User admin = new User();
admin.setFirstName("Admin");
admin.setLastName("User");
admin.setEmail("admin@test.com");
admin.setPassword(passwordEncoder.encode("admin123"));
admin.setRole(Role.ADMIN);
userRepository.save(admin);
```

Then you can use the admin account to create agents via the API.

## Password Validation Rules

- Minimum 6 characters
- Cannot be empty
- Required field

## Email Validation
- Must be a valid email format
- Must be unique (no duplicates)
- Required field

## Security Features Implemented

✅ **JWT Token Authentication** - Stateless authentication using JSON Web Tokens  
✅ **HTTP-Only Cookies** - Tokens stored securely, not accessible via JavaScript  
✅ **BCrypt Password Hashing** - Industry-standard password encryption  
✅ **Role-Based Access Control** - Different permissions for BUYER, AGENT, ADMIN  
✅ **CSRF Protection** - Disabled for stateless JWT (can be re-enabled if needed)  
✅ **Session Management** - Stateless sessions for scalability  
✅ **Input Validation** - Comprehensive validation for all user inputs  
✅ **Exception Handling** - Proper error messages for validation failures  

## Integration with Teammates' Code

This authentication system is designed to integrate seamlessly with other features:

### For Property Management (Agent feature)
```java
@RestController
@RequestMapping("/api/agent/properties")
public class PropertyController {
    
    @PostMapping
    public ResponseEntity<?> createProperty(@AuthenticationPrincipal UserDetails userDetails, ...) {
        // Get current authenticated user
        User agent = userService.loadUserByEmail(userDetails.getUsername());
        
        // Verify they are an agent
        if (agent.getRole() != Role.AGENT) {
            throw new InvalidUserParameterException("Only agents can create properties");
        }
        
        // Create property associated with this agent
        // ...
    }
}
```

### For Favorites (Buyer feature)
```java
@RestController
@RequestMapping("/api/buyer/favorites")
public class FavoriteController {
    
    @PostMapping
    public ResponseEntity<?> addFavorite(@AuthenticationPrincipal UserDetails userDetails, ...) {
        User buyer = userService.loadUserByEmail(userDetails.getUsername());
        // Add favorite for this buyer
        // ...
    }
}
```

### For Messages
```java
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    
    @PostMapping
    public ResponseEntity<?> sendMessage(@AuthenticationPrincipal UserDetails userDetails, ...) {
        User sender = userService.loadUserByEmail(userDetails.getUsername());
        // Create message from this sender
        // ...
    }
}
```

## Next Steps

The following features should be implemented by teammates:

1. **Property Management** - CRUD operations for properties (Agent role)
2. **Browse Properties** - Property listing and filtering (Buyer role)
3. **Favorites System** - Save/remove favorites (Buyer role)
4. **Messaging System** - Communication between buyers and agents
5. **User Management** - Admin interface to view/delete users
6. **Role-Specific Dashboards** - Custom dashboards for each role

## Troubleshooting

### Issue: "Table 'users' doesn't exist"
- Make sure MySQL is running
- Check database connection in `application.properties`
- Ensure `spring.jpa.hibernate.ddl-auto=update` is set

### Issue: "401 Unauthorized" on protected endpoints
- Verify JWT cookie is being sent with requests
- Check cookie hasn't expired (24 hour expiration)
- Verify user has the correct role for the endpoint

### Issue: "Invalid JWT signature"
- JWT secret must be consistent between application restarts
- Don't change `jwt.secret` in `application.properties` after tokens are issued

## Contact

For questions about this authentication implementation, contact the team member responsible for Users, Roles & Security/Authentication.

