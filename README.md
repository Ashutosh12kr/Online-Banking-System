Online Banking System - Java Console Application
Overview

OnlineBankingSystem is a console-based banking application implemented in Java. It demonstrates key OOP concepts, collections, multithreading, and database integration (JDBC). Users can create accounts, log in, deposit/withdraw money, and view balances. The system also supports thread-safe transactions and stores account data in a MySQL database.

Features

Account Management

Create new accounts with random account IDs.

Login to existing accounts using account ID and password.

View account balance.

Deposit and withdraw money.

Multithreading

Demo of concurrent deposit and withdrawal using threads.

Synchronized operations to prevent race conditions.

Database Integration

Accounts stored in MySQL database (bank_db) using JDBC.

CRUD operations through AccountDAO class.

Accounts loaded into in-memory Map for fast access.

Account Types

SavingsAccount: Standard withdrawal.

CurrentAccount: Allows overdraft up to a limit.

Collections & Generics

Map<Integer, IAccount> stores accounts in-memory for fast lookup.

Core Concepts Demonstrated
Concept	Implementation
OOP	IAccount interface, Account abstract class, inheritance via SavingsAccount and CurrentAccount
Polymorphism	IAccount acc references both Savings and Current accounts
Exception Handling	Handles insufficient funds, invalid deposit/withdraw amounts
Collections	HashMap<Integer, IAccount> to store accounts in-memory
Multithreading	TransactionThread class with synchronized deposit/withdraw methods
Database (JDBC)	DBConnection and AccountDAO for MySQL operations
Project Structure

OnlineBankingSystem.java – Main class, handles UI and user interactions.

IAccount.java – Interface for account operations.

Account.java – Abstract class for common account logic.

SavingsAccount.java – Implements standard withdrawal rules.

CurrentAccount.java – Implements overdraft rules.

DBConnection.java – Handles MySQL database connection.

AccountDAO.java – Performs CRUD operations in the database.

TransactionThread.java – Demonstrates multithreaded transactions.

Database Setup

Create database

CREATE DATABASE bank_db;


Create accounts table

USE bank_db;

CREATE TABLE accounts (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(50) NOT NULL,
    balance DOUBLE NOT NULL
);


MySQL Connection

Username: root

Password: empty ("")

URL: jdbc:mysql://localhost:3306/bank_db

Make sure MySQL server is running and JDBC driver is available.

How to Compile and Run

Open terminal or command prompt.

Navigate to project folder containing OnlineBankingSystem.java.

Compile:

javac OnlineBankingSystem.java


Run:

java OnlineBankingSystem

Sample Console Interaction
1. Create Account
2. Login
3. List Accounts
4. Exit
Select: 1

Name: John Doe
Password: 1234
Initial Deposit: 5000
Account Created. Your ID: 1023

Select: 2
Account ID: 1023
Password: 1234
Welcome, John Doe!
1. Balance
2. Deposit
3. Withdraw
4. Demo Multithread
5. Exit
Select: 1
Balance: 5000.0

Notes

Thread safety is ensured using synchronized methods in Account and TransactionThread.

Passwords are stored in plain text for demo purposes (not recommended for production).

In-memory Map improves performance by caching accounts while still persisting them to MySQL.

Supports multithreaded deposit and withdrawal demonstrations.
