package th.in.ffc.map.map;

import static android.app.PendingIntent.getActivity;

//import static androidx.appcompat.graphics.drawable.DrawableContainer.Api21Impl.getResources;
import static th.in.ffc.map.FGActivity.fgsys;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;

import org.osmdroid.api.IMapController;
//import org.osmdroid.google.wrapper.MyLocationOverlay;
//import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;

import th.in.ffc.R;
import th.in.ffc.map.FGActivity;
import th.in.ffc.map.MapFragment;
import th.in.ffc.map.ResourceProxyImpl;
import th.in.ffc.map.UserResourceProxyImpl;
import th.in.ffc.map.database.DatabaseManager;
import th.in.ffc.map.overlay.FGOverlay;
import th.in.ffc.map.overlay.ItemGestureListener;
import th.in.ffc.map.system.FGSystemManager;
import th.in.ffc.map.value.FinalValue;
import th.in.ffc.map.value.MARKER_TYPE;
import th.in.ffc.map.village.spot.Spot;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class FGMapManager implements OnClickListener {

    private FGSystemManager fgSystemManager;

    private MapView mapView;
    private IMapController mapController;
    private MyLocationNewOverlay mLocation;
    private ItemGestureListener gesture;
    private ItemizedIconOverlay<Spot> markers = null;

    private MapTileProviderBasic tileProvider;

    private OnlineTileSourceBase sourceBase;

    private FGOverlay emptyOverlay;

    private TilesOverlay tilesOverlay;

    private ArrayList<String> sat_url;

    private ArrayList<String> hybrid_url;

    private ArrayList<String> maps_url;

    private static int currentMapStyle = -1;

    private static boolean[] group_check;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private int minAge = 0;
    private int maxAge = 100;
    private boolean isAgeFilterActive = false;
    private Handler uiHandler = new Handler(Looper.getMainLooper());

    public FGMapManager(FGSystemManager fgSystemManager) {
        this.fgSystemManager = fgSystemManager;
        reloadMap();
    }
    public void reloadMap(){
        try {
            group_check = new boolean[MARKER_TYPE.size];
            Arrays.fill(group_check, true);

            FGActivity fgActivity = this.fgSystemManager.getFGActivity();

            // ตรวจสอบว่า Activity และ SupportFragmentManager ไม่เป็น null
            if (fgActivity == null || fgActivity.isFinishing() || fgActivity.isDestroyed()) {
                Log.e("TAG!", "FGActivity is null or finishing/destroyed");
                return;
            }

            if (fgActivity.getSupportFragmentManager() == null) {
                Log.e("TAG!", "FragmentManager is null");
                return;
            }

            MapFragment mf = (MapFragment) fgActivity.getSupportFragmentManager().findFragmentById(R.id.map_fragment_id);

            // ตรวจสอบว่า MapFragment และ View ไม่เป็น null
            if (mf == null) {
                Log.e("TAG!", "MapFragment is null - will retry");
                // ลองใหม่หลังจาก delay
                retryReloadMap(500);
                return;
            }

            View fragmentView = mf.getView();
            if (fragmentView == null) {
                Log.e("TAG!", "MapFragment view is null - will retry");
                // ลองใหม่หลังจาก delay
                retryReloadMap(500);
                return;
            }

            this.mapView = (MapView) fragmentView.findViewById(R.id.mapview);
            if (this.mapView == null) {
                Log.e("TAG!", "MapView is null");
                return;
            }

            this.gesture = new ItemGestureListener(fgSystemManager);

            // ดำเนินการต่อเฉพาะเมื่อ mapView พร้อมแล้ว
            requirePermission();

            // ตรวจสอบก่อนดาวน์โหลดไฟล์
            InputStream target = null;
            try {
                File previous = this.fgSystemManager.getFGActivity().getFileStreamPath("map-loaded");
                String filename_current = downloadFile();

                if (filename_current != null) {
                    File current = this.fgSystemManager.getFGActivity().getFileStreamPath(filename_current);
                    if (previous.exists()) {
                        previous.delete();
                    }
                    current.renameTo(previous);

                    Log.d("TAG!", "Download file is completed");
                    target = new FileInputStream(previous);
                } else if (previous.exists()) {
                    Log.d("TAG!", "Download file is NOT completed, use the previous one");
                    target = new FileInputStream(previous);
                } else {
                    Log.d("TAG!", "No previous file, cannot download, fall back! fall back!");
                    target = this.fgSystemManager.getFGActivity().getAssets().open("maps-fallback");
                }
            } catch (Exception e) {
                Log.e("TAG!", "Error loading map files", e);
                try {
                    target = this.fgSystemManager.getFGActivity().getAssets().open("maps-fallback");
                } catch (IOException ioException) {
                    Log.e("TAG!", "Error loading fallback map", ioException);
                    return;
                }
            }

            this.tokenize(target);

            markers = new ItemizedIconOverlay<Spot>(new ArrayList<Spot>(), this.gesture, this.mapView.getContext());
            this.emptyOverlay = new FGOverlay(fgSystemManager);
            this.mapController = this.mapView.getController();

            // ตรวจสอบก่อนรันใน UI Thread
            if (fgActivity != null && !fgActivity.isFinishing() && !fgActivity.isDestroyed()) {
                fgActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            setMapStyle(FinalValue.INT_SATELLITE);
                        } catch (Exception e) {
                            Log.e("TAG!", "Error setting map style", e);
                        }
                    }
                });
            }

            this.mapView.setUseDataConnection(true);
            this.initialButtonZoomControl();
            this.initializeCurrentLocation();
            checkGPS();

        } catch (Exception e) {
            Log.e("TAG!", "Error in reloadMap", e);
        }
    }

    // เมธอดใหม่สำหรับลองใหม่
    private void retryReloadMap(int delayMs) {
        FGActivity fgActivity = this.fgSystemManager.getFGActivity();
        if (fgActivity != null && !fgActivity.isFinishing() && !fgActivity.isDestroyed()) {
            fgActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reloadMap(); // เรียกตัวเองใหม่
                        }
                    }, delayMs);
                }
            });
        }
    }

    public void checkGPS() {

        LocationManager lm = fgSystemManager.getFGGPSManager().getLocationManager();

        Resources res = fgSystemManager.getFGActivity().getResources();
        String str = res.getString(R.string.gps_text);

        Message msg = new Message();
        msg.what = FGActivity.UPDATE_GPS_ICON;
        if (lm.isProviderEnabled("gps")) {
            msg.arg1 = R.drawable.gps1;
            msg.obj = str + "GPS";
        } else if (mapView.useDataConnection())
            if (lm.isProviderEnabled("network")) {
                msg.arg1 = R.drawable.gps3;
                msg.obj = str + "AGPS";
            } else {
                msg.arg1 = R.drawable.gps2;
                msg.obj = str + "Off";
            }
        else {
            msg.arg1 = R.drawable.gps2;
            msg.obj = str + "Off";
        }

        fgSystemManager.getFGActivity().getHandler().sendMessage(msg);
    }

    public void setMapStyle(int newStyle) {

        if (currentMapStyle == newStyle)
            return;

        this.clearCahce();
        List<Overlay> stackOverlay = this.mapView.getOverlays();
//        stackOverlay.clear();
        Context ctx = this.getMapView().getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        switch (newStyle) {
            case FinalValue.INT_SATELLITE:
                mapView.setTileSource(GoogleSat);
                break;
            case FinalValue.INT_SATELLITE_OVERLAY:
                mapView.setTileSource(TileSourceFactory.MAPNIK);
                break;
            case FinalValue.INT_GOOGLE_MAPS:
                mapView.setTileSource(GoogleRoads);
                break;
        }

        stackOverlay.add(emptyOverlay);
        stackOverlay.add(markers);
        // TODO Change to Google Map
/*        if (mLocation != null)
            stackOverlay.add(mLocation);*/

        currentMapStyle = newStyle;
    }
    // Google satellite
    public static final OnlineTileSourceBase GoogleSat = new XYTileSource("Google-Sat",
            0, 19, 512, ".png", new String[]{
            "https://mt0.google.com",
            "https://mt1.google.com",
            "https://mt2.google.com",
            "https://mt3.google.com",
    }) {
        @Override
        public String getTileURLString(long pMapTileIndex) {
            return getBaseUrl() + "/vt/lyrs=s&scale=2&hl=zh-CN&gl=CN&src=app&x=" + MapTileIndex.getX(pMapTileIndex) + "&y=" + MapTileIndex.getY(pMapTileIndex) + "&z=" + MapTileIndex.getZoom(pMapTileIndex);
        }
    };
    public static final OnlineTileSourceBase GoogleRoads = new XYTileSource("Google-Roads",
            0, 18, 512, ".png", new String[]{
            "https://mt0.google.com",
            "https://mt1.google.com",
            "https://mt2.google.com",
            "https://mt3.google.com",
    }) {
        @Override
        public String getTileURLString(long pMapTileIndex) {
            return getBaseUrl() + "/vt/lyrs=m&scale=2&hl=zh-CN&gl=CN&src=app&x=" + MapTileIndex.getX(pMapTileIndex) + "&y=" + MapTileIndex.getY(pMapTileIndex) + "&z=" + MapTileIndex.getZoom(pMapTileIndex);
        }
    };
    public static int getCurrentMapStyle() {
        return currentMapStyle;
    }
    private void requirePermission(){
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET
        });
    }
