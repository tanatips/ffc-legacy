package th.in.ffc.person;

import static th.in.ffc.util.Log.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import th.in.ffc.R;
import th.in.ffc.api.nhso.FSDataResponse;
import th.in.ffc.api.nhso.NHSOFSDataApiCaller;
import th.in.ffc.api.nhso.NhsoApiCaller;
import th.in.ffc.app.form.nhso.dao.NHSOCHADao;
import th.in.ffc.app.form.nhso.model.NHSOCHADInfo;
import th.in.ffc.app.form.nhso.model.NHSOCHAInfo;
import th.in.ffc.app.form.nhso.model.NHSODiagnosisInfo;
import th.in.ffc.app.form.nhso.model.NHSOHospitalInfo;
import th.in.ffc.app.form.nhso.model.NHSOOPDInfo;
import th.in.ffc.app.form.nhso.model.NHSOPatientInfo;
import th.in.ffc.app.form.nhso.model.NHSOPractitionerInfo;
import th.in.ffc.app.form.nhso.service.NHSOCHADService;
import th.in.ffc.app.form.nhso.service.NHSOCHAService;
import th.in.ffc.app.form.nhso.service.NHSODiagnosisService;
import th.in.ffc.app.form.nhso.service.NHSOHospitalService;
import th.in.ffc.app.form.nhso.service.NHSOOPDService;
import th.in.ffc.app.form.nhso.service.NHSOPatientService;
import th.in.ffc.app.form.nhso.service.NHSOPractitionerService;
import th.in.ffc.app.form.nhso.util.NHSOJsonConverter;
import th.in.ffc.app.form.screening.dao.CounselingSignatureDao;
import th.in.ffc.app.form.screening.dao.SfCardiovascularRiskInfoDao;
import th.in.ffc.app.form.screening.dao.SfDrinkingInfoDao;
import th.in.ffc.app.form.screening.dao.SfDrugsDao;
import th.in.ffc.app.form.screening.dao.SfHealthRiskAssessmentInfoDao;
import th.in.ffc.app.form.screening.dao.SfNicotineInfoDao;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.dao.SfSmokerInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepressionInfoDao;
import th.in.ffc.app.form.screening.dao.SfSuicideAssessment8qInfoDao;
import th.in.ffc.app.form.screening.dao.SfTokenDao;
import th.in.ffc.app.form.screening.model.CardiovascularRiskInfo;
import th.in.ffc.app.form.screening.model.DrinkingInfo;
import th.in.ffc.app.form.screening.model.DrugsInfo;
import th.in.ffc.app.form.screening.model.HealthRiskAssessmentInfo;
import th.in.ffc.app.form.screening.model.NicotineInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.model.SfToken;
import th.in.ffc.app.form.screening.model.StressDepressionInfo;
import th.in.ffc.app.form.screening.model.SuicideAssessment8qInfo;
import th.in.ffc.app.form.screening.model.VisitDiagInfo;
import th.in.ffc.dao.UserDao;
import th.in.ffc.dao.VisitDao;
import th.in.ffc.dao.VisitDiagDao;
import th.in.ffc.model.UserModel;
import th.in.ffc.provider.ScreeningFormProvider;
import th.in.ffc.security.LoginActivity;
import th.in.ffc.session.UserSessionManager;
import th.in.ffc.util.AgeCalculator;
import th.in.ffc.util.InvoiceNumberGenerator;
import th.in.ffc.util.Log;

import th.in.ffc.app.form.screening.dao.SfStressDepression2qInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepression9qInfoDao;


import th.in.ffc.app.form.screening.model.SmokerInfo;

