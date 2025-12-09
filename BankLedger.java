import java.io.*;
import java.util.*;
import java.awt.Desktop;

public class BankLedger {

    static class Account {
        String id;
        double balance;
        ArrayList<String> transactionHistory = new ArrayList<>();

        Account(String id, double balance) {
            this.id = id;
            this.balance = balance;
        }

        void deposit(double amount) {
            if (amount <= 0) {
                transactionHistory.add("INVALID DEPOSIT: " + amount);
                return;
            }
            balance += amount;
            transactionHistory.add("DEPOSIT: +" + amount + " | New Balance: " + balance);
        }

        void withdraw(double amount) {
            if (amount <= 0) {
                transactionHistory.add("INVALID WITHDRAWAL: " + amount);
                return;
            }
            if (amount > balance) {
                transactionHistory.add("OVERDRAFT DENIED: " + amount + " | Balance: " + balance);
                return;
            }
            balance -= amount;
            transactionHistory.add("WITHDRAWAL: -" + amount + " | New Balance: " + balance);
        }
    }

    public static HashMap<String, Account> loadAccounts(Scanner file) {
        HashMap<String, Account> accounts = new HashMap<>();
        int accountCount = Integer.parseInt(file.nextLine());
        for (int i = 0; i < accountCount; i++) {
            String[] parts = file.nextLine().split(",");
            String id = parts[0].trim();
            double balance = Double.parseDouble(parts[1].trim());
            accounts.put(id, new Account(id, balance));
        }
        return accounts;
    }

    public static void processTransactions(Scanner file, HashMap<String, Account> accounts) {
        while (file.hasNextLine()) {
            String line = file.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length != 3) continue;

            String id = parts[0].trim();
            String type = parts[1].trim();
            double amount = Double.parseDouble(parts[2].trim());

            Account acc = accounts.get(id);
            if (acc == null) continue;

            if (type.equalsIgnoreCase("D")) {
                acc.deposit(amount);
            } else if (type.equalsIgnoreCase("W")) {
                acc.withdraw(amount);
            } else {
                acc.transactionHistory.add("INVALID TRANSACTION TYPE: " + type);
            }
        }
    }

    public static void writeReport(HashMap<String, Account> accounts) throws IOException {
        PrintWriter writer = new PrintWriter("TransactionReport.txt");
        for (Account acc : accounts.values()) {
            writer.println("====================================");
            writer.println("Account ID: " + acc.id);
            writer.println("Final Balance: " + acc.balance);
            writer.println("Transaction History:");
            for (String t : acc.transactionHistory) {
                writer.println("   - " + t);
            }
            writer.println();
        }
        writer.close();
    }

    public static void main(String[] args) {
        File file = new File("bankdata.txt");
        if (!file.exists()) {
            System.out.println("Error: bankdata.txt not found in project root!");
            return;
        }

        try (Scanner inputFile = new Scanner(file)) {
            HashMap<String, Account> accounts = loadAccounts(inputFile);
            processTransactions(inputFile, accounts);
            writeReport(accounts);
            System.out.println("Report generated: TransactionReport.txt");

            // Automatically open the report on Windows
            try {
                File reportFile = new File("TransactionReport.txt");
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(reportFile);
                } else {
                    System.out.println("Automatic opening not supported on this system.");
                }
            } catch (IOException e) {
                System.out.println("Could not open report automatically: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
