/* ***********************************************************************
 *                                                                 _ _ _
 *                                                               ( _ _  |
 *                                                           _ _ _ _  | |
 *                                                          (_ _ _  | |_|
 *  _     _   _ _ _ _     _ _ _   _ _ _ _ _   _ _ _ _     _ _ _   | | 
 * |  \  | | |  _ _ _|   /  _ _| |_ _   _ _| |  _ _ _|   /  _ _|  | |
 * | | \ | | | |_ _ _   /  /         | |     | |_ _ _   /  /      |_|
 * | |\ \| | |  _ _ _| (  (          | |     |  _ _ _| (  (    
 * | | \ | | | |_ _ _   \  \_ _      | |     | |_ _ _   \  \_ _ 
 * |_|  \__| |_ _ _ _|   \_ _ _|     |_|     |_ _ _ _|   \_ _ _| 
 *  a member of NSTDA, @Thailand
 *  
 * ***********************************************************************
 *
 *
 * FFC-Plus Project
 *
 * Copyright (C) 2010-2012 National Electronics and Computer Technology Center
 * All Rights Reserved.
 * 
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 * 
 */

package th.in.ffc.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import th.in.ffc.provider.PersonProvider.Person;
import th.in.ffc.provider.UserProvider.User;
import th.in.ffc.provider.UserProvider.UserDatabaseOpenHelper;

import java.util.HashMap;

