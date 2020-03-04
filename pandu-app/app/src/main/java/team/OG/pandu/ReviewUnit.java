package team.OG.pandu;

public class ReviewUnit {
    private String text;
    private String uid;

    public ReviewUnit(String text, String uid) {
        this.text = text;
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public String getUid() {
        return uid;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
