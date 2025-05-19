package operation;

import model.User;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.Base64;

public class UserOperation {
    private static UserOperation instance;

    private static final String DATA_DIR  = "data";
    private static final String USER_FILE = "users.txt";

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[A-Za-z0-9_]{3,20}$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$");

    private UserOperation() {
        try {
            Path dataDir = Paths.get(DATA_DIR);
            if (Files.notExists(dataDir)) {
                Files.createDirectory(dataDir);
            }
            Path userFile = dataDir.resolve(USER_FILE);
            if (Files.notExists(userFile)) {
                Files.createFile(userFile);
            }
        } catch (IOException e) {
            System.err.println("Không thể khởi tạo thư mục data hoặc file users.txt: " + e.getMessage());
        }
    }


    public static UserOperation getInstance() {
        if (instance == null) {
            synchronized (UserOperation.class) {
                if (instance == null) {
                    instance = new UserOperation();
                }
            }
        }
        return instance;
    }


    public String generateUniqueUserId() {
        List<User> existing = readAllUsers();
        String newId;
        Random random = new Random();
        do {
            int num = 10000 + random.nextInt(90000); // 10000..99999
            newId = "u_" + num;
        } while (existing.stream().anyMatch(u -> u.getUserId().equals(newId)));
        return newId;
    }


    public String encryptPassword(String plainPwd) {
        if (plainPwd == null) return "";
        return Base64.getEncoder().encodeToString(plainPwd.getBytes());
    }


    public String decryptPassword(String encryptedPwd) {
        if (encryptedPwd == null) return "";
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedPwd);
            return new String(decoded);
        } catch (IllegalArgumentException e) {
            return "";
        }
    }


    public boolean checkUsernameExist(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return false;
        }
        List<User> list = readAllUsers();
        return list.stream()
                .anyMatch(u -> u.getUserName().equalsIgnoreCase(userName));
    }


    public boolean validateUsername(String userName) {
        if (userName == null) return false;
        return USERNAME_PATTERN.matcher(userName).matches();
    }


    public boolean validatePassword(String password) {
        if (password == null) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public User login(String userName, String rawPassword) {
        if (userName == null || rawPassword == null) {
            return null;
        }
        List<User> list = readAllUsers();
        for (User u : list) {
            if (u.getUserName().equalsIgnoreCase(userName)) {
                String decrypted = decryptPassword(u.getUserPassword());
                if (decrypted.equals(rawPassword)) {
                    return u;
                } else {
                    return null; // username đúng nhưng pass sai
                }
            }
        }
        return null; // không tìm thấy username
    }


    private List<User> readAllUsers() {
        List<User> list = new ArrayList<>();
        Path userFile = Paths.get(DATA_DIR, USER_FILE);
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    User u = parseUserFromString(trimmed);
                    if (u != null) {
                        list.add(u);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file users.txt: " + e.getMessage());
        }
        return list;
    }


    private boolean overwriteAllUsers(List<User> users) {
        Path userFile = Paths.get(DATA_DIR, USER_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile.toFile(), false))) {
            for (User u : users) {
                writer.write(u.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi đè file users.txt: " + e.getMessage());
            return false;
        }
    }

    private User parseUserFromString(String s) {
        if (!s.startsWith("{") || !s.endsWith("}")) {
            return null;
        }
        String body = s.substring(1, s.length() - 1).trim();
        // Split theo dấu phẩy, bỏ qua phẩy nằm trong dấu "..."
        String[] parts = body.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        String userId = null, userName = null, userPassword = null, userRegisterTime = null, userRole = null;
        for (String part : parts) {
            String[] kv = part.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (kv.length != 2) continue;
            String key = kv[0].trim().replaceAll("^\"|\"$", "");
            String value = kv[1].trim().replaceAll("^\"|\"$", "");
            switch (key) {
                case "user_id":
                    userId = value;
                    break;
                case "user_name":
                    userName = value;
                    break;
                case "user_password":
                    userPassword = value;
                    break;
                case "user_register_time":
                    userRegisterTime = value;
                    break;
                case "user_role":
                    userRole = value;
                    break;
                default:
                    break;
            }
        }
        if (userId != null && userName != null && userPassword != null
                && userRegisterTime != null && userRole != null) {
            return new User(userId, userName, userPassword, userRegisterTime, userRole);
        }
        return null;
    }
}