/**
 * add description here! please
 *
 * @author Piruin Panichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class HouseProvider extends ContentProvider {

    public static String AUTHORITY = "th.in.ffc.provider.HouseProvider";

    private static final int HOUSE = 1;
    private static final int HOUSE_ID = 2;
    private static final int HOUSE_BY_VILLAGE_ID = 5;
    private static final int HOUSE_WITH_VILLAGE = 100;

    private static final int VILLAGE = 3;
    private static final int VILLAGE_ID = 4;
    private static final int VILLAGE_EXIST_HOUSE = 6;
    private static final int ANIMAL = 7;
    private static final int ANIMAL_BY_HOUSE_ID = 8;
    private static final int GENUSCULEX = 9;
    private static final int GENUSCULEX_ITEM = 399;
    private static final int VESSELWATER = 10;
    private static final int VESSELWATER_BY_HOUSE_ID = 11;

    //marker
    private static final int WATER = 12;
    private static final int VILLAGE_WATER = 13;
    private static final int TEMPLE = 14;
    private static final int VILLAGE_TEMPLE = 15;
    private static final int SCHOOL = 16;
    private static final int VILLAGE_SCHOOL = 17;
    private static final int BUSINESS = 18;
    private static final int VILLAGE_BUSINESS = 19;
    private static final int HOSPITAL = 20;
    private static final int VILLAGE_HOSPITAL = 21;
    private static final int POI = 22;
    private static final int VILLAGE_POI = 23;
    private static final int HOUSE_MARKER = 99;
    //filter
    private static final int HOUSE_VOLA = 94;
    private static final int HOUSE_PERSON = 95;
    private static final int HOUSE_AFTER_PREGNANT = 96;
    private static final int HOUSE_PREGNANT = 97;
    private static final int HOUSE_UNABLE = 98;

    private DbOpenHelper mOpenHelper;
    private static UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, "house", HOUSE);
        mUriMatcher.addURI(AUTHORITY, "house/#", HOUSE_ID);
        mUriMatcher.addURI(AUTHORITY, "house/village", HOUSE_WITH_VILLAGE);
        mUriMatcher.addURI(AUTHORITY, "house/marker", HOUSE_MARKER);
        mUriMatcher.addURI(AUTHORITY, "village/#/house", HOUSE_BY_VILLAGE_ID);
        mUriMatcher.addURI(AUTHORITY, "village", VILLAGE);
        mUriMatcher.addURI(AUTHORITY, "village/#", VILLAGE_ID);
        mUriMatcher.addURI(AUTHORITY, "village/house", VILLAGE_EXIST_HOUSE);
        mUriMatcher.addURI(AUTHORITY, "house/animal", ANIMAL);
        mUriMatcher.addURI(AUTHORITY, "house/animal/#", ANIMAL_BY_HOUSE_ID);
        mUriMatcher.addURI(AUTHORITY, "house/genusculex", GENUSCULEX);
        mUriMatcher.addURI(AUTHORITY, "house/genusculex/#", GENUSCULEX_ITEM);
        mUriMatcher.addURI(AUTHORITY, "house/vesselwater", VESSELWATER);
        mUriMatcher.addURI(AUTHORITY, "house/vesselwater/#", VESSELWATER_BY_HOUSE_ID);

        //for house's map filter
        mUriMatcher.addURI(AUTHORITY, "house/vola", HOUSE_VOLA);
        mUriMatcher.addURI(AUTHORITY, "house/person", HOUSE_PERSON);
        mUriMatcher.addURI(AUTHORITY, "house/person/unable", HOUSE_UNABLE);
        mUriMatcher.addURI(AUTHORITY, "house/person/pregnant", HOUSE_PREGNANT);
        mUriMatcher.addURI(AUTHORITY, "house/person/afterpregnant", HOUSE_AFTER_PREGNANT);

        mUriMatcher.addURI(AUTHORITY, "village/temple", TEMPLE);
        mUriMatcher.addURI(AUTHORITY, "village/#/temple", VILLAGE_TEMPLE);
        mUriMatcher.addURI(AUTHORITY, "village/water", WATER);
        mUriMatcher.addURI(AUTHORITY, "village/#/water", VILLAGE_WATER);
        mUriMatcher.addURI(AUTHORITY, "village/school", SCHOOL);
        mUriMatcher.addURI(AUTHORITY, "village/#/school", VILLAGE_SCHOOL);
        mUriMatcher.addURI(AUTHORITY, "village/business", BUSINESS);
        mUriMatcher.addURI(AUTHORITY, "village/#/business", VILLAGE_BUSINESS);
        mUriMatcher.addURI(AUTHORITY, "village/hospital", HOSPITAL);
        mUriMatcher.addURI(AUTHORITY, "village/#/hospital", VILLAGE_HOSPITAL);
        mUriMatcher.addURI(AUTHORITY, "village/poi", POI);
        mUriMatcher.addURI(AUTHORITY, "village/#/poi", VILLAGE_POI);


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows = 0;
        switch (mUriMatcher.match(uri)) {
            case VESSELWATER:
                rows = db.delete(HouseVesselWater.TABLENAME, selection, selectionArgs);
                break;
            case ANIMAL:
                rows = db.delete(HouseAnimal.TABLENAME, selection, selectionArgs);
                break;
            case GENUSCULEX:
                rows = db.delete(HouseGenusculex.TABLENAME, selection, selectionArgs);
            default:
                break;
        }
        return rows;
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case HOUSE:
            case HOUSE_BY_VILLAGE_ID:
            case HOUSE_MARKER:
                return House.CONTENT_DIR_TYPE;
            case HOUSE_ID:
                return House.CONTENT_ITEM_TYPE;
            case VILLAGE:
            case VILLAGE_EXIST_HOUSE:
                return Village.CONTENT_DIR_TYPE;
            case VILLAGE_ID:
                return Village.CONTENT_ITEM_TYPE;
            case ANIMAL:
                return HouseAnimal.CONTENT_DIR_TYPE;
            case ANIMAL_BY_HOUSE_ID:
                return HouseAnimal.CONTENT_ITEM_TYPE;
            case GENUSCULEX:
                return HouseGenusculex.CONTENT_ITEM_TYPE;
            case VESSELWATER:
                return HouseVesselWater.CONTENT_DIR_TYPE;
            case VESSELWATER_BY_HOUSE_ID:
                return HouseVesselWater.CONTENT_ITEM_TYPE;
            default:
                return null;
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case VESSELWATER:
                db.insert(HouseVesselWater.TABLENAME, null, values);
                break;
            case ANIMAL:
                db.insert(HouseAnimal.TABLENAME, null, values);
                break;
            case GENUSCULEX:
                db.insert(HouseGenusculex.TABLENAME, null, values);
            default:
                break;
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int updated = 0;
        switch (mUriMatcher.match(uri)) {
            case HOUSE_ID:
                selection = "hcode=" + uri.getLastPathSegment();
            case HOUSE:
                updated = db.update(House.TABLENAME, values, selection, selectionArgs);
                break;
            case VESSELWATER:
                updated = db.update(HouseVesselWater.TABLENAME, values, selection, selectionArgs);
                break;
            case ANIMAL:
                updated = db.update(HouseAnimal.TABLENAME, values, selection, selectionArgs);
                break;
            case GENUSCULEX:
                updated = db.update(HouseGenusculex.TABLENAME, values, selection, selectionArgs);
                break;
        }
        return updated;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DbOpenHelper(this.getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        if (!DbOpenHelper.isDbExist()) {
            //return cursor that will moveToFirst() = false
            UserDatabaseOpenHelper udoh = new UserDatabaseOpenHelper(getContext());
            SQLiteQueryBuilder bu = new SQLiteQueryBuilder();
            bu.setProjectionMap(UserProvider.PROJECTION_MAP);
            bu.setTables(User.TABLENAME);
            return bu.query(udoh.getReadableDatabase(),
                    new String[]{User.USERNAME},
                    "username='FFC-Nectec-50' AND password='004662'",
                    null, null, null, User.USERNAME);
        }

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        String groupby = null;
        String having = null;
        String defaultSort = null;
        switch (mUriMatcher.match(uri)) {
            case HOUSE:
                builder.setProjectionMap(House.PROJECTION_MAP);
                builder.setTables(House.TABLENAME);
                defaultSort = House.DEFAULT_SORTING;
                break;
            case HOUSE_ID:
                builder.setProjectionMap(House.PROJECTION_MAP);
                builder.setTables(House.TABLENAME);
                builder.appendWhere("house.hcode=" + uri.getLastPathSegment());
                defaultSort = House.DEFAULT_SORTING;
                break;
            case HOUSE_BY_VILLAGE_ID:
                builder.setProjectionMap(House.PROJECTION_MAP);
                builder.setTables(House.TABLENAME + " LEFT JOIN " + Person.TABLENAME +
                        " ON house.pid=person.pid");
                builder.appendWhere("house.villcode=" + uri.getPathSegments().get(1));
                defaultSort = House.DEFAULT_SORTING;
                break;
            case HOUSE_WITH_VILLAGE:
                HashMap<String, String> proj = new HashMap<String, String>(Village.PROJECTION_MAP);
                proj.putAll(House.PROJECTION_MAP);

                builder.setProjectionMap(proj);
                builder.setTables(House.TABLENAME + " LEFT JOIN " + Village.TABLENAME +
                        " ON house.villcode=village.villcode");
                defaultSort = House.DEFAULT_SORTING;
                break;
            case VILLAGE:
                builder.setProjectionMap(Village.PROJECTION_MAP);
                builder.setTables(Village.TABLENAME);
                defaultSort = Village.DEFAULT_SORTING;
                break;
            case VILLAGE_ID:
                builder.setProjectionMap(Village.PROJECTION_MAP);
                builder.setTables(Village.TABLENAME);
                builder.appendWhere("village.villcode=" + uri.getLastPathSegment());
                defaultSort = Village.DEFAULT_SORTING;
                break;
            case VILLAGE_EXIST_HOUSE:
                builder.setProjectionMap(Village.PROJECTION_MAP);
                //builder.setTables(Village.TABLENAME + " INNER JOIN ");
                builder.setTables(Village.TABLENAME + " INNER JOIN " + House.TABLENAME + " ON village.villcode=house.villcode");
                groupby = "village.villcode";
                defaultSort = Village.DEFAULT_SORTING;
                break;
            case ANIMAL:
                builder.setProjectionMap(HouseAnimal.PROJECTION_MAP);
                builder.setTables(HouseAnimal.TABLENAME);
                defaultSort = HouseAnimal.DEFAULT_SORTING;
                break;
            case ANIMAL_BY_HOUSE_ID:
                builder.setProjectionMap(HouseAnimal.PROJECTION_MAP);
                builder.setTables(HouseAnimal.TABLENAME);
                builder.appendWhere(HouseAnimal.HCODE + "=" + uri.getLastPathSegment());
                defaultSort = HouseAnimal.DEFAULT_SORTING;
                break;
            case GENUSCULEX:
                builder.setProjectionMap(HouseGenusculex.PROJECTION_MAP);
                builder.setTables(HouseGenusculex.TABLENAME);
                defaultSort = HouseGenusculex.DEFAULT_SORTING;
                break;
            case GENUSCULEX_ITEM:
                builder.setProjectionMap(HouseGenusculex.PROJECTION_MAP);
                builder.setTables(HouseGenusculex.TABLENAME);
                builder.appendWhere(HouseGenusculex.HCODE + "=" + uri.getLastPathSegment());
                defaultSort = HouseGenusculex.DEFAULT_SORTING;
                break;
            case VESSELWATER:
                builder.setProjectionMap(HouseVesselWater.PROJECTION_MAP);
                builder.setTables(HouseVesselWater.TABLENAME);
                defaultSort = HouseVesselWater.DEFAULT_SORTING;
                break;
            case VESSELWATER_BY_HOUSE_ID:
                builder.setProjectionMap(HouseVesselWater.PROJECTION_MAP);
                builder.setTables(HouseVesselWater.TABLENAME);
                builder.appendWhere(HouseVesselWater.HCODE + "=" + uri.getLastPathSegment());
                defaultSort = HouseVesselWater.DEFAULT_SORTING;
                break;


            /// Filter segment
            case HOUSE_PERSON:
                builder.setProjectionMap(House.PROJECTION_MAP);
                builder.setTables(House.TABLENAME_WITH_PERSON);
                groupby = House.HCODE;
                break;
            case HOUSE_VOLA:
                builder.setProjectionMap(House.PROJECTION_MAP);
                builder.setTables(House.TABLENAME_WITH_VOLA);
                groupby = House.HCODE;
            case HOUSE_PREGNANT:
                builder.setProjectionMap(House.PROJECTION_MAP);
                builder.setTables(House.TABLENAME_WITH_PREGNANT);
                groupby = House.HCODE;
                break;
            case HOUSE_UNABLE:
                builder.setProjectionMap(House.PROJECTION_MAP);
                builder.setTables(House.TABLENAME_WITH_UNABLE);
                groupby = House.HCODE;
                break;
            case HOUSE_AFTER_PREGNANT:
                builder.setProjectionMap(House.PROJECTION_MAP);
                builder.setTables(House.TABLENAME_WITH_AFTER_PREGNANT);
                groupby = House.HCODE;
                break;

            //marker
            case HOUSE_MARKER:
                builder.setProjectionMap(House.PROJECTION_MAP);
                builder.setTables(House.HOUSE_MARKER);
                break;
            case HOSPITAL:
                builder.setProjectionMap(Hospital.PROJECTION_MAP);
                builder.setTables(Hospital.TABLENAME);
                break;
            case VILLAGE_HOSPITAL:
                builder.setProjectionMap(Hospital.PROJECTION_MAP);
                builder.setTables(Hospital.TABLENAME);
                builder.appendWhere(Hospital.VILLCODE + "='" + uri.getPathSegments().get(1) + "'");
                break;
            case POI:
                builder.setProjectionMap(Poi.PROJECTION_MAP);
                builder.setTables(Poi.TABLENAME);
                break;
            case VILLAGE_POI:
                builder.setProjectionMap(Poi.PROJECTION_MAP);
                builder.setTables(Poi.TABLENAME);
                builder.appendWhere(Poi.VILLCODE + "='" + uri.getPathSegments().get(1) + "'");
                break;
            case WATER:
                builder.setProjectionMap(Water.PROJECTION_MAP);
                builder.setTables(Water.TABLENAME);
                break;
            case VILLAGE_WATER:
                builder.setProjectionMap(Water.PROJECTION_MAP);
                builder.setTables(Water.TABLENAME);
                builder.appendWhere(Water.VILLCODE + "='" + uri.getPathSegments().get(1) + "'");
                break;
            case TEMPLE:
                builder.setProjectionMap(Temple.PROJECTION_MAP);
                builder.setTables(Temple.TABLENAME);
                break;
            case VILLAGE_TEMPLE:
                builder.setProjectionMap(Temple.PROJECTION_MAP);
                builder.setTables(Temple.TABLENAME);
                builder.appendWhere(Temple.VILLCODE + "='" + uri.getPathSegments().get(1) + "'");
                break;
            case BUSINESS:
                builder.setProjectionMap(Business.PROJECTION_MAP);
                builder.setTables(Business.TABLENAME);
                break;
            case VILLAGE_BUSINESS:
                builder.setProjectionMap(Business.PROJECTION_MAP);
                builder.setTables(Business.TABLENAME);
                builder.appendWhere(Business.VILLCODE + "='" + uri.getPathSegments().get(1) + "'");
                break;
            case SCHOOL:
                builder.setProjectionMap(School.PROJECTION_MAP);
                builder.setTables(School.TABLENAME);
                break;
            case VILLAGE_SCHOOL:
                builder.setProjectionMap(School.PROJECTION_MAP);
                builder.setTables(School.TABLENAME);
                builder.appendWhere(School.VILLCODE + "='" + uri.getPathSegments().get(1) + "'");
                break;

            default:
                throw new IllegalArgumentException("Unknown URI : "
                        + uri.toString());
        }

        if (TextUtils.isEmpty(sortOrder))
            sortOrder = defaultSort;

        Cursor c = builder.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs,
                groupby, having, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }


    public static class House implements BaseColumns {

        public static final String TABLENAME = "house";

        //filter
        public static final String TABLENAME_WITH_VOLA = "house INNER JOIN person ON house.pidvola = person.pid";
        public static final String PERSON_VOLA_COUNT = "personvola";

        public static final String TABLENAME_WITH_PERSON = "person INNER JOIN house ON house.hcode = person.hcode";
        public static final String PERSON_IN_HOUSE = "personcount";

        public static final String TABLENAME_WITH_UNABLE = TABLENAME_WITH_PERSON + " LEFT JOIN personunable ON person.pid = personunable.pid";
        public static final String PERSON_UNABLE_COUNT = "unablecount";

        public static final String TABLENAME_WITH_AFTER_PREGNANT = TABLENAME_WITH_PERSON + " LEFT JOIN visitancdeliver ON person.pid = visitancdeliver.pid";
        public static final String PERSON_AFTER_PREGNANT_COUNT = "afpregcount";

        public static final String TABLENAME_WITH_PREGNANT = TABLENAME_WITH_PERSON + " LEFT JOIN visitancpregnancy ON person.pid = visitancpregnancy.pid";
        public static final String PERSON_PREGNANT_COUNT = "pregcount";

        public static final String HOUSE_MARKER = "house " +
                "LEFT JOIN (select person.hcode, count(person.pid) AS count from " + Person.TABLENAME_WITH_CHRONIC + " GROUP BY person.hcode) AS chronic ON house.hcode = chronic.hcode " +
                "LEFT JOIN (select person.hcode, count(person.pid) AS count from " + Person.TABLENAME_WITH_PERSONTYPE + " GROUP BY person.hcode) AS type ON  house.hcode = type.hcode";
        public static final String PERSON_CHRONIC_COUNT = "chronic.count";
        public static final String PERSON_TYPE_COUNT = "type.count";


        public static final Uri CONTENT_URI = Uri.parse("content://"
                + HouseProvider.AUTHORITY + "/house");

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.house";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.house";

        public static final String DEFAULT_SORTING = "house.villcode, CAST(hno AS INTEGER), length(hno) ,hno";

        public static final String PCUCODE = "pcucode";
        public static final String HNO = "hno";
        public static final String HCODE = "hcode";
        public static final String VILLCODE = "villcode";
        public static final String HID = "hid";
        public static final String TEL = "telephonehouse";
        public static final String ROAD = "road";
        public static final String AREA = "area";
        public static final String COMMUNITY = "communityno";
        public static final String PCUCODEPERSON = "pcucodeperson";
        public static final String PID = "pid";
        public static final String PIDVOLA = "pidvola";
        public static final String CHAR = "housechar";
        public static final String CHARGROUND = "housecharground";
        public static final String USERNAMEDOC = "usernamedoc";
        public static final String HEADHEALTHHOUSE = "headhealthhouse";
        public static final String DATEREGISTER = "dateregister";
        public static final String SURVEYDATE = "housurveydate";
        public static final String X_GIS = "xgis";
        public static final String Y_GIS = "ygis";
        public static final String DATEUPDATE = "dateupdate";
        //carrier
        public static final String CONTROLRAT = "controlrat";
        public static final String CONTROLCOCKROACH = "controlcockroach";
        public static final String CONTROLHOUSEFLY = "controlhousefly";
        public static final String CONTROLMQT = "controlmqt";
        public static final String CONTROLINSECTDISEASE = "controlinsetdisease";
        public static final String NEARHOUSE = "nearhouse";


        private static HashMap<String, String> PROJECTION_MAP;

        static {
            PROJECTION_MAP = new HashMap<String, String>();

            PROJECTION_MAP.put(House.PCUCODE, House.PCUCODE);
            PROJECTION_MAP.put(House._ID, "house.hcode AS " + House._ID);
            PROJECTION_MAP.put(House.HCODE, House.HCODE);
            PROJECTION_MAP.put(House.HNO, "house.hno AS " + House.HNO);
            PROJECTION_MAP.put(House.HID, "house.hid AS " + House.HID);
            PROJECTION_MAP.put(House.VILLCODE, "house.villcode AS " + House.VILLCODE);
            PROJECTION_MAP.put(House.TEL, "house.telephonehouse AS " + House.TEL);
            PROJECTION_MAP.put(House.ROAD, "house.road AS " + House.ROAD);
            PROJECTION_MAP.put(House.CHAR, "house.housechar AS " + House.CHAR);
            PROJECTION_MAP.put(House.CHARGROUND, "housecharground AS " + House.CHARGROUND);
            PROJECTION_MAP.put(House.AREA, "house.area AS " + House.AREA);
            PROJECTION_MAP.put(House.COMMUNITY, "house.communityno AS " + House.COMMUNITY);
            PROJECTION_MAP.put(House.VILLCODE, "house.villcode AS " + House.VILLCODE);
            PROJECTION_MAP.put(House.PCUCODEPERSON, House.PCUCODEPERSON);
            PROJECTION_MAP.put(House.PID, "house.pid AS " + House.PID);
            PROJECTION_MAP.put(Person.FULL_NAME, Person.PROJECTION_MAP.get(Person.FULL_NAME));
            PROJECTION_MAP.put(House.DATEUPDATE, "house.dateupdate AS " + House.DATEUPDATE);
            PROJECTION_MAP.put(House.SURVEYDATE, "house.surveydate AS " + House.SURVEYDATE);
            PROJECTION_MAP.put(House.X_GIS, "house.xgis AS " + House.X_GIS);
            PROJECTION_MAP.put(House.Y_GIS, "house.ygis AS " + House.Y_GIS);
            PROJECTION_MAP.put(House.PIDVOLA, "house.pidvola AS " + House.PIDVOLA);
            PROJECTION_MAP.put(House.DATEREGISTER, "house.dateregister AS " + House.DATEREGISTER);
            PROJECTION_MAP.put(House.USERNAMEDOC, "house.usernamedoc AS " + House.USERNAMEDOC);
            PROJECTION_MAP.put(House.HEADHEALTHHOUSE, "house.headhealthhouse AS " + House.HEADHEALTHHOUSE);

            // Peojection for Carrier
            PROJECTION_MAP.put(House.CONTROLRAT, "house.controlrat AS " + House.CONTROLRAT);
            PROJECTION_MAP.put(House.CONTROLCOCKROACH, "house.controlcockroach AS " + House.CONTROLCOCKROACH);
            PROJECTION_MAP.put(House.CONTROLHOUSEFLY, "house.controlhousefly AS " + House.CONTROLHOUSEFLY);
            PROJECTION_MAP.put(House.CONTROLMQT, "house.controlmqt AS " + House.CONTROLMQT);
            PROJECTION_MAP.put(House.CONTROLINSECTDISEASE, "house.controlinsetdisease AS " + House.CONTROLINSECTDISEASE);
            PROJECTION_MAP.put(House.NEARHOUSE, "house.nearhouse AS " + House.NEARHOUSE);

            // Projection for Water Sanitation
            PROJECTION_MAP.put(Sanitation.WATERDRINK, "house.waterdrink AS " + Sanitation.WATERDRINK);
            PROJECTION_MAP.put(Sanitation.WATERDRINKENO, "house.waterdrinkeno AS " + Sanitation.WATERDRINKENO);
            PROJECTION_MAP.put(Sanitation.WATERUSE, "house.wateruse AS " + Sanitation.WATERUSE);
            PROJECTION_MAP.put(Sanitation.WATERUSEENO, "house.wateruseeno AS " + Sanitation.WATERUSEENO);


            // Projection for Food
            PROJECTION_MAP.put(Sanitation.FOODCOOK, "house.foodcook AS " + Sanitation.FOODCOOK);
            PROJECTION_MAP.put(Sanitation.FOODKEEPSAFE, "house.foodkeepsafe AS " + Sanitation.FOODKEEPSAFE);
            PROJECTION_MAP.put(Sanitation.FOODWARE, "house.foodware AS " + Sanitation.FOODWARE);
            PROJECTION_MAP.put(Sanitation.FOODWAREWASH, "house.	foodwarewash AS " + Sanitation.FOODWAREWASH);
            PROJECTION_MAP.put(Sanitation.FOODWAREKEEP, "house.foodwarekeep AS " + Sanitation.FOODWAREKEEP);
            PROJECTION_MAP.put(Sanitation.FOODGARBAGEWARE, "house.foodgarbageware AS " + Sanitation.FOODGARBAGEWARE);
            PROJECTION_MAP.put(Sanitation.FOODCOOKROOM, "house.	foodcookroom AS " + Sanitation.FOODCOOKROOM);
            PROJECTION_MAP.put(Sanitation.FOODSANITATION, "house.foodsanitation AS " + Sanitation.FOODSANITATION);
            PROJECTION_MAP.put(Sanitation.IODEINSALT, "house.iodeinsalt AS " + Sanitation.IODEINSALT);
            PROJECTION_MAP.put(Sanitation.IODEINMATERIAL, "house.iodeinmaterial	AS " + Sanitation.IODEINMATERIAL);
            PROJECTION_MAP.put(Sanitation.IODEINUSE, "house.iodeinuse	AS " + Sanitation.IODEINUSE);
            PROJECTION_MAP.put(Sanitation.FTLJ, "house.ftlj AS " + Sanitation.FTLJ);
            PROJECTION_MAP.put(Sanitation.WHJRK, "house.whjrk AS " + Sanitation.WHJRK);
            PROJECTION_MAP.put(Sanitation.SLPP, "house.slpp AS " + Sanitation.SLPP);
            PROJECTION_MAP.put(Sanitation.CHT, "house.cht AS " + Sanitation.CHT);
            PROJECTION_MAP.put(Sanitation.KMCH, "house.kmch AS " + Sanitation.KMCH);

            // Projection for House Sanitation
            PROJECTION_MAP.put(Sanitation.HOUSEENDURE, "house.houseendur AS " + Sanitation.HOUSEENDURE);
            PROJECTION_MAP.put(Sanitation.HOUSECLEAN, "house.houseclean AS " + Sanitation.HOUSECLEAN);
            PROJECTION_MAP.put(Sanitation.HOUSECOMPLETE, "house.housecomplete AS " + Sanitation.HOUSECOMPLETE);
            PROJECTION_MAP.put(Sanitation.HOUSEAIRFLOW, "house.houseairflow AS " + Sanitation.HOUSEAIRFLOW);
            PROJECTION_MAP.put(Sanitation.HOUSELIGHT, "house.houselight AS " + Sanitation.HOUSELIGHT);
            PROJECTION_MAP.put(Sanitation.HOUSESANITATION, "house.housesanitation AS " + Sanitation.HOUSESANITATION);
            PROJECTION_MAP.put(Sanitation.TOILET, "house.toilet AS " + Sanitation.TOILET);
            PROJECTION_MAP.put(Sanitation.WATERASSUAGE, "house.waterassuage AS " + Sanitation.WATERASSUAGE);
            PROJECTION_MAP.put(Sanitation.GARBAGEWARE, "house.garbageware AS " + Sanitation.GARBAGEWARE);
            PROJECTION_MAP.put(Sanitation.GARBAGEERASE, "house.garbageerase AS " + Sanitation.GARBAGEERASE);
            PROJECTION_MAP.put(Sanitation.PETS, "house.pets AS " + Sanitation.PETS);
            PROJECTION_MAP.put(Sanitation.PETSDUNG, "house.petsdung AS " + Sanitation.PETSDUNG);


            PROJECTION_MAP.put(PERSON_CHRONIC_COUNT, PERSON_CHRONIC_COUNT);
            PROJECTION_MAP.put(PERSON_TYPE_COUNT, PERSON_TYPE_COUNT);
            PROJECTION_MAP.put(PERSON_PREGNANT_COUNT, "count(visitancpregnancy.pid) AS " + PERSON_PREGNANT_COUNT);
            PROJECTION_MAP.put(PERSON_UNABLE_COUNT, "count(personunable.pid) AS " + PERSON_UNABLE_COUNT);
            PROJECTION_MAP.put(PERSON_IN_HOUSE, "count(person.pid) AS " + PERSON_IN_HOUSE);
            PROJECTION_MAP.put(PERSON_AFTER_PREGNANT_COUNT, "count(visitancdeliver.pid) AS " + PERSON_AFTER_PREGNANT_COUNT);
            PROJECTION_MAP.put(PERSON_VOLA_COUNT, "count(person.pid) AS " + PERSON_VOLA_COUNT);

        }

        public static Uri getHouseUriByVillCode(long villcode) {
            return Uri.withAppendedPath(Village.CONTENT_URI, villcode + "/house");
        }

        public static class Sanitation {
            //water
            public static final String WATERDRINK = "waterdrink";
            public static final String WATERDRINKENO = "waterdrinkno";
            public static final String WATERUSE = "wateruse";
            public static final String WATERUSEENO = "wateruseeno";
            //food
            public static final String FOODCOOK = "foodcook";
            public static final String FOODKEEPSAFE = "foodkeepsafe";
            public static final String FOODWARE = "foodware";
            public static final String FOODWAREWASH = "foodwarewash";
            public static final String FOODWAREKEEP = "foodwarekeep";
            public static final String FOODGARBAGEWARE = "foodgarbageware";
            public static final String FOODCOOKROOM = "foodcookroom";
            public static final String FOODSANITATION = "foodsanitation";
            public static final String IODEINSALT = "iodeinsalt";
            public static final String IODEINMATERIAL = "iodeinmaterial";
            public static final String IODEINUSE = "iodeinuse";
            public static final String FTLJ = "ftlj";
            public static final String WHJRK = "whjrk";
            public static final String SLPP = "slpp";
            public static final String CHT = "cht";
            public static final String KMCH = "kmch";
            //house sanitation
            public static final String HOUSEENDURE = "houseendur";
            public static final String HOUSECLEAN = "houseclean";
            public static final String HOUSECOMPLETE = "housecomplete";
            public static final String HOUSEAIRFLOW = "houseairflow";
            public static final String HOUSELIGHT = "houselight";
            public static final String HOUSESANITATION = "housesanitation";
            public static final String TOILET = "toilet";
            public static final String WATERASSUAGE = "waterassuage";
            public static final String GARBAGEWARE = "garbageware";
            public static final String GARBAGEERASE = "garbageerase";
            public static final String PETS = "pets";
            public static final String PETSDUNG = "petsdung";
        }
    }


    public static class HouseAnimal implements BaseColumns {
        public static final String TABLENAME = "houseanimal";
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + HouseProvider.AUTHORITY + "/house/animal");

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.house.animal";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.house.animal";

        public static final String DEFAULT_SORTING = "hcode, animaltype";

        public static final String PCUPERSONCODE = "pcucode";
        public static final String HCODE = "hcode";
        public static final String TYPE = "animaltype";
        public static final String MALE = "animalmale";
        public static final String FEMALE = "animalfemale";

        private static HashMap<String, String> PROJECTION_MAP;

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(HouseAnimal._ID, "pcucode || hcode || animaltype AS " + HouseAnimal._ID);
            PROJECTION_MAP.put(HouseAnimal.HCODE, HouseAnimal.HCODE);
            PROJECTION_MAP.put(HouseAnimal.TYPE, HouseAnimal.TYPE);
            PROJECTION_MAP.put(HouseAnimal.MALE, HouseAnimal.MALE);
            PROJECTION_MAP.put(HouseAnimal.FEMALE, HouseAnimal.FEMALE);

        }
    }

    public static class HouseGenusculex implements BaseColumns {
        public static final String TABLENAME = "housegenusculex";
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + HouseProvider.AUTHORITY + "/house/genusculex");

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.house.genusculex";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.house.genusculex";

        public static final String DEFAULT_SORTING = "hcode, datesurvey";

        public static final String PCUPERSONCODE = "pcucode";
        public static final String HCODE = "hcode";
        public static final String DATESURVEY = "datesurvey";
        public static final String NOOFVES = "noofves";
        public static final String NOOFGENUSCULEX = "noofgenusculex";

        private static HashMap<String, String> PROJECTION_MAP;

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(HouseGenusculex._ID, "pcucode || hcode || datesurvey AS " + HouseGenusculex._ID);
            PROJECTION_MAP.put(HouseGenusculex.HCODE, "hcode");
            PROJECTION_MAP.put(HouseGenusculex.DATESURVEY, "datesurvey");
            PROJECTION_MAP.put(HouseGenusculex.NOOFVES, "noofves");
            PROJECTION_MAP.put(HouseGenusculex.NOOFGENUSCULEX, "noofgenusculex");
        }
    }

    public static class HouseVesselWater implements BaseColumns {
        public static final String TABLENAME = "housevesselwater";
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + HouseProvider.AUTHORITY + "/house/vesselwater");

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.house.vesselwater";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.house.vesselwater";

        public static final String DEFAULT_SORTING = "hcode, vessel";

        public static final String PCUPERSONCODE = "pcucode";
        public static final String HCODE = "hcode";
        public static final String VESSELCODE = "vessel";
        public static final String QUANTITY = "quantity";


        private static HashMap<String, String> PROJECTION_MAP;

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(HouseVesselWater._ID, "pcucode || hcode || veesel AS " + HouseVesselWater._ID);
            PROJECTION_MAP.put(HouseVesselWater.HCODE, "hcode");
            PROJECTION_MAP.put(HouseVesselWater.VESSELCODE, "vessel");
            PROJECTION_MAP.put(HouseVesselWater.QUANTITY, "quantity");

        }
    }

    public static class Village implements BaseColumns {
        public static final String TABLENAME = "village";
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + HouseProvider.AUTHORITY + "/village");

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.village";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.village";

        public static final String DEFAULT_SORTING = "village.villcode";

        public static final String PCUPERSONCODE = "pcucode";
        public static final String VILLNO = "villno";
        public static final String VILLNAME = "villname";
        public static final String VILLCODE = "villcode";
        public static final String POSTCODE = "postcode";
        public static final String HOUSE_COUNT = "housecount";

        private static HashMap<String, String> PROJECTION_MAP;

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(Village._ID, "village.villcode AS "
                    + Village._ID);
            PROJECTION_MAP.put(Village.VILLCODE, "village.villcode AS "
                    + Village.VILLCODE);
            PROJECTION_MAP.put(Village.VILLNO, "village.villno AS "
                    + Village.VILLNO);
            PROJECTION_MAP.put(Village.VILLNAME, "village.villname AS "
                    + Village.VILLNAME);
            PROJECTION_MAP.put(Village.POSTCODE, "village.postcode AS "
                    + Village.POSTCODE);
            PROJECTION_MAP.put(Village.HOUSE_COUNT, "count(house.hno) AS "
                    + Village.HOUSE_COUNT);
        }
    }


    public static class Water implements BaseColumns {
        public static final String TABLENAME = "villagewater";
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + HouseProvider.AUTHORITY + "/village/water");

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.village.water";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.village.water";

        public static final String DEFAULT_SORTING = "villcode, waterno";

        public static final String PCUCODE = "pcucode";
        public static final String VILLCODE = "villcode";
        public static final String NO = "waterno";
        public static final String TYPE = "watertype";
        public static final String OWNER = "owner";
        public static final String OPERATER = "operater";
        public static final String ENABLE = "enableuse";
        public static final String QUALITY = "quality";
        public static final String UPDATE = "dateupdate";
        public static final String X_GIS = "xgis";
        public static final String Y_GIS = "ygis";

        private static HashMap<String, String> PROJECTION_MAP;

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(Water._ID, "villcode || waterno AS " + Water._ID);
            PROJECTION_MAP.put(Water.PCUCODE, Water.PCUCODE);
            PROJECTION_MAP.put(Water.VILLCODE, Water.VILLCODE);
            PROJECTION_MAP.put(Water.NO, Water.NO);
            PROJECTION_MAP.put(Water.TYPE, Water.TYPE);
            PROJECTION_MAP.put(Water.OWNER, Water.OWNER);
            PROJECTION_MAP.put(Water.OPERATER, Water.OPERATER);
            PROJECTION_MAP.put(Water.ENABLE, Water.ENABLE);
            PROJECTION_MAP.put(Water.QUALITY, Water.QUALITY);
            PROJECTION_MAP.put(Water.UPDATE, Water.UPDATE);
            PROJECTION_MAP.put(Water.X_GIS, Water.X_GIS);
            PROJECTION_MAP.put(Water.Y_GIS, Water.Y_GIS);

        }
    }

    public static class Temple implements BaseColumns {
        public static final String TABLENAME = "villagetemple";
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + HouseProvider.AUTHORITY + "/village/temple");

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.village.temple";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.village.temple";

        public static final String DEFAULT_SORTING = "villcode, templeno";

        public static final String PCUCODE = "pcucode";
        public static final String VILLCODE = "villcode";
        public static final String NO = "templeno";
        public static final String NAME = "templename";
        public static final String RELIGION = "religion";
        public static final String CHARACTERISTIC = "characteristic";
        public static final String ADDRESS = "address";
        public static final String UPDATE = "dateupdate";
        public static final String X_GIS = "xgis";
        public static final String Y_GIS = "ygis";

        private static HashMap<String, String> PROJECTION_MAP;

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(Temple._ID, "villcode || templeno AS " + Temple._ID);
            PROJECTION_MAP.put(Temple.PCUCODE, Temple.PCUCODE);
            PROJECTION_MAP.put(Temple.VILLCODE, Temple.VILLCODE);
            PROJECTION_MAP.put(Temple.NO, Temple.NO);
            PROJECTION_MAP.put(Temple.NAME, Temple.NAME);
            PROJECTION_MAP.put(Temple.RELIGION, Temple.RELIGION);
            PROJECTION_MAP.put(Temple.CHARACTERISTIC, Temple.CHARACTERISTIC);
            PROJECTION_MAP.put(Temple.ADDRESS, Temple.ADDRESS);
            PROJECTION_MAP.put(Temple.UPDATE, Temple.UPDATE);
            PROJECTION_MAP.put(Temple.X_GIS, Temple.X_GIS);
            PROJECTION_MAP.put(Temple.Y_GIS, Temple.Y_GIS);
        }

    }

    public static class School implements BaseColumns {
        public static final String TABLENAME = "villageschool";
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + HouseProvider.AUTHORITY + "/village/school");

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.village.school";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.village.school";

        public static final String DEFAULT_SORTING = "villcode, schoolno";

        public static final String PCUCODE = "pcucode";
        public static final String VILLCODE = "villcode";
        public static final String NO = "schoolno";
        public static final String NAME = "schoolname";
        public static final String DEPEND = "depend";
        public static final String MAX_CLASS = "maxclass";
        public static final String ADDRESS = "address";
        public static final String TEL = "telephone";
        public static final String TEACHER_MALE = "amountteacherm";
        public static final String TEACHER_FEMALE = "amountteacherf";
        public static final String TEACHER_HEALTH_CARE = "teacherhealthcare";
        public static final String USER = "user";
        public static final String UPDATE = "dateupdate";
        public static final String X_GIS = "xgis";
        public static final String Y_GIS = "ygis";

        private static HashMap<String, String> PROJECTION_MAP;

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(_ID, "villcode || schoolno AS " + Temple._ID);
            PROJECTION_MAP.put(PCUCODE, PCUCODE);
            PROJECTION_MAP.put(VILLCODE, VILLCODE);
            PROJECTION_MAP.put(NO, NO);
            PROJECTION_MAP.put(NAME, NAME);
            PROJECTION_MAP.put(DEPEND, DEPEND);
            PROJECTION_MAP.put(MAX_CLASS, MAX_CLASS);
            PROJECTION_MAP.put(ADDRESS, ADDRESS);
            PROJECTION_MAP.put(TEACHER_MALE, TEACHER_MALE);
            PROJECTION_MAP.put(TEACHER_FEMALE, TEACHER_FEMALE);
            PROJECTION_MAP.put(TEACHER_HEALTH_CARE, TEACHER_HEALTH_CARE);
            PROJECTION_MAP.put(TEL, TEL);
            PROJECTION_MAP.put(USER, USER);
            PROJECTION_MAP.put(UPDATE, UPDATE);
            PROJECTION_MAP.put(X_GIS, X_GIS);
            PROJECTION_MAP.put(Y_GIS, Y_GIS);

        }

    }

    public static class Business implements BaseColumns {
        public static final String TABLENAME = "villagebusiness";
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + HouseProvider.AUTHORITY + "/village/business");

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.village.business";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.village.business";

        public static final String DEFAULT_SORTING = "villcode, businessno";

        public static final String PCUCODE = "pcucode";
        public static final String VILLCODE = "villcode";
        public static final String NO = "businessno";
        public static final String NAME = "businessname";
        public static final String TYPE = "businesstype";
        public static final String OWNER = "owner";
        public static final String ADDRESS = "address";
        public static final String REGIS_DATE = "regis";
        public static final String EMPLOYEE_MALE = "amountemployeem";
        public static final String EMPLOYEE_FEMALE = "amountemployeef";
        public static final String FOOD_AND_DRINK = "foodanddrink";
        public static final String FRESHMART = "freshmart";
        public static final String ALCHOHOL = "alchoholpermit";
        public static final String UPDATE = "dateupdate";
        public static final String X_GIS = "xgis";
        public static final String Y_GIS = "ygis";

        private static HashMap<String, String> PROJECTION_MAP;

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(_ID, "villcode || businessno AS " + Business._ID);
            PROJECTION_MAP.put(PCUCODE, PCUCODE);
            PROJECTION_MAP.put(VILLCODE, VILLCODE);
            PROJECTION_MAP.put(NO, NO);
            PROJECTION_MAP.put(NAME, NAME);
            PROJECTION_MAP.put(TYPE, TYPE);
            PROJECTION_MAP.put(OWNER, OWNER);
            PROJECTION_MAP.put(ADDRESS, ADDRESS);
            PROJECTION_MAP.put(EMPLOYEE_FEMALE, EMPLOYEE_FEMALE);
            PROJECTION_MAP.put(EMPLOYEE_MALE, EMPLOYEE_MALE);
            PROJECTION_MAP.put(REGIS_DATE, REGIS_DATE);
            PROJECTION_MAP.put(FOOD_AND_DRINK, FOOD_AND_DRINK);
            PROJECTION_MAP.put(FRESHMART, FRESHMART);
            PROJECTION_MAP.put(ALCHOHOL, ALCHOHOL);
            PROJECTION_MAP.put(UPDATE, UPDATE);
            PROJECTION_MAP.put(X_GIS, X_GIS);
            PROJECTION_MAP.put(Y_GIS, Y_GIS);

        }
    }

    public static class Hospital implements BaseColumns {
        public static final String TABLENAME = "ffc_hospital";
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + HouseProvider.AUTHORITY + "/village/hospital");

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.village.hospital";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.village.hospital";

        public static final String DEFAULT_SORTING = "villcode, hospitalno";

        public static final String PCUCODE = "pcucode";
        public static final String VILLCODE = "villcode";
        public static final String NO = "hospitalno";
        public static final String NAME = "hospitalname";
        public static final String TEL = "tel";
        public static final String BED = "bedtotal";
        public static final String X_GIS = "xgis";
        public static final String Y_GIS = "ygis";

        private static HashMap<String, String> PROJECTION_MAP;

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(_ID, "villcode || hospitalno AS " + Hospital._ID);
            PROJECTION_MAP.put(PCUCODE, PCUCODE);
            PROJECTION_MAP.put(VILLCODE, VILLCODE);
            PROJECTION_MAP.put(NO, NO);
            PROJECTION_MAP.put(NAME, NAME);
            PROJECTION_MAP.put(TEL, TEL);
            PROJECTION_MAP.put(BED, BED);
            PROJECTION_MAP.put(X_GIS, X_GIS);
            PROJECTION_MAP.put(Y_GIS, Y_GIS);
        }
    }

    public static class Poi implements BaseColumns {
        public static final String TABLENAME = "ffc_poi";
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + HouseProvider.AUTHORITY + "/village/poi");

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.village.poi";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.village.poi";

        public static final String DEFAULT_SORTING = "villcode, poino";

        public static final String PCUCODE = "pcucode";
        public static final String VILLCODE = "villcode";
        public static final String NO = "poino";
        public static final String NAME = "poiname";
        public static final String TYPE = "poitype";
        public static final String TEL = "tel";
        public static final String X_GIS = "xgis";
        public static final String Y_GIS = "ygis";

        private static HashMap<String, String> PROJECTION_MAP;

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(_ID, "villcode || poino AS " + Poi._ID);
            PROJECTION_MAP.put(PCUCODE, PCUCODE);
            PROJECTION_MAP.put(VILLCODE, VILLCODE);
            PROJECTION_MAP.put(NO, NO);
            PROJECTION_MAP.put(NAME, NAME);
            PROJECTION_MAP.put(TEL, TEL);
            PROJECTION_MAP.put(TYPE, TYPE);
            PROJECTION_MAP.put(X_GIS, X_GIS);
            PROJECTION_MAP.put(Y_GIS, Y_GIS);
        }
    }


}
