package th.in.ffc.map;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

//import org.osmdroid.google.wrapper.MyLocationOverlay;

//import org.osmdroid.google.wrapper.MyLocationOverlay;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;

import th.in.ffc.FamilyFolderCollector;
import th.in.ffc.R;
import th.in.ffc.app.FFCFragmentActivity;
import th.in.ffc.intent.Action;
import th.in.ffc.intent.Category;
import th.in.ffc.map.database.DatabaseManager;
import th.in.ffc.map.map.FGMapManager;
import th.in.ffc.map.preference.PreferenceFilter;
import th.in.ffc.map.service.GeneralAsyncTask;
import th.in.ffc.map.service.ProcessingFilterAsyncTask;
import th.in.ffc.map.system.FGSystemManager;
import th.in.ffc.map.value.FinalValue;
import th.in.ffc.map.value.MARKER_TYPE;
import th.in.ffc.map.village.spot.Spot;
import th.in.ffc.person.genogram.V1.Family;
import th.in.ffc.person.genogram.V1.FamilyTree;
import th.in.ffc.provider.HouseProvider.House;

public class FGActivity extends FFCFragmentActivity {

    public static String EXTRA_HCODE = "hcode";

    public static FGSystemManager fgsys = null;
    private static String PICTURE_PATH = null;
    private static String TEMP_PATH = null;
    private static String DATA_PATH = null;
    private ConnectivityReceiver conRec;

    public static final int INITIALIZE = 0;
    public static final int LOCATION_INITIALIZE = 1;
    public static final int UPDATE_WIFI_ICON = 2;
    public static final int UPDATE_GPS_ICON = 3;
    public static final int FAILED = -1;

    private MenuItem wifi_item;
    private MenuItem gps_item;

    private Handler handler;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    public static boolean filter_enabled = false;
    private boolean firsttime = true;
    String hcode;
    MapView mapView;
//    Spot item;
    public Spot item ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setSupportProgressBarIndeterminateVisibility(false);
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentId(getPcuCode())
                .putContentType("Map"));

        DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/" + this.getPackageName() + "/";
        PICTURE_PATH = DATA_PATH + "pictures/";
        TEMP_PATH = DATA_PATH + "temps/";
