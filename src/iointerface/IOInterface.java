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
        System.out.println(message);
        String[] userInput = scanner.nextLine().trim().split("\\s+");
        if (userInput.length != numOfArgs) {
            System.out.println("Invalid input. Please enter " + numOfArgs + " values(s) in the form like: userName userPassword.");
            System.out.println(message);
            return getUserInput(message, numOfArgs);
        }
        else return userInput;
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

    public void showList (String userRole, String listType, List<?> objectList, int pageNumber, int totalPages) {
        System.out.println("=== " + listType + " List (Page " + pageNumber + ") ===");
        int no = 1;
        for (Object obj : objectList) {
            System.out.println(no++ + ". " + obj.toString());
        }
    }

    public void printErrorMessage (String errorSource, String errorMessage) {
        System.out.println(errorMessage + "from" + errorSource);
    }

    public void printMessage (String message) {
        System.out.println(message);
    }

    public void printObject (Objects targetObject) {
        System.out.println(targetObject);
    }
}