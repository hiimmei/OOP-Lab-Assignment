package model;

public class Order {
    private String orderId;
    private String userId;
    private String proId;
    private String orderTime;

    public Order() {
        //Constructor
    }

    public Order(String orderId, String userId, String proId, String orderTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.proId = proId;
        this.orderTime = orderTime;
    }

    //Getter
    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public String getProId() {
        return proId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    //Setter
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", proId='" + proId + '\'' +
                ", orderTime='" + orderTime + '\'' +
                '}';
    }
}