🏦 Online Banking System (Java Console)

This project is a simple console-based Online Banking System written in Java.
It demonstrates Object-Oriented Programming (OOP) concepts such as classes, encapsulation, and object management.

🚀 Features

✔ Create a new bank account
✔ Login to existing account
✔ Check account balance
✔ Deposit money
✔ Withdraw money
✔ Transfer funds to another account
✔ View account details
✔ Close account (balance must be zero)

🧱 System Architecture
1️⃣ OnlineBankingSystem (Main Class)

Entry point of the program.

Creates a Bank object and starts the menu loop.

2️⃣ Bank (Controller Class)

Manages accounts using a HashMap<Integer, Account>.

Generates unique account IDs (starting from 1001).

Handles:

User menu navigation

Account creation

Login logic

Deposits

Withdrawals

Transfers

Closure of accounts

3️⃣ Account (Model Class)

Represents a single bank account.

Stores:

Account ID

Holder name

Password

Balance

Provides secure operations:

Deposit

Withdraw

Password check

🛠 How to Run
1. Save the file

Save as:

OnlineBankingSystem.java

2. Compile
javac OnlineBankingSystem.java

3. Run
java OnlineBankingSystem

💡 Notes

Account data is stored in-memory only using a HashMap.

Passwords are stored as plain text (for demo only).

This is a console demo, not a production banking system.
