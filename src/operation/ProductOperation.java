package operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.stage.Stage;

import model.Product;
import file_manager.FileManager;
import result.ProductListResult;

public class ProductOperation {
    private final String productFilePath = "data/products.txt";

    private static ProductOperation instance;

    private List<Product> products;

    private ProductOperation () {
        products = new ArrayList<>();
    }

    public static ProductOperation getInstance() {
        if (instance == null) {
            instance = new ProductOperation();
        }
        return instance;
    }

    public void extractProductsFromFiles () {
        products = FileManager.readObjects(productFilePath, Product::parseProductFromString);
    }

    public ProductListResult getProductList (int pageNumber) {
        if (products == null || products.isEmpty()) {
            return new ProductListResult(new ArrayList<>(), 1, 1);
        }

        int pageSize = 10;
        int totalProducts = products.size();
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

        if (pageNumber < 1) pageNumber = 1;
        if (pageNumber > totalPages) pageNumber = totalPages;

        int startIndex = (pageNumber - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalProducts);

        List<Product> pageItems = products.subList(startIndex, endIndex);


        return new ProductListResult(pageItems, pageNumber, totalPages);
    }

    public boolean deleteProduct (String productId) {
        for (Product product : products) {
            String currentId = product.getProId();
            if (currentId.equalsIgnoreCase(productId)) {
                products.remove(product);
                List<String> lines = products.stream().map(Product::toString).toList();
                FileManager.writeLinesToFile(productFilePath, lines, false);
                return true;
            }
        }
        return false;
    }

    public List<Product> getProductListByKeyword (String keyword) {
        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (product.getProName().toLowerCase().contains(keyword.toLowerCase()) || product.getProModel().toLowerCase().contains(keyword.toLowerCase()))
                result.add(product);
        }
        return result;
    }

    public Product getProductById (String productId) {
        for (Product product : products) {
            String currentId = product.getProId();
            if (currentId.equalsIgnoreCase(productId)) {
                return product;
            }
        }
        return null;
    }

    public void generateCategoryFigure () {
        Map<String,Integer> listOfCategory = new HashMap<>();
        for (Product product : products) {
            String cat = product.getProCategory();
            int prevCat = listOfCategory.getOrDefault(cat, 0);
            listOfCategory.put(cat, prevCat + 1);
        }

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Category");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : listOfCategory.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        Stage stage = new Stage();
        Scene scene = new Scene(barChart, 800, 600);
        barChart.getData().add(series);
        stage.setScene(scene);
        stage.show();
    }

    public void generateDiscountFigure () {
        Map<String, Integer> discountGroups = new HashMap<>();
        discountGroups.put("0%", 0);
        discountGroups.put("<10%", 0);
        discountGroups.put("10–20%", 0);
        discountGroups.put(">20%", 0);

        for (Product product : products) {
            double discount = product.getProDiscount();
            if (discount == 0.0) {
                discountGroups.put("0%", discountGroups.get("0%") + 1);
            } else if (discount < 10.0) {
                discountGroups.put("<10%", discountGroups.get("<10%") + 1);
            } else if (discount <= 20.0) {
                discountGroups.put("10–20%", discountGroups.get("10–20%") + 1);
            } else {
                discountGroups.put(">20%", discountGroups.get(">20%") + 1);
            }
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : discountGroups.entrySet()) {
            if (entry.getValue() > 0) {
                pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
        }

        PieChart pieChart = new PieChart(pieData);

        Stage stage = new Stage();
        Scene scene = new Scene(pieChart, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    public void generateLikesCountFigure () {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Product");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Likes Count");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (Product product : products) {
            series.getData().add(
                    new XYChart.Data<>(product.getProName(), product.getProLikesCount())
            );
        }

        barChart.getData().add(series);

        Stage stage = new Stage();
        Scene scene = new Scene(barChart, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public void generateDiscountLikesCountFigure () {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Discount (%)");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Likes Count");

        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        for (Product product : products) {
            double discount = product.getProDiscount();
            int likes = product.getProLikesCount();
            series.getData().add(new XYChart.Data<>(discount, likes));
        }

        scatterChart.getData().add(series);

        Stage stage = new Stage();
        Scene scene = new Scene(scatterChart, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public void deleteAllProducts () {
        products.clear();
        FileManager.clearFile("data/products");
    }
}