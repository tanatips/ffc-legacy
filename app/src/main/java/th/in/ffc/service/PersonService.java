package th.in.ffc.service;

import android.content.Context;
import th.in.ffc.dao.PersonDao;
import th.in.ffc.model.Person;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Person Service Class
 * ให้บริการด้านธุรกิจสำหรับจัดการข้อมูล Person
 * รวมถึงการตรวจสอบข้อมูลและ business logic
 *
 * @author Generated
 * @version 1.0
 */
public class PersonService {

    private PersonDao personDao;
    private Context context;

    // Pattern สำหรับตรวจสอบเลขบัตรประชาชน
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("\\d{13}");

    public PersonService(Context context) {
        this.context = context;
        this.personDao = new PersonDao(context);
    }

    /**
     * ค้นหาบุคคลด้วยเลขบัตรประชาชน
     * รวมถึงการตรวจสอบความถูกต้องของเลขบัตรประชาชน
     *
     * @param idCard เลขบัตรประชาชน
     * @return Person object หากพบและถูกต้อง, null หากไม่พบหรือไม่ถูกต้อง
     */
    public Person findPersonByIdCard(String idCard) {
        // ตรวจสอบความถูกต้องของ input
        if (!isValidIdCard(idCard)) {
            return null;
        }

        // ค้นหาจากฐานข้อมูล
        return personDao.findByIdCard(idCard);
    }

    /**
     * ค้นหาบุคคลด้วย PID
     *
     * @param pid รหัส PID
     * @return Person object หากพบ, null หากไม่พบ
     */
    public Person findPersonByPid(String pid) {
        if (pid == null || pid.trim().isEmpty()) {
            return null;
        }

        return personDao.findByPid(pid.trim());
    }

    /**
     * ค้นหาสมาชิกในครอบครัวทั้งหมด
     *
     * @param hcode รหัสบ้าน
     * @return List ของสมาชิกในครอบครัว
     */
    public List<Person> findFamilyMembers(String hcode) {
        if (hcode == null || hcode.trim().isEmpty()) {
            return null;
        }

        return personDao.findByHouseCode(hcode.trim());
    }

    /**
     * ค้นหาบุคคลด้วยชื่อ
     *
     * @param firstName ชื่อ (สามารถเป็น partial match)
     * @param lastName นามสกุล (สามารถเป็น partial match)
     * @return List ของ Person ที่ตรงกับเงื่อนไข
     */
    public List<Person> searchPersonByName(String firstName, String lastName) {
        return personDao.findByName(firstName, lastName);
    }

