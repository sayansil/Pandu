package team.OG.pandu.Units;

public class User {
    boolean type;
    String name;
    String uid;
    String email;
    String password;

    public User(boolean type, String name, String uid, String email, String password) {
        this.type = type;
        this.name = name;
        this.uid = uid;
        this.email = email;
        this.password = password;
    }

    public User(boolean type, String name, String uid) {
        this.type = type;
        this.name = name;
        this.uid = uid;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
