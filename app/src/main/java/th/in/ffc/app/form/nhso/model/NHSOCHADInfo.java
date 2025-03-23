package th.in.ffc.app.form.nhso.model;

import java.util.Date;

/**
 * แฟ้มที่ 7 NHSO CHAD - ข้อมูลรายละเอียดค่าใช้จ่ายราย รายการของผู้เข้ารับบริการตามรหัสมาตรฐาน
 */
public class NHSOCHADInfo {

    // รหัสอ้างอิงภายใน
    private long id;

    // 1. รหัสการบริการที่กำหนดโดยโปรแกรม (Visit Number)
    private String seq;

    // 2. รหัส ของรหัสมาตรฐานสากลที่ระบบนั้นกำหนด หรือเป็นรหัสมาตรฐานตามกองทุนประกันสุขภาพ เช่น กรมบัญชีกลาง เป็นต้น
    private String stdcode;

    // 3. เลขที่อ้างอิงในแจ้งหนี้ของหน่วยบริการ
    private String invoiceNo;

    // 4. วันที่ ให้/ใช้ บริการ/ทรัพยากร
    private Date servdate;

    // 5. รหัสรายการค่าบริการที่สถานพยาบาลกำหนด
    private String localcode;

    // 6. ชื่อรายการที่สถานพยาบาลกำหนดรวมหน่วยนับ/ขนาด
    private String descript;

    // 7. จำนวนหน่วยที่ใช้
    private Integer qty;

    // 8. ราคาต่อหน่วยของ รพ.
    private Double unitprice;

    // 9. จำนวนเงินเรียกเก็บ (QTY x unitPrice)
    private Double chargeamt;

    // 10. หมวดค่าใช้จ่ายตาม CodeSys (01-19)
    private String billgrcs;

    // 11. ระบบรหัสที่ใช้กับ STDCODE (001-006)
    private String codesys;

    // 12. ผลของการตรวจของห้องปฏิบัติการ
    private String labResult;

    // 13. หน่วยนับ
    private String unit;

    // 14. ราคากลางต่อหน่วย
    private Double reimbprice;

    // 15. ผลการตรวจรังสี
    private String xrayResult;

    // 16. ผลการตรวจทางพยาธิวิทยา
    private String pathoResult;

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getStdcode() {
        return stdcode;
    }

    public void setStdcode(String stdcode) {
        this.stdcode = stdcode;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public Date getServdate() {
        return servdate;
    }

    public void setServdate(Date servdate) {
        this.servdate = servdate;
    }

    public String getLocalcode() {
        return localcode;
    }

    public void setLocalcode(String localcode) {
        this.localcode = localcode;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Double getUnitprice() {
        return unitprice;
    }

    public void setUnitprice(Double unitprice) {
        this.unitprice = unitprice;
    }

    public Double getChargeamt() {
        return chargeamt;
    }

    public void setChargeamt(Double chargeamt) {
        this.chargeamt = chargeamt;
    }

    public String getBillgrcs() {
        return billgrcs;
    }

    public void setBillgrcs(String billgrcs) {
        this.billgrcs = billgrcs;
    }

    public String getCodesys() {
        return codesys;
    }

    public void setCodesys(String codesys) {
        this.codesys = codesys;
    }

    public String getLabResult() {
        return labResult;
    }

    public void setLabResult(String labResult) {
        this.labResult = labResult;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getReimbprice() {
        return reimbprice;
    }

    public void setReimbprice(Double reimbprice) {
        this.reimbprice = reimbprice;
    }

    public String getXrayResult() {
        return xrayResult;
    }

    public void setXrayResult(String xrayResult) {
        this.xrayResult = xrayResult;
    }

    public String getPathoResult() {
        return pathoResult;
    }

    public void setPathoResult(String pathoResult) {
        this.pathoResult = pathoResult;
    }
}