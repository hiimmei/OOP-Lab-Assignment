package operation;

import iointerface.IOInterface;
import model.Customer;
import result.CustomerListResult;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class CustomerOperation {
    private static CustomerOperation instance;

    private static final String DATA_DIR       = "data";
    private static final String CUSTOMER_FILE  = "data/users.txt";
    private static final int    PAGE_SIZE      = 10;
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");

    private static final Pattern EMAIL_PATTERN  =
            Pattern.compile("^[\\w\\.-]+@[\\w\\.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern MOBILE_PATTERN =
            Pattern.compile("^(04|03)[0-9]{8}$");

    private CustomerOperation() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File customerFile = new File(CUSTOMER_FILE);
        if (!customerFile.exists()) {
            try {
                customerFile.createNewFile();
            } catch (IOException e) {
                IOInterface.getInstance().printErrorMessage("Init", "Không thể tạo file customers.txt: " + e.getMessage());
            }
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
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }


    public boolean validateMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile).matches();
    }

    public Customer registerCustomer(String userName, String password, String email, String mobile) {
        IOInterface io = IOInterface.getInstance();

        if (!validateEmail(email)) {
            io.printErrorMessage("Register", "Email không hợp lệ: " + email);
            return null;
        }
        if (!validateMobile(mobile)) {
            io.printErrorMessage("Register", "Số điện thoại không hợp lệ: " + mobile);
            return null;
        }

        UserOperation userOp = UserOperation.getInstance();
        String userId = userOp.generateUniqueUserId();
        String registerTime = LocalDateTime.now().format(TIME_FORMATTER);
        String encryptedPw = userOp.encryptPassword(password);

        Customer newCustomer = new Customer(
                userId, userName, encryptedPw, registerTime, "customer", email, mobile
        );

        io.printMessage("Customer registered successfully. Welcome, " + userName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMER_FILE, true))) {
            writer.write("{");
            writer.write(newCustomer.toString());
            writer.write("}");
            writer.newLine();
            return newCustomer;
        } catch (IOException e) {
            io.printErrorMessage("Register", "Lỗi khi ghi Customer vào file: " + e.getMessage());
            return null;
        }
    }


    public boolean updateProfile(String userId, String newEmail, String newMobile) {
        IOInterface io = IOInterface.getInstance();

        if (!validateEmail(newEmail)) {
            io.printErrorMessage("Update", "Email mới không hợp lệ: " + newEmail);
            return false;
        }
        if (!validateMobile(newMobile)) {
            io.printErrorMessage("Update", "Số điện thoại mới không hợp lệ: " + newMobile);
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
            io.printErrorMessage("Update", "Không tìm thấy userId = " + userId);
            return false;
        }

        return overwriteAllCustomers(allCustomers);
    }

    public boolean deleteCustomer(String userId) {
        IOInterface io = IOInterface.getInstance();

        List<Customer> allCustomers = readAllCustomers();
        boolean removed = allCustomers.removeIf(c -> c.getUserId().equals(userId));

        if (!removed) {
            io.printErrorMessage("Delete", "Không tìm thấy customer với ID: " + userId);
            return false;
        }

        return overwriteAllCustomers(allCustomers);
    }

    public CustomerListResult getCustomerList(int pageNumber) {
        List<Customer> allCustomers = readAllCustomers();
        int total = allCustomers.size();
        int totalPages = (total + PAGE_SIZE - 1) / PAGE_SIZE;
        if (totalPages == 0) totalPages = 1;

        pageNumber = Math.max(1, Math.min(pageNumber, totalPages));
        int fromIndex = (pageNumber - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, total);
        List<Customer> page = allCustomers.subList(fromIndex, toIndex);

        CustomerListResult result = new CustomerListResult();
        result.setCustomers(page);
        result.setCurrentPage(pageNumber);
        result.setTotalPages(totalPages);
        return result;
    }

    public void deleteAllCustomers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMER_FILE, false))) {
            // Clear file
        } catch (IOException e) {
            IOInterface.getInstance().printErrorMessage("DeleteAll", "Không thể xóa toàn bộ: " + e.getMessage());
        }
    }

    private List<Customer> readAllCustomers() {
        List<Customer> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Customer c = parseCustomerFromString(line.trim());
                if (c != null) list.add(c);
            }
        } catch (IOException e) {
            IOInterface.getInstance().printErrorMessage("ReadFile", "Lỗi khi đọc customers.txt: " + e.getMessage());
        }
        return list;
    }


    private boolean overwriteAllCustomers(List<Customer> list) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMER_FILE, false))) {
            for (Customer c : list) {
                writer.write(c.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            IOInterface.getInstance().printErrorMessage("Overwrite", "Lỗi ghi file: " + e.getMessage());
            return false;
        }
    }


    private Customer parseCustomerFromString(String s) {
        if (!s.startsWith("{") || !s.endsWith("}")) return null;

        String body = s.substring(1, s.length() - 1).trim();
        String[] parts = body.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        String userId = null, userName = null, userPassword = null;
        String userRegisterTime = null, userRole = null, userEmail = null, userMobile = null;

        for (String part : parts) {
            String[] kv = part.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (kv.length != 2) continue;
            String key = kv[0].trim().replaceAll("^\"|\"$", "");
            String value = kv[1].trim().replaceAll("^\"|\"$", "");

            switch (key) {
                case "user_id": userId = value; break;
                case "user_name": userName = value; break;
                case "user_password": userPassword = value; break;
                case "user_register_time": userRegisterTime = value; break;
                case "user_role": userRole = value; break;
                case "user_email": userEmail = value; break;
                case "user_mobile": userMobile = value; break;
            }
        }

        if (userId != null && userName != null && userPassword != null &&
                userRegisterTime != null && userRole != null &&
                userEmail != null && userMobile != null) {
            return new Customer(userId, userName, userPassword, userRegisterTime, userRole, userEmail, userMobile);
        }

        return null;
    }
}
