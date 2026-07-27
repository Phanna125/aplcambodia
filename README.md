# Coffee Ordering System (APL Cambodia)

A full-stack coffee shop management system featuring a **Spring Boot REST API backend** and a **Java Swing Desktop Application**. It supports role-based access for Admins, Cashiers, Baristas, and Customers.

---

## 🚀 Quick Start

### 1. Database Setup
- Ensure **MySQL** is running locally (default port `3306`).
- Run the SQL scripts in the `sql/` directory:
  1. `sql/schema.sql` (Creates database structure)
  2. `sql/data.sql` (Seeds initial menu items, users, and roles)
- Update database credentials in `backend/src/main/resources/application.yml` if necessary (Default: `root` / `Firstloveling8$`).

### 2. Launching the System
Simply double-click **`run-all.bat`** (or execute `.\run-all.bat` in terminal).

This script automatically:
1. Auto-detects your `JAVA_HOME` and Maven installations.
2. Launches the Spring Boot REST API backend on port `8080`.
3. Waits for the backend to initialize, then launches the Java Swing Desktop Application.

---

## 🔐 Login Credentials

| Role | Username / Email | Password |
|---|---|---|
| **Admin** | `admin@coffeeshop.com` | `admin123` |
| **Cashier** | `cashier@coffeeshop.com` | `cashier123` |
| **Barista** | `barista@coffeeshop.com` | `barista123` |
| **Customer** | `bopha@gmail.com` | `customer123` |

---

## 📁 Architecture & Components

* **Backend (`/backend`)**: Built with Spring Boot, Spring Security (JWT), Spring Data JPA, Hibernate, and MySQL. Serves RESTful endpoints.
* **Desktop App (`/desktop-app`)**: Java Swing Desktop Client. Consumes backend REST endpoints via HTTP and displays role-based dashboards.
* **Docs (`/docs`)**: Architecture documentation and Decision Records (ADRs).

---

## 🛠️ Individual Commands & Scripts

If you need to run components separately:

| Script / Command | Description |
|---|---|
| `.\run-backend.bat` | Starts the Spring Boot REST API backend |
| `.\run-desktop-app.bat` | Compiles and starts the Java Swing Desktop Client |
| `.\run-all.bat` | Launches both backend and desktop app concurrently |

---

## 💡 Troubleshooting & Environment Notes

### 1. `JAVA_HOME` Environment Variable
The batch scripts automatically scan common JDK installation paths (`Eclipse Adoptium`, `Oracle Java`, `Amazon Corretto`, etc.). If Java is not detected automatically:
- Set `JAVA_HOME` in your Windows Environment Variables to your JDK installation directory (e.g. `C:\Program Files\Eclipse Adoptium\jdk-17...`).
- Add `%JAVA_HOME%\bin` to your system `Path`.

### 2. Windows Encoding Fix (`windows-1252`)
All source code files (including `CoffeeShopApp.java`) use Java Unicode escape sequences (`\uXXXX`) for UI icons and emojis. This ensures clean compilation on all Windows, Mac, and Linux environments without encountering `unmappable character for encoding windows-1252` errors.

### 3. Running in IDEs
When running in VS Code or IntelliJ IDEA, ensure the project is imported as a Maven project so dependencies (`Gson`, `Spring Boot starter`, etc.) are correctly resolved on the classpath.
