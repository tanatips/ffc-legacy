package th.in.ffc.service;

// ตัวอย่างการสร้าง Dialog สำหรับยืนยันการส่งข้อมูลเบิก
// วางโค้ดนี้ในส่วนที่ตรวจสอบเงื่อนไขการคัดกรองครบถ้วนแล้ว

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.in.ffc.app.form.screening.dao.CounselingSignatureDao;
import th.in.ffc.app.form.screening.dao.SfCardiovascularRiskInfoDao;
import th.in.ffc.app.form.screening.dao.SfDrinkingInfoDao;
import th.in.ffc.app.form.screening.dao.SfDrugsDao;
import th.in.ffc.app.form.screening.dao.SfHealthRiskAssessmentInfoDao;
import th.in.ffc.app.form.screening.dao.SfNicotineInfoDao;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.dao.SfSmokerInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepression2qInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepression9qInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepressionInfoDao;
import th.in.ffc.app.form.screening.dao.SfSuicideAssessment8qInfoDao;
import th.in.ffc.app.form.screening.model.CardiovascularRiskInfo;
import th.in.ffc.app.form.screening.model.CounselingInfo;
import th.in.ffc.app.form.screening.model.DrinkingInfo;
import th.in.ffc.app.form.screening.model.DrugsInfo;
import th.in.ffc.app.form.screening.model.HealthRiskAssessmentInfo;
import th.in.ffc.app.form.screening.model.NicotineInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.app.form.screening.model.StressDepression9qInfo;
import th.in.ffc.app.form.screening.model.StressDepressionInfo;
import th.in.ffc.app.form.screening.model.SuicideAssessment8qInfo;
import th.in.ffc.person.PersonAdapter;
import th.in.ffc.util.AgeCalculator;

public class ScreeningCompletionHandler {

    private Context context;
    private PersonInfo personInfo;
    private ClaimSubmissionService claimService;

    public ScreeningCompletionHandler(Context context, PersonInfo personInfo) {
        this.context = context;
        this.personInfo = personInfo;
        this.claimService = new ClaimSubmissionService(context);

        // ตั้งค่า listener สำหรับรับ callback
        setupClaimListener();
    }

    /**
     * แสดง Dialog ยืนยันการส่งข้อมูลเบิก
     * เรียกใช้เมื่อทำแบบคัดกรองครบถ้วนตามเงื่อนไขแล้ว
     */
    public void showClaimConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("ยืนยันการส่งข้อมูล");
        builder.setMessage("การคัดกรองสำเร็จเรียบร้อยแล้ว\n\nต้องการส่งข้อมูลไปยัง สปสช เพื่อเบิกค่าบริการหรือไม่?");

        // ปุ่มยืนยัน - ส่งข้อมูลเบิก
        builder.setPositiveButton("ส่งข้อมูลเบิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // เรียกใช้ function ส่งข้อมูลเบิกจาก ClaimSubmissionService
                submitClaim();
                dialog.dismiss();
            }
        });

        // ปุ่มยกเลิก
