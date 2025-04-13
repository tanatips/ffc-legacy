package th.in.ffc.app.form.screening;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import th.in.ffc.R;
import th.in.ffc.api.nhso.StatusTrackResponse;
import th.in.ffc.app.form.screening.adapter.ClaimListAdapter;
import th.in.ffc.app.form.screening.dao.ClaimInfoDao;
import th.in.ffc.app.form.screening.model.ClaimInfo;

public class ClaimListActivity extends AppCompatActivity implements ClaimListAdapter.OnClaimClickListener {

    private EditText etPatientName;
    private Spinner spinnerPatientGroup;

    private Button btnDateFrom, btnDateTo;
    private Spinner spinnerServiceType;
    private Spinner spinnerStatus;
    private Button btnSearch;
    private Button btnToggleSearch;
    private Button btnRefresh;
    private CardView cardSearchFilters;
    private RecyclerView recyclerViewClaims;
    private TextView tvNoResults;
    private TextView tvResultCount;

    private ClaimListAdapter claimListAdapter;
    private List<ClaimInfo> claimList;
    private ClaimInfoDao claimInfoDao;

    private String selectedFromDate = "";
    private String selectedToDate = "";
    private Calendar calendar;
    private SimpleDateFormat displayDateFormat;
    private SimpleDateFormat storageDateFormat;
    private boolean isSearchVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_list);
        // Set action bar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("รายการเบิก");
        }

        // Initialize date format
        calendar = Calendar.getInstance();
        displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        storageDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Initialize views
        initViews();

        // เพิ่มการตั้งค่าเริ่มต้นให้กับปุ่มวันที่
        btnDateFrom.setText("DD/MM/YYYY");
        btnDateTo.setText("DD/MM/YYYY");

        // Initialize DAO
        claimInfoDao = new ClaimInfoDao(this);

        // Initialize claim list
        claimList = new ArrayList<>();

        // Set up RecyclerView
        setupRecyclerView();

        // Set up spinners
        setupSpinners();

        // Set up date pickers
        setupDatePickers();

        // Set up action buttons
        setupButtons();

        // Load initial data
        loadClaimData();
    }

    /**
     * Initialize all views
     */
    private void initViews() {
        etPatientName = findViewById(R.id.etPatientName);
        spinnerPatientGroup = findViewById(R.id.spinnerPatientGroup);
        btnDateFrom = findViewById(R.id.btnDateFrom);
        btnDateTo = findViewById(R.id.btnDateTo);

        spinnerServiceType = findViewById(R.id.spinnerServiceType);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnSearch = findViewById(R.id.btnSearch);
        btnToggleSearch = findViewById(R.id.btnToggleSearch);
        btnRefresh = findViewById(R.id.btnRefresh);
        cardSearchFilters = findViewById(R.id.cardSearchFilters);
        recyclerViewClaims = findViewById(R.id.recyclerViewClaims);
        tvNoResults = findViewById(R.id.tvNoResults);
        tvResultCount = findViewById(R.id.tvResultCount);
    }

    /**
     * Set up RecyclerView
     */
    private void setupRecyclerView() {
        recyclerViewClaims.setLayoutManager(new LinearLayoutManager(this));
        claimListAdapter = new ClaimListAdapter(this, claimList);
        claimListAdapter.setOnClaimClickListener(this);
        recyclerViewClaims.setAdapter(claimListAdapter);
    }

    /**
     * Set up spinner adapters
     */
    private void setupSpinners() {
        // Patient Group Spinner
        List<String> patientGroups = claimInfoDao.getAvailablePatientGroups();
        ArrayAdapter<String> patientGroupAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, patientGroups);
        patientGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPatientGroup.setAdapter(patientGroupAdapter);

        // Service Type Spinner
        List<String> serviceTypes = claimInfoDao.getAvailableServiceTypes();
        ArrayAdapter<String> serviceTypeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, serviceTypes);
        serviceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServiceType.setAdapter(serviceTypeAdapter);

        // Status Spinner
        List<String> statuses = claimInfoDao.getAvailableStatuses();
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
    }

    /**
     * Set up date picker buttons
     */
    private void setupDatePickers() {
        btnDateFrom.setOnClickListener(v -> {
            showDatePickerDialog(true);
        });

        btnDateTo.setOnClickListener(v -> {
            showDatePickerDialog(false);
        });
    }

    /**
     * Set up action buttons click listeners
     */
    private void setupButtons() {
        // Search button
        btnSearch.setOnClickListener(v -> {
            performSearch();
        });

        // Toggle search panel button
        btnToggleSearch.setOnClickListener(v -> {
            toggleSearchPanel();
        });

        // Refresh button
        btnRefresh.setOnClickListener(v -> {
            // Clear search fields
            etPatientName.setText("");
            spinnerPatientGroup.setSelection(0);
            spinnerServiceType.setSelection(0);
            spinnerStatus.setSelection(0);

            btnDateFrom.setText("DD/MM/YYYY");
            btnDateTo.setText("DD/MM/YYYY");

            selectedFromDate = "";
            selectedToDate = "";

            // Reload all data
            loadClaimData();
        });
    }

    /**
     * Toggle search panel visibility with animation
     */
    private void toggleSearchPanel() {
        if (isSearchVisible) {
            // Hide search panel
            Animation slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
            cardSearchFilters.startAnimation(slideUp);
            cardSearchFilters.setVisibility(View.GONE);
            isSearchVisible = false;
        } else {
            // Show search panel
            cardSearchFilters.setVisibility(View.VISIBLE);
            Animation slideDown = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
            cardSearchFilters.startAnimation(slideDown);
            isSearchVisible = true;
        }
    }

    /**
     * Show date picker dialog
     * @param isFromDate true if selecting from date, false if selecting to date
     */
    private void showDatePickerDialog(final boolean isFromDate) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String displayDate = displayDateFormat.format(calendar.getTime());
            String storageDate = storageDateFormat.format(calendar.getTime());

            if (isFromDate) {
                btnDateFrom.setText(displayDate);
                selectedFromDate = storageDate;
            } else {
                btnDateTo.setText(displayDate);
                selectedToDate = storageDate;
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    /**
     * Load all claim data
     */
    private void loadClaimData() {
        claimList.clear();
        claimList.addAll(claimInfoDao.getAllClaims());
        claimListAdapter.notifyDataSetChanged();

        // อัปเดตจำนวนรายการ
        tvResultCount.setText(String.format("รายการเบิกทั้งหมด (%d)", claimList.size()));

        // แสดง/ซ่อนข้อความไม่พบผลลัพธ์
        if (claimList.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
            recyclerViewClaims.setVisibility(View.GONE);
        } else {
            tvNoResults.setVisibility(View.GONE);
            recyclerViewClaims.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Perform search based on filter criteria
     */
    private void performSearch() {
        String patientName = etPatientName.getText().toString().trim();
        String patientGroup = spinnerPatientGroup.getSelectedItem().toString();
        String serviceType = spinnerServiceType.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        // Perform search
        List<ClaimInfo> searchResults = claimInfoDao.searchClaims(
                patientName,
                patientGroup,
                selectedFromDate,
                selectedToDate,
                serviceType,
                status
        );

        // Update adapter
        claimListAdapter.updateData(searchResults);

        // Update result count
        tvResultCount.setText(String.format("ผลการค้นหา (%d)", searchResults.size()));

        // Show/hide no results message
        if (searchResults.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
            recyclerViewClaims.setVisibility(View.GONE);
        } else {
            tvNoResults.setVisibility(View.GONE);
            recyclerViewClaims.setVisibility(View.VISIBLE);

            // Hide search panel after search is performed
            if (isSearchVisible) {
                toggleSearchPanel();
            }
        }
    }

    @Override
    public void onViewDetailsClick(ClaimInfo claim, int position) {
        // ไปยังหน้ารายละเอียด claim
        Intent intent = new Intent(this, ClaimDetailActivity.class);
        intent.putExtra("CLAIM_ID", claim.getId());
        startActivity(intent);
    }

    @Override
    public void onStatusCheckComplete(ClaimInfo claim, List<StatusTrackResponse> responses) {

        // ดำเนินการเมื่อตรวจสอบสถานะเสร็จสิ้น
        // เช่น บันทึกข้อมูลลงฐานข้อมูล หรือแสดงรายละเอียดเพิ่มเติม
        if (responses != null && !responses.isEmpty()) {
            StatusTrackResponse response = responses.get(0);

        }
    }

    @Override
    public void onClaimStatusUpdated(ClaimInfo claim, boolean success) {

    }

}