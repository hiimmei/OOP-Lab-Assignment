import iointerface.IOInterface;
import model.User;
import operation.*;
import result.CustomerListResult;
import result.ProductListResult;

public class Main {
    public static void main(String[] args) {
        ProductOperation.getInstance().extractProductsFromFiles();
        UserOperation.getInstance().readAllUsers();

        IOInterface io = IOInterface.getInstance();

        while (true) {
            io.printMessage("====== E-Commerce System ======\n");

            io.mainMenu();

            io.printMessage("===============================\n");

            String[] input = io.getUserInput("Enter your choice: ", 1);

            switch (Integer.valueOf(input[0])) {
                case 1 -> {
                    String[] userInput = io.getUserInput("Enter username and password: ", 2);
                    User user;
                    if (UserOperation.getInstance().checkUsernameExist(userInput[0]) && UserOperation.getInstance().validateUsername(userInput[0])
                            && UserOperation.getInstance().validatePassword(userInput[1])) {
                        user = UserOperation.getInstance().login(userInput[0], userInput[1]);

                        io.printMessage("\n");
                        io.printMessage("Login successful. Welcome, " + userInput[0]);

                        io.printMessage("\n");

                        while (true) {
                            io.printMessage("====== Admin Menu ======\n");

                            io.adminMenu();

                            io.printMessage("===============================\n");

                            String[] inputChoice = io.getUserInput("Enter your choice: ", 1);

                            switch (Integer.valueOf(inputChoice[0])) {
                                case 1 -> {
                                    int pageNum = 1;
                                    while (true) {
                                        ProductListResult result = ProductOperation.getInstance().getProductList(pageNum);
                                        io.showList("admin", "Product", result.getProducts(), result.getCurrentPage(), result.getTotalPages());

                                        String[] choice = io.getUserInput(
                                                "Enter 'n' for next page, 'p' for previous page, or 'b' to go back\nEnter your choice: ", 1
                                        );

                                        if (choice[0].equalsIgnoreCase("n") && pageNum < result.getTotalPages())
                                            pageNum++;
                                        else if (choice[0].equalsIgnoreCase("p") && pageNum > 1) pageNum--;
                                        else if (choice[0].equalsIgnoreCase("b")) break;
                                    }
                                }
                                case 2 -> {

                                }
                                case 3 -> {
                                    int pageNum = 1;
                                    while (true) {
                                        CustomerListResult result = CustomerOperation.getInstance().getCustomerList(pageNum);
                                        io.showList("admin", "Customer", result.getCustomers(), result.getCurrentPage(), result.getTotalPages());

                                        String[] choice = io.getUserInput(
                                                "Enter 'n' for next page, 'p' for previous page, or 'b' to go back\nEnter your choice: ", 1
                                        );

                                        if (choice[0].equalsIgnoreCase("n") && pageNum < result.getTotalPages())
                                            pageNum++;
                                        else if (choice[0].equalsIgnoreCase("p") && pageNum > 1) pageNum--;
                                        else if (choice[0].equalsIgnoreCase("b")) break;
                                    }
                                }
                                case 4 -> {
                                }
                                case 5 -> {

                                }
                                case 6 -> {

                                }
                                case 7 -> {

                                }
                                case 8 -> {
                                    io.printMessage("Goodbye! See you next time");
                                }
                            }
                        }
                    } else {
                        io.printErrorMessage("WrongInformation", "Your username and password does not exist");
                    }
                }
                case 2 -> {
                    io.registerMenu();
                    io.printMessage("===============================\n");
                    String[] inputC = io.getUserInput("Enter your choice: ", 1);
                    switch (Integer.valueOf(inputC[0])) {
                        case 1 -> {
                            AdminOperation.getInstance().registerAdmin();
                            UserOperation.getInstance().readAllUsers();
                            UserOperation.getInstance().readAllUsers().forEach(u -> System.out.println(u.getUserName()));
                        }
                        case 2 -> {
                            String[] inputCustomer = io.getUserInput("Enter your name, password, email and mobile phone: ", 4);
                            CustomerOperation.getInstance().registerCustomer(inputCustomer[0], inputCustomer[1], inputCustomer[2], inputCustomer[3]);
                            UserOperation.getInstance().readAllUsers();
                        }
                    }
                }
                case 3 -> {
                }
            }
        }
    }
}
