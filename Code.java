import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class PersonalFinanceTrackerUI {
    private static final String URL = "jdbc:mysql://localhost:3306/my_database";
    private static final String USER = "root"; // your MySQL username
    private static final String PASSWORD = "Sharol@2005"; // your MySQL password
    private static Connection conn;

    private JFrame frame;
    private JTextArea summaryArea;

    public PersonalFinanceTrackerUI() {
        frame = new JFrame("Personal Finance Tracker");
        frame.setSize(600, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(9, 1, 10, 10));
        JButton addIncomeBtn = new JButton("Add Income");
        JButton addExpenseBtn = new JButton("Add Expense");
        JButton setBudgetBtn = new JButton("Set Budget");
        JButton addInvestmentBtn = new JButton("Add Investment");
        JButton addDebtBtn = new JButton("Add Debt");
        JButton addTransactionBtn = new JButton("Add Transaction");
        JButton addPortfolioBtn = new JButton("Add Portfolio");
        JButton addInsuranceBtn = new JButton("Add Insurance");
        JButton viewSummaryBtn = new JButton("View Summary");

        // Add Action Listeners
        addIncomeBtn.addActionListener(this::addIncome);
        addExpenseBtn.addActionListener(this::addExpense);
        setBudgetBtn.addActionListener(this::setBudget);
        addInvestmentBtn.addActionListener(this::addInvestment);
        addDebtBtn.addActionListener(this::addDebt);
        addTransactionBtn.addActionListener(this::addTransaction);
        addPortfolioBtn.addActionListener(this::addPortfolio);
        addInsuranceBtn.addActionListener(this::addInsurance);
        viewSummaryBtn.addActionListener(this::viewSummary);

        // Add buttons to panel
        buttonPanel.add(addIncomeBtn);
        buttonPanel.add(addExpenseBtn);
        buttonPanel.add(setBudgetBtn);
        buttonPanel.add(addInvestmentBtn);
        buttonPanel.add(addDebtBtn);
        buttonPanel.add(addTransactionBtn);
        buttonPanel.add(addPortfolioBtn);
        buttonPanel.add(addInsuranceBtn);
        buttonPanel.add(viewSummaryBtn);

        // Summary Text Area
        summaryArea = new JTextArea(20, 50);
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Arial", Font.PLAIN, 14));
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(summaryArea);
        scrollPane.setPreferredSize(new Dimension(550, 350));

        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);

        // Connect to Database
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addIncome(ActionEvent e) {
        addEntry("income", "source", "Enter income source:", "amount", "Enter income amount:", "date");
    }

    private void addExpense(ActionEvent e) {
        addEntry("expenses", "category", "Enter expense category:", "amount", "Enter expense amount:", "date");
    }

    private void setBudget(ActionEvent e) {
        addEntry("budget", "id", "Enter budget ID:", "amount", "Enter budget amount:", "date");
    }

    private void addInvestment(ActionEvent e) {
        addEntry("investments", "type", "Enter investment type:", "amount", "Enter investment amount:", "date");
    }

    private void addDebt(ActionEvent e) {
        addEntry("debt", "type", "Enter debt type:", "amount", "Enter debt amount:", "date");
    }

    private void addTransaction(ActionEvent e) {
        addEntry("transactions", "description", "Enter transaction description:", "amount", "Enter transaction amount:", "date");
    }

    private void addPortfolio(ActionEvent e) {
        addEntry("portfolio", "asset_type", "Enter asset type (e.g., Stocks):", "value", "Enter asset value:", "date");
    }

    private void addInsurance(ActionEvent e) {
        addEntry("insurance", "policy_type", "Enter insurance type:", "premium", "Enter premium amount:", "date");
    }

    private void addEntry(String tableName, String col1, String msg1, String col2, String msg2, String dateCol) {
        String val1 = JOptionPane.showInputDialog(msg1);
        String val2 = JOptionPane.showInputDialog(msg2);
        String date = JOptionPane.showInputDialog("Enter date (YYYY-MM-DD):");

        try {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO " + tableName + " (" + col1 + ", " + col2 + ", " + dateCol + ") VALUES (?, ?, ?)"
            );
            stmt.setString(1, val1);
            stmt.setDouble(2, Double.parseDouble(val2));
            stmt.setDate(3, Date.valueOf(date));
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, tableName + " added successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void viewSummary(ActionEvent e) {
        try {
            StringBuilder summary = new StringBuilder("\n=== Financial Summary ===\n");
            summary.append(fetchTableData("income", "source", "amount", "date"));
            summary.append(fetchTableData("expenses", "category", "amount", "date"));
            summary.append(fetchTableData("budget", "id", "amount", "date"));
            summary.append(fetchTableData("investments", "type", "amount", "date"));
            summary.append(fetchTableData("debt", "type", "amount", "date"));
            summary.append(fetchTableData("transactions", "description", "amount", "date"));
            summary.append(fetchTableData("portfolio", "asset_type", "value", "date"));
            summary.append(fetchTableData("insurance", "policy_type", "premium", "date"));
            summaryArea.setText(summary.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private String fetchTableData(String tableName, String col1, String col2, String dateCol) throws SQLException {
        StringBuilder data = new StringBuilder("\n" + tableName.toUpperCase() + "\n");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT " + col1 + ", " + col2 + ", " + dateCol + " FROM " + tableName);

        while (rs.next()) {
            data.append(rs.getString(col1))
                .append(" - ")
                .append(rs.getDouble(col2))
                .append(" - Date: ")
                .append(rs.getDate(dateCol))
                .append("\n");
        }
        return data.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PersonalFinanceTrackerUI::new);
    }
}