//        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // แสดง Toast แจ้งว่ายกเลิกการส่งข้อมูล
//                Toast.makeText(context, "ยกเลิกการส่งข้อมูลเบิก", Toast.LENGTH_SHORT).show();
//                dialog.dismiss();
//            }
//        });

        // ปุ่ม "ส่งทีหลัง" (ตัวเลือกเพิ่มเติม)
        builder.setNeutralButton("ส่งทีหลัง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "บันทึกข้อมูลแล้ว สามารถส่งเบิกได้ภายหลัง", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        // ตั้งค่าให้ไม่สามารถยกเลิก Dialog ด้วยการกด back หรือนอก Dialog
        builder.setCancelable(false);

        // แสดง Dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * เรียกใช้ ClaimSubmissionService เพื่อส่งข้อมูลเบิก
     */
    private void submitClaim() {
        if (personInfo != null) {
            // แสดง Toast แจ้งว่าเริ่มส่งข้อมูล
            Toast.makeText(context, "เริ่มต้นการส่งข้อมูลเบิก...", Toast.LENGTH_SHORT).show();

            // เรียกใช้ function submitClaim จาก ClaimSubmissionService
            claimService.submitClaim(personInfo);
        } else {
            Toast.makeText(context, "ไม่พบข้อมูลผู้ป่วย กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * ตั้งค่า listener สำหรับรับผลลัพธ์จากการส่งข้อมูลเบิก
     */
    private void setupClaimListener() {
        claimService.setClaimSubmissionListener(new ClaimSubmissionService.ClaimSubmissionListener() {
            @Override
            public void onClaimSubmissionSuccess(String message, String seqNo) {
                // แสดงข้อความเมื่อส่งข้อมูลสำเร็จ
                showSuccessDialog(message, seqNo);
            }

            @Override
            public void onClaimSubmissionError(String errorMessage) {
                // แสดงข้อความเมื่อส่งข้อมูลไม่สำเร็จ
                showErrorDialog(errorMessage);
            }

            @Override
            public void onClaimSubmissionProgress(String message) {
                // แสดงความคืบหน้า (อาจจะใช้ ProgressBar หรือ Toast)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * แสดง Dialog เมื่อส่งข้อมูลสำเร็จ
     */
    private void showSuccessDialog(String message, String seqNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ส่งข้อมูลสำเร็จ");
        builder.setMessage(message + "\n\nหมายเลขอ้างอิง: " + seqNo);
        builder.setIcon(android.R.drawable.ic_dialog_info);

        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // อาจจะเปลี่ยนหน้าหรือทำอะไรต่อหลังจากส่งข้อมูลสำเร็จ
            }
        });

        builder.create().show();
    }

    /**
     * แสดง Dialog เมื่อส่งข้อมูลไม่สำเร็จ
     */
    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ส่งข้อมูลไม่สำเร็จ");
        builder.setMessage("เกิดข้อผิดพลาด: " + errorMessage + "\n\nกรุณาลองใหม่อีกครั้ง");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("ลองใหม่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // เรียกใช้ function ส่งข้อมูลอีกครั้ง
                submitClaim();
            }
        });

        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private DataCompletionStatus checkDataCompleteness() {
        if (context == null || personInfo == null) {
            return new DataCompletionStatus(false, "ไม่สามารถตรวจสอบข้อมูลได้");
        }

        try {
            if (personInfo.getId() == null || personInfo.getId().isEmpty()) {
                return new DataCompletionStatus(false, "ไม่พบข้อมูลผู้ป่วย");
            }

            int personId = Integer.parseInt(personInfo.getId());
            List<String> missingAssessments = new ArrayList<>();

            // ตรวจสอบข้อมูลพื้นฐาน
            boolean hasBasicInfo = isBasicInfoComplete(personInfo);
            if (!hasBasicInfo) missingAssessments.add("ข้อมูลพื้นฐาน");

            // เช็ค validation mode
            if (PersonAdapter.isPartialMode()) {
                return checkPartialMode(personId, missingAssessments);
            } else {
                return checkStrictMode(personId, missingAssessments);
            }

        } catch (Exception e) {
            Log.e("ScreeningHandler", "Error checking data completeness: " + e.getMessage());
            return new DataCompletionStatus(false, "เกิดข้อผิดพลาดในการตรวจสอบข้อมูล");
        }
    }
    private DataCompletionStatus checkPartialMode(int personId, List<String> missingAssessments) {
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
        int age = getPersonAge(personId);

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
            if (!cardioInfos.isEmpty() && isCardiovascularRiskComplete(cardioInfos.get(0), age)) {
                hasHealthRisk = true;
            } else if (age < 35) {
                hasHealthRisk = false;
            }
        }

        if (!hasHealthRisk) {
            String riskMsg = "ความเสี่ยงด้านสุขภาพ (อย่างน้อย 1 ใน 2 แบบ";
            if (age < 35) {
                riskMsg += " - หัวใจไม่บังคับสำหรับอายุ < 35 ปี";
            }
            riskMsg += ")";
            missingAssessments.add(riskMsg);
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
    private DataCompletionStatus checkStrictMode(int personId, List<String> missingAssessments) {
        // 1. ตรวจสอบสารเสพติด
        SfDrugsDao sfDrugsDao = new SfDrugsDao(context);
        List<DrugsInfo> drugsInfos = sfDrugsDao.getSfDrugsByPersonInfoId(personId);
        boolean hasDrugAssessment = !drugsInfos.isEmpty() && isDrugsComplete(drugsInfos);
        if (!hasDrugAssessment) {
            missingAssessments.add("แบบประเมินสารเสพติด");
        } else {
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
        if (!has2qAssessment) {
            missingAssessments.add("แบบประเมิน 2Q");
        } else {
            // ตรวจสอบ 9Q (เฉพาะกรณีที่ 2Q ผิดปกติ)
            boolean needs9Q = needs9QAssessment(depression2qInfos.get(0));
            if (needs9Q) {
                SfStressDepression9qInfoDao depression9qDao = new SfStressDepression9qInfoDao(context);
                List<StressDepression9qInfo> depression9qInfos = depression9qDao.getByPersonId(personId);
                boolean has9qAssessment = !depression9qInfos.isEmpty() && is9qAssessmentComplete(depression9qInfos.get(0));
                if (!has9qAssessment) {
                    missingAssessments.add("แบบประเมิน 9Q (เนื่องจาก 2Q ผิดปกติ)");
                }
            }
        }

        boolean needs8Q = needs8QAssessment(personId);
        if (needs8Q) {
            SfSuicideAssessment8qInfoDao depression8qDao = new SfSuicideAssessment8qInfoDao(context);
            List<SuicideAssessment8qInfo> depression8qInfos = depression8qDao.getByPersonId(personId);
            boolean has8qAssessment = !depression8qInfos.isEmpty() && is8qAssessmentComplete(depression8qInfos.get(0));
            if (!has8qAssessment) missingAssessments.add("แบบประเมิน 8Q");
        }

        // 6. การให้คำปรึกษา
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
    private boolean isDrugsComplete(List<DrugsInfo> drugsInfos) {
        if (drugsInfos == null || drugsInfos.isEmpty()) return false;

        try {
            // ตรวจสอบ Q1 ให้ครบทั้ง 10 คำถาม (a-j)
            Map<String, String> q1Answers = new HashMap<>();
            for (DrugsInfo drug : drugsInfos) {
                if ("Q1".equals(drug.getQuestion())) {
                    q1Answers.put(drug.getSubquestion(), drug.getAnswer());
                }
            }

            String[] expectedSubQuestions = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
            for (String subQ : expectedSubQuestions) {
                if (!q1Answers.containsKey(subQ) || q1Answers.get(subQ) == null || q1Answers.get(subQ).isEmpty()) {
                    Log.d("DRUGS_CHECK", "Missing Q1 subquestion: " + subQ);
                    return false;
                }
            }

            // ตรวจสอบว่ามีการใช้สารเสพติดหรือไม่
            boolean allQ1Zero = true;
            List<String> substancesUsed = new ArrayList<>();

            for (String subQ : expectedSubQuestions) {
                String answer = q1Answers.get(subQ);
                if (!"0".equals(answer)) {
                    allQ1Zero = false;
                    substancesUsed.add(subQ);
                }
            }

            // ถ้าไม่เคยใช้สารเสพติดเลย = ครบถ้วน
            if (allQ1Zero) {
                Log.d("DRUGS_CHECK", "All Q1 answers are 0 - Complete (no substance use)");
                return true;
            }

            Log.d("DRUGS_CHECK", "Substances used: " + substancesUsed.toString());

            // ตรวจสอบ Q2 สำหรับสารที่เคยใช้
            Map<String, String> q2Answers = new HashMap<>();
            for (DrugsInfo drug : drugsInfos) {
                if ("Q2".equals(drug.getQuestion())) {
                    q2Answers.put(drug.getSubquestion(), drug.getAnswer());
                }
            }

            // ตรวจสอบว่า Q2 ตอบครบสำหรับทุกสารที่เคยใช้
            for (String substance : substancesUsed) {
                if (!q2Answers.containsKey(substance) ||
                        q2Answers.get(substance) == null ||
                        q2Answers.get(substance).isEmpty()) {
                    Log.d("DRUGS_CHECK", "Missing Q2 answer for substance: " + substance);
                    return false;
                }
            }

            // ตรวจสอบว่า Q2 ตอบ "ไม่เคย" (0) ทั้งหมดหรือไม่
            boolean allQ2Zero = true;
            for (String substance : substancesUsed) {
                String q2Answer = q2Answers.get(substance);
                if (!"0".equals(q2Answer)) {
                    allQ2Zero = false;
                    break;
                }
            }

            Log.d("DRUGS_CHECK", "All Q2 answers are 0 (Never): " + allQ2Zero);

            if (allQ2Zero) {
                // Q2 ตอบ "ไม่เคย" ทั้งหมด - ข้าม Q3,Q4,Q5 แต่ต้องตรวจสอบ Q6,Q7,Q8
                Log.d("DRUGS_CHECK", "All Q2 Never - checking Q6,Q7,Q8 only");
                return checkQ6Q7Q8Complete(drugsInfos, substancesUsed);
            } else {
                // Q2 มีบางตัวที่ไม่ใช่ "ไม่เคย" - ต้องตรวจสอบ Q2-Q7 ครบ
                Log.d("DRUGS_CHECK", "Some Q2 not Never - checking Q2-Q7");
                return checkFullAssessmentComplete(drugsInfos, substancesUsed);
            }

        } catch (Exception e) {
            Log.e("DRUGS_CHECK", "Error checking drugs completion: " + e.getMessage());
            return false;
        }
    }
    private boolean checkQ6Q7Q8Complete(List<DrugsInfo> drugsInfos, List<String> substancesUsed) {
        // ตรวจสอบ Q6, Q7 สำหรับทุกสารที่เคยใช้ใน Q1
        String[] questions678 = {"Q6", "Q7"};

        for (String substance : substancesUsed) {
            for (String question : questions678) {
                boolean found = false;
                String foundAnswer = null;

                for (DrugsInfo drug : drugsInfos) {
                    if (question.equals(drug.getQuestion()) && substance.equals(drug.getSubquestion())) {
                        foundAnswer = drug.getAnswer();
                        found = true;
                        break;
                    }
                }

                if (!found || foundAnswer == null || foundAnswer.isEmpty()) {
                    Log.d("DRUGS_CHECK", "Missing " + question + " answer for substance: " + substance);
                    return false;
                }

                Log.d("DRUGS_CHECK", "Found " + question + substance + " = " + foundAnswer);
            }
        }

        // ตรวจสอบ Q8 (injection) - ไม่มี subquestion
        boolean foundQ8 = false;
        String q8Answer = null;

        for (DrugsInfo drug : drugsInfos) {
            if ("Q8".equals(drug.getQuestion())) {
                q8Answer = drug.getAnswer();
                foundQ8 = true;
                break;
            }
        }

        if (!foundQ8 || q8Answer == null || q8Answer.isEmpty()) {
            Log.d("DRUGS_CHECK", "Missing Q8 (injection) answer");
            return false;
        }

        Log.d("DRUGS_CHECK", "Found Q8 (injection) = " + q8Answer);
        Log.d("DRUGS_CHECK", "Q6,Q7,Q8 assessment complete");
        return true;
    }
    /**
     * ตรวจสอบแบบสมบูรณ์ Q2-Q7 เมื่อ Q2 มีการใช้สารบางอย่าง
     */
    private boolean checkFullAssessmentComplete(List<DrugsInfo> drugsInfos, List<String> substancesUsed) {
        // ตรวจสอบ Q2-Q4, Q6-Q7 สำหรับทุกสารที่เคยใช้
        String[] followUpQuestions = {"Q2", "Q3", "Q4", "Q6", "Q7"};

        for (String substance : substancesUsed) {
            for (String question : followUpQuestions) {
                boolean found = false;
                String foundAnswer = null;

                for (DrugsInfo drug : drugsInfos) {
                    if (question.equals(drug.getQuestion()) && substance.equals(drug.getSubquestion())) {
                        foundAnswer = drug.getAnswer();
                        found = true;
                        break;
                    }
                }

                if (!found || foundAnswer == null || foundAnswer.isEmpty()) {
                    Log.d("DRUGS_CHECK", "Missing " + question + " answer for substance: " + substance);
                    return false;
                }

                Log.d("DRUGS_CHECK", "Found " + question + substance + " = " + foundAnswer);
            }
        }

        // ตรวจสอบ Q5 สำหรับสารที่ไม่ใช่ "a" (ยาสูบ)
        if (!substancesUsed.isEmpty()) {
            List<String> substancesNeedingQ5 = new ArrayList<>();
            for (String substance : substancesUsed) {
                if (!"a".equals(substance)) {
                    substancesNeedingQ5.add(substance);
                }
            }

            for (String substance : substancesNeedingQ5) {
                boolean foundQ5 = false;
                String q5Answer = null;

                for (DrugsInfo drug : drugsInfos) {
                    if ("Q5".equals(drug.getQuestion()) && substance.equals(drug.getSubquestion())) {
                        q5Answer = drug.getAnswer();
                        foundQ5 = true;
                        break;
                    }
                }

                if (!foundQ5 || q5Answer == null || q5Answer.isEmpty()) {
                    Log.d("DRUGS_CHECK", "Missing Q5 answer for substance: " + substance);
                    return false;
                }

                Log.d("DRUGS_CHECK", "Found Q5" + substance + " = " + q5Answer);
            }
        }

        // ตรวจสอบ Q8 (injection)
        boolean foundQ8 = false;
        String q8Answer = null;

        for (DrugsInfo drug : drugsInfos) {
            if ("Q8".equals(drug.getQuestion())) {
                q8Answer = drug.getAnswer();
                foundQ8 = true;
                break;
            }
        }

        if (!foundQ8 || q8Answer == null || q8Answer.isEmpty()) {
            Log.d("DRUGS_CHECK", "Missing Q8 (injection) answer");
            return false;
        }

        Log.d("DRUGS_CHECK", "Found Q8 (injection) = " + q8Answer);
        Log.d("DRUGS_CHECK", "Full ASSIST assessment complete");
        return true;
    }

    private SubstanceUseStatus getSubstanceUseStatus(List<DrugsInfo> drugInfos) {
        SubstanceUseStatus status = new SubstanceUseStatus();

        if (drugInfos == null || drugInfos.isEmpty()) {
            return status;
        }

        for (DrugsInfo drug : drugInfos) {
            if ("Q1".equals(drug.getQuestion())) {
                String answer = drug.getAnswer();
                String subQuestion = drug.getSubquestion();

                if ("a".equals(subQuestion) && answer != null && !"0".equals(answer) && !answer.isEmpty()) {
                    status.usesTobacco = true;
                }

                if ("b".equals(subQuestion) && answer != null && !"0".equals(answer) && !answer.isEmpty()) {
                    status.usesAlcohol = true;
                }
            }
        }

        return status;
    }
    private boolean isBasicInfoComplete(PersonInfo person) {
        return person.getIdcard() != null && !person.getIdcard().isEmpty() &&
                person.getFname() != null && !person.getFname().isEmpty() &&
                person.getLname() != null && !person.getLname().isEmpty() &&
                person.getBirthday() != null && !person.getBirthday().isEmpty() &&
                person.getGender() != null && !person.getGender().isEmpty() &&
                person.getWeight() > 0 &&
                person.getHeight() > 0;
    }

    private boolean isSt5AssessmentComplete(StressDepressionInfo stressDepressionInfo) {
        return stressDepressionInfo.getQ1() != null && !stressDepressionInfo.getQ1().equals("0") &&
                stressDepressionInfo.getQ2() != null && !stressDepressionInfo.getQ2().equals("0") &&
                stressDepressionInfo.getQ3() != null && !stressDepressionInfo.getQ3().equals("0") &&
                stressDepressionInfo.getQ4() != null && !stressDepressionInfo.getQ4().equals("0") &&
                stressDepressionInfo.getQ5() != null && !stressDepressionInfo.getQ5().equals("0");
    }

    private boolean is2qAssessmentComplete(StressDepression2qInfo depression2qInfo) {
        return depression2qInfo.getQ1() != null && !depression2qInfo.getQ1().equals("0") &&
                depression2qInfo.getQ2() != null && !depression2qInfo.getQ2().equals("0");
    }

    private boolean is9qAssessmentComplete(StressDepression9qInfo depression9qInfo) {
        return depression9qInfo.getQ1() != null && !depression9qInfo.getQ1().equals("0") &&
                depression9qInfo.getQ2() != null && !depression9qInfo.getQ2().equals("0") &&
                depression9qInfo.getQ3() != null && !depression9qInfo.getQ3().equals("0") &&
                depression9qInfo.getQ4() != null && !depression9qInfo.getQ4().equals("0") &&
                depression9qInfo.getQ5() != null && !depression9qInfo.getQ5().equals("0") &&
                depression9qInfo.getQ6() != null && !depression9qInfo.getQ6().equals("0") &&
                depression9qInfo.getQ7() != null && !depression9qInfo.getQ7().equals("0") &&
                depression9qInfo.getQ8() != null && !depression9qInfo.getQ8().equals("0") &&
                depression9qInfo.getQ9() != null && !depression9qInfo.getQ9().equals("0");
    }

    private boolean is8qAssessmentComplete(SuicideAssessment8qInfo suicideAssessment8qInfo) {
        return suicideAssessment8qInfo.getQ1() != null && !suicideAssessment8qInfo.getQ1().equals("0") &&
                suicideAssessment8qInfo.getQ2() != null && !suicideAssessment8qInfo.getQ2().equals("0") &&
                suicideAssessment8qInfo.getQ3() != null && !suicideAssessment8qInfo.getQ3().equals("0") &&
                suicideAssessment8qInfo.getQ3_2_1() != null && !suicideAssessment8qInfo.getQ3_2_1().equals("0") &&
                suicideAssessment8qInfo.getQ4() != null && !suicideAssessment8qInfo.getQ4().equals("0") &&
                suicideAssessment8qInfo.getQ5() != null && !suicideAssessment8qInfo.getQ5().equals("0") &&
                suicideAssessment8qInfo.getQ6() != null && !suicideAssessment8qInfo.getQ6().equals("0") &&
                suicideAssessment8qInfo.getQ7() != null && !suicideAssessment8qInfo.getQ7().equals("0") &&
                suicideAssessment8qInfo.getQ8() != null && !suicideAssessment8qInfo.getQ8().equals("0");
    }

    private boolean isSmokingAssessmentComplete(SmokerInfo smokerInfo) {
        return smokerInfo.getSmokerRegularly() != null && !smokerInfo.getSmokerRegularly().equals("0") &&
                smokerInfo.getSmokerAssist() != null && !smokerInfo.getSmokerAssist().equals("0") &&
                smokerInfo.getSmokerGroup() != null && !smokerInfo.getSmokerGroup().equals("0");
    }

    private boolean isAlcoholAssessmentComplete(DrinkingInfo drinkingInfo) {
        return drinkingInfo.getDrinking() != null && !drinkingInfo.getDrinking().equals("0") &&
                drinkingInfo.getDrinkingAlway() != null && !drinkingInfo.getDrinkingAlway().equals("0") &&
                drinkingInfo.getDrinkingFrequency() != null && !drinkingInfo.getDrinkingFrequency().equals("0");
    }

    private boolean isNicotineComplete(NicotineInfo nicotineInfo) {
        return nicotineInfo.getNicotine1() != null && !nicotineInfo.getNicotine1().equals("0") &&
                nicotineInfo.getNicotine2() != null && !nicotineInfo.getNicotine2().equals("0") &&
                nicotineInfo.getNicotine3() != null && !nicotineInfo.getNicotine3().equals("0") &&
                nicotineInfo.getNicotine4() != null && !nicotineInfo.getNicotine4().equals("0") &&
                nicotineInfo.getNicotine5() != null && !nicotineInfo.getNicotine5().equals("0") &&
                nicotineInfo.getNicotine6() != null && !nicotineInfo.getNicotine6().equals("0");
    }

    private boolean isHealthRiskComplete(HealthRiskAssessmentInfo healthRisk) {
        return healthRisk.getHealthRiskQ1() != null && !healthRisk.getHealthRiskQ1().equals("0") &&
                healthRisk.getHealthRiskQ2() != null && !healthRisk.getHealthRiskQ2().equals("0") &&
                healthRisk.getHealthRiskQ3() != null && !healthRisk.getHealthRiskQ3().equals("0") &&
                healthRisk.getHealthRiskQ4() != null && !healthRisk.getHealthRiskQ4().equals("0") &&
                healthRisk.getHealthRiskQ5() != null && !healthRisk.getHealthRiskQ5().equals("0") &&
                healthRisk.getHealthRiskQ6() != null && !healthRisk.getHealthRiskQ6().equals("0");
    }

    private boolean isCardiovascularRiskComplete(CardiovascularRiskInfo cardioRisk, int age) {
        if (age < 35) return true;

        if (cardioRisk == null) return false;

        boolean hasRequiredFields = true;
        if (cardioRisk.getCholesterol() == null || cardioRisk.getCholesterol().isEmpty()) {
            hasRequiredFields = false;
        }
        if (cardioRisk.getRiskPercentage() == null || cardioRisk.getRiskPercentage().isEmpty()) {
            hasRequiredFields = false;
        }
        if (cardioRisk.getRiskLevel() == null || cardioRisk.getRiskLevel().isEmpty()) {
            hasRequiredFields = false;
        }

        return hasRequiredFields;
    }

    private boolean isCounselingComplete(CounselingInfo counselingInfo) {
        if (counselingInfo == null) return false;

        if (counselingInfo.getCounselingType() != null && counselingInfo.getCounselingType() == 1) {
            return counselingInfo.getDetail() != null && !counselingInfo.getDetail().isEmpty();
        } else if (counselingInfo.getCounselingType() != null && counselingInfo.getCounselingType() == 2) {
            return counselingInfo.getReferralDetail() != null && !counselingInfo.getReferralDetail().isEmpty();
        }

        return false;
    }

    private boolean needs9QAssessment(StressDepression2qInfo depression2qInfo) {
        if (depression2qInfo == null) return false;

        try {
            int q1Score = Integer.parseInt(depression2qInfo.getQ1() != null ? depression2qInfo.getQ1() : "0");
            int q2Score = Integer.parseInt(depression2qInfo.getQ2() != null ? depression2qInfo.getQ2() : "0");
            int totalScore = q1Score + q2Score;

            return totalScore >= 3;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private boolean needs8QAssessment(int personId) {
        try {
            int age = getPersonAge(personId);
            if (age >= 35) return true;

            SfStressDepression2qInfoDao depression2qDao = new SfStressDepression2qInfoDao(context);
            List<StressDepression2qInfo> depression2qInfos = depression2qDao.getByPersonId(personId);

            if (!depression2qInfos.isEmpty()) {
                StressDepression2qInfo data = depression2qInfos.get(0);
                if (is2qAssessmentComplete(data)) {
                    return needs9QAssessment(data);
                }
            }

            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private int getPersonAge(int personId) {
        try {
            SfPersonInfoDao personInfoDao = new SfPersonInfoDao(context);
            List<PersonInfo> personInfos = personInfoDao.getSfPersonInfoById(personId);

            if (!personInfos.isEmpty()) {
                PersonInfo personInfo = personInfos.get(0);
                if (personInfo.getBirthday() != null && !personInfo.getBirthday().isEmpty()) {
                    return AgeCalculator.calculateAge(personInfo.getBirthday());
                }
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // Helper classes
    private static class SubstanceUseStatus {
        boolean usesTobacco = false;
        boolean usesAlcohol = false;
    }

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

    /**
     * ตรวจสอบความครบถ้วนของแบบคัดกรอง
     * @return true ถ้าครบถ้วน, false ถ้าไม่ครบถ้วน
     */
    public boolean validateScreeningData() {
        DataCompletionStatus status = checkDataCompleteness();
        return status.isComplete();
    }

    /**
     * ได้รับข้อความสถานะการตรวจสอบแบบคัดกรอง
     * @return ข้อความแสดงสถานะ
     */
    public String getScreeningStatusMessage() {
        DataCompletionStatus status = checkDataCompleteness();
        return status.getMessage();
    }

}