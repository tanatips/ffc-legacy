package th.in.ffc.util;
import java.util.*;
import java.util.stream.Collectors;

/**
 * NumberValidator Class สำหรับตรวจสอบและจัดการข้อมูลตัวเลข
 * ใช้งานได้ทันทีโดยไม่ต้องสร้าง instance
 */
public class NumberValidator {

    /**
     * ตรวจสอบว่าค่าที่ส่งเข้ามาเป็นตัวเลขหรือไม่
     *
     * @param value ค่าที่ต้องการตรวจสอบ
     * @return true ถ้าเป็นตัวเลข, false ถ้าไม่ใช่
     */
    public static boolean isNumber(Object value) {
        if (value == null) return false;

        // ตรวจสอบ Number types
        if (value instanceof Number) {
            double num = ((Number) value).doubleValue();
            return !Double.isNaN(num) && Double.isFinite(num);
        }

        // ตรวจสอบ String ที่แปลงเป็นตัวเลขได้
        if (value instanceof String) {
            String str = ((String) value).trim();
            if (str.isEmpty()) return false;

            try {
                double num = Double.parseDouble(str);
                return !Double.isNaN(num) && Double.isFinite(num);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
    }

    /**
     * ตรวจสอบว่าเป็นจำนวนทศนิยม (double/float)
     *
     * @param value ค่าที่ต้องการตรวจสอบ
     * @return true ถ้าเป็นจำนวนทศนิยม
     */
    public static boolean isDouble(Object value) {
        if (!isNumber(value)) return false;

        double num = toDouble(value);
        return num % 1 != 0;
    }

    /**
     * ตรวจสอบว่าเป็นจำนวนเต็ม (integer)
     *
     * @param value ค่าที่ต้องการตรวจสอบ
     * @return true ถ้าเป็นจำนวนเต็ม
     */
    public static boolean isInteger(Object value) {
        if (!isNumber(value)) return false;

        double num = toDouble(value);
        return num % 1 == 0;
    }

    /**
     * ตรวจสอบว่าเป็นจำนวนบวก
     *
     * @param value ค่าที่ต้องการตรวจสอบ
     * @return true ถ้าเป็นจำนวนบวก
     */
    public static boolean isPositive(Object value) {
        if (!isNumber(value)) return false;

        double num = toDouble(value);
        return num > 0;
    }

    /**
     * ตรวจสอบว่าเป็นจำนวนลบ
     *
     * @param value ค่าที่ต้องการตรวจสอบ
     * @return true ถ้าเป็นจำนวนลบ
     */
    public static boolean isNegative(Object value) {
        if (!isNumber(value)) return false;

        double num = toDouble(value);
        return num < 0;
    }

    /**
     * ตรวจสอบว่าตัวเลขอยู่ในช่วงที่กำหนด
     *
     * @param value ค่าที่ต้องการตรวจสอบ
     * @param min   ค่าต่ำสุด
     * @param max   ค่าสูงสุด
     * @return true ถ้าอยู่ในช่วงที่กำหนด
     */
    public static boolean isInRange(Object value, double min, double max) {
        if (!isNumber(value)) return false;

        double num = toDouble(value);
        return num >= min && num <= max;
    }

    /**
     * กรองข้อมูลที่เป็นตัวเลข
     *
     * @param data List ข้อมูล
     * @return List ที่มีเฉพาะตัวเลข
     */
    public static List<Object> filterNumbers(List<Object> data) {
        if (data == null) return new ArrayList<>();

        return data.stream()
                .filter(NumberValidator::isNumber)
                .collect(Collectors.toList());
    }

    /**
     * กรองข้อมูลที่เป็นจำนวนทศนิยม
     *
     * @param data List ข้อมูล
     * @return List ที่มีเฉพาะจำนวนทศนิยม
     */
    public static List<Object> filterDoubles(List<Object> data) {
        if (data == null) return new ArrayList<>();

        return data.stream()
                .filter(NumberValidator::isDouble)
                .collect(Collectors.toList());
    }

    /**
     * กรองข้อมูลที่เป็นจำนวนเต็ม
     *
     * @param data List ข้อมูล
     * @return List ที่มีเฉพาะจำนวนเต็ม
     */
    public static List<Object> filterIntegers(List<Object> data) {
        if (data == null) return new ArrayList<>();

        return data.stream()
                .filter(NumberValidator::isInteger)
                .collect(Collectors.toList());
    }

    /**
     * กรองข้อมูลที่เป็นจำนวนบวก
     *
     * @param data List ข้อมูล
     * @return List ที่มีเฉพาะจำนวนบวก
     */
    public static List<Object> filterPositive(List<Object> data) {
        if (data == null) return new ArrayList<>();

        return data.stream()
                .filter(NumberValidator::isPositive)
                .collect(Collectors.toList());
    }

    /**
     * กรองข้อมูลในช่วงที่กำหนด
     *
     * @param data List ข้อมูล
     * @param min  ค่าต่ำสุด
     * @param max  ค่าสูงสุด
     * @return List ที่มีข้อมูลในช่วงที่กำหนด
     */
    public static List<Object> filterRange(List<Object> data, double min, double max) {
        if (data == null) return new ArrayList<>();

        return data.stream()
                .filter(item -> isInRange(item, min, max))
                .collect(Collectors.toList());
    }

    /**
     * แปลงค่าเป็นตัวเลข double
     *
     * @param value ค่าที่ต้องการแปลง
     * @return ตัวเลข double
     * @throws IllegalArgumentException ถ้าแปลงไม่ได้
     */
    public static double toDouble(Object value) {
        if (!isNumber(value)) {
            throw new IllegalArgumentException("ค่าที่ส่งเข้ามาไม่ใช่ตัวเลข: " + value);
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof String) {
            return Double.parseDouble(((String) value).trim());
        }

        throw new IllegalArgumentException("ไม่สามารถแปลงค่าเป็นตัวเลขได้: " + value);
    }

    /**
     * แปลงค่าเป็นตัวเลข int
     *
     * @param value ค่าที่ต้องการแปลง
     * @return ตัวเลข int
     * @throws IllegalArgumentException ถ้าแปลงไม่ได้หรือไม่ใช่จำนวนเต็ม
     */
    public static int toInteger(Object value) {
        if (!isInteger(value)) {
            throw new IllegalArgumentException("ค่าที่ส่งเข้ามาไม่ใช่จำนวนเต็ม: " + value);
        }

        return (int) toDouble(value);
    }

    /**
     * แปลงค่าเป็นตัวเลขอย่างปลอดภัย (ไม่ throw exception)
     *
     * @param value ค่าที่ต้องการแปลง
     * @return ตัวเลข หรือ null ถ้าแปลงไม่ได้
     */
    public static Double toNumberSafe(Object value) {
        try {
            return isNumber(value) ? toDouble(value) : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * ตรวจสอบและรายงานสถานะของค่า
     *
     * @param value ค่าที่ต้องการตรวจสอบ
     * @return ValidationResult รายงานสถานะ
     */
    public static ValidationResult validate(Object value) {
        ValidationResult result = new ValidationResult();
        result.value = value;
        result.isNumber = isNumber(value);
        result.type = value != null ? value.getClass().getSimpleName() : "null";

        if (result.isNumber) {
            double num = toDouble(value);
            result.numericValue = num;
            result.isInteger = isInteger(value);
            result.isDouble = isDouble(value);
            result.isPositive = isPositive(value);
            result.isNegative = isNegative(value);
            result.isZero = num == 0.0;
        }

        return result;
    }

    /**
     * Inner class สำหรับเก็บผลการตรวจสอบ
     */
    public static class ValidationResult {
        public Object value;
        public boolean isNumber;
        public String type;
        public Double numericValue;
        public boolean isInteger;
        public boolean isDouble;
        public boolean isPositive;
        public boolean isNegative;
        public boolean isZero;

        @Override
        public String toString() {
            if (!isNumber) {
                return String.format("Value: %s, Type: %s, IsNumber: false", value, type);
            }

            return String.format(
                    "Value: %s, Type: %s, NumericValue: %.2f, IsInteger: %b, IsDouble: %b, IsPositive: %b, IsNegative: %b, IsZero: %b",
                    value, type, numericValue, isInteger, isDouble, isPositive, isNegative, isZero
            );
        }
    }
}