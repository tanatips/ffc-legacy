package th.in.ffc.map.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import th.in.ffc.map.FGActivity;
import th.in.ffc.map.database.DatabaseManager;
import th.in.ffc.map.preference.PreferenceFilter;
import th.in.ffc.map.value.FILTER_GROUP;
import th.in.ffc.map.value.MARKER_TYPE;
import th.in.ffc.map.village.spot.Spot;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class ProcessingFilterAsyncTask extends AsyncTask<String, String, String> {

    private FGActivity act;
    private SharedPreferences sh;
    private SharedPreferences sh_house;

    public ProcessingFilterAsyncTask(FGActivity act, SharedPreferences sh) {
        this.act = act;
        this.sh = act.getSharedPreferences(PreferenceFilter.FILE_XML, Context.MODE_PRIVATE);
        this.sh_house = act.getSharedPreferences("house_preference", Context.MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        act.setSupportProgressBarIndeterminateVisibility(true);
//		mProgressDialog = new ProgressDialog(act);
//		mProgressDialog.setMessage("Please wait...");
//		mProgressDialog.setCancelable(false);
//		mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        boolean current = sh.getBoolean("enable_checkbox", false);

        if (current) {
            // TODO : (A) Begin!

            Collection<Spot> marked = FGActivity.fgsys.getFGDatabaseManager().getMarked().values();
            TreeMap<Integer, Spot> houseSet = new TreeMap<Integer, Spot>();

            String house_codename = MARKER_TYPE.HOUSE.name();

            for (Spot entry : marked) {

                //entry.setVisible(sh.getBoolean(entry.getUid().toLowerCase() + "_checkbox", true));

                if (entry.getUid().equals(house_codename))
                    houseSet.put(entry.getPartialID(), entry);
            }

            boolean group_check = sh.getBoolean("house_checkbox", true);
            boolean group_all_check = sh_house.getBoolean("house_checkbox_all", true);

            // TODO : (A) Special Cases of House Marker
            if (!houseSet.isEmpty()) {
                if (group_check && !group_all_check) {

                    FILTER_GROUP group[] = FILTER_GROUP.values();

                    // Generate the WHERE clause of House
                    boolean firstElement = true;
                    StringBuilder houseClause = new StringBuilder();
                    houseClause.append("(");
                    for (Integer i : houseSet.keySet()) {
                        if (firstElement)
                            firstElement = false;
                        else
                            houseClause.append(" OR ");

                        houseClause.append("h.hcode=" + i);
                    }
                    houseClause.append(")");

                    String finalHouseClause = houseClause.toString();

                    DatabaseManager db = FGActivity.fgsys.getFGDatabaseManager().getDatabaseManager();
                    if (db.openDatabase()) {

                        for (int i = 0; i < group.length && !houseSet.isEmpty(); i++) {
                            if (sh_house.getBoolean(group[i].getCheckBoxName(), true)) {
                                this.accumulateFilterList(houseSet, group[i], db, finalHouseClause);
                            }
                        }

                        if (sh_house.getBoolean("unable_checkbox", false) && !houseSet.isEmpty()) {
                            String query = "SELECT DISTINCT h.hcode,count(DISTINCT p1.pid)"
                                    + " FROM house h INNER JOIN person p ON h.hcode=p.hcode LEFT JOIN personunable p1 ON p.pid = p1.pid AND (p1.dateexpire IS NULL OR date(p1.dateexpire) < date('now'))"
                                    + " WHERE " + finalHouseClause + " GROUP BY h.hcode";

                            processFilter(db, query, houseSet);
                        }

                        if (sh_house.getBoolean("pregnant_checkbox", false) && !houseSet.isEmpty()) {
                            String date = sh_house.getString("pregnant_date", "2003-01-01");
                            String query = "SELECT DISTINCT h.hcode,count(DISTINCT v.pid)"
                                    + " FROM house h INNER JOIN person p ON h.hcode=p.hcode LEFT JOIN visitancpregnancy v ON p.pid = v.pid AND (date(v.edc) > date('now')) AND (date(v.lmp) BETWEEN date('"
                                    + date + "') AND date('now'))" + " WHERE " + finalHouseClause + " GROUP BY h.hcode";

                            processFilter(db, query, houseSet);
                        }

                        if (sh_house.getBoolean("after-pregnant_checkbox", false) && !houseSet.isEmpty()) {
                            // TODO
                            String date = sh_house.getString("after-pregnant_date", "2003-01-01");
                            String query = "SELECT DISTINCT h.hcode,count(DISTINCT v.pid)"
                                    + " FROM house h INNER JOIN person p ON h.hcode=p.hcode LEFT JOIN visitancdeliver v ON p.pid = v.pid AND (date(v.datedeliver) BETWEEN date('"
                                    + date + "') AND date('now'))" + " WHERE " + finalHouseClause + " GROUP BY h.hcode";
                            processFilter(db, query, houseSet);
                        }

                        if (sh_house.getBoolean("old_checkbox", false) && !houseSet.isEmpty()) {
                            String query = "SELECT DISTINCT h.hcode,count(DISTINCT p1.pid)"
                                    + " FROM house h INNER JOIN person p ON h.hcode=p.hcode LEFT JOIN person p1 ON p.pid = p1.pid AND strftime('%Y',datetime(strftime('%s','now') - strftime('%s',p1.birth),'unixepoch')) - 1970 >= 60"
                                    + " WHERE " + finalHouseClause + " GROUP BY h.hcode";

                            processFilter(db, query, houseSet);
                        }

                        db.closeDatabase();
                    }

                } else if (group_check && group_all_check) {
                    // Reset Only House to be all visible
                    for (Spot spot : houseSet.values()) {
                      //  spot.setVisible(true);
                    }
                }
            }

        } else if (FGActivity.filter_enabled) {
            // Reset everything back to normal

            Collection<Spot> marked = FGActivity.fgsys.getFGDatabaseManager().getMarked().values();

            for (Spot entry : marked) {
               // entry.setVisible(true);
            }
        }

        FGActivity.filter_enabled = current;

        return null;
    }

    private void processFilter(DatabaseManager db, String query, TreeMap<Integer, Spot> houseSet) {

        // Log.d("TAG!", query);

        Cursor cur = db.getCursor(query);

        // if(houseSet.isEmpty())
        // return;

        if (cur.moveToFirst()) {
            do {

                boolean visible = cur.getInt(1) > 0;
                int key = cur.getInt(0);
                Spot spot = houseSet.get(key);
                if (spot == null)
                    continue;

               // spot.setVisible(visible);

                if (visible) {
                    houseSet.remove(key);
                    if (houseSet.isEmpty())
                        break;
                }

            } while (cur.moveToNext());
        }

        cur.close();
        cur = null;
    }

    private void accumulateFilterList(TreeMap<Integer, Spot> houseSet, FILTER_GROUP fg, DatabaseManager db, String finalHouseClause) {
        String query = null;
        String column = null;
        String quote = "'";
        switch (fg) {
            case CHRONIC:
                query = "SELECT DISTINCT h.hcode,count(DISTINCT p1.pid) "
                        + "FROM house h INNER join person p ON h.hcode=p.hcode LEFT JOIN personchronic p1 ON p1.pid = p.pid LEFT JOIN cdisease c ON p1.chroniccode = c.diseasecode LEFT JOIN cdiseasechronic c1 ON c.codechronic = c1.groupcode AND *|-|* "
                        + "WHERE " + finalHouseClause + " " + "GROUP BY h.hcode";
                column = "c1.groupcode";
                break;
            case DISEASE:
                String date = sh_house.getString("disease_date", "2003-01-01");
                query = "SELECT DISTINCT h.hcode,count(DISTINCT c1.group506code) "
                        + "FROM house h INNER JOIN person p ON h.hcode=p.hcode LEFT JOIN (SELECT v.pid,v.visitno,max(date(v.visitdate)) as 'maxdate' FROM visit v GROUP BY v.pid) v ON v.pid = p.pid LEFT JOIN visitdiag506address v1 ON v.visitno = v1.visitno AND v1.status <> 1 AND (date(v1.sickdatefind) BETWEEN date('"
                        + date
                        + "') AND date('now')) LEFT JOIN cdisease c ON c.diseasecode = v1.diagcode LEFT JOIN cdisease506 c1 ON c1.group506code = c.code506 AND *|-|* "
                        + "WHERE " + finalHouseClause + " " + "GROUP BY h.hcode";
                column = "c1.group506code";
                break;
            case VOLA:
                query = "SELECT h.hcode,count(DISTINCT h1.pidvola)" + " FROM house h LEFT JOIN house h1 ON h.hcode = h1.hcode AND *|-|*" + " WHERE "
                        + finalHouseClause + " " + " GROUP BY h.hcode";
                column = "h1.pidvola";
                quote = "";
                break;
        }

        SharedPreferences sp_group = act.getSharedPreferences(fg.getPreferenceName(), Context.MODE_PRIVATE);
        boolean checkAll = sp_group.getBoolean("check_all", true);

        @SuppressWarnings("unchecked")
        Map<String, Boolean> map = (Map<String, Boolean>) sp_group.getAll();

        StringBuilder sb = new StringBuilder();
        boolean firstElement = true;
        sb.append("(");
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (entry.getKey().equals("check_all"))
                continue;
            if (checkAll || entry.getValue()) {
                if (firstElement)
                    firstElement = false;
                else
                    sb.append(" OR ");

                sb.append(column).append("=").append(quote + entry.getKey() + quote);
            }
        }
        sb.append(")");

        if (sb.length() == 2 || sb.equals("()")) {
            sb.setLength(0);
            sb.append("(1=1)");
        }

        // sb.append(" OR "+column+" IS NULL").append(")");
        query = query.replace("*|-|*", sb.toString());

        processFilter(db, query, houseSet);
    }

    @Override
    protected void onPostExecute(String result) {
        //mProgressDialog.dismiss();

        act.setSupportProgressBarIndeterminateVisibility(false);
        FGActivity.fgsys.getFGMapManager().getMapView().invalidate();
    }

}
