package operation;

import model.Order;
import result.OrderListResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


public class OrderOperation {
    private static OrderOperation instance;
    private static final String DATA_DIR = "data";
    private static final String ORDER_FILE = "orders.txt";
    private static final int PAGE_SIZE = 10;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
    private static final Random RANDOM = new Random();

    // Private constructor để đảm bảo Singleton
    private OrderOperation() {
        // Tạo thư mục và file nếu chưa tồn tại
        try {
            Path dataDir = Paths.get(DATA_DIR);
            if (Files.notExists(dataDir)) {
                Files.createDirectory(dataDir);
            }
            Path orderFile = dataDir.resolve(ORDER_FILE);
            if (Files.notExists(orderFile)) {
                Files.createFile(orderFile);
            }
        } catch (IOException e) {
            // Nếu có lỗi, in ra console (hoặc ghi log tùy cách bạn muốn xử lý)
            System.err.println("Unable to initialize data directory or order file: " + e.getMessage());
        }
    }

    /**
     * Trả về single instance của OrderOperation.
     */
    public static OrderOperation getInstance() {
        if (instance == null) {
            synchronized (OrderOperation.class) {
                if (instance == null) {
                    instance = new OrderOperation();
                }
            }
        }
        return instance;
    }


    public String generateUniqueOrderId() {
        List<Order> existing = readAllOrders();
        String newId;
        do {
            int num = 10000 + RANDOM.nextInt(90000); // 5 chữ số, từ 10000 đến 99999
            newId = "o_" + num;
        } while (existing.stream().anyMatch(o -> o.getOrderId().equals(newId)));
        return newId;
    }


    public boolean createAnOrder(String customerId, String productId, String createTime) {
        String orderId = generateUniqueOrderId();
        String time;
        if (createTime == null || createTime.trim().isEmpty()) {
            time = LocalDateTime.now().format(TIME_FORMATTER);
        } else {
            time = createTime;
        }
        Order newOrder = new Order(orderId, customerId, productId, time);

        Path orderFile = Paths.get(DATA_DIR, ORDER_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(orderFile.toFile(), true))) {
            writer.write(newOrder.toString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Error writing new order to file: " + e.getMessage());
            return false;
        }
    }


    public boolean deleteOrder(String orderId) {
        Path orderFile = Paths.get(DATA_DIR, ORDER_FILE);
        List<Order> all = readAllOrders();
        boolean removed = all.removeIf(o -> o.getOrderId().equals(orderId));
        if (!removed) {
            return false; // Không tìm thấy orderId
        }

        // Ghi ngược lại toàn bộ danh sách vào file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(orderFile.toFile(), false))) {
            for (Order o : all) {
                writer.write(o.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error rewriting order file after delete: " + e.getMessage());
            return false;
        }
    }


    public OrderListResult getOrderList(String customerId, int pageNumber) {
        List<Order> all = readAllOrders()
                .stream()
                .filter(o -> o.getUserId().equals(customerId))
                .collect(Collectors.toList());

        int total = all.size();
        int totalPages = (total + PAGE_SIZE - 1) / PAGE_SIZE;
        if (pageNumber < 1) pageNumber = 1;
        if (pageNumber > totalPages) pageNumber = totalPages;

        int fromIndex = (pageNumber - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, total);

        List<Order> pageList = new ArrayList<>();
        if (fromIndex < toIndex) {
            pageList = all.subList(fromIndex, toIndex);
        }

        OrderListResult result = new OrderListResult();
        result.setOrders(pageList);
        result.setCurrentPage(pageNumber);
        result.setTotalPages(totalPages);

        return result;
    }

    public void generateTestOrderData() {
        // Trong thực tế bạn sẽ gọi CustomerOperation để tạo 10 khách hàng.
        // Ở đây tạm giả sử 2 khách: c_00001, c_00002
        String[] sampleCustomers = {"c_00001", "c_00002"};
        String[] sampleProducts = {"p_00001", "p_00002", "p_00003", "p_00004"};

        for (String custId : sampleCustomers) {
            int count = 50 + RANDOM.nextInt(151); // từ 50 đến 200
            for (int i = 0; i < count; i++) {
                String pid = sampleProducts[RANDOM.nextInt(sampleProducts.length)];
                // Thời gian random trong năm 2025
                int month = 1 + RANDOM.nextInt(12);
                int day = 1 + RANDOM.nextInt(27);
                int hour = RANDOM.nextInt(24);
                int minute = RANDOM.nextInt(60);
                int second = RANDOM.nextInt(60);
                LocalDateTime randTime = LocalDateTime.of(2025, month, day, hour, minute, second);
                String strTime = randTime.format(TIME_FORMATTER);
                createAnOrder(custId, pid, strTime);
            }
        }
    }

    public void generateSingleCustomerConsumptionFigure(String customerId) {
        System.out.println("Generating consumption figure for customer: " + customerId);
    }

    public void generateAllCustomersConsumptionFigure() {
        System.out.println("Generating consumption figure for ALL customers");
    }

    public void generateAllTop10BestSellersFigure() {
        System.out.println("Generating TOP 10 Best Sellers figure");
    }

    public void deleteAllOrders() {
        Path orderFile = Paths.get(DATA_DIR, ORDER_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(orderFile.toFile(), false))) {
        } catch (IOException e) {
            System.err.println("Error clearing all orders: " + e.getMessage());
        }
    }

    private List<Order> readAllOrders() {
        List<Order> list = new ArrayList<>();
        Path orderFile = Paths.get(DATA_DIR, ORDER_FILE);

        try (BufferedReader reader = new BufferedReader(new FileReader(orderFile.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Order o = parseOrderFromString(line.trim());
                if (o != null) {
                    list.add(o);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading orders from file: " + e.getMessage());
        }

        return list;
    }

    private Order parseOrderFromString(String s) {
        if (!s.startsWith("{") || !s.endsWith("}")) {
            return null;
        }
        String body = s.substring(1, s.length() - 1).trim();

        String[] parts = body.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        String orderId = null, userId = null, proId = null, orderTime = null;
        for (String part : parts) {
            String[] kv = part.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (kv.length != 2) continue;
            String key = kv[0].trim().replaceAll("^\"|\"$", "");
            String value = kv[1].trim().replaceAll("^\"|\"$", "");
            switch (key) {
                case "order_id":
                    orderId = value;
                    break;
                case "user_id":
                    userId = value;
                    break;
                case "pro_id":
                    proId = value;
                    break;
                case "order_time":
                    orderTime = value;
                    break;
                default:
                    break;
            }
        }
        if (orderId != null && userId != null && proId != null && orderTime != null) {
            return new Order(orderId, userId, proId, orderTime);
        }
        return null;
    }
}