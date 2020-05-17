package team.OG.pandu_organiser.Managers;

import java.util.ArrayList;

import team.OG.pandu_organiser.Units.Feedback;

public interface FeedbackManager {

    void addFeedback(String text, double rating, String uid, String oid);

    void getFeedbackData(String oid);

    void getRanking();

    void analyzeFeedback();
}
