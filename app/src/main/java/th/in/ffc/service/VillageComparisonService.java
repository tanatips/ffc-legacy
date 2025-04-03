package th.in.ffc.service;

import android.content.Context;
import th.in.ffc.dao.PersonVillageDao;
import th.in.ffc.dao.SfPersonVillageDao;
import th.in.ffc.model.SfPersonVillageSummary;
import th.in.ffc.model.VillageSummary;
import th.in.ffc.model.VillagePersonComparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for comparing person and sf_person_info data by village
 */
public class VillageComparisonService {

    private PersonVillageDao personVillageDao;
    private SfPersonVillageDao sfPersonVillageDao;

    public VillageComparisonService(Context context) {
        this.personVillageDao = new PersonVillageDao(context);
        this.sfPersonVillageDao = new SfPersonVillageDao(context);
    }

    /**
     * Get comparison data between person and sf_person_info by village
     *
     * @return List of VillagePersonComparison objects
     */
    public List<VillagePersonComparison> getVillageComparisonData() {
        List<VillagePersonComparison> resultList = new ArrayList<>();

        // ดึงข้อมูลสรุปจากทั้งสองตาราง
        List<VillageSummary> personSummaries = personVillageDao.getPersonCountByVillage();
        List<SfPersonVillageSummary> sfPersonSummaries = sfPersonVillageDao.getPersonCountByVillage();

        // สร้าง Map เพื่อให้ง่ายต่อการเข้าถึงข้อมูล
        Map<String, VillageSummary> personMap = new HashMap<>();
        Map<String, SfPersonVillageSummary> sfPersonMap = new HashMap<>();

        // เก็บข้อมูลลงใน Map
        for (VillageSummary summary : personSummaries) {
            personMap.put(summary.getVillageCode(), summary);
        }

        for (SfPersonVillageSummary summary : sfPersonSummaries) {
            sfPersonMap.put(summary.getVillageCode(), summary);
        }

        // ทำการรวมข้อมูลและคำนวณเปอร์เซ็นต์
        // 1. เริ่มจากข้อมูลใน person (ฐานประชากร)
        for (Map.Entry<String, VillageSummary> entry : personMap.entrySet()) {
            String villageCode = entry.getKey();
            VillageSummary personSummary = entry.getValue();
            int sfCount = 0;

            // หาข้อมูลจำนวนผู้รับบริการในหมู่บ้านเดียวกัน
            if (sfPersonMap.containsKey(villageCode)) {
                sfCount = sfPersonMap.get(villageCode).getPersonCount();
            }

            // สร้างข้อมูลเปรียบเทียบ
            VillagePersonComparison comparison = new VillagePersonComparison(
                    personSummary.getVillageCode(),
                    personSummary.getVillageNo(),
                    personSummary.getVillageName(),
                    personSummary.getPersonCount(),
                    sfCount
            );

            resultList.add(comparison);
        }

        // 2. เพิ่มข้อมูลหมู่บ้านที่มีในฐานข้อมูลผู้รับบริการแต่ไม่มีในฐานประชากร (ถ้ามี)
        for (Map.Entry<String, SfPersonVillageSummary> entry : sfPersonMap.entrySet()) {
            String villageCode = entry.getKey();

            // ถ้าไม่มีในฐานประชากร ให้เพิ่มเข้าไป
            if (!personMap.containsKey(villageCode)) {
                SfPersonVillageSummary sfSummary = entry.getValue();

                VillagePersonComparison comparison = new VillagePersonComparison(
                        sfSummary.getVillageCode(),
                        sfSummary.getVillageNo(),
                        sfSummary.getVillageName(),
                        0,  // ไม่มีข้อมูลในฐานประชากร
                        sfSummary.getPersonCount()
                );

                resultList.add(comparison);
            }
        }

        return resultList;
    }

    /**
     * Get overall coverage percentage
     *
     * @return Overall percentage of coverage
     */
    public double getOverallPercentage() {
        int totalPerson = 0;
        int totalSfPerson = 0;

        List<VillagePersonComparison> comparisonList = getVillageComparisonData();

        for (VillagePersonComparison comparison : comparisonList) {
            totalPerson += comparison.getPersonCount();
            totalSfPerson += comparison.getSfPersonCount();
        }

        if (totalPerson > 0) {
            return ((double) totalSfPerson / totalPerson) * 100;
        } else {
            return 0.0;
        }
    }
}