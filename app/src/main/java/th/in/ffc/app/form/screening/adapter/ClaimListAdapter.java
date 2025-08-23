package th.in.ffc.app.form.screening.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.util.Log;

import th.in.ffc.R;
import th.in.ffc.api.nhso.StatusTrackApiCaller;
import th.in.ffc.api.nhso.StatusTrackResponse;
import th.in.ffc.api.nhso.StatusTracksV2ApiCaller;
import th.in.ffc.api.nhso.StatusTracksV2Response;
import th.in.ffc.app.form.nhso.dao.NHSOCHADao;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.model.ClaimInfo;

/**
 * Adapter สำหรับแสดงรายการ claim ใน RecyclerView
 */
public class ClaimListAdapter extends RecyclerView.Adapter<ClaimListAdapter.ClaimViewHolder> {

    private static final String TAG = "ClaimListAdapter";
    private Context mContext;
    private List<ClaimInfo> mClaimList;
    private OnClaimClickListener mListener;
    private SimpleDateFormat displayFormat;
    private DecimalFormat decimalFormat;
    private StatusTrackApiCaller statusTrackApiCaller;
    private StatusTracksV2ApiCaller statusTracksV2ApiCaller;
    private NHSOCHADao nhsochaDao;

    public ClaimListAdapter(Context context, List<ClaimInfo> claimList) {
        this.mContext = context;
        this.mClaimList = claimList;
        this.displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        this.decimalFormat = new DecimalFormat("#,##0.00");
        this.statusTrackApiCaller = new StatusTrackApiCaller(context);
        this.statusTracksV2ApiCaller = new StatusTracksV2ApiCaller(context);
        this.nhsochaDao = new NHSOCHADao(context);
    }

    public interface OnClaimClickListener {
        void onViewDetailsClick(ClaimInfo claim, int position);
        void onStatusCheckComplete(ClaimInfo claim, List<StatusTrackResponse> responses);
        void onClaimStatusUpdated(ClaimInfo claim, boolean success);
    }

