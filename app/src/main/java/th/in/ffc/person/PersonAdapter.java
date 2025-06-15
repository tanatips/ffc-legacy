package th.in.ffc.person;

import static androidx.core.content.ContextCompat.startActivity;

import static th.in.ffc.util.Log.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import th.in.ffc.R;
import th.in.ffc.api.nhso.FSDataResponse;
import th.in.ffc.api.nhso.NHSOFSDataApiCaller;
import th.in.ffc.api.nhso.NhsoApiCaller;
import th.in.ffc.app.form.nhso.dao.NHSOCHADao;
import th.in.ffc.app.form.nhso.dao.NHSOOPDDao;
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
import th.in.ffc.app.form.screening.dao.SfCardiovascularRiskInfoDao;
import th.in.ffc.app.form.screening.dao.SfHealthRiskAssessmentInfoDao;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.dao.SfTokenDao;
import th.in.ffc.app.form.screening.model.CardiovascularRiskInfo;
import th.in.ffc.app.form.screening.model.HealthRiskAssessmentInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.model.SfToken;
import th.in.ffc.dao.VisitDao;
import th.in.ffc.security.LoginActivity;
import th.in.ffc.session.UserSessionManager;
import th.in.ffc.util.AgeCalculator;
import th.in.ffc.util.DateTime;
import th.in.ffc.util.InvoiceNumberGenerator;
import th.in.ffc.util.Log;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {
    private List<PersonInfo> personList;
    private OnItemClickListener listener;
    private OnButtonClickListener buttonListener; // เพิ่ม listener สำหรับปุ่ม Submit
    boolean isButtonClicked = false;
    private SimpleDateFormat dateFormat;
    private Context mContext;
    public static final String EXTRA_PCUCODE = "pcucode";
    PersonInfo personInfo;
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
        holder.tvIdcard.setText("เลขบัตรประชาชน: " + person.getIdcard());
        holder.tvBirthday.setText("วันเกิด: " + person.getBirthday());
        holder.tvPhone.setText("โทรศัพท์: " + person.getPhone());
        holder.tvPersonId.setText(person.getId());
        if(person.getSend_to_claim().equals(1)){
            holder.btnSubmitClaim.setText("ส่งข้อมูลเรียบร้อย");
            holder.btnSubmitClaim.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvIdcard, tvBirthday, tvPhone, tvPersonId;
        Button btnSubmitClaim;
        boolean isButtonClicked = false;

        PersonViewHolder(View itemView) {
            super(itemView);
            tvPersonId = itemView.findViewById(R.id.tvPersonId);
            tvName = itemView.findViewById(R.id.tvName);
            tvIdcard = itemView.findViewById(R.id.tvIdcard);
            tvBirthday = itemView.findViewById(R.id.tvBirthday);
            tvPhone = itemView.findViewById(R.id.tvPhone);
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

                            // แฟ้ม 5
                            NHSODiagnosisInfo nhsoDiagnosisInfo = new NHSODiagnosisInfo();
                            List<NHSODiagnosisInfo> nhsoDiagnosisInfos = new ArrayList<>();
                            NHSODiagnosisService nhsoDiagnosisService = new NHSODiagnosisService(itemView.getContext());
                            nhsoDiagnosisInfo.setSeq(seq);
                            nhsoDiagnosisInfo.setDiag("E119"); // E119
                            nhsoDiagnosisInfo.setDiagType("1"); // 1

                            try {
                                nhsoDiagnosisInfo.setDateDx(dateFormat.parse(personInfo.getCreated_date()));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            nhsoDiagnosisInfos.add(nhsoDiagnosisInfo);
                            nhsoDiagnosisService.createDiagnosis(nhsoDiagnosisInfo,userSessionManager.getUser());
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
                                fpg = Double.parseDouble(healthRiskAssessmentInfos.get(0).getFpg());
                            }
                            if(!cardiovascularRiskInfos.isEmpty())
                            {
                                choresteral = Double.parseDouble(cardiovascularRiskInfos.get(0).getCholesterol()!=null?cardiovascularRiskInfos.get(0).getCholesterol():"0");
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
}
