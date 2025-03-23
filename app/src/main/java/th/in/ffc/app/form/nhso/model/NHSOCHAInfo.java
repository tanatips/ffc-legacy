package th.in.ffc.app.form.nhso.model;

import java.util.Date;

/**
 * แฟ้มที่ 8 NHSO CHA - ข้อมูลรายละเอียดทางการเงินของผู้เข้ารับบริการ
 */
public class NHSOCHAInfo {

    // รหัสอ้างอิงภายใน
    private long id;

    // 1. รหัสการบริการที่กำหนดโดยโปรแกรม (Visit Number)
    private String seq;

    // 2. วันที่ค่ารักษา วันที่จ่ายบริการ หรือวันที่ผู้ป่วยเปลี่ยนสถานะการรักษา บันทึกเป็นปี ค.ศ.
    private Date date;

    // 3. ชนิดของบริการที่คิดค่ารักษา ตามรหัสที่กำหนด (เช่น C1)
    private String chrgitem;

    // 4. เลขที่อ้างอิงในแจ้งหนี้ของหน่วยบริการ
    private String invoiceNo;

    // 5. จำนวนเงิน ค่ารักษาของบริการรายการนั้น เป็นบาท
    private Double amount;

    // 6. จำนวนเงินค่ารักษารวมหน่วยเป็นบาท ที่รวมเรียบร้อยแล้ว
    private Double total;

    // 7. รายละเอียดค่าบริการและการรักษาเพิ่มเติม (ถ้ามี)
    private String opdMemo;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getChrgitem() {
        return chrgitem;
    }

    public void setChrgitem(String chrgitem) {
        this.chrgitem = chrgitem;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getOpdMemo() {
        return opdMemo;
    }

    public void setOpdMemo(String opdMemo) {
        this.opdMemo = opdMemo;
    }
}