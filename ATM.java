import java.util.*;

public class ATM {
    private Account[] accounts;
    private Scanner scanner;
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    public ATM() {
        accounts = new Account1[5];
        scanner = new Scanner(System.in);
        seedAccounts();
    }

    private void seedAccounts() {
        accounts[0] = new Account1("12345", "1234", 5000.0, "Savings");
        accounts[1] = new Account1("67890", "5678", 3000.0, "Checking");
        accounts[2] = new Account1("11223", "0000", 2000.0, "Savings");
        accounts[3] = new Account1("33445", "4321", 1500.0, "Checking");
        accounts[4] = new Account1("55667", "9999", 10000.0, "Savings");
    }

    public void run() {
        System.out.println("Welcome to the ATM Machine!");
        while (true) {
            System.out.println("\n1. Login\n2. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            if (choice == 1) {
                login();
            } else if (choice == 2) {
                System.out.println("Thank you for using the ATM! Goodbye!");
                break;
            } else {
                System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void login() {
        System.out.print("Enter Account Number: ");
        String accountNumber = scanner.next();

        Account1 account = findAccount(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        int attempts = 0;

        while (attempts < MAX_LOGIN_ATTEMPTS) {
            System.out.print("Enter PIN: ");
            String pin = scanner.next();

            if (account.validatePin(pin)) {
                System.out.println("Login successful.");
                showMenu(account);
                return;
            } else {
                attempts++;
                System.out.println("Incorrect PIN. Attempts left: " + (MAX_LOGIN_ATTEMPTS - attempts));
            }
        }

        System.out.println("Account locked due to multiple failed attempts.");
    }

    public Account1 findAccount(String accountNumber) {
        for (Account1 acc : accounts) {
            if (acc.getAccountNumber().equals(accountNumber)) {
                return acc;
            }
        }
        return null;
    }

    private void showMenu(Account1 account) {
        while (true) {
            System.out.println("\n1. Balance Inquiry\n2. Cash Withdrawal\n3. Cash Deposit\n4. Mini Statement\n5. Undo Last Transaction\n6. Currency Exchange\n7. Generate Virtual card\n8. Change Pin\n9.Transfer Fund \n10. loan request \n11.loan return \n12.logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            if (choice == 1) {account.showBalance();}
            else if (choice == 2) {
                System.out.print("Enter amount to withdraw: ");
                account.withdraw(scanner.nextDouble());
            } else if (choice == 3) {
                System.out.print("Enter amount to deposit: ");
                account.deposit(scanner.nextDouble());
            } else if (choice == 4) account.showMiniStatement();
            else if (choice == 5) account.undoLastTransaction(this);
            else if (choice == 6) {
                int choices;
                do {
                    System.out.println("Select currency to exchange:");
                    System.out.println("1 - Dollar (USD)");
                    System.out.println("2 - Euro (EUR)");
                    System.out.print("Enter your choice: ");
                    while (!scanner.hasNextInt()) {
                        System.out.print("Invalid input. Enter 1 for Dollar or 2 for Euro: ");
                        scanner.next();
                    }
                    choices = scanner.nextInt();
                } while (choices != 1 && choices != 2);
        
                System.out.print("Enter amount to convert: ");
                while (!scanner.hasNextDouble()) {
                    System.out.print("Invalid input. Enter a valid amount: ");
                    scanner.next();
                }
                double amount = scanner.nextDouble();
                account.currencyExchange(amount, choices);
            }else if(choice == 7){
                System.out.print(account.generateVirtualCard());
            }else if(choice == 8){
                System.out.print("Enter new PIN: ");
                String newPin = scanner.next();
                account.changePin(newPin);
            }else if(choice == 9){
                performTransfer(account);
            }else if (choice == 10){
                System.out.print("Enter loan amount(Amount must be lesser than 3 times your current balance): ");
                double loanAmount = scanner.nextDouble();
                account.requestLoan(loanAmount);
            } else if (choice == 11) {
                System.out.print("Enter repayment amount: ");
                double repaymentAmount = scanner.nextDouble();
                account.returnLoan(repaymentAmount);
            }
             else if(choice == 12) break;
        }
    }
    private void performTransfer(Account1 sender) {
        System.out.print("Enter receiver's account number: ");
        String receiverAccNum = scanner.next();
    
        Account1 receiver = findAccount(receiverAccNum);
    
        if (receiver == null) {
            System.out.println("Receiver account not found.");
            return;
        }
    
        if (receiver.getAccountNumber().equals(sender.getAccountNumber())) {
            System.out.println("Cannot transfer to the same account!");
            return;
        }
    
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
    
        if (sender.sameBankTransfer(receiver, amount)) {
            System.out.println("Transfer of" + amount + " to Account " + receiver.getAccountNumber() + " successful!");
        } else {
            System.out.println("Transfer failed. Please check your balance and try again.");
        }
    }
}
