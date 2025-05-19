package model;

public abstract class User {
    private String userId;
    private String userName;
    private String userPassword;
    private String userRegisterTime;
    private String userRole;

    public User(String userId, String userName, String userPassword, String userRegisterTime, String userRole) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRegisterTime = userRegisterTime;
        this.userRole = userRole;
    }

    public User () {}

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserRegisterTime() {
        return userRegisterTime;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public void setUserRegisterTime(String userRegisterTime) {
        this.userRegisterTime = userRegisterTime;
    }

    @Override
    public String toString () {
        return "user_id: " + userId
                + ", user_name: " + userName
                + ", user_password: " + userPassword
                + ", user_register_time: " + userRegisterTime
                + ", user_role: " + userRole;
    }

    public static User parseUserFromString(String s) {
        if (!s.startsWith("{") || !s.endsWith("}")) {
            return null;
        }
        String body = s.substring(1, s.length() - 1).trim();
        // Split theo dấu phẩy, bỏ qua phẩy nằm trong dấu "..."
        String[] parts = body.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        String userId = null, userName = null, userPassword = null, userRegisterTime = null, userRole = null, userEmail = null, userMobile = null;
        for (String part : parts) {
            String[] kv = part.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (kv.length != 2) continue;
            String key = kv[0].trim().replaceAll("^\"|\"$", "");
            String value = kv[1].trim().replaceAll("^\"|\"$", "");
            switch (key) {
                case "user_id":
                    userId = value;
                    break;
                case "user_name":
                    userName = value;
                    break;
                case "user_password":
                    userPassword = value;
                    break;
                case "user_register_time":
                    userRegisterTime = value;
                    break;
                case "user_role":
                    userRole = value;
                    break;
                case "user_email":
                    userEmail = value;
                    break;
                case "user_mobile":
                    userMobile = value;
                    break;
                default:
                    break;
            }
        }
        if (userId != null && userName != null && userPassword != null
                && userRegisterTime != null && userRole != null) {
            if (userRole.equalsIgnoreCase("admin")) return new Admin(userId, userName, userPassword, userRegisterTime, userRole);
            else if (userRole.equalsIgnoreCase("customer")) return new Customer(userId, userName, userPassword, userRegisterTime, userRole, userEmail, userMobile);
        }
        return null;
    }

    public static User parseFromLine (String s) {
        return parseUserFromString(s);
    }
}

