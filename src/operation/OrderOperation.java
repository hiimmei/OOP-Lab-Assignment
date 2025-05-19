package operation;

import iointerface.IOInterface;
import model.Order;
import result.OrderListResult;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderOperation {
    private static OrderOperation instance;

    private static final String DATA_DIR = "data";
    private static final String ORDER_FILE = "data/orders.txt";
    private static final int PAGE_SIZE = 10;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
    private static final Random RANDOM = new Random();

    private OrderOperation() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                IOInterface.getInstance().printErrorMessage("Init", "Không thể tạo thư mục data.");
            }
        }

        File file = new File(ORDER_FILE);
        if (!file.exists()) {
            try {
                boolean created = file.createNewFile();
                if (!created) {
                    IOInterface.getInstance().printErrorMessage("Init", "Không thể tạo file orders.txt.");
                }
            } catch (IOException e) {
                IOInterface.getInstance().printErrorMessage("Init", "Lỗi khi tạo file orders.txt: " + e.getMessage());
            }
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

        while (true) {
            int num = 10000 + RANDOM.nextInt(90000);
            newId = "o_" + num;
            final String checkId = newId;
            boolean exists = existing.stream().anyMatch(o -> o.getOrderId().equals(checkId));
            if (!exists) break;
        }

        return newId;
    }


    public boolean createAnOrder(String customerId, String productId, String createTime) {
        String orderId = generateUniqueOrderId();
        String time = (createTime == null || createTime.trim().isEmpty())
                ? LocalDateTime.now().format(TIME_FORMATTER)
                : createTime;

        Order newOrder = new Order(orderId, customerId, productId, time);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_FILE, true))) {
            writer.write("{");
            writer.write(newOrder.toString());
            writer.write("}");
            writer.newLine();
            return true;
        } catch (IOException e) {
            IOInterface.getInstance().printErrorMessage("CreateOrder", "Không thể ghi order mới: " + e.getMessage());
            return false;
        }
    }



    public boolean deleteOrder(String orderId) {
        List<Order> all = readAllOrders();
        boolean removed = all.removeIf(o -> o.getOrderId().equals(orderId));
        if (!removed) return false;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_FILE, false))) {
            for (Order o : all) {
                writer.write(o.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            IOInterface.getInstance().printErrorMessage("DeleteOrder", "Lỗi khi ghi lại file: " + e.getMessage());
            return false;
        }
    }


    public OrderListResult getOrderList(String customerId, int pageNumber) {
        List<Order> filtered = new ArrayList<>();
        for (Order o : readAllOrders()) {
            if (o.getUserId().equals(customerId)) {
                filtered.add(o);
            }
        }

        int total = filtered.size();
        int totalPages = Math.max(1, (total + PAGE_SIZE - 1) / PAGE_SIZE);
        pageNumber = Math.max(1, Math.min(pageNumber, totalPages));

        int fromIndex = (pageNumber - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, total);
        List<Order> page = filtered.subList(fromIndex, toIndex);

        OrderListResult result = new OrderListResult(page, pageNumber, totalPages);
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
        IOInterface.getInstance().printMessage("Tạo dữ liệu đơn hàng test thành công.");
    }

    public void generateSingleCustomerConsumptionFigure(String customerId) {
        IOInterface.getInstance().printMessage("Tạo biểu đồ tiêu dùng cho customer: " + customerId);
        // TODO: Dùng javafx để vẽ biểu đồ
    }

    public void generateAllCustomersConsumptionFigure() {
        IOInterface.getInstance().printMessage("Tạo biểu đồ tiêu dùng cho toàn bộ customer.");
        // TODO: Dùng javafx để vẽ biểu đồ
    }

    public void generateAllTop10BestSellersFigure() {
        IOInterface.getInstance().printMessage("Tạo biểu đồ 10 sản phẩm bán chạy nhất.");
        // TODO: Dùng javafx để vẽ biểu đồ
    }

    public void deleteAllOrders() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_FILE, false))) {
            // Clear file
        } catch (IOException e) {
            IOInterface.getInstance().printErrorMessage("DeleteAllOrders", "Không thể xóa đơn hàng: " + e.getMessage());
        }
    }

    private List<Order> readAllOrders() {
        List<Order> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Order o = parseOrderFromString(line.trim());
                if (o != null) list.add(o);
            }
        } catch (IOException e) {
            IOInterface.getInstance().printErrorMessage("ReadOrders", "Không thể đọc file: " + e.getMessage());
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