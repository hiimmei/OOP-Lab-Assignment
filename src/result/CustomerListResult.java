package result;

import model.Customer;
import java.util.List;

/**
 * CustomerListResult: chứa danh sách Customer và thông tin phân trang.
 */
public class CustomerListResult {
    private List<Customer> customers;
    private int currentPage;
    private int totalPages;

    public CustomerListResult() {
    }

    public CustomerListResult(List<Customer> customers, int currentPage, int totalPages) {
        this.customers = customers;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
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