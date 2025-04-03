package th.in.ffc.service;

import android.content.Context;
import th.in.ffc.dao.PersonVillageDao;
import th.in.ffc.model.VillageSummary;

import java.util.List;

/**
 * Service for accessing person village summary data
 */
public class PersonVillageService {

    private PersonVillageDao personVillageDao;

    public PersonVillageService(Context context) {
        this.personVillageDao = new PersonVillageDao(context);
    }

    /**
     * Get summary of person count grouped by village
     *
     * @return List of VillageSummary objects
     */
    public List<VillageSummary> getPersonCountByVillage() {
        return personVillageDao.getPersonCountByVillage();
    }

    /**
     * Get summary of person count for a specific village
     *
     * @param villageCode Village code to filter
     * @return VillageSummary object or null if not found
     */
    public VillageSummary getPersonCountForVillage(String villageCode) {
        return personVillageDao.getPersonCountForVillage(villageCode);
    }

    /**
     * Get total number of persons across all villages
     *
     * @return Total number of persons
     */
    public int getTotalPersonCount() {
        List<VillageSummary> summaries = getPersonCountByVillage();
        int total = 0;

        for (VillageSummary summary : summaries) {
            total += summary.getPersonCount();
        }

        return total;
    }
}