package operation;

import iointerface.IOInterface;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;

import model.Order;
import result.OrderListResult;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class OrderOperation {
    private static OrderOperation instance;
    private static final String DATA_DIR = "data";
    private static final String ORDER_FILE = "data/orders.txt";
    private static final String FIGURE_DIR = "data/figure";
    private static final int PAGE_SIZE = 10;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
    private static final Random RANDOM = new Random();

    // Initialize JavaFX toolkit
    static {
        new JFXPanel();
    }

    private OrderOperation() {
        createDirectory(DATA_DIR);
        createDirectory(FIGURE_DIR);
        createFile(ORDER_FILE);
    }

    public static OrderOperation getInstance() {
        if (instance == null) {
            synchronized (OrderOperation.class) {
                if (instance == null) instance = new OrderOperation();
            }
        }
        return instance;
    }

    private void createDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists() && !dir.mkdirs()) {
            IOInterface.getInstance().printErrorMessage("Init", "Cannot create directory: " + path);
        }
    }

    private void createFile(String path) {
        try {
            File f = new File(path);
            if (!f.exists() && !f.createNewFile()) {
                IOInterface.getInstance().printErrorMessage("Init", "Cannot create file: " + path);
            }
        } catch (IOException e) {
            IOInterface.getInstance().printErrorMessage("Init", "File error: " + e.getMessage());
        }
    }

// ========= order operations =========

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
        String time = (createTime == null || createTime.isBlank())
                ? LocalDateTime.now().format(TIME_FORMATTER)
                : createTime;
        Order newOrder = new Order(orderId, customerId, productId, time);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_FILE, true))) {
            writer.write(newOrder.toString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            IOInterface.getInstance().printErrorMessage("CreateOrder", "Cannot write new order: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteOrder(String orderId) {
        List<Order> all = readAllOrders();
        boolean removed = all.removeIf(o -> o.getOrderId().equals(orderId));
        if (!removed) return false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_FILE, false))) {
            for (Order o : all) {
                writer.write(o.toString()); writer.newLine();
            }
            return true;
        } catch (IOException e) {
            IOInterface.getInstance().printErrorMessage("DeleteOrder", e.getMessage());
            return false;
        }
    }

    public OrderListResult getOrderList(String customerId, int pageNumber) {
        List<Order> filtered = readAllOrders().stream()
                .filter(o -> o.getUserId().equals(customerId))
                .collect(Collectors.toList());
        return paginate(filtered, pageNumber);
    }

    public OrderListResult getOrderList(int pageNumber) {
        List<Order> all = readAllOrders();
        return paginate(all, pageNumber);
    }

    private OrderListResult paginate(List<Order> list, int pageNumber) {
        int total = list.size();
        int totalPages = Math.max(1, (total + PAGE_SIZE - 1) / PAGE_SIZE);
        pageNumber = Math.max(1, Math.min(pageNumber, totalPages));
        int from = (pageNumber - 1) * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, total);
        List<Order> page = list.subList(from, to);
        return new OrderListResult(page, pageNumber, totalPages);
    }

    public void generateTestOrderData() {
        // sample data logic ...
        IOInterface.getInstance().printMessage("Test order data generated.");
    }

    public void generateSingleCustomerConsumptionFigure(final String customerId) {
        List<Order> orders = readAllOrders().stream()
                .filter(o -> o.getUserId().equals(customerId))
                .collect(Collectors.toList());
        Map<Integer, Double> monthSum = new TreeMap<>();
        for (int m = 1; m <= 12; m++) monthSum.put(m, 0.0);
        for (Order o : orders) {
            int m = Integer.parseInt(o.getOrderTime().substring(3, 5));
            monthSum.put(m, monthSum.get(m) + 1.0);
        }
        LineChart<String, Number> chart = createLineChart("Month", "Consumption", "Customer " + customerId + " Consumption");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        monthSum.forEach((m, sum) -> series.getData().add(new XYChart.Data<>(String.valueOf(m), sum)));
        chart.getData().add(series);
        saveChart(chart, "single_customer_" + customerId + ".png");
    }

    public void generateAllCustomersConsumptionFigure() {
        List<Order> orders = readAllOrders();
        Map<Integer, Double> monthSum = new TreeMap<>();
        for (int m = 1; m <= 12; m++) monthSum.put(m, 0.0);
        for (Order o : orders) {
            int m = Integer.parseInt(o.getOrderTime().substring(3, 5));
            monthSum.put(m, monthSum.get(m) + 1.0);
        }
        LineChart<String, Number> chart = createLineChart("Month", "Total Consumption", "All Customers Consumption");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        monthSum.forEach((m, sum) -> series.getData().add(new XYChart.Data<>(String.valueOf(m), sum)));
        chart.getData().add(series);
        saveChart(chart, "all_customers_consumption.png");
    }

    public void generateAllTop10BestSellersFigure() {
        List<Order> orders = readAllOrders();
        Map<String, Long> countMap = orders.stream()
                .collect(Collectors.groupingBy(Order::getProId, Collectors.counting()));
        List<Map.Entry<String, Long>> top10 = countMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .collect(Collectors.toList());
        PieChart chart = new PieChart();
        chart.setTitle("Top 10 Best Sellers");
        top10.forEach(e -> chart.getData().add(new PieChart.Data(e.getKey(), e.getValue())));
        saveChart(chart, "top10_bestsellers.png");
    }

    public void deleteAllOrders() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_FILE, false))) {
            // overwrite with empty content
        } catch (IOException e) {
            IOInterface.getInstance().printErrorMessage("DeleteAllOrders", e.getMessage());
        }
    }

    private List<Order> readAllOrders() {
        List<Order> list = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new FileReader(ORDER_FILE))) {
            String line;
            while ((line = r.readLine()) != null) {
                Order o = parseOrderFromString(line);
                if (o != null) list.add(o);
            }
        } catch (IOException e) {
            IOInterface.getInstance().printErrorMessage("ReadOrders", e.getMessage());
        }
        return list;
    }

    private Order parseOrderFromString(String s) {
        if (!s.startsWith("{") || !s.endsWith("}")) return null;
        String body = s.substring(1, s.length() - 1);
        String[] parts = body.split(",");
        String id = "", uid = "", pid = "", time = "";
        for (String part : parts) {
            String[] kv = part.replace("\"", "").split(":");
            switch (kv[0].trim()) {
                case "order_id": id = kv[1]; break;
                case "user_id": uid = kv[1]; break;
                case "pro_id": pid = kv[1]; break;
                case "order_time": time = kv[1]; break;
            }
        }
        return id.isEmpty() ? null : new Order(id, uid, pid, time);
    }

    /**
     * Helper to create a standard LineChart with labeled axes and title.
     */
    private LineChart<String, Number> createLineChart(String xLabel, String yLabel, String title) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xLabel);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yLabel);
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(title);
        return chart;
    }

    /**
     * Safely snapshot and save a JavaFX chart to disk.
     */
    private <T extends javafx.scene.chart.Chart> void saveChart(T chart, String filename) {
        final String target = filename;
        Platform.runLater(() -> {
            Scene scene = new Scene(chart, 800, 600);
            chart.applyCss();
            WritableImage image = chart.snapshot(new SnapshotParameters(), null);
            File file = new File(FIGURE_DIR, target);
            try {
                ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(image, null), "png", file);
                IOInterface.getInstance().printMessage("Saved chart: " + file.getAbsolutePath());
            } catch (IOException e) {
                IOInterface.getInstance().printErrorMessage("SaveChart", e.getMessage());
            }
        });
    }
}