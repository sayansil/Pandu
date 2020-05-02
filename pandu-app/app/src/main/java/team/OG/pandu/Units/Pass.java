package team.OG.pandu.Units;

public class Pass {
    int admit;
    String info;
    String pid;
    double price;

    public Pass(int admit, String info, String pid, double price) {
        this.admit = admit;
        this.info = info;
        this.pid = pid;
        this.price = price;
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
