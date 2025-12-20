import java.sql.*;
import java.util.*;

/* 
   1. INTERFACE (OOP)
*/
interface IAccount {
    void deposit(double amt);
    void withdraw(double amt) throws Exception;
    double getBalance();
    int getAccountId();
    String getName();
}

/* 
   2. ABSTRACT CLASS (INHERITANCE)
   */
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

    public int getAccountId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public boolean checkPassword(String p) {
        return password.equals(p);
    }

    public synchronized void deposit(double amt) {
        if (amt <= 0)
            throw new IllegalArgumentException("Invalid deposit amount");
        balance += amt;
    }
}

/* 
   3. CHILD CLASS (POLYMORPHISM)
   */
class SavingsAccount extends Account {

    public SavingsAccount(int id, String n, String p, double bal) {
        super(id, n, p, bal);
    }

    @Override
    public synchronized void withdraw(double amt) throws Exception {
        if (amt <= 0)
            throw new Exception("Invalid withdraw amount");
        if (amt > balance)
            throw new Exception("Insufficient balance");
        balance -= amt;
    }
}

/* 
   4. DATABASE CONNECTION (JDBC)
 */
class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/bank_db";
    private static final String USER = "root";
    private static final String PASS = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.out.println("MySQL Driver Load Failed");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

/*
   5. DAO CLASS (DATABASE OPERATIONS)
   */
class AccountDAO {

    public void saveAccount(IAccount acc) throws Exception {
        Account a = (Account) acc;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(
                             "INSERT INTO accounts VALUES (?,?,?,?)")) {

            ps.setInt(1, a.id);
            ps.setString(2, a.name);
            ps.setString(3, a.password);
            ps.setDouble(4, a.balance);
            ps.executeUpdate();
        }
    }

    public IAccount getAccount(int id) throws Exception {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(
                             "SELECT * FROM accounts WHERE id=?")) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next())
                return null;

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
             PreparedStatement ps =
                     con.prepareStatement(
                             "UPDATE accounts SET balance=? WHERE id=?")) {

            ps.setDouble(1, acc.getBalance());
            ps.setInt(2, acc.getAccountId());
            ps.executeUpdate();
        }
    }

    public List<IAccount> getAllAccounts() throws Exception {
        List<IAccount> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement("SELECT * FROM accounts");
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

/* 
   6. MULTITHREADING (TRANSACTIONS)
   */
class TransactionThread extends Thread {

    private IAccount acc;
    private double amt;
    private boolean deposit;

    public TransactionThread(IAccount acc, double amt, boolean dep) {
        this.acc = acc;
        this.amt = amt;
        this.deposit = dep;
    }

    public void run() {
        synchronized (acc) {
            try {
                if (deposit)
                    acc.deposit(amt);
                else
                    acc.withdraw(amt);

                System.out.println(
                        getName() + " | Balance: " + acc.getBalance());
            } catch (Exception e) {
                System.out.println(getName() + " Transaction Failed");
            }
        }
    }
}

/*
   7. MAIN CONSOLE APPLICATION
   */
public class OnlineBankingSystem {

    private static Scanner sc = new Scanner(System.in);
    private static AccountDAO dao = new AccountDAO();
    private static Map<Integer, IAccount> accountMap = new HashMap<>();

    public static void main(String[] args) throws Exception {

        for (IAccount acc : dao.getAllAccounts()) {
            accountMap.put(acc.getAccountId(), acc);
        }

        while (true) {
            System.out.println("\n1.Create Account");
            System.out.println("2.Login");
            System.out.println("3.List Accounts");
            System.out.println("4.Exit");
            System.out.print("Choice: ");

            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1 -> createAccount();
                case 2 -> login();
                case 3 -> listAccounts();
                case 4 -> System.exit(0);
                default -> System.out.println("Invalid choice");
            }
        }
    }

    static void createAccount() throws Exception {
        System.out.print("Name: ");
        String n = sc.nextLine();

        System.out.print("Password: ");
        String p = sc.nextLine();

        System.out.print("Initial Deposit: ");
        double b = sc.nextDouble();

        int id = new Random().nextInt(9000) + 1000;
        IAccount acc = new SavingsAccount(id, n, p, b);

        dao.saveAccount(acc);
        accountMap.put(id, acc);

        System.out.println("Account Created Successfully");
        System.out.println("Account ID: " + id);
    }

    static void login() throws Exception {
        System.out.print("Account ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        IAccount acc = accountMap.get(id);

        if (acc == null) {
            System.out.println("Account Not Found");
            return;
        }

        System.out.print("Password: ");
        if (!((Account) acc).checkPassword(sc.nextLine())) {
            System.out.println("Wrong Password");
            return;
        }

        System.out.println("Login Successful");

        Thread t1 = new TransactionThread(acc, 500, true);
        Thread t2 = new TransactionThread(acc, 300, false);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        dao.updateBalance(acc);
    }

    static void listAccounts() {
        System.out.println("\n--- Accounts ---");
        for (IAccount a : accountMap.values()) {
            System.out.println(
                    a.getAccountId() + " | " +
                    a.getName() + " | Balance: " +
                    a.getBalance());
        }
    }
}
