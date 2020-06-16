package team.OG.pandu.Units;

public class LocalPass {
    String info;
    String pid;
    int remaining;

    public LocalPass(String info, String pid, int remaining) {
        this.info = info;
        this.pid = pid;
        this.remaining = remaining;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
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
}
