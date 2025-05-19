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

    public static Product parseProductFromString(String line) {
        if (!line.startsWith("{") || !line.endsWith("}")) {
            return null;
        }

        String body = line.substring(1, line.length() - 1).trim();
        String[] parts = body.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        String proId = "";
        String proModel = "";
        String proCategory = "";
        String proName = "";
        double proCurrentPrice = 0;
        double proRawPrice = 0;
        double proDiscount = 0;
        int proLikesCount = 0;

        for (String part : parts) {
            String[] keyValue = part.split(":", 2);
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");

            switch (key) {
                case "pro_id" -> proId = value;
                case "pro_model" -> proModel = value;
                case "pro_category" -> proCategory = value;
                case "pro_name" -> proName = value;
                case "pro_current_price" -> proCurrentPrice = Double.parseDouble(value);
                case "pro_raw_price" -> proRawPrice = Double.parseDouble(value);
                case "pro_discount" -> proDiscount = Double.parseDouble(value);
                case "pro_likes_count" -> proLikesCount = Integer.parseInt(value);
            }
        }

        return new Product(proId, proModel, proCategory, proName,
                proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
    }
}