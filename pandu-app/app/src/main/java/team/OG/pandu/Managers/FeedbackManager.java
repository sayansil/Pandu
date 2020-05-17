package team.OG.pandu.Managers;

public interface FeedbackManager {

    void addFeedback(String text, double rating, String uid, String oid);

    void getFeedbackData(String oid);

    void analyzeFeedback();
}

