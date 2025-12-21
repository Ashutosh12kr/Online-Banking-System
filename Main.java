import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

/* 
   1. INTERFACE
 */
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
            throw new IllegalArgumentException("Invalid Amount");
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
            throw new Exception("Invalid Amount");
        if (amt > balance)
            throw new Exception("Insufficient Balance");
        balance -= amt;
    }
}

/*
   4. COLLECTIONS & GENERICS
 */
class AccountCache {
    public static Map<Integer, IAccount> cache = new HashMap<>();
}

/*
   5. JDBC CONNECTION
 */
class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/bank_db";
    private static final String USER = "root";
    private static final String PASS = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.out.println("Driver Load Error");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

/*
   6. DAO CLASS
 */
class AccountDAO {

    public void save(Account a) throws Exception {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps =
            con.prepareStatement("INSERT INTO accounts VALUES (?,?,?,?)");
        ps.setInt(1, a.id);
        ps.setString(2, a.name);
        ps.setString(3, a.password);
        ps.setDouble(4, a.balance);
        ps.executeUpdate();
        con.close();
    }

    public IAccount get(int id) throws Exception {
        if (AccountCache.cache.containsKey(id))
            return AccountCache.cache.get(id);

        Connection con = DBConnection.getConnection();
        PreparedStatement ps =
            con.prepareStatement("SELECT * FROM accounts WHERE id=?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) return null;

        IAccount acc = new SavingsAccount(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("password"),
            rs.getDouble("balance")
        );
        AccountCache.cache.put(id, acc);
        con.close();
        return acc;
    }

    public void update(IAccount acc) throws Exception {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps =
            con.prepareStatement("UPDATE accounts SET balance=? WHERE id=?");
        ps.setDouble(1, acc.getBalance());
        ps.setInt(2, acc.getAccountId());
        ps.executeUpdate();
        con.close();
    }
}

/* 
   7. MULTITHREADING
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
                System.out.println("Transaction Failed");
            }
        }
    }
}

/*
   8. GUI (SWING)
 */
public class OnlineBankingSystem extends JFrame {

    JTextField idField, nameField, amountField;
    JPasswordField passField;
    JTextArea output;

    AccountDAO dao = new AccountDAO();
    IAccount current;

    public OnlineBankingSystem() {
        setTitle("Online Banking System");
        setSize(500, 450);
        setLayout(new GridLayout(8, 2));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        idField = new JTextField();
        nameField = new JTextField();
        passField = new JPasswordField();
        amountField = new JTextField();
        output = new JTextArea();

        JButton create = new JButton("Create");
        JButton login = new JButton("Login");
        JButton deposit = new JButton("Deposit");
        JButton withdraw = new JButton("Withdraw");

        add(new JLabel("Account ID")); add(idField);
        add(new JLabel("Name")); add(nameField);
        add(new JLabel("Password")); add(passField);
        add(new JLabel("Amount")); add(amountField);

        add(create); add(login);
        add(deposit); add(withdraw);
        add(new JLabel("Output")); add(new JScrollPane(output));

        create.addActionListener(e -> createAccount());
        login.addActionListener(e -> login());
        deposit.addActionListener(e -> doTransaction(true));
        withdraw.addActionListener(e -> doTransaction(false));

        setVisible(true);
    }

    void createAccount() {
        try {
            int id = new Random().nextInt(9000) + 1000;
            Account acc = new SavingsAccount(
                id,
                nameField.getText(),
                new String(passField.getPassword()),
                Double.parseDouble(amountField.getText())
            );
            dao.save(acc);
            output.setText("Account Created\nID: " + id);
        } catch (Exception e) {
            output.setText("Creation Failed");
        }
    }

    void login() {
        try {
            int id = Integer.parseInt(idField.getText());
            current = dao.get(id);
            if (current != null &&
                ((Account) current).checkPassword(
                    new String(passField.getPassword()))) {
                output.setText("Login Success\nBalance: " +
                    current.getBalance());
            } else {
                output.setText("Login Failed");
            }
        } catch (Exception e) {
            output.setText("Error");
        }
    }

    void doTransaction(boolean dep) {
        try {
            double amt = Double.parseDouble(amountField.getText());
            Thread t = new TransactionThread(current, amt, dep);
            t.start();
            t.join();
            dao.update(current);
            output.setText("Balance: " + current.getBalance());
        } catch (Exception e) {
            output.setText("Transaction Error");
        }
    }

    public static void main(String[] args) {
        new OnlineBankingSystem();
    }
}

/*
   9. SERVLETS 
 */
class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse res)
            throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String pass = req.getParameter("password");
        try {
            AccountDAO dao = new AccountDAO();
            Account acc = (Account) dao.get(id);
            res.getWriter().println(
                acc != null && acc.checkPassword(pass)
                ? "Login Success"
                : "Login Failed"
            );
        } catch (Exception e) {
            res.getWriter().println("Error");
        }
    }
}
