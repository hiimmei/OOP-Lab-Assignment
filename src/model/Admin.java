package model;

public class Admin extends User {
    public Admin(String userId, String userName, String userPassword, String userRegisterTime, String userRole) {
        super(userId, userName, userPassword, userRegisterTime, "admin");
    }

    public Admin () {}

    @Override
    public String toString () {
        return super.toString();
    }
}