# 🏦 Online Banking System (Java)

## 📌 Project Description
The **Online Banking System** is a Java-based desktop application developed using **Core Java, Swing, JDBC, Multithreading, and Servlets**.  
It simulates basic banking operations such as account creation, login, deposit, withdrawal, and balance enquiry with proper **event handling, data validation, and database connectivity**.

This project demonstrates **Object-Oriented Programming concepts** and is suitable for **3rd Semester / Mini Project / Academic Submission**.

---

## 🚀 Features
- Create new bank account
- Secure login using password
- Deposit and withdraw money
- Real-time balance update
- Multithreaded transaction handling
- JDBC connectivity with MySQL
- Swing-based GUI
- Servlet-based login (web support)
- Data validation & exception handling

---

## 🛠️ Technologies Used
- **Language:** Java  
- **GUI:** Swing (JFrame, JButton, JTextField, JTextArea)  
- **Database:** MySQL  
- **Connectivity:** JDBC  
- **Web:** Servlet API  
- **Multithreading:** Thread class  
- **Collections:** HashMap, Map  

---

## 🧩 OOP Concepts Used
- Interface (`IAccount`)
- Abstract Class (`Account`)
- Inheritance (`SavingsAccount`)
- Polymorphism
- Encapsulation
- Exception Handling

---

## ⚙️ System Architecture
GUI (Swing)
↓
Event Handling (ActionListener)
↓
Business Logic (OOP + Threads)
↓
DAO Layer (JDBC)
↓
MySQL Database

yaml
Copy code

---

## 📂 Project Structure
OnlineBankingSystem/
│
├── IAccount.java
├── Account.java
├── SavingsAccount.java
├── AccountCache.java
├── DBConnection.java
├── AccountDAO.java
├── TransactionThread.java
├── OnlineBankingSystem.java
└── LoginServlet.java

pgsql
Copy code

---

## 🗄️ Database Structure
```sql
CREATE DATABASE bank_db;

CREATE TABLE accounts (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    password VARCHAR(50),
    balance DOUBLE
);
🖱️ Event Handling & Processing
Buttons act as event sources

ActionListener handles user actions

Methods like createAccount(), login(), doTransaction() process events

Multithreading ensures safe transactions

🔐 Data Validation
Empty field checks

Positive amount validation

Password length validation

Login verification

Null object protection

🌐 Servlet Support
LoginServlet handles HTTP POST requests

Validates user credentials

Returns login success or failure message







