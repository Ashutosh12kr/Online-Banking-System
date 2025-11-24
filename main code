import java.util.*;

/**
 * Simple console-based online banking demo.
 * Save as OnlineBankingSystem.java
 *
 * Compile: javac OnlineBankingSystem.java
 * Run:     java OnlineBankingSystem
 */
public class OnlineBankingSystem {
    public static void main(String[] args) {
        Bank bank = new Bank();
        bank.run(); // starts interactive console
    }
}

/* ---------------------------
   Bank class: manages accounts
   --------------------------- */
class Bank {
    private final Map<Integer, Account> accounts = new HashMap<>();
    private int nextAccountId = 1001; // starting account number
    private final Scanner sc = new Scanner(System.in);

    public void run() {
        System.out.println("Welcome to Demo Online Bank");
        boolean exit = false;
        while (!exit) {
            printMainMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": createAccount(); break;
                case "2": login(); break;
                case "3": listAccounts(); break; // admin / debug (optional)
                case "4":
                    System.out.println("Thank you for using Demo Online Bank. Goodbye!");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice — try again.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Create new account");
        System.out.println("2. Login to existing account");
        System.out.println("3. (Debug) List all accounts");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }

    private void createAccount() {
        System.out.println("\n--- Create New Account ---");
        System.out.print("Full name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        String password;
        while (true) {
            System.out.print("Create password (min 4 chars): ");
            password = sc.nextLine();
            if (password.length() >= 4) break;
            System.out.println("Password too short.");
        }

        double initialDeposit = readPositiveDouble("Initial deposit (>=0): ");

        int accId = nextAccountId++;
        Account acc = new Account(accId, name, password, initialDeposit);
        accounts.put(accId, acc);

        System.out.printf("Account created! Account ID: %d\n", accId);
    }

    private void login() {
        System.out.println("\n--- Login ---");
        Integer accId = readInt("Enter Account ID: ");
        if (accId == null) return;

        Account acc = accounts.get(accId);
        if (acc == null) {
            System.out.println("Account not found.");
            return;
        }

        System.out.print("Enter password: ");
        String password = sc.nextLine();
        if (!acc.checkPassword(password)) {
            System.out.println("Authentication failed.");
            return;
        }

        System.out.printf("Welcome, %s!\n", acc.getName());
        accountMenu(acc);
    }

    private void accountMenu(Account acc) {
        boolean logout = false;
        while (!logout) {
            printAccountMenu();
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1": System.out.printf("Balance: %.2f\n", acc.getBalance()); break;
                case "2": depositTo(acc); break;
                case "3": withdrawFrom(acc); break;
                case "4": transferFrom(acc); break;
                case "5": showDetails(acc); break;
                case "6": closeAccount(acc); logout = true; break;
                case "7": System.out.println("Logging out..."); logout = true; break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private void printAccountMenu() {
        System.out.println("\nAccount Menu:");
        System.out.println("1. Check balance");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer to another account");
        System.out.println("5. Account details");
        System.out.println("6. Close account");
        System.out.println("7. Logout");
        System.out.print("Choose an option: ");
    }

    private void depositTo(Account acc) {
        double amt = readPositiveDouble("Enter deposit amount: ");
        if (amt <= 0) return;
        acc.deposit(amt);
        System.out.printf("Deposited %.2f. New balance: %.2f\n", amt, acc.getBalance());
    }

    private void withdrawFrom(Account acc) {
        double amt = readPositiveDouble("Enter withdraw amount: ");
        if (amt <= 0) return;
        try {
            acc.withdraw(amt);
            System.out.printf("Withdrawn %.2f. New balance: %.2f\n", amt, acc.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println("Withdraw failed: " + e.getMessage());
        }
    }

    private void transferFrom(Account acc) {
        Integer targetId = readInt("Enter target Account ID: ");
        if (targetId == null) return;
        Account target = accounts.get(targetId);
        if (target == null) {
            System.out.println("Target account not found.");
            return;
        }
        double amt = readPositiveDouble("Enter transfer amount: ");
        if (amt <= 0) return;
        try {
            acc.withdraw(amt); // will throw if insufficient
            target.deposit(amt);
            System.out.printf("Transferred %.2f from %d to %d\n", amt, acc.getAccountId(), targetId);
            System.out.printf("Your new balance: %.2f\n", acc.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }

    private void showDetails(Account acc) {
        System.out.println("\n--- Account Details ---");
        System.out.println("Account ID : " + acc.getAccountId());
        System.out.println("Holder Name: " + acc.getName());
        System.out.printf("Balance    : %.2f\n", acc.getBalance());
    }

    private void closeAccount(Account acc) {
        System.out.print("Are you sure you want to close your account? (yes/no): ");
        String ans = sc.nextLine().trim().toLowerCase();
        if (!ans.equals("yes")) {
            System.out.println("Canceling close.");
            return;
        }
        if (acc.getBalance() != 0.0) {
            System.out.println("Account must have zero balance to close (withdraw or transfer remaining funds).");
            return;
        }
        accounts.remove(acc.getAccountId());
        System.out.println("Account closed.");
    }

    // Debug helper to show all accounts (not for production)
    private void listAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts exist.");
            return;
        }
        System.out.println("\nAll accounts (debug):");
        for (Account a : accounts.values()) {
            System.out.printf("ID: %d | Name: %s | Balance: %.2f\n", a.getAccountId(), a.getName(), a.getBalance());
        }
    }

    /* ---------- Input helpers ---------- */
    private Integer readInt(String prompt) {
        System.out.print(prompt);
        String line = sc.nextLine().trim();
        if (line.isEmpty()) {
            System.out.println("Input required.");
            return null;
        }
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return null;
        }
    }

    private double readPositiveDouble(String prompt) {
        System.out.print(prompt);
        String line = sc.nextLine().trim();
        if (line.isEmpty()) {
            System.out.println("Input required.");
            return -1;
        }
        try {
            double v = Double.parseDouble(line);
            if (v < 0) {
                System.out.println("Amount must be non-negative.");
                return -1;
            }
            return v;
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return -1;
        }
    }
}

/* ---------------------------
   Account class
   --------------------------- */
class Account {
    private final int accountId;
    private final String name;
    private final String password; // demo only: plain text
    private double balance;

    public Account(int accountId, String name, String password, double initialBalance) {
        this.accountId = accountId;
        this.name = name;
        this.password = password;
        this.balance = Math.max(0.0, initialBalance);
    }

    public int getAccountId() { return accountId; }
    public String getName() { return name; }
    public double getBalance() { return balance; }

    public boolean checkPassword(String attempt) {
        return password.equals(attempt);
    }

    public synchronized void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        balance += amount;
    }

    public synchronized void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdraw amount must be positive.");
        if (amount > balance) throw new IllegalArgumentException("Insufficient funds.");
        balance -= amount;
    }
}
