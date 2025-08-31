package th.in.ffc.person;

import static th.in.ffc.util.Log.TAG;

import android.content.Context;
import android.content.Intent;
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
import th.in.ffc.service.ClaimSubmissionService;
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
import th.in.ffc.app.form.screening.ClaimListActivity;
import th.in.ffc.service.ScreeningCompletionHandler;
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
    private ClaimSubmissionService claimSubmissionService;



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

        this.claimSubmissionService = new ClaimSubmissionService(parent.getContext());

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
        if (person.getVisitNo() != null && !person.getVisitNo().isEmpty()) {
            holder.tvVisitNumber.setText("หมายเลขการเข้ารับบริการ: " + person.getVisitNo());
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

            holder.btnViewClaimList.setVisibility(View.VISIBLE);

            holder.tvDataStatus.setText("✅ ส่งข้อมูลแล้ว");
            holder.tvDataStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
        }
        else {
            DataCompletionStatus status = checkDataCompleteness(person, holder.itemView.getContext());
            holder.btnViewClaimList.setVisibility(View.GONE);
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
        Button btnSubmitClaim, btnViewClaimList;
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
            btnViewClaimList = itemView.findViewById(R.id.btnViewClaimList); // เพิ่มการเชื่อม View

            // ตั้งค่า Click Listener สำหรับปุ่มดูรายการเบิก
            btnViewClaimList.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), ClaimListActivity.class);
                itemView.getContext().startActivity(intent);
            });
            btnSubmitClaim.setOnClickListener(v -> {
                if (listener != null && getBindingAdapterPosition() != RecyclerView.NO_POSITION) {
                    isButtonClicked = true;
                    mContext = itemView.getContext();
                    int position = getBindingAdapterPosition();
                    PersonInfo person = personList.get(position);

                    btnSubmitClaim.setEnabled(false);
                    btnSubmitClaim.setText("กำลังส่ง...");
                    claimSubmissionService.setClaimSubmissionListener(new ClaimSubmissionService.ClaimSubmissionListener() {
                        @Override
                        public void onClaimSubmissionSuccess(String message, String seqNo) {
                            // อัพเดท UI เมื่อสำเร็จ
                            btnSubmitClaim.setText("ส่งข้อมูลเรียบร้อย");
                            btnSubmitClaim.setEnabled(false);
                            btnViewClaimList.setVisibility(View.VISIBLE);

                            tvDataStatus.setText("✅ ส่งข้อมูลแล้ว");
                            tvDataStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark));

                            // แสดง Toast
                            Toast.makeText(itemView.getContext(), message, Toast.LENGTH_LONG).show();

                            // อัพเดท adapter
                            notifyItemChanged(position);

                            // เรียก listener หลัก (ถ้ามี)
                            if (listener != null) {
                                listener.onItemClick(person, isButtonClicked);
                            }
                        }

                        @Override
                        public void onClaimSubmissionError(String errorMessage) {
                            // อัพเดท UI เมื่อเกิดข้อผิดพลาด
                            btnSubmitClaim.setText("ส่งข้อมูล");
                            btnSubmitClaim.setEnabled(true);

                            tvDataStatus.setText("❌ ส่งข้อมูลไม่สำเร็จ");
                            tvDataStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark));

                            // แสดง Toast
                            Toast.makeText(itemView.getContext(), "เกิดข้อผิดพลาด: " + errorMessage, Toast.LENGTH_LONG).show();

                            // อัพเดท adapter
                            notifyItemChanged(position);
                        }

                        @Override
                        public void onClaimSubmissionProgress(String message) {
                            // อัพเดท UI แสดงความคืบหน้า
                            tvDataStatus.setText("⏳ " + message);
                            tvDataStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_blue_dark));
                        }
                    });

                    // เริ่มการส่งเคลม
                    claimSubmissionService.submitClaim(person);

                    isButtonClicked = false;

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


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    private DataCompletionStatus checkDataCompleteness(PersonInfo person, Context context) {
        if (context == null) return new DataCompletionStatus(false, "ไม่สามารถตรวจสอบข้อมูลได้");

        try {
            // สร้าง ScreeningCompletionHandler
            ScreeningCompletionHandler handler = new ScreeningCompletionHandler(context, person);

            // ใช้ method validateScreeningData เพื่อตรวจสอบความสมบูรณ์
            boolean isComplete = handler.validateScreeningData();

            // ใช้ method getScreeningStatusMessage เพื่อได้รับข้อความสถานะ
            String message = handler.getScreeningStatusMessage();

            return new DataCompletionStatus(isComplete, message);

        } catch (Exception e) {
            Log.e("PersonAdapter", "Error checking data completeness using ScreeningCompletionHandler: " + e.getMessage());
            return new DataCompletionStatus(false, "เกิดข้อผิดพลาดในการตรวจสอบข้อมูล");
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
