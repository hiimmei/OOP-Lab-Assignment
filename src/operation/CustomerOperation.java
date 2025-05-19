package operation;

import model.Customer;
import result.CustomerListResult;
import operation.UserOperation;    // Để gọi generateUniqueUserId()
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

public class CustomerOperation {
    private static CustomerOperation instance;

    private static final String DATA_DIR       = "data";
    private static final String CUSTOMER_FILE  = "customers.txt";
    private static final int    PAGE_SIZE      = 10;
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");

    private static final Pattern EMAIL_PATTERN  =
            Pattern.compile("^[\\w\\.-]+@[\\w\\.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern MOBILE_PATTERN =
            Pattern.compile("^[0-9]{10,11}$");

    private CustomerOperation() {
        try {
            Path dataDir = Paths.get(DATA_DIR);
            if (Files.notExists(dataDir)) {
                Files.createDirectory(dataDir);
            }
            Path customerFile = dataDir.resolve(CUSTOMER_FILE);
            if (Files.notExists(customerFile)) {
                Files.createFile(customerFile);
            }
        } catch (IOException e) {
            System.err.println("Không thể khởi tạo thư mục dữ liệu hoặc file customers.txt: " + e.getMessage());
        }
    }


    public static CustomerOperation getInstance() {
        if (instance == null) {
            synchronized (CustomerOperation.class) {
                if (instance == null) {
                    instance = new CustomerOperation();
                }
            }
        }
        return instance;
    }

    public boolean validateEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }


    public boolean validateMobile(String mobile) {
        if (mobile == null) return false;
        return MOBILE_PATTERN.matcher(mobile).matches();
    }

    public Customer registerCustomer(String userName, String password, String email, String mobile) {
        // Validate email/mobile
        if (!validateEmail(email)) {
            System.err.println("Email không hợp lệ: " + email);
            return null;
        }
        if (!validateMobile(mobile)) {
            System.err.println("Số điện thoại không hợp lệ: " + mobile);
            return null;
        }

        // Sinh userId duy nhất
        String newUserId = UserOperation.getInstance().generateUniqueUserId();
        // Timestamp đăng ký
        String registerTime = LocalDateTime.now().format(TIME_FORMATTER);
        // Vai trò mặc định
        String role = "customer";

        String encryptedPw = UserOperation.getInstance().encryptPassword(password);

        // Tạo đối tượng Customer
        Customer newCustomer = new Customer(
                newUserId,
                userName,
                encryptedPw,
                registerTime,
                role,
                email,
                mobile
        );

        Path customerFile = Paths.get(DATA_DIR, CUSTOMER_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(customerFile.toFile(), true))) {
            writer.write(newCustomer.toString());
            writer.newLine();
            return newCustomer;
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi Customer mới vào file: " + e.getMessage());
            return null;
        }
    }


    public boolean updateProfile(String userId, String newEmail, String newMobile) {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }
        if (!validateEmail(newEmail)) {
            System.err.println("Email mới không hợp lệ: " + newEmail);
            return false;
        }
        if (!validateMobile(newMobile)) {
            System.err.println("Số điện thoại mới không hợp lệ: " + newMobile);
            return false;
        }

        List<Customer> allCustomers = readAllCustomers();
        boolean found = false;
        for (Customer c : allCustomers) {
            if (c.getUserId().equals(userId)) {
                c.setUserEmail(newEmail);
                c.setUserMobile(newMobile);
                found = true;
                break;
            }
        }
        if (!found) {
            System.err.println("Không tìm thấy Customer với userId = " + userId);
            return false;
        }

        // Ghi đè lại file với danh sách đã update
        return overwriteAllCustomers(allCustomers);
    }


    public boolean deleteCustomer(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }
        List<Customer> allCustomers = readAllCustomers();
        boolean removed = allCustomers.removeIf(c -> c.getUserId().equals(userId));
        if (!removed) {
            System.err.println("Không tìm thấy Customer để xóa với userId = " + userId);
            return false;
        }
        // Ghi lại file mới (overwrite)
        return overwriteAllCustomers(allCustomers);
    }

    public CustomerListResult getCustomerList(int pageNumber) {
        List<Customer> allCustomers = readAllCustomers();
        int total = allCustomers.size();
        int totalPages = (total + PAGE_SIZE - 1) / PAGE_SIZE;
        if (totalPages == 0) {
            totalPages = 1;
        }
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageNumber > totalPages) {
            pageNumber = totalPages;
        }

        int fromIndex = (pageNumber - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, total);
        List<Customer> pageList = new ArrayList<>();
        if (fromIndex < toIndex) {
            pageList = allCustomers.subList(fromIndex, toIndex);
        }

        CustomerListResult result = new CustomerListResult();
        result.setCustomers(pageList);
        result.setCurrentPage(pageNumber);
        result.setTotalPages(totalPages);
        return result;
    }

    public void deleteAllCustomers() {
        Path customerFile = Paths.get(DATA_DIR, CUSTOMER_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(customerFile.toFile(), false))) {
            // Mở file với false => ghi đè, không ghi gì => xóa hết
        } catch (IOException e) {
            System.err.println("Lỗi khi xóa tất cả khách hàng: " + e.getMessage());
        }
    }

    private List<Customer> readAllCustomers() {
        List<Customer> list = new ArrayList<>();
        Path customerFile = Paths.get(DATA_DIR, CUSTOMER_FILE);

        try (BufferedReader reader = new BufferedReader(new FileReader(customerFile.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    Customer c = parseCustomerFromString(trimmed);
                    if (c != null) {
                        list.add(c);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file customers.txt: " + e.getMessage());
        }
        return list;
    }


    private boolean overwriteAllCustomers(List<Customer> listCustomers) {
        Path customerFile = Paths.get(DATA_DIR, CUSTOMER_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(customerFile.toFile(), false))) {
            for (Customer c : listCustomers) {
                writer.write(c.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi đè file customers.txt: " + e.getMessage());
            return false;
        }
    }


    private Customer parseCustomerFromString(String s) {
        if (!s.startsWith("{") || !s.endsWith("}")) {
            return null;
        }
        String body = s.substring(1, s.length() - 1).trim();
        // Split theo dấu phẩy nhưng bỏ qua phẩy nằm trong chuỗi ""
        String[] parts = body.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        String userId = null, userName = null, userPassword = null;
        String userRegisterTime = null, userRole = null, userEmail = null, userMobile = null;

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
                case "user_email":
                    userEmail = value;
                    break;
                case "user_mobile":
                    userMobile = value;
                    break;
                default:
                    // Bỏ qua nếu không khớp key nào
                    break;
            }
        }

        if (userId != null && userName != null && userPassword != null
                && userRegisterTime != null && userRole != null
                && userEmail != null && userMobile != null) {
            return new Customer(
                    userId,
                    userName,
                    userPassword,
                    userRegisterTime,
                    userRole,
                    userEmail,
                    userMobile
            );
        }
        return null;
    }
}