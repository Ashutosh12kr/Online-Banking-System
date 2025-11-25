import java.sql.*;
import java.util.*;

  // 1) INTERFACE (OOP)

interface IAccount {
    void deposit(double amt);
    void withdraw(double amt) throws Exception;
    double getBalance();
    int getAccountId();
    String getName();
}


  // 2) ABSTRACT CLASS (Inheritance base)

abstract class Account implements IAccount {
    protected int id;
    protected String name;
    protected String password;
    protected double balance;

    public Account(int id, String name, String pass, double bal) {
        this.id = id;
        this.name = name;
        this.password = pass;
        this.balance = bal;
    }

    public int getAccountId() { return id; }
    public String getName() { return name; }
    public double getBalance() { return balance; }

    public boolean checkPassword(String p) {
        return password.equals(p);
    }

    public synchronized void deposit(double amt) {
        if (amt <= 0) throw new IllegalArgumentException("Deposit must be > 0");
        balance += amt;
    }
}


 //  3) CHILD CLASSES (Polymorphism + Inheritance)

class SavingsAccount extends Account {
    public SavingsAccount(int id, String n, String p, double bal) {
        super(id, n, p, bal);
    }

    @Override
    public synchronized void withdraw(double amt) throws Exception {
        if (amt <= 0) throw new Exception("Amount must be > 0");
        if (amt > balance) throw new Exception("Insufficient funds");
        balance -= amt;
    }
}

class CurrentAccount extends Account {
    private double overdraftLimit = 1000;

    public CurrentAccount(int id, String n, String p, double bal) {
        super(id, n, p, bal);
    }

    @Override
    public synchronized void withdraw(double amt) throws Exception {
        if (amt <= 0) throw new Exception("Amount must be > 0");
        if (amt > balance + overdraftLimit)
            throw new Exception("Overdraft Limit exceeded");
        balance -= amt;
    }
}


 //  4) DATABASE CONNECTION CLASS

class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/bank_db";
    private static final String USER = "root";
    private static final String PASS = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.out.println("Driver Error: " + e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}


 //  5) DAO — Database CRUD operations

class AccountDAO {

    public void saveAccount(IAccount acc) throws Exception {
        Account a = (Account) acc;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO accounts (id, name, password, balance) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, a.id);
            ps.setString(2, a.name);
            ps.setString(3, a.password);
            ps.setDouble(4, a.balance);
            ps.executeUpdate();
        }
    }

    public IAccount getAccount(int id) throws Exception {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            return new SavingsAccount(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("password"),
                    rs.getDouble("balance")
            );
        }
    }

    public void updateBalance(IAccount acc) throws Exception {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE accounts SET balance=? WHERE id=?")) {
            ps.setDouble(1, acc.getBalance());
            ps.setInt(2, acc.getAccountId());
            ps.executeUpdate();
        }
    }

    public List<IAccount> getAllAccounts() throws Exception {
        List<IAccount> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new SavingsAccount(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getDouble("balance")
                ));
            }
        }
        return list;
    }
}


 //  6) THREADING — Deposit/Withdraw

class TransactionThread extends Thread {
    private IAccount acc;
    private double amount;
    private boolean deposit;

    public TransactionThread(IAccount acc, double amt, boolean deposit) {
        this.acc = acc;
        this.amount = amt;
        this.deposit = deposit;
    }

    @Override
    public void run() {
        synchronized (acc) {
            try {
                if (deposit) acc.deposit(amount);
                else acc.withdraw(amount);

                System.out.println(Thread.currentThread().getName() +
                        " Completed. New Balance: " + acc.getBalance());
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " FAILED: " + e.getMessage());
            }
        }
    }
}

 //  7) MAIN BANKING SYSTEM
   //7) MAIN BANKING SYSTEM

public class OnlineBankingSystem {

    private static Scanner sc = new Scanner(System.in);
    private static AccountDAO dao = new AccountDAO();

    // In-memory map for Collections & Generics mark
    private static Map<Integer, IAccount> accountMap = new HashMap<>();

    public static void main(String[] args) throws Exception {
        // Load accounts from DB into map at startup
        List<IAccount> accounts = dao.getAllAccounts();
        for (IAccount acc : accounts) {
            accountMap.put(acc.getAccountId(), acc);
        }

        while (true) {
            System.out.println("\n1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. List Accounts");
            System.out.println("4. Exit");
            System.out.print("Select: ");
            int ch = sc.nextInt(); sc.nextLine();

            switch (ch) {
                case 1 -> createAccount();
                case 2 -> login();
                case 3 -> listAccounts();
                case 4 -> System.exit(0);
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void createAccount() {
        try {
            System.out.print("Name: ");
            String name = sc.nextLine();

            System.out.print("Password: ");
            String pass = sc.nextLine();

            System.out.print("Initial Deposit: ");
            double bal = sc.nextDouble(); sc.nextLine();

            int id = new Random().nextInt(9000) + 1000;
            IAccount acc = new SavingsAccount(id, name, pass, bal);

            // Save in-memory
            accountMap.put(id, acc);

            // Save in DB
            dao.saveAccount(acc);

            System.out.println("Account Created. Your ID: " + id);
        } catch (Exception e) {
            System.out.println("Create ERROR: " + e);
        }
    }

    private static void login() {
        try {
            System.out.print("Account ID: ");
            int id = sc.nextInt(); sc.nextLine();

            IAccount acc = accountMap.get(id);
            if (acc == null) {
                System.out.println("Not found!");
                return;
            }

            System.out.print("Password: ");
            String p = sc.nextLine();
            if (!((Account) acc).checkPassword(p)) {
                System.out.println("Wrong Password");
                return;
            }

            accountMenu(acc);

        } catch (Exception e) {
            System.out.println("Login ERROR: " + e);
        }
    }

    private static void accountMenu(IAccount acc) throws Exception {
        while (true) {
            System.out.println("\n1. Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Demo Multithread");
            System.out.println("5. Exit");

            System.out.print("Select: ");
            int ch = sc.nextInt(); sc.nextLine();

            switch (ch) {
                case 1 -> System.out.println("Balance: " + acc.getBalance());
                case 2 -> doDeposit(acc);
                case 3 -> doWithdraw(acc);
                case 4 -> doThreads(acc);
                case 5 -> { dao.updateBalance(acc); return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void doDeposit(IAccount acc) throws Exception {
        System.out.print("Amount: ");
        double amt = sc.nextDouble(); sc.nextLine();

        acc.deposit(amt);
        dao.updateBalance(acc);
        System.out.println("Deposited.");
    }

    private static void doWithdraw(IAccount acc) {
        System.out.print("Amount: ");
        double amt = sc.nextDouble(); sc.nextLine();
        try {
            acc.withdraw(amt);
            dao.updateBalance(acc);
            System.out.println("Withdraw OK");
        } catch (Exception e) {
            System.out.println("Withdraw ERROR: " + e.getMessage());
        }
    }

    private static void doThreads(IAccount acc) {
        Thread t1 = new TransactionThread(acc, 200, true);
        Thread t2 = new TransactionThread(acc, 150, false);

        t1.setName("DepositThread");
        t2.setName("WithdrawThread");
        t1.start();
        t2.start();
    }

    private static void listAccounts() {
        if (accountMap.isEmpty()) {
            System.out.println("No accounts exist.");
            return;
        }
        System.out.println("\nAll accounts (in-memory):");
        for (IAccount a : accountMap.values()) {
            System.out.printf("ID: %d | Name: %s | Balance: %.2f\n",
                    a.getAccountId(), a.getName(), a.getBalance());
        }
    }
}
