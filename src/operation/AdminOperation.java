package operation;

import file_manager.FileManager;
import iointerface.IOInterface;
import model.Admin;
import model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminOperation {
    private static AdminOperation instance;

    private AdminOperation () {}

    public static AdminOperation getInstance() {
        if (instance == null) {
            instance = new AdminOperation();
        }
        return instance;
    }

    public void registerAdmin() {
        List<User> users = FileManager.readObjects("data/users.txt", User::parseUserFromString);

        boolean adminExists = users.stream()
                .anyMatch(u -> u.getUserRole().equalsIgnoreCase("admin"));

        if (adminExists) {
            return;
        }

        IOInterface io = IOInterface.getInstance();
        io.printMessage("No admin found. Please register the first admin:");

        String[] input = io.getUserInput("Enter admin name and password (separated by space):", 2);
        String name = input[0];
        String password = input[1];

        String userId = UserOperation.getInstance().generateUniqueUserId();
        String encryptedPassword = UserOperation.getInstance().encryptPassword(password);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        String registerTime = LocalDateTime.now().format(dateTimeFormatter);

        Admin admin = new Admin(userId, name, encryptedPassword, registerTime, "admin");

        FileManager.writeObject("data/users.txt", admin, true);
        io.printMessage("Admin registered successfully. Welcome, " + input[0]);
    }

}