# 🏠 RentPal — Property Rental Management System

RentPal is a full‑stack Java application that simplifies property rental🏠 management for owners and tenants.  
It includes a **Spring Boot backend (REST APIs)** and a **JavaFX frontend** for a rich, interactive desktop experience.

---

## 📁 Project Structure

```
RENTPAL/
│
├── rentpal-backend/                # Spring Boot backend
│   ├── src/main/java/com/rentpal/rentpal_backend/
│   │   ├── config/                 # Database and security configurations
│   │   ├── controller/             # REST Controllers (Auth, Owner, Tenant, Payment, etc.)
│   │   ├── dto/                    # Data Transfer Objects (Create, Update, Summary)
│   │   ├── exception/              # Exception handling and error responses
│   │   ├── model/                  # JPA entity models
│   │   ├── repository/             # Spring Data JPA repositories
│   │   ├── service/                # Business logic services
│   │   └── RentPalBackendApplication.java
│   ├── src/main/resources/
│   │   └── application.properties  # DB + server configurations
│   ├── build.gradle
│   └── rentpal_new.db              # SQLite database
│
├── rentpal-frontend/               # JavaFX frontend
│   ├── src/main/java/com/rentpal/
│   │   ├── controllers/            # UI controllers (auth, tenants, payments, dashboards...)
│   │   ├── dto/                    # DTOs for frontend requests/responses
│   │   ├── service/                # Business layer calling backend APIs
│   │   └── utils/                  # Helpers (SceneSwitcher, SessionManager, ApiUtil)
│   ├── src/main/resources/com/rentpal/
│   │   ├── fxml/                   # FXML layouts for pages
│   │   └── css/                    # Styling files (theme.css, login.css...)
│   ├── pom.xml
│   └── Main.java                   # JavaFX entry point
│
└── README.md
```

---

## ⚙️ Backend — Spring Boot

**Tech Stack**
- Spring Boot 3.x  
- Spring Data JPA  
- SQLite Database  
- Gradle build system  

**Database Config (application.properties)**
```properties
spring.datasource.url=jdbc:sqlite:rentpal_new.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update
server.port=8080
```

**Common Endpoints**
| Method | Endpoint | Description |
|---------|-----------|-------------|
| `POST` | `/api/auth/login` | Authenticate user |
| `POST` | `/api/auth/register` | Register new owner or tenant |
| `GET` | `/api/owners` | Get all owners |
| `GET` | `/api/tenants` | Get all tenants |
| `POST` | `/api/payments` | Add payment |
| `POST` | `/api/complaints` | Add complaint |

To run backend:
```bash
cd rentpal-backend
./gradlew bootRun
```

---

## 💻 Frontend — JavaFX (Maven)

**Tech Stack**
- JavaFX 21  
- FXML + CSS for UI  
- REST API integration via `ApiUtil.java`  
- Scene management via `SceneSwitcher.java`  

**Run frontend**
```bash
cd rentpal-frontend
mvn javafx:run
```

**FXML Layouts**
| Page | FXML | Controller |
|------|------|-------------|
| Login | `login.fxml` | `LoginController.java` |
| Signup | `signup.fxml` | `SignupController.java` |
| Tenant Dashboard | `tenant_dashboard.fxml` | `TenantDashboardController.java` |
| Owner Dashboard | `owner_dashboard.fxml` | `OwnerDashboardController.java` |
| Tenant Home | `tenant_home.fxml` | `TenantHomeController.java` |

**Styling**
Theme CSS file: `src/main/resources/com/rentpal/css/theme.css`  
Example:
```css
.card {
    -fx-background-color: white;
    -fx-background-radius: 16;
    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 16, 0.2, 0, 6);
}
```

---

## 🔄 Communication Flow

1. **Frontend (JavaFX)** sends HTTP requests via `ApiUtil.java` to backend APIs.  
2. **Backend (Spring Boot)** handles logic and persists data to SQLite.  
3. Responses are mapped to DTOs and displayed in the UI.

---

## 🚀 Setup Steps

### 1️⃣ Run Backend
```bash
cd rentpal-backend
./gradlew clean build
./gradlew bootRun
```

### 2️⃣ Run Frontend
```bash
cd rentpal-frontend
mvn clean javafx:run
```

Ensure backend is running at `http://localhost:8080` before launching frontend.

---

## 🧩 Key Features

✅ Role-based Login (Owner / Tenant)  
✅ Tenant & Owner Signup  
✅ Payment Management  
✅ Complaint Submission & Tracking  
✅ Dashboard Views  
✅ SQLite lightweight DB  
✅ JavaFX Modern UI with Theme CSS  

---

## 👩‍💻 Author
**Alekhya Ayinam**  
🎓 M.S. Computer Science – University of South Florida  
🔗 [GitHub](https://github.com/ayinam-alekhya)
