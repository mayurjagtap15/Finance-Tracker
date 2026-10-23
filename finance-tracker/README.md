# Finance Tracker

A personal finance tracking web application built with **Spring Boot 2.7**, **Thymeleaf**, **Spring Security**, **Spring Data JPA**, and an **H2 in-memory database**.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Application Flow — Service Layer](#application-flow--service-layer)
- [Flow by Feature](#flow-by-feature)
  - [1. Authentication — UserServiceImpl](#1-authentication--userserviceimpl)
  - [2. Dashboard — DashboardServiceImpl](#2-dashboard--dashboardserviceimpl)
  - [3. Transactions — TransactionServiceImpl](#3-transactions--transactionserviceimpl)
  - [4. Categories — CategoryServiceImpl](#4-categories--categoryserviceimpl)
- [Service Dependency Map](#service-dependency-map)
- [Request → Service → Repository Flow](#request--service--repository-flow)

---

## Tech Stack

| Layer        | Technology                              |
|--------------|-----------------------------------------|
| Language     | Java 11                                 |
| Framework    | Spring Boot 2.7.18                      |
| Web / Views  | Spring MVC + Thymeleaf 3                |
| Security     | Spring Security 5.7                     |
| Persistence  | Spring Data JPA + Hibernate 5.6         |
| Database     | H2 (in-memory)                          |
| Build        | Maven 3                                 |
| Charts       | Chart.js 4 (CDN)                        |
| Icons / CSS  | Bootstrap Icons 1.11 (CDN) + custom CSS |

---

## Getting Started

### Prerequisites

- Java 11+
- Maven 3.6+

### Run

```bash
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080)

### Demo credentials

| Field    | Value      |
|----------|------------|
| Username | `demo`     |
| Password | `password` |

### H2 Console (dev)

[http://localhost:8080/h2-console](http://localhost:8080/h2-console)

- JDBC URL: `jdbc:h2:mem:financedb`
- Username: `sa`
- Password: *(empty)*

---

## Project Structure

```
src/
└── main/
    ├── java/com/financeapp/
    │   ├── FinanceTrackerApplication.java
    │   ├── config/
    │   │   ├── PasswordEncoderConfig.java   # BCrypt bean (isolated to break circular dep)
    │   │   └── SecurityConfig.java          # Filter chain, form login, CSRF
    │   ├── controller/
    │   │   ├── AppErrorController.java      # Custom /error handler (replaces Whitelabel)
    │   │   ├── AuthController.java          # /login, /register
    │   │   ├── CategoryController.java      # /categories/**
    │   │   ├── DashboardController.java     # /, /dashboard
    │   │   └── TransactionController.java   # /transactions/**
    │   ├── dto/
    │   │   ├── CategoryDto.java
    │   │   ├── DashboardDto.java
    │   │   ├── RegistrationDto.java
    │   │   └── TransactionDto.java
    │   ├── model/
    │   │   ├── Category.java
    │   │   ├── Role.java
    │   │   ├── Transaction.java
    │   │   ├── TransactionType.java         # Enum: INCOME | EXPENSE
    │   │   └── User.java
    │   ├── repository/
    │   │   ├── CategoryRepository.java
    │   │   ├── RoleRepository.java
    │   │   ├── TransactionRepository.java
    │   │   └── UserRepository.java
    │   └── service/
    │       ├── CategoryService.java
    │       ├── DashboardService.java
    │       ├── TransactionService.java
    │       ├── UserService.java
    │       └── impl/
    │           ├── CategoryServiceImpl.java
    │           ├── DashboardServiceImpl.java
    │           ├── TransactionServiceImpl.java
    │           └── UserServiceImpl.java
    └── resources/
        ├── application.properties
        ├── sql/
        │   ├── schema.sql                   # DDL — tables created on startup
        │   └── data.sql                     # Seed data — demo user, categories, transactions
        ├── static/
        │   ├── css/app.css
        │   └── js/charts.js                 # Chart.js bar + doughnut chart init
        └── templates/
            ├── auth/login.html
            ├── auth/register.html
            ├── categories/form.html
            ├── categories/list.html
            ├── dashboard.html
            ├── error.html
            ├── fragments/layout.html        # Shared head, navbar, alerts
            └── transactions/
                ├── form.html
                └── list.html
```

---

## Architecture

The application is structured in strict layers. Each layer communicates only with the layer directly below it — controllers never touch repositories directly.

```
┌──────────────────────────────────────────────────────────────────────┐
│  BROWSER  (Thymeleaf HTML + Chart.js + Bootstrap Icons)              │
└────────────────────────────┬─────────────────────────────────────────┘
                             │  HTTP (port 8080)
┌────────────────────────────▼─────────────────────────────────────────┐
│  SECURITY LAYER                                                       │
│  ┌──────────────────┐  ┌────────────────────────────┐                │
│  │  SecurityConfig  │  │  DaoAuthenticationProvider │                │
│  │  (FilterChain)   │  │  → UserServiceImpl         │                │
│  └──────────────────┘  │  → BCryptPasswordEncoder   │                │
│                         └────────────────────────────┘                │
└────────────────────────────┬─────────────────────────────────────────┘
                             │  Authenticated request
┌────────────────────────────▼─────────────────────────────────────────┐
│  CONTROLLER LAYER                                                     │
│  AuthController · DashboardController · TransactionController         │
│  CategoryController · AppErrorController                              │
└────────────────────────────┬─────────────────────────────────────────┘
                             │  Service interface calls
┌────────────────────────────▼─────────────────────────────────────────┐
│  SERVICE LAYER                                                        │
│  UserServiceImpl · TransactionServiceImpl                             │
│  CategoryServiceImpl · DashboardServiceImpl                           │
└────────────────────────────┬─────────────────────────────────────────┘
                             │  JPA Repository calls
┌────────────────────────────▼─────────────────────────────────────────┐
│  REPOSITORY LAYER  (Spring Data JPA)                                  │
│  UserRepository · RoleRepository                                      │
│  CategoryRepository · TransactionRepository                           │
└────────────────────────────┬─────────────────────────────────────────┘
                             │  Hibernate ORM
┌────────────────────────────▼─────────────────────────────────────────┐
│  DATABASE  (H2 in-memory)                                             │
│  users · roles · user_roles · categories · transactions               │
└──────────────────────────────────────────────────────────────────────┘
```

---

## Application Flow — Service Layer

```
flowchart TD
    Browser["Browser Request"]

    subgraph Controllers
        AC["AuthController\n/login  /register"]
        DC["DashboardController\n/ /dashboard"]
        TC["TransactionController\n/transactions/**"]
        CC["CategoryController\n/categories/**"]
    end

    subgraph Services
        US["UserService (UserServiceImpl)"]
        TS["TransactionService (TransactionServiceImpl)"]
        CS["CategoryService (CategoryServiceImpl)"]
        DS["DashboardService (DashboardServiceImpl)"]
    end

    subgraph Security
        SP["Spring Security / DaoAuthenticationProvider"]
        UDL["UserDetailsService (UserServiceImpl)"]
    end

    subgraph Repositories
        UR["UserRepository"]
        TR["TransactionRepository"]
        CR["CategoryRepository"]
        RR["RoleRepository"]
    end

    DB[("H2 In-Memory DB")]

    Browser --> AC & DC & TC & CC
    AC --> US
    DC --> US & DS
    TC --> US & TS & CS
    CC --> US & CS

    DS --> TS & TR
    TS --> TR & UR & CR
    CS --> CR & UR
    US --> UR & RR

    SP --> UDL --> UR
    UR & TR & CR & RR --> DB
```

**Key design rules enforced at the service layer:**

- Every write operation (`save`, `update`, `delete`) is annotated `@Transactional` — reads are not, keeping transactions short.
- **Ownership is always enforced inside the service** — no controller accesses a repository directly. All user-scoping (e.g., `findByIdAndUserId`, `category.user.id == userId`) happens inside the service so no controller can bypass it.
- `UserServiceImpl` implements both `UserService` and Spring Security's `UserDetailsService` — one bean handles both app-level user management and the security authentication contract.

---

## Flow by Feature

### 1. Authentication — UserServiceImpl

#### Registration — `POST /register` → `AuthController` → `UserService.register()`

```
RegistrationDto  (form: username, fullName, email, password, confirmPassword)
  │
  ▼  UserServiceImpl.register()
  ├─ UserRepository.existsByUsername()     duplicate username check
  ├─ UserRepository.existsByEmail()        duplicate email check
  ├─ dto.password == dto.confirmPassword   passwords-match check
  ├─ PasswordEncoder.encode(password)      BCrypt hash
  ├─ RoleRepository.findByName("ROLE_USER") assign default role
  └─ UserRepository.save(user)             persist to DB
       → redirect to /login with success flash
```

#### Login — Spring Security → `UserServiceImpl.loadUserByUsername()`

```
Login form  (username + password)
  │
  ▼  DaoAuthenticationProvider
  ├─ UserServiceImpl.loadUserByUsername(username)
  │    └─ UserRepository.findByUsername()
  │         → builds Spring Security UserDetails with granted authorities (ROLE_USER)
  └─ BCryptPasswordEncoder.matches(rawPassword, encodedPassword)
       → success  → redirect to /dashboard
       → failure  → redirect to /login?error=true
```

---

### 2. Dashboard — DashboardServiceImpl

#### `GET /dashboard` → `DashboardController` → `DashboardService.getDashboard(userId)`

```
DashboardServiceImpl.getDashboard(userId)
  │
  ├─ TransactionRepository.sumByUserAndTypeAndDateRange(INCOME,  thisMonth)
  │    → totalIncome  (BigDecimal)
  │
  ├─ TransactionRepository.sumByUserAndTypeAndDateRange(EXPENSE, thisMonth)
  │    → totalExpense (BigDecimal)
  │    → balance = totalIncome − totalExpense
  │
  ├─ TransactionService.findRecentByUser(userId)     top-5 latest
  │    └─ each Transaction → TransactionService.toDto()
  │         → List<TransactionDto>   (recent-transactions table on dashboard)
  │
  ├─ TransactionRepository.sumByCategoryForUserAndTypeAndDateRange(EXPENSE, thisMonth)
  │    → Map<categoryName, amount>   (doughnut chart: expenses by category)
  │
  └─ TransactionRepository.monthlyTotals(INCOME/EXPENSE, last 6 months)
       → List<String> monthLabels
       → List<BigDecimal> monthlyIncome
       → List<BigDecimal> monthlyExpense
            (bar chart: 6-month overview)
  
  → returns DashboardDto → model → dashboard.html (Thymeleaf + Chart.js)
```

> `DashboardServiceImpl` depends on both `TransactionService` (for the recent-transactions list)
> **and** `TransactionRepository` directly (for the aggregate `SUM` / `GROUP BY` queries).
> This is intentional — the aggregate queries are efficient SQL operations that
> don't need the full service layer.

---

### 3. Transactions — TransactionServiceImpl

#### Create — `POST /transactions/new`

```
TransactionDto  (categoryId, amount, type, date, description)
  │
  ▼  TransactionServiceImpl.save(dto, userId)
  └─ populateAndSave()
       ├─ UserRepository.findById(userId)        load owner
       ├─ CategoryRepository.findById(catId)     load category
       └─ TransactionRepository.save(transaction) persist
            → redirect to /transactions with success flash
```

#### Update — `POST /transactions/{id}/edit`

```
TransactionServiceImpl.update(id, dto, userId)
  ├─ findByIdAndUserId(id, userId)          ownership check
  │    ├─ TransactionRepository.findById(id)
  │    └─ transaction.user.id == userId ? proceed : throw "Access denied"
  └─ populateAndSave()                      same path as create
```

#### Delete — `POST /transactions/{id}/delete`

```
TransactionServiceImpl.delete(id, userId)
  ├─ findByIdAndUserId(id, userId)          ownership check (same as update)
  └─ TransactionRepository.delete(transaction)
```

#### List — `GET /transactions`

```
TransactionServiceImpl.findAllByUser(userId, Pageable)
  └─ TransactionRepository
       .findByUserIdOrderByTransactionDateDescCreatedAtDesc(pageable)
            → Page<Transaction>   (paginated, newest first)
```

#### Edit form — `GET /transactions/{id}/edit`

```
TransactionServiceImpl.findByIdAndUserId(id, userId)   load existing
TransactionServiceImpl.toDto(transaction)               entity → DTO
CategoryService.findAllForUser(userId)                  populate category dropdown
  → returns pre-filled form
```

---

### 4. Categories — CategoryServiceImpl

#### Create — `POST /categories/new`

```
CategoryDto  (name, type, icon)
  │
  ▼  CategoryServiceImpl.save(dto, userId)
  ├─ dto.getId() != null → load existing category (edit path)
  ├─ UserRepository.findById(userId)         link to owner
  └─ CategoryRepository.save(category)       persist
       Note:  user_id = current user    →  user-owned category
              user_id = NULL            →  system/global category (seeded via data.sql)
```

#### Delete — `POST /categories/{id}/delete`

```
CategoryServiceImpl.delete(id, userId)
  ├─ CategoryRepository.findById(id)
  ├─ category.user == null          → throw "Cannot delete system categories"
  ├─ category.user.id != userId     → throw "Cannot delete system categories"
  └─ CategoryRepository.delete(category)
```

#### List for transaction form dropdowns

```
CategoryService.findAllForUser(userId)
  └─ CategoryRepository.findAllForUser(userId)
       JPQL:  WHERE c.user IS NULL OR c.user.id = :userId
       ORDER BY c.name
            → system categories + user's own categories, merged and sorted A–Z
```

---

## Service Dependency Map

| Service | Injects |
|---|---|
| `UserServiceImpl` | `UserRepository`, `RoleRepository`, `PasswordEncoder` |
| `CategoryServiceImpl` | `CategoryRepository`, `UserRepository` |
| `TransactionServiceImpl` | `TransactionRepository`, `UserRepository`, `CategoryRepository` |
| `DashboardServiceImpl` | `TransactionRepository` *(aggregate queries)*, `TransactionService` *(recent list + toDto)* |

---

## Request → Service → Repository Flow

| URL | Method | Controller | Services Called | Repositories Hit |
|---|---|---|---|---|
| `/login` | Security | Spring Security | `UserServiceImpl.loadUserByUsername()` | `UserRepository` |
| `/register` | POST | `AuthController` | `UserService.register()` | `UserRepository`, `RoleRepository` |
| `/dashboard` | GET | `DashboardController` | `UserService.findByUsername()` → `DashboardService.getDashboard()` | `TransactionRepository` (×4 queries) |
| `/transactions` | GET | `TransactionController` | `UserService` → `TransactionService.findAllByUser()` | `TransactionRepository` (paginated) |
| `/transactions/new` | GET | `TransactionController` | `UserService` → `CategoryService.findAllForUser()` | `CategoryRepository` |
| `/transactions/new` | POST | `TransactionController` | `UserService` → `TransactionService.save()` | `UserRepository`, `CategoryRepository`, `TransactionRepository` |
| `/transactions/{id}/edit` | GET | `TransactionController` | `TransactionService.findByIdAndUserId()` + `toDto()`, `CategoryService.findAllForUser()` | `TransactionRepository`, `CategoryRepository` |
| `/transactions/{id}/edit` | POST | `TransactionController` | `TransactionService.update()` → ownership check | `TransactionRepository`, `UserRepository`, `CategoryRepository` |
| `/transactions/{id}/delete` | POST | `TransactionController` | `TransactionService.delete()` → ownership check | `TransactionRepository` |
| `/categories` | GET | `CategoryController` | `UserService` → `CategoryService.findAllForUser()` | `CategoryRepository` |
| `/categories/new` | GET | `CategoryController` | `UserService` | — |
| `/categories/new` | POST | `CategoryController` | `CategoryService.save()` | `CategoryRepository`, `UserRepository` |
| `/categories/{id}/delete` | POST | `CategoryController` | `CategoryService.delete()` (guards system categories) | `CategoryRepository` |
| `/error` | GET | `AppErrorController` | — | — |

---

## Database Schema

```sql
users        (id, username, email, password, full_name, enabled, created_at)
roles        (id, name)
user_roles   (user_id, role_id)               -- many-to-many join
categories   (id, name, type, icon, user_id)  -- user_id NULL = system category
transactions (id, user_id, category_id, amount, type, description, transaction_date, created_at)
```

Schema is created on startup from `src/main/resources/sql/schema.sql`.  
Seed data (demo user, 12 default categories, sample transactions) is loaded from `src/main/resources/sql/data.sql`.

---

## Security Notes

- Passwords are stored as **BCrypt** hashes (never plaintext).
- Every authenticated URL is guarded by Spring Security's filter chain.
- Transaction and category **ownership is enforced at the service layer** — a user can never read, edit, or delete another user's data.
- System categories (`user_id IS NULL`) cannot be deleted by any user.
- CSRF protection is enabled for all endpoints except `/h2-console/**` (dev only).
