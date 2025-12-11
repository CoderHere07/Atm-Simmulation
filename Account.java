import java.util.*;
public class Account {
    private String accountNumber;
    private String pin;
    private double balance;
    private LinkedList<String[]> transactionHistory;
    private Stack<String[]> undoStack;
    private String accountType;
    private double dailyWithdrawalLimit;
    private double withdrawnToday, loanBalance;

    public Account1(String accountNumber, String pin, double balance, String accountType) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
        this.transactionHistory = new LinkedList<>();
        this.undoStack = new Stack<>();
        this.accountType = accountType;
        this.dailyWithdrawalLimit = (accountType.equals("Savings")) ? 2000.0 : 5000.0;
        this.loanBalance = 0.0;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public boolean validatePin(String enteredPin) {
        return this.pin.equals(enteredPin);
    }

    public void showBalance() {
        System.out.println("Current Balance: " + balance);
        recordTransaction("Balance Inquiry", balance);
    }
    public void changePin(String newPin) {
        this.pin = newPin;
        System.out.println("PIN changed successfully.");
    }

    public void withdraw(double amount) {
        if (amount > balance) {
            System.out.println("Insufficient funds.");
        } else if ((withdrawnToday + amount) > dailyWithdrawalLimit) {
            System.out.println("Daily withdrawal limit exceeded!");
        } else {
            balance -= amount;
            withdrawnToday += amount;
            System.out.println("Withdrawal successful. New Balance: " + balance);
            recordTransaction("Withdrawal", amount);
            undoStack.push(new String[]{"Deposit", String.valueOf(amount)});
        }
    }

    public void deposit(double amount) {
        balance += amount;
        System.out.println("Deposit successful. New Balance: " + balance);
        recordTransaction("Deposit", amount);
        undoStack.push(new String[]{"Withdrawal", String.valueOf(amount)});
    }

    public void undoLastTransaction(ATM atm) {
        if (!undoStack.isEmpty()) {
            String[] lastTransaction = undoStack.pop();
            if (lastTransaction[0].equals("Deposit")) {
                balance += Double.parseDouble(lastTransaction[1]);
                System.out.println("Undo successful: Deposited back " + lastTransaction[1]);
            } else if(lastTransaction[0].equals("Withdrawal")) {
                balance -= Double.parseDouble(lastTransaction[1]);
                System.out.println("Undo successful: Withdrawn " + lastTransaction[1]);
            }else if(lastTransaction[0].equals("Transfer")){
                String receiverAccountNumber = lastTransaction[1];
                double amount = Double.parseDouble(lastTransaction[2]);

                Account1 receiver = atm.findAccount(receiverAccountNumber);
                if (receiver != null && receiver.balance >= amount) {
                    receiver.balance -= amount;
                    this.balance += amount;
    
                    System.out.println("Undo successful: Reversed transfer of $" + amount + " from " + receiverAccountNumber);
                } else {
                    System.out.println("Undo failed: Insufficient funds in receiver's account or account not found.");
                }
            }
        } else {
            System.out.println("No transactions to undo.");
        }
    }

    public void showMiniStatement() {
        System.out.println("Mini Statement (Last 5 Transactions):");
        int count = 0;
        for (int i = Math.max(0, transactionHistory.size() - 5); i < transactionHistory.size(); i++) {
            System.out.println(transactionHistory.get(i)[0] + ": " + transactionHistory.get(i)[1]);
            count++;
        }
        if (count == 0) {
            System.out.println("No recent transactions.");
        }
    }

    public void currencyExchange(double amount, double choice) {
        final double USD_RATE = 300;
        final double EUR_RATE = 350;

        double rate;
        if (choice == 1) {
            rate = USD_RATE;
        } else {
            rate = EUR_RATE;
        }

        double convertedAmount = amount * rate;

        if (convertedAmount > balance) {
            System.out.println("Insufficient funds for currency exchange.");
        } else {
            balance -= convertedAmount;
            undoStack.push(new String[]{"Deposit", String.valueOf(convertedAmount)});
            System.out.println("Currency Exchange Successful! Withdrawn " + convertedAmount + " in foreign currency.");
            recordTransaction("Currency Exchange", convertedAmount);
        }
    }

    private void recordTransaction(String type, double amount) {
        if (transactionHistory.size() >= 100) {
            transactionHistory.removeFirst();
        }
        transactionHistory.add(new String[]{type, String.valueOf(amount)});
    }
    public String generateVirtualCard() {
        return "VIRTUAL-" + (int) (Math.random() * 1000000);
    }
    public boolean sameBankTransfer(Account1 receiver, double amount) {
        if (amount > balance) {
            System.out.println("Insufficient funds for transfer!");
            return false;
        }
    
        if (receiver == null) {
            System.out.println("Receiver account not found!");
            return false;
        }
    
        if (receiver.getAccountNumber().equals(this.accountNumber)) {
            System.out.println("Cannot transfer to the same account!");
            return false;
        }
        this.balance -= amount;
        receiver.balance += amount;
        undoStack.push(new String[]{"Transfer", receiver.getAccountNumber(), String.valueOf(amount)});
        this.recordTransaction("Transfer Out", amount);
        receiver.recordTransaction("Transfer In", amount);
        
        System.out.println("Transfer Successful! Transferred $" + amount + " to Account " + receiver.getAccountNumber());
        return true;
    }



    public void requestLoan(double amount) {
        if (loanBalance > 0) {
            System.out.println("Loan request denied! You already have an outstanding loan of: " + loanBalance);
            return;
        }

        double maxLoanAmount = balance * 3;
        if (amount <= 0) {
            System.out.println("Invalid loan amount!");
            return;
        }
        if (amount > maxLoanAmount) {
            System.out.println("Loan request denied! Maximum allowed loan is: " + maxLoanAmount);
            return;
        }

        balance += amount;
        loanBalance = amount;
        System.out.println("Loan approved! Amount credited: " + amount);
        System.out.println("New Balance: " + balance);
        System.out.println("Total Loan Balance: " + loanBalance);
        recordTransaction("Loan Received", amount);
    }
    public void returnLoan(double amount) {
        if (loanBalance == 0) {
            System.out.println("You have no outstanding loans to repay.");
            return;
        }
        if (amount <= 0) {
            System.out.println("Invalid repayment amount.");
            return;
        }
        if (amount > balance) {
            System.out.println("Insufficient funds to repay the loan.");
            return;
        }

        if (amount >= loanBalance) {
            System.out.println("Loan fully repaid! Thank you.");
            balance -= loanBalance;
            loanBalance = 0;
        } else {
            loanBalance -= amount;
            balance -= amount;
            System.out.println("Partial loan repayment successful. Remaining Loan Balance: " + loanBalance);
        }

        System.out.println("New Balance: " + balance);
        recordTransaction("Loan Repayment", amount);
    }
    
}
