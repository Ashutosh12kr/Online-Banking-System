import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

/* 
   1. INTERFACE
======================= */
interface IAccount {
    void deposit(double amt);
    void withdraw(double amt) throws Exception;
    double getBalance();
    int getAccountId();
    String getName();
}

/* 
   2. ABSTRACT CLASS
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

    public int getAccountId() { return id; }
    public String getName() { return name; }
    public double getBalance() { return balance; }

    public boolean checkPassword(String p) {
        return password.equals(p);
    }

    public synchronized void deposit(double amt) {
        if (amt <= 0)
            throw new IllegalArgumentException("Invalid amount");
        balance += amt;
    }
}

/* 
   3. CHILD CLASS
 */
class SavingsAccount extends Account {
    public SavingsAccount(int id, String n, String p, double bal) {
        super(id, n, p, bal);
    }

    @Override
    public synchronized void withdraw(double amt) throws Exception {
        if (amt <= 0)
            throw new Exception("Invalid amount");
        if (amt > balance)
            throw new Exception("Insufficient balance");
        balance -= amt;
    }
}

/* 
   4. JDBC CONNECTION
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
            System.out.println("Driver error");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

/* 
   5. DAO CLASS
 */
class AccountDAO {

    public void save(IAccount acc) throws Exception {
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

    public IAccount get(int id) throws Exception {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
               con.prepareStatement(
                 "SELECT * FROM accounts WHERE id=?")) {

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

    public void update(IAccount acc) throws Exception {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
               con.prepareStatement(
                 "UPDATE accounts SET balance=? WHERE id=?")) {

            ps.setDouble(1, acc.getBalance());
            ps.setInt(2, acc.getAccountId());
            ps.executeUpdate();
        }
    }
}

/* 
   6. MULTITHREADING
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
                if (deposit) acc.deposit(amt);
                else acc.withdraw(amt);
            } catch (Exception e) {
                System.out.println("Transaction failed");
            }
        }
    }
}

/* =======================
   7. GUI MAIN CLASS
======================= */
public class OnlineBankingSystemGUI extends JFrame {

    private JTextField idField, nameField, amountField;
    private JPasswordField passField;
    private JTextArea output;

    private AccountDAO dao = new AccountDAO();
    private IAccount currentAccount;

    public OnlineBankingSystemGUI() {
        setTitle("Online Banking System");
        setSize(500, 450);
        setLayout(new GridLayout(8, 2));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        idField = new JTextField();
        nameField = new JTextField();
        passField = new JPasswordField();
        amountField = new JTextField();
        output = new JTextArea();

        JButton create = new JButton("Create Account");
        JButton login = new JButton("Login");
        JButton deposit = new JButton("Deposit");
        JButton withdraw = new JButton("Withdraw");

        add(new JLabel("Account ID"));
        add(idField);
        add(new JLabel("Name"));
        add(nameField);
        add(new JLabel("Password"));
        add(passField);
        add(new JLabel("Amount"));
        add(amountField);

        add(create);
        add(login);
        add(deposit);
        add(withdraw);

        add(new JLabel("Output"));
        add(new JScrollPane(output));

        create.addActionListener(e -> createAccount());
        login.addActionListener(e -> login());
        deposit.addActionListener(e -> deposit());
        withdraw.addActionListener(e -> withdraw());

        setVisible(true);
    }

    private void createAccount() {
        try {
            int id = new Random().nextInt(9000) + 1000;
            IAccount acc = new SavingsAccount(
                id,
                nameField.getText(),
                new String(passField.getPassword()),
                Double.parseDouble(amountField.getText())
            );
            dao.save(acc);
            output.setText("Account created\nID: " + id);
        } catch (Exception e) {
            output.setText("Error creating account");
        }
    }

    private void login() {
        try {
            int id = Integer.parseInt(idField.getText());
            currentAccount = dao.get(id);

            if (currentAccount != null &&
                ((Account) currentAccount)
                .checkPassword(new String(passField.getPassword()))) {

                output.setText("Login successful\nBalance: "
                    + currentAccount.getBalance());
            } else {
                output.setText("Login failed");
            }
        } catch (Exception e) {
            output.setText("Error");
        }
    }

    private void deposit() {
        try {
            double amt = Double.parseDouble(amountField.getText());
            Thread t = new TransactionThread(currentAccount, amt, true);
            t.start();
            t.join();
            dao.update(currentAccount);
            output.setText("Deposited\nBalance: "
                + currentAccount.getBalance());
        } catch (Exception e) {
            output.setText("Deposit failed");
        }
    }

    private void withdraw() {
        try {
            double amt = Double.parseDouble(amountField.getText());
            Thread t = new TransactionThread(currentAccount, amt, false);
            t.start();
            t.join();
            dao.update(currentAccount);
            output.setText("Withdrawn\nBalance: "
                + currentAccount.getBalance());
        } catch (Exception e) {
            output.setText(e.getMessage());
        }
    }

    public static void main(String[] args) {
        new OnlineBankingSystemGUI();
    }
}
