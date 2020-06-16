package team.OG.pandu.Units;

public class Pass {
    int admit;
    String info;
    String pid;
    String uid;
    double price;

    public Pass(int admit, String info, String pid, String uid, double price) {
        this.admit = admit;
        this.info = info;
        this.pid = pid;
        this.uid = uid;
        this.price = price;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getAdmit() {
        return admit;
    }

    public void setAdmit(int admit) {
        this.admit = admit;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