//        this.setContentView(R.layout.main_maps_activity);
        this.setContentView(R.layout.main_maps_activity);
        Intent intent = getIntent();
        hcode = intent.getStringExtra("hcode");
        loadMap();
        if(hcode != null) {
            goToHouse(hcode);
        } else {
            getCurrentLocation();
        }
    }
    private void searchHelper(final String query) {
        if(hcode!=null) {
            DatabaseManager db = fgsys.getFGDatabaseManager().getDatabaseManager();
//            Spot item = null;
            if (db.openDatabase()) {
                Cursor cur = db
                        .getCursor("SELECT distinct h.hno FROM person p,house h WHERE p.hcode=h.hcode and (h.xgis is not null and not h.xgis = '0.0' and not h.xgis = '0' and not h.xgis = ' ' and not h.xgis = '  ' and not h.xgis = '') and (h.ygis is not null and not h.ygis = '0.0' and not h.ygis = '0' and not h.ygis = ' ' and not h.ygis = '  ' and not h.ygis = '') and "
                                + query);

                if (cur.moveToFirst()) {
                    String hno = cur.getString(0);
                    item = normalSearch(hno);
                }
                cur.close();
                cur = null;
                db.closeDatabase();

            } else {
                Message msg = new Message();
                msg.what = FGActivity.FAILED;
                msg.obj = "Database cannot be opened. Please try again.";
                handler.sendMessage(msg);
            }
        }
    }
    private Spot normalSearch(String str) {
        // ItemizedIconOverlay<Spot> marker = fgSystemManager
        // .getFGOverlayManager().getMarker();
        Collection<Spot> marked = this.fgsys.getFGDatabaseManager().getMarked().values();
        String house_codename = MARKER_TYPE.HOUSE.name();

        for (Spot spot : marked) {
            if (spot.getUid().equals(house_codename) && spot.getBundle().getString("HNo").equals(str)) {
                return spot;
            }
        }

        return null;
    }
    private void loadMap(){
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        mapView = (MapView) findViewById(R.id.mapview);
        initialMap();


        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (fgsys == null)
                    return;

                switch (msg.what) {
                    case INITIALIZE:
                        if (conRec == null) {
                            Log.d("TAG!", "conRec is null!");
                            conRec = new ConnectivityReceiver();
                            registerReceiver(conRec, new IntentFilter(
                                    "android.net.conn.CONNECTIVITY_CHANGE"));

                            //TODO Change to Google Map
//                            final MyLocationOverlay lo = fgsys.getFGMapManager()
//                                    .getMyLocationOverlay();
//                            lo.runOnFirstFix(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Log.d("TAG!", "RUN!");
//                                    fgsys.getFGGPSManager().regenerateProvider();
//                                    handler.sendEmptyMessage(LOCATION_INITIALIZE);
//                                }
//                            });
//                            handler.sendEmptyMessage(LOCATION_INITIALIZE);
//
//                            fgsys.getFGMapManager().getMyLocationOverlay()
//                                .enableMyLocation();
                        }

//                        fgsys.getFGMapManager().checkGPS();
                        break;
                    case LOCATION_INITIALIZE:
                        GeoPoint point = fgsys.getFGGPSManager()
                                .getGeoPointCurrent();
                        if (point == null)
                            point = FinalValue.GEOPOINT_VICTORY;
//                        fgsys.getFGMapManager().getMapController().setCenter(point);

                        break;
                    case UPDATE_WIFI_ICON:
                        wifi_item.setIcon(msg.arg1);
                        wifi_item.setTitle(msg.obj.toString());
                        break;
                    case UPDATE_GPS_ICON:
                        gps_item.setIcon(msg.arg1);
                        gps_item.setTitle(msg.obj.toString());
                        break;
                }
            }
        };

    }
    private void initialMap(){
        Context ctx = this.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        MapView mapView = (MapView) findViewById(R.id.mapview);

        mapView.getController().setZoom(18.0);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET
        });
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mapView.setMultiTouchControls(true);

        CompassOverlay compassOverlay = new CompassOverlay(getApplicationContext(),  mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);
    }
    private void getCurrentLocation(){

            final MyLocationNewOverlay mLocation = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), mapView);
            mLocation.enableMyLocation();
            mLocation.enableFollowLocation();
            mapView.getOverlays().add(mLocation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mLocation != null){
                            if(mLocation.isFollowLocationEnabled()){
                                mLocation.disableFollowLocation();
                            }
                        }
                        IMapController controller = mapView.getController();
                        controller.animateTo(mLocation.getMyLocation());
                    }
                }, 3000);

    }
    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
    public static String getPictureDir() {
        return PICTURE_PATH;
    }

    public static String getTempDir() {
        return TEMP_PATH;
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadStart();

    }
    private void setCenterHome(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(item != null){
                    {
                        if(item.getPoint().getLatitude() != 0 && item.getPoint().getLongitude() !=0){
                            fgsys.getFGMapManager().getMapController().setCenter(item.getPoint());
                        }
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"ไม่พบตำแหน่งบ้าน hcode:"+hcode+" โปรแกรมจะมายังตำแหน่งปัจจุบัน",Toast.LENGTH_LONG).show();
                    getCurrentLocation();
                }
            }
        }, 3000);

    }
    private void LoadStart(){
        SharedPreferences prefsFamilyTree = getSharedPreferences(
                FamilyTree.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefsFamilyTree.edit();
        editor.putBoolean(FamilyTree.PREFS_FIRST, true);
        editor.putInt(FamilyTree.PREFS_FOCUS, 1);
        editor.commit();

        FamilyFolderCollector ffc = (FamilyFolderCollector) getApplicationContext();

        ffc.mFamilys = new HashMap<Integer, Family>();
        if (fgsys == null) {
            Log.i("TAG!", "First time NULL!!");
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    fgsys = new FGSystemManager(FGActivity.this);
                    Log.d("TAG!", "Completed This Thread!");
                    searchHelper("p.hcode="+hcode);
                }
            };
            new GeneralAsyncTask(this, null, handler, INITIALIZE, FAILED)
                    .execute(r, null);
//            if(item==null){
//                getCurrentLocation();
//            }
        } else if (!fgsys.getFGActivity().equals(this)) {
            Log.i("TAG!", "Not equal!");
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    fgsys.setFGActivity(FGActivity.this);

                }
            };
            new GeneralAsyncTask(this, null, handler, INITIALIZE, FAILED)
                    .execute(r, null);
        } else {
            Log.i("TAG!", "Normally functional");
            fgsys.getFGMapManager().checkGPS();
        }
    }
    @Override
    public boolean onSearchRequested() {
        Dialog dia = fgsys.getFGDialogManager().getDialogSearchHouseMarker();
        dia.show();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (conRec != null) {
            registerReceiver(conRec, new IntentFilter(
                "android.net.conn.CONNECTIVITY_CHANGE"));
//           this.recreate();
        }
    }

    // @Override
    // public void onBackPressed() {
    // //super.onBackPressed();
    // Intent intent = new Intent(FGActivity.this, MainPage.class);
    // intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    // startActivityForResult(intent, 1);
    // }

    public static Bitmap getPicture(MARKER_TYPE type, String filename) {
        if (type == null || filename == null)
            return null;

        String path = FGActivity.PICTURE_PATH + type + "/" + filename + ".jpg";

        return BitmapFactory.decodeFile(path);
    }

    public static Bitmap getPictureThumb(MARKER_TYPE type, String filename) {
        if (type == null || filename == null)
            return null;

        return BitmapFactory.decodeFile(FGActivity.PICTURE_PATH + type + "/"
            + filename + "_thumb.jpg");
    }

    public static String getPicturePath(MARKER_TYPE type, String filename) {
        if (type == null || filename == null)
            return null;

        return (FGActivity.PICTURE_PATH + type + "/" + filename + ".jpg");
    }

    public static boolean removePicture(MARKER_TYPE type, String filename) {
        File file1 = new File(FGActivity.PICTURE_PATH + type + "/" + filename
            + ".jpg");
        File file2 = new File(FGActivity.PICTURE_PATH + type + "/" + filename
            + "_thumb.jpg");
        return file1.delete() && file2.delete();
    }

    public Handler getHandler() {
        return handler;
    }

    public void startFamilyTree(int hcode, Spot spot) {

        Intent intent = new Intent(Action.MAIN);
        intent.addCategory(Category.HOUSE);
        intent.setData(ContentUris.withAppendedId(House.CONTENT_URI, hcode));
        intent.putExtra("hcode", hcode);
        intent.putExtra("doubleLatitude", spot.getDoubleLat());
        intent.putExtra("doubleLongitude", spot.getDoubleLong());

        // SharedPreferences house = getSharedPreferences("house", 0);
        // SharedPreferences.Editor editor = house.edit();
        // editor.putInt("hcode", hcode);
        // editor.commit();

        startActivity(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAG!", "Map paused");
        if (fgsys != null) {
            Log.d("TAG!", "Map paused Passed!");
            fgsys.getFGMapManager().clearCahce();
            //fgsys.getFGMapManager().getMyLocationOverlay().disableMyLocation();
        }
        if (conRec != null)
            unregisterReceiver(conRec);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FinalValue.FILTER_REQUEST) {
            SharedPreferences sh = this.getSharedPreferences(
                PreferenceFilter.FILE_XML, MODE_PRIVATE);

            // Check if whether this function is open by user
            new ProcessingFilterAsyncTask(this, sh).execute("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        wifi_item = menu.findItem(R.id.menu_wifi_status);
        gps_item = menu.findItem(R.id.menu_gps_status);

        int mapStyle = FGMapManager.getCurrentMapStyle();
        int menuID = -1;
        switch (mapStyle) {
            case FinalValue.INT_SATELLITE_OVERLAY:
                menuID = R.id.menu_sat_path;
                break;
            case FinalValue.INT_GOOGLE_MAPS:
                menuID = R.id.menu_normal;
                break;
            default:
                menuID = R.id.menu_sat;
        }
        menu.findItem(menuID).setChecked(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle()!=null){
            Log.d("TAG!", item.getTitle().toString());
        }

        switch (item.getItemId()) {
            case R.id.menu_center:
                Location location = fgsys.getFGGPSManager().getLastKnownLocation();
                GeoPoint geoPointCurrent;
                MyLocationNewOverlay mLocation = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), mapView);
                Log.d("Tag(menu)","menu_center");
                //TODO Change to Google Map
                if (mLocation.getLastFix() != null){
                    fgsys.getFGMapManager().getMapController()
                            .setCenter(mLocation.getMyLocation());
                } else if (location != null) {
                    geoPointCurrent = new GeoPoint(location.getLatitude(),
                            location.getLongitude());
                    fgsys.getFGMapManager().getMapController()
                            .setCenter(geoPointCurrent);
                }
                return true;
            case R.id.menu_filter:
                this.startActivityForResult(
                    new Intent(this, PreferenceFilter.class),
                    FinalValue.FILTER_REQUEST);
                return true;
            case R.id.menu_search:
                Dialog dialogSearchHouseMarker = fgsys.getFGDialogManager()
                    .getDialogSearchHouseMarker();
                dialogSearchHouseMarker.show();
                return true;
            case R.id.menu_wifi_status:
                this.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                return true;
            case R.id.menu_gps_status:
                this.startActivity(new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                return true;
            case R.id.menu_sat:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fgsys.getFGMapManager().setMapStyle(FinalValue.INT_SATELLITE);

                    }
                });
