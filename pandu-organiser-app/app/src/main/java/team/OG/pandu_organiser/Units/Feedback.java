package team.OG.pandu_organiser.Units;

public class Feedback {

    String text;
    double rating;
    String uid;

    public Feedback(String text, double rating, String uid) {
        this.text = text;
        this.rating = rating;
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
