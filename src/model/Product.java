package model;

public class Product {
    private String proId;
    private String proModel;
    private String proCategory;
    private String proName;
    private double proCurrentPrice;
    private double proRawPrice;
    private double proDiscount;
    private int proLikesCount;

    public Product() {
        //Constructor
    }

    public Product(String proId, String proModel, String proCategory, String proName,
                   double proCurrentPrice, double proRawPrice, double proDiscount, int proLikesCount) {
        this.proId = proId;
        this.proModel = proModel;
        this.proCategory = proCategory;
        this.proName = proName;
        this.proCurrentPrice = proCurrentPrice;
        this.proRawPrice = proRawPrice;
        this.proDiscount = proDiscount;
        this.proLikesCount = proLikesCount;
    }

    //Getter
    public String getProId() {
        return proId;
    }

    public String getProModel() {
        return proModel;
    }

    public String getProCategory() {
        return proCategory;
    }

    public String getProName() {
        return proName;
    }

    public double getProCurrentPrice() {
        return proCurrentPrice;
    }

    public double getProRawPrice() {
        return proRawPrice;
    }

    public double getProDiscount() {
        return proDiscount;
    }

    public int getProLikesCount() {
        return proLikesCount;
    }

    //Setter
    public void setProId(String proId) {
        this.proId = proId;
    }

    public void setProModel(String proModel) {
        this.proModel = proModel;
    }

    public void setProCategory(String proCategory) {
        this.proCategory = proCategory;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public void setProCurrentPrice(double proCurrentPrice) {
        this.proCurrentPrice = proCurrentPrice;
    }

    public void setProRawPrice(double proRawPrice) {
        this.proRawPrice = proRawPrice;
    }

    public void setProDiscount(double proDiscount) {
        this.proDiscount = proDiscount;
    }

    public void setProLikesCount(int proLikesCount) {
        this.proLikesCount = proLikesCount;
    }

    @Override
    public String toString() {
        return "Product{" +
                "proId='" + proId + '\'' +
                ", proModel='" + proModel + '\'' +
                ", proCategory='" + proCategory + '\'' +
                ", proName='" + proName + '\'' +
                ", proCurrentPrice=" + proCurrentPrice +
                ", proRawPrice=" + proRawPrice +
                ", proDiscount=" + proDiscount +
                ", proLikesCount=" + proLikesCount +
                '}';
    }
}