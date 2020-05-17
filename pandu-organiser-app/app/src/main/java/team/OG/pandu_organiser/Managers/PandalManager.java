package team.OG.pandu_organiser.Managers;

import team.OG.pandu_organiser.Units.Pandal;

public interface PandalManager {
    void getRankingData();

    void fetchCurrentCrowdData(String oid);

    void updateInformation(String oid, Pandal pandal);

    void getInformation(String oid);
}
