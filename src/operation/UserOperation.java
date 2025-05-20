package operation;

import iointerface.IOInterface;
import model.User;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class UserOperation {
    private static UserOperation instance = new UserOperation();
    private List<User> users;

    private static final String DATA_DIR  = "data";
    private static final String USER_FILE = "data/users.txt";

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z_]{5,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{5,}$");

    private UserOperation() {
        File dir = new File(DATA_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            IOInterface.getInstance().printErrorMessage("Init", "Không thể tạo thư mục data.");
        }

        File file = new File(USER_FILE);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    IOInterface.getInstance().printErrorMessage("Init", "Không thể tạo file users.txt.");
                }
            } catch (IOException e) {
                IOInterface.getInstance().printErrorMessage("Init", "Lỗi khi tạo file users.txt: " + e.getMessage());
            }
        }
    }


    public static UserOperation getInstance() {
        return instance;
    }


    public String generateUniqueUserId() {
        List<User> existing = readAllUsers();
        Random random = new Random();
        String newId;

        boolean isDuplicate;
        do {
            StringBuilder sb = new StringBuilder("u_");
            for (int i = 0; i < 10; i++) {
                sb.append(random.nextInt(10));
            }
            newId = sb.toString();

            isDuplicate = false;
            for (User u : existing) {
                if (u.getUserId().equals(newId)) {
                    isDuplicate = true;
                    break;
                }
            }
        } while (isDuplicate);

        return newId;
    }

    public String getCurrentTime() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }

    public String encryptPassword(String password) {
        if (password == null || password.isEmpty()) return "";

        Random random = new Random();
        String candidate = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randStr = new StringBuilder();

        for (int i = 0; i < password.length() * 2; i++) {
            randStr.append(candidate.charAt(random.nextInt(candidate.length())));
        }

        StringBuilder encrypted = new StringBuilder("^^");
        for (int i = 0; i < password.length(); i++) {
            encrypted.append(randStr.charAt(i * 2));
            encrypted.append(randStr.charAt(i * 2 + 1));
            encrypted.append(password.charAt(i));
        }
        encrypted.append("$$");
        return encrypted.toString();
    }


    public String decryptPassword(String encryptedPwd) {
        if (encryptedPwd == null || !encryptedPwd.startsWith("^^") || !encryptedPwd.endsWith("$$")) return "";

        String core = encryptedPwd.substring(2, encryptedPwd.length() - 2);
        StringBuilder original = new StringBuilder();
        for (int i = 2; i < core.length(); i += 3) {
            original.append(core.charAt(i));
        }
        return original.toString();
    }


    public boolean checkUsernameExist(String userName) {
        if (userName == null || userName.trim().isEmpty()) return false;

        return users.stream()
                .anyMatch(u -> u.getUserName().equalsIgnoreCase(userName));
    }


    public boolean validateUsername(String userName) {
        return userName != null && USERNAME_PATTERN.matcher(userName).matches();
    }


    public boolean validatePassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public User login(String userName, String rawPassword) {
        if (userName == null || rawPassword == null) return null;

        return readAllUsers().stream()
                .filter(u -> u.getUserName().equalsIgnoreCase(userName))
                .filter(u -> decryptPassword(u.getUserPassword()).equals(rawPassword))
                .findFirst()
                .orElse(null);
    }


    public List<User> readAllUsers() {
        List<User> loadedUsers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User u = User.parseUserFromString(line.trim());
                if (u != null) loadedUsers.add(u);
            }
        } catch (IOException e) {
            IOInterface.getInstance().printErrorMessage("ReadUsers", "Không thể đọc file users.txt: " + e.getMessage());
        }

        this.users = loadedUsers;
        return users;
    }


    public boolean overwriteAllUsers(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, false))) {
            for (User u : users) {
                writer.write(u.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            IOInterface.getInstance().printErrorMessage("WriteUsers", "Không thể ghi file users.txt: " + e.getMessage());
            return false;
        }
    }
}