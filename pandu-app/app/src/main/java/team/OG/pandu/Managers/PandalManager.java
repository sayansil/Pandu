package team.OG.pandu.Managers;


import team.OG.pandu.Units.Pandal;

public interface PandalManager {

    void getRankingData();

    void fetchCurrentCrowdData(String oid);

    void updateInformation(String oid, Pandal pandal);

    void getInformation(String oid);

    void getAllInformation();
}
