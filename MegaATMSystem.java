import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/*
    ============================================
    MEGA ATM BANKING SYSTEM
    Single File Version
    Academic + Professional Style
    ============================================
*/

public class MegaATMSystem {

    /* ===============================
       INNER USER CLASS
    =============================== */
    static class User implements Serializable {
        String accountNumber;
        int pin;
        double balance;
        ArrayList<String> transactions;

        public User(String acc, int pin) {
            this.accountNumber = acc;
            this.pin = pin;
            this.balance = 0;
            this.transactions = new ArrayList<>();
        }
    }

    /* ===============================
       GLOBAL VARIABLES
    =============================== */

    static HashMap<String, User> users = new HashMap<>();
    static User currentUser = null;
    static final String FILE_NAME = "mega_atm_data.dat";

    /* ===============================
       MAIN METHOD
    =============================== */

    public static void main(String[] args) {
        loadData();
        SwingUtilities.invokeLater(() -> showLogin());
    }

    /* ===============================
       FILE HANDLING
    =============================== */

    static void loadData() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME));
            users = (HashMap<String, User>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            users = new HashMap<>();
        }
    }

    static void saveData() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME));
            oos.writeObject(users);
            oos.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error saving data!");
        }
    }

    /* ===============================
       LOGIN SCREEN
    =============================== */

    static void showLogin() {
        JFrame frame = new JFrame("Mega ATM - Login");
        frame.setSize(400, 350);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel title = new JLabel("WELCOME TO MEGA ATM", JLabel.CENTER);
        title.setBounds(50, 20, 300, 30);
        frame.add(title);

        JLabel accLabel = new JLabel("Account Number:");
        accLabel.setBounds(60, 80, 120, 25);
        frame.add(accLabel);

        JTextField accField = new JTextField();
        accField.setBounds(190, 80, 130, 25);
        frame.add(accField);

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setBounds(60, 120, 120, 25);
        frame.add(pinLabel);

        JPasswordField pinField = new JPasswordField();
        pinField.setBounds(190, 120, 130, 25);
        frame.add(pinField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(60, 180, 110, 30);
        frame.add(loginBtn);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBounds(210, 180, 110, 30);
        frame.add(registerBtn);

        JButton adminBtn = new JButton("Admin");
        adminBtn.setBounds(135, 230, 110, 30);
        frame.add(adminBtn);

        /* LOGIN ACTION */
        loginBtn.addActionListener(e -> {
            String acc = accField.getText();
            String pinText = new String(pinField.getPassword());

            if (users.containsKey(acc) && users.get(acc).pin == Integer.parseInt(pinText)) {
                currentUser = users.get(acc);
                frame.dispose();
                showDashboard();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Credentials!");
            }
        });

        /* REGISTER ACTION */
        registerBtn.addActionListener(e -> {
            String acc = accField.getText();
            String pinText = new String(pinField.getPassword());

            if (users.containsKey(acc)) {
                JOptionPane.showMessageDialog(frame, "Account already exists!");
            } else {
                users.put(acc, new User(acc, Integer.parseInt(pinText)));
                saveData();
                JOptionPane.showMessageDialog(frame, "Account Created Successfully!");
            }
        });

        /* ADMIN PANEL */
        adminBtn.addActionListener(e -> {
            String password = JOptionPane.showInputDialog("Enter Admin Password:");
            if ("admin123".equals(password)) {
                frame.dispose();
                showAdminPanel();
            } else {
                JOptionPane.showMessageDialog(frame, "Wrong Password!");
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /* ===============================
       DASHBOARD
    =============================== */

    static void showDashboard() {
        JFrame frame = new JFrame("Dashboard - " + currentUser.accountNumber);
        frame.setSize(450, 450);
        frame.setLayout(new GridLayout(8, 1, 10, 10));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton balanceBtn = new JButton("Check Balance");
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton transferBtn = new JButton("Transfer");
        JButton historyBtn = new JButton("Transaction History");
        JButton changePinBtn = new JButton("Change PIN");
        JButton logoutBtn = new JButton("Logout");

        frame.add(balanceBtn);
        frame.add(depositBtn);
        frame.add(withdrawBtn);
        frame.add(transferBtn);
        frame.add(historyBtn);
        frame.add(changePinBtn);
        frame.add(logoutBtn);

        balanceBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(frame,
                        "Current Balance: ₹" + currentUser.balance)
        );

        depositBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter deposit amount:");
            double amount = Double.parseDouble(input);
            currentUser.balance += amount;
            currentUser.transactions.add(LocalDateTime.now() + " | Deposited ₹" + amount);
            saveData();
            JOptionPane.showMessageDialog(frame, "Deposit Successful!");
        });

        withdrawBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter withdrawal amount:");
            double amount = Double.parseDouble(input);

            if (amount <= currentUser.balance) {
                currentUser.balance -= amount;
                currentUser.transactions.add(LocalDateTime.now() + " | Withdrawn ₹" + amount);
                saveData();
                JOptionPane.showMessageDialog(frame, "Withdrawal Successful!");
            } else {
                JOptionPane.showMessageDialog(frame, "Insufficient Balance!");
            }
        });

        transferBtn.addActionListener(e -> {
            String toAcc = JOptionPane.showInputDialog("Enter Receiver Account:");
            String amtStr = JOptionPane.showInputDialog("Enter Amount:");

            if (users.containsKey(toAcc)) {
                double amount = Double.parseDouble(amtStr);

                if (amount <= currentUser.balance) {
                    currentUser.balance -= amount;
                    users.get(toAcc).balance += amount;

                    currentUser.transactions.add(LocalDateTime.now() + " | Transferred ₹" + amount + " to " + toAcc);
                    users.get(toAcc).transactions.add(LocalDateTime.now() + " | Received ₹" + amount + " from " + currentUser.accountNumber);

                    saveData();
                    JOptionPane.showMessageDialog(frame, "Transfer Successful!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Insufficient Balance!");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Account Not Found!");
            }
        });

        historyBtn.addActionListener(e -> {
            JTextArea area = new JTextArea();
            for (String t : currentUser.transactions) {
                area.append(t + "\n");
            }
            area.setEditable(false);
            JOptionPane.showMessageDialog(frame, new JScrollPane(area), "Transaction History", JOptionPane.INFORMATION_MESSAGE);
        });

        changePinBtn.addActionListener(e -> {
            String newPin = JOptionPane.showInputDialog("Enter New PIN:");
            currentUser.pin = Integer.parseInt(newPin);
            saveData();
            JOptionPane.showMessageDialog(frame, "PIN Changed Successfully!");
        });

        logoutBtn.addActionListener(e -> {
            currentUser = null;
            frame.dispose();
            showLogin();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /* ===============================
       ADMIN PANEL
    =============================== */

    static void showAdminPanel() {
        JFrame frame = new JFrame("Admin Panel");
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextArea area = new JTextArea();
        area.setEditable(false);

        for (User u : users.values()) {
            area.append("Account: " + u.accountNumber + " | Balance: ₹" + u.balance + "\n");
        }

        JButton deleteBtn = new JButton("Delete Account");
        JButton backBtn = new JButton("Back");

        JPanel panel = new JPanel();
        panel.add(deleteBtn);
        panel.add(backBtn);

        frame.add(new JScrollPane(area), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        deleteBtn.addActionListener(e -> {
            String acc = JOptionPane.showInputDialog("Enter Account to Delete:");
            if (users.containsKey(acc)) {
                users.remove(acc);
                saveData();
                JOptionPane.showMessageDialog(frame, "Account Deleted!");
                frame.dispose();
                showAdminPanel();
            }
        });

        backBtn.addActionListener(e -> {
            frame.dispose();
            showLogin();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}