package result;

import model.Order;
import java.util.List;

/**
 * OrderListResult: chứa danh sách Order và thông tin phân trang.
 */
public class OrderListResult {
    private List<Order> orders;
    private int currentPage;
    private int totalPages;

    public OrderListResult() {
    }

    public OrderListResult(List<Order> orders, int currentPage, int totalPages) {
        this.orders = orders;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}