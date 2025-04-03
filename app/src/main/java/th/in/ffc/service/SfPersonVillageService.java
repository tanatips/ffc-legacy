package th.in.ffc.service;

import android.content.Context;
import th.in.ffc.dao.SfPersonVillageDao;
import th.in.ffc.model.SfPersonVillageSummary;

import java.util.List;

/**
 * Service for accessing sf_person_info village summary data
 */
public class SfPersonVillageService {

    private SfPersonVillageDao sfPersonVillageDao;

    public SfPersonVillageService(Context context) {
        this.sfPersonVillageDao = new SfPersonVillageDao(context);
    }

    /**
     * Get summary of sf_person_info count grouped by village
     *
     * @return List of SfPersonVillageSummary objects
     */
    public List<SfPersonVillageSummary> getPersonCountByVillage() {
        return sfPersonVillageDao.getPersonCountByVillage();
    }

    /**
     * Get summary of person count for a specific village
     *
     * @param villageCode Village code to filter
     * @return SfPersonVillageSummary object or null if not found
     */
    public SfPersonVillageSummary getPersonCountForVillage(String villageCode) {
        return sfPersonVillageDao.getPersonCountForVillage(villageCode);
    }

    /**
     * Get total number of persons across all villages
     *
     * @return Total number of persons
     */
    public int getTotalPersonCount() {
        List<SfPersonVillageSummary> summaries = getPersonCountByVillage();
        int total = 0;

        for (SfPersonVillageSummary summary : summaries) {
            total += summary.getPersonCount();
        }

        return total;
    }
}