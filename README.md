# Online Banking System (Java GUI)

## Overview
This is a **Java-based Online Banking System** with GUI, implementing core banking operations such as account creation, login, deposit, and withdrawal. The system integrates **OOP principles**, **JDBC database connectivity**, **multithreading**, and **event-driven GUI** using Swing.

---

## Features

### 1. OOP Implementation
- **Interface (`IAccount`)** for account operations
- **Abstract class (`Account`)** for common account properties
- **Inheritance & Polymorphism** via `SavingsAccount` class
- **Exception Handling** for invalid operations

### 2. GUI (Event Handling & Processing)
- Built with **Java Swing**
- Responsive buttons: Create Account, Login, Deposit, Withdraw
- Input validation for account creation and transactions
- Event handling via **ActionListener** and **Lambda expressions**

### 3. Collections & Generics
- `Map<Integer, IAccount>` used to store accounts in memory

### 4. Multithreading & Synchronization
- Transaction operations (deposit/withdraw) use **threads**
- `synchronized` methods ensure thread safety

### 5. Database Connectivity (JDBC)
- MySQL database used to store account data
- DAO class handles **CRUD operations**
- JDBC ensures persistent storage

### 6. Data Validation
- Amounts cannot be negative or zero
- Password and account ID verified during login
- GUI displays error messages for invalid input

### 7. Innovation / Extra Effort
- GUI integrated with multithreading and database
- Real-time updates in GUI after transactions
- Thread-safe transactions using `TransactionThread`

---

## Setup Instructions

1. **Database Setup**
   - Create MySQL database `bank_db`
   - Table structure:
     ```sql
     CREATE TABLE accounts (
       id INT PRIMARY KEY,
       name VARCHAR(50),
       password VARCHAR(50),
       balance DOUBLE
     );
     ```
2. **Run the Application**
   - Ensure MySQL server is running
   - Compile and run `OnlineBankingSystemGUI.java`
   - GUI will open for interaction

---

## Usage

1. **Create Account:** Enter Name, Password, Initial Deposit → Click *Create Account*
2. **Login:** Enter Account ID & Password → Click *Login*
3. **Deposit / Withdraw:** Enter Amount → Click respective button
4. **View Balance:** After login, balance is displayed in GUI output area

---

## Project Rubric Mapping

| Feature | Implemented |
|---------|------------|
| OOP (Polymorphism, Inheritance, Exception Handling, Interfaces) | ✅ |
| Collections & Generics | ✅ |
| Multithreading & Synchronization | ✅ |
| Classes for DB operations | ✅ |
| Database Connectivity (JDBC) | ✅ |
| GUI & Event Handling | ✅ |
| Input Validation | ✅ |
| Innovation / Extra Effort | ✅ |

---

## Author
- Your Name  
- Email / GitHub

---

## License
This project is for academic purposes.