    public void setOnClaimClickListener(OnClaimClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ClaimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_claim, parent, false);
        return new ClaimViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClaimViewHolder holder, int position) {
        ClaimInfo claim = mClaimList.get(position);

        // แสดงข้อมูลบุคคล
        holder.tvPatientName.setText(claim.getPatientName());
        holder.tvIdCard.setText("เลขบัตรประชาชน: " + formatIdCard(claim.getIdCard()));

        // ดึงจำนวนเงินการเบิกจาก NHSOCHADao โดยใช้ visitId (seq)
        String visitNo = claim.getVisitNo();
        String seq = claim.getSeq();
        double claimAmount = 0.0;

        if (seq != null && !seq.isEmpty()) {
            // ดึงจำนวนเงินจาก NHSOCHADao โดยใช้ seq (visitId)
            claimAmount = nhsochaDao.getTotalClaimAmountBySeq(seq);

            // กรณีไม่พบข้อมูล หรือเป็น 0 ให้ลองดึงจาก total
            if (claimAmount == 0.0) {
                claimAmount = nhsochaDao.getTotalSumBySeq(seq);
            }

            // ถ้ายังไม่พบข้อมูล ใช้ค่าจาก claim.getAmount()
            if (claimAmount == 0.0 && claim.getAmount() > 0) {
                claimAmount = claim.getAmount();
            }

            // อัพเดตค่า amount ในข้อมูล claim ด้วย
            claim.setAmount(claimAmount);
        } else if (claim.getAmount() > 0) {
            // กรณีไม่มี visitId แต่มีการกำหนดค่า amount ไว้แล้ว
            claimAmount = claim.getAmount();
        }
        // แสดงข้อมูลการเบิก
        String formattedAmount = decimalFormat.format(claimAmount);
        holder.tvClaimAmount.setText(formattedAmount + " บาท");


        // แสดงวันที่รับบริการ/วันที่ทำรายการ
//        String serviceDate = (claim.getServiceDate() != null) ? formatDate(claim.getServiceDate()) : "-";
//        String claimDate = (claim.getClaimDate() != null) ? formatDate(claim.getClaimDate()) : "-";
        String serviceDate = (claim.getServiceDate() != null) ? claim.getServiceDate() : "-";
        String claimDate = (claim.getClaimDate() != null) ? claim.getClaimDate() : "-";

        holder.tvServiceDate.setText("วันที่รับบริการ: " + serviceDate);
        holder.tvClaimDate.setText("วันที่ทำรายการ: " + claimDate);

        // แสดงสถานะ
        String status = (claim.getClaimStatus() != null) ? claim.getClaimStatus() : "รอดำเนินการ";
        holder.tvClaimStatus.setText("สถานะ: " + status);

        // กำหนดสีตามสถานะ
        int textColor;
        if ("อนุมัติ".equals(status)) {
            textColor = mContext.getResources().getColor(android.R.color.holo_green_dark);
        } else if ("ไม่อนุมัติ".equals(status)) {
            textColor = mContext.getResources().getColor(android.R.color.holo_red_dark);
        } else {
            textColor = mContext.getResources().getColor(android.R.color.holo_orange_dark);
        }
        holder.tvClaimStatus.setTextColor(textColor);

        // กำหนด event listeners
        holder.btnViewDetails.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onViewDetailsClick(claim, position);
            }
        });

        holder.btnCheckStatus.setOnClickListener(v -> {
            checkClaimStatus(claim, position);
        });
    }

    /**
     * ส่งข้อมูลเพื่อตรวจสอบสถานะการเบิก
     */
    private void checkClaimStatus(ClaimInfo claim, int position) {
        // แสดง Progress Dialog
        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("กำลังตรวจสอบสถานะการเบิก...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            JSONObject statusTrackData = StatusTrackApiCaller.createStatusTrackData(
                    claim.getClaimId(), // ใช้ ID ของ claim เป็น id
                    claim.getSeq()
            );

            // เรียกใช้ API
            statusTrackApiCaller.sendStatusTrackData(statusTrackData, new StatusTrackApiCaller.StatusTrackApiCallback() {
                @Override
                public void onSuccess(String response) {
                    // ไม่ต้องทำอะไร จะใช้ method ด้านล่างแทน
                }

                @Override
                public void onSuccess(List<StatusTrackResponse> responses) {
                    // ส่งต่อข้อมูลให้ listener
                    if (mListener != null) {
                        mListener.onStatusCheckComplete(claim, responses);
                    }

                    // ตรวจสอบว่ามี response และมี uuid
                    if (responses != null && !responses.isEmpty()) {
                        StatusTrackResponse firstResponse = responses.get(0);
                        String uuid = firstResponse.getUid();

                        if (uuid != null && !uuid.isEmpty()) {
                            // เรียกใช้ StatusTracksV2ApiCaller ด้วย uuid
                            checkStatusTracksV2(claim, uuid, firstResponse, position, progressDialog);
                        } else {
                            // ปิด Progress Dialog และแสดงผลแบบเดิม
                            progressDialog.dismiss();
                            showResponseDialog(claim, firstResponse, position);
                        }
                    } else {
                        // ปิด Progress Dialog และแสดงผลแบบเดิม
                        progressDialog.dismiss();
                        showResponseDialog(claim, null, position);
                    }
                }

                @Override
                public void onError(String errorMessage, Exception e) {
                    // ปิด Progress Dialog
                    progressDialog.dismiss();

                    // แสดงข้อความผิดพลาดในรูปแบบ Dialog
                    showErrorDialog(errorMessage, e);
                }
            });
        } catch (Exception e) {
            // ปิด Progress Dialog
            progressDialog.dismiss();

            // แสดงข้อความผิดพลาดในรูปแบบ Dialog
            showErrorDialog("เกิดข้อผิดพลาดในการตรวจสอบสถานะ", e);
        }
    }

    /**
     * เรียกใช้ StatusTracksV2ApiCaller เพื่อดูผลการส่งเบิก
     */

    private void checkStatusTracksV2(ClaimInfo claim, String uuid, StatusTrackResponse originalResponse,
                                     int position, ProgressDialog progressDialog) {
        try {
            // อัพเดตข้อความ Progress Dialog
            progressDialog.setMessage("กำลังตรวจสอบผลการส่งเบิก...");

            // สร้าง JSON Object สำหรับ track data
            JSONObject trackData = StatusTracksV2ApiCaller.createTrackData(uuid);

            if (trackData == null) {
                // ปิด Progress Dialog
                progressDialog.dismiss();
                Log.e(TAG, "Failed to create track data for uuid: " + uuid);
                showResponseDialog(claim, originalResponse, position);
                return;
            }

            statusTracksV2ApiCaller.sendTrackData(trackData, new StatusTracksV2ApiCaller.StatusTracksV2ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    // ไม่ต้องทำอะไร จะใช้ method ด้านล่างแทน
                }

                @Override
                public void onSuccess(List<StatusTracksV2Response> responses) {
                    // ปิด Progress Dialog
                    progressDialog.dismiss();

                    // แสดงผลการเบิกที่ได้จาก V2 API
                    if (responses != null && !responses.isEmpty()) {
                        StatusTracksV2Response firstResponse = responses.get(0);
                        showClaimResultDialog(claim, originalResponse, firstResponse, position);
                    } else {
                        Log.w(TAG, "StatusTracksV2 API returned empty response");
                        showResponseDialog(claim, originalResponse, position);
                    }
                }

                @Override
                public void onError(String errorMessage, Exception e) {
                    // ปิด Progress Dialog
                    progressDialog.dismiss();

                    Log.w(TAG, "StatusTracksV2 API failed, falling back to original response: " + errorMessage);

                    // แสดงผลแบบเดิมถ้า V2 API ไม่สำเร็จ
                    showResponseDialog(claim, originalResponse, position);
                }
            });
        } catch (Exception e) {
            // ปิด Progress Dialog
            progressDialog.dismiss();

            Log.e(TAG, "Error calling StatusTracksV2 API", e);

            // แสดงผลแบบเดิมถ้าเกิดข้อผิดพลาด
            showResponseDialog(claim, originalResponse, position);
        }
    }
    /**
     * แสดงผลการเบิกจาก StatusTracksV2ApiCaller
     */
    private void showClaimResultDialog(ClaimInfo claim, StatusTrackResponse originalResponse,
                                       StatusTracksV2Response v2Response, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("ผลการตรวจสอบสถานะการเบิก");

        // สร้างข้อความสำหรับแสดงใน Dialog
        StringBuilder content = new StringBuilder();
        content.append("ผู้ป่วย: ").append(claim.getPatientName()).append("\n\n");

        // แสดงรายละเอียดจาก original response
        if (originalResponse != null) {
            content.append("รหัสอ้างอิง (ID): ").append(originalResponse.getId()).append("\n");
            content.append("UID: ").append(originalResponse.getUid()).append("\n");
            content.append("SEQ: ").append(originalResponse.getSeq()).append("\n");

            if (originalResponse.getHcode() != null && !originalResponse.getHcode().isEmpty()) {
                content.append("รหัสสถานพยาบาล: ").append(originalResponse.getHcode()).append("\n");
            }

            if (originalResponse.getHn() != null && !originalResponse.getHn().isEmpty()) {
                content.append("HN: ").append(originalResponse.getHn()).append("\n");
            }

            if (originalResponse.getRecordStatus() != null && !originalResponse.getRecordStatus().isEmpty()) {
                content.append("สถานะการบันทึก: ").append(originalResponse.getRecordStatus()).append("\n");
            }

            content.append("UUID: ").append(originalResponse.getUid()).append("\n\n");
        }

        // แสดงผลการเบิกจาก V2 API
        String claimResult = "";
        boolean isSuccess = false;

        if (v2Response != null && v2Response.getMessage() != null) {
            claimResult = v2Response.getMessage();
            content.append("ผลการเบิก: ").append(claimResult).append("\n");

            // ตรวจสอบสถานะความสำเร็จ
            isSuccess = "SUCCESS".equalsIgnoreCase(claimResult) ||
                    "APPROVED".equalsIgnoreCase(claimResult) ||
                    claimResult.contains("อนุมัติ");
        } else {
            // ใช้ข้อมูลจาก original response ถ้าไม่มีข้อมูลจาก V2
            if (originalResponse != null) {
                claimResult = originalResponse.getMessage();
                content.append("ผลการตรวจสอบ: ").append(claimResult);
                isSuccess = "SUCCESS".equals(claimResult);
            } else {
                content.append("ไม่พบข้อมูลผลการเบิก");
            }
        }

        builder.setMessage(content.toString());

        // กำหนดไอคอนและสีตามผลการเบิก
        if (isSuccess) {
            builder.setIcon(android.R.drawable.ic_dialog_info);

            // อัปเดตสถานะและบันทึกลงฐานข้อมูล
            claim.setClaimStatus("อนุมัติ");
            updateClaimStatusInDatabase(claim, originalResponse);
            notifyItemChanged(position);
        } else {
            builder.setIcon(android.R.drawable.ic_dialog_alert);
        }

        // เพิ่มปุ่มตกลง
        builder.setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());

        // แสดง Dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * แสดงข้อความผิดพลาดในรูปแบบ Dialog
     */
    private void showErrorDialog(String errorMessage, Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("เกิดข้อผิดพลาด");

        String detailMessage = e != null ? e.getMessage() : "";
        String fullMessage = errorMessage;
        if (detailMessage != null && !detailMessage.isEmpty()) {
            fullMessage += "\n\nรายละเอียด: " + detailMessage;
        }

        builder.setMessage(fullMessage);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());

        // แสดง Dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /**
     * แสดงข้อมูลตอบกลับจาก API ในรูปแบบ Dialog (ใช้เมื่อไม่มี V2 API)
     */
    private void showResponseDialog(ClaimInfo claim, StatusTrackResponse response, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("ผลการตรวจสอบสถานะ");

        if (response != null) {
            String message = response.getMessage();

            // สร้างข้อความสำหรับแสดงใน Dialog
            StringBuilder content = new StringBuilder();
            content.append("ผู้ป่วย: ").append(claim.getPatientName()).append("\n\n");

            // แสดงรายละเอียดเพิ่มเติมจาก response
            content.append("รหัสอ้างอิง (ID): ").append(response.getId()).append("\n");
            content.append("UID: ").append(response.getUid()).append("\n");
            content.append("SEQ: ").append(response.getSeq()).append("\n");

            if (response.getHcode() != null && !response.getHcode().isEmpty()) {
                content.append("รหัสสถานพยาบาล: ").append(response.getHcode()).append("\n");
            }

            if (response.getHn() != null && !response.getHn().isEmpty()) {
                content.append("HN: ").append(response.getHn()).append("\n");
            }

            if (response.getRecordStatus() != null && !response.getRecordStatus().isEmpty()) {
                content.append("สถานะการบันทึก: ").append(response.getRecordStatus()).append("\n");
            }

            content.append("\nผลการตรวจสอบ: ").append(message);

            builder.setMessage(content.toString());

            // กำหนดสีปุ่มตามสถานะ
            if ("SUCCESS".equals(message)) {
                builder.setIcon(android.R.drawable.ic_dialog_info);

                // อัปเดตสถานะและบันทึกลงฐานข้อมูล
                claim.setClaimStatus("อนุมัติ");
                updateClaimStatusInDatabase(claim, response);
                notifyItemChanged(position);
            } else {
                builder.setIcon(android.R.drawable.ic_dialog_alert);
            }

            // เพิ่มปุ่มตกลง
            builder.setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());
        } else {
            builder.setMessage("ไม่พบข้อมูลสถานะการเบิก");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());
        }

        // แสดง Dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    /**
     * อัพเดตสถานะการเบิกในฐานข้อมูล
     * @param claim ข้อมูล claim
     * @param response ข้อมูลการตอบกลับจาก API
     */
    private void updateClaimStatusInDatabase(ClaimInfo claim, StatusTrackResponse response) {
        try {
            // ใช้ ID ของ claim
            String personId = String.valueOf(claim.getId());

            // กำหนดค่าสถานะ
            String claimStatus = "อนุมัติ";
            String claimMessage = response.getMessage();
            String claimDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String visitId = response.getSeq(); // ใช้ uid จาก response เป็น visitId
            String seq = response.getSeq(); // ใช้ seq จาก response เป็น visitId
            String claimId = String.valueOf(response.getId()); // ใช้ seq จาก response เป็น claim_id

            // บันทึก visitId ไว้ในข้อมูล claim
            claim.setVisitNo(visitId);

            // อัปเดตข้อมูลในฐานข้อมูล - ใช้เมธอด static
            long result = SfPersonInfoDao.updateClaimInfo(
                    personId,
                    claimId,
                    claimStatus,
                    claimMessage,
                    claimDate,
                    visitId
            );

            // ดึงข้อมูลจำนวนเงินจาก NHSOCHADao หลังจากได้รับ visitId
            if (seq != null && !seq.isEmpty()) {
                double amount = nhsochaDao.getTotalAmountBySeq(seq);
                if (amount > 0) {
                    // อัพเดตจำนวนเงินในข้อมูล claim
                    claim.setAmount(amount);
                    Log.d(TAG, "Updated claim amount from NHSOCHA: " + amount + " for seq: " + seq);
                } else {
                    // ถ้าไม่พบข้อมูลใน amount ให้ลองดูที่ total
                    amount = nhsochaDao.getTotalSumBySeq(seq);
                    if (amount > 0) {
                        claim.setAmount(amount);
                        Log.d(TAG, "Updated claim amount from NHSOCHA (total): " + amount + " for seq: " + seq);
                    }
                }
            }

            // แจ้งผลการอัปเดตกลับไปยัง listener
            boolean success = (result > 0);
            if (mListener != null) {
                mListener.onClaimStatusUpdated(claim, success);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error updating claim status in database", e);
            if (mListener != null) {
                mListener.onClaimStatusUpdated(claim, false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mClaimList.size();
    }

    /**
     * อัปเดตข้อมูลรายการ
     * @param newClaims รายการใหม่
     */
    public void updateData(List<ClaimInfo> newClaims) {
        this.mClaimList = newClaims;
        notifyDataSetChanged();
    }

    /**
     * จัดรูปแบบเลขบัตรประชาชน (X-XXXX-XXXXX-XX-X)
     */
    private String formatIdCard(String idcard) {
        if (idcard == null || idcard.length() != 13) {
            return idcard;
        }
        return idcard.substring(0, 1) + "-" +
                idcard.substring(1, 5) + "-" +
                idcard.substring(5, 10) + "-" +
                idcard.substring(10, 12) + "-" +
                idcard.substring(12);
    }

    /**
     * จัดรูปแบบวันที่
     */
    private String formatDate(String dateStr) {
        try {
            // จัดการกับรูปแบบวันที่ที่อาจมีหลายรูปแบบ
            SimpleDateFormat[] formats = {
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            };

            Date date = null;
            for (SimpleDateFormat format : formats) {
                try {
                    date = format.parse(dateStr);
                    if (date != null) break;
                } catch (Exception ignored) {}
            }

            if (date != null) {
                return displayFormat.format(date);
            }
        } catch (Exception e) {
            // กรณีมีข้อผิดพลาด ให้แสดงข้อมูลเดิม
        }

        return dateStr;
    }

    /**
     * ViewHolder สำหรับรายการ claim
     */
    static class ClaimViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName;
        TextView tvIdCard;
        TextView tvServiceDate;
        TextView tvClaimDate;
        TextView tvClaimStatus;
        TextView tvClaimAmount;
        Button btnViewDetails;
        Button btnCheckStatus; // เปลี่ยนชื่อจาก btnPrint เป็น btnCheckStatus

        ClaimViewHolder(View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvIdCard = itemView.findViewById(R.id.tvIdCard);
            tvServiceDate = itemView.findViewById(R.id.tvServiceDate);
            tvClaimDate = itemView.findViewById(R.id.tvClaimDate);
            tvClaimStatus = itemView.findViewById(R.id.tvClaimStatus);
            tvClaimAmount = itemView.findViewById(R.id.tvClaimAmount);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            btnCheckStatus = itemView.findViewById(R.id.btnCheckStatus); // เปลี่ยนตาม ID ที่แก้ไขในไฟล์ XML
        }
    }
}