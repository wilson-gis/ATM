import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// Account class to represent bank accounts
class Account {
    private String accountNumber;
    private String pin;
    private double balance;
    private String accountHolderName;
    
    public Account(String accountNumber, String pin, double balance, String accountHolderName) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
        this.accountHolderName = accountHolderName;
    }
    
    // Getters and setters
    public String getAccountNumber() { return accountNumber; }
    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getAccountHolderName() { return accountHolderName; }
}

// Transaction class to record all transactions
class Transaction {
    private String transactionId;
    private String accountNumber;
    private String type;
    private double amount;
    private String dateTime;
    private double balanceAfter;
    
    public Transaction(String accountNumber, String type, double amount, double balanceAfter) {
        this.transactionId = generateTransactionId();
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
    
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1000, 9999);
    }
    
    @Override
    public String toString() {
        return transactionId + "," + accountNumber + "," + type + "," + amount + "," + dateTime + "," + balanceAfter;
    }
    
    public static Transaction fromString(String str) {
        String[] parts = str.split(",");
        if (parts.length != 6) return null;
        
        try {
            double amount = Double.parseDouble(parts[3]);
            double balanceAfter = Double.parseDouble(parts[5]);
            Transaction txn = new Transaction(parts[1], parts[2], amount, balanceAfter);
            // Since we can't set the transactionId and datetime, we'll need to handle this differently
            return txn;
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public String getFormattedString() {
        return String.format("%s | %s | %s | MK%.2f | MK%.2f", 
                            dateTime, transactionId, type, amount, balanceAfter);
    }
}

// Main ATM class
public class ATMSimulator {
    private static final String ACCOUNTS_FILE = "accounts.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private static final String BANK_NAME = "National Bank of RAS VAIP"; // Change based on group
    private Account currentAccount;
    private Scanner scanner;
    
    public ATMSimulator() {
        scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        ATMSimulator atm = new ATMSimulator();
        atm.start();
    }
    
    public void start() {
        System.out.println("============================================");
        System.out.println("      WELCOME TO " + BANK_NAME + " ATM");
        System.out.println("============================================");
        
        while (true) {
            if (currentAccount == null) {
                showMainMenu();
            } else {
                showAccountMenu();
            }
        }
    }
    
    private void showMainMenu() {
        System.out.println("\nPlease select an option:");
        System.out.println("1. Create Account");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Enter choice: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                createAccount();
                break;
            case 2:
                login();
                break;
            case 3:
                System.out.println("Thank you for using " + BANK_NAME + " ATM. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    private void showAccountMenu() {
        System.out.println("\n============================================");
        System.out.println("Welcome, " + currentAccount.getAccountHolderName());
        System.out.println("Account: " + currentAccount.getAccountNumber());
        System.out.println("============================================");
        System.out.println("Please select an option:");
        System.out.println("1. Check Balance");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Change PIN");
        System.out.println("5. Transaction History");
        System.out.println("6. Logout");
        System.out.print("Enter choice: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                checkBalance();
                break;
            case 2:
                deposit();
                break;
            case 3:
                withdraw();
                break;
            case 4:
                changePin();
                break;
            case 5:
                showTransactionHistory();
                break;
            case 6:
                currentAccount = null;
                System.out.println("You have been logged out successfully.");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
    
    private void createAccount() {
        System.out.println("\n--- Create New Account ---");
        
        System.out.print("Enter your full name: ");
        String name = scanner.nextLine();
        
        System.out.print("Set your 4-digit PIN: ");
        String pin = scanner.nextLine();
        
        if (pin.length() != 4 || !pin.matches("\\d+")) {
            System.out.println("PIN must be exactly 4 digits. Account creation failed.");
            return;
        }
        
        // Generate account number (10 digits)
        String accountNumber = generateAccountNumber();
        
        // Create account with initial balance of 0
        Account newAccount = new Account(accountNumber, pin, 0.0, name);
        
        // Save account to file
        try (FileWriter fw = new FileWriter(ACCOUNTS_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(accountNumber + "," + pin + ",0.0," + name);
            System.out.println("Account created successfully!");
            System.out.println("Your account number is: " + accountNumber);
        } catch (IOException e) {
            System.out.println("Error creating account. Please try again.");
        }
    }
    
    private void login() {
        System.out.println("\n--- Login ---");
        
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();
        
        // Read accounts from file and validate
        try (BufferedReader br = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[0].equals(accountNumber) && parts[1].equals(pin)) {
                    double balance = Double.parseDouble(parts[2]);
                    currentAccount = new Account(accountNumber, pin, balance, parts[3]);
                    System.out.println("Login successful! Welcome " + parts[3]);
                    return;
                }
            }
            System.out.println("Invalid account number or PIN.");
        } catch (IOException e) {
            System.out.println("No accounts found. Please create an account first.");
        }
    }
    
    private void checkBalance() {
        System.out.println("\n--- Account Balance ---");
        System.out.printf("Your current balance is: MK%.2f%n", currentAccount.getBalance());
    }
    
    private void deposit() {
        System.out.println("\n--- Deposit Money ---");
        System.out.print("Enter amount to deposit: MK");
        
        double amount = getDoubleInput();
        if (amount <= 0) {
            System.out.println("Invalid amount. Deposit failed.");
            return;
        }
        
        double newBalance = currentAccount.getBalance() + amount;
        currentAccount.setBalance(newBalance);
        
        // Update account balance in file
        updateAccountBalance();
        
        // Record transaction
        recordTransaction("DEPOSIT", amount, newBalance);
        
        System.out.printf("Deposit successful! New balance: MK%.2f%n", newBalance);
    }
    
    private void withdraw() {
        System.out.println("\n--- Withdraw Money ---");
        System.out.print("Enter amount to withdraw: MK");
        
        double amount = getDoubleInput();
        if (amount <= 0) {
            System.out.println("Invalid amount. Withdrawal failed.");
            return;
        }
        
        if (amount > currentAccount.getBalance()) {
            System.out.println("Insufficient funds. Withdrawal failed.");
            return;
        }
        
        double newBalance = currentAccount.getBalance() - amount;
        currentAccount.setBalance(newBalance);
        
        // Update account balance in file
        updateAccountBalance();
        
        // Record transaction
        recordTransaction("WITHDRAW", amount, newBalance);
        
        System.out.printf("Withdrawal successful! New balance: MK%.2f%n", newBalance);
    }
    
    private void changePin() {
        System.out.println("\n--- Change PIN ---");
        
        System.out.print("Enter current PIN: ");
        String currentPin = scanner.nextLine();
        
        if (!currentPin.equals(currentAccount.getPin())) {
            System.out.println("Incorrect current PIN.");
            return;
        }
        
        System.out.print("Enter new 4-digit PIN: ");
        String newPin = scanner.nextLine();
        
        if (newPin.length() != 4 || !newPin.matches("\\d+")) {
            System.out.println("PIN must be exactly 4 digits. PIN change failed.");
            return;
        }
        
        // Update PIN in memory
        currentAccount.setPin(newPin);
        
        // Update PIN in file
        updateAccountPin();
        
        System.out.println("PIN changed successfully!");
    }
    
    private void showTransactionHistory() {
        System.out.println("\n--- Transaction History ---");
        
        try (BufferedReader br = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            String line;
            boolean found = false;
            
            while ((line = br.readLine()) != null) {
                Transaction txn = Transaction.fromString(line);
                if (txn != null && line.contains(currentAccount.getAccountNumber())) {
                    System.out.println(txn.getFormattedString());
                    found = true;
                }
            }
            
            if (!found) {
                System.out.println("No transactions found.");
            }
        } catch (IOException e) {
            System.out.println("No transaction history available.");
        }
    }
    
    private String generateAccountNumber() {
        // Simple account number generation (10 digits)
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    private void updateAccountBalance() {
        // Read all accounts, update the current account's balance, and write back
        List<String> accounts = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[0].equals(currentAccount.getAccountNumber())) {
                    line = parts[0] + "," + parts[1] + "," + currentAccount.getBalance() + "," + parts[3];
                }
                accounts.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error updating account balance.");
            return;
        }
        
        // Write updated accounts back to file
        try (PrintWriter out = new PrintWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (String account : accounts) {
                out.println(account);
            }
        } catch (IOException e) {
            System.out.println("Error updating account balance.");
        }
    }
    
    private void updateAccountPin() {
        // Read all accounts, update the current account's PIN, and write back
        List<String> accounts = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[0].equals(currentAccount.getAccountNumber())) {
                    line = parts[0] + "," + currentAccount.getPin() + "," + parts[2] + "," + parts[3];
                }
                accounts.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error updating PIN.");
            return;
        }
        
        // Write updated accounts back to file
        try (PrintWriter out = new PrintWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (String account : accounts) {
                out.println(account);
            }
        } catch (IOException e) {
            System.out.println("Error updating PIN.");
        }
    }
    
    private void recordTransaction(String type, double amount, double balanceAfter) {
        try (FileWriter fw = new FileWriter(TRANSACTIONS_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            Transaction txn = new Transaction(currentAccount.getAccountNumber(), type, amount, balanceAfter);
            out.println(txn.toString());
        } catch (IOException e) {
            System.out.println("Error recording transaction.");
        }
    }
    
    private int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
    
    private double getDoubleInput() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid amount: ");
            }
        }
    }
}