import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.app.form.screening.model.StressDepression9qInfo;
import th.in.ffc.app.form.screening.model.CounselingInfo;
import th.in.ffc.dao.NHSOClaimDataDao;
public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {
    private List<PersonInfo> personList;
    private OnItemClickListener listener;
    private OnButtonClickListener buttonListener; // เพิ่ม listener สำหรับปุ่ม Submit
    boolean isButtonClicked = false;
    private SimpleDateFormat dateFormat;
    private Context mContext;
    public static final String EXTRA_PCUCODE = "pcucode";
    PersonInfo personInfo;

    public static boolean isPartialValidationMode = false;
    // เพิ่ม interface สำหรับปุ่ม
    public interface OnButtonClickListener {
        void onButtonClick(PersonInfo person, int position);
    }

    // เพิ่ม method สำหรับตั้งค่า buttonListener
    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.buttonListener = listener;
    }

    public PersonAdapter(List<PersonInfo> personList) {
        this.personList = personList;
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_item, parent, false);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.US);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {

        PersonInfo person = personList.get(position);
        holder.tvName.setText(person.getFname() + " " + person.getLname());
        holder.tvPersonId.setText(person.getId());

        // แสดงโหมดการตรวจสอบปัจจุบัน
        if (isPartialValidationMode) {
            holder.tvValidationMode.setText("⚡ โหมดบางส่วน");
            holder.tvValidationMode.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
        } else {
            holder.tvValidationMode.setText("🔍 โหมดปกติ");
            holder.tvValidationMode.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_blue_dark));
        }
        // เพิ่มการแสดง Visit Number
        if (person.getVisitId() != null && !person.getVisitId().isEmpty()) {
            holder.tvVisitNumber.setText("หมายเลขการเข้ารับบริการ: " + person.getVisitId());
            holder.tvVisitNumber.setVisibility(View.VISIBLE);
        } else {
            holder.tvVisitNumber.setVisibility(View.GONE);
        }

        // เพิ่มการแสดง Visit Date (ใช้ created_date เป็นวันที่ visit)
        if (person.getCreated_date() != null && !person.getCreated_date().isEmpty()) {
            // Format วันที่ให้สวยงาม
            String visitDate = formatVisitDate(person.getCreated_date());
            holder.tvVisitDate.setText("วันที่เข้ารับบริการ: " + visitDate);
            holder.tvVisitDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvVisitDate.setVisibility(View.GONE);
        }

        if(person.getSend_to_claim().equals(1)){
            holder.btnSubmitClaim.setText("ส่งข้อมูลเรียบร้อย");
            holder.btnSubmitClaim.setEnabled(false);
            holder.tvDataStatus.setText("✅ ส่งข้อมูลแล้ว");
            holder.tvDataStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
        }
        else {
            DataCompletionStatus status = checkDataCompleteness(person, holder.itemView.getContext());

            if (status.isComplete()) {
                holder.btnSubmitClaim.setText("ส่งข้อมูล");
                holder.btnSubmitClaim.setEnabled(true);
                holder.tvDataStatus.setText("✅ " + status.getMessage());
                holder.tvDataStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
            } else {
                holder.btnSubmitClaim.setText("ข้อมูลไม่ครบ");
                holder.btnSubmitClaim.setEnabled(false);
                holder.tvDataStatus.setText("⚠️ " + status.getMessage());
                holder.tvDataStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_orange_dark));
            }
            holder.tvDataStatus.setVisibility(View.VISIBLE);
        }
    }
    private String formatVisitDate(String dateTimeString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(dateTimeString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateTimeString;
        }
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPersonId, tvVisitNumber, tvVisitDate, tvDataStatus, tvValidationMode;
        Button btnSubmitClaim;
        boolean isButtonClicked = false;

        PersonViewHolder(View itemView) {
            super(itemView);
            tvPersonId = itemView.findViewById(R.id.tvPersonId);
            tvName = itemView.findViewById(R.id.tvName);

            tvVisitNumber = itemView.findViewById(R.id.tvVisitNumber);
            tvVisitDate = itemView.findViewById(R.id.tvVisitDate);
            tvDataStatus = itemView.findViewById(R.id.tvDataStatus);
            tvValidationMode = itemView.findViewById(R.id.tvValidationMode);
            btnSubmitClaim = itemView.findViewById(R.id.btnSubmitClaim);
            btnSubmitClaim.setOnClickListener(v -> {
                if (listener != null && getBindingAdapterPosition() != RecyclerView.NO_POSITION) {
                    isButtonClicked = true;
                    mContext = itemView.getContext();
                    int position = getBindingAdapterPosition();
                    PersonInfo person = personList.get(position);
                    listener.onItemClick(person, isButtonClicked);

//                    personList.remove(getBindingAdapterPosition());
//                    notifyItemRemoved(getBindingAdapterPosition());

                    notifyItemRangeChanged(getBindingAdapterPosition(), personList.size());
//                    Toast.makeText(itemView.getContext(), tvIdcard.getText().toString(), Toast.LENGTH_LONG ).show();
                    String[] names = tvName.getText().toString().split(" ");
                    List<PersonInfo>  personInfos =  SfPersonInfoDao.getSfPersonInfoById(Integer.valueOf(tvPersonId.getText().toString()));
                    personInfo = new PersonInfo();
                    SharedPreferences prefs = mContext.getSharedPreferences(LoginActivity.PREFS_FILE, Context.MODE_PRIVATE);
                    String pcuCode = prefs.getString(EXTRA_PCUCODE, "");
                    if(!personInfos.isEmpty()) {
                        personInfo = personInfos.get(0);
                        personInfo.setHcode(pcuCode);  // รหัสสถานบริการ)
                    }
                    UserSessionManager userSessionManager = new UserSessionManager(itemView.getContext());
                    String user = userSessionManager.getUser();
//                    Toast.makeText(itemView.getContext(), user,Toast.LENGTH_LONG).show();

                    NHSOPatientService nhsoPatientService = new NHSOPatientService(itemView.getContext());
                    NHSOHospitalService nhsoHospitalService = new NHSOHospitalService(itemView.getContext());
                    NHSOPractitionerService nhsoPractitionerService = new NHSOPractitionerService(itemView.getContext());
                    NHSOOPDService nhsoopdService =new NHSOOPDService(itemView.getContext());
                    NHSOPatientInfo patient = new NHSOPatientInfo();
                    NHSOHospitalInfo hospital = new NHSOHospitalInfo();
                    NHSOPractitionerInfo practitioner = new NHSOPractitionerInfo();
                    List<NHSOPractitionerInfo> practitioners = new ArrayList<>();
                    List<NHSOHospitalInfo> hospitals = new ArrayList<>();

                        practitioner.setSeq(personInfo.getVisitId());
                        practitioner.setHcode(personInfo.getHcode());
                        practitioner.setCid(userSessionManager.getIdcard());


                    practitioners.add(practitioner);
                    NHSOOPDInfo hnSoOPDInfo = new NHSOOPDInfo();

                    VisitDao visitDao = new VisitDao(itemView.getContext().getContentResolver());
                    long visitId = Long.valueOf(personInfo.getVisitId());
                    String seq = personInfo.getSeq();
//                    long visitId = visitDao.saveNewVisitWithVitalSigns(
//                            userSessionManager.getPcuCode(),                     // pcucode
//                            userSessionManager.getPcuCode(),                     // pcucodePerson
//                            personInfo.getIdcard(),                              // pid
//                            personInfo.getCreated_date(),                        // visitDate
//                            (float)personInfo.getWeight(),                       // weight
//                            (float)personInfo.getHeight(),                       // height
//                            personInfo.getBp(),                                  // pressure
//                            (float)personInfo.getTemperature(),                  // temperature
//                            Integer.valueOf(personInfo.getBp()!=null?personInfo.getBp():"0"),                               // pluse
//                            (float)personInfo.getWaist_size(),                   // waist
//                            String.valueOf(personInfo.getSystolic_pressure()),                 // systolic
//                            String.valueOf(personInfo.getDiastolic_pressure()),                // diastolic                               // diagnote
//                            userSessionManager.getUsername()                     // username
//                    );
                    // แฟ้ม 1
                    patient.setType("CID");
                    patient.setCid(personInfo.getIdcard());
                    patient.setNameGiven(names[0]);
                    patient.setNameFamily(names[1]);
                    patient.setSeq(seq);
                    patient.setBirthDate(personInfo.getBirthday());
                    patient.setGender(personInfo.getGender().equals("M")?"1":"2");
                    patient.setAddressLine(personInfo.getHomeNo()+" หมู่ที่ "+personInfo.getVillageNo());
                    patient.setAddressCity(personInfo.getSubDistCode());
                    patient.setAddressDistrict(personInfo.getDistCode());
                    patient.setAddressState(personInfo.getProvCode());
                    patient.setAddressPostalCode(personInfo.getPostCode());
                    patient.setHn(personInfo.getHn());
                    nhsoPatientService.createPatient(patient, userSessionManager.getUser());

                    // แฟ้ม 2
                    hospital.setSeq(seq);
                    hospital.setHcode(personInfo.getHcode());
                    nhsoHospitalService.createHospital(hospital, userSessionManager.getUser());

                    // แฟ้ม 3

                    practitioner.setSeq(seq);
                    practitioner.setHcode(personInfo.getHcode());
                    practitioner.setCid(personInfo.getIdcard());
                    nhsoPractitionerService.createPractitioner(practitioner,userSessionManager.getUser());

                    // แฟ้ม 4
                    hnSoOPDInfo.setSeq(seq);
                    hnSoOPDInfo.setHtype("1");
                    hnSoOPDInfo.setUuc("1");

                    try {
                        hnSoOPDInfo.setDateOPD(dateFormat.parse(personInfo.getCreated_date()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    SfTokenDao sfTokenDao = new SfTokenDao(itemView.getContext());
                    NhsoApiCaller nhsoApiCaller = new NhsoApiCaller(itemView.getContext());
                    List<SfToken>  sfTokens   = sfTokenDao.getAllTokens();
                    String token = "";
                    if(sfTokens.size()>0){
                        token = sfTokens.get(0).getTokenAuth();
                    }
                    nhsoApiCaller.testRealPersonApi(personInfo.getIdcard(),token, new NhsoApiCaller.RealPersonApiCallback() {
                        @Override
                        public void onSuccess(String response) {
                            String inscl=  nhsoApiCaller.extractInsuranceCode(response);
                            hnSoOPDInfo.setInscl(inscl);
                            Log.d("NHSO: inscl", inscl);
                            nhsoopdService.createOPD(hnSoOPDInfo,userSessionManager.getUser());

                            VisitDiagDao visitDiagDao = new VisitDiagDao(itemView.getContext());

                            // แฟ้ม 5
                            List<VisitDiagInfo> visitDiagInfos = visitDiagDao.getVisitDiagByVisitNo(personInfo.getVisitId());
                            NHSODiagnosisInfo nhsoDiagnosisInfo = new NHSODiagnosisInfo();
                            List<NHSODiagnosisInfo> nhsoDiagnosisInfos = new ArrayList<>();
                            NHSODiagnosisService nhsoDiagnosisService = new NHSODiagnosisService(itemView.getContext());

                            for( VisitDiagInfo visitDiagInfo : visitDiagInfos) {
                                nhsoDiagnosisInfo = new NHSODiagnosisInfo();
                                nhsoDiagnosisInfo.setSeq(seq);
                                nhsoDiagnosisInfo.setDiag(visitDiagInfo.getDiagcode().replace(".",""));
                                nhsoDiagnosisInfo.setDiagType(visitDiagInfo.getDxtype());
                                try {
                                    nhsoDiagnosisInfo.setDateDx(dateFormat.parse(personInfo.getCreated_date()));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                nhsoDiagnosisInfos.add(nhsoDiagnosisInfo);
                                nhsoDiagnosisService.createDiagnosis(nhsoDiagnosisInfo,userSessionManager.getUser());
                            }


//                    Toast.makeText(itemView.getContext(), "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_LONG).show();

                            // แฟ้ม 7
                            InvoiceNumberGenerator invoiceNumberGenerator =new InvoiceNumberGenerator(itemView.getContext());
                            String invoiceNumber = invoiceNumberGenerator.generateInvoiceNumber();
                            NHSOCHADService chadService = new NHSOCHADService(itemView.getContext());
                            NHSOCHADInfo nhsochadInfo = new NHSOCHADInfo();
                            List<NHSOCHADInfo> nhsochadInfos = new ArrayList<>();
                            nhsochadInfo.setSeq(seq);
                            nhsochadInfo.setStdcode("1170884"); /* 1170884 (TMTID) 220001 (TMLT Code) 9099264 (TTMTID) */
                            nhsochadInfo.setInvoiceNo(invoiceNumber);  // เลขที่อ้างอิงในแจ้งหนี้

                            try {
                                nhsochadInfo.setServdate(dateFormat.parse(personInfo.getCreated_date()));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            nhsochadInfo.setCodesys("002");      // ระบบรหัสที่ใช้ (TMLT)
                            nhsochadInfo.setBillgrcs("04");      // หมวดค่าใช้จ่าย
                            nhsochadInfo.setQty(1);              // จำนวนที่ใช้

                            SfHealthRiskAssessmentInfoDao sfHealthRiskAssessmentInfoDao = new SfHealthRiskAssessmentInfoDao(itemView.getContext());
                            List<HealthRiskAssessmentInfo> healthRiskAssessmentInfos =  sfHealthRiskAssessmentInfoDao.getByPersonId(Integer.parseInt(personInfo.getId()));
                            SfCardiovascularRiskInfoDao sfCardiovascularRiskInfoDao = new SfCardiovascularRiskInfoDao(itemView.getContext());
                            List<CardiovascularRiskInfo> cardiovascularRiskInfos = sfCardiovascularRiskInfoDao.getByPersonId(Integer.parseInt(personInfo.getId()));
                            double fpg= 0.0;
                            double choresteral = 0.0;
                            if(!healthRiskAssessmentInfos.isEmpty())
                            {
                                if(healthRiskAssessmentInfos.get(0).getFpg()==null && healthRiskAssessmentInfos.get(0).getFpg().isEmpty() && Objects.equals(healthRiskAssessmentInfos.get(0).getFpg(), "")) {
                                    fpg = Double.parseDouble(healthRiskAssessmentInfos.get(0).getFpg());
                                }
                            }
                            if(!cardiovascularRiskInfos.isEmpty())
                            {
                                if(cardiovascularRiskInfos.get(0).getCholesterol()==null && cardiovascularRiskInfos.get(0).getCholesterol().isEmpty() && Objects.equals(cardiovascularRiskInfos.get(0).getCholesterol(), "")) {
                                    choresteral = Double.parseDouble(cardiovascularRiskInfos.get(0).getCholesterol());
                                }
                            }
                            double cost13 = AgeCalculator.calculateServiceCost(AgeCalculator.calculateAge(personInfo.getBirthday()),0,0);
                            double costFpg = AgeCalculator.calculateServiceCost(AgeCalculator.calculateAge(personInfo.getBirthday()),fpg,0);
                            double costChoresteral = AgeCalculator.calculateServiceCost(AgeCalculator.calculateAge(personInfo.getBirthday()),0,choresteral);
                            double costTotal = cost13+costFpg+costChoresteral;
                            nhsochadInfo.setUnitprice(costTotal);    // ราคาต่อหน่วย
                            nhsochadInfo.setChargeamt(costTotal);    // จำนวนเงินเรียกเก็บ

                            nhsochadInfos.add(nhsochadInfo);
                            chadService.createCHAD(nhsochadInfo, userSessionManager.getUser());

                            // แฟ้ม 8
                            NHSOCHAInfo chaInfo = new NHSOCHAInfo();
                            List<NHSOCHAInfo> chaInfos = new ArrayList<>();
                            NHSOCHAService chaService = new NHSOCHAService(itemView.getContext());
                            chaInfo.setSeq(seq);
                            try {
                                chaInfo.setDate(dateFormat.parse(personInfo.getCreated_date()));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            double amount = 0.0,total=0.0 ,memo = 0.0;
                            amount = costTotal;
                            total = costTotal;
                            chaInfo.setChrgitem("I1"); // ทำหัตถการ และบริการวิสัญญี
                            chaInfo.setInvoiceNo(invoiceNumber);
                            chaInfo.setAmount(amount);
                            chaInfo.setTotal(total);

                            chaInfos.add(chaInfo);
                            chaService.createCHA(chaInfo, userSessionManager.getUser());
                            isButtonClicked = false;
                            // 2. สร้าง JSON ด้วย NHSOJsonConverter
                            JSONObject jsonObject = NHSOJsonConverter.createNHSORequestJson(
                                    patient, // แฟ้ม 1
                                    hospital, // แฟ้ม 2
                                    practitioners, // แฟ้ม 3
                                    hnSoOPDInfo, // แฟ้ม 4
                                    nhsoDiagnosisInfos, // แฟ้ม 5
                                    chaInfos, // แฟ้ม 7
                                    nhsochadInfos // แฟ้ม 8
                            );
                            Log.d("== NHSO ==", jsonObject.toString());
                            if (jsonObject == null) {
                                showMessage("ไม่สามารถสร้างข้อมูล JSON ได้");
                                return;
                            }
                            NHSOClaimDataDao claimDataDao = new NHSOClaimDataDao(itemView.getContext());
                            claimDataDao.saveClaimData(Integer.parseInt(personInfo.getVisitId()), jsonObject);

                            NHSOFSDataApiCaller apiCaller = new NHSOFSDataApiCaller(itemView.getContext());
                            apiCaller.sendFSData(jsonObject, new NHSOFSDataApiCaller.FSDataApiCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    Log.d(TAG, "API Response: " + response);

                                    // แปลงข้อมูล JSON เป็น FSDataResponse
                                    try {

                                        FSDataResponse[] fsResponses = new Gson().fromJson(response, FSDataResponse[].class);
                                        FSDataResponse fsResponse = fsResponses[0];
                                        if (fsResponse.isSuccess()) {
                                            // กรณีสำเร็จ
                                            showMessage("ส่งข้อมูลสำเร็จ! seq no: " + fsResponse.getSeq());

                                            // อัพเดทสถานะการส่งข้อมูลในฐานข้อมูล
                                            updateSyncStatus(String.valueOf(visitId), true);
                                            // อัพเดทข้อมูล claim ในตาราง ffc_sf_person_info
                                            String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());
                                            SfPersonInfoDao.updateClaimInfo(
                                                    person.getId(),        // ID ของผู้ป่วย
                                                    fsResponse.getId(),         // ID การเคลม
                                                    "",                      // สถานะการเคลม
                                                    "",              // ข้อความ
                                                    currentDateTime,
                                                    String.valueOf(visitId)
                                            );
                                            claimDataDao.updateSuccessStatus((int) visitId, fsResponse.getSeq(), response);
                                            Log.d(TAG, "Updated claim information for person ID: " + personInfo.getId());
                                        } else {
                                            // กรณีไม่สำเร็จ
                                            showMessage("ส่งข้อมูลไม่สำเร็จ: " + fsResponse.getErrorSummary());
                                        }
                                    } catch (Exception e) {
                                        showMessage("ส่งข้อมูลสำเร็จ แต่ไม่สามารถประมวลผลการตอบกลับได้: " + e.getMessage());
                                    }
                                }

                                @Override
                                public void onError(String errorMessage, Exception e) {
                                    Log.e(TAG, "API Error: " + errorMessage+" "+e.getMessage());
                                    showMessage("เกิดข้อผิดพลาด: " + errorMessage);
                                    // เมื่อ API สำเร็จ - อัพเดทสถานะ
                                    claimDataDao.updateFailedStatus((int)visitId, errorMessage);
                                }
                            });
                        }

                        @Override
                        public void onError(String errorMessage) {

                            Log.e("NHSO", errorMessage);
                            Toast.makeText(itemView.getContext(), "Error:"+errorMessage, Toast.LENGTH_LONG).show();

                        }
                    });
                    }
            });
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(personList.get(position),isButtonClicked);
                }
                isButtonClicked = false;
            });


        }

    }
    private void updateSyncStatus(String visitNumber, boolean synced) {
        // ตัวอย่างการอัพเดทสถานะในฐานข้อมูล
        // ในการใช้งานจริงควรอัพเดทสถานะของทุกแฟ้มที่เกี่ยวข้อง
        try {
            NHSOCHADao chaDao = new NHSOCHADao(mContext);
            List<NHSOCHAInfo> chaList = chaDao.getCHABySeq(visitNumber);

            for (NHSOCHAInfo cha : chaList) {
                chaDao.updateSyncStatus(cha.getId(), synced ? "1" : "0");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating sync status:"+e.getMessage());
        }
    }
    private void showMessage(String message) {
        Log.d(TAG, message);
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
    public interface OnItemClickListener {
        void onItemClick(PersonInfo person, boolean isButtonClicked);
    }
    // เพิ่ม interface ใหม่สำหรับปุ่ม Submit
    public interface OnSubmitClickListener {
        void onSubmitClick(PersonInfo person);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // เพิ่มเมธอดใหม่สำหรับตรวจสอบความสมบูรณ์ของข้อมูล
//    private DataCompletionStatus checkDataCompleteness(PersonInfo person, Context context) {
//        if (context == null) return new DataCompletionStatus(false, "ไม่สามารถตรวจสอบข้อมูลได้");
//
//        try {
//            // ตรวจสอบข้อมูลพื้นฐาน
//            if (person.getId() == null || person.getId().isEmpty()) {
//                return new DataCompletionStatus(false, "ไม่พบข้อมูลผู้ป่วย");
//            }
//
//            int personId = Integer.parseInt(person.getId());
//            List<String> missingAssessments = new ArrayList<>();
//
//            // ตรวจสอบข้อมูลพื้นฐานของผู้ป่วย
//            boolean hasBasicInfo = isBasicInfoComplete(person);
//            if (!hasBasicInfo) missingAssessments.add("ข้อมูลพื้นฐาน");
//
//            // 1. ตรวจสอบสารเสพติด (Drug Assessment) - ต้องตรวจสอบก่อนเสมอ
//            SfDrugsDao drugDao = new SfDrugsDao(context);
//            List<DrugsInfo> drugInfos = drugDao.getSfDrugsByPersonInfoId(personId);
//            boolean hasDrugAssessment = !drugInfos.isEmpty() && isDrugsComplete(drugInfos);
//            if (!hasDrugAssessment) {
//                missingAssessments.add("แบบประเมินสารเสพติด");
//                // หากยังไม่มีการประเมินสารเสพติด ให้ข้ามการตรวจสอบบุหรี่และสุรา
//            } else {
//                // มีการประเมินสารเสพติดแล้ว - ตรวจสอบว่าต้องการประเมินบุหรี่และสุราหรือไม่
//                SubstanceUseStatus substanceStatus = getSubstanceUseStatus(drugInfos);
//
//                // 2. ตรวจสอบการสูบบุหรี่ (เฉพาะกรณีที่เคยสูบบุหรี่)
//                if (substanceStatus.usesTobacco) {
//                    SfSmokerInfoDao smokingDao = new SfSmokerInfoDao(context);
//                    List<SmokerInfo> smokingInfos = smokingDao.getByPersonId(personId);
//                    boolean hasSmokingAssessment = !smokingInfos.isEmpty() && isSmokingAssessmentComplete(smokingInfos.get(0));
//                    if (!hasSmokingAssessment) missingAssessments.add("แบบประเมินการสูบบุหรี่");
//
//                    // 3. ตรวจสอบการติดบุหรี่ (เฉพาะกรณีที่เคยสูบบุหรี่)
//                    SfNicotineInfoDao sfNicotineInfoDao = new SfNicotineInfoDao(context);
//                    List<NicotineInfo> smokingAddictionInfos = sfNicotineInfoDao.getByPersonId(personId);
//                    boolean hasSmokingAddiction = !smokingAddictionInfos.isEmpty() && isNicotineComplete(smokingAddictionInfos.get(0));
//                    if (!hasSmokingAddiction) missingAssessments.add("แบบประเมินการติดบุหรี่");
//                } else {
//                    Log.d("DATA_CHECK", "ข้ามการตรวจสอบบุหรี่ - ไม่เคยสูบบุหรี่");
//                }
//
//                // 4. ตรวจสอบการดื่มสุรา (เฉพาะกรณีที่เคยดื่มสุรา)
//                if (substanceStatus.usesAlcohol) {
//                    SfDrinkingInfoDao alcoholDao = new SfDrinkingInfoDao(context);
//                    List<DrinkingInfo> alcoholInfos = alcoholDao.getByPersonId(personId);
//                    boolean hasAlcoholAssessment = !alcoholInfos.isEmpty() && isAlcoholAssessmentComplete(alcoholInfos.get(0));
//                    if (!hasAlcoholAssessment) missingAssessments.add("แบบประเมินการดื่มสุรา");
//                } else {
//                    Log.d("DATA_CHECK", "ข้ามการตรวจสอบสุรา - ไม่เคยดื่มสุรา");
//                }
//            }
//
//            // 5. ตรวจสอบ ST-5 (Stress Test 5) - จำเป็นเสมอ
//            SfStressDepressionInfoDao sfStressDepressionInfoDao = new SfStressDepressionInfoDao(context);
//            List<StressDepressionInfo> st5Infos = sfStressDepressionInfoDao.getByPersonId(personId);
//            boolean hasSt5Assessment = !st5Infos.isEmpty() && isSt5AssessmentComplete(st5Infos.get(0));
//            if (!hasSt5Assessment) missingAssessments.add("แบบประเมิน ST-5");
//
//            // 6. ตรวจสอบ 2Q (Depression 2 Questions) - จำเป็นเสมอ
//            SfStressDepression2qInfoDao depression2qDao = new SfStressDepression2qInfoDao(context);
//            List<StressDepression2qInfo> depression2qInfos = depression2qDao.getByPersonId(personId);
//            boolean has2qAssessment = !depression2qInfos.isEmpty() && is2qAssessmentComplete(depression2qInfos.get(0));
//            if (!has2qAssessment) missingAssessments.add("แบบประเมิน 2Q");
//
//            // 7. ตรวจสอบ 9Q (Depression 9 Questions) - จำเป็นเสมอ
//            SfStressDepression9qInfoDao depression9qDao = new SfStressDepression9qInfoDao(context);
//            List<StressDepression9qInfo> depression9qInfos = depression9qDao.getByPersonId(personId);
//            boolean has9qAssessment = !depression9qInfos.isEmpty() && is9qAssessmentComplete(depression9qInfos.get(0));
//            if (!has9qAssessment) missingAssessments.add("แบบประเมิน 9Q");
//
//            // 8. ตรวจสอบ 8Q (Suicide Assessment 8 Questions) - จำเป็นเสมอ
//            SfSuicideAssessment8qInfoDao depression8qDao = new SfSuicideAssessment8qInfoDao(context);
//            List<SuicideAssessment8qInfo> depression8qInfos = depression8qDao.getByPersonId(personId);
//            boolean has8qAssessment = !depression8qInfos.isEmpty() && is8qAssessmentComplete(depression8qInfos.get(0));
//            if (!has8qAssessment) missingAssessments.add("แบบประเมิน 8Q");
//
//            // 9. ตรวจสอบโรคเบาหวาน (Health Risk Assessment - Diabetes) - จำเป็นเสมอ
//            SfHealthRiskAssessmentInfoDao healthRiskDao = new SfHealthRiskAssessmentInfoDao(context);
//            List<HealthRiskAssessmentInfo> healthRiskInfos = healthRiskDao.getByPersonId(personId);
//            boolean hasHealthRisk = !healthRiskInfos.isEmpty() && isHealthRiskComplete(healthRiskInfos.get(0));
//            if (!hasHealthRisk) missingAssessments.add("แบบประเมินโรคเบาหวาน");
//
//            // 10. ตรวจสอบโรคหัวใจและหลอดเลือด (Cardiovascular Risk) - จำเป็นเสมอ
//            SfCardiovascularRiskInfoDao cardioDao = new SfCardiovascularRiskInfoDao(context);
//            List<CardiovascularRiskInfo> cardioInfos = cardioDao.getByPersonId(personId);
//            boolean hasCardioRisk = !cardioInfos.isEmpty() && isCardiovascularRiskComplete(cardioInfos.get(0));
//            if (!hasCardioRisk) missingAssessments.add("แบบประเมินโรคหัวใจและหลอดเลือด");
//
//            // 11. ตรวจสอบการให้คำปรึกษาและแนะนำ (Counseling) - จำเป็นเสมอ
//            CounselingSignatureDao counselingDao = new CounselingSignatureDao(context);
//            List<CounselingInfo> counselingInfos = counselingDao.getCounselingByPersonId(String.valueOf(personId));
//            boolean hasCounseling = !counselingInfos.isEmpty() && isCounselingComplete(counselingInfos.get(0));
//            if (!hasCounseling) missingAssessments.add("การให้คำปรึกษาและแนะนำ");
//
//            // สรุปผลการตรวจสอบ
//            if (missingAssessments.isEmpty()) {
//                return new DataCompletionStatus(true, "ข้อมูลครบถ้วนทุกแบบประเมิน - พร้อมส่ง Claim");
//            } else {
//                String missingText = String.join(", ", missingAssessments);
//                return new DataCompletionStatus(false, "ข้อมูลไม่ครบ: " + missingText);
//            }
//
//        } catch (Exception e) {
//            Log.e("PersonAdapter", "Error checking data completeness: " + e.getMessage());
//            return new DataCompletionStatus(false, "เกิดข้อผิดพลาดในการตรวจสอบข้อมูล");
//        }
//    }

    private DataCompletionStatus checkDataCompleteness(PersonInfo person, Context context) {
        if (context == null) return new DataCompletionStatus(false, "ไม่สามารถตรวจสอบข้อมูลได้");

        try {
            if (person.getId() == null || person.getId().isEmpty()) {
                return new DataCompletionStatus(false, "ไม่พบข้อมูลผู้ป่วย");
            }

            int personId = Integer.parseInt(person.getId());
            List<String> missingAssessments = new ArrayList<>();

            // ตรวจสอบข้อมูลพื้นฐาน
            boolean hasBasicInfo = isBasicInfoComplete(person);
            if (!hasBasicInfo) missingAssessments.add("ข้อมูลพื้นฐาน");

            if (isPartialValidationMode) {
                // โหมดบางส่วน - ง่ายๆ
                return checkPartialMode(personId, context, missingAssessments);
            } else {
                // โหมดปกติ - เดิม
                return checkStrictMode(personId, context, missingAssessments);
            }

        } catch (Exception e) {
            Log.e("PersonAdapter", "Error checking data completeness: " + e.getMessage());
            return new DataCompletionStatus(false, "เกิดข้อผิดพลาดในการตรวจสอบข้อมูล");
        }
    }
    private DataCompletionStatus checkPartialMode(int personId, Context context, List<String> missingAssessments) {
        // 1. การคัดกรองสารเสพติด - ต้องมี
        SfDrugsDao drugDao = new SfDrugsDao(context);
        List<DrugsInfo> drugInfos = drugDao.getSfDrugsByPersonInfoId(personId);
        boolean hasDrugAssessment = !drugInfos.isEmpty() && isDrugsComplete(drugInfos);
        if (!hasDrugAssessment) {
            missingAssessments.add("แบบคัดกรองการใช้สารเสพติด");
        }

        // 2. ภาวะเครียด-ซึมเศร้า - อย่างน้อย 1 ใน 4
        boolean hasMentalHealth = false;

        // ตรวจสอบ ST-5
        SfStressDepressionInfoDao st5Dao = new SfStressDepressionInfoDao(context);
        List<StressDepressionInfo> st5Infos = st5Dao.getByPersonId(personId);
        if (!st5Infos.isEmpty() && isSt5AssessmentComplete(st5Infos.get(0))) {
            hasMentalHealth = true;
        }

        // ตรวจสอบ 2Q
        if (!hasMentalHealth) {
            SfStressDepression2qInfoDao depression2qDao = new SfStressDepression2qInfoDao(context);
            List<StressDepression2qInfo> depression2qInfos = depression2qDao.getByPersonId(personId);
            if (!depression2qInfos.isEmpty() && is2qAssessmentComplete(depression2qInfos.get(0))) {
                hasMentalHealth = true;
            }
        }

        // ตรวจสอบ 9Q
        if (!hasMentalHealth) {
            SfStressDepression9qInfoDao depression9qDao = new SfStressDepression9qInfoDao(context);
            List<StressDepression9qInfo> depression9qInfos = depression9qDao.getByPersonId(personId);
            if (!depression9qInfos.isEmpty() && is9qAssessmentComplete(depression9qInfos.get(0))) {
                hasMentalHealth = true;
            }
        }

        // ตรวจสอบ 8Q
        if (!hasMentalHealth) {
            SfSuicideAssessment8qInfoDao depression8qDao = new SfSuicideAssessment8qInfoDao(context);
            List<SuicideAssessment8qInfo> depression8qInfos = depression8qDao.getByPersonId(personId);
            if (!depression8qInfos.isEmpty() && is8qAssessmentComplete(depression8qInfos.get(0))) {
                hasMentalHealth = true;
            }
        }

        if (!hasMentalHealth) {
            missingAssessments.add("ภาวะเครียด-ซึมเศร้า (อย่างน้อย 1 ใน 4 แบบ)");
        }

        // 3. ความเสี่ยงด้านสุขภาพ - อย่างน้อย 1 ใน 2
        boolean hasHealthRisk = false;

        // ตรวจสอบเบาหวาน
        SfHealthRiskAssessmentInfoDao healthRiskDao = new SfHealthRiskAssessmentInfoDao(context);
        List<HealthRiskAssessmentInfo> healthRiskInfos = healthRiskDao.getByPersonId(personId);
        if (!healthRiskInfos.isEmpty() && isHealthRiskComplete(healthRiskInfos.get(0))) {
            hasHealthRisk = true;
        }

        // ตรวจสอบหัวใจและหลอดเลือด
        if (!hasHealthRisk) {
            SfCardiovascularRiskInfoDao cardioDao = new SfCardiovascularRiskInfoDao(context);
            List<CardiovascularRiskInfo> cardioInfos = cardioDao.getByPersonId(personId);
            if (!cardioInfos.isEmpty() && isCardiovascularRiskComplete(cardioInfos.get(0))) {
                hasHealthRisk = true;
            }
        }

        if (!hasHealthRisk) {
            missingAssessments.add("ความเสี่ยงด้านสุขภาพ (อย่างน้อย 1 ใน 2 แบบ)");
        }

        // 4. การให้คำปรึกษา - ต้องมีเสมอ
        CounselingSignatureDao counselingDao = new CounselingSignatureDao(context);
        List<CounselingInfo> counselingInfos = counselingDao.getCounselingByPersonId(String.valueOf(personId));
        boolean hasCounseling = !counselingInfos.isEmpty() && isCounselingComplete(counselingInfos.get(0));
        if (!hasCounseling) {
            missingAssessments.add("การให้คำปรึกษาและแนะนำ");
        }

        // สรุปผล
        if (missingAssessments.isEmpty()) {
            return new DataCompletionStatus(true, "ข้อมูลครบถ้วน (โหมดบางส่วน)");
        } else {
            String missingText = String.join(", ", missingAssessments);
            return new DataCompletionStatus(false, "ข้อมูลไม่ครบ: " + missingText);
        }
    }
    private DataCompletionStatus checkStrictMode(int personId, Context context, List<String> missingAssessments) {
        // 1. ตรวจสอบสารเสพติด
        SfDrugsDao sfDrugsDao = new SfDrugsDao(context);
        List<DrugsInfo> drugsInfos = sfDrugsDao.getSfDrugsByPersonInfoId(personId);
        boolean hasDrugAssessment = !drugsInfos.isEmpty() && isDrugsComplete(drugsInfos);
        if (!hasDrugAssessment) {
            missingAssessments.add("แบบประเมินสารเสพติด");
        } else {
            // ตรวจสอบการใช้สารเสพติดเพื่อดูว่าต้องทำแบบประเมินเพิ่มหรือไม่
            SubstanceUseStatus substanceStatus = getSubstanceUseStatus(drugsInfos);

            // ตรวจสอบการสูบบุหรี่
            if (substanceStatus.usesTobacco) {
                SfSmokerInfoDao smokingDao = new SfSmokerInfoDao(context);
                List<SmokerInfo> smokingInfos = smokingDao.getByPersonId(personId);
                boolean hasSmokingAssessment = !smokingInfos.isEmpty() && isSmokingAssessmentComplete(smokingInfos.get(0));
                if (!hasSmokingAssessment) missingAssessments.add("แบบประเมินการสูบบุหรี่");

                SfNicotineInfoDao sfNicotineInfoDao = new SfNicotineInfoDao(context);
                List<NicotineInfo> smokingAddictionInfos = sfNicotineInfoDao.getByPersonId(personId);
                boolean hasSmokingAddiction = !smokingAddictionInfos.isEmpty() && isNicotineComplete(smokingAddictionInfos.get(0));
                if (!hasSmokingAddiction) missingAssessments.add("แบบประเมินการติดบุหรี่");
            }

            // ตรวจสอบการดื่มสุรา
            if (substanceStatus.usesAlcohol) {
                SfDrinkingInfoDao alcoholDao = new SfDrinkingInfoDao(context);
                List<DrinkingInfo> alcoholInfos = alcoholDao.getByPersonId(personId);
                boolean hasAlcoholAssessment = !alcoholInfos.isEmpty() && isAlcoholAssessmentComplete(alcoholInfos.get(0));
                if (!hasAlcoholAssessment) missingAssessments.add("แบบประเมินการดื่มสุรา");
            }
        }

        // 2-5. ตรวจสอบภาวะเครียด-ซึมเศร้า (ทุกแบบ)
        SfStressDepressionInfoDao sfStressDepressionInfoDao = new SfStressDepressionInfoDao(context);
        List<StressDepressionInfo> st5Infos = sfStressDepressionInfoDao.getByPersonId(personId);
        boolean hasSt5Assessment = !st5Infos.isEmpty() && isSt5AssessmentComplete(st5Infos.get(0));
        if (!hasSt5Assessment) missingAssessments.add("แบบประเมิน ST-5");

        SfStressDepression2qInfoDao depression2qDao = new SfStressDepression2qInfoDao(context);
        List<StressDepression2qInfo> depression2qInfos = depression2qDao.getByPersonId(personId);
        boolean has2qAssessment = !depression2qInfos.isEmpty() && is2qAssessmentComplete(depression2qInfos.get(0));
        if (!has2qAssessment) missingAssessments.add("แบบประเมิน 2Q");

        SfStressDepression9qInfoDao depression9qDao = new SfStressDepression9qInfoDao(context);
        List<StressDepression9qInfo> depression9qInfos = depression9qDao.getByPersonId(personId);
        boolean has9qAssessment = !depression9qInfos.isEmpty() && is9qAssessmentComplete(depression9qInfos.get(0));
        if (!has9qAssessment) missingAssessments.add("แบบประเมิน 9Q");

        SfSuicideAssessment8qInfoDao depression8qDao = new SfSuicideAssessment8qInfoDao(context);
        List<SuicideAssessment8qInfo> depression8qInfos = depression8qDao.getByPersonId(personId);
        boolean has8qAssessment = !depression8qInfos.isEmpty() && is8qAssessmentComplete(depression8qInfos.get(0));
        if (!has8qAssessment) missingAssessments.add("แบบประเมิน 8Q");

        // 6-7. ตรวจสอบความเสี่ยงด้านสุขภาพ (ทุกแบบ)
        SfHealthRiskAssessmentInfoDao healthRiskDao = new SfHealthRiskAssessmentInfoDao(context);
        List<HealthRiskAssessmentInfo> healthRiskInfos = healthRiskDao.getByPersonId(personId);
        boolean hasHealthRisk = !healthRiskInfos.isEmpty() && isHealthRiskComplete(healthRiskInfos.get(0));
        if (!hasHealthRisk) missingAssessments.add("แบบประเมินโรคเบาหวาน");

        SfCardiovascularRiskInfoDao cardioDao = new SfCardiovascularRiskInfoDao(context);
        List<CardiovascularRiskInfo> cardioInfos = cardioDao.getByPersonId(personId);
        boolean hasCardioRisk = !cardioInfos.isEmpty() && isCardiovascularRiskComplete(cardioInfos.get(0));
        if (!hasCardioRisk) missingAssessments.add("แบบประเมินโรคหัวใจและหลอดเลือด");

        // 8. การให้คำปรึกษา
        CounselingSignatureDao counselingDao = new CounselingSignatureDao(context);
        List<CounselingInfo> counselingInfos = counselingDao.getCounselingByPersonId(String.valueOf(personId));
        boolean hasCounseling = !counselingInfos.isEmpty() && isCounselingComplete(counselingInfos.get(0));
        if (!hasCounseling) missingAssessments.add("การให้คำปรึกษาและแนะนำ");

        // สรุปผล
        if (missingAssessments.isEmpty()) {
            return new DataCompletionStatus(true, "ข้อมูลครบถ้วนทุกแบบประเมิน");
        } else {
            String missingText = String.join(", ", missingAssessments);
            return new DataCompletionStatus(false, "ข้อมูลไม่ครบ: " + missingText);
        }
    }
    public static void setValidationMode(boolean isPartialMode) {
        isPartialValidationMode = isPartialMode;
        Log.d("VALIDATION_MODE", "Set to: " + (isPartialMode ? "Partial" : "Strict"));
    }
    public static boolean isPartialMode() {
        return isPartialValidationMode;
    }

    public static String getCurrentModeText() {
        return isPartialValidationMode ? "⚡ โหมดบางส่วน" : "🔍 โหมดปกติ";
    }
    private SubstanceUseStatus getSubstanceUseStatus(List<DrugsInfo> drugInfos) {
        SubstanceUseStatus status = new SubstanceUseStatus();

        if (drugInfos == null || drugInfos.isEmpty()) {
            return status; // default: false, false
        }

        for (DrugsInfo drug : drugInfos) {
            if ("Q1".equals(drug.getQuestion())) {
                String answer = drug.getAnswer();
                String subQuestion = drug.getSubquestion();

                // ตรวจสอบการใช้ยาสูบ (Q1a = ผลิตภัณฑ์ยาสูบ)
                if ("a".equals(subQuestion) && answer != null && !"0".equals(answer) && !answer.isEmpty()) {
                    status.usesTobacco = true;
                    Log.d("SUBSTANCE_CHECK", "พบการใช้ยาสูบ: " + answer);
                }

                // ตรวจสอบการดื่มสุรา (Q1b = เครื่องดื่มแอลกอฮอล์)
                if ("b".equals(subQuestion) && answer != null && !"0".equals(answer) && !answer.isEmpty()) {
                    status.usesAlcohol = true;
                    Log.d("SUBSTANCE_CHECK", "พบการดื่มสุรา: " + answer);
                }
            }
        }

        Log.d("SUBSTANCE_CHECK", "สถานะการใช้สาร - ยาสูบ: " + status.usesTobacco + ", สุรา: " + status.usesAlcohol);
        return status;
    }
    private static class SubstanceUseStatus {
        boolean usesTobacco = false;  // เคยใช้ยาสูบหรือไม่
        boolean usesAlcohol = false;  // เคยดื่มสุราหรือไม่

        public SubstanceUseStatus() {}

        public SubstanceUseStatus(boolean usesTobacco, boolean usesAlcohol) {
            this.usesTobacco = usesTobacco;
            this.usesAlcohol = usesAlcohol;
        }

        @Override
        public String toString() {
            return "SubstanceUseStatus{usesTobacco=" + usesTobacco + ", usesAlcohol=" + usesAlcohol + "}";
        }
    }
// เพิ่มเมธอดตรวจสอบแต่ละแบบประเมิน
private boolean isDrugsComplete(List<DrugsInfo> drugsInfos) {
    if (drugsInfos == null || drugsInfos.isEmpty()) {
        return false;
    }
    if( drugsInfos.size() == 70) {
        Log.d("ASSIST_CHECK", "Drugs assessment incomplete - less than 10 records found");
        return true; // ต้องมีอย่างน้อย 10 records สำหรับ Q1
    }
    try {
        // ตรวจสอบ Q1 (คำถามการใช้สารเสพติด) - ต้องมี 10 subquestions (a-j)
        Map<String, String> q1Answers = new HashMap<>();

        // เก็บคำตอบ Q1 ทั้งหมด
        for (DrugsInfo drug : drugsInfos) {
            if ("Q1".equals(drug.getQuestion())) {
                q1Answers.put(drug.getSubquestion(), drug.getAnswer());
            }
        }

        // ตรวจสอบว่า Q1 ครบ 10 subquestions (a-j)
        String[] expectedSubQuestions = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        for (String subQ : expectedSubQuestions) {
            if (!q1Answers.containsKey(subQ)) {
                Log.d("ASSIST_CHECK", "Missing Q1 subquestion: " + subQ);
                return false; // ยังไม่ได้ตอบ Q1 ครบ
            }
        }

        // ตรวจสอบว่า Q1 ตอบเป็น "0" ทั้งหมดหรือไม่
        boolean allQ1Zero = true;
        List<String> substancesUsed = new ArrayList<>(); // เก็บสารที่เคยใช้ (ตอบไม่ใช่ 0)

        for (String subQ : expectedSubQuestions) {
            String answer = q1Answers.get(subQ);
            if (answer == null || answer.isEmpty()) {
                Log.d("ASSIST_CHECK", "Q1 subquestion " + subQ + " has no answer");
                return false;
            }

            if (!"0".equals(answer)) {
                allQ1Zero = false;
                substancesUsed.add(subQ); // เพิ่มสารที่เคยใช้ในรายการ
            }
        }

        // กรณีที่ Q1 ตอบเป็น "0" ทั้งหมด = ครบถ้วนแล้ว (ไม่เคยใช้สารใดๆ)
        if (allQ1Zero) {
            Log.d("ASSIST_CHECK", "All Q1 answers are 0 - Assessment complete");
            return true;
        }

        // กรณีที่มีการใช้สารบางชนิด = ต้องตอบ Q2-Q8 สำหรับสารที่เคยใช้
        Log.d("ASSIST_CHECK", "Substances used: " + substancesUsed.toString());

        // ตรวจสอบ Q2-Q8 สำหรับสารที่เคยใช้
        String[] followUpQuestions = {"Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8"};

        for (String substance : substancesUsed) {
            for (String question : followUpQuestions) {
                boolean found = false;
                for (DrugsInfo drug : drugsInfos) {
                    if (question.equals(drug.getQuestion()) &&
                            substance.equals(drug.getSubquestion())) {

                        // ตรวจสอบว่ามีคำตอบและไม่ใช่ค่าเริ่มต้น
                        if (drug.getAnswer() != null &&
                                !drug.getAnswer().isEmpty() &&
                                !"0".equals(drug.getAnswer())) {
                            found = true;
                            break;
                        }
                    }
                }

                if (!found) {
                    Log.d("ASSIST_CHECK", "Missing answer for " + question + " substance " + substance);
                    return false; // ยังไม่ได้ตอบคำถามครบ
                }
            }
        }

        Log.d("ASSIST_CHECK", "All required questions answered - Assessment complete");
        return true;

    } catch (Exception e) {
        Log.e("ASSIST_CHECK", "Error checking drugs completion: " + e.getMessage());
        return false;
    }
}
    private void debugAssistStatus(List<DrugsInfo> drugsInfos) {
        if (drugsInfos == null || drugsInfos.isEmpty()) {
            Log.d("ASSIST_DEBUG", "No ASSIST data found");
            return;
        }

        Log.d("ASSIST_DEBUG", "=== ASSIST Data Debug ===");
        Log.d("ASSIST_DEBUG", "Total records: " + drugsInfos.size());

        // จัดกลุ่มข้อมูลตามคำถาม
        Map<String, List<DrugsInfo>> questionGroups = new HashMap<>();
        for (DrugsInfo drug : drugsInfos) {
            String question = drug.getQuestion();
            if (!questionGroups.containsKey(question)) {
                questionGroups.put(question, new ArrayList<>());
            }
            questionGroups.get(question).add(drug);
        }

        // แสดงข้อมูลแต่ละคำถาม
        for (String question : questionGroups.keySet()) {
            List<DrugsInfo> questionData = questionGroups.get(question);
            Log.d("ASSIST_DEBUG", question + " has " + questionData.size() + " answers");

            for (DrugsInfo drug : questionData) {
                Log.d("ASSIST_DEBUG", "  " + question + drug.getSubquestion() +
                        " = " + drug.getAnswer());
            }
        }
        Log.d("ASSIST_DEBUG", "========================");
    }
    private boolean isQ1Complete(List<DrugsInfo> drugsInfos) {
        if (drugsInfos == null || drugsInfos.isEmpty()) {
            return false;
        }

        String[] expectedSubQuestions = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        Set<String> answeredSubQuestions = new HashSet<>();

        for (DrugsInfo drug : drugsInfos) {
            if ("Q1".equals(drug.getQuestion()) &&
                    drug.getAnswer() != null &&
                    !drug.getAnswer().isEmpty()) {
                answeredSubQuestions.add(drug.getSubquestion());
            }
        }

        return answeredSubQuestions.size() == expectedSubQuestions.length;
    }
    private boolean needsFollowUpQuestions(List<DrugsInfo> drugsInfos) {
        if (drugsInfos == null || drugsInfos.isEmpty()) {
            return false;
        }

        // ตรวจสอบ Q1 ว่ามีการตอบไม่ใช่ "0" หรือไม่
        for (DrugsInfo drug : drugsInfos) {
            if ("Q1".equals(drug.getQuestion()) &&
                    drug.getAnswer() != null &&
                    !"0".equals(drug.getAnswer()) &&
                    !drug.getAnswer().isEmpty()) {
                return true; // มีการใช้สารอย่างน้อย 1 ชนิด
            }
        }

        return false; // ไม่เคยใช้สารใดๆ
    }
    // ตรวจสอบการสูบบุหรี่
    private boolean isSmokingAssessmentComplete(SmokerInfo smokerInfo) {
        return smokerInfo.getSmokerRegularly() != null && !smokerInfo.getSmokerRegularly().equals("0") &&
                smokerInfo.getSmokerAssist() != null && !smokerInfo.getSmokerAssist().equals("0") &&
                smokerInfo.getSmokerGroup() != null && !smokerInfo.getSmokerGroup().equals("0");

        // เพิ่มเงื่อนไขตามที่จำเป็น
    }

    // ตรวจสอบการดื่มสุรา
    private boolean isAlcoholAssessmentComplete(DrinkingInfo drinkingInfo) {
        return drinkingInfo.getDrinking() != null && !drinkingInfo.getDrinking().equals("0") &&
                drinkingInfo.getDrinkingAlway() != null && !drinkingInfo.getDrinkingAlway().equals("0") &&
                drinkingInfo.getDrinkingFrequency() != null && !drinkingInfo.getDrinkingFrequency().equals("0");
        // เพิ่มเงื่อนไขตามที่จำเป็น
    }
    private boolean isNicotineComplete(NicotineInfo nicotineInfo) {
        return nicotineInfo.getNicotine1() != null && !nicotineInfo.getNicotine1().equals("0") &&
                nicotineInfo.getNicotine2() != null && !nicotineInfo.getNicotine2().equals("0") &&
                nicotineInfo.getNicotine3() != null && !nicotineInfo.getNicotine3().equals("0") &&
                nicotineInfo.getNicotine4() != null && !nicotineInfo.getNicotine4().equals("0") &&
                nicotineInfo.getNicotine5() != null && !nicotineInfo.getNicotine5().equals("0") &&
                nicotineInfo.getNicotine6() != null && !nicotineInfo.getNicotine6().equals("0")
                ;
    }
    // ตรวจสอบ ST-5
    private boolean isSt5AssessmentComplete(StressDepressionInfo stressDepressionInfo) {
        return stressDepressionInfo.getQ1() != null && !stressDepressionInfo.getQ1().equals("0") &&
                stressDepressionInfo.getQ2() != null && !stressDepressionInfo.getQ2().equals("0") &&
                stressDepressionInfo.getQ3() != null && !stressDepressionInfo.getQ3().equals("0") &&
                stressDepressionInfo.getQ4() != null && !stressDepressionInfo.getQ4().equals("0") &&
                stressDepressionInfo.getQ5() != null && !stressDepressionInfo.getQ5().equals("0");
    }

    // ตรวจสอบ 2Q
    private boolean is2qAssessmentComplete(StressDepression2qInfo depression2qInfo) {
        return depression2qInfo.getQ1() != null && !depression2qInfo.getQ1().equals("0") &&
                depression2qInfo.getQ2() != null && !depression2qInfo.getQ2().equals("0");
    }

    // ตรวจสอบ 9Q
    private boolean is9qAssessmentComplete(StressDepression9qInfo depression9qInfo) {
        return depression9qInfo.getQ1() != null && !depression9qInfo.getQ1().equals("0") &&
                depression9qInfo.getQ2() != null && !depression9qInfo.getQ2().equals("0") &&
                depression9qInfo.getQ3() != null && !depression9qInfo.getQ3().equals("0") &&
                depression9qInfo.getQ4() != null && !depression9qInfo.getQ4().equals("0") &&
                depression9qInfo.getQ5() != null && !depression9qInfo.getQ5().equals("0") &&
                depression9qInfo.getQ6() != null && !depression9qInfo.getQ6().equals("0") &&
                depression9qInfo.getQ7() != null && !depression9qInfo.getQ7().equals("0") &&
                depression9qInfo.getQ8() != null && !depression9qInfo.getQ8().equals("0") &&
                depression9qInfo.getQ9() != null && !depression9qInfo.getQ9().equals("0")
                 ;
    }

    // ตรวจสอบ 8Q
    private boolean is8qAssessmentComplete(SuicideAssessment8qInfo suicideAssessment8qInfo) {
        return suicideAssessment8qInfo.getQ1() != null && !suicideAssessment8qInfo.getQ1().equals("0") &&
                suicideAssessment8qInfo.getQ2() != null && !suicideAssessment8qInfo.getQ2().equals("0") &&
                suicideAssessment8qInfo.getQ3() != null && !suicideAssessment8qInfo.getQ3().equals("0") &&
                suicideAssessment8qInfo.getQ3_2_1() != null && !suicideAssessment8qInfo.getQ3_2_1().equals("0") &&
                suicideAssessment8qInfo.getQ4() != null && !suicideAssessment8qInfo.getQ4().equals("0") &&
                suicideAssessment8qInfo.getQ5() != null && !suicideAssessment8qInfo.getQ5().equals("0") &&
                suicideAssessment8qInfo.getQ6() != null && !suicideAssessment8qInfo.getQ6().equals("0") &&
                suicideAssessment8qInfo.getQ7() != null && !suicideAssessment8qInfo.getQ7().equals("0") &&
                suicideAssessment8qInfo.getQ8() != null && !suicideAssessment8qInfo.getQ8().equals("0")
                ;
    }

    // ตรวจสอบการให้คำปรึกษา
    private boolean isCounselingComplete(CounselingInfo counselingInfo) {
         Boolean isComplete = false;
         if( counselingInfo == null) return false;
         if(counselingInfo.getCounselingType() !=null && counselingInfo.getCounselingType() == 1) // detail
         {
             isComplete = counselingInfo.getDetail()!=null && !counselingInfo.getDetail().isEmpty();
         }
         else if( counselingInfo.getCounselingType() !=null && counselingInfo.getCounselingType() == 2) // referral
         {
             isComplete = counselingInfo.getReferralDetail()!=null && !counselingInfo.getReferralDetail().isEmpty();
         }
         if(counselingInfo.getProviderSignature()!=null && counselingInfo.getProviderSignature().length>0)
         {
             isComplete = true;
         } else {
             isComplete = false;
             return isComplete;
         }
         if(counselingInfo.getPatientSignature()!=null && counselingInfo.getPatientSignature().length>0)
         {
             isComplete = true;
         } else {
             isComplete = false;
             return isComplete;
         }
         return isComplete;
    }
    // ตรวจสอบความสมบูรณ์ของข้อมูลพื้นฐาน
    private boolean isBasicInfoComplete(PersonInfo person) {
        return person.getIdcard() != null && !person.getIdcard().isEmpty() &&
                person.getFname() != null && !person.getFname().isEmpty() &&
                person.getLname() != null && !person.getLname().isEmpty() &&
                person.getBirthday() != null && !person.getBirthday().isEmpty() &&
                person.getGender() != null && !person.getGender().isEmpty() &&
                person.getWeight() > 0 &&
                person.getHeight() > 0;
    }

    // ตรวจสอบความสมบูรณ์ของ Health Risk Assessment
    private boolean isHealthRiskComplete(HealthRiskAssessmentInfo healthRisk) {
        return healthRisk.getHealthRiskQ1() != null && !healthRisk.getHealthRiskQ1().equals("0") &&
                healthRisk.getHealthRiskQ2() != null && !healthRisk.getHealthRiskQ2().equals("0") &&
                healthRisk.getHealthRiskQ3() != null && !healthRisk.getHealthRiskQ3().equals("0") &&
                healthRisk.getHealthRiskQ4() != null && !healthRisk.getHealthRiskQ4().equals("0") &&
                healthRisk.getHealthRiskQ5() != null && !healthRisk.getHealthRiskQ5().equals("0") &&
                healthRisk.getHealthRiskQ6() != null && !healthRisk.getHealthRiskQ6().equals("0");
    }

    // ตรวจสอบความสมบูรณ์ของ Cardiovascular Risk
    private boolean isCardiovascularRiskComplete(CardiovascularRiskInfo cardioRisk) {
        // ตรวจสอบข้อมูลที่จำเป็นสำหรับ cardiovascular risk
        return cardioRisk.getCholesterol() != null && !cardioRisk.getCholesterol().isEmpty();
    }

    // Class สำหรับเก็บผลการตรวจสอบ
    private static class DataCompletionStatus {
        private boolean isComplete;
        private String message;

        public DataCompletionStatus(boolean isComplete, String message) {
            this.isComplete = isComplete;
            this.message = message;
        }

        public boolean isComplete() { return isComplete; }
        public String getMessage() { return message; }
    }
}
