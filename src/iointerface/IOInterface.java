package iointerface;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class IOInterface {
    private static IOInterface instance;

    private IOInterface () {}

    Scanner scanner = new Scanner(System.in);

    public static IOInterface getInstance() {
        if (instance == null) {
            instance = new IOInterface();
        }
        return instance;
    }

    public String[] getUserInput (String message, int numOfArgs) {
        System.out.print(message);
        String[] userInput = scanner.nextLine().trim().split("\\s+");
        String[] result = new String[numOfArgs];

        for (int i = 0; i < numOfArgs; i++) {
            if (i < userInput.length) {
                result[i] = userInput[i];
            } else {
                result [i] = "";
            }
        }
        return result;
    }

    public void mainMenu () {
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Quit");
    }

    public void adminMenu () {
        System.out.println("1. Show products");
        System.out.println("2. Add customers");
        System.out.println("3. Show customers");
        System.out.println("4. Show orders");
        System.out.println("5. Generate test data");
        System.out.println("6. Generate all statistical figures");
        System.out.println("7. Delete all data");
        System.out.println("8. Logout");
    }

    public void customerMenu () {
        System.out.println("1. Show profile");
        System.out.println("2. Update profile");
        System.out.println("3. Show products");
        System.out.println("4. Show history orders");
        System.out.println("5. Generate all consumption figures");
        System.out.println("6. Logout");
    }

    public void registerMenu () {
        System.out.println("1. As an admin");
        System.out.println("2. As a customer");
    }

    public void showList (String userRole, String listType, List<?> objectList, int pageNumber, int totalPages) {
        System.out.println(("====== " + listType + " List (Page " + pageNumber + "/" + totalPages + ") ======"));
        for (int i = 0; i < objectList.size(); i++) {
            System.out.println((i + 1) + ". " + objectList.get(i).toString());
        }
        printMessage("");
    }

    public void printErrorMessage (String errorSource, String errorMessage) {
        System.out.println("Error from " + errorSource + ": " + errorMessage);
    }

    public void printMessage (String message) {
        System.out.print(message);
    }

    public void printObject (Objects targetObject) {
        System.out.println(targetObject);
    }
}