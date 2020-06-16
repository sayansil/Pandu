package team.OG.pandu.Units;

public class PurchasedPass {
    String info;
    String pid;
    String id;

    public PurchasedPass(String info, String pid, String id) {
        this.info = info;
        this.pid = pid;
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
