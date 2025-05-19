import iointerface.IOInterface;
import model.User;
import operation.ProductOperation;
import operation.UserOperation;
import result.ProductListResult;

public class Main {
    public static void main(String[] args) {
        ProductOperation.getInstance().extractProductsFromFiles();

        IOInterface.getInstance().printMessage("====== E-Commerce System ======");
        IOInterface.getInstance().printMessage("\n");

        IOInterface.getInstance().mainMenu();

        IOInterface.getInstance().printMessage("===============================");
        IOInterface.getInstance().printMessage("\n");

        String[] input = IOInterface.getInstance().getUserInput("Enter your choice: ", 1);

        switch (Integer.valueOf(input[0])) {

            case 1 -> {
                String[] userInput = IOInterface.getInstance().getUserInput("Enter username and password: ", 2);

                User user;
                if (UserOperation.getInstance().checkUsernameExist(userInput[0]) && UserOperation.getInstance().validateUsername(userInput[0])
                        && UserOperation.getInstance().validatePassword(userInput[1])) {
                    user = UserOperation.getInstance().login(userInput[0], UserOperation.getInstance().encryptPassword(userInput[1]));

                    IOInterface.getInstance().printMessage("\n");
                    IOInterface.getInstance().printMessage("Login successful. Welcome, " + userInput[0]);

                    IOInterface.getInstance().printMessage("\n");
                    IOInterface.getInstance().printMessage("====== Admin Menu ======");

                    IOInterface.getInstance().printMessage("\n");
                    IOInterface.getInstance().adminMenu();

                    IOInterface.getInstance().printMessage("===============================");
                    IOInterface.getInstance().printMessage("\n");

                    String[] inputChoice = IOInterface.getInstance().getUserInput("Enter your choice: ", 1);

                    switch (Integer.valueOf(inputChoice[0])) {
                        case 1 -> {
                            int pageNum = 1;
                            while (true) {
                                ProductListResult result = ProductOperation.getInstance().getProductList(pageNum);
                                IOInterface.getInstance().showList("admin", "Product", result.getProducts(), result.getCurrentPage(), result.getTotalPages());

                                String[] choice = IOInterface.getInstance().getUserInput(
                                        "Enter 'n' for next page, 'p' for previous page, or 'b' to go back\nEnter your choice: ", 1
                                );

                                if (choice[0].equalsIgnoreCase("n") && pageNum < result.getTotalPages()) pageNum++;
                                else if (choice[0].equalsIgnoreCase("p") && pageNum > 1) pageNum--;
                                else if (choice[0].equalsIgnoreCase("b")) break;
                            }
                        }
                    }
                } else {
                    IOInterface.getInstance().printErrorMessage("checking your information", "Your username and password does not exist");
                }
            }
        }
    }
}