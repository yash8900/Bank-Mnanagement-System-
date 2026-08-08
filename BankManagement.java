package banking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

public class BankManagement {

    public static boolean createAccount(String name, int passCode) {
        if (name == null || name.trim().isEmpty() || passCode <= 0) {
            System.out.println("Invalid input! All fields are required.");
            return false;
        }

        String sql = "INSERT INTO customer(cname, balance, pass_code) VALUES (?, 1000, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name.trim());
            ps.setInt(2, passCode);

            int rows = ps.executeUpdate();
            if (rows == 1) {
                System.out.println("Account created successfully! You can now login.");
                return true;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Username already exists! Try another one.");
        } catch (SQLException e) {
            System.err.println("Database error during account creation: " + e.getMessage());
        }
        return false;
    }

    public static boolean loginAccount(String name, int passCode) {
        if (name == null || name.trim().isEmpty() || passCode <= 0) {
            System.out.println("Invalid login credentials provided!");
            return false;
        }

        String sql = "SELECT * FROM customer WHERE cname = ? AND pass_code = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name.trim());
            ps.setInt(2, passCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int senderAc = rs.getInt("ac_no");
                    String customerName = rs.getString("cname");
                    
                    BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
                    while (true) {
                        System.out.println("\nHello, " + customerName + "! What would you like to do?");
                        System.out.println("1) Transfer Money");
                        System.out.println("2) View Balance");
                        System.out.println("3) Logout");
                        System.out.print("Enter Choice: ");

                        int ch;
                        try {
                            ch = Integer.parseInt(sc.readLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid numeric input!");
                            continue;
                        }

                        if (ch == 1) {
                            System.out.print("Enter Receiver A/c No: ");
                            int receiverAc = Integer.parseInt(sc.readLine());
                            System.out.print("Enter Amount: ");
                            int amt = Integer.parseInt(sc.readLine());

                            if (transferMoney(senderAc, receiverAc, amt)) {
                                System.out.println("Transaction successful!");
                            } else {
                                System.out.println("Transaction failed!");
                            }
                        } else if (ch == 2) {
                            getBalance(senderAc);
                        } else if (ch == 3) {
                            System.out.println("Logged out successfully.");
                            break;
                        } else {
                            System.out.println("Invalid choice! Try again.");
                        }
                    }
                    return true;
                } else {
                    System.out.println("Invalid username or password!");
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println("Error during operation: " + e.getMessage());
        }
        return false;
    }

    public static void getBalance(int acNo) {
        String sql = "SELECT ac_no, cname, balance FROM customer WHERE ac_no = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, acNo);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n-------------------------------------------------");
                System.out.printf("%12s %15s %10s\n", "Account No", "Customer Name", "Balance");
                if (rs.next()) {
                    System.out.printf("%12d %15s %10d.00\n",
                            rs.getInt("ac_no"),
                            rs.getString("cname"),
                            rs.getInt("balance"));
                }
                System.out.println("-------------------------------------------------");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving balance: " + e.getMessage());
        }
    }

    public static boolean transferMoney(int senderAc, int receiverAc, int amount) {
        if (receiverAc <= 0 || amount <= 0) {
            System.out.println("Invalid transfer details!");
            return false;
        }

        if (senderAc == receiverAc) {
            System.out.println("Cannot transfer money to your own account!");
            return false;
        }

        String checkBalanceSql = "SELECT balance FROM customer WHERE ac_no = ?";
        String debitSql = "UPDATE customer SET balance = balance - ? WHERE ac_no = ?";
        String creditSql = "UPDATE customer SET balance = balance + ? WHERE ac_no = ?";

        Connection con = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Begin transaction

            // 1. Verify Sender Balance
            try (PreparedStatement psCheck = con.prepareStatement(checkBalanceSql)) {
                psCheck.setInt(1, senderAc);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Sender account not found!");
                        con.rollback();
                        return false;
                    }
                    if (rs.getInt("balance") < amount) {
                        System.out.println("Insufficient Balance!");
                        con.rollback();
                        return false;
                    }
                }
            }

            // 2. Debit Sender
            try (PreparedStatement psDebit = con.prepareStatement(debitSql)) {
                psDebit.setInt(1, amount);
                psDebit.setInt(2, senderAc);
                psDebit.executeUpdate();
            }

            // 3. Credit Receiver
            int receiverUpdatedRows;
            try (PreparedStatement psCredit = con.prepareStatement(creditSql)) {
                psCredit.setInt(1, amount);
                psCredit.setInt(2, receiverAc);
                receiverUpdatedRows = psCredit.executeUpdate();
            }

            if (receiverUpdatedRows == 0) {
                System.out.println("Receiver account does not exist!");
                con.rollback();
                return false;
            }

            // Commit transaction on success
            con.commit();
            return true;

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Transaction rolled back due to error: " + e.getMessage());
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