//                fgsys.getFGMapManager().setMapStyle(FinalValue.INT_SATELLITE);
                item.setChecked(true);
                return true;
            case R.id.menu_sat_path:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fgsys.getFGMapManager().setMapStyle(
                                FinalValue.INT_SATELLITE_OVERLAY);
                    }
                });

                item.setChecked(true);
                return true;
            case R.id.menu_normal:
                fgsys.getFGMapManager().setMapStyle(FinalValue.INT_GOOGLE_MAPS);
                item.setChecked(true);
                return true;
            case android.R.id.home:
                startHomeActivity();
        }
        return super.onOptionsItemSelected(item);
    }


    public void goToHouse(String hcode) {
        String[] projection = new String[]{House._ID, House.X_GIS, House.Y_GIS};
        String selection = "hcode=?";
        Cursor c = getContentResolver().query(House.CONTENT_URI, projection, selection,
            new String[]{hcode}, House._ID);
        if(c.getCount()>0) {
            if (c.moveToFirst()) {
                double x = c.getDouble(1);
                double y = c.getDouble(2);
                if (x > 0 || y > 0) {
//                    Toast.makeText(this, "x=" + x + " y=" + y, Toast.LENGTH_SHORT).show();
                    GeoPoint geo = new GeoPoint(x, y);
                    mapView.getController().setCenter(geo);
                }
                else {
                    Toast.makeText(this, "ไม่พบตำแหน่งของบ้าน hcode:"+hcode+" โปรแกรมจะไปยังตำแหน่งปัจจุบัน", Toast.LENGTH_SHORT).show();
                    getCurrentLocation();
                }
            }
        }
        else {
            Toast.makeText(this, "ไม่พบตำแหน่งของบ้าน hcode:"+hcode+" โปรแกรมจะไปยังตำแหน่งปัจจุบัน", Toast.LENGTH_SHORT).show();
            getCurrentLocation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TAG!", "Map Destroyed");
        if (fgsys != null) {
            fgsys.close();
            fgsys = null;
        }


        handler = null;
        wifi_item = null;
        gps_item = null;
        conRec = null;

    }
}