    /**
     * ค้นหาบุคคลด้วยชื่อเต็ม
     *
     * @param fullName ชื่อเต็ม
     * @return List ของ Person ที่ตรงกับเงื่อนไข
     */
    public List<Person> searchPersonByFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return null;
        }

        // แยกชื่อและนามสกุล
        String[] nameParts = fullName.trim().split("\\s+");
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : "";

        return personDao.findByName(firstName, lastName);
    }

    /**
     * ตรวจสอบว่ามีบุคคลที่มีเลขบัตรประชาชนนี้หรือไม่
     *
     * @param idCard เลขบัตรประชาชน
     * @return true หากมีอยู่, false หากไม่มี
     */
    public boolean isPersonExists(String idCard) {
        if (!isValidIdCard(idCard)) {
            return false;
        }

        return personDao.existsByIdCard(idCard);
    }

    /**
     * นับจำนวนสมาชิกในครอบครัว
     *
     * @param hcode รหัสบ้าน
     * @return จำนวนสมาชิกในครอบครัว
     */
    public int countFamilyMembers(String hcode) {
        if (hcode == null || hcode.trim().isEmpty()) {
            return 0;
        }

        return personDao.countByHouseCode(hcode.trim());
    }

    /**
     * ได้ข้อมูลสถิติของบุคคลในครอบครัว
     *
     * @param hcode รหัสบ้าน
     * @return PersonFamilyStats object
     */
    public PersonFamilyStats getFamilyStats(String hcode) {
        List<Person> familyMembers = findFamilyMembers(hcode);

        if (familyMembers == null || familyMembers.isEmpty()) {
            return new PersonFamilyStats(0, 0, 0, 0);
        }

        int totalMembers = familyMembers.size();
        int maleCount = 0;
        int femaleCount = 0;
        int unknownSexCount = 0;

        for (Person person : familyMembers) {
            if (person.isMale()) {
                maleCount++;
            } else if (person.isFemale()) {
                femaleCount++;
            } else {
                unknownSexCount++;
            }
        }

        return new PersonFamilyStats(totalMembers, maleCount, femaleCount, unknownSexCount);
    }

    /**
     * ตรวจสอบความถูกต้องของเลขบัตรประชาชน
     *
     * @param idCard เลขบัตรประชาชน
     * @return true หากถูกต้อง, false หากไม่ถูกต้อง
     */
    public boolean isValidIdCard(String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return false;
        }

        String cleanIdCard = idCard.trim().replaceAll("[^0-9]", "");

        // ตรวจสอบความยาว
        if (cleanIdCard.length() != 13) {
            return false;
        }

        // ตรวจสอบ pattern
        if (!ID_CARD_PATTERN.matcher(cleanIdCard).matches()) {
            return false;
        }

        // ตรวจสอบ check digit (เลขตรวจสอบ)
        return isValidIdCardChecksum(cleanIdCard);
    }

    /**
     * ตรวจสอบเลขตรวจสอบของบัตรประชาชน
     *
     * @param idCard เลขบัตรประชาชน 13 หลัก
     * @return true หากถูกต้อง, false หากไม่ถูกต้อง
     */
    private boolean isValidIdCardChecksum(String idCard) {
        if (idCard.length() != 13) {
            return false;
        }

        try {
            int sum = 0;
            for (int i = 0; i < 12; i++) {
                sum += Character.getNumericValue(idCard.charAt(i)) * (13 - i);
            }

            int remainder = sum % 11;
            int checkDigit = (11 - remainder) % 10;

            return checkDigit == Character.getNumericValue(idCard.charAt(12));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * ทำความสะอาดเลขบัตรประชาชน (เอาเครื่องหมายออก)
     *
     * @param idCard เลขบัตรประชาชน
     * @return เลขบัตรประชาชนที่สะอาด
     */
    public String cleanIdCard(String idCard) {
        if (idCard == null) {
            return null;
        }

        return idCard.trim().replaceAll("[^0-9]", "");
    }

    /**
     * จัดรูปแบบเลขบัตรประชาชนให้อ่านง่าย (x-xxxx-xxxxx-xx-x)
     *
     * @param idCard เลขบัตรประชาชน
     * @return เลขบัตรประชาชนที่จัดรูปแบบแล้ว
     */
    public String formatIdCard(String idCard) {
        String cleanIdCard = cleanIdCard(idCard);

        if (cleanIdCard == null || cleanIdCard.length() != 13) {
            return idCard; // คืนค่าเดิมหากไม่ถูกต้อง
        }

        return String.format("%s-%s-%s-%s-%s",
                cleanIdCard.substring(0, 1),
                cleanIdCard.substring(1, 5),
                cleanIdCard.substring(5, 10),
                cleanIdCard.substring(10, 12),
                cleanIdCard.substring(12, 13));
    }

    /**
     * ค้นหาบุคคลแบบ fuzzy search
     *
     * @param searchTerm คำค้นหา
     * @return List ของ Person ที่ตรงกับเงื่อนไข
     */
    public List<Person> fuzzySearchPerson(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return null;
        }

        String cleanTerm = searchTerm.trim();

        // ถ้าเป็นตัวเลข อาจเป็นเลขบัตรประชาชนหรือ PID
        if (cleanTerm.matches("\\d+")) {
            // ลองค้นหาด้วยเลขบัตรประชาชนก่อน
            if (cleanTerm.length() == 13) {
                Person person = findPersonByIdCard(cleanTerm);
                if (person != null) {
                    return List.of(person);
                }
            }

            // ลองค้นหาด้วย PID
            Person person = findPersonByPid(cleanTerm);
            if (person != null) {
                return List.of(person);
            }
        }

        // ค้นหาด้วยชื่อ
        return searchPersonByFullName(cleanTerm);
    }

    /**
     * Class สำหรับเก็บสถิติของครอบครัว
     */
    public static class PersonFamilyStats {
        private final int totalMembers;
        private final int maleCount;
        private final int femaleCount;
        private final int unknownSexCount;

        public PersonFamilyStats(int totalMembers, int maleCount, int femaleCount, int unknownSexCount) {
            this.totalMembers = totalMembers;
            this.maleCount = maleCount;
            this.femaleCount = femaleCount;
            this.unknownSexCount = unknownSexCount;
        }

        public int getTotalMembers() {
            return totalMembers;
        }

        public int getMaleCount() {
            return maleCount;
        }

        public int getFemaleCount() {
            return femaleCount;
        }

        public int getUnknownSexCount() {
            return unknownSexCount;
        }

        public double getMalePercentage() {
            return totalMembers > 0 ? (maleCount * 100.0) / totalMembers : 0;
        }

        public double getFemalePercentage() {
            return totalMembers > 0 ? (femaleCount * 100.0) / totalMembers : 0;
        }

        @Override
        public String toString() {
            return String.format("FamilyStats{total=%d, male=%d(%.1f%%), female=%d(%.1f%%), unknown=%d}",
                    totalMembers, maleCount, getMalePercentage(),
                    femaleCount, getFemalePercentage(), unknownSexCount);
        }
    }
}