package banking;

import javax.sql.DataSource;
import java.util.Random;
import java.util.Scanner;
import java.sql.*;

public class Operations {
    static int custAccNum = 123456789;
    DataSource dataSource;

    Operations(DataSource dataSourse) {
        this.dataSource = dataSourse;

    }

    void accauntCreating() {
        Random random = new Random();
        int pin = random.nextInt(9000) + 1000;
        int balance = 0;
        String cardNumber = "400000" + custAccNum + luhn("400000" + custAccNum);

        String query = "INSERT INTO card (number, pin, balance) " +
                "VALUES ('" + cardNumber + "', '" + pin + "', '" + balance + "') ";

        try {
            Connection con = dataSource.getConnection();
            Statement statement = con.createStatement();
            statement.executeUpdate(query);
            statement.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Your card has been created" + "\n" +
                "Your card number:" + "\n" +
                cardNumber + "\n" +
                "Your card PIN:" + "\n" +
                pin);
        custAccNum++;
    }

    void accauntLogging() {
        System.out.println("\nEnter your card number:");
        String cardNumber = new Scanner(System.in).nextLine();
        System.out.println("Enter your PIN:");
        int pin = new Scanner(System.in).nextInt();
        String query = "SELECT balance " +
                "FROM card " +
                "WHERE " +
                "number = '" + cardNumber + "' AND pin = '" + pin + "' ";

        boolean checkPin = false;

        try {
            Connection con = dataSource.getConnection();
            Statement statement = con.createStatement();
            ResultSet resultRow = statement.executeQuery(query);
            int balance = resultRow.getInt("balance");
            checkPin = true;
            statement.close();

        } catch (Exception e) {
            System.out.println("\nWrong card number or PIN!");
        }

        if (checkPin) {
            boolean flag = true;

            System.out.println("\nYou have successfully logged in!\n");
            while (flag) {
                System.out.println("1. Balance" + "\n" +
                        "2. Add income" + "\n" +
                        "3. Do transfer" + "\n" +
                        "4. Close account" + "\n" +
                        "5. Log out" + "\n" +
                        "0. Exit");
                String command = new Scanner(System.in).nextLine();
                switch (command) {
                    case "1":
                        System.out.println("\nBalance: " + getBalance(cardNumber) + "\n");
                        break;
                    case "2":
                        System.out.println("\nEnter income:");
                        int income = new Scanner(System.in).nextInt();
                        incomeBalance(income, cardNumber);
                        break;
                    case "3":
                        transfer(cardNumber);
                        break;
                    case "4":
                        closeAccount(cardNumber);
                        flag = false;
                        break;
                    case "5":
                        System.out.println("\nYou have successfully logged out!");
                        flag = false;
                        break;
                    case "0":
                        System.exit(0);
                        flag = false;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public int luhn(String cardNumber) {
        int controlSum;
        int sum = 0;
        char[] arr = cardNumber.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            int n = Character.getNumericValue(arr[i]);
            if (i % 2 == 0) {
                n = n * 2;
            }
            if (n > 9) {
                n = n - 9;
            }
            sum = sum + n;
        }
        if (sum % 10 == 0) {
            controlSum = 0;
        } else {
            controlSum = 10 - (sum % 10);
        }

        return controlSum;
    }

    void incomeBalance(int income, String cardNumber) {
        String query = "UPDATE card " +
                "SET balance = balance + " + income +
                " WHERE " +
                " number = '" + cardNumber + "'";
        try {
            Connection con = dataSource.getConnection();
            Statement statement = con.createStatement();
            statement.executeUpdate(query);
            statement.close();
            System.out.println("Income was added!\n");

        } catch (Exception e) {
            System.out.println("\nCannot update your balance");
            System.out.println(e.getMessage());
        }

    }

    int getBalance(String cardNumber) {
        String query = "SELECT balance " +
                "FROM card " +
                "WHERE " +
                "number = '" + cardNumber + "'";
        int balance = -1;
        try {
            Connection con = dataSource.getConnection();
            Statement statement = con.createStatement();
            ResultSet resultRow = statement.executeQuery(query);
            balance = resultRow.getInt("balance");
            statement.close();
        } catch (Exception e) {
            System.out.println("\nSuch a card does not exist.");
        }
        return balance;
    }

    void closeAccount(String cardNumber) {
        String query = "DELETE FROM card " +
                " WHERE number = '" + cardNumber + "'";
        try {
            Connection con = dataSource.getConnection();
            Statement statement = con.createStatement();
            statement.executeUpdate(query);
            statement.close();
            System.out.println("The account has been closed!\n");

        } catch (Exception e) {
            System.out.println("\nCannot close account");
            System.out.println(e.getMessage());
        }

    }
    void transfer(String cardNumberFrom) {
        System.out.println("\nTransfer\n" +
                "Enter card number:");
        String cardNumberTo = new Scanner(System.in).nextLine();

        if(luhn(cardNumberTo.substring(0, 15)) != Integer.parseInt(cardNumberTo.substring(15))) {
            System.out.println("Probably you made mistake in the card number. Please try again!");
            return;
        }

        if (cardNumberFrom.equals(cardNumberTo)) {
            System.out.println("You can't transfer money to the same account!");
            return;
        }

        if (getBalance(cardNumberTo) == -1) {
            return;
        }

        System.out.println("Enter how much money you want to transfer:");
        int amountToTransfer = new Scanner(System.in).nextInt();

        if (getBalance(cardNumberFrom) < amountToTransfer) {
            System.out.println("Not enough money!");
            return;
        }

        String actionFrom = "UPDATE card " +
                "SET balance = balance - " + amountToTransfer +
                " WHERE " +
                " number = '" + cardNumberFrom + "'";
        String actionTo = "UPDATE card " +
                "SET balance = balance + " + amountToTransfer +
                " WHERE " +
                " number = '" + cardNumberTo + "'";

        try (Connection con = dataSource.getConnection();) {
            con.setAutoCommit(false);
            Statement statement = con.createStatement();
            statement.executeUpdate(actionFrom);
            statement.executeUpdate(actionTo);
            con.commit();
            statement.close();
            System.out.println("Success!\n");

        } catch (Exception e) {
            System.out.println("\nCannot do transfer");
            System.out.println(e.getMessage());
        }

    }
}