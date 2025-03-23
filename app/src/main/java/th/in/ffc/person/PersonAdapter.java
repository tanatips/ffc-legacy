package th.in.ffc.person;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import th.in.ffc.R;
import th.in.ffc.api.nhso.NhsoApiCaller;
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
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.dao.SfTokenDao;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.model.SfToken;
import th.in.ffc.dao.VisitDao;
import th.in.ffc.session.UserSessionManager;
import th.in.ffc.util.DateTime;
import th.in.ffc.util.InvoiceNumberGenerator;
import th.in.ffc.util.Log;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {
    private List<PersonInfo> personList;
    private OnItemClickListener listener;
    private OnButtonClickListener buttonListener; // เพิ่ม listener สำหรับปุ่ม Submit
    boolean isButtonClicked = false;
    private SimpleDateFormat dateFormat;
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

                    int position = getBindingAdapterPosition();
                    PersonInfo person = personList.get(position);
                    listener.onItemClick(person, isButtonClicked);

//                    personList.remove(getBindingAdapterPosition());
//                    notifyItemRemoved(getBindingAdapterPosition());

                    notifyItemRangeChanged(getBindingAdapterPosition(), personList.size());
//                    Toast.makeText(itemView.getContext(), tvIdcard.getText().toString(), Toast.LENGTH_LONG ).show();
                    String[] names = tvName.getText().toString().split(" ");
                    List<PersonInfo>  personInfos =  SfPersonInfoDao.getSfPersonInfoById(Integer.valueOf(tvPersonId.getText().toString()));
                    PersonInfo personInfo = new PersonInfo();
                    if(!personInfos.isEmpty()) {
                        personInfo = personInfos.get(0);
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

                    NHSOOPDInfo hnSoOPDInfo = new NHSOOPDInfo();

                    VisitDao visitDao = new VisitDao(itemView.getContext().getContentResolver());

                    long visitId = visitDao.saveNewVisitWithVitalSigns(
                            userSessionManager.getPcuCode(),                     // pcucode
                            userSessionManager.getPcuCode(),                     // pcucodePerson
                            personInfo.getIdcard(),                              // pid
                            personInfo.getCreated_date(),                        // visitDate
                            (float)personInfo.getWeight(),                       // weight
                            (float)personInfo.getHeight(),                       // height
                            personInfo.getBp(),                                  // pressure
                            (float)personInfo.getTemperature(),                  // temperature
                            Integer.valueOf(personInfo.getBp()),                               // pluse
                            (float)personInfo.getWaist_size(),                   // waist
                            String.valueOf(personInfo.getSystolic_pressure()),                 // systolic
                            String.valueOf(personInfo.getDiastolic_pressure()),                // diastolic                               // diagnote
                            userSessionManager.getUsername()                     // username
                    );
                    // แฟ้ม 1
                    patient.setType("CID");
                    patient.setCid(personInfo.getIdcard());
                    patient.setNameGiven(names[0]);
                    patient.setNameFamily(names[1]);
                    patient.setSeq(String.valueOf(visitId));
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
                    hospital.setSeq(String.valueOf(visitId));
                    hospital.setHcode(personInfo.getHcode());
                    nhsoHospitalService.createHospital(hospital, userSessionManager.getUser());

                    // แฟ้ม 3

                    practitioner.setSeq(String.valueOf(visitId));
                    practitioner.setHcode(personInfo.getHcode());
                    practitioner.setCid(personInfo.getIdcard());
                    nhsoPractitionerService.createPractitioner(practitioner,userSessionManager.getUser());

                    // แฟ้ม 4
                    hnSoOPDInfo.setSeq(String.valueOf(visitId));
                    hnSoOPDInfo.setHtype("1");
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
                            Toast.makeText(itemView.getContext(), "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onError(String errorMessage) {

                            Log.e("NHSO", errorMessage);
                            Toast.makeText(itemView.getContext(), "Error:"+errorMessage, Toast.LENGTH_LONG).show();

                        }
                    });
                    // แฟ้ม 5
                    NHSODiagnosisInfo nhsoDiagnosisInfo = new NHSODiagnosisInfo();
                    NHSODiagnosisService nhsoDiagnosisService = new NHSODiagnosisService(itemView.getContext());
                    nhsoDiagnosisInfo.setSeq(String.valueOf(visitId));
                    nhsoDiagnosisInfo.setDiag("E119"); // E119
                    nhsoDiagnosisInfo.setDiagType("1"); // 1
                    try {
                        nhsoDiagnosisInfo.setDateDx(dateFormat.parse(personInfo.getCreated_date()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    nhsoDiagnosisService.createDiagnosis(nhsoDiagnosisInfo,userSessionManager.getUser());
//                    Toast.makeText(itemView.getContext(), "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_LONG).show();

                    // แฟ้ม 7
                    InvoiceNumberGenerator invoiceNumberGenerator =new InvoiceNumberGenerator(itemView.getContext());
                    String invoiceNumber = invoiceNumberGenerator.generateInvoiceNumber();
                    NHSOCHADService chadService = new NHSOCHADService(itemView.getContext());
                    NHSOCHADInfo nhsochadInfo = new NHSOCHADInfo();

                    nhsochadInfo.setSeq(String.valueOf(visitId));
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
                    nhsochadInfo.setUnitprice(300.0);    // ราคาต่อหน่วย
                    nhsochadInfo.setChargeamt(300.0);    // จำนวนเงินเรียกเก็บ

                    // บันทึกข้อมูล
                    chadService.createCHAD(nhsochadInfo, userSessionManager.getUser());

                    NHSOCHAInfo chaInfo = new NHSOCHAInfo();
                    NHSOCHAService chaService = new NHSOCHAService(itemView.getContext());
                    chaInfo.setSeq(String.valueOf(visitId));
                    try {
                        chaInfo.setDate(dateFormat.parse(personInfo.getCreated_date()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    Double amount = 0.0,total=0.0 ,memo = 0.0;
                    chaInfo.setChrgitem("C1");
                    chaInfo.setInvoiceNo(invoiceNumber);
                    chaInfo.setAmount(amount);
                    chaInfo.setTotal(total);
                    chaService.createCHA(chaInfo, userSessionManager.getUser());
                    isButtonClicked = false;
                    Toast.makeText(itemView.getContext(), "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_LONG).show();

                }

//                v.getParent().requestDisallowInterceptTouchEvent(true);
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