//    private OnlineTileSourceBase initializeSatellite() {
//        if (sourceBase == null || (!sourceBase.name().equals("Google_Satellite"))) {
//            sourceBase = new OnlineTileSourceBase("Google_Satellite", 1, 19, 256, ".jpg",
//                    sat_url.toArray(new String[sat_url.size()])) {
//                @Override
//                public String getTileURLString(long l) {
//                    return null;
//                }
//            };
//        }
//        return sourceBase;
//    }
    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getMapView().getContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(fgSystemManager.getFGActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
//    private OnlineTileSourceBase initializeGoogleMaps() {
//       if (sourceBase == null || (!sourceBase.name().equals("Google_Maps"))) {
//           sourceBase = new OnlineTileSourceBase("Google_Maps", 1, 20, 256, ".jpg",
//                   maps_url.toArray(new String[maps_url.size()])) {
//               @Override
//               public String getTileURLString(long pMapTileIndex) {
//                   return null;
//               }
////               @Override
////               public String getTileURLString(final MapTile aTile) {
////                   return getBaseUrl() + "x=" + aTile.getX() + "&y=" + aTile.getY() + "&z=" + aTile.getZoomLevel();
////               }
//           };
//       }
//        return sourceBase;
//    }

//    private TilesOverlay initializeGoogleOverlay() {
//        if (tileProvider == null) {
//            tileProvider = new MapTileProviderBasic(this.fgSystemManager.getFGActivity());
//           final ITileSource tileSource = new OnlineTileSourceBase("Google_Hybrid", 1, 19, 256, ".png",
//                   hybrid_url.toArray(new String[hybrid_url.size()])) {
//               @Override
//               public String getTileURLString(long pMapTileIndex) {
//                   return null;
//               }
////               @Override
////               public String getTileURLString(final MapTile aTile) {
////
////                   return getBaseUrl() + "x=" + aTile.getX() + "&y=" + aTile.getY() + "&z=" + aTile.getZoomLevel();
////               }
//           };
//           tileProvider.setTileSource(tileSource);
//        }
//        if (tilesOverlay == null) {
//            tilesOverlay = new TilesOverlay(tileProvider, this.fgSystemManager.getFGActivity());
//            tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
//        }
//        return tilesOverlay;
//    }

    // private void initialImageButtonMenu() {
    // FGActivity fgActivity = this.fgSystemManager.getFGActivity();
    //
    // this.initialImageButtonSearch(fgActivity);
    // this.initialImageButtonGPS(fgActivity);
    // this.initialImageButtonNetwork(fgActivity);
    // this.initialImageButtonMyLocation(fgActivity);
    // this.initializeImageButtonFilter(fgActivity);
    // this.initialImageButtonMapStyle(fgActivity);
    // }

    // private void initialImageButtonUser(FGActivity fgActivity){
    // ImageButton imageButtonUser =
    // (ImageButton)fgActivity.findViewById(R.id.imagebutton_center);
    // imageButtonUser.setOnClickListener(new View.OnClickListener() {
    // @Override
    // public void onClick(View v) {
    //
    // }
    // });
    // }

    // private void initialImageButtonMyLocation(FGActivity fgActivity) {
    // ImageButton imgMyLocation = (ImageButton)
    // fgActivity.findViewById(R.id.imagebutton_center);
    // imgMyLocation.setOnClickListener(this);
    // }
    //
    // private void initialImageButtonSearch(FGActivity fgActivity) {
    // ImageButton imageButtonSearch = (ImageButton)
    // fgActivity.findViewById(R.id.imagebutton_search);
    // imageButtonSearch.setOnClickListener(this);
    // }
    //
    // private void initializeImageButtonFilter(FGActivity fgActivity) {
    // ImageButton imageButtonFilter = (ImageButton)
    // fgActivity.findViewById(R.id.imagebutton_filter);
    // imageButtonFilter.setOnClickListener(this);
    // }
    //
    // private void initialImageButtonMapStyle(FGActivity fgActivity) {
    // ImageButton imageButtonMapStyle = (ImageButton)
    // fgActivity.findViewById(R.id.imagebutton_mapstyle);
    // imageButtonMapStyle.setOnClickListener(this);
    // }

    // private void initialImageButtonBack(final FGActivity fgActivity){
    // ImageButton imageButtonBack =
    // (ImageButton)fgActivity.findViewById(R.id.imagebutton_back);
    // imageButtonBack.setOnClickListener(new View.OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // Intent intent = new Intent(fgActivity, MainPage.class);
    // intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    // fgActivity.startActivity(intent);
    // }
    // });
    // }

    // private void initialImageButtonGPS(final FGActivity fgActivity) {
    // ImageButton imageGPS = (ImageButton)
    // fgActivity.findViewById(R.id.gps_status);
    // imageGPS.setOnClickListener(this);
    // }
    //
    // private void initialImageButtonNetwork(final FGActivity fgActivity) {
    // ImageButton wifi_button = (ImageButton)
    // fgActivity.findViewById(R.id.wifi_status);
    // wifi_button.setOnClickListener(this);
    // }

    private void initializeCurrentLocation() {
        Context context = fgSystemManager.getFGActivity().getApplicationContext();


//       mLocation = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);
//        mapView.getOverlays().add(mLocation);
//         mLocation.enableMyLocation();
//         mLocation.enableFollowLocation();
//         mapView.getController().setCenter(mLocation.getMyLocation());
//         mLocation.runOnFirstFix(new Runnable() {
//         @Override
//         public void run() {
////         FGMapManager.this.fgSystemManager.getFGGPSManager().regenerateGeoPosition();
////             mapView.getController().setCenter(mLocation.getMyLocation());
//         }
//         });

//         this.fgSystemManager.getFGGPSManager().getLastKnownLocation();
//         GeoPoint geoPointCurrent;
//         if (mLocation != null) {
//            geoPointCurrent = new GeoPoint(mLocation.getMyLocation().getLatitude(),
//                    mLocation.getMyLocation().getLongitude());
//         } else {
//            geoPointCurrent = FinalValue.GEOPOINT_VICTORY;
//         }
//         if (mLocation.getMyLocation() != null) {
//             this.mapController.setCenter(mLocation.getMyLocation());
//         }
//         else {
//             this.mapController.setCenter(geoPointCurrent);
//         }
    }

    private void initialButtonZoomControl() {
//        this.mapController.setZoom(18);

        FGActivity fgActivity = this.fgSystemManager.getFGActivity();
        MapFragment mf = (MapFragment) fgActivity.getSupportFragmentManager().findFragmentById(R.id.map_fragment_id);
        if(mf==null || mf.getView()==null) return;
        ImageButton imageButtonZoomout = (ImageButton) mf.getView().findViewById(R.id.imagebutton_zoomout);
        imageButtonZoomout.setOnClickListener(this);

        ImageButton imageButtonZoomin = (ImageButton) mf.getView().findViewById(R.id.imagebutton_zoomin);
        imageButtonZoomin.setOnClickListener(this);

        ImageButton filterGroup = (ImageButton) mf.getView().findViewById(R.id.filter_group_button);
        filterGroup.setOnClickListener(this);
    }

    public MapView getMapView() {
        return this.mapView;
    }

    public ItemizedIconOverlay<Spot> getMarker() {
        return markers;
    }

    public IMapController getMapController() {
        return this.mapController;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // case R.id.imagebutton_center:
            // Location location =
            // fgSystemManager.getFGGPSManager().getLastKnownLocation();
            // GeoPoint geoPointCurrent;
            // if (mLocation.getMyLocation() != null)
            // fgSystemManager.getFGMapManager().getMapController().animateTo(mLocation.getMyLocation());
            // else if (location != null) {
            // geoPointCurrent = new GeoPoint(location.getLatitude(),
            // location.getLongitude());
            // fgSystemManager.getFGMapManager().getMapController().animateTo(geoPointCurrent);
            // }
            //
            // // For testing-purpose only
            //
            // // Spot[] set =
            // this.fgSystemManager.getFGDatabaseManager().getMarked().values().toArray(new
            // Spot[this.fgSystemManager.getFGDatabaseManager().getMarked().size()]);
            // // for (Spot spot : set) {
            // // this.fgSystemManager.removeMarkerOnMap(spot);
            // // }
            //
            // // -------------------------
            // break;
            // case R.id.imagebutton_filter:
            // this.fgSystemManager.getFGActivity().startActivityForResult(new
            // Intent(this.fgSystemManager.getFGActivity(), PreferenceFilter.class),
            // FinalValue.FILTER_REQUEST);
            // break;
            // case R.id.imagebutton_search:
            // DialogSearchHouseMarker dialogSearchHouseMarker =
            // this.fgSystemManager.getFGDialogManager().getDialogSearchHouseMarker();
            // dialogSearchHouseMarker.show();
            // break;
            case R.id.imagebutton_zoomout:
                this.mapController.zoomOut();
                break;
            case R.id.imagebutton_zoomin:
                this.mapController.zoomIn();
                break;
            case R.id.filter_group_button:
                showAgeFilterDialog();
                break;
            // case R.id.wifi_status:
            // this.fgSystemManager.getFGActivity().startActivity(new
            // Intent(Settings.ACTION_WIFI_SETTINGS));
            // break;
            // case R.id.gps_status:
            // this.fgSystemManager.getFGActivity().startActivity(new
            // Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            // break;
            // case R.id.imagebutton_mapstyle:
            // DialogMapStyle dialogMapStyle =
            // FGMapManager.this.fgSystemManager.getFGDialogManager().getDialogMapStyle();
            // dialogMapStyle.show();
            // break;
        }
    }
    private void showAgeFilterDialog() {
        // สร้างรายการกลุ่มที่ต้องการให้เลือก
        final String[] ageRanges = {"ทั้งหมด", "15-34 ปี", "35-50 ปี", "51-60 ปี", "มากกว่า 60 ปี"};

        // กำหนดค่าที่เลือกอยู่ปัจจุบัน (0 หมายถึงตัวแรก)
        int currentSelection = 0; // ค่าเริ่มต้น
        if (fgsys != null) {
            currentSelection = fgsys.getFGDatabaseManager().getCurrentAgeRangeSelection();
        }

        // สร้าง dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(fgSystemManager.getFGActivity());
        builder.setTitle("กรองตามช่วงอายุ")
                .setSingleChoiceItems(ageRanges, currentSelection, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int minAge = 0;
                        int maxAge = 100;
                        boolean isActive = false;

                        switch (which) {
                            case 0: // ทั้งหมด
                                isActive = false;
                                break;
                            case 1: // 15-34 ปี
                                isActive = true;
                                minAge = 15;
                                maxAge = 34;
                                break;
                            case 2: // 35-50 ปี
                                isActive = true;
                                minAge = 35;
                                maxAge = 50;
                                break;
                            case 3: // 51-60 ปี
                                isActive = true;
                                minAge = 51;
                                maxAge = 60;
                                break;
                            case 4: // มากกว่า 60 ปี
                                isActive = true;
                                minAge = 61;
                                maxAge = 200;
                                break;
                        }

                        // เรียกใช้ฟังก์ชันกรองข้อมูลจาก FGDatabaseManager
                        if (fgsys != null) {
//                            if(isActive) {
                                fgsys.getFGDatabaseManager().filterHousesByAgeRange(minAge, maxAge, isActive,which);
//                            } else {
//                                fgsys.getFGDatabaseManager().initializeSpot();
//                            }

                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void filterHousesByAgeRange() {
        if (fgsys == null) return;

        // แสดง progress
        //setSupportProgressBarIndeterminateVisibility(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // ลบ markers เดิมบนแผนที่
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // ลบ markers เดิม (ส่วนนี้ขึ้นอยู่กับว่าคุณเก็บ markers ไว้ที่ไหน)
//                        fgsys.getFGMapManager().clearMarkers();
                    }
                });

                // ถ้าไม่ได้กรอง แสดงทั้งหมด
                if (!isAgeFilterActive) {
                    // แสดงบ้านทั้งหมด
//                    showAllHouses();
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            setSupportProgressBarIndeterminateVisibility(false);
                            Toast.makeText(getMapView().getContext(), "แสดงบ้านทั้งหมด", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                // เริ่มการ query ข้อมูลบ้านตามช่วงอายุ
                DatabaseManager db = fgsys.getFGDatabaseManager().getDatabaseManager();

                if (db.openDatabase()) {
                    // สร้าง query เพื่อดึงบ้านที่มีคนในช่วงอายุที่ต้องการ
                    String query = "SELECT DISTINCT h.hcode, h.xgis, h.ygis " +
                            "FROM house h " +
                            "JOIN person p ON h.hcode = p.hcode " +
                            "WHERE " +
                            "((strftime('%Y', 'now') - strftime('%Y', p.birth)) - " +
                            "(strftime('%m-%d', 'now') < strftime('%m-%d', p.birth))) " +
                            "BETWEEN " + minAge + " AND " + maxAge + " " +
                            "AND h.xgis IS NOT NULL AND h.xgis != '0' AND h.xgis != '0.0' " +
                            "AND h.ygis IS NOT NULL AND h.ygis != '0' AND h.ygis != '0.0'";

                    Cursor cursor = db.getCursor(query);

                    final ArrayList<GeoPoint> filteredHouses = new ArrayList<>();

                    if (cursor.moveToFirst()) {
                        do {
                            String hcode = cursor.getString(0);
                            double x = cursor.getDouble(1);
                            double y = cursor.getDouble(2);

                            if (x > 0 && y > 0) {
                                filteredHouses.add(new GeoPoint(x, y));
                            }
                        } while (cursor.moveToNext());
                    }

                    cursor.close();
                    db.closeDatabase();

                    // แสดงบ้านที่กรองแล้วบนแผนที่
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            for (GeoPoint point : filteredHouses) {

                                // ตรวจสอบค่าพิกัดก่อนสร้าง Marker
                                double lat = point.getLatitude();
                                double lon = point.getLongitude();

                                // ตรวจสอบความถูกต้องของค่าละติจูด
                                if (lat < -85.05 || lat > 85.05) {
                                    Log.e("MAP", "Invalid latitude: " + lat + " for point, skipping...");
                                    continue; // ข้ามจุดที่ไม่ถูกต้อง
                                }
                                // เพิ่ม marker ของบ้านที่กรองแล้ว
                                Marker marker = new Marker(mapView);
                                marker.setPosition(new GeoPoint(lat, lon));
                                marker.setIcon(mapView.getResources().getDrawable(R.drawable.house_green));
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                mapView.getOverlays().add(marker);
                            }

                            mapView.invalidate();
//                            setSupportProgressBarIndeterminateVisibility(false);
                            Toast.makeText(getMapView().getContext(),
                                    "พบบ้านที่มีคนอายุ " + minAge + "-" + maxAge + " ปี จำนวน " +
                                            filteredHouses.size() + " หลัง",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            setSupportProgressBarIndeterminateVisibility(false);
                            Toast.makeText(getMapView().getContext(),
                                    "ไม่สามารถเชื่อมต่อฐานข้อมูลได้",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
    private void filterByGroup(String group) {
        // จัดการกับการกรองตามกลุ่มที่เลือก
        // ตัวอย่างเช่น ส่งข้อมูลไปที่ Activity หลักเพื่อกรองข้อมูล
        if (getMapView().getContext() instanceof FGActivity) {
            FGActivity activity = (FGActivity) getMapView().getContext();
            // เรียกเมธอดที่เกี่ยวข้องกับการกรองข้อมูล
            // activity.filterMarkersByGroup(group);

            // หรือถ้ายังไม่มีเมธอดอยู่ คุณสามารถแสดง Toast เพื่อทดสอบก่อนได้
            Toast.makeText(getMapView().getContext(), "เลือกกลุ่ม: " + group, Toast.LENGTH_SHORT).show();
        }
    }

    public MyLocationNewOverlay getMyLocationOverlay() {
        return mLocation;
    }

    public ItemGestureListener getGesture() {
        return gesture;
    }

    private void tokenize(InputStream input) {
        sat_url = new ArrayList<String>();
        hybrid_url = new ArrayList<String>();
        maps_url = new ArrayList<String>();

        if (input == null) {
            // If everything fail, Hard-coded
            Log.d("TAG!", "Everything fail, hard-coded");

            sat_url.add("https://khms0.googleapis.com/kh?v=113&hl=th&");
            sat_url.add("https://khms1.googleapis.com/kh?v=113&hl=th&");

            hybrid_url.add("https://mts0.googleapis.com/vt?lyrs=h@177000000&src=api&hl=th&");
            hybrid_url.add("https://mts1.googleapis.com/vt?lyrs=h@177000000&src=api&hl=th&");

            maps_url.add("https://mts0.googleapis.com/vt?lyrs=m@177000000&src=api&hl=th&");
            maps_url.add("https://mts1.googleapis.com/vt?lyrs=m@177000000&src=api&hl=th&");
            maps_url.add("https://mts0.googleapis.com/mapslt?hl=th&");
            maps_url.add("https://mts1.googleapis.com/mapslt?hl=th&");

            return;
        }

        double version = -1;

        Scanner sc = null;

        sc = new Scanner(input);

        while (sc.hasNextLine()) {
            String str = sc.nextLine();
            if (str.indexOf("googleapis") != -1) {
                String token[] = str.split("\\\"");
                for (int i = 0; i < token.length; i++) {
                    if (token[i].indexOf("http") != -1) {

                        if (token[i].indexOf("h@") != -1) {
                            token[i] = normalizeString(token[i]);
                            hybrid_url.add(token[i]);
                        }

                        if (token[i].indexOf("m@") != -1 || token[i].indexOf("mapslt?") != -1) {
                            token[i] = normalizeString(token[i]);
                            maps_url.add(token[i]);
                        }

                        if (token[i].indexOf("khm") != -1 && token[i].indexOf("kh?") != -1) {
                            token[i] = normalizeString(token[i]);
                            Uri uri = Uri.parse(token[i]);

                            double tmp = -2;
                            try {
                                tmp = Double.parseDouble(uri.getQueryParameter("v"));
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                            if (tmp >= version) {
                                sat_url.add(token[i]);
                                version = tmp;
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    private String normalizeString(String str) {
        return str.trim().replaceAll("\\\\u0026", "&").replaceAll("en-US", "th");
    }

    private String downloadFile() {
        Log.d("TAG!", "Begin Downloading!");
        // AsyncTaskDownloadFile down = new
        // AsyncTaskDownloadFile(this.fgSystemManager.getFGActivity());
        // down.execute("https://maps.googleapis.com/maps/api/js?sensor=false");
        String result = null;
        // try {
        result = downloadManager("https://maps.googleapis.com/maps/api/js?sensor=false");
        // } catch (InterruptedException e1) {
        // e1.printStackTrace();
        // } catch (ExecutionException e1) {
        // e1.printStackTrace();
        // } catch (TimeoutException e) {
        // Log.d("TAG!", "Timeout");
        // e.printStackTrace();
        // }
        Log.d("TAG!", "*" + result + "*");
        return result;
    }

    private String downloadManager(String params) {
        String file_name = null;
        try {

            Log.d("TAG!", "Initializing Downloading!");

            URL url = new URL(params);
            Log.d("TAG!", "URL Connection!");
            URLConnection connection = url.openConnection();
            Log.d("TAG!", "Begin Connect!");
            connection.connect();

            // int fileLength = connection.getContentLength();

            Log.d("TAG!", "Begin Real Donwlaoding!");

            file_name = "temp_file";

            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = fgSystemManager.getFGActivity().openFileOutput(file_name, Context.MODE_PRIVATE);

            byte data[] = new byte[1024];
            // long total = 0;
            int count;

            while ((count = input.read(data)) != -1) {
                // total += count;
                Log.d("TAG!", "Progress Updated!!");
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file_name;
    }

    public MapTileProviderBasic getOverlayTileProvider() {
        return tileProvider;
    }

    public void clearCahce() {
        this.mapView.getTileProvider().clearTileCache();
        if (tileProvider != null)
            tileProvider.clearTileCache();
    }

    public void close() {
        currentMapStyle = -1;
    }
}
