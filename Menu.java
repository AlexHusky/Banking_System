package banking;

import javax.sql.DataSource;
import java.util.Scanner;

public class Menu {

    static String command;

    Menu(DataSource dataSource) {
        Operations operations = new Operations(dataSource);

        command = "";
        while (!command.equals("0")) {
            System.out.println("\n1. Create an account" + "\n" +
                    "2. Log into account" + "\n" +
                    "0. Exit");
            command = new Scanner(System.in).nextLine();

            switch (command) {
                case "1":
                    operations.accauntCreating();
                    break;
                case "2":
                    operations.accauntLogging();
                    break;
                case "0":
                    System.out.println("\nBye!\n");
                    break;
                default:
                    break;
            }
        }
    }
}