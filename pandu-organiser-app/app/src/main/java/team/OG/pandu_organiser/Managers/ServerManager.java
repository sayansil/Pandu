package team.OG.pandu_organiser.Managers;

import team.OG.pandu_organiser.Units.Feedback;

public interface ServerManager {

    void updateCount(String oid, int count);

    void updateFeedback(String oid, String uid, Feedback feedback);
}
