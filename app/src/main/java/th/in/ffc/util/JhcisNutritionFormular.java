package th.in.ffc.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class JhcisNutritionFormular {

    public static String Zero2Null(Object o) {
        return Integer.valueOf(o.toString()) == 0 ? null : o.toString();
    }

    // method ����¹�ѹ��������Ẻ dd/mm/yyyy
    public static String ConvertDate(Calendar time) {
        int date = time.get(Calendar.DAY_OF_MONTH);
        int month = time.get(Calendar.MONTH) + 1;
        int year = time.get(Calendar.YEAR) + 543;
        String currentDate = date + "/" + month + "/" + year;
        return currentDate;
    } // �� method ConvertDate

    public static String ConvertDate(java.util.Date d) {
        GregorianCalendar time = new GregorianCalendar();
        time.setTime(d);
        int date = time.get(Calendar.DAY_OF_MONTH);
        int month = time.get(Calendar.MONTH) + 1;
        int year = time.get(Calendar.YEAR) + 543;
        String currentDate = date + "/" + month + "/" + year;
        return currentDate;
    }

    public static int getMonth(java.util.Date d) {
        GregorianCalendar time = new GregorianCalendar();
        time.setTime(d);
        return (time.get(Calendar.MONTH)) + 1;
    }

    /*
     * �ŧ������ �ѹ��� ������� Ẻ yyyy-mm-dd : HH:MM:ss ����ѹ���
	 * �ٻẺ��ͤ���(Ẻ���) ��Ш�����ʴ� ����������� ��� showtime �� true
	 * ��� �ʴ����Ҵ��� �� false ��� ����ʴ����� ���ѹ��� �ٻẺ dd/mm/yyyy
	 */
    public static String getDateShotToString(java.util.Date date,
                                             boolean showtime) {
        String pattern = "d MMM yyyy";
        String dateString = new String();
        // java.util.Date today = new java.util.Date();
        java.text.SimpleDateFormat formatter = null;

        try {
            if (showtime) {
                formatter = new java.text.SimpleDateFormat(pattern);
                dateString = formatter.format(date);
            } else {
                pattern = "dd/MM/yyyy";
                formatter = new java.text.SimpleDateFormat(pattern);
                dateString = formatter.format(date);
            }

        } catch (IllegalArgumentException iae) {
            dateString = null;
        }
        formatter = null;
        pattern = null;
        return dateString;
    }

    /*
	 * �ŧ������ �ҡ��ͤ��� �� �ѹ��� �ٻẺ �ͧ��������� ��
	 * yyyy-mm-dd,hh:nn:ss
	 */
    public static java.util.Date getDateFromText(String text) {
        Calendar c = Calendar.getInstance();
        if (text == null || text.length() < 10)
            return null;
        try {
            int yyyy = Integer.parseInt(text.substring(0, 4)) + 543;
            int mm = Integer.parseInt(text.substring(5, 7)) - 1;
            int dd = Integer.parseInt(text.substring(8, 10));
            c.set(yyyy, mm, dd);
            if (text.length() > 10) {
                int hh = Integer.parseInt(text.substring(11, 13));
                int nn = Integer.parseInt(text.substring(14, 16));
                int ss = Integer.parseInt(text.substring(17));
                c.set(yyyy, mm, dd, hh, nn, ss);
            }
            return c.getTime();
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }
    }

    /**
     * <p/>
     * �ŧ������ �ѹ��� ��Դ Date Ẻ yyyy-mm-dd
     * <p/>
     * ����ѹ��� �ٻẺ��ͤ���(Ẻ���)
     * <p/>
     * "d MMMM yyyy" => 1 ���Ҥ� 2551
     * <p/>
     * "d MMM yyyy" => 1 �.�. 2551
     * <p/>
     * "dd/MM/yyyy" => 01/01/2551
     */
    public static String getDateThaiMonth(java.util.Date date, String pattern) {
        if (!pattern.equals("yyyy"))
            pattern = pattern.startsWith("d MMMM") ? "d MMMM " : "d MMM ";
        String dateString = new String();
        java.text.SimpleDateFormat formatter = null;
        // new java.sql.Timestamp(120000).valueOf(pattern);
        try {
            formatter = new java.text.SimpleDateFormat(pattern);
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(date);
            if (pattern.equals("yyyy"))
                dateString = "" + (c.get(Calendar.YEAR) + 1086);
            else
                dateString = formatter.format(date) + ""
                        + (c.get(Calendar.YEAR) + 1086);
        } catch (IllegalArgumentException iae) {
            dateString = null;
        }
        formatter = null;
        pattern = null;
        return dateString;
    }

    public static String calculateAgeMonth(java.sql.Timestamp sqldate) {
        java.util.Date today = new java.util.Date();
        java.util.Date d = new java.util.Date();
        d.setTime(sqldate.getTime());
//		if (d == null) {
//			return "";
//		}
        Calendar c3 = Calendar.getInstance();
        c3.setTimeInMillis(today.getTime() - d.getTime() - 86400000);

        String year = String.valueOf(c3.get(Calendar.YEAR) - 543 - 1970);
        String month = String.valueOf(c3.get(Calendar.MONTH));
        String date = String.valueOf(c3.get(Calendar.DATE));
        // String minute = String.valueOf(c3.get(Calendar.MINUTE));
        // String hour = String.valueOf(c3.get(Calendar.HOUR_OF_DAY));
        Integer year1;
        Integer month1;
        Integer date1;
        year1 = Integer.parseInt(year) * 12;
        month1 = Integer.parseInt(month) * 1;
        date1 = Integer.parseInt(date) * 1;

        if (year.substring(0, 1).equals("-"))
            return "0";

        // �ѹ
        if (year.equalsIgnoreCase("0")) {
            if (month.equalsIgnoreCase("0")) {
                if (date.equalsIgnoreCase("0")) {
                    return "";
                } else {
                    if (date1 > 14) {
                        return "1";
                    } else {
                        return "0";
                    }
                }
            } else {
                // ��͹ + �ѹ
                if (date.equalsIgnoreCase("0")) {
                    return month1.toString();
                } else {
                    if (date1 > 14) {
                        month1 = month1 + 1;
                        return month1.toString();
                    } else { // ��͹ ��ǹ ������ѹ
                        return month;
                    }
                }
            }
        } else {
            // �� + �ѹ
            if (month.equalsIgnoreCase("0")) {
                if (date.equalsIgnoreCase("0")) {
                    return year1.toString();
                } else {

                    if (date1 > 14) { // �Ѻ�� 1 ��͹
                        year1 = year1 + 1; // year1 ����͹��������
                        return year1.toString();
                    }
                    return year1.toString();
                }
            } else {
                // �� ��͹ �ѹ
                if (date.equalsIgnoreCase("0")) {
                    month1 = year1 + month1;
                    return month1.toString();
                } else {
                    if (date1 > 14) {
                        month1 = month1 + 1;
                        month1 = year1 + month1;
                        return month1.toString();
                    } else {
                        month1 = year1 + month1;
                        return month1.toString();
                    }
                }
            }
        }

    }

    public static Integer calculateAgeMonth1(java.sql.Timestamp sqldate) {
        java.util.Date today = new java.util.Date();
        java.util.Date d = new java.util.Date();
        d.setTime(sqldate.getTime());
//		if (d == null)
//			return null;
        Calendar c3 = Calendar.getInstance();
        c3.setTimeInMillis(today.getTime() - d.getTime() - 86400000);

        String year = String.valueOf(c3.get(Calendar.YEAR) - 543 - 1970);
        String month = String.valueOf(c3.get(Calendar.MONTH));
        String date = String.valueOf(c3.get(Calendar.DATE));
//		String minute = String.valueOf(c3.get(Calendar.MINUTE));
//		String hour = String.valueOf(c3.get(Calendar.HOUR_OF_DAY));
        Integer year1;
        Integer month1;
        Integer date1;
        year1 = Integer.parseInt(year) * 12;
        month1 = Integer.parseInt(month) * 1;
        date1 = Integer.parseInt(date) * 1;

        if (year.substring(0, 1).equals("-"))
            return 0;

        // �ѹ
        if (year.equalsIgnoreCase("0")) {
            if (month.equalsIgnoreCase("0")) {
                if (date.equalsIgnoreCase("0")) {
                    return null;
                } else {
                    if (date1 > 14) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            } else {
                // ��͹ + �ѹ
                if (date.equalsIgnoreCase("0")) {
                    return month1;
                } else {
                    if (date1 > 14) {
                        month1 = month1 + 1;
                        return month1;
                    } else { // ��͹ ��ǹ ������ѹ
                        return Integer.parseInt(month);
                    }
                }
            }
        } else {
            // �� + �ѹ
            if (month.equalsIgnoreCase("0")) {
                if (date.equalsIgnoreCase("0")) {
                    return year1;
                } else {

                    if (date1 > 14) { // �Ѻ�� 1 ��͹
                        year1 = year1 + 1; // year1 ����͹��������
                        return year1;
                    }
                    return year1;
                }
            } else {
                // �� ��͹ �ѹ
                if (date.equalsIgnoreCase("0")) {
                    month1 = year1 + month1;
                    return month1;
                } else {
                    if (date1 > 14) {
                        month1 = month1 + 1;
                        month1 = year1 + month1;
                        return month1;
                    } else {
                        month1 = year1 + month1;
                        return month1;
                    }
                }
            }
        }

    }

    /*
	 * �ŧ��������Ҩҡ �ѹ��� �ٻẺ dd/mm/yyyy ��� yyyy-mm-dd
	 */
    public static String getGuiBDate(String date) {
        if (date == null)
            return "";
        if (date.length() < 9)
            return "";
        try {
            String temp = new String();
            // System.out.println("Real Date : " + text);
            // System.out.println(text.substring(0,(text.indexOf("/"))) );
            String dd = date.substring(0, date.indexOf("/"));
            int ddd = Integer.parseInt(dd);
            String d = "00" + String.valueOf(ddd);
            d = d.substring(d.length() - 2, d.length());
            // System.out.println("DATE : " + dd);
            temp = date.substring(dd.length() + 1);
            // System.out.println(temp);
            int m = Integer.parseInt(temp.substring(0, temp.indexOf("/")));
            String mm = "00" + String.valueOf(m);
            mm = mm.substring(mm.length() - 2, mm.length());
            // System.out.println("MONHT : " + mm);
            temp = temp.substring(temp.indexOf("/") + 1);
            // System.out.println(temp);
            int yyyy = Integer.parseInt(temp.substring(0, 4)) - 543;
            // System.out.println(yyyy);
            return yyyy + "-" + mm + "-" + d;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ���� / ���˹ѡ
    public static String UpdateNutritionStateAgeLessThen18AgeWeight(int age,
                                                                    double weight, String sex) {

        String nutrionText = "";
        int nutritionState1 = 0;

        if (age >= 0 && weight > 0) {
            if (sex.equals("1")) {
                nutritionState1 = GetNutritionStateMaleAgeLessThen18AgeWeight(
                        age, weight);

                // System.out.println(">>>after call>>>>age= " +age +
                // "  weight="+weight+" nutritionState1="+nutritionState1+
                // " sex="+sex);

            }

            if (sex.equals("2")) {
                nutritionState1 = GetNutritionStateFemaleAgeLessThen18AgeWeight(
                        age, weight);
            }

            // switch (nutritionState1) {
            // case 1: nutrionText = "���˹ѡ����ҡ"; break;
            // case 2: nutrionText = "���˹ѡ���"; break;
            // case 3: nutrionText = "���˹ѡ����"; break;
            // case 4: nutrionText = "���˹ѡ�٧"; break;
            // case 5: nutrionText = "���˹ѡ�٧�ҡ"; break;
            // case 6: nutrionText = "���˹ѡ�٧�ҡ�Թࡳ��"; break;
            // default: nutrionText = ""; break;
            // }
            switch (nutritionState1) {
                case 1:
                    nutrionText = "0";
                    break;
                case 2:
                    nutrionText = "1";
                    break;
                case 3:
                    nutrionText = "2";
                    break;
                case 4:
                    nutrionText = "3";
                    break;
                case 5:
                    nutrionText = "4";
                    break;
                case 6:
                    nutrionText = "5";
                    break;
                default:
                    nutrionText = "6";
                    break;
            }
        }

        return nutrionText;
    }

    // ��ǹ�٧ / ���˹ѡ
    public static String UpdateNutritionStateAgeLessThen18WeightHeight(
            double weight, int height, String sex) {
        int nutritionState = 0;
        String nutrionText = "";

        if (weight >= 0 && height >= 0) {
            if (sex.equals("1")) {
                nutritionState = GetNutritionStateMaleAgeLessThen18WeightHeight(
                        weight, height);

                // System.out.println("height= " +height +
                // "weight="+weight+" nutritionState="+nutritionState);
            }
            if (sex.equals("2")) {
                nutritionState = GetNutritionStateFemaleAgeLessThen18WeightHeight(
                        weight, height);
            }
            // switch (nutritionState) {
            // case 1: nutrionText = "���"; break;
            // case 2: nutrionText = "��͹��ҧ���"; break;
            // case 3: nutrionText = "����ǹ"; break;
            // case 4: nutrionText = "��͹��ҧ��ǹ"; break;
            // case 5: nutrionText = "��ǹ"; break;
            // case 6: nutrionText = "��ǹ�ҡ"; break;
            // default: nutrionText = ""; break;
            // }
            switch (nutritionState) {
                case 1:
                    nutrionText = "0";
                    break;
                case 2:
                    nutrionText = "1";
                    break;
                case 3:
                    nutrionText = "2";
                    break;
                case 4:
                    nutrionText = "3";
                    break;
                case 5:
                    nutrionText = "4";
                    break;
                case 6:
                    nutrionText = "5";
                    break;
                default:
                    nutrionText = "6";
                    break;
            }
        }
        // System.out.println("height= " +height +
        // "weight="+weight+" nutrionText="+nutrionText);
        return nutrionText;
    }

    // ���� / ��ǹ�٧
    public static String UpdateNutritionStateAgeLessThen18AgeHeight(int age,
                                                                    double height, String sex) {
        int nutritionState = 0;

        String nutrionText = "";

        if (age >= 0 && height >= 0) {

            if (sex.equals("1")) {
                nutritionState = GetNutritionStateMaleAgeLessThen18AgeHeight(
                        age, height);
            }
            if (sex.equals("2")) {
                nutritionState = GetNutritionStateFemaleAgeLessThen18AgeHeight(
                        age, height);
            }
            // switch (nutritionState) {
            // case 1: nutrionText = "����"; break;
            // case 2: nutrionText = "��͹��ҧ����"; break;
            // case 3: nutrionText = "����"; break;
            // case 4: nutrionText = "��͹��ҧ�٧"; break;
            // case 5: nutrionText = "�٧�Թࡳ��"; break;
            // default: nutrionText = ""; break;
            // }
            switch (nutritionState) {
                case 1:
                    nutrionText = "0";
                    break;
                case 2:
                    nutrionText = "1";
                    break;
                case 3:
                    nutrionText = "2";
                    break;
                case 4:
                    nutrionText = "3";
                    break;
                case 5:
                    nutrionText = "4";
                    break;
                default:
                    nutrionText = "5";
                    break;
            }
        }

        return nutrionText;
    }

    // ˭ԧ : ���� / ���˹ѡ
    public static int GetNutritionStateFemaleAgeLessThen18AgeWeight(int age,
                                                                    double weight) {
        int nutritionState = 0;
        switch (age) {
            case 0:
                if (weight < 2.6)
                    nutritionState = 1;
                else if (weight < 2.7)
                    nutritionState = 2;
                else if (weight < 3.8)
                    nutritionState = 3;
                else if (weight < 3.9)
                    nutritionState = 4;
                else if (weight >= 3.9)
                    nutritionState = 5;
                break;
            case 1:
                if (weight < 3.2)
                    nutritionState = 1;
                else if (weight < 3.3)
                    nutritionState = 2;
                else if (weight < 3.5)
                    nutritionState = 3;
                else if (weight < 3.8)
                    nutritionState = 4;
                else if (weight >= 3.8)
                    nutritionState = 5;
                break;
            case 2:
                if (weight < 3.7)
                    nutritionState = 1;
                else if (weight < 3.8)
                    nutritionState = 2;
                else if (weight < 5.3)
                    nutritionState = 3;
                else if (weight < 5.6)
                    nutritionState = 4;
                else if (weight >= 5.6)
                    nutritionState = 5;
                break;
            case 3:
                if (weight < 4.1)
                    nutritionState = 1;
                else if (weight < 4.4)
                    nutritionState = 2;
                else if (weight < 6.1)
                    nutritionState = 3;
                else if (weight < 6.4)
                    nutritionState = 4;
                else if (weight >= 6.4)
                    nutritionState = 5;
                break;
            case 4:
                if (weight < 4.6)
                    nutritionState = 1;
                else if (weight < 4.9)
                    nutritionState = 2;
                else if (weight < 6.8)
                    nutritionState = 3;
                else if (weight < 7.1)
                    nutritionState = 4;
                else if (weight >= 7.1)
                    nutritionState = 5;
                break;
            case 5:
                if (weight < 5)
                    nutritionState = 1;
                else if (weight < 5.3)
                    nutritionState = 2;
                else if (weight < 7.4)
                    nutritionState = 3;
                else if (weight < 7.8)
                    nutritionState = 4;
                else if (weight >= 7.8)
                    nutritionState = 5;
                break;
            case 6:
                if (weight < 5.5)
                    nutritionState = 1;
                else if (weight < 5.8)
                    nutritionState = 2;
                else if (weight < 8)
                    nutritionState = 3;
                else if (weight < 8.4)
                    nutritionState = 4;
                else if (weight >= 8.4)
                    nutritionState = 5;
                break;
            case 7:
                if (weight < 5.8)
                    nutritionState = 1;
                else if (weight < 6.2)
                    nutritionState = 2;
                else if (weight < 8.6)
                    nutritionState = 3;
                else if (weight < 9)
                    nutritionState = 4;
                else if (weight >= 9)
                    nutritionState = 5;
                break;
            case 8:
                if (weight < 6.2)
                    nutritionState = 1;
                else if (weight < 6.6)
                    nutritionState = 2;
                else if (weight < 9.1)
                    nutritionState = 3;
                else if (weight < 9.5)
                    nutritionState = 4;
                else if (weight >= 9.5)
                    nutritionState = 5;
                break;
            case 9:
                if (weight < 6.5)
                    nutritionState = 1;
                else if (weight < 6.9)
                    nutritionState = 2;
                else if (weight < 9.4)
                    nutritionState = 3;
                else if (weight < 9.9)
                    nutritionState = 4;
                else if (weight >= 9.9)
                    nutritionState = 5;
                break;
            case 10:
                if (weight < 6.8)
                    nutritionState = 1;
                else if (weight < 7.2)
                    nutritionState = 2;
                else if (weight < 9.9)
                    nutritionState = 3;
                else if (weight < 10.4)
                    nutritionState = 4;
                else if (weight >= 10.4)
                    nutritionState = 5;
                break;
            case 11:
                if (weight < 7.1)
                    nutritionState = 1;
                else if (weight < 7.5)
                    nutritionState = 2;
                else if (weight < 10.3)
                    nutritionState = 3;
                else if (weight < 10.8)
                    nutritionState = 4;
                else if (weight >= 10.8)
                    nutritionState = 5;
                break;
            case 12:
                if (weight < 7.3)
                    nutritionState = 1;
                else if (weight < 7.7)
                    nutritionState = 2;
                else if (weight < 10.6)
                    nutritionState = 3;
                else if (weight < 11.1)
                    nutritionState = 4;
                else if (weight >= 11.1)
                    nutritionState = 5;
                break;
            case 13:
                if (weight < 7.5)
                    nutritionState = 1;
                else if (weight < 7.9)
                    nutritionState = 2;
                else if (weight < 10.9)
                    nutritionState = 3;
                else if (weight < 11.4)
                    nutritionState = 4;
                else if (weight >= 11.4)
                    nutritionState = 5;
                break;
            case 14:
                if (weight < 7.7)
                    nutritionState = 1;
                else if (weight < 8.1)
                    nutritionState = 2;
                else if (weight < 11.2)
                    nutritionState = 3;
                else if (weight < 11.7)
                    nutritionState = 4;
                else if (weight >= 11.7)
                    nutritionState = 5;
                break;
            case 15:
                if (weight < 7.9)
                    nutritionState = 1;
                else if (weight < 8.3)
                    nutritionState = 2;
                else if (weight < 11.4)
                    nutritionState = 3;
                else if (weight < 12)
                    nutritionState = 4;
                else if (weight >= 12)
                    nutritionState = 5;
                break;
            case 16:
                if (weight < 8)
                    nutritionState = 1;
                else if (weight < 8.4)
                    nutritionState = 2;
                else if (weight < 11.7)
                    nutritionState = 3;
                else if (weight < 12.2)
                    nutritionState = 4;
                else if (weight >= 12.2)
                    nutritionState = 5;
                break;
            case 17:
                if (weight < 8.2)
                    nutritionState = 1;
                else if (weight < 8.6)
                    nutritionState = 2;
                else if (weight < 11.9)
                    nutritionState = 3;
                else if (weight < 12.5)
                    nutritionState = 4;
                else if (weight >= 12.5)
                    nutritionState = 5;
                break;
            case 18:
                if (weight < 8.3)
                    nutritionState = 1;
                else if (weight < 8.8)
                    nutritionState = 2;
                else if (weight < 12.2)
                    nutritionState = 3;
                else if (weight < 12.9)
                    nutritionState = 4;
                else if (weight >= 12.9)
                    nutritionState = 5;
                break;
            case 19:
                if (weight < 8.5)
                    nutritionState = 1;
                else if (weight < 9)
                    nutritionState = 2;
                else if (weight < 12.5)
                    nutritionState = 3;
                else if (weight < 13.2)
                    nutritionState = 4;
                else if (weight >= 13.2)
                    nutritionState = 5;
                break;
            case 20:
                if (weight < 8.6)
                    nutritionState = 1;
                else if (weight < 9.1)
                    nutritionState = 2;
                else if (weight < 12.7)
                    nutritionState = 3;
                else if (weight < 13.4)
                    nutritionState = 4;
                else if (weight >= 13.4)
                    nutritionState = 5;
                break;
            case 21:
                if (weight < 8.8)
                    nutritionState = 1;
                else if (weight < 9.3)
                    nutritionState = 2;
                else if (weight < 13)
                    nutritionState = 3;
                else if (weight < 13.7)
                    nutritionState = 4;
                else if (weight >= 13.7)
                    nutritionState = 5;
                break;
            case 22:
                if (weight < 8.9)
                    nutritionState = 1;
                else if (weight < 9.4)
                    nutritionState = 2;
                else if (weight < 13.2)
                    nutritionState = 3;
                else if (weight < 13.9)
                    nutritionState = 4;
                else if (weight >= 13.9)
                    nutritionState = 5;
                break;
            case 23:
                if (weight < 9)
                    nutritionState = 1;
                else if (weight < 9.5)
                    nutritionState = 2;
                else if (weight < 13.5)
                    nutritionState = 3;
                else if (weight < 14.2)
                    nutritionState = 4;
                else if (weight >= 14.2)
                    nutritionState = 5;
                break;
            case 24:
                if (weight < 9.1)
                    nutritionState = 1;
                else if (weight < 9.7)
                    nutritionState = 2;
                else if (weight < 13.8)
                    nutritionState = 3;
                else if (weight < 14.5)
                    nutritionState = 4;
                else if (weight >= 14.5)
                    nutritionState = 5;
                break;
            case 25:
                if (weight < 9.2)
                    nutritionState = 1;
                else if (weight < 9.8)
                    nutritionState = 2;
                else if (weight < 14)
                    nutritionState = 3;
                else if (weight < 14.7)
                    nutritionState = 4;
                else if (weight >= 14.7)
                    nutritionState = 5;
                break;
            case 26:
                if (weight < 9.3)
                    nutritionState = 1;
                else if (weight < 10)
                    nutritionState = 2;
                else if (weight < 14.3)
                    nutritionState = 3;
                else if (weight < 15)
                    nutritionState = 4;
                else if (weight >= 15)
                    nutritionState = 5;
                break;
            case 27:
                if (weight < 9.5)
                    nutritionState = 1;
                else if (weight < 10.1)
                    nutritionState = 2;
                else if (weight < 14.5)
                    nutritionState = 3;
                else if (weight < 15.2)
                    nutritionState = 4;
                else if (weight >= 15.2)
                    nutritionState = 5;
                break;
            case 28:
                if (weight < 9.6)
                    nutritionState = 1;
                else if (weight < 10.2)
                    nutritionState = 2;
                else if (weight < 14.7)
                    nutritionState = 3;
                else if (weight < 15.5)
                    nutritionState = 4;
                else if (weight >= 15.5)
                    nutritionState = 5;
                break;
            case 29:
                if (weight < 9.7)
                    nutritionState = 1;
                else if (weight < 10.4)
                    nutritionState = 2;
                else if (weight < 15)
                    nutritionState = 3;
                else if (weight < 15.8)
                    nutritionState = 4;
                else if (weight >= 15.8)
                    nutritionState = 5;
                break;
            case 30:
                if (weight < 9.8)
                    nutritionState = 1;
                else if (weight < 10.6)
                    nutritionState = 2;
                else if (weight < 15.2)
                    nutritionState = 3;
                else if (weight < 16)
                    nutritionState = 4;
                else if (weight >= 16)
                    nutritionState = 5;
                break;
            case 31:
                if (weight < 10)
                    nutritionState = 1;
                else if (weight < 10.8)
                    nutritionState = 2;
                else if (weight < 15.5)
                    nutritionState = 3;
                else if (weight < 16.3)
                    nutritionState = 4;
                else if (weight >= 16.3)
                    nutritionState = 5;
                break;
            case 32:
                if (weight < 10.1)
                    nutritionState = 1;
                else if (weight < 10.9)
                    nutritionState = 2;
                else if (weight < 15.7)
                    nutritionState = 3;
                else if (weight < 16.5)
                    nutritionState = 4;
                else if (weight >= 16.5)
                    nutritionState = 5;
                break;
            case 33:
                if (weight < 10.3)
                    nutritionState = 1;
                else if (weight < 11.1)
                    nutritionState = 2;
                else if (weight < 16)
                    nutritionState = 3;
                else if (weight < 16.8)
                    nutritionState = 4;
                else if (weight >= 16.8)
                    nutritionState = 5;
                break;
            case 34:
                if (weight < 10.5)
                    nutritionState = 1;
                else if (weight < 11.2)
                    nutritionState = 2;
                else if (weight < 16.2)
                    nutritionState = 3;
                else if (weight < 17)
                    nutritionState = 4;
                else if (weight >= 17)
                    nutritionState = 5;
                break;
            case 35:
                if (weight < 10.6)
                    nutritionState = 1;
                else if (weight < 11.4)
                    nutritionState = 2;
                else if (weight < 16.5)
                    nutritionState = 3;
                else if (weight < 17.3)
                    nutritionState = 4;
                else if (weight >= 17.3)
                    nutritionState = 5;
                break;
            case 36:
                if (weight < 10.7)
                    nutritionState = 1;
                else if (weight < 11.5)
                    nutritionState = 2;
                else if (weight < 16.6)
                    nutritionState = 3;
                else if (weight < 17.5)
                    nutritionState = 4;
                else if (weight >= 17.5)
                    nutritionState = 5;
                break;
            case 37:
                if (weight < 10.9)
                    nutritionState = 1;
                else if (weight < 11.7)
                    nutritionState = 2;
                else if (weight < 16.9)
                    nutritionState = 3;
                else if (weight < 17.8)
                    nutritionState = 4;
                else if (weight >= 17.8)
                    nutritionState = 5;
                break;
            case 38:
                if (weight < 11)
                    nutritionState = 1;
                else if (weight < 11.8)
                    nutritionState = 2;
                else if (weight < 17.1)
                    nutritionState = 3;
                else if (weight < 18)
                    nutritionState = 4;
                else if (weight >= 18)
                    nutritionState = 5;
                break;
            case 39:
                if (weight < 11.1)
                    nutritionState = 1;
                else if (weight < 11.9)
                    nutritionState = 2;
                else if (weight < 17.4)
                    nutritionState = 3;
                else if (weight < 18.3)
                    nutritionState = 4;
                else if (weight >= 18.3)
                    nutritionState = 5;
                break;
            case 40:
                if (weight < 11.2)
                    nutritionState = 1;
                else if (weight < 12)
                    nutritionState = 2;
                else if (weight < 17.6)
                    nutritionState = 3;
                else if (weight < 18.5)
                    nutritionState = 4;
                else if (weight >= 18.5)
                    nutritionState = 5;
                break;
            case 41:
                if (weight < 11.4)
                    nutritionState = 1;
                else if (weight < 12.2)
                    nutritionState = 2;
                else if (weight < 17.8)
                    nutritionState = 3;
                else if (weight < 18.8)
                    nutritionState = 4;
                else if (weight >= 18.8)
                    nutritionState = 5;
                break;
            case 42:
                if (weight < 11.5)
                    nutritionState = 1;
                else if (weight < 12.3)
                    nutritionState = 2;
                else if (weight < 18)
                    nutritionState = 3;
                else if (weight < 19)
                    nutritionState = 4;
                else if (weight >= 19)
                    nutritionState = 5;
                break;
            case 43:
                if (weight < 11.6)
                    nutritionState = 1;
                else if (weight < 12.4)
                    nutritionState = 2;
                else if (weight < 18.2)
                    nutritionState = 3;
                else if (weight < 19.2)
                    nutritionState = 4;
                else if (weight >= 19.2)
                    nutritionState = 5;
                break;
            case 44:
                if (weight < 11.7)
                    nutritionState = 1;
                else if (weight < 12.6)
                    nutritionState = 2;
                else if (weight < 18.5)
                    nutritionState = 3;
                else if (weight < 19.6)
                    nutritionState = 4;
                else if (weight >= 18.5)
                    nutritionState = 5;
                break;
            case 45:
                if (weight < 11.8)
                    nutritionState = 1;
                else if (weight < 12.7)
                    nutritionState = 2;
                else if (weight < 18.7)
                    nutritionState = 3;
                else if (weight < 19.8)
                    nutritionState = 4;
                else if (weight >= 19.8)
                    nutritionState = 5;
                break;
            case 46:
                if (weight < 11.9)
                    nutritionState = 1;
                else if (weight < 12.8)
                    nutritionState = 2;
                else if (weight < 18.8)
                    nutritionState = 3;
                else if (weight < 20)
                    nutritionState = 4;
                else if (weight >= 20)
                    nutritionState = 5;
                break;
            case 47:
                if (weight < 12)
                    nutritionState = 1;
                else if (weight < 12.9)
                    nutritionState = 2;
                else if (weight < 19)
                    nutritionState = 3;
                else if (weight < 20.2)
                    nutritionState = 4;
                else if (weight >= 20.2)
                    nutritionState = 5;
                break;
            // 48 - 59 ????? (5 ??)
            case 48:
                if (weight < 12.1)
                    nutritionState = 1;
                else if (weight < 13)
                    nutritionState = 2;
                else if (weight < 19.3)
                    nutritionState = 3;
                else if (weight < 20.5)
                    nutritionState = 4;
                else if (weight >= 20.5)
                    nutritionState = 5;
                break;
            case 49:
                if (weight < 12.2)
                    nutritionState = 1;
                else if (weight < 13.1)
                    nutritionState = 2;
                else if (weight < 19.5)
                    nutritionState = 3;
                else if (weight < 20.7)
                    nutritionState = 4;
                else if (weight >= 20.7)
                    nutritionState = 5;
                break;
            case 50:
                if (weight < 12.3)
                    nutritionState = 1;
                else if (weight < 13.2)
                    nutritionState = 2;
                else if (weight < 19.7)
                    nutritionState = 3;
                else if (weight < 20.9)
                    nutritionState = 4;
                else if (weight >= 20.9)
                    nutritionState = 5;
                break;
            case 51:
                if (weight < 12.4)
                    nutritionState = 1;
                else if (weight < 13.3)
                    nutritionState = 2;
                else if (weight < 19.9)
                    nutritionState = 3;
                else if (weight < 21.1)
                    nutritionState = 4;
                else if (weight >= 21.1)
                    nutritionState = 5;
                break;
            case 52:
                if (weight < 12.6)
                    nutritionState = 1;
                else if (weight < 13.5)
                    nutritionState = 2;
                else if (weight < 20)
                    nutritionState = 3;
                else if (weight < 21.3)
                    nutritionState = 4;
                else if (weight >= 21.3)
                    nutritionState = 5;
                break;
            case 53:
                if (weight < 12.7)
                    nutritionState = 1;
                else if (weight < 13.6)
                    nutritionState = 2;
                else if (weight < 20.3)
                    nutritionState = 3;
                else if (weight < 21.6)
                    nutritionState = 4;
                else if (weight >= 21.6)
                    nutritionState = 5;
                break;
            case 54:
                if (weight < 12.8)
                    nutritionState = 1;
                else if (weight < 13.7)
                    nutritionState = 2;
                else if (weight < 20.4)
                    nutritionState = 3;
                else if (weight < 21.7)
                    nutritionState = 4;
                else if (weight >= 21.7)
                    nutritionState = 5;
                break;
            case 55:
                if (weight < 12.9)
                    nutritionState = 1;
                else if (weight < 13.8)
                    nutritionState = 2;
                else if (weight < 20.6)
                    nutritionState = 3;
                else if (weight < 21.9)
                    nutritionState = 4;
                else if (weight >= 21.9)
                    nutritionState = 5;
                break;
            case 56:
                if (weight < 13)
                    nutritionState = 1;
                else if (weight < 13.9)
                    nutritionState = 2;
                else if (weight < 20.8)
                    nutritionState = 3;
                else if (weight < 22.1)
                    nutritionState = 4;
                else if (weight >= 22.1)
                    nutritionState = 5;
                break;
            case 57:
                if (weight < 13.1)
                    nutritionState = 1;
                else if (weight < 14)
                    nutritionState = 2;
                else if (weight < 21.1)
                    nutritionState = 3;
                else if (weight < 22.4)
                    nutritionState = 4;
                else if (weight >= 22.4)
                    nutritionState = 5;
                break;
            case 58:
                if (weight < 13.2)
                    nutritionState = 1;
                else if (weight < 14.1)
                    nutritionState = 2;
                else if (weight < 21.3)
                    nutritionState = 3;
                else if (weight < 22.6)
                    nutritionState = 4;
                else if (weight >= 22.6)
                    nutritionState = 5;
                break;
            case 59:
                if (weight < 13.4)
                    nutritionState = 1;
                else if (weight < 14.3)
                    nutritionState = 2;
                else if (weight < 21.5)
                    nutritionState = 3;
                else if (weight < 22.9)
                    nutritionState = 4;
                else if (weight >= 22.9)
                    nutritionState = 5;
                break;
            // 60 - 71 ????? (6 ??)
            case 60:
                if (weight < 13.5)
                    nutritionState = 1;
                else if (weight < 14.4)
                    nutritionState = 2;
                else if (weight < 21.8)
                    nutritionState = 3;
                else if (weight < 23.2)
                    nutritionState = 4;
                else if (weight >= 23.2)
                    nutritionState = 5;
                break;
            case 61:
                if (weight < 13.6)
                    nutritionState = 1;
                else if (weight < 14.5)
                    nutritionState = 2;
                else if (weight < 22.1)
                    nutritionState = 3;
                else if (weight < 23.6)
                    nutritionState = 4;
                else if (weight >= 23.6)
                    nutritionState = 5;
                break;
            case 62:
                if (weight < 13.7)
                    nutritionState = 1;
                else if (weight < 14.7)
                    nutritionState = 2;
                else if (weight < 22.3)
                    nutritionState = 3;
                else if (weight < 23.9)
                    nutritionState = 4;
                else if (weight >= 23.9)
                    nutritionState = 5;
                break;
            case 63:
                if (weight < 13.9)
                    nutritionState = 1;
                else if (weight < 14.9)
                    nutritionState = 2;
                else if (weight < 22.6)
                    nutritionState = 3;
                else if (weight < 24.2)
                    nutritionState = 4;
                else if (weight >= 24.2)
                    nutritionState = 5;
                break;
            case 64:
                if (weight < 14)
                    nutritionState = 1;
                else if (weight < 15)
                    nutritionState = 2;
                else if (weight < 22.8)
                    nutritionState = 3;
                else if (weight < 24.4)
                    nutritionState = 4;
                else if (weight >= 24.4)
                    nutritionState = 5;
                break;
            case 65:
                if (weight < 14.2)
                    nutritionState = 1;
                else if (weight < 15.2)
                    nutritionState = 2;
                else if (weight < 23.1)
                    nutritionState = 3;
                else if (weight < 24.7)
                    nutritionState = 4;
                else if (weight >= 24.7)
                    nutritionState = 5;
                break;
            case 66:
                if (weight < 14.3)
                    nutritionState = 1;
                else if (weight < 15.3)
                    nutritionState = 2;
                else if (weight < 23.4)
                    nutritionState = 3;
                else if (weight < 25)
                    nutritionState = 4;
                else if (weight >= 25)
                    nutritionState = 5;
                break;
            case 67:
                if (weight < 14.4)
                    nutritionState = 1;
                else if (weight < 15.4)
                    nutritionState = 2;
                else if (weight < 23.6)
                    nutritionState = 3;
                else if (weight < 25.3)
                    nutritionState = 4;
                else if (weight >= 25.3)
                    nutritionState = 5;
                break;
            case 68:
                if (weight < 14.6)
                    nutritionState = 1;
                else if (weight < 15.6)
                    nutritionState = 2;
                else if (weight < 23.9)
                    nutritionState = 3;
                else if (weight < 25.6)
                    nutritionState = 4;
                else if (weight >= 25.6)
                    nutritionState = 5;
                break;
            case 69:
                if (weight < 14.6)
                    nutritionState = 1;
                else if (weight < 15.7)
                    nutritionState = 2;
                else if (weight < 24.1)
                    nutritionState = 3;
                else if (weight < 25.8)
                    nutritionState = 4;
                else if (weight >= 25.8)
                    nutritionState = 5;
                break;
            case 70:
                if (weight < 14.9)
                    nutritionState = 1;
                else if (weight < 15.9)
                    nutritionState = 2;
                else if (weight < 24.3)
                    nutritionState = 3;
                else if (weight < 26)
                    nutritionState = 4;
                else if (weight >= 26)
                    nutritionState = 5;
                break;
            case 71:
                if (weight < 14.9)
                    nutritionState = 1;
                else if (weight < 16)
                    nutritionState = 2;
                else if (weight < 24.6)
                    nutritionState = 3;
                else if (weight < 26.3)
                    nutritionState = 4;
                else if (weight >= 26.3)
                    nutritionState = 5;
                break;
            // 72 - 83 ????? (7 ??)
            case 72:
                if (weight < 15)
                    nutritionState = 1;
                else if (weight < 16.1)
                    nutritionState = 2;
                else if (weight < 24.8)
                    nutritionState = 3;
                else if (weight < 26.5)
                    nutritionState = 4;
                else if (weight >= 26.5)
                    nutritionState = 5;
                break;
            case 73:
                if (weight < 15.2)
                    nutritionState = 1;
                else if (weight < 16.3)
                    nutritionState = 2;
                else if (weight < 25.1)
                    nutritionState = 3;
                else if (weight < 26.9)
                    nutritionState = 4;
                else if (weight >= 26.9)
                    nutritionState = 5;
                break;
            case 74:
                if (weight < 15.3)
                    nutritionState = 1;
                else if (weight < 16.4)
                    nutritionState = 2;
                else if (weight < 25.4)
                    nutritionState = 3;
                else if (weight < 27.2)
                    nutritionState = 4;
                else if (weight >= 27.2)
                    nutritionState = 5;
                break;
            case 75:
                if (weight < 15.4)
                    nutritionState = 1;
                else if (weight < 16.6)
                    nutritionState = 2;
                else if (weight < 25.7)
                    nutritionState = 3;
                else if (weight < 27.7)
                    nutritionState = 4;
                else if (weight >= 27.7)
                    nutritionState = 5;
                break;
            case 76:
                if (weight < 15.5)
                    nutritionState = 1;
                else if (weight < 16.7)
                    nutritionState = 2;
                else if (weight < 26.1)
                    nutritionState = 3;
                else if (weight < 28.1)
                    nutritionState = 4;
                else if (weight >= 28.1)
                    nutritionState = 5;
                break;
            case 77:
                if (weight < 15.7)
                    nutritionState = 1;
                else if (weight < 16.9)
                    nutritionState = 2;
                else if (weight < 26.5)
                    nutritionState = 3;
                else if (weight < 28.6)
                    nutritionState = 4;
                else if (weight >= 28.6)
                    nutritionState = 5;
                break;
            case 78:
                if (weight < 15.8)
                    nutritionState = 1;
                else if (weight < 17)
                    nutritionState = 2;
                else if (weight < 26.9)
                    nutritionState = 3;
                else if (weight < 29)
                    nutritionState = 4;
                else if (weight >= 29)
                    nutritionState = 5;
                break;
            case 79:
                if (weight < 15.9)
                    nutritionState = 1;
                else if (weight < 17.1)
                    nutritionState = 2;
                else if (weight < 27.2)
                    nutritionState = 3;
                else if (weight < 29.3)
                    nutritionState = 4;
                else if (weight >= 29.3)
                    nutritionState = 5;
                break;
            case 80:
                if (weight < 16)
                    nutritionState = 1;
                else if (weight < 17.2)
                    nutritionState = 2;
                else if (weight < 27.5)
                    nutritionState = 3;
                else if (weight < 29.7)
                    nutritionState = 4;
                else if (weight >= 29.7)
                    nutritionState = 5;
                break;
            case 81:
                if (weight < 16.2)
                    nutritionState = 1;
                else if (weight < 17.4)
                    nutritionState = 2;
                else if (weight < 27.9)
                    nutritionState = 3;
                else if (weight < 30.1)
                    nutritionState = 4;
                else if (weight >= 30.1)
                    nutritionState = 5;
                break;
            case 82:
                if (weight < 16.3)
                    nutritionState = 1;
                else if (weight < 17.5)
                    nutritionState = 2;
                else if (weight < 28.2)
                    nutritionState = 3;
                else if (weight < 30.6)
                    nutritionState = 4;
                else if (weight >= 30.6)
                    nutritionState = 5;
                break;
            case 83:
                if (weight < 16.4)
                    nutritionState = 1;
                else if (weight < 17.6)
                    nutritionState = 2;
                else if (weight < 28.5)
                    nutritionState = 3;
                else if (weight < 30.9)
                    nutritionState = 4;
                else if (weight >= 30.9)
                    nutritionState = 5;
                break;
            // 84 - 95 ????? (8 ??)
            case 84:
                if (weight < 16.5)
                    nutritionState = 1;
                else if (weight < 17.7)
                    nutritionState = 2;
                else if (weight < 28.8)
                    nutritionState = 3;
                else if (weight < 31.3)
                    nutritionState = 4;
                else if (weight >= 31.3)
                    nutritionState = 5;
                break;
            case 85:
                if (weight < 16.7)
                    nutritionState = 1;
                else if (weight < 17.9)
                    nutritionState = 2;
                else if (weight < 29.2)
                    nutritionState = 3;
                else if (weight < 31.7)
                    nutritionState = 4;
                else if (weight >= 31.7)
                    nutritionState = 5;
                break;
            case 86:
                if (weight < 16.8)
                    nutritionState = 1;
                else if (weight < 18.1)
                    nutritionState = 2;
                else if (weight < 29.5)
                    nutritionState = 3;
                else if (weight < 32)
                    nutritionState = 4;
                else if (weight >= 32)
                    nutritionState = 5;
                break;
            case 87:
                if (weight < 16.9)
                    nutritionState = 1;
                else if (weight < 18.2)
                    nutritionState = 2;
                else if (weight < 29.8)
                    nutritionState = 3;
                else if (weight < 32.4)
                    nutritionState = 4;
                else if (weight >= 32.4)
                    nutritionState = 5;
                break;
            case 88:
                if (weight < 17)
                    nutritionState = 1;
                else if (weight < 18.3)
                    nutritionState = 2;
                else if (weight < 30.2)
                    nutritionState = 3;
                else if (weight < 32.8)
                    nutritionState = 4;
                else if (weight >= 32.8)
                    nutritionState = 5;
                break;
            case 89:
                if (weight < 17.1)
                    nutritionState = 1;
                else if (weight < 18.4)
                    nutritionState = 2;
                else if (weight < 30.5)
                    nutritionState = 3;
                else if (weight < 33.1)
                    nutritionState = 4;
                else if (weight >= 33.1)
                    nutritionState = 5;
                break;
            case 90:
                if (weight < 17.3)
                    nutritionState = 1;
                else if (weight < 18.6)
                    nutritionState = 2;
                else if (weight < 30.8)
                    nutritionState = 3;
                else if (weight < 33.6)
                    nutritionState = 4;
                else if (weight >= 33.6)
                    nutritionState = 5;
                break;
            case 91:
                if (weight < 17.4)
                    nutritionState = 1;
                else if (weight < 18.7)
                    nutritionState = 2;
                else if (weight < 31.2)
                    nutritionState = 3;
                else if (weight < 34)
                    nutritionState = 4;
                else if (weight >= 34)
                    nutritionState = 5;
                break;
            case 92:
                if (weight < 17.5)
                    nutritionState = 1;
                else if (weight < 18.8)
                    nutritionState = 2;
                else if (weight < 31.4)
                    nutritionState = 3;
                else if (weight < 34.3)
                    nutritionState = 4;
                else if (weight >= 34.3)
                    nutritionState = 5;
                break;
            case 93:
                if (weight < 17.6)
                    nutritionState = 1;
                else if (weight < 18.9)
                    nutritionState = 2;
                else if (weight < 31.8)
                    nutritionState = 3;
                else if (weight < 34.7)
                    nutritionState = 4;
                else if (weight >= 34.7)
                    nutritionState = 5;
                break;
            case 94:
                if (weight < 17.7)
                    nutritionState = 1;
                else if (weight < 19.1)
                    nutritionState = 2;
                else if (weight < 32.1)
                    nutritionState = 3;
                else if (weight < 35)
                    nutritionState = 4;
                else if (weight >= 35)
                    nutritionState = 5;
                break;
            case 95:
                if (weight < 17.8)
                    nutritionState = 1;
                else if (weight < 19.2)
                    nutritionState = 2;
                else if (weight < 32.4)
                    nutritionState = 3;
                else if (weight < 35.3)
                    nutritionState = 4;
                else if (weight >= 35.3)
                    nutritionState = 5;
                break;
            // 96 - 107 ????? (9 ??)
            case 96:
                if (weight < 17.9)
                    nutritionState = 1;
                else if (weight < 19.3)
                    nutritionState = 2;
                else if (weight < 32.6)
                    nutritionState = 3;
                else if (weight < 35.6)
                    nutritionState = 4;
                else if (weight >= 35.6)
                    nutritionState = 5;
                break;
            case 97:
                if (weight < 18)
                    nutritionState = 1;
                else if (weight < 19.4)
                    nutritionState = 2;
                else if (weight < 33)
                    nutritionState = 3;
                else if (weight < 36)
                    nutritionState = 4;
                else if (weight >= 36)
                    nutritionState = 5;
                break;
            case 98:
                if (weight < 18.2)
                    nutritionState = 1;
                else if (weight < 19.6)
                    nutritionState = 2;
                else if (weight < 33.4)
                    nutritionState = 3;
                else if (weight < 36.4)
                    nutritionState = 4;
                else if (weight >= 36.4)
                    nutritionState = 5;
                break;
            case 99:
                if (weight < 18.2)
                    nutritionState = 1;
                else if (weight < 19.7)
                    nutritionState = 2;
                else if (weight < 33.7)
                    nutritionState = 3;
                else if (weight < 36.8)
                    nutritionState = 4;
                else if (weight >= 36.8)
                    nutritionState = 5;
                break;
            case 100:
                if (weight < 18.3)
                    nutritionState = 1;
                else if (weight < 19.9)
                    nutritionState = 2;
                else if (weight < 34.2)
                    nutritionState = 3;
                else if (weight < 37.4)
                    nutritionState = 4;
                else if (weight >= 37.4)
                    nutritionState = 5;
                break;
            case 101:
                if (weight < 18.4)
                    nutritionState = 1;
                else if (weight < 20)
                    nutritionState = 2;
                else if (weight < 34.6)
                    nutritionState = 3;
                else if (weight < 37.9)
                    nutritionState = 4;
                else if (weight >= 37.9)
                    nutritionState = 5;
                break;
            case 102:
                if (weight < 18.6)
                    nutritionState = 1;
                else if (weight < 20.2)
                    nutritionState = 2;
                else if (weight < 35)
                    nutritionState = 3;
                else if (weight < 38.4)
                    nutritionState = 4;
                else if (weight >= 38.4)
                    nutritionState = 5;
                break;
            case 103:
                if (weight < 18.7)
                    nutritionState = 1;
                else if (weight < 20.3)
                    nutritionState = 2;
                else if (weight < 35.5)
                    nutritionState = 3;
                else if (weight < 38.9)
                    nutritionState = 4;
                else if (weight >= 38.9)
                    nutritionState = 5;
                break;
            case 104:
                if (weight < 18.9)
                    nutritionState = 1;
                else if (weight < 20.5)
                    nutritionState = 2;
                else if (weight < 35.9)
                    nutritionState = 3;
                else if (weight < 39.3)
                    nutritionState = 4;
                else if (weight >= 39.3)
                    nutritionState = 5;
                break;
            case 105:
                if (weight < 19)
                    nutritionState = 1;
                else if (weight < 20.7)
                    nutritionState = 2;
                else if (weight < 36.3)
                    nutritionState = 3;
                else if (weight < 39.8)
                    nutritionState = 4;
                else if (weight >= 39.8)
                    nutritionState = 5;
                break;
            case 106:
                if (weight < 19.1)
                    nutritionState = 1;
                else if (weight < 20.8)
                    nutritionState = 2;
                else if (weight < 36.8)
                    nutritionState = 3;
                else if (weight < 40.3)
                    nutritionState = 4;
                else if (weight >= 40.3)
                    nutritionState = 5;
                break;
            case 107:
                if (weight < 19.3)
                    nutritionState = 1;
                else if (weight < 21)
                    nutritionState = 2;
                else if (weight < 37.1)
                    nutritionState = 3;
                else if (weight < 40.7)
                    nutritionState = 4;
                else if (weight >= 40.7)
                    nutritionState = 5;
                break;
            // 108 - 119 ????? (10 ??)
            case 108:
                if (weight < 19.4)
                    nutritionState = 1;
                else if (weight < 21.2)
                    nutritionState = 2;
                else if (weight < 37.5)
                    nutritionState = 3;
                else if (weight < 41.2)
                    nutritionState = 4;
                else if (weight >= 41.2)
                    nutritionState = 5;
                break;
            case 109:
                if (weight < 19.6)
                    nutritionState = 1;
                else if (weight < 21.4)
                    nutritionState = 2;
                else if (weight < 37.9)
                    nutritionState = 3;
                else if (weight < 41.6)
                    nutritionState = 4;
                else if (weight >= 41.6)
                    nutritionState = 5;
                break;
            case 110:
                if (weight < 19.7)
                    nutritionState = 1;
                else if (weight < 21.5)
                    nutritionState = 2;
                else if (weight < 38.3)
                    nutritionState = 3;
                else if (weight < 42.1)
                    nutritionState = 4;
                else if (weight >= 42.1)
                    nutritionState = 5;
                break;
            case 111:
                if (weight < 19.7)
                    nutritionState = 1;
                else if (weight < 21.6)
                    nutritionState = 2;
                else if (weight < 38.7)
                    nutritionState = 3;
                else if (weight < 42.5)
                    nutritionState = 4;
                else if (weight >= 42.5)
                    nutritionState = 5;
                break;
            case 112:
                if (weight < 19.9)
                    nutritionState = 1;
                else if (weight < 21.8)
                    nutritionState = 2;
                else if (weight < 39.1)
                    nutritionState = 3;
                else if (weight < 42.9)
                    nutritionState = 4;
                else if (weight >= 42.9)
                    nutritionState = 5;
                break;
            case 113:
                if (weight < 20)
                    nutritionState = 1;
                else if (weight < 22)
                    nutritionState = 2;
                else if (weight < 39.5)
                    nutritionState = 3;
                else if (weight < 43.4)
                    nutritionState = 4;
                else if (weight >= 43.4)
                    nutritionState = 5;
                break;
            case 114:
                if (weight < 20.2)
                    nutritionState = 1;
                else if (weight < 22.2)
                    nutritionState = 2;
                else if (weight < 39.9)
                    nutritionState = 3;
                else if (weight < 43.8)
                    nutritionState = 4;
                else if (weight >= 43.8)
                    nutritionState = 5;
                break;
            case 115:
                if (weight < 20.3)
                    nutritionState = 1;
                else if (weight < 22.3)
                    nutritionState = 2;
                else if (weight < 40.3)
                    nutritionState = 3;
                else if (weight < 44.2)
                    nutritionState = 4;
                else if (weight >= 44.2)
                    nutritionState = 5;
                break;
            case 116:
                if (weight < 20.5)
                    nutritionState = 1;
                else if (weight < 22.6)
                    nutritionState = 2;
                else if (weight < 40.7)
                    nutritionState = 3;
                else if (weight < 42.6)
                    nutritionState = 4;
                else if (weight >= 42.6)
                    nutritionState = 5;
                break;
            case 117:
                if (weight < 20.7)
                    nutritionState = 1;
                else if (weight < 22.8)
                    nutritionState = 2;
                else if (weight < 41.1)
                    nutritionState = 3;
                else if (weight < 45.1)
                    nutritionState = 4;
                else if (weight >= 45.1)
                    nutritionState = 5;
                break;
            case 118:
                if (weight < 20.9)
                    nutritionState = 1;
                else if (weight < 23)
                    nutritionState = 2;
                else if (weight < 41.4)
                    nutritionState = 3;
                else if (weight < 45.5)
                    nutritionState = 4;
                else if (weight >= 45.5)
                    nutritionState = 5;
                break;
            case 119:
                if (weight < 21.1)
                    nutritionState = 1;
                else if (weight < 23.2)
                    nutritionState = 2;
                else if (weight < 41.8)
                    nutritionState = 3;
                else if (weight < 45.9)
                    nutritionState = 4;
                else if (weight >= 45.9)
                    nutritionState = 5;
                break;
            // 120 - 131 ????? (11 ??)
            case 120:
                if (weight < 21.3)
                    nutritionState = 1;
                else if (weight < 23.4)
                    nutritionState = 2;
                else if (weight < 42.2)
                    nutritionState = 3;
                else if (weight < 46.3)
                    nutritionState = 4;
                else if (weight >= 46.3)
                    nutritionState = 5;
                break;
            case 121:
                if (weight < 21.5)
                    nutritionState = 1;
                else if (weight < 23.7)
                    nutritionState = 2;
                else if (weight < 42.6)
                    nutritionState = 3;
                else if (weight < 46.7)
                    nutritionState = 4;
                else if (weight >= 46.7)
                    nutritionState = 5;
                break;
            case 122:
                if (weight < 21.7)
                    nutritionState = 1;
                else if (weight < 23.9)
                    nutritionState = 2;
                else if (weight < 43)
                    nutritionState = 3;
                else if (weight < 47.1)
                    nutritionState = 4;
                else if (weight >= 47.1)
                    nutritionState = 5;
                break;
            case 123:
                if (weight < 21.9)
                    nutritionState = 1;
                else if (weight < 24.1)
                    nutritionState = 2;
                else if (weight < 43.4)
                    nutritionState = 3;
                else if (weight < 47.5)
                    nutritionState = 4;
                else if (weight >= 47.5)
                    nutritionState = 5;
                break;
            case 124:
                if (weight < 22)
                    nutritionState = 1;
                else if (weight < 24.4)
                    nutritionState = 2;
                else if (weight < 43.7)
                    nutritionState = 3;
                else if (weight < 47.8)
                    nutritionState = 4;
                else if (weight >= 47.8)
                    nutritionState = 5;
                break;
            case 125:
                if (weight < 22.2)
                    nutritionState = 1;
                else if (weight < 24.6)
                    nutritionState = 2;
                else if (weight < 44.1)
                    nutritionState = 3;
                else if (weight < 48.2)
                    nutritionState = 4;
                else if (weight >= 48.2)
                    nutritionState = 5;
                break;
            case 126:
                if (weight < 22.4)
                    nutritionState = 1;
                else if (weight < 24.8)
                    nutritionState = 2;
                else if (weight < 44.5)
                    nutritionState = 3;
                else if (weight < 48.6)
                    nutritionState = 4;
                else if (weight >= 48.6)
                    nutritionState = 5;
                break;
            case 127:
                if (weight < 22.5)
                    nutritionState = 1;
                else if (weight < 25)
                    nutritionState = 2;
                else if (weight < 44.8)
                    nutritionState = 3;
                else if (weight < 48.9)
                    nutritionState = 4;
                else if (weight >= 48.9)
                    nutritionState = 5;
                break;
            case 128:
                if (weight < 22.7)
                    nutritionState = 1;
                else if (weight < 25.2)
                    nutritionState = 2;
                else if (weight < 45.2)
                    nutritionState = 3;
                else if (weight < 49.3)
                    nutritionState = 4;
                else if (weight >= 49.3)
                    nutritionState = 5;
                break;
            case 129:
                if (weight < 22.9)
                    nutritionState = 1;
                else if (weight < 25.5)
                    nutritionState = 2;
                else if (weight < 45.6)
                    nutritionState = 3;
                else if (weight < 49.7)
                    nutritionState = 4;
                else if (weight >= 49.7)
                    nutritionState = 5;
                break;
            case 130:
                if (weight < 23.1)
                    nutritionState = 1;
                else if (weight < 25.7)
                    nutritionState = 2;
                else if (weight < 45.9)
                    nutritionState = 3;
                else if (weight < 50)
                    nutritionState = 4;
                else if (weight >= 50)
                    nutritionState = 5;
                break;
            case 131:
                if (weight < 23.2)
                    nutritionState = 1;
                else if (weight < 25.8)
                    nutritionState = 2;
                else if (weight < 46.2)
                    nutritionState = 3;
                else if (weight < 50.3)
                    nutritionState = 4;
                else if (weight >= 50.3)
                    nutritionState = 5;
                break;
            // 132 - 143 ????? (12 ??)
            case 132:
                if (weight < 23.4)
                    nutritionState = 1;
                else if (weight < 26.1)
                    nutritionState = 2;
                else if (weight < 46.6)
                    nutritionState = 3;
                else if (weight < 50.7)
                    nutritionState = 4;
                else if (weight >= 50.7)
                    nutritionState = 5;
                break;
            case 133:
                if (weight < 23.5)
                    nutritionState = 1;
                else if (weight < 26.3)
                    nutritionState = 2;
                else if (weight < 46.9)
                    nutritionState = 3;
                else if (weight < 51)
                    nutritionState = 4;
                else if (weight >= 51)
                    nutritionState = 5;
                break;
            case 134:
                if (weight < 23.7)
                    nutritionState = 1;
                else if (weight < 26.5)
                    nutritionState = 2;
                else if (weight < 47.2)
                    nutritionState = 3;
                else if (weight < 51.3)
                    nutritionState = 4;
                else if (weight >= 51.3)
                    nutritionState = 5;
                break;
            case 135:
                if (weight < 24)
                    nutritionState = 1;
                else if (weight < 26.8)
                    nutritionState = 2;
                else if (weight < 47.5)
                    nutritionState = 3;
                else if (weight < 51.6)
                    nutritionState = 4;
                else if (weight >= 51.6)
                    nutritionState = 5;
                break;
            case 136:
                if (weight < 24.2)
                    nutritionState = 1;
                else if (weight < 27.1)
                    nutritionState = 2;
                else if (weight < 47.9)
                    nutritionState = 3;
                else if (weight < 52)
                    nutritionState = 4;
                else if (weight >= 52)
                    nutritionState = 5;
                break;
            case 137:
                if (weight < 24.4)
                    nutritionState = 1;
                else if (weight < 27.3)
                    nutritionState = 2;
                else if (weight < 48.2)
                    nutritionState = 3;
                else if (weight < 52.3)
                    nutritionState = 4;
                else if (weight >= 52.3)
                    nutritionState = 5;
                break;
            case 138:
                if (weight < 24.7)
                    nutritionState = 1;
                else if (weight < 27.6)
                    nutritionState = 2;
                else if (weight < 48.5)
                    nutritionState = 3;
                else if (weight < 52.6)
                    nutritionState = 4;
                else if (weight >= 52.6)
                    nutritionState = 5;
                break;
            case 139:
                if (weight < 25)
                    nutritionState = 1;
                else if (weight < 27.9)
                    nutritionState = 2;
                else if (weight < 48.8)
                    nutritionState = 3;
                else if (weight < 52.9)
                    nutritionState = 4;
                else if (weight >= 52.9)
                    nutritionState = 5;
                break;
            case 140:
                if (weight < 25.2)
                    nutritionState = 1;
                else if (weight < 28.1)
                    nutritionState = 2;
                else if (weight < 49.1)
                    nutritionState = 3;
                else if (weight < 53.1)
                    nutritionState = 4;
                else if (weight >= 53.1)
                    nutritionState = 5;
                break;
            case 141:
                if (weight < 25.5)
                    nutritionState = 1;
                else if (weight < 28.5)
                    nutritionState = 2;
                else if (weight < 49.4)
                    nutritionState = 3;
                else if (weight < 53.3)
                    nutritionState = 4;
                else if (weight >= 53.3)
                    nutritionState = 5;
                break;
            case 142:
                if (weight < 25.8)
                    nutritionState = 1;
                else if (weight < 28.8)
                    nutritionState = 2;
                else if (weight < 49.7)
                    nutritionState = 3;
                else if (weight < 53.6)
                    nutritionState = 4;
                else if (weight >= 53.6)
                    nutritionState = 5;
                break;
            case 143:
                if (weight < 26.1)
                    nutritionState = 1;
                else if (weight < 29.1)
                    nutritionState = 2;
                else if (weight < 50)
                    nutritionState = 3;
                else if (weight < 53.9)
                    nutritionState = 4;
                else if (weight >= 53.9)
                    nutritionState = 5;
                break;
            // 144 - 155 ????? (13 ??)
            case 144:
                if (weight < 26.4)
                    nutritionState = 1;
                else if (weight < 29.4)
                    nutritionState = 2;
                else if (weight < 50.3)
                    nutritionState = 3;
                else if (weight < 54.1)
                    nutritionState = 4;
                else if (weight >= 54.1)
                    nutritionState = 5;
                break;
            case 145:
                if (weight < 26.7)
                    nutritionState = 1;
                else if (weight < 29.7)
                    nutritionState = 2;
                else if (weight < 50.6)
                    nutritionState = 3;
                else if (weight < 54.4)
                    nutritionState = 4;
                else if (weight >= 54.4)
                    nutritionState = 5;
                break;
            case 146:
                if (weight < 27)
                    nutritionState = 1;
                else if (weight < 30)
                    nutritionState = 2;
                else if (weight < 50.9)
                    nutritionState = 3;
                else if (weight < 54.7)
                    nutritionState = 4;
                else if (weight >= 54.7)
                    nutritionState = 5;
                break;
            case 147:
                if (weight < 27.3)
                    nutritionState = 1;
                else if (weight < 30.4)
                    nutritionState = 2;
                else if (weight < 51.1)
                    nutritionState = 3;
                else if (weight < 54.9)
                    nutritionState = 4;
                else if (weight >= 54.9)
                    nutritionState = 5;
                break;
            case 148:
                if (weight < 27.6)
                    nutritionState = 1;
                else if (weight < 30.7)
                    nutritionState = 2;
                else if (weight < 51.3)
                    nutritionState = 3;
                else if (weight < 55.1)
                    nutritionState = 4;
                else if (weight >= 55.1)
                    nutritionState = 5;
                break;
            case 149:
                if (weight < 27.9)
                    nutritionState = 1;
                else if (weight < 31)
                    nutritionState = 2;
                else if (weight < 51.7)
                    nutritionState = 3;
                else if (weight < 55.4)
                    nutritionState = 4;
                else if (weight >= 55.4)
                    nutritionState = 5;
                break;
            case 150:
                if (weight < 28.2)
                    nutritionState = 1;
                else if (weight < 31.3)
                    nutritionState = 2;
                else if (weight < 51.9)
                    nutritionState = 3;
                else if (weight < 55.6)
                    nutritionState = 4;
                else if (weight >= 55.6)
                    nutritionState = 5;
                break;
            case 151:
                if (weight < 28.5)
                    nutritionState = 1;
                else if (weight < 31.6)
                    nutritionState = 2;
                else if (weight < 52.1)
                    nutritionState = 3;
                else if (weight < 55.8)
                    nutritionState = 4;
                else if (weight >= 55.8)
                    nutritionState = 5;
                break;
            case 152:
                if (weight < 28.8)
                    nutritionState = 1;
                else if (weight < 31.9)
                    nutritionState = 2;
                else if (weight < 52.3)
                    nutritionState = 3;
                else if (weight < 56)
                    nutritionState = 4;
                else if (weight >= 56)
                    nutritionState = 5;
                break;
            case 153:
                if (weight < 29.1)
                    nutritionState = 1;
                else if (weight < 32.2)
                    nutritionState = 2;
                else if (weight < 52.6)
                    nutritionState = 3;
                else if (weight < 56.3)
                    nutritionState = 4;
                else if (weight >= 56.3)
                    nutritionState = 5;
                break;
            case 154:
                if (weight < 29.3)
                    nutritionState = 1;
                else if (weight < 32.4)
                    nutritionState = 2;
                else if (weight < 52.8)
                    nutritionState = 3;
                else if (weight < 56.5)
                    nutritionState = 4;
                else if (weight >= 56.5)
                    nutritionState = 5;
                break;
            case 155:
                if (weight < 29.6)
                    nutritionState = 1;
                else if (weight < 32.7)
                    nutritionState = 2;
                else if (weight < 53)
                    nutritionState = 3;
                else if (weight < 56.7)
                    nutritionState = 4;
                else if (weight >= 56.7)
                    nutritionState = 5;
                break;
            // :6 - 167 ????? (14 ??)
            case 156:
                if (weight < 29.9)
                    nutritionState = 1;
                else if (weight < 33)
                    nutritionState = 2;
                else if (weight < 53.2)
                    nutritionState = 3;
                else if (weight < 56.9)
                    nutritionState = 4;
                else if (weight >= 56.9)
                    nutritionState = 5;
                break;
            case 157:
                if (weight < 30.2)
                    nutritionState = 1;
                else if (weight < 33.2)
                    nutritionState = 2;
                else if (weight < 53.4)
                    nutritionState = 3;
                else if (weight < 57)
                    nutritionState = 4;
                else if (weight >= 57)
                    nutritionState = 5;
                break;
            case 158:
                if (weight < 30.4)
                    nutritionState = 1;
                else if (weight < 33.5)
                    nutritionState = 2;
                else if (weight < 53.6)
                    nutritionState = 3;
                else if (weight < 57.1)
                    nutritionState = 4;
                else if (weight >= 57.1)
                    nutritionState = 5;
                break;
            case 159:
                if (weight < 30.7)
                    nutritionState = 1;
                else if (weight < 33.7)
                    nutritionState = 2;
                else if (weight < 53.8)
                    nutritionState = 3;
                else if (weight < 57.3)
                    nutritionState = 4;
                else if (weight >= 57.3)
                    nutritionState = 5;
                break;
            case 160:
                if (weight < 31)
                    nutritionState = 1;
                else if (weight < 34)
                    nutritionState = 2;
                else if (weight < 54)
                    nutritionState = 3;
                else if (weight < 57.5)
                    nutritionState = 4;
                else if (weight >= 57.5)
                    nutritionState = 5;
                break;
            case 161:
                if (weight < 31.3)
                    nutritionState = 1;
                else if (weight < 34.3)
                    nutritionState = 2;
                else if (weight < 54.1)
                    nutritionState = 3;
                else if (weight < 57.6)
                    nutritionState = 4;
                else if (weight >= 57.6)
                    nutritionState = 5;
                break;
            case 162:
                if (weight < 31.5)
                    nutritionState = 1;
                else if (weight < 34.5)
                    nutritionState = 2;
                else if (weight < 54.3)
                    nutritionState = 3;
                else if (weight < 57.8)
                    nutritionState = 4;
                else if (weight >= 57.8)
                    nutritionState = 5;
                break;
            case 163:
                if (weight < 31.8)
                    nutritionState = 1;
                else if (weight < 34.8)
                    nutritionState = 2;
                else if (weight < 54.5)
                    nutritionState = 3;
                else if (weight < 58)
                    nutritionState = 4;
                else if (weight >= 58)
                    nutritionState = 5;
                break;
            case 164:
                if (weight < 32.1)
                    nutritionState = 1;
                else if (weight < 35.1)
                    nutritionState = 2;
                else if (weight < 54.6)
                    nutritionState = 3;
                else if (weight < 58.1)
                    nutritionState = 4;
                else if (weight >= 58.1)
                    nutritionState = 5;
                break;
            case 165:
                if (weight < 32.5)
                    nutritionState = 1;
                else if (weight < 35.4)
                    nutritionState = 2;
                else if (weight < 54.8)
                    nutritionState = 3;
                else if (weight < 58.2)
                    nutritionState = 4;
                else if (weight >= 58.2)
                    nutritionState = 5;
                break;
            case 166:
                if (weight < 32.8)
                    nutritionState = 1;
                else if (weight < 35.7)
                    nutritionState = 2;
                else if (weight < 55)
                    nutritionState = 3;
                else if (weight < 58.4)
                    nutritionState = 4;
                else if (weight >= 58.4)
                    nutritionState = 5;
                break;
            case 167:
                if (weight < 33.1)
                    nutritionState = 1;
                else if (weight < 36)
                    nutritionState = 2;
                else if (weight < 55.1)
                    nutritionState = 3;
                else if (weight < 58.5)
                    nutritionState = 4;
                else if (weight >= 58.5)
                    nutritionState = 5;
                break;
            // 168 - 179 ????? (15 ??)
            case 168:
                if (weight < 33.4)
                    nutritionState = 1;
                else if (weight < 36.3)
                    nutritionState = 2;
                else if (weight < 55.3)
                    nutritionState = 3;
                else if (weight < 58.7)
                    nutritionState = 4;
                else if (weight >= 58.7)
                    nutritionState = 5;
                break;
            case 169:
                if (weight < 33.6)
                    nutritionState = 1;
                else if (weight < 36.5)
                    nutritionState = 2;
                else if (weight < 55.4)
                    nutritionState = 3;
                else if (weight < 58.8)
                    nutritionState = 4;
                else if (weight >= 58.8)
                    nutritionState = 5;
                break;
            case 170:
                if (weight < 33.9)
                    nutritionState = 1;
                else if (weight < 36.8)
                    nutritionState = 2;
                else if (weight < 55.5)
                    nutritionState = 3;
                else if (weight < 58.9)
                    nutritionState = 4;
                else if (weight >= 58.9)
                    nutritionState = 5;
                break;
            case 171:
                if (weight < 34.1)
                    nutritionState = 1;
                else if (weight < 37)
                    nutritionState = 2;
                else if (weight < 55.6)
                    nutritionState = 3;
                else if (weight < 59)
                    nutritionState = 4;
                else if (weight >= 59)
                    nutritionState = 5;
                break;
            case 172:
                if (weight < 34.4)
                    nutritionState = 1;
                else if (weight < 37.2)
                    nutritionState = 2;
                else if (weight < 55.7)
                    nutritionState = 3;
                else if (weight < 59.1)
                    nutritionState = 4;
                else if (weight >= 59.1)
                    nutritionState = 5;
                break;
            case 173:
                if (weight < 34.6)
                    nutritionState = 1;
                else if (weight < 37.4)
                    nutritionState = 2;
                else if (weight < 55.9)
                    nutritionState = 3;
                else if (weight < 59.2)
                    nutritionState = 4;
                else if (weight >= 59.2)
                    nutritionState = 5;
                break;
            case 174:
                if (weight < 34.8)
                    nutritionState = 1;
                else if (weight < 37.6)
                    nutritionState = 2;
                else if (weight < 56)
                    nutritionState = 3;
                else if (weight < 59.3)
                    nutritionState = 4;
                else if (weight >= 59.3)
                    nutritionState = 5;
                break;
            case 175:
                if (weight < 35)
                    nutritionState = 1;
                else if (weight < 37.8)
                    nutritionState = 2;
                else if (weight < 56.1)
                    nutritionState = 3;
                else if (weight < 59.4)
                    nutritionState = 4;
                else if (weight >= 59.4)
                    nutritionState = 5;
                break;
            case 176:
                if (weight < 35.2)
                    nutritionState = 1;
                else if (weight < 38)
                    nutritionState = 2;
                else if (weight < 56.2)
                    nutritionState = 3;
                else if (weight < 59.5)
                    nutritionState = 4;
                else if (weight >= 59.5)
                    nutritionState = 5;
                break;
            case 177:
                if (weight < 35.3)
                    nutritionState = 1;
                else if (weight < 38.1)
                    nutritionState = 2;
                else if (weight < 56.3)
                    nutritionState = 3;
                else if (weight < 59.6)
                    nutritionState = 4;
                else if (weight >= 59.6)
                    nutritionState = 5;
                break;
            case 178:
                if (weight < 35.5)
                    nutritionState = 1;
                else if (weight < 38.3)
                    nutritionState = 2;
                else if (weight < 56.4)
                    nutritionState = 3;
                else if (weight < 59.7)
                    nutritionState = 4;
                else if (weight >= 59.7)
                    nutritionState = 5;
                break;
            case 179:
                if (weight < 35.8)
                    nutritionState = 1;
                else if (weight < 38.5)
                    nutritionState = 2;
                else if (weight < 56.5)
                    nutritionState = 3;
                else if (weight < 59.8)
                    nutritionState = 4;
                else if (weight >= 59.8)
                    nutritionState = 5;
                break;
            // 180 - 191 ????? (16 ??)
            case 180:
                if (weight < 35.8)
                    nutritionState = 1;
                else if (weight < 38.6)
                    nutritionState = 2;
                else if (weight < 56.6)
                    nutritionState = 3;
                else if (weight < 59.8)
                    nutritionState = 4;
                else if (weight >= 59.8)
                    nutritionState = 5;
                break;
            case 181:
                if (weight < 36.1)
                    nutritionState = 1;
                else if (weight < 38.8)
                    nutritionState = 2;
                else if (weight < 56.7)
                    nutritionState = 3;
                else if (weight < 59.9)
                    nutritionState = 4;
                else if (weight >= 59.9)
                    nutritionState = 5;
                break;
            case 182:
                if (weight < 36.3)
                    nutritionState = 1;
                else if (weight < 38.9)
                    nutritionState = 2;
                else if (weight < 56.8)
                    nutritionState = 3;
                else if (weight < 60)
                    nutritionState = 4;
                else if (weight >= 60)
                    nutritionState = 5;
                break;
            case 183:
                if (weight < 36.4)
                    nutritionState = 1;
                else if (weight < 39)
                    nutritionState = 2;
                else if (weight < 56.9)
                    nutritionState = 3;
                else if (weight < 60.1)
                    nutritionState = 4;
                else if (weight >= 60.1)
                    nutritionState = 5;
                break;
            case 184:
                if (weight < 36.5)
                    nutritionState = 1;
                else if (weight < 39.1)
                    nutritionState = 2;
                else if (weight < 56.9)
                    nutritionState = 3;
                else if (weight < 60.1)
                    nutritionState = 4;
                else if (weight >= 60.1)
                    nutritionState = 5;
                break;
            case 185:
                if (weight < 36.7)
                    nutritionState = 1;
                else if (weight < 39.3)
                    nutritionState = 2;
                else if (weight < 57)
                    nutritionState = 3;
                else if (weight < 60.2)
                    nutritionState = 4;
                else if (weight >= 60.2)
                    nutritionState = 5;
                break;
            case 186:
                if (weight < 36.8)
                    nutritionState = 1;
                else if (weight < 39.4)
                    nutritionState = 2;
                else if (weight < 57)
                    nutritionState = 3;
                else if (weight < 60.2)
                    nutritionState = 4;
                else if (weight >= 60.2)
                    nutritionState = 5;
                break;
            case 187:
                if (weight < 37)
                    nutritionState = 1;
                else if (weight < 39.6)
                    nutritionState = 2;
                else if (weight < 57.1)
                    nutritionState = 3;
                else if (weight < 60.3)
                    nutritionState = 4;
                else if (weight >= 60.3)
                    nutritionState = 5;
                break;
            case 188:
                if (weight < 37.1)
                    nutritionState = 1;
                else if (weight < 39.7)
                    nutritionState = 2;
                else if (weight < 57.1)
                    nutritionState = 3;
                else if (weight < 60.3)
                    nutritionState = 4;
                else if (weight >= 60.3)
                    nutritionState = 5;
                break;
            case 189:
                if (weight < 37.2)
                    nutritionState = 1;
                else if (weight < 39.8)
                    nutritionState = 2;
                else if (weight < 57.2)
                    nutritionState = 3;
                else if (weight < 60.3)
                    nutritionState = 4;
                else if (weight >= 60.3)
                    nutritionState = 5;
                break;
            case 190:
                if (weight < 37.3)
                    nutritionState = 1;
                else if (weight < 39.9)
                    nutritionState = 2;
                else if (weight < 57.2)
                    nutritionState = 3;
                else if (weight < 60.3)
                    nutritionState = 4;
                else if (weight >= 60.3)
                    nutritionState = 5;
                break;
            case 191:
                if (weight < 37.4)
                    nutritionState = 1;
                else if (weight < 40)
                    nutritionState = 2;
                else if (weight < 57.3)
                    nutritionState = 3;
                else if (weight < 60.4)
                    nutritionState = 4;
                else if (weight >= 60.4)
                    nutritionState = 5;
                break;
            // 192 - 203 ????? (17 ??)
            case 192:
                if (weight < 37.5)
                    nutritionState = 1;
                else if (weight < 40.1)
                    nutritionState = 2;
                else if (weight < 57.3)
                    nutritionState = 3;
                else if (weight < 60.4)
                    nutritionState = 4;
                else if (weight >= 60.4)
                    nutritionState = 5;
                break;
            case 193:
                if (weight < 37.6)
                    nutritionState = 1;
                else if (weight < 40.2)
                    nutritionState = 2;
                else if (weight < 57.3)
                    nutritionState = 3;
                else if (weight < 60.4)
                    nutritionState = 4;
                else if (weight >= 60.4)
                    nutritionState = 5;
                break;
            case 194:
                if (weight < 37.7)
                    nutritionState = 1;
                else if (weight < 40.3)
                    nutritionState = 2;
                else if (weight < 57.4)
                    nutritionState = 3;
                else if (weight < 60.5)
                    nutritionState = 4;
                else if (weight >= 60.5)
                    nutritionState = 5;
                break;
            case 195:
                if (weight < 37.8)
                    nutritionState = 1;
                else if (weight < 40.3)
                    nutritionState = 2;
                else if (weight < 57.4)
                    nutritionState = 3;
                else if (weight < 60.5)
                    nutritionState = 4;
                else if (weight >= 60.5)
                    nutritionState = 5;
                break;
            case 196:
                if (weight < 37.9)
                    nutritionState = 1;
                else if (weight < 40.4)
                    nutritionState = 2;
                else if (weight < 57.5)
                    nutritionState = 3;
                else if (weight < 60.5)
                    nutritionState = 4;
                else if (weight >= 60.5)
                    nutritionState = 5;
                break;
            case 197:
                if (weight < 38)
                    nutritionState = 1;
                else if (weight < 40.5)
                    nutritionState = 2;
                else if (weight < 57.5)
                    nutritionState = 3;
                else if (weight < 60.6)
                    nutritionState = 4;
                else if (weight >= 60.6)
                    nutritionState = 5;
                break;
            case 198:
                if (weight < 38)
                    nutritionState = 1;
                else if (weight < 40.5)
                    nutritionState = 2;
                else if (weight < 57.6)
                    nutritionState = 3;
                else if (weight < 60.6)
                    nutritionState = 4;
                else if (weight >= 60.6)
                    nutritionState = 5;
                break;
            case 199:
                if (weight < 38.1)
                    nutritionState = 1;
                else if (weight < 40.6)
                    nutritionState = 2;
                else if (weight < 57.6)
                    nutritionState = 3;
                else if (weight < 60.6)
                    nutritionState = 4;
                else if (weight >= 60.6)
                    nutritionState = 5;
                break;
            case 200:
                if (weight < 38.1)
                    nutritionState = 1;
                else if (weight < 40.6)
                    nutritionState = 2;
                else if (weight < 57.6)
                    nutritionState = 3;
                else if (weight < 60.7)
                    nutritionState = 4;
                else if (weight >= 60.7)
                    nutritionState = 5;
                break;
            case 201:
                if (weight < 38.2)
                    nutritionState = 1;
                else if (weight < 40.7)
                    nutritionState = 2;
                else if (weight < 57.7)
                    nutritionState = 3;
                else if (weight < 60.7)
                    nutritionState = 4;
                else if (weight >= 60.7)
                    nutritionState = 5;
                break;
            case 202:
                if (weight < 38.2)
                    nutritionState = 1;
                else if (weight < 40.7)
                    nutritionState = 2;
                else if (weight < 57.7)
                    nutritionState = 3;
                else if (weight < 60.7)
                    nutritionState = 4;
                else if (weight >= 60.7)
                    nutritionState = 5;
                break;
            case 203:
                if (weight < 38.3)
                    nutritionState = 1;
                else if (weight < 40.8)
                    nutritionState = 2;
                else if (weight < 57.7)
                    nutritionState = 3;
                else if (weight < 60.7)
                    nutritionState = 4;
                else if (weight >= 60.7)
                    nutritionState = 5;
                break;
            // 204 - 215 ????? (18 ??)
            case 204:
                if (weight < 38.3)
                    nutritionState = 1;
                else if (weight < 40.8)
                    nutritionState = 2;
                else if (weight < 57.7)
                    nutritionState = 3;
                else if (weight < 60.7)
                    nutritionState = 4;
                else if (weight >= 60.7)
                    nutritionState = 5;
                break;
            case 205:
                if (weight < 38.4)
                    nutritionState = 1;
                else if (weight < 40.9)
                    nutritionState = 2;
                else if (weight < 57.7)
                    nutritionState = 3;
                else if (weight < 60.7)
                    nutritionState = 4;
                else if (weight >= 60.7)
                    nutritionState = 5;
                break;
            case 206:
                if (weight < 38.4)
                    nutritionState = 1;
                else if (weight < 40.9)
                    nutritionState = 2;
                else if (weight < 57.7)
                    nutritionState = 3;
                else if (weight < 60.7)
                    nutritionState = 4;
                else if (weight >= 60.7)
                    nutritionState = 5;
                break;
            case 207:
                if (weight < 38.4)
                    nutritionState = 1;
                else if (weight < 40.9)
                    nutritionState = 2;
                else if (weight < 57.7)
                    nutritionState = 3;
                else if (weight < 60.7)
                    nutritionState = 4;
                else if (weight >= 60.7)
                    nutritionState = 5;
                break;
            case 208:
                if (weight < 38.5)
                    nutritionState = 1;
                else if (weight < 41)
                    nutritionState = 2;
                else if (weight < 57.8)
                    nutritionState = 3;
                else if (weight < 60.8)
                    nutritionState = 4;
                else if (weight >= 60.8)
                    nutritionState = 5;
                break;
            case 209:
                if (weight < 38.5)
                    nutritionState = 1;
                else if (weight < 41)
                    nutritionState = 2;
                else if (weight < 57.8)
                    nutritionState = 3;
                else if (weight < 60.8)
                    nutritionState = 4;
                else if (weight >= 60.8)
                    nutritionState = 5;
                break;
            case 210:
                if (weight < 38.6)
                    nutritionState = 1;
                else if (weight < 41.1)
                    nutritionState = 2;
                else if (weight < 57.8)
                    nutritionState = 3;
                else if (weight < 60.8)
                    nutritionState = 4;
                else if (weight >= 60.8)
                    nutritionState = 5;
                break;
            case 211:
                if (weight < 38.6)
                    nutritionState = 1;
                else if (weight < 41.1)
                    nutritionState = 2;
                else if (weight < 57.8)
                    nutritionState = 3;
                else if (weight < 60.8)
                    nutritionState = 4;
                else if (weight >= 60.8)
                    nutritionState = 5;
                break;
            case 212:
                if (weight < 38.7)
                    nutritionState = 1;
                else if (weight < 41.2)
                    nutritionState = 2;
                else if (weight < 57.8)
                    nutritionState = 3;
                else if (weight < 60.8)
                    nutritionState = 4;
                else if (weight >= 60.8)
                    nutritionState = 5;
                break;
            case 213:
                if (weight < 38.7)
                    nutritionState = 1;
                else if (weight < 41.2)
                    nutritionState = 2;
                else if (weight < 57.8)
                    nutritionState = 3;
                else if (weight < 60.8)
                    nutritionState = 4;
                else if (weight >= 60.8)
                    nutritionState = 5;
                break;
            case 214:
                if (weight < 38.7)
                    nutritionState = 1;
                else if (weight < 41.2)
                    nutritionState = 2;
                else if (weight < 57.8)
                    nutritionState = 3;
                else if (weight < 60.8)
                    nutritionState = 4;
                else if (weight >= 60.8)
                    nutritionState = 5;
                break;
            case 215:
                if (weight < 38.8)
                    nutritionState = 1;
                else if (weight < 41.3)
                    nutritionState = 2;
                else if (weight < 57.8)
                    nutritionState = 3;
                else if (weight < 60.8)
                    nutritionState = 4;
                else if (weight >= 60.8)
                    nutritionState = 5;
                break;
            // 216 - 227 ????? (19 ??)
            case 216:
                if (weight < 38.8)
                    nutritionState = 1;
                else if (weight < 41.3)
                    nutritionState = 2;
                else if (weight < 57.8)
                    nutritionState = 3;
                else if (weight < 60.8)
                    nutritionState = 4;
                else if (weight >= 60.8)
                    nutritionState = 5;
                break;
            case 217:
            case 218:
                if (weight < 38.9)
                    nutritionState = 1;
                else if (weight < 41.4)
                    nutritionState = 2;
                else if (weight < 57.8)
                    nutritionState = 3;
                else if (weight < 60.8)
                    nutritionState = 4;
                else if (weight >= 60.8)
                    nutritionState = 5;
                break;
            case 219:
            case 220:
            case 221:
                if (weight < 39)
                    nutritionState = 1;
                else if (weight < 41.5)
                    nutritionState = 2;
                else if (weight < 57.8)
                    nutritionState = 3;
                else if (weight < 60.8)
                    nutritionState = 4;
                else if (weight >= 60.8)
                    nutritionState = 5;
                break;
            case 222:
            case 223:
            case 224:
            case 225:
                if (weight < 39.1)
                    nutritionState = 1;
                else if (weight < 41.6)
                    nutritionState = 2;
                else if (weight < 57.9)
                    nutritionState = 3;
                else if (weight < 60.8)
                    nutritionState = 4;
                else if (weight >= 60.8)
                    nutritionState = 5;
                break;
            case 226:
            case 227:
                if (weight < 39.2)
                    nutritionState = 1;
                else if (weight < 41.7)
                    nutritionState = 2;
                else if (weight < 57.9)
                    nutritionState = 3;
                else if (weight < 60.8)
                    nutritionState = 4;
                else if (weight >= 60.8)
                    nutritionState = 5;
                break;
            case 12987:
            case 12988:
            case 12989: // ??????????????(?????????? ????????????????????????
                nutritionState = 9;
                break; // ???????????
            default:
                nutritionState = 0;
                break; // ???????????
        }

        return nutritionState;
    }

    // ˭ԧ : ��ǹ�٧ / ���˹ѡ
    public static int GetNutritionStateFemaleAgeLessThen18WeightHeight(
            double weight, int height) {
        int nutritionState = 0;

        switch (height) {
            case 50:
                if (weight < 2.7)
                    nutritionState = 1;
                else if (weight < 2.8)
                    nutritionState = 2;
                else if (weight < 3.9)
                    nutritionState = 3;
                else if (weight < 4)
                    nutritionState = 4;
                else if (weight < 4.3)
                    nutritionState = 5;
                else if (weight >= 4.3)
                    nutritionState = 6;
                break;
            case 51:
                if (weight < 2.9)
                    nutritionState = 1;
                else if (weight < 3)
                    nutritionState = 2;
                else if (weight < 4.2)
                    nutritionState = 3;
                else if (weight < 4.3)
                    nutritionState = 4;
                else if (weight < 4.6)
                    nutritionState = 5;
                else if (weight >= 4.6)
                    nutritionState = 6;
                break;
            case 52:
                if (weight < 3.1)
                    nutritionState = 1;
                else if (weight < 3.3)
                    nutritionState = 2;
                else if (weight < 4.4)
                    nutritionState = 3;
                else if (weight < 4.6)
                    nutritionState = 4;
                else if (weight < 4.9)
                    nutritionState = 5;
                else if (weight >= 4.9)
                    nutritionState = 6;
                break;
            case 53:
                if (weight < 3.3)
                    nutritionState = 1;
                else if (weight < 3.5)
                    nutritionState = 2;
                else if (weight < 4.7)
                    nutritionState = 3;
                else if (weight < 4.9)
                    nutritionState = 4;
                else if (weight < 5.2)
                    nutritionState = 5;
                else if (weight >= 5.2)
                    nutritionState = 6;
                break;
            case 54:
                if (weight < 3.5)
                    nutritionState = 1;
                else if (weight < 3.7)
                    nutritionState = 2;
                else if (weight < 5)
                    nutritionState = 3;
                else if (weight < 5.1)
                    nutritionState = 4;
                else if (weight < 5.5)
                    nutritionState = 5;
                else if (weight >= 5.5)
                    nutritionState = 6;
                break;
            case 55:
                if (weight < 3.7)
                    nutritionState = 1;
                else if (weight < 3.9)
                    nutritionState = 2;
                else if (weight < 5.2)
                    nutritionState = 3;
                else if (weight < 5.4)
                    nutritionState = 4;
                else if (weight < 5.8)
                    nutritionState = 5;
                else if (weight >= 5.8)
                    nutritionState = 6;
                break;
            case 56:
                if (weight < 3.9)
                    nutritionState = 1;
                else if (weight < 4.1)
                    nutritionState = 2;
                else if (weight < 5.5)
                    nutritionState = 3;
                else if (weight < 5.7)
                    nutritionState = 4;
                else if (weight < 6.1)
                    nutritionState = 5;
                else if (weight >= 6.1)
                    nutritionState = 6;
                break;
            case 57:
                if (weight < 4.1)
                    nutritionState = 1;
                else if (weight < 4.3)
                    nutritionState = 2;
                else if (weight < 5.7)
                    nutritionState = 3;
                else if (weight < 6)
                    nutritionState = 4;
                else if (weight < 6.4)
                    nutritionState = 5;
                else if (weight >= 6.4)
                    nutritionState = 6;
                break;
            case 58:
                if (weight < 4.3)
                    nutritionState = 1;
                else if (weight < 4.5)
                    nutritionState = 2;
                else if (weight < 6)
                    nutritionState = 3;
                else if (weight < 6.2)
                    nutritionState = 4;
                else if (weight < 6.7)
                    nutritionState = 5;
                else if (weight >= 6.7)
                    nutritionState = 6;
                break;
            case 59:
                if (weight < 4.5)
                    nutritionState = 1;
                else if (weight < 4.8)
                    nutritionState = 2;
                else if (weight < 6.3)
                    nutritionState = 3;
                else if (weight < 6.5)
                    nutritionState = 4;
                else if (weight < 7)
                    nutritionState = 5;
                else if (weight >= 7)
                    nutritionState = 6;
                break;
            case 60:
                if (weight < 4.7)
                    nutritionState = 1;
                else if (weight < 5)
                    nutritionState = 2;
                else if (weight < 6.5)
                    nutritionState = 3;
                else if (weight < 6.8)
                    nutritionState = 4;
                else if (weight < 7.3)
                    nutritionState = 5;
                else if (weight >= 7.3)
                    nutritionState = 6;
                break;
            case 61:
                if (weight < 4.9)
                    nutritionState = 1;
                else if (weight < 5.2)
                    nutritionState = 2;
                else if (weight < 6.8)
                    nutritionState = 3;
                else if (weight < 7)
                    nutritionState = 4;
                else if (weight < 7.5)
                    nutritionState = 5;
                else if (weight >= 7.5)
                    nutritionState = 6;
                break;
            case 62:
                if (weight < 5.1)
                    nutritionState = 1;
                else if (weight < 5.4)
                    nutritionState = 2;
                else if (weight < 7)
                    nutritionState = 3;
                else if (weight < 7.3)
                    nutritionState = 4;
                else if (weight < 7.8)
                    nutritionState = 5;
                else if (weight >= 7.8)
                    nutritionState = 6;
                break;
            case 63:
                if (weight < 5.3)
                    nutritionState = 1;
                else if (weight < 5.6)
                    nutritionState = 2;
                else if (weight < 7.3)
                    nutritionState = 3;
                else if (weight < 7.6)
                    nutritionState = 4;
                else if (weight < 8.1)
                    nutritionState = 5;
                else if (weight >= 8.1)
                    nutritionState = 6;
                break;
            case 64:
                if (weight < 5.5)
                    nutritionState = 1;
                else if (weight < 5.8)
                    nutritionState = 2;
                else if (weight < 7.5)
                    nutritionState = 3;
                else if (weight < 7.8)
                    nutritionState = 4;
                else if (weight < 8.4)
                    nutritionState = 5;
                else if (weight >= 8.4)
                    nutritionState = 6;
                break;
            case 65:
                if (weight < 5.7)
                    nutritionState = 1;
                else if (weight < 6)
                    nutritionState = 2;
                else if (weight < 7.8)
                    nutritionState = 3;
                else if (weight < 8.1)
                    nutritionState = 4;
                else if (weight < 8.6)
                    nutritionState = 5;
                else if (weight >= 8.6)
                    nutritionState = 6;
                break;
            case 66:
                if (weight < 5.9)
                    nutritionState = 1;
                else if (weight < 6.2)
                    nutritionState = 2;
                else if (weight < 8)
                    nutritionState = 3;
                else if (weight < 8.3)
                    nutritionState = 4;
                else if (weight < 8.9)
                    nutritionState = 5;
                else if (weight >= 8.9)
                    nutritionState = 6;
                break;
            case 67:
                if (weight < 6.1)
                    nutritionState = 1;
                else if (weight < 6.4)
                    nutritionState = 2;
                else if (weight < 8.3)
                    nutritionState = 3;
                else if (weight < 8.6)
                    nutritionState = 4;
                else if (weight < 9.2)
                    nutritionState = 5;
                else if (weight >= 9.2)
                    nutritionState = 6;
                break;
            case 68:
                if (weight < 6.3)
                    nutritionState = 1;
                else if (weight < 6.6)
                    nutritionState = 2;
                else if (weight < 8.5)
                    nutritionState = 3;
                else if (weight < 8.8)
                    nutritionState = 4;
                else if (weight < 9.4)
                    nutritionState = 5;
                else if (weight >= 9.4)
                    nutritionState = 6;
                break;
            case 69:
                if (weight < 6.5)
                    nutritionState = 1;
                else if (weight < 6.8)
                    nutritionState = 2;
                else if (weight < 8.8)
                    nutritionState = 3;
                else if (weight < 9.1)
                    nutritionState = 4;
                else if (weight < 9.7)
                    nutritionState = 5;
                else if (weight >= 9.7)
                    nutritionState = 6;
                break;
            case 70:
                if (weight < 6.7)
                    nutritionState = 1;
                else if (weight < 7)
                    nutritionState = 2;
                else if (weight < 9)
                    nutritionState = 3;
                else if (weight < 9.3)
                    nutritionState = 4;
                else if (weight < 10)
                    nutritionState = 5;
                else if (weight >= 10)
                    nutritionState = 6;
                break;
            case 71:
                if (weight < 6.9)
                    nutritionState = 1;
                else if (weight < 7.2)
                    nutritionState = 2;
                else if (weight < 9.2)
                    nutritionState = 3;
                else if (weight < 9.6)
                    nutritionState = 4;
                else if (weight < 10.2)
                    nutritionState = 5;
                else if (weight >= 10.2)
                    nutritionState = 6;
                break;
            case 72:
                if (weight < 7.1)
                    nutritionState = 1;
                else if (weight < 7.4)
                    nutritionState = 2;
                else if (weight < 9.5)
                    nutritionState = 3;
                else if (weight < 9.8)
                    nutritionState = 4;
                else if (weight < 10.5)
                    nutritionState = 5;
                else if (weight >= 10.5)
                    nutritionState = 6;
                break;
            case 73:
                if (weight < 7.3)
                    nutritionState = 1;
                else if (weight < 7.6)
                    nutritionState = 2;
                else if (weight < 9.7)
                    nutritionState = 3;
                else if (weight < 10.1)
                    nutritionState = 4;
                else if (weight < 10.7)
                    nutritionState = 5;
                else if (weight >= 10.7)
                    nutritionState = 6;
                break;
            case 74:
                if (weight < 7.5)
                    nutritionState = 1;
                else if (weight < 7.8)
                    nutritionState = 2;
                else if (weight < 9.9)
                    nutritionState = 3;
                else if (weight < 10.3)
                    nutritionState = 4;
                else if (weight < 11)
                    nutritionState = 5;
                else if (weight >= 11)
                    nutritionState = 6;
                break;
            case 75:
                if (weight < 7.7)
                    nutritionState = 1;
                else if (weight < 8)
                    nutritionState = 2;
                else if (weight < 10.2)
                    nutritionState = 3;
                else if (weight < 10.5)
                    nutritionState = 4;
                else if (weight < 11.2)
                    nutritionState = 5;
                else if (weight >= 11.2)
                    nutritionState = 6;
                break;
            case 76:
                if (weight < 7.9)
                    nutritionState = 1;
                else if (weight < 8.2)
                    nutritionState = 2;
                else if (weight < 10.4)
                    nutritionState = 3;
                else if (weight < 10.8)
                    nutritionState = 4;
                else if (weight < 11.5)
                    nutritionState = 5;
                else if (weight >= 11.5)
                    nutritionState = 6;
                break;
            case 77:
                if (weight < 8.1)
                    nutritionState = 1;
                else if (weight < 8.4)
                    nutritionState = 2;
                else if (weight < 10.6)
                    nutritionState = 3;
                else if (weight < 11)
                    nutritionState = 4;
                else if (weight < 11.7)
                    nutritionState = 5;
                else if (weight >= 11.7)
                    nutritionState = 6;
                break;
            case 78:
                if (weight < 8.2)
                    nutritionState = 1;
                else if (weight < 8.6)
                    nutritionState = 2;
                else if (weight < 10.9)
                    nutritionState = 3;
                else if (weight < 11.2)
                    nutritionState = 4;
                else if (weight < 11.9)
                    nutritionState = 5;
                else if (weight >= 11.9)
                    nutritionState = 6;
                break;
            case 79:
                if (weight < 8.4)
                    nutritionState = 1;
                else if (weight < 8.8)
                    nutritionState = 2;
                else if (weight < 11.1)
                    nutritionState = 3;
                else if (weight < 11.4)
                    nutritionState = 4;
                else if (weight < 12.2)
                    nutritionState = 5;
                else if (weight >= 12.2)
                    nutritionState = 6;
                break;
            case 80:
                if (weight < 8.6)
                    nutritionState = 1;
                else if (weight < 9)
                    nutritionState = 2;
                else if (weight < 11.3)
                    nutritionState = 3;
                else if (weight < 11.7)
                    nutritionState = 4;
                else if (weight < 12.4)
                    nutritionState = 5;
                else if (weight >= 12.4)
                    nutritionState = 6;
                break;
            case 81:
                if (weight < 8.8)
                    nutritionState = 1;
                else if (weight < 9.2)
                    nutritionState = 2;
                else if (weight < 11.5)
                    nutritionState = 3;
                else if (weight < 11.9)
                    nutritionState = 4;
                else if (weight < 12.7)
                    nutritionState = 5;
                else if (weight >= 12.7)
                    nutritionState = 6;
                break;
            case 82:
                if (weight < 8.9)
                    nutritionState = 1;
                else if (weight < 9.3)
                    nutritionState = 2;
                else if (weight < 11.8)
                    nutritionState = 3;
                else if (weight < 12.1)
                    nutritionState = 4;
                else if (weight < 12.9)
                    nutritionState = 5;
                else if (weight >= 12.9)
                    nutritionState = 6;
                break;
            case 83:
                if (weight < 9.1)
                    nutritionState = 1;
                else if (weight < 9.5)
                    nutritionState = 2;
                else if (weight < 12)
                    nutritionState = 3;
                else if (weight < 12.4)
                    nutritionState = 4;
                else if (weight < 13.1)
                    nutritionState = 5;
                else if (weight >= 13.1)
                    nutritionState = 6;
                break;
            case 84:
                if (weight < 9.3)
                    nutritionState = 1;
                else if (weight < 9.7)
                    nutritionState = 2;
                else if (weight < 12.2)
                    nutritionState = 3;
                else if (weight < 12.6)
                    nutritionState = 4;
                else if (weight < 13.4)
                    nutritionState = 5;
                else if (weight >= 13.4)
                    nutritionState = 6;
                break;
            case 85:
                if (weight < 9.8)
                    nutritionState = 1;
                else if (weight < 10.2)
                    nutritionState = 2;
                else if (weight < 13.6)
                    nutritionState = 3;
                else if (weight < 14.3)
                    nutritionState = 4;
                else if (weight < 15.7)
                    nutritionState = 5;
                else if (weight >= 15.7)
                    nutritionState = 6;
                break;
            case 86:
                if (weight < 10)
                    nutritionState = 1;
                else if (weight < 10.4)
                    nutritionState = 2;
                else if (weight < 13.9)
                    nutritionState = 3;
                else if (weight < 14.6)
                    nutritionState = 4;
                else if (weight < 16)
                    nutritionState = 5;
                else if (weight >= 16)
                    nutritionState = 6;
                break;
            case 87:
                if (weight < 10.2)
                    nutritionState = 1;
                else if (weight < 10.6)
                    nutritionState = 2;
                else if (weight < 14.1)
                    nutritionState = 3;
                else if (weight < 14.8)
                    nutritionState = 4;
                else if (weight < 16.1)
                    nutritionState = 5;
                else if (weight >= 16.1)
                    nutritionState = 6;
                break;
            case 88:
                if (weight < 10.4)
                    nutritionState = 1;
                else if (weight < 10.8)
                    nutritionState = 2;
                else if (weight < 14.4)
                    nutritionState = 3;
                else if (weight < 15.1)
                    nutritionState = 4;
                else if (weight < 16.5)
                    nutritionState = 5;
                else if (weight >= 16.5)
                    nutritionState = 6;
                break;
            case 89:
                if (weight < 10.6)
                    nutritionState = 1;
                else if (weight < 11)
                    nutritionState = 2;
                else if (weight < 14.7)
                    nutritionState = 3;
                else if (weight < 15.4)
                    nutritionState = 4;
                else if (weight < 16.8)
                    nutritionState = 5;
                else if (weight >= 16.8)
                    nutritionState = 6;
                break;
            case 90:
                if (weight < 10.8)
                    nutritionState = 1;
                else if (weight < 11.3)
                    nutritionState = 2;
                else if (weight < 14.9)
                    nutritionState = 3;
                else if (weight < 15.6)
                    nutritionState = 4;
                else if (weight < 16.9)
                    nutritionState = 5;
                else if (weight >= 16.9)
                    nutritionState = 6;
                break;
            case 91:
                if (weight < 11)
                    nutritionState = 1;
                else if (weight < 11.5)
                    nutritionState = 2;
                else if (weight < 15.2)
                    nutritionState = 3;
                else if (weight < 15.9)
                    nutritionState = 4;
                else if (weight < 17.2)
                    nutritionState = 5;
                else if (weight >= 17.2)
                    nutritionState = 6;
                break;
            case 92:
                if (weight < 11.2)
                    nutritionState = 1;
                else if (weight < 11.7)
                    nutritionState = 2;
                else if (weight < 15.5)
                    nutritionState = 3;
                else if (weight < 16.2)
                    nutritionState = 4;
                else if (weight < 17.6)
                    nutritionState = 5;
                else if (weight >= 17.6)
                    nutritionState = 6;
                break;
            case 93:
                if (weight < 11.4)
                    nutritionState = 1;
                else if (weight < 11.9)
                    nutritionState = 2;
                else if (weight < 15.8)
                    nutritionState = 3;
                else if (weight < 16.5)
                    nutritionState = 4;
                else if (weight < 17.9)
                    nutritionState = 5;
                else if (weight >= 17.9)
                    nutritionState = 6;
                break;
            case 94:
                if (weight < 11.6)
                    nutritionState = 1;
                else if (weight < 12.1)
                    nutritionState = 2;
                else if (weight < 16.1)
                    nutritionState = 3;
                else if (weight < 16.8)
                    nutritionState = 4;
                else if (weight < 18.2)
                    nutritionState = 5;
                else if (weight >= 18.2)
                    nutritionState = 6;
                break;
            case 95:
                if (weight < 11.8)
                    nutritionState = 1;
                else if (weight < 12.3)
                    nutritionState = 2;
                else if (weight < 16.3)
                    nutritionState = 3;
                else if (weight < 17.1)
                    nutritionState = 4;
                else if (weight < 18.6)
                    nutritionState = 5;
                else if (weight >= 18.6)
                    nutritionState = 6;
                break;
            case 96:
                if (weight < 12.1)
                    nutritionState = 1;
                else if (weight < 12.6)
                    nutritionState = 2;
                else if (weight < 16.6)
                    nutritionState = 3;
                else if (weight < 17.4)
                    nutritionState = 4;
                else if (weight < 18.9)
                    nutritionState = 5;
                else if (weight >= 18.9)
                    nutritionState = 6;
                break;
            case 97:
                if (weight < 12.3)
                    nutritionState = 1;
                else if (weight < 12.8)
                    nutritionState = 2;
                else if (weight < 17)
                    nutritionState = 3;
                else if (weight < 17.8)
                    nutritionState = 4;
                else if (weight < 19.3)
                    nutritionState = 5;
                else if (weight >= 19.3)
                    nutritionState = 6;
                break;
            case 98:
                if (weight < 12.5)
                    nutritionState = 1;
                else if (weight < 13)
                    nutritionState = 2;
                else if (weight < 17.3)
                    nutritionState = 3;
                else if (weight < 18.1)
                    nutritionState = 4;
                else if (weight < 19.7)
                    nutritionState = 5;
                else if (weight >= 19.7)
                    nutritionState = 6;
                break;
            case 99:
                if (weight < 12.7)
                    nutritionState = 1;
                else if (weight < 13.3)
                    nutritionState = 2;
                else if (weight < 17.7)
                    nutritionState = 3;
                else if (weight < 18.5)
                    nutritionState = 4;
                else if (weight < 20.1)
                    nutritionState = 5;
                else if (weight >= 20.1)
                    nutritionState = 6;
                break;
            case 100:
                if (weight < 13)
                    nutritionState = 1;
                else if (weight < 13.5)
                    nutritionState = 2;
                else if (weight < 18)
                    nutritionState = 3;
                else if (weight < 18.8)
                    nutritionState = 4;
                else if (weight < 20.5)
                    nutritionState = 5;
                else if (weight >= 20.5)
                    nutritionState = 6;
                break;
            case 101:
                if (weight < 13.2)
                    nutritionState = 1;
                else if (weight < 13.8)
                    nutritionState = 2;
                else if (weight < 18.3)
                    nutritionState = 3;
                else if (weight < 19.2)
                    nutritionState = 4;
                else if (weight < 21)
                    nutritionState = 5;
                else if (weight >= 21)
                    nutritionState = 6;
                break;
            case 102:
                if (weight < 13.4)
                    nutritionState = 1;
                else if (weight < 14)
                    nutritionState = 2;
                else if (weight < 18.6)
                    nutritionState = 3;
                else if (weight < 19.5)
                    nutritionState = 4;
                else if (weight < 21.3)
                    nutritionState = 5;
                else if (weight >= 21.3)
                    nutritionState = 6;
                break;
            case 103:
                if (weight < 13.5)
                    nutritionState = 1;
                else if (weight < 14.2)
                    nutritionState = 2;
                else if (weight < 19)
                    nutritionState = 3;
                else if (weight < 19.9)
                    nutritionState = 4;
                else if (weight < 21.8)
                    nutritionState = 5;
                else if (weight >= 21.8)
                    nutritionState = 6;
                break;
            case 104:
                if (weight < 13.8)
                    nutritionState = 1;
                else if (weight < 14.5)
                    nutritionState = 2;
                else if (weight < 19.3)
                    nutritionState = 3;
                else if (weight < 20.2)
                    nutritionState = 4;
                else if (weight < 22.1)
                    nutritionState = 5;
                else if (weight >= 22.1)
                    nutritionState = 6;
                break;
            case 105:
                if (weight < 14)
                    nutritionState = 1;
                else if (weight < 14.7)
                    nutritionState = 2;
                else if (weight < 19.7)
                    nutritionState = 3;
                else if (weight < 20.6)
                    nutritionState = 4;
                else if (weight < 22.5)
                    nutritionState = 5;
                else if (weight >= 22.5)
                    nutritionState = 6;
                break;
            case 106:
                if (weight < 14.3)
                    nutritionState = 1;
                else if (weight < 15)
                    nutritionState = 2;
                else if (weight < 20.1)
                    nutritionState = 3;
                else if (weight < 21)
                    nutritionState = 4;
                else if (weight < 23)
                    nutritionState = 5;
                else if (weight >= 23)
                    nutritionState = 6;
                break;
            case 107:
                if (weight < 14.5)
                    nutritionState = 1;
                else if (weight < 15.2)
                    nutritionState = 2;
                else if (weight < 20.4)
                    nutritionState = 3;
                else if (weight < 21.4)
                    nutritionState = 4;
                else if (weight < 23.5)
                    nutritionState = 5;
                else if (weight >= 23.5)
                    nutritionState = 6;
                break;
            case 108:
                if (weight < 14.8)
                    nutritionState = 1;
                else if (weight < 15.5)
                    nutritionState = 2;
                else if (weight < 20.8)
                    nutritionState = 3;
                else if (weight < 21.8)
                    nutritionState = 4;
                else if (weight < 23.9)
                    nutritionState = 5;
                else if (weight >= 23.9)
                    nutritionState = 6;
                break;
            case 109:
                if (weight < 15)
                    nutritionState = 1;
                else if (weight < 15.7)
                    nutritionState = 2;
                else if (weight < 21.2)
                    nutritionState = 3;
                else if (weight < 22.3)
                    nutritionState = 4;
                else if (weight < 24.4)
                    nutritionState = 5;
                else if (weight >= 24.4)
                    nutritionState = 6;
                break;
            case 110:
                if (weight < 15.3)
                    nutritionState = 1;
                else if (weight < 16)
                    nutritionState = 2;
                else if (weight < 21.6)
                    nutritionState = 3;
                else if (weight < 22.7)
                    nutritionState = 4;
                else if (weight < 24.8)
                    nutritionState = 5;
                else if (weight >= 24.8)
                    nutritionState = 6;
                break;
            case 111:
                if (weight < 15.5)
                    nutritionState = 1;
                else if (weight < 16.3)
                    nutritionState = 2;
                else if (weight < 22)
                    nutritionState = 3;
                else if (weight < 23.2)
                    nutritionState = 4;
                else if (weight < 25.5)
                    nutritionState = 5;
                else if (weight >= 25.5)
                    nutritionState = 6;
                break;
            case 112:
                if (weight < 15.8)
                    nutritionState = 1;
                else if (weight < 16.6)
                    nutritionState = 2;
                else if (weight < 22.4)
                    nutritionState = 3;
                else if (weight < 23.6)
                    nutritionState = 4;
                else if (weight < 25.9)
                    nutritionState = 5;
                else if (weight >= 25.9)
                    nutritionState = 6;
                break;
            case 113:
                if (weight < 16.1)
                    nutritionState = 1;
                else if (weight < 16.9)
                    nutritionState = 2;
                else if (weight < 22.9)
                    nutritionState = 3;
                else if (weight < 24.1)
                    nutritionState = 4;
                else if (weight < 26.5)
                    nutritionState = 5;
                else if (weight >= 26.5)
                    nutritionState = 6;
                break;
            case 114:
                if (weight < 16.4)
                    nutritionState = 1;
                else if (weight < 17.2)
                    nutritionState = 2;
                else if (weight < 23.3)
                    nutritionState = 3;
                else if (weight < 24.6)
                    nutritionState = 4;
                else if (weight < 27.1)
                    nutritionState = 5;
                else if (weight >= 27.1)
                    nutritionState = 6;
                break;
            case 115:
                if (weight < 16.7)
                    nutritionState = 1;
                else if (weight < 17.5)
                    nutritionState = 2;
                else if (weight < 23.8)
                    nutritionState = 3;
                else if (weight < 25.1)
                    nutritionState = 4;
                else if (weight < 27.7)
                    nutritionState = 5;
                else if (weight >= 27.7)
                    nutritionState = 6;
                break;
            case 116:
                if (weight < 16.9)
                    nutritionState = 1;
                else if (weight < 17.7)
                    nutritionState = 2;
                else if (weight < 24.4)
                    nutritionState = 3;
                else if (weight < 25.7)
                    nutritionState = 4;
                else if (weight < 28.4)
                    nutritionState = 5;
                else if (weight >= 28.4)
                    nutritionState = 6;
                break;
            case 117:
                if (weight < 17.2)
                    nutritionState = 1;
                else if (weight < 18)
                    nutritionState = 2;
                else if (weight < 24.8)
                    nutritionState = 3;
                else if (weight < 26.2)
                    nutritionState = 4;
                else if (weight < 29)
                    nutritionState = 5;
                else if (weight >= 29)
                    nutritionState = 6;
                break;
            case 118:
                if (weight < 17.5)
                    nutritionState = 1;
                else if (weight < 18.4)
                    nutritionState = 2;
                else if (weight < 25.4)
                    nutritionState = 3;
                else if (weight < 26.8)
                    nutritionState = 4;
                else if (weight < 29.7)
                    nutritionState = 5;
                else if (weight >= 29.7)
                    nutritionState = 6;
                break;
            case 119:
                if (weight < 17.8)
                    nutritionState = 1;
                else if (weight < 18.7)
                    nutritionState = 2;
                else if (weight < 25.9)
                    nutritionState = 3;
                else if (weight < 27.5)
                    nutritionState = 4;
                else if (weight < 30.5)
                    nutritionState = 5;
                else if (weight >= 30.5)
                    nutritionState = 6;
                break;
            case 120:
                if (weight < 18.2)
                    nutritionState = 1;
                else if (weight < 19.1)
                    nutritionState = 2;
                else if (weight < 26.6)
                    nutritionState = 3;
                else if (weight < 28.2)
                    nutritionState = 4;
                else if (weight < 31.4)
                    nutritionState = 5;
                else if (weight >= 31.4)
                    nutritionState = 6;
                break;
            case 121:
                if (weight < 18.5)
                    nutritionState = 1;
                else if (weight < 19.4)
                    nutritionState = 2;
                else if (weight < 27.2)
                    nutritionState = 3;
                else if (weight < 28.8)
                    nutritionState = 4;
                else if (weight < 32)
                    nutritionState = 5;
                else if (weight >= 32)
                    nutritionState = 6;
                break;
            case 122:
                if (weight < 18.8)
                    nutritionState = 1;
                else if (weight < 19.7)
                    nutritionState = 2;
                else if (weight < 27.8)
                    nutritionState = 3;
                else if (weight < 29.5)
                    nutritionState = 4;
                else if (weight < 32.9)
                    nutritionState = 5;
                else if (weight >= 32.9)
                    nutritionState = 6;
                break;
            case 123:
                if (weight < 19.1)
                    nutritionState = 1;
                else if (weight < 20)
                    nutritionState = 2;
                else if (weight < 28.5)
                    nutritionState = 3;
                else if (weight < 30.3)
                    nutritionState = 4;
                else if (weight < 33.9)
                    nutritionState = 5;
                else if (weight >= 33.9)
                    nutritionState = 6;
                break;
            case 124:
                if (weight < 19.5)
                    nutritionState = 1;
                else if (weight < 20.5)
                    nutritionState = 2;
                else if (weight < 29.2)
                    nutritionState = 3;
                else if (weight < 31)
                    nutritionState = 4;
                else if (weight < 34.8)
                    nutritionState = 5;
                else if (weight >= 34.8)
                    nutritionState = 6;
                break;
            case 125:
                if (weight < 19.8)
                    nutritionState = 1;
                else if (weight < 20.8)
                    nutritionState = 2;
                else if (weight < 29.9)
                    nutritionState = 3;
                else if (weight < 31.9)
                    nutritionState = 4;
                else if (weight < 35.8)
                    nutritionState = 5;
                else if (weight >= 35.8)
                    nutritionState = 6;
                break;
            case 126:
                if (weight < 20)
                    nutritionState = 1;
                else if (weight < 21.1)
                    nutritionState = 2;
                else if (weight < 30.6)
                    nutritionState = 3;
                else if (weight < 32.7)
                    nutritionState = 4;
                else if (weight < 36.8)
                    nutritionState = 5;
                else if (weight >= 36.8)
                    nutritionState = 6;
                break;
            case 127:
                if (weight < 20.4)
                    nutritionState = 1;
                else if (weight < 21.6)
                    nutritionState = 2;
                else if (weight < 31.4)
                    nutritionState = 3;
                else if (weight < 33.5)
                    nutritionState = 4;
                else if (weight < 37.8)
                    nutritionState = 5;
                else if (weight >= 37.8)
                    nutritionState = 6;
                break;
            case 128:
                if (weight < 20.7)
                    nutritionState = 1;
                else if (weight < 21.9)
                    nutritionState = 2;
                else if (weight < 32.1)
                    nutritionState = 3;
                else if (weight < 34.3)
                    nutritionState = 4;
                else if (weight < 38.7)
                    nutritionState = 5;
                else if (weight >= 38.7)
                    nutritionState = 6;
                break;
            case 129:
                if (weight < 21.1)
                    nutritionState = 1;
                else if (weight < 22.3)
                    nutritionState = 2;
                else if (weight < 32.9)
                    nutritionState = 3;
                else if (weight < 35.3)
                    nutritionState = 4;
                else if (weight < 39.9)
                    nutritionState = 5;
                else if (weight >= 39.9)
                    nutritionState = 6;
                break;
            case 130:
                if (weight < 21.4)
                    nutritionState = 1;
                else if (weight < 22.7)
                    nutritionState = 2;
                else if (weight < 33.8)
                    nutritionState = 3;
                else if (weight < 36.2)
                    nutritionState = 4;
                else if (weight < 41)
                    nutritionState = 5;
                else if (weight >= 41)
                    nutritionState = 6;
                break;
            case 131:
                if (weight < 21.8)
                    nutritionState = 1;
                else if (weight < 23.1)
                    nutritionState = 2;
                else if (weight < 34.6)
                    nutritionState = 3;
                else if (weight < 37.1)
                    nutritionState = 4;
                else if (weight < 42)
                    nutritionState = 5;
                else if (weight >= 42)
                    nutritionState = 6;
                break;
            case 132:
                if (weight < 22.2)
                    nutritionState = 1;
                else if (weight < 23.5)
                    nutritionState = 2;
                else if (weight < 35.4)
                    nutritionState = 3;
                else if (weight < 38)
                    nutritionState = 4;
                else if (weight < 43.2)
                    nutritionState = 5;
                else if (weight >= 43.2)
                    nutritionState = 6;
                break;
            case 133:
                if (weight < 22.6)
                    nutritionState = 1;
                else if (weight < 24)
                    nutritionState = 2;
                else if (weight < 36.3)
                    nutritionState = 3;
                else if (weight < 38.9)
                    nutritionState = 4;
                else if (weight < 44.3)
                    nutritionState = 5;
                else if (weight >= 44.3)
                    nutritionState = 6;
                break;
            case 134:
                if (weight < 23)
                    nutritionState = 1;
                else if (weight < 24.4)
                    nutritionState = 2;
                else if (weight < 37.2)
                    nutritionState = 3;
                else if (weight < 40)
                    nutritionState = 4;
                else if (weight < 45.5)
                    nutritionState = 5;
                else if (weight >= 45.5)
                    nutritionState = 6;
                break;
            case 135:
                if (weight < 23.3)
                    nutritionState = 1;
                else if (weight < 24.9)
                    nutritionState = 2;
                else if (weight < 38.1)
                    nutritionState = 3;
                else if (weight < 40.9)
                    nutritionState = 4;
                else if (weight < 46.5)
                    nutritionState = 5;
                else if (weight >= 46.5)
                    nutritionState = 6;
                break;
            case 136:
                if (weight < 23.8)
                    nutritionState = 1;
                else if (weight < 25.4)
                    nutritionState = 2;
                else if (weight < 39)
                    nutritionState = 3;
                else if (weight < 41.9)
                    nutritionState = 4;
                else if (weight < 47.7)
                    nutritionState = 5;
                else if (weight >= 47.7)
                    nutritionState = 6;
                break;
            case 137:
                if (weight < 24.2)
                    nutritionState = 1;
                else if (weight < 25.9)
                    nutritionState = 2;
                else if (weight < 39.9)
                    nutritionState = 3;
                else if (weight < 42.8)
                    nutritionState = 4;
                else if (weight < 48.7)
                    nutritionState = 5;
                else if (weight >= 48.7)
                    nutritionState = 6;
                break;
            case 138:
                if (weight < 24.7)
                    nutritionState = 1;
                else if (weight < 26.4)
                    nutritionState = 2;
                else if (weight < 40.7)
                    nutritionState = 3;
                else if (weight < 43.7)
                    nutritionState = 4;
                else if (weight < 49.7)
                    nutritionState = 5;
                else if (weight >= 49.7)
                    nutritionState = 6;
                break;
            case 139:
                if (weight < 25.2)
                    nutritionState = 1;
                else if (weight < 27)
                    nutritionState = 2;
                else if (weight < 41.7)
                    nutritionState = 3;
                else if (weight < 44.7)
                    nutritionState = 4;
                else if (weight < 50.8)
                    nutritionState = 5;
                else if (weight >= 50.8)
                    nutritionState = 6;
                break;
            case 140:
                if (weight < 25.8)
                    nutritionState = 1;
                else if (weight < 27.6)
                    nutritionState = 2;
                else if (weight < 42.5)
                    nutritionState = 3;
                else if (weight < 45.6)
                    nutritionState = 4;
                else if (weight < 51.9)
                    nutritionState = 5;
                else if (weight >= 51.9)
                    nutritionState = 6;
                break;
            case 141:
                if (weight < 26.2)
                    nutritionState = 1;
                else if (weight < 28.2)
                    nutritionState = 2;
                else if (weight < 43.5)
                    nutritionState = 3;
                else if (weight < 46.7)
                    nutritionState = 4;
                else if (weight < 53)
                    nutritionState = 5;
                else if (weight >= 53)
                    nutritionState = 6;
                break;
            case 142:
                if (weight < 26.8)
                    nutritionState = 1;
                else if (weight < 28.8)
                    nutritionState = 2;
                else if (weight < 44.4)
                    nutritionState = 3;
                else if (weight < 47.6)
                    nutritionState = 4;
                else if (weight < 53.9)
                    nutritionState = 5;
                else if (weight >= 53.9)
                    nutritionState = 6;
                break;
            case 143:
                if (weight < 27.4)
                    nutritionState = 1;
                else if (weight < 29.5)
                    nutritionState = 2;
                else if (weight < 45.3)
                    nutritionState = 3;
                else if (weight < 48.5)
                    nutritionState = 4;
                else if (weight < 54.8)
                    nutritionState = 5;
                else if (weight >= 54.8)
                    nutritionState = 6;
                break;
            case 144:
                if (weight < 28.1)
                    nutritionState = 1;
                else if (weight < 30.2)
                    nutritionState = 2;
                else if (weight < 46.2)
                    nutritionState = 3;
                else if (weight < 49.4)
                    nutritionState = 4;
                else if (weight < 55.8)
                    nutritionState = 5;
                else if (weight >= 55.8)
                    nutritionState = 6;
                break;
            case 145:
                if (weight < 28.7)
                    nutritionState = 1;
                else if (weight < 30.8)
                    nutritionState = 2;
                else if (weight < 47.1)
                    nutritionState = 3;
                else if (weight < 50.4)
                    nutritionState = 4;
                else if (weight < 56.9)
                    nutritionState = 5;
                else if (weight >= 56.9)
                    nutritionState = 6;
                break;
            case 146:
                if (weight < 29.4)
                    nutritionState = 1;
                else if (weight < 31.6)
                    nutritionState = 2;
                else if (weight < 48)
                    nutritionState = 3;
                else if (weight < 51.3)
                    nutritionState = 4;
                else if (weight < 57.8)
                    nutritionState = 5;
                else if (weight >= 57.8)
                    nutritionState = 6;
                break;
            case 147:
                if (weight < 30.2)
                    nutritionState = 1;
                else if (weight < 32.4)
                    nutritionState = 2;
                else if (weight < 48.9)
                    nutritionState = 3;
                else if (weight < 52.2)
                    nutritionState = 4;
                else if (weight < 58.7)
                    nutritionState = 5;
                else if (weight >= 58.7)
                    nutritionState = 6;
                break;
            case 148:
                if (weight < 30.9)
                    nutritionState = 1;
                else if (weight < 33.1)
                    nutritionState = 2;
                else if (weight < 49.8)
                    nutritionState = 3;
                else if (weight < 53.1)
                    nutritionState = 4;
                else if (weight < 59.6)
                    nutritionState = 5;
                else if (weight >= 59.6)
                    nutritionState = 6;
                break;
            case 149:
                if (weight < 31.5)
                    nutritionState = 1;
                else if (weight < 33.9)
                    nutritionState = 2;
                else if (weight < 50.6)
                    nutritionState = 3;
                else if (weight < 53.9)
                    nutritionState = 4;
                else if (weight < 60.4)
                    nutritionState = 5;
                else if (weight >= 60.4)
                    nutritionState = 6;
                break;
            case 150:
                if (weight < 32.3)
                    nutritionState = 1;
                else if (weight < 34.7)
                    nutritionState = 2;
                else if (weight < 51.5)
                    nutritionState = 3;
                else if (weight < 54.8)
                    nutritionState = 4;
                else if (weight < 61.3)
                    nutritionState = 5;
                else if (weight >= 61.3)
                    nutritionState = 6;
                break;
            case 151:
                if (weight < 33.1)
                    nutritionState = 1;
                else if (weight < 35.5)
                    nutritionState = 2;
                else if (weight < 52.3)
                    nutritionState = 3;
                else if (weight < 55.6)
                    nutritionState = 4;
                else if (weight < 62.1)
                    nutritionState = 5;
                else if (weight >= 62.1)
                    nutritionState = 6;
                break;
            case 152:
                if (weight < 33.8)
                    nutritionState = 1;
                else if (weight < 36.2)
                    nutritionState = 2;
                else if (weight < 53.1)
                    nutritionState = 3;
                else if (weight < 56.4)
                    nutritionState = 4;
                else if (weight < 62.9)
                    nutritionState = 5;
                else if (weight >= 62.9)
                    nutritionState = 6;
                break;
            case 153:
                if (weight < 34.6)
                    nutritionState = 1;
                else if (weight < 37)
                    nutritionState = 2;
                else if (weight < 53.9)
                    nutritionState = 3;
                else if (weight < 57.2)
                    nutritionState = 4;
                else if (weight < 63.7)
                    nutritionState = 5;
                else if (weight >= 63.7)
                    nutritionState = 6;
                break;
            case 154:
                if (weight < 35.3)
                    nutritionState = 1;
                else if (weight < 37.7)
                    nutritionState = 2;
                else if (weight < 54.7)
                    nutritionState = 3;
                else if (weight < 58)
                    nutritionState = 4;
                else if (weight < 64.5)
                    nutritionState = 5;
                else if (weight >= 64.5)
                    nutritionState = 6;
                break;
            case 155:
                if (weight < 36)
                    nutritionState = 1;
                else if (weight < 38.4)
                    nutritionState = 2;
                else if (weight < 55.5)
                    nutritionState = 3;
                else if (weight < 58.8)
                    nutritionState = 4;
                else if (weight < 65.3)
                    nutritionState = 5;
                else if (weight >= 65.3)
                    nutritionState = 6;
                break;
            case 156:
                if (weight < 36.7)
                    nutritionState = 1;
                else if (weight < 39.2)
                    nutritionState = 2;
                else if (weight < 56.3)
                    nutritionState = 3;
                else if (weight < 59.5)
                    nutritionState = 4;
                else if (weight < 65.9)
                    nutritionState = 5;
                else if (weight >= 65.9)
                    nutritionState = 6;
                break;
            case 157:
                if (weight < 37.4)
                    nutritionState = 1;
                else if (weight < 39.9)
                    nutritionState = 2;
                else if (weight < 57)
                    nutritionState = 3;
                else if (weight < 60.2)
                    nutritionState = 4;
                else if (weight < 66.5)
                    nutritionState = 5;
                else if (weight >= 66.5)
                    nutritionState = 6;
                break;
            case 158:
                if (weight < 38.1)
                    nutritionState = 1;
                else if (weight < 40.6)
                    nutritionState = 2;
                else if (weight < 57.7)
                    nutritionState = 3;
                else if (weight < 60.9)
                    nutritionState = 4;
                else if (weight < 67.2)
                    nutritionState = 5;
                else if (weight >= 67.2)
                    nutritionState = 6;
                break;
            case 159:
                if (weight < 38.8)
                    nutritionState = 1;
                else if (weight < 41.3)
                    nutritionState = 2;
                else if (weight < 58.4)
                    nutritionState = 3;
                else if (weight < 61.5)
                    nutritionState = 4;
                else if (weight < 67.8)
                    nutritionState = 5;
                else if (weight >= 67.8)
                    nutritionState = 6;
                break;
            case 160:
                if (weight < 39.5)
                    nutritionState = 1;
                else if (weight < 42)
                    nutritionState = 2;
                else if (weight < 59.1)
                    nutritionState = 3;
                else if (weight < 62.2)
                    nutritionState = 4;
                else if (weight < 68.5)
                    nutritionState = 5;
                else if (weight >= 68.5)
                    nutritionState = 6;
                break;
            case 161:
                if (weight < 40.1)
                    nutritionState = 1;
                else if (weight < 42.7)
                    nutritionState = 2;
                else if (weight < 59.8)
                    nutritionState = 3;
                else if (weight < 62.8)
                    nutritionState = 4;
                else if (weight < 68.9)
                    nutritionState = 5;
                else if (weight >= 68.9)
                    nutritionState = 6;
                break;
            case 162:
                if (weight < 40.8)
                    nutritionState = 1;
                else if (weight < 43.4)
                    nutritionState = 2;
                else if (weight < 60.4)
                    nutritionState = 3;
                else if (weight < 63.4)
                    nutritionState = 4;
                else if (weight < 69.4)
                    nutritionState = 5;
                else if (weight >= 69.4)
                    nutritionState = 6;
                break;
            case 163:
                if (weight < 41.5)
                    nutritionState = 1;
                else if (weight < 44.2)
                    nutritionState = 2;
                else if (weight < 61.2)
                    nutritionState = 3;
                else if (weight < 64.1)
                    nutritionState = 4;
                else if (weight < 69.9)
                    nutritionState = 5;
                else if (weight >= 69.9)
                    nutritionState = 6;
                break;
            case 164:
                if (weight < 42.1)
                    nutritionState = 1;
                else if (weight < 44.9)
                    nutritionState = 2;
                else if (weight < 61.8)
                    nutritionState = 3;
                else if (weight < 64.7)
                    nutritionState = 4;
                else if (weight < 70.4)
                    nutritionState = 5;
                else if (weight >= 70.4)
                    nutritionState = 6;
                break;
            case 165:
                if (weight < 42.8)
                    nutritionState = 1;
                else if (weight < 45.6)
                    nutritionState = 2;
                else if (weight < 62.4)
                    nutritionState = 3;
                else if (weight < 65.2)
                    nutritionState = 4;
                else if (weight < 70.7)
                    nutritionState = 5;
                else if (weight >= 70.7)
                    nutritionState = 6;
                break;
            case 166:
                if (weight < 43.6)
                    nutritionState = 1;
                else if (weight < 46.4)
                    nutritionState = 2;
                else if (weight < 63.1)
                    nutritionState = 3;
                else if (weight < 65.7)
                    nutritionState = 4;
                else if (weight < 71.1)
                    nutritionState = 5;
                else if (weight >= 71.1)
                    nutritionState = 6;
                break;
            case 167:
                if (weight < 44.3)
                    nutritionState = 1;
                else if (weight < 47.4)
                    nutritionState = 2;
                else if (weight < 63.7)
                    nutritionState = 3;
                else if (weight < 66.3)
                    nutritionState = 4;
                else if (weight < 71.5)
                    nutritionState = 5;
                else if (weight >= 71.5)
                    nutritionState = 6;
                break;
            case 168:
                if (weight < 45.1)
                    nutritionState = 1;
                else if (weight < 48)
                    nutritionState = 2;
                else if (weight < 64.3)
                    nutritionState = 3;
                else if (weight < 66.8)
                    nutritionState = 4;
                else if (weight < 71.7)
                    nutritionState = 5;
                else if (weight >= 71.7)
                    nutritionState = 6;
                break;
            case 169:
                if (weight < 45.9)
                    nutritionState = 1;
                else if (weight < 48.8)
                    nutritionState = 2;
                else if (weight < 65)
                    nutritionState = 3;
                else if (weight < 67.4)
                    nutritionState = 4;
                else if (weight < 72.2)
                    nutritionState = 5;
                else if (weight >= 72.2)
                    nutritionState = 6;
                break;
            case 170:
                if (weight < 46.8)
                    nutritionState = 1;
                else if (weight < 49.8)
                    nutritionState = 2;
                else if (weight < 65.6)
                    nutritionState = 3;
                else if (weight < 67.8)
                    nutritionState = 4;
                else if (weight < 72.4)
                    nutritionState = 5;
                else if (weight >= 72.4)
                    nutritionState = 6;
                break;
            case 12987:
            case 12988:
            case 12989:
                nutritionState = 9;
                break;
            default:
                nutritionState = 0;
                break;
        }
        return nutritionState;
    }

    // ˭ԧ : ���� / ��ǹ�٧
    public static int GetNutritionStateFemaleAgeLessThen18AgeHeight(int age,
                                                                    double height) {
        int nutritionState = 0;
        switch (age) {

            case 0:// 1
                if (height < 45.8)
                    nutritionState = 1;
                else if (height >= 45.8 && height < 46.8)
                    nutritionState = 2;
                else if (height >= 46.8 && height < 53)
                    nutritionState = 3;
                else if (height >= 53 && height < 54)
                    nutritionState = 4;
                else if (height >= 54)
                    nutritionState = 5;
                break;
            case 1:// 2
                if (height < 48.4)
                    nutritionState = 1;
                else if (height >= 48.4 && height < 49.4)
                    nutritionState = 2;
                else if (height >= 49.4 && height < 56.1)
                    nutritionState = 3;
                else if (height >= 56.1 && height < 57.3)
                    nutritionState = 4;
                else if (height >= 57.3)
                    nutritionState = 5;
                break;
            case 2:// 3
                if (height < 50.9)
                    nutritionState = 1;
                else if (height >= 50.9 && height < 52)
                    nutritionState = 2;
                else if (height >= 52 && height < 59.1)
                    nutritionState = 3;
                else if (height >= 59.1 && height < 60.4)
                    nutritionState = 4;
                else if (height >= 60.4)
                    nutritionState = 5;
                break;
            case 3:// 4
                if (height < 53.3)
                    nutritionState = 1;
                else if (height >= 53.3 && height < 54.4)
                    nutritionState = 2;
                else if (height >= 54.4 && height < 61.9)
                    nutritionState = 3;
                else if (height >= 61.9 && height < 63.3)
                    nutritionState = 4;
                else if (height >= 63.3)
                    nutritionState = 5;
                break;
            case 4:// 5
                if (height < 55.6)
                    nutritionState = 1;
                else if (height >= 55.6 && height < 56.8)
                    nutritionState = 2;
                else if (height >= 56.8 && height < 64.6)
                    nutritionState = 3;
                else if (height >= 64.6 && height < 66)
                    nutritionState = 4;
                else if (height >= 66)
                    nutritionState = 5;
                break;
            case 5:// 6
                if (height < 57.7)
                    nutritionState = 1;
                else if (height >= 57.7 && height < 58.9)
                    nutritionState = 2;
                else if (height >= 58.9 && height < 67)
                    nutritionState = 3;
                else if (height >= 67 && height < 68.6)
                    nutritionState = 4;
                else if (height >= 68.6)
                    nutritionState = 5;
                break;
            case 6:// 7
                if (height < 59.7)
                    nutritionState = 1;
                else if (height >= 59.7 && height < 60.9)
                    nutritionState = 2;
                else if (height >= 60.9 && height < 69.2)
                    nutritionState = 3;
                else if (height >= 69.2 && height < 70.8)
                    nutritionState = 4;
                else if (height >= 70.8)
                    nutritionState = 5;
                break;
            case 7:// 8
                if (height < 61.4)
                    nutritionState = 1;
                else if (height >= 61.4 && height < 62.6)
                    nutritionState = 2;
                else if (height >= 62.6 && height < 71.2)
                    nutritionState = 3;
                else if (height >= 71.2 && height < 72.8)
                    nutritionState = 4;
                else if (height >= 72.8)
                    nutritionState = 5;
                break;
            case 8:// 9
                if (height < 62.9)
                    nutritionState = 1;
                else if (height >= 62.9 && height < 64.2)
                    nutritionState = 2;
                else if (height >= 64.2 && height < 72.9)
                    nutritionState = 3;
                else if (height >= 72.9 && height < 74.6)
                    nutritionState = 4;
                else if (height >= 74.6)
                    nutritionState = 5;
                break;
            case 9:// 10
                if (height < 64.2)
                    nutritionState = 1;
                else if (height >= 64.2 && height < 65.5)
                    nutritionState = 2;
                else if (height >= 65.5 && height < 74.6)
                    nutritionState = 3;
                else if (height >= 74.6 && height < 76.3)
                    nutritionState = 4;
                else if (height >= 76.3)
                    nutritionState = 5;
                break;
            case 10:// 11
                if (height < 65.3)
                    nutritionState = 1;
                else if (height >= 65.3 && height < 66.7)
                    nutritionState = 2;
                else if (height >= 66.7 && height < 76.2)
                    nutritionState = 3;
                else if (height >= 76.2 && height < 77.9)
                    nutritionState = 4;
                else if (height >= 77.9)
                    nutritionState = 5;
                break;
            case 11:// 12
                if (height < 66.2)
                    nutritionState = 1;
                else if (height >= 66.2 && height < 67.7)
                    nutritionState = 2;
                else if (height >= 67.7 && height < 77.7)
                    nutritionState = 3;
                else if (height >= 77.7 && height < 79.4)
                    nutritionState = 4;
                else if (height >= 79.4)
                    nutritionState = 5;
                break;
            case 12:// 13
                if (height < 67.3)
                    nutritionState = 1;
                else if (height >= 67.3 && height < 68.8)
                    nutritionState = 2;
                else if (height >= 68.8 && height < 79)
                    nutritionState = 3;
                else if (height >= 79 && height < 80.9)
                    nutritionState = 4;
                else if (height >= 80.9)
                    nutritionState = 5;
                break;
            case 13:// 14
                if (height < 68.3)
                    nutritionState = 1;
                else if (height >= 68.3 && height < 69.9)
                    nutritionState = 2;
                else if (height >= 69.9 && height < 80.3)
                    nutritionState = 3;
                else if (height >= 80.3 && height < 82.2)
                    nutritionState = 4;
                else if (height >= 82.2)
                    nutritionState = 5;
                break;
            case 14:// 15
                if (height < 69.3)
                    nutritionState = 1;
                else if (height >= 69.3 && height < 70.9)
                    nutritionState = 2;
                else if (height >= 70.9 && height < 81.5)
                    nutritionState = 3;
                else if (height >= 81.5 && height < 83.4)
                    nutritionState = 4;
                else if (height >= 83.4)
                    nutritionState = 5;
                break;
            case 15:// 16
                if (height < 70.4)
                    nutritionState = 1;
                else if (height >= 70.4 && height < 71.9)
                    nutritionState = 2;
                else if (height >= 71.9 && height < 82.6)
                    nutritionState = 3;
                else if (height >= 82.6 && height < 84.5)
                    nutritionState = 4;
                else if (height >= 84.5)
                    nutritionState = 5;
                break;
            case 16:// 17
                if (height < 71.4)
                    nutritionState = 1;
                else if (height >= 71.4 && height < 73)
                    nutritionState = 2;
                else if (height >= 73 && height < 83.6)
                    nutritionState = 3;
                else if (height >= 83.6 && height < 85.5)
                    nutritionState = 4;
                else if (height >= 85.5)
                    nutritionState = 5;
                break;
            case 17:// 18
                if (height < 72.4)
                    nutritionState = 1;
                else if (height >= 72.4 && height < 74)
                    nutritionState = 2;
                else if (height >= 74 && height < 84.5)
                    nutritionState = 3;
                else if (height >= 84.5 && height < 86.4)
                    nutritionState = 4;
                else if (height >= 86.4)
                    nutritionState = 5;
                break;
            case 18:// 19
                if (height < 73.5)
                    nutritionState = 1;
                else if (height >= 73.5 && height < 75)
                    nutritionState = 2;
                else if (height >= 75 && height < 85.4)
                    nutritionState = 3;
                else if (height >= 85.4 && height < 87.3)
                    nutritionState = 4;
                else if (height >= 87.3)
                    nutritionState = 5;
                break;
            case 19:// 20
                if (height < 74.5)
                    nutritionState = 1;
                else if (height >= 74.5 && height < 76.1)
                    nutritionState = 2;
                else if (height >= 76.1 && height < 86.3)
                    nutritionState = 3;
                else if (height >= 86.3 && height < 88.2)
                    nutritionState = 4;
                else if (height >= 88.2)
                    nutritionState = 5;
                break;
            case 20:// 21
                if (height < 75.5)
                    nutritionState = 1;
                else if (height >= 75.5 && height < 77.1)
                    nutritionState = 2;
                else if (height >= 77.1 && height < 87.2)
                    nutritionState = 3;
                else if (height >= 87.2 && height < 89.1)
                    nutritionState = 4;
                else if (height >= 89.1)
                    nutritionState = 5;
                break;
            case 21:// 22
                if (height < 76.6)
                    nutritionState = 1;
                else if (height >= 76.6 && height < 78.1)
                    nutritionState = 2;
                else if (height >= 78.1 && height < 88.1)
                    nutritionState = 3;
                else if (height >= 88.1 && height < 90)
                    nutritionState = 4;
                else if (height >= 90)
                    nutritionState = 5;
                break;
            case 22:// 23
                if (height < 77.6)
                    nutritionState = 1;
                else if (height >= 77.6 && height < 79.1)
                    nutritionState = 2;
                else if (height >= 79.1 && height < 89.1)
                    nutritionState = 3;
                else if (height >= 89.1 && height < 90.8)
                    nutritionState = 4;
                else if (height >= 90.8)
                    nutritionState = 5;
                break;
            case 23:// 24
                if (height < 78.7)
                    nutritionState = 1;
                else if (height >= 78.7 && height < 80.2)
                    nutritionState = 2;
                else if (height >= 80.2 && height < 90)
                    nutritionState = 3;
                else if (height >= 90 && height < 91.7)
                    nutritionState = 4;
                else if (height >= 91.7)
                    nutritionState = 5;
                break;
            case 24:// 25
                if (height < 78.4)
                    nutritionState = 1;
                else if (height >= 78.4 && height < 80)
                    nutritionState = 2;
                else if (height >= 80 && height < 90)
                    nutritionState = 3;
                else if (height >= 90 && height < 91.7)
                    nutritionState = 4;
                else if (height >= 91.7)
                    nutritionState = 5;
                break;
            case 25:// 1
                if (height < 79.2)
                    nutritionState = 1;
                else if (height >= 79.2 && height < 80.8)
                    nutritionState = 2;
                else if (height >= 80.8 && height < 90.8)
                    nutritionState = 3;
                else if (height >= 90.8 && height < 92.5)
                    nutritionState = 4;
                else if (height >= 92.5)
                    nutritionState = 5;
                break;
            case 26:// 2
                if (height < 79.8)
                    nutritionState = 1;
                else if (height >= 79.8 && height < 81.5)
                    nutritionState = 2;
                else if (height >= 81.5 && height < 91.6)
                    nutritionState = 3;
                else if (height >= 91.6 && height < 93.3)
                    nutritionState = 4;
                else if (height >= 93.3)
                    nutritionState = 5;
                break;
            case 27:// 3
                if (height < 80.5)
                    nutritionState = 1;
                else if (height >= 80.5 && height < 82.2)
                    nutritionState = 2;
                else if (height >= 82.2 && height < 92.4)
                    nutritionState = 3;
                else if (height >= 92.4 && height < 94.1)
                    nutritionState = 4;
                else if (height >= 94.1)
                    nutritionState = 5;
                break;
            case 28:// 4
                if (height < 81.1)
                    nutritionState = 1;
                else if (height >= 81.1 && height < 82.8)
                    nutritionState = 2;
                else if (height >= 82.8 && height < 93.1)
                    nutritionState = 3;
                else if (height >= 93.1 && height < 94.8)
                    nutritionState = 4;
                else if (height >= 94.8)
                    nutritionState = 5;
                break;
            case 29:// 5
                if (height < 81.8)
                    nutritionState = 1;
                else if (height >= 81.8 && height < 83.6)
                    nutritionState = 2;
                else if (height >= 83.6 && height < 93.9)
                    nutritionState = 3;
                else if (height >= 93.9 && height < 95.6)
                    nutritionState = 4;
                else if (height >= 95.6)
                    nutritionState = 5;
                break;
            case 30:// 6
                if (height < 82.4)
                    nutritionState = 1;
                else if (height >= 82.4 && height < 84.2)
                    nutritionState = 2;
                else if (height >= 84.2 && height < 94.7)
                    nutritionState = 3;
                else if (height >= 94.7 && height < 96.4)
                    nutritionState = 4;
                else if (height >= 96.4)
                    nutritionState = 5;
                break;
            case 31:// 7
                if (height < 82.9)
                    nutritionState = 1;
                else if (height >= 82.9 && height < 84.8)
                    nutritionState = 2;
                else if (height >= 84.8 && height < 95.5)
                    nutritionState = 3;
                else if (height >= 95.5 && height < 97.2)
                    nutritionState = 4;
                else if (height >= 97.2)
                    nutritionState = 5;
                break;
            case 32:// 8
                if (height < 83.6)
                    nutritionState = 1;
                else if (height >= 83.6 && height < 85.5)
                    nutritionState = 2;
                else if (height >= 85.5 && height < 96.3)
                    nutritionState = 3;
                else if (height >= 96.3 && height < 98)
                    nutritionState = 4;
                else if (height >= 98)
                    nutritionState = 5;
                break;
            case 33:// 9
                if (height < 84.2)
                    nutritionState = 1;
                else if (height >= 84.2 && height < 86.2)
                    nutritionState = 2;
                else if (height >= 86.2 && height < 97.1)
                    nutritionState = 3;
                else if (height >= 97.1 && height < 98.8)
                    nutritionState = 4;
                else if (height >= 98.8)
                    nutritionState = 5;
                break;
            case 34:// 10
                if (height < 84.8)
                    nutritionState = 1;
                else if (height >= 84.8 && height < 86.8)
                    nutritionState = 2;
                else if (height >= 86.8 && height < 97.8)
                    nutritionState = 3;
                else if (height >= 97.8 && height < 99.5)
                    nutritionState = 4;
                else if (height >= 99.5)
                    nutritionState = 5;
                break;
            case 35:// 11
                if (height < 85.5)
                    nutritionState = 1;
                else if (height >= 85.5 && height < 87.5)
                    nutritionState = 2;
                else if (height >= 87.5 && height < 98.6)
                    nutritionState = 3;
                else if (height >= 98.6 && height < 100.3)
                    nutritionState = 4;
                else if (height >= 100.3)
                    nutritionState = 5;
                break;
            case 36:// 12
                if (height < 86.1)
                    nutritionState = 1;
                else if (height >= 86.1 && height < 88.1)
                    nutritionState = 2;
                else if (height >= 88.1 && height < 99.3)
                    nutritionState = 3;
                else if (height >= 99.3 && height < 101)
                    nutritionState = 4;
                else if (height >= 101)
                    nutritionState = 5;
                break;
            case 37:// 13
                if (height < 86.7)
                    nutritionState = 1;
                else if (height >= 86.7 && height < 88.7)
                    nutritionState = 2;
                else if (height >= 88.7 && height < 100.1)
                    nutritionState = 3;
                else if (height >= 100.1 && height < 101.8)
                    nutritionState = 4;
                else if (height >= 101.8)
                    nutritionState = 5;
                break;
            case 38:// 14
                if (height < 87.3)
                    nutritionState = 1;
                else if (height >= 87.3 && height < 89.3)
                    nutritionState = 2;
                else if (height >= 89.3 && height < 100.7)
                    nutritionState = 3;
                else if (height >= 100.7 && height < 102.5)
                    nutritionState = 4;
                else if (height >= 102.5)
                    nutritionState = 5;
                break;
            case 39:// 15
                if (height < 87.9)
                    nutritionState = 1;
                else if (height >= 87.9 && height < 89.9)
                    nutritionState = 2;
                else if (height >= 89.9 && height < 101.4)
                    nutritionState = 3;
                else if (height >= 101.4 && height < 103.2)
                    nutritionState = 4;
                else if (height >= 103.2)
                    nutritionState = 5;
                break;
            case 40:// 16
                if (height < 88.5)
                    nutritionState = 1;
                else if (height >= 88.5 && height < 90.5)
                    nutritionState = 2;
                else if (height >= 90.5 && height < 102.1)
                    nutritionState = 3;
                else if (height >= 102.1 && height < 103.9)
                    nutritionState = 4;
                else if (height >= 103.9)
                    nutritionState = 5;
                break;
            case 41:// 17
                if (height < 89.1)
                    nutritionState = 1;
                else if (height >= 89.1 && height < 91.1)
                    nutritionState = 2;
                else if (height >= 91.1 && height < 102.8)
                    nutritionState = 3;
                else if (height >= 102.8 && height < 104.6)
                    nutritionState = 4;
                else if (height >= 104.6)
                    nutritionState = 5;
                break;
            case 42:// 18
                if (height < 89.6)
                    nutritionState = 1;
                else if (height >= 89.6 && height < 91.6)
                    nutritionState = 2;
                else if (height >= 91.6 && height < 103.4)
                    nutritionState = 3;
                else if (height >= 103.4 && height < 105.2)
                    nutritionState = 4;
                else if (height >= 105.2)
                    nutritionState = 5;
                break;
            case 43:// 19
                if (height < 90.2)
                    nutritionState = 1;
                else if (height >= 90.2 && height < 92.2)
                    nutritionState = 2;
                else if (height >= 92.2 && height < 104.1)
                    nutritionState = 3;
                else if (height >= 104.1 && height < 106)
                    nutritionState = 4;
                else if (height >= 106)
                    nutritionState = 5;
                break;
            case 44:// 20
                if (height < 90.7)
                    nutritionState = 1;
                else if (height >= 90.7 && height < 92.8)
                    nutritionState = 2;
                else if (height >= 92.8 && height < 104.7)
                    nutritionState = 3;
                else if (height >= 104.7 && height < 106.6)
                    nutritionState = 4;
                else if (height >= 106.6)
                    nutritionState = 5;
                break;
            case 45:// 21
                if (height < 91.3)
                    nutritionState = 1;
                else if (height >= 91.3 && height < 93.3)
                    nutritionState = 2;
                else if (height >= 93.3 && height < 105.2)
                    nutritionState = 3;
                else if (height >= 105.2 && height < 107.2)
                    nutritionState = 4;
                else if (height >= 107.2)
                    nutritionState = 5;
                break;
            case 46:// 22
                if (height < 91.8)
                    nutritionState = 1;
                else if (height >= 91.8 && height < 93.9)
                    nutritionState = 2;
                else if (height >= 93.9 && height < 105.8)
                    nutritionState = 3;
                else if (height >= 105.8 && height < 107.8)
                    nutritionState = 4;
                else if (height >= 107.8)
                    nutritionState = 5;
                break;
            case 47:// 23
                if (height < 92.3)
                    nutritionState = 1;
                else if (height >= 93.3 && height < 94.4)
                    nutritionState = 2;
                else if (height >= 94.4 && height < 106.4)
                    nutritionState = 3;
                else if (height >= 106.4 && height < 108.4)
                    nutritionState = 4;
                else if (height >= 108.4)
                    nutritionState = 5;
                break;
            // 48 - 59 ????? (5 ??)
            case 48:
                if (height < 92.9)
                    nutritionState = 1;
                else if (height >= 92.9 && height < 95)
                    nutritionState = 2;
                else if (height >= 95 && height < 107)
                    nutritionState = 3;
                else if (height >= 107 && height < 109)
                    nutritionState = 4;
                else if (height >= 109)
                    nutritionState = 5;
                break;
            case 49:
                if (height < 93.4)
                    nutritionState = 1;
                else if (height >= 93.4 && height < 95.5)
                    nutritionState = 2;
                else if (height >= 95.5 && height < 107.6)
                    nutritionState = 3;
                else if (height >= 107.6 && height < 109.6)
                    nutritionState = 4;
                else if (height >= 109.6)
                    nutritionState = 5;
                break;
            case 50:
                if (height < 93.9)
                    nutritionState = 1;
                else if (height >= 93.9 && height < 96)
                    nutritionState = 2;
                else if (height >= 96 && height < 108.1)
                    nutritionState = 3;
                else if (height >= 108.1 && height < 110.1)
                    nutritionState = 4;
                else if (height >= 110.1)
                    nutritionState = 5;
                break;
            case 51:
                if (height < 94.4)
                    nutritionState = 1;
                else if (height >= 94.4 && height < 96.5)
                    nutritionState = 2;
                else if (height >= 96.5 && height < 108.7)
                    nutritionState = 3;
                else if (height >= 108.7 && height < 110.7)
                    nutritionState = 4;
                else if (height >= 110.7)
                    nutritionState = 5;
                break;
            case 52:
                if (height < 94.9)
                    nutritionState = 1;
                else if (height >= 94.9 && height < 97)
                    nutritionState = 2;
                else if (height >= 97 && height < 109.3)
                    nutritionState = 3;
                else if (height >= 109.3 && height < 111.3)
                    nutritionState = 4;
                else if (height >= 111.3)
                    nutritionState = 5;
                break;
            case 53:
                if (height < 95.4)
                    nutritionState = 1;
                else if (height >= 95.4 && height < 97.5)
                    nutritionState = 2;
                else if (height >= 97.5 && height < 109.9)
                    nutritionState = 3;
                else if (height >= 109.9 && height < 111.9)
                    nutritionState = 4;
                else if (height >= 111.9)
                    nutritionState = 5;
                break;
            case 54:
                if (height < 95.9)
                    nutritionState = 1;
                else if (height >= 95.9 && height < 98)
                    nutritionState = 2;
                else if (height >= 98 && height < 110.5)
                    nutritionState = 3;
                else if (height >= 110.5 && height < 112.5)
                    nutritionState = 4;
                else if (height >= 112.5)
                    nutritionState = 5;
                break;
            case 55:
                if (height < 96.4)
                    nutritionState = 1;
                else if (height >= 96.4 && height < 98.5)
                    nutritionState = 2;
                else if (height >= 98.5 && height < 111.1)
                    nutritionState = 3;
                else if (height >= 111.1 && height < 113.1)
                    nutritionState = 4;
                else if (height >= 113.1)
                    nutritionState = 5;
                break;
            case 56:
                if (height < 96.9)
                    nutritionState = 1;
                else if (height >= 96.9 && height < 99)
                    nutritionState = 2;
                else if (height >= 99 && height < 111.7)
                    nutritionState = 3;
                else if (height >= 111.7 && height < 113.7)
                    nutritionState = 4;
                else if (height >= 113.7)
                    nutritionState = 5;
                break;
            case 57:
                if (height < 97.4)
                    nutritionState = 1;
                else if (height >= 97.4 && height < 99.5)
                    nutritionState = 2;
                else if (height >= 99.5 && height < 112.2)
                    nutritionState = 3;
                else if (height >= 112.2 && height < 114.3)
                    nutritionState = 4;
                else if (height >= 114.3)
                    nutritionState = 5;
                break;
            case 58:
                if (height < 98)
                    nutritionState = 1;
                else if (height >= 98 && height < 100.1)
                    nutritionState = 2;
                else if (height >= 100.1 && height < 112.8)
                    nutritionState = 3;
                else if (height >= 112.8 && height < 114.9)
                    nutritionState = 4;
                else if (height >= 114.9)
                    nutritionState = 5;
                break;
            case 59:
                if (height < 98.5)
                    nutritionState = 1;
                else if (height >= 98.5 && height < 100.6)
                    nutritionState = 2;
                else if (height >= 100.6 && height < 113.4)
                    nutritionState = 3;
                else if (height >= 113.4 && height < 115.5)
                    nutritionState = 4;
                else if (height >= 115.5)
                    nutritionState = 5;
                break;
            case 60:
                if (height < 99)
                    nutritionState = 1;
                else if (height >= 99 && height < 101.1)
                    nutritionState = 2;
                else if (height >= 101.1 && height < 114)
                    nutritionState = 3;
                else if (height >= 114 && height < 116.1)
                    nutritionState = 4;
                else if (height >= 116.1)
                    nutritionState = 5;
                break;
            case 61:
                if (height < 99.5)
                    nutritionState = 1;
                else if (height >= 99.5 && height < 101.6)
                    nutritionState = 2;
                else if (height >= 101.6 && height < 114.6)
                    nutritionState = 3;
                else if (height >= 114.6 && height < 116.7)
                    nutritionState = 4;
                else if (height >= 116.7)
                    nutritionState = 5;
                break;
            case 62:
                if (height < 100.1)
                    nutritionState = 1;
                else if (height >= 100.1 && height < 102.2)
                    nutritionState = 2;
                else if (height >= 102.2 && height < 115.2)
                    nutritionState = 3;
                else if (height >= 115.2 && height < 117.3)
                    nutritionState = 4;
                else if (height >= 117.3)
                    nutritionState = 5;
                break;
            case 63:
                if (height < 100.6)
                    nutritionState = 1;
                else if (height >= 100.6 && height < 102.7)
                    nutritionState = 2;
                else if (height >= 102.7 && height < 115.8)
                    nutritionState = 3;
                else if (height >= 115.8 && height < 118)
                    nutritionState = 4;
                else if (height >= 118)
                    nutritionState = 5;
                break;
            case 64:
                if (height < 101.1)
                    nutritionState = 1;
                else if (height >= 101.1 && height < 103.2)
                    nutritionState = 2;
                else if (height >= 103.2 && height < 116.4)
                    nutritionState = 3;
                else if (height >= 116.4 && height < 118.6)
                    nutritionState = 4;
                else if (height >= 118.6)
                    nutritionState = 5;
                break;
            case 65:
                if (height < 101.6)
                    nutritionState = 1;
                else if (height >= 101.6 && height < 103.7)
                    nutritionState = 2;
                else if (height >= 103.7 && height < 117)
                    nutritionState = 3;
                else if (height >= 117 && height < 119.2)
                    nutritionState = 4;
                else if (height >= 119.2)
                    nutritionState = 5;
                break;
            case 66:
                if (height < 102.2)
                    nutritionState = 1;
                else if (height >= 102.2 && height < 104.3)
                    nutritionState = 2;
                else if (height >= 104.3 && height < 117.5)
                    nutritionState = 3;
                else if (height >= 117.5 && height < 119.7)
                    nutritionState = 4;
                else if (height >= 119.7)
                    nutritionState = 5;
                break;
            case 67:
                if (height < 102.7)
                    nutritionState = 1;
                else if (height >= 102.7 && height < 104.8)
                    nutritionState = 2;
                else if (height >= 104.8 && height < 118.1)
                    nutritionState = 3;
                else if (height >= 118.1 && height < 120.3)
                    nutritionState = 4;
                else if (height >= 120.3)
                    nutritionState = 5;
                break;
            case 68:
                if (height < 103.2)
                    nutritionState = 1;
                else if (height >= 103.2 && height < 105.3)
                    nutritionState = 2;
                else if (height >= 105.3 && height < 118.7)
                    nutritionState = 3;
                else if (height >= 118.7 && height < 120.9)
                    nutritionState = 4;
                else if (height >= 120.9)
                    nutritionState = 5;
                break;
            case 69:
                if (height < 103.7)
                    nutritionState = 1;
                else if (height >= 103.7 && height < 105.8)
                    nutritionState = 2;
                else if (height >= 105.8 && height < 119.3)
                    nutritionState = 3;
                else if (height >= 119.3 && height < 121.5)
                    nutritionState = 4;
                else if (height >= 121.5)
                    nutritionState = 5;
                break;
            case 70:
                if (height < 104.2)
                    nutritionState = 1;
                else if (height >= 104.2 && height < 106.3)
                    nutritionState = 2;
                else if (height >= 106.3 && height < 119.8)
                    nutritionState = 3;
                else if (height >= 119.8 && height < 122)
                    nutritionState = 4;
                else if (height >= 122)
                    nutritionState = 5;
                break;
            case 71:
                if (height < 104.7)
                    nutritionState = 1;
                else if (height >= 104.7 && height < 106.9)
                    nutritionState = 2;
                else if (height >= 106.9 && height < 120.4)
                    nutritionState = 3;
                else if (height >= 120.4 && height < 122.6)
                    nutritionState = 4;
                else if (height >= 122.6)
                    nutritionState = 5;
                break;
            case 72:
                if (height < 105.2)
                    nutritionState = 1;
                else if (height >= 105.2 && height < 107.4)
                    nutritionState = 2;
                else if (height >= 107.4 && height < 120.9)
                    nutritionState = 3;
                else if (height >= 120.9 && height < 123.1)
                    nutritionState = 4;
                else if (height >= 123.1)
                    nutritionState = 5;
                break;
            case 73:
                if (height < 105.6)
                    nutritionState = 1;
                else if (height >= 105.6 && height < 107.8)
                    nutritionState = 2;
                else if (height >= 107.8 && height < 121.4)
                    nutritionState = 3;
                else if (height >= 121.4 && height < 123.8)
                    nutritionState = 4;
                else if (height >= 123.8)
                    nutritionState = 5;
                break;
            case 74:
                if (height < 106.1)
                    nutritionState = 1;
                else if (height >= 106.1 && height < 108.3)
                    nutritionState = 2;
                else if (height >= 108.3 && height < 122)
                    nutritionState = 3;
                else if (height >= 122 && height < 124.2)
                    nutritionState = 4;
                else if (height >= 124.2)
                    nutritionState = 5;
                break;
            case 75:
                if (height < 106.5)
                    nutritionState = 1;
                else if (height >= 106.5 && height < 108.7)
                    nutritionState = 2;
                else if (height >= 108.7 && height < 122.5)
                    nutritionState = 3;
                else if (height >= 122.5 && height < 124.7)
                    nutritionState = 4;
                else if (height >= 124.7)
                    nutritionState = 5;
                break;
            case 76:
                if (height < 107)
                    nutritionState = 1;
                else if (height >= 107 && height < 109.2)
                    nutritionState = 2;
                else if (height >= 109.2 && height < 123)
                    nutritionState = 3;
                else if (height >= 123 && height < 125.4)
                    nutritionState = 4;
                else if (height >= 125.4)
                    nutritionState = 5;
                break;
            case 77:
                if (height < 107.4)
                    nutritionState = 1;
                else if (height >= 107.4 && height < 109.6)
                    nutritionState = 2;
                else if (height >= 109.6 && height < 123.5)
                    nutritionState = 3;
                else if (height >= 123.5 && height < 125.9)
                    nutritionState = 4;
                else if (height >= 125.9)
                    nutritionState = 5;
                break;
            case 78:
                if (height < 107.8)
                    nutritionState = 1;
                else if (height >= 107.8 && height < 110)
                    nutritionState = 2;
                else if (height >= 110 && height < 124)
                    nutritionState = 3;
                else if (height >= 124 && height < 126.4)
                    nutritionState = 4;
                else if (height >= 126.4)
                    nutritionState = 5;
                break;
            case 79:
                if (height < 108.1)
                    nutritionState = 1;
                else if (height >= 108.1 && height < 110.5)
                    nutritionState = 2;
                else if (height >= 110.5 && height < 124.5)
                    nutritionState = 3;
                else if (height >= 124.5 && height < 126.9)
                    nutritionState = 4;
                else if (height >= 126.9)
                    nutritionState = 5;
                break;
            case 80:
                if (height < 108.5)
                    nutritionState = 1;
                else if (height >= 108.5 && height < 110.9)
                    nutritionState = 2;
                else if (height >= 110.9 && height < 125)
                    nutritionState = 3;
                else if (height >= 125 && height < 127.4)
                    nutritionState = 4;
                else if (height >= 127.4)
                    nutritionState = 5;
                break;
            case 81:
                if (height < 108.8)
                    nutritionState = 1;
                else if (height >= 108.8 && height < 111.2)
                    nutritionState = 2;
                else if (height >= 111.2 && height < 125.5)
                    nutritionState = 3;
                else if (height >= 125.5 && height < 127.9)
                    nutritionState = 4;
                else if (height >= 127.9)
                    nutritionState = 5;
                break;
            case 82:
                if (height < 109.2)
                    nutritionState = 1;
                else if (height >= 109.2 && height < 111.6)
                    nutritionState = 2;
                else if (height >= 111.6 && height < 126)
                    nutritionState = 3;
                else if (height >= 126 && height < 128.4)
                    nutritionState = 4;
                else if (height >= 128.4)
                    nutritionState = 5;
                break;
            case 83:
                if (height < 109.6)
                    nutritionState = 1;
                else if (height >= 109.6 && height < 112)
                    nutritionState = 2;
                else if (height >= 112 && height < 126.5)
                    nutritionState = 3;
                else if (height >= 126.5 && height < 128.9)
                    nutritionState = 4;
                else if (height >= 128.9)
                    nutritionState = 5;
                break;
            case 84:
                if (height < 109.9)
                    nutritionState = 1;
                else if (height >= 109.9 && height < 112.4)
                    nutritionState = 2;
                else if (height >= 111.4 && height < 126.9)
                    nutritionState = 3;
                else if (height >= 126.9 && height < 129.3)
                    nutritionState = 4;
                else if (height >= 129.3)
                    nutritionState = 5;
                break;
            case 85:
                if (height < 110.3)
                    nutritionState = 1;
                else if (height >= 110.3 && height < 112.8)
                    nutritionState = 2;
                else if (height >= 112.8 && height < 127.4)
                    nutritionState = 3;
                else if (height >= 127.4 && height < 129.8)
                    nutritionState = 4;
                else if (height >= 129.8)
                    nutritionState = 5;
                break;
            case 86:
                if (height < 110.7)
                    nutritionState = 1;
                else if (height >= 110.7 && height < 113.2)
                    nutritionState = 2;
                else if (height >= 113.2 && height < 127.9)
                    nutritionState = 3;
                else if (height >= 127.9 && height < 130.3)
                    nutritionState = 4;
                else if (height >= 130.3)
                    nutritionState = 5;
                break;
            case 87:
                if (height < 111)
                    nutritionState = 1;
                else if (height >= 111 && height < 113.5)
                    nutritionState = 2;
                else if (height >= 113.5 && height < 128.3)
                    nutritionState = 3;
                else if (height >= 128.3 && height < 130.7)
                    nutritionState = 4;
                else if (height >= 130.7)
                    nutritionState = 5;
                break;
            case 88:
                if (height < 111.4)
                    nutritionState = 1;
                else if (height >= 111.4 && height < 113.9)
                    nutritionState = 2;
                else if (height >= 113.9 && height < 128.7)
                    nutritionState = 3;
                else if (height >= 128.7 && height < 131.2)
                    nutritionState = 4;
                else if (height >= 131.2)
                    nutritionState = 5;
                break;
            case 89:
                if (height < 111.8)
                    nutritionState = 1;
                else if (height >= 111.8 && height < 114.3)
                    nutritionState = 2;
                else if (height >= 114.3 && height < 129.2)
                    nutritionState = 3;
                else if (height >= 129.2 && height < 131.7)
                    nutritionState = 4;
                else if (height >= 131.7)
                    nutritionState = 5;
                break;
            case 90:
                if (height < 112.2)
                    nutritionState = 1;
                else if (height >= 112.2 && height < 114.7)
                    nutritionState = 2;
                else if (height >= 114.7 && height < 129.6)
                    nutritionState = 3;
                else if (height >= 129.6 && height < 132.1)
                    nutritionState = 4;
                else if (height >= 132.1)
                    nutritionState = 5;
                break;
            case 91:
                if (height < 112.5)
                    nutritionState = 1;
                else if (height >= 112.5 && height < 115)
                    nutritionState = 2;
                else if (height >= 115 && height < 130.1)
                    nutritionState = 3;
                else if (height >= 130.1 && height < 132.6)
                    nutritionState = 4;
                else if (height >= 132.6)
                    nutritionState = 5;
                break;
            case 92:
                if (height < 112.9)
                    nutritionState = 1;
                else if (height >= 112.9 && height < 115.4)
                    nutritionState = 2;
                else if (height >= 115.4 && height < 130.6)
                    nutritionState = 3;
                else if (height >= 130.6 && height < 133.1)
                    nutritionState = 4;
                else if (height >= 133.1)
                    nutritionState = 5;
                break;
            case 93:
                if (height < 113.3)
                    nutritionState = 1;
                else if (height >= 113.3 && height < 115.8)
                    nutritionState = 2;
                else if (height >= 115.8 && height < 131.1)
                    nutritionState = 3;
                else if (height >= 131.1 && height < 133.6)
                    nutritionState = 4;
                else if (height >= 133.6)
                    nutritionState = 5;
                break;
            case 94:
                if (height < 113.7)
                    nutritionState = 1;
                else if (height >= 113.7 && height < 116.2)
                    nutritionState = 2;
                else if (height >= 116.2 && height < 131.6)
                    nutritionState = 3;
                else if (height >= 131.6 && height < 134.1)
                    nutritionState = 4;
                else if (height >= 134.1)
                    nutritionState = 5;
                break;
            case 95:
                if (height < 114.1)
                    nutritionState = 1;
                else if (height >= 114.1 && height < 116.6)
                    nutritionState = 2;
                else if (height >= 116.6 && height < 132.1)
                    nutritionState = 3;
                else if (height >= 132.1 && height < 134.6)
                    nutritionState = 4;
                else if (height >= 134.6)
                    nutritionState = 5;
                break;
            case 96:
                if (height < 114.4)
                    nutritionState = 1;
                else if (height >= 114.4 && height < 117)
                    nutritionState = 2;
                else if (height >= 117 && height < 132.6)
                    nutritionState = 3;
                else if (height >= 132.6 && height < 135.2)
                    nutritionState = 4;
                else if (height >= 135.2)
                    nutritionState = 5;
                break;
            case 97:
                if (height < 114.8)
                    nutritionState = 1;
                else if (height >= 114.8 && height < 117.4)
                    nutritionState = 2;
                else if (height >= 117.4 && height < 133.1)
                    nutritionState = 3;
                else if (height >= 133.1 && height < 135.7)
                    nutritionState = 4;
                else if (height >= 135.7)
                    nutritionState = 5;
                break;
            case 98:
                if (height < 115.2)
                    nutritionState = 1;
                else if (height >= 115.2 && height < 117.8)
                    nutritionState = 2;
                else if (height >= 117.8 && height < 133.6)
                    nutritionState = 3;
                else if (height >= 133.6 && height < 136.2)
                    nutritionState = 4;
                else if (height >= 136.2)
                    nutritionState = 5;
                break;
            case 99:
                if (height < 115.6)
                    nutritionState = 1;
                else if (height >= 115.6 && height < 118.2)
                    nutritionState = 2;
                else if (height >= 118.2 && height < 134.2)
                    nutritionState = 3;
                else if (height >= 134.2 && height < 136.8)
                    nutritionState = 4;
                else if (height >= 136.8)
                    nutritionState = 5;
                break;
            case 100:
                if (height < 116)
                    nutritionState = 1;
                else if (height >= 116 && height < 118.6)
                    nutritionState = 2;
                else if (height >= 118.6 && height < 134.8)
                    nutritionState = 3;
                else if (height >= 134.8 && height < 137.4)
                    nutritionState = 4;
                else if (height >= 137.4)
                    nutritionState = 5;
                break;
            case 101:
                if (height < 116.4)
                    nutritionState = 1;
                else if (height >= 116.4 && height < 119)
                    nutritionState = 2;
                else if (height >= 119 && height < 135.3)
                    nutritionState = 3;
                else if (height >= 135.3 && height < 137.9)
                    nutritionState = 4;
                else if (height >= 137.9)
                    nutritionState = 5;
                break;
            case 102:
                if (height < 116.8)
                    nutritionState = 1;
                else if (height >= 116.8 && height < 119.5)
                    nutritionState = 2;
                else if (height >= 119.5 && height < 135.8)
                    nutritionState = 3;
                else if (height >= 135.8 && height < 138.5)
                    nutritionState = 4;
                else if (height >= 138.5)
                    nutritionState = 5;
                break;
            case 103:
                if (height < 117.2)
                    nutritionState = 1;
                else if (height >= 117.2 && height < 119.9)
                    nutritionState = 2;
                else if (height >= 119.9 && height < 136.4)
                    nutritionState = 3;
                else if (height >= 136.4 && height < 139.2)
                    nutritionState = 4;
                else if (height >= 139.2)
                    nutritionState = 5;
                break;
            case 104:
                if (height < 117.6)
                    nutritionState = 1;
                else if (height >= 117.6 && height < 120.3)
                    nutritionState = 2;
                else if (height >= 120.3 && height < 137)
                    nutritionState = 3;
                else if (height >= 137 && height < 139.8)
                    nutritionState = 4;
                else if (height >= 139.8)
                    nutritionState = 5;
                break;
            case 105:
                if (height < 117.9)
                    nutritionState = 1;
                else if (height >= 117.9 && height < 120.7)
                    nutritionState = 2;
                else if (height >= 120.7 && height < 137.5)
                    nutritionState = 3;
                else if (height >= 137.5 && height < 140.4)
                    nutritionState = 4;
                else if (height >= 140.4)
                    nutritionState = 5;
                break;
            case 106:
                if (height < 118.3)
                    nutritionState = 1;
                else if (height >= 118.3 && height < 121.1)
                    nutritionState = 2;
                else if (height >= 121.1 && height < 138.1)
                    nutritionState = 3;
                else if (height >= 138.1 && height < 141)
                    nutritionState = 4;
                else if (height >= 141)
                    nutritionState = 5;
                break;
            case 107:
                if (height < 118.7)
                    nutritionState = 1;
                else if (height >= 118.7 && height < 121.5)
                    nutritionState = 2;
                else if (height >= 121.5 && height < 138.7)
                    nutritionState = 3;
                else if (height >= 138.7 && height < 141.6)
                    nutritionState = 4;
                else if (height >= 141.6)
                    nutritionState = 5;
                break;
            // 108 - 119 ????? (10 ??)
            case 108:
                if (height < 119.1)
                    nutritionState = 1;
                else if (height >= 119.1 && height < 121.9)
                    nutritionState = 2;
                else if (height >= 121.9 && height < 139.2)
                    nutritionState = 3;
                else if (height >= 139.2 && height < 142.2)
                    nutritionState = 4;
                else if (height >= 142.2)
                    nutritionState = 5;
                break;
            case 109:
                if (height < 119.5)
                    nutritionState = 1;
                else if (height >= 119.5 && height < 122.3)
                    nutritionState = 2;
                else if (height >= 122.3 && height < 139.8)
                    nutritionState = 3;
                else if (height >= 139.8 && height < 142.8)
                    nutritionState = 4;
                else if (height >= 142.8)
                    nutritionState = 5;
                break;
            case 110:
                if (height < 119.9)
                    nutritionState = 1;
                else if (height >= 119.9 && height < 122.7)
                    nutritionState = 2;
                else if (height >= 122.7 && height < 140.4)
                    nutritionState = 3;
                else if (height >= 140.4 && height < 143.4)
                    nutritionState = 4;
                else if (height >= 143.4)
                    nutritionState = 5;
                break;
            case 111:
                if (height < 120.3)
                    nutritionState = 1;
                else if (height >= 120.3 && height < 123.1)
                    nutritionState = 2;
                else if (height >= 123.1 && height < 140.9)
                    nutritionState = 3;
                else if (height >= 140.9 && height < 144)
                    nutritionState = 4;
                else if (height >= 144)
                    nutritionState = 5;
                break;
            case 112:
                if (height < 120.7)
                    nutritionState = 1;
                else if (height >= 120.7 && height < 123.5)
                    nutritionState = 2;
                else if (height >= 123.5 && height < 141.5)
                    nutritionState = 3;
                else if (height >= 141.5 && height < 144.6)
                    nutritionState = 4;
                else if (height >= 144.6)
                    nutritionState = 5;
                break;
            case 113:
                if (height < 121.1)
                    nutritionState = 1;
                else if (height >= 121.1 && height < 124)
                    nutritionState = 2;
                else if (height >= 124 && height < 142.1)
                    nutritionState = 3;
                else if (height >= 142.1 && height < 145.3)
                    nutritionState = 4;
                else if (height >= 145.3)
                    nutritionState = 5;
                break;
            case 114:
                if (height < 121.6)
                    nutritionState = 1;
                else if (height >= 121.6 && height < 124.5)
                    nutritionState = 2;
                else if (height >= 124.5 && height < 142.7)
                    nutritionState = 3;
                else if (height >= 142.7 && height < 145.9)
                    nutritionState = 4;
                else if (height >= 145.9)
                    nutritionState = 5;
                break;
            case 115:
                if (height < 122)
                    nutritionState = 1;
                else if (height >= 122 && height < 124.9)
                    nutritionState = 2;
                else if (height >= 124.9 && height < 143.2)
                    nutritionState = 3;
                else if (height >= 143.2 && height < 146.4)
                    nutritionState = 4;
                else if (height >= 146.4)
                    nutritionState = 5;
                break;
            case 116:
                if (height < 122.4)
                    nutritionState = 1;
                else if (height >= 122.4 && height < 125.3)
                    nutritionState = 2;
                else if (height >= 125.3 && height < 143.8)
                    nutritionState = 3;
                else if (height >= 143.8 && height < 147)
                    nutritionState = 4;
                else if (height >= 147)
                    nutritionState = 5;
                break;
            case 117:
                if (height < 122.8)
                    nutritionState = 1;
                else if (height >= 122.8 && height < 125.7)
                    nutritionState = 2;
                else if (height >= 125.7 && height < 144.3)
                    nutritionState = 3;
                else if (height >= 144.3 && height < 147.6)
                    nutritionState = 4;
                else if (height >= 147.6)
                    nutritionState = 5;
                break;
            case 118:
                if (height < 123.2)
                    nutritionState = 1;
                else if (height >= 123.2 && height < 126.2)
                    nutritionState = 2;
                else if (height >= 126.2 && height < 144.9)
                    nutritionState = 3;
                else if (height >= 144.9 && height < 148.2)
                    nutritionState = 4;
                else if (height >= 148.2)
                    nutritionState = 5;
                break;
            case 119:
                if (height < 123.7)
                    nutritionState = 1;
                else if (height >= 123.7 && height < 126.7)
                    nutritionState = 2;
                else if (height >= 126.7 && height < 145.6)
                    nutritionState = 3;
                else if (height >= 145.6 && height < 148.9)
                    nutritionState = 4;
                else if (height >= 148.9)
                    nutritionState = 5;
                break;
            // 120 - 131 ????? (11 ??)
            case 120:
                if (height < 124.1)
                    nutritionState = 1;
                else if (height >= 124.1 && height < 127.1)
                    nutritionState = 2;
                else if (height >= 127.1 && height < 146.2)
                    nutritionState = 3;
                else if (height >= 146.2 && height < 149.5)
                    nutritionState = 4;
                else if (height >= 149.5)
                    nutritionState = 5;
                break;
            case 121:
                if (height < 124.5)
                    nutritionState = 1;
                else if (height >= 124.5 && height < 127.5)
                    nutritionState = 2;
                else if (height >= 127.5 && height < 146.8)
                    nutritionState = 3;
                else if (height >= 146.8 && height < 150.1)
                    nutritionState = 4;
                else if (height >= 150.1)
                    nutritionState = 5;
                break;
            case 122:
                if (height < 125)
                    nutritionState = 1;
                else if (height >= 125 && height < 128.1)
                    nutritionState = 2;
                else if (height >= 128.1 && height < 147.4)
                    nutritionState = 3;
                else if (height >= 147.4 && height < 150.7)
                    nutritionState = 4;
                else if (height >= 150.7)
                    nutritionState = 5;
                break;
            case 123:
                if (height < 125.3)
                    nutritionState = 1;
                else if (height >= 125.3 && height < 128.5)
                    nutritionState = 2;
                else if (height >= 128.5 && height < 148)
                    nutritionState = 3;
                else if (height >= 148 && height < 151.3)
                    nutritionState = 4;
                else if (height >= 151.3)
                    nutritionState = 5;
                break;
            case 124:
                if (height < 125.8)
                    nutritionState = 1;
                else if (height >= 125.8 && height < 129)
                    nutritionState = 2;
                else if (height >= 129 && height < 148.6)
                    nutritionState = 3;
                else if (height >= 148.6 && height < 151.9)
                    nutritionState = 4;
                else if (height >= 151.9)
                    nutritionState = 5;
                break;
            case 125:
                if (height < 126.2)
                    nutritionState = 1;
                else if (height >= 126.2 && height < 129.5)
                    nutritionState = 2;
                else if (height >= 129.5 && height < 149.2)
                    nutritionState = 3;
                else if (height >= 149.2 && height < 152.5)
                    nutritionState = 4;
                else if (height >= 152.5)
                    nutritionState = 5;
                break;
            case 126:
                if (height < 126.7)
                    nutritionState = 1;
                else if (height >= 126.7 && height < 130)
                    nutritionState = 2;
                else if (height >= 130 && height < 149.8)
                    nutritionState = 3;
                else if (height >= 149.8 && height < 153.1)
                    nutritionState = 4;
                else if (height >= 153.1)
                    nutritionState = 5;
                break;
            case 127:
                if (height < 127.2)
                    nutritionState = 1;
                else if (height >= 127.2 && height < 130.5)
                    nutritionState = 2;
                else if (height >= 130.5 && height < 150.3)
                    nutritionState = 3;
                else if (height >= 150.3 && height < 153.6)
                    nutritionState = 4;
                else if (height >= 153.6)
                    nutritionState = 5;
                break;
            case 128:
                if (height < 127.6)
                    nutritionState = 1;
                else if (height >= 127.6 && height < 130.9)
                    nutritionState = 2;
                else if (height >= 130.9 && height < 150.8)
                    nutritionState = 3;
                else if (height >= 150.8 && height < 154.1)
                    nutritionState = 4;
                else if (height >= 154.1)
                    nutritionState = 5;
                break;
            case 129:
                if (height < 128.1)
                    nutritionState = 1;
                else if (height >= 128.1 && height < 131.5)
                    nutritionState = 2;
                else if (height >= 131.5 && height < 151.3)
                    nutritionState = 3;
                else if (height >= 151.3 && height < 154.6)
                    nutritionState = 4;
                else if (height >= 154.6)
                    nutritionState = 5;
                break;
            case 130:
                if (height < 128.6)
                    nutritionState = 1;
                else if (height >= 128.6 && height < 132)
                    nutritionState = 2;
                else if (height >= 132 && height < 151.8)
                    nutritionState = 3;
                else if (height >= 151.8 && height < 155.1)
                    nutritionState = 4;
                else if (height >= 155.1)
                    nutritionState = 5;
                break;
            case 131:
                if (height < 129)
                    nutritionState = 1;
                else if (height >= 129 && height < 132.4)
                    nutritionState = 2;
                else if (height >= 132.4 && height < 152.3)
                    nutritionState = 3;
                else if (height >= 152.3 && height < 155.5)
                    nutritionState = 4;
                else if (height >= 155.5)
                    nutritionState = 5;
                break;
            // 132 - 143 ????? (12 ??)
            case 132:
                if (height < 129.5)
                    nutritionState = 1;
                else if (height >= 129.5 && height < 132.9)
                    nutritionState = 2;
                else if (height >= 132.9 && height < 152.7)
                    nutritionState = 3;
                else if (height >= 152.7 && height < 155.9)
                    nutritionState = 4;
                else if (height >= 155.9)
                    nutritionState = 5;
                break;
            case 133:
                if (height < 130)
                    nutritionState = 1;
                else if (height >= 130 && height < 133.4)
                    nutritionState = 2;
                else if (height >= 133.4 && height < 153.1)
                    nutritionState = 3;
                else if (height >= 153.1 && height < 156.3)
                    nutritionState = 4;
                else if (height >= 156.3)
                    nutritionState = 5;
                break;
            case 134:
                if (height < 130.5)
                    nutritionState = 1;
                else if (height >= 130.5 && height < 133.9)
                    nutritionState = 2;
                else if (height >= 133.9 && height < 153.4)
                    nutritionState = 3;
                else if (height >= 153.4 && height < 156.5)
                    nutritionState = 4;
                else if (height >= 156.5)
                    nutritionState = 5;
                break;
            case 135:
                if (height < 131)
                    nutritionState = 1;
                else if (height >= 131 && height < 134.3)
                    nutritionState = 2;
                else if (height >= 134.3 && height < 153.8)
                    nutritionState = 3;
                else if (height >= 153.8 && height < 156.9)
                    nutritionState = 4;
                else if (height >= 156.9)
                    nutritionState = 5;
                break;
            case 136:
                if (height < 131.5)
                    nutritionState = 1;
                else if (height >= 131.5 && height < 134.8)
                    nutritionState = 2;
                else if (height >= 134.8 && height < 154.2)
                    nutritionState = 3;
                else if (height >= 154.2 && height < 157.2)
                    nutritionState = 4;
                else if (height >= 157.2)
                    nutritionState = 5;
                break;
            case 137:
                if (height < 131.9)
                    nutritionState = 1;
                else if (height >= 131.9 && height < 135.2)
                    nutritionState = 2;
                else if (height >= 135.2 && height < 154.5)
                    nutritionState = 3;
                else if (height >= 154.5 && height < 157.5)
                    nutritionState = 4;
                else if (height >= 157.5)
                    nutritionState = 5;
                break;
            case 138:
                if (height < 132.4)
                    nutritionState = 1;
                else if (height >= 132.4 && height < 135.7)
                    nutritionState = 2;
                else if (height >= 135.7 && height < 154.8)
                    nutritionState = 3;
                else if (height >= 154.8 && height < 157.8)
                    nutritionState = 4;
                else if (height >= 157.8)
                    nutritionState = 5;
                break;
            case 139:
                if (height < 132.9)
                    nutritionState = 1;
                else if (height >= 132.9 && height < 136.2)
                    nutritionState = 2;
                else if (height >= 136.2 && height < 155.2)
                    nutritionState = 3;
                else if (height >= 155.2 && height < 158.2)
                    nutritionState = 4;
                else if (height >= 158.2)
                    nutritionState = 5;
                break;
            case 140:
                if (height < 133.4)
                    nutritionState = 1;
                else if (height >= 133.4 && height < 136.7)
                    nutritionState = 2;
                else if (height >= 136.7 && height < 155.6)
                    nutritionState = 3;
                else if (height >= 155.6 && height < 158.5)
                    nutritionState = 4;
                else if (height >= 158.5)
                    nutritionState = 5;
                break;
            case 141:
                if (height < 133.9)
                    nutritionState = 1;
                else if (height >= 133.9 && height < 137.2)
                    nutritionState = 2;
                else if (height >= 137.2 && height < 155.9)
                    nutritionState = 3;
                else if (height >= 155.9 && height < 158.8)
                    nutritionState = 4;
                else if (height >= 158.8)
                    nutritionState = 5;
                break;
            case 142:
                if (height < 134.4)
                    nutritionState = 1;
                else if (height >= 134.4 && height < 137.8)
                    nutritionState = 2;
                else if (height >= 137.8 && height < 156.3)
                    nutritionState = 3;
                else if (height >= 156.3 && height < 159.1)
                    nutritionState = 4;
                else if (height >= 159.1)
                    nutritionState = 5;
                break;
            case 143:
                if (height < 134.9)
                    nutritionState = 1;
                else if (height >= 134.9 && height < 138.3)
                    nutritionState = 2;
                else if (height >= 138.3 && height < 156.6)
                    nutritionState = 3;
                else if (height >= 156.6 && height < 159.4)
                    nutritionState = 4;
                else if (height >= 159.4)
                    nutritionState = 5;
                break;
            // 144 - 155 ????? (13 ??)
            case 144:
                if (height < 135.4)
                    nutritionState = 1;
                else if (height >= 135.4 && height < 138.8)
                    nutritionState = 2;
                else if (height >= 138.8 && height < 157)
                    nutritionState = 3;
                else if (height >= 157 && height < 159.6)
                    nutritionState = 4;
                else if (height >= 159.6)
                    nutritionState = 5;
                break;
            case 145:
                if (height < 135.9)
                    nutritionState = 1;
                else if (height >= 135.9 && height < 139.2)
                    nutritionState = 2;
                else if (height >= 139.2 && height < 157.3)
                    nutritionState = 3;
                else if (height >= 157.3 && height < 159.9)
                    nutritionState = 4;
                else if (height >= 159.9)
                    nutritionState = 5;
                break;
            case 146:
                if (height < 136.4)
                    nutritionState = 1;
                else if (height >= 136.4 && height < 139.7)
                    nutritionState = 2;
                else if (height >= 139.7 && height < 157.6)
                    nutritionState = 3;
                else if (height >= 157.6 && height < 160.2)
                    nutritionState = 4;
                else if (height >= 160.2)
                    nutritionState = 5;
                break;
            case 147:
                if (height < 136.8)
                    nutritionState = 1;
                else if (height >= 136.8 && height < 140.1)
                    nutritionState = 2;
                else if (height >= 140.1 && height < 157.9)
                    nutritionState = 3;
                else if (height >= 157.9 && height < 160.5)
                    nutritionState = 4;
                else if (height >= 160.5)
                    nutritionState = 5;
                break;
            case 148:
                if (height < 137.3)
                    nutritionState = 1;
                else if (height >= 137.3 && height < 140.6)
                    nutritionState = 2;
                else if (height >= 140.6 && height < 158.2)
                    nutritionState = 3;
                else if (height >= 158.2 && height < 160.7)
                    nutritionState = 4;
                else if (height >= 160.7)
                    nutritionState = 5;
                break;
            case 149:
                if (height < 137.7)
                    nutritionState = 1;
                else if (height >= 137.7 && height < 141)
                    nutritionState = 2;
                else if (height >= 141 && height < 158.5)
                    nutritionState = 3;
                else if (height >= 158.5 && height < 161)
                    nutritionState = 4;
                else if (height >= 161)
                    nutritionState = 5;
                break;
            case 150:
                if (height < 138.1)
                    nutritionState = 1;
                else if (height >= 138.1 && height < 141.4)
                    nutritionState = 2;
                else if (height >= 141.4 && height < 158.8)
                    nutritionState = 3;
                else if (height >= 158.8 && height < 161.3)
                    nutritionState = 4;
                else if (height >= 161.3)
                    nutritionState = 5;
                break;
            case 151:
                if (height < 138.5)
                    nutritionState = 1;
                else if (height >= 138.5 && height < 141.7)
                    nutritionState = 2;
                else if (height >= 141.7 && height < 159.1)
                    nutritionState = 3;
                else if (height >= 159.1 && height < 161.6)
                    nutritionState = 4;
                else if (height >= 161.6)
                    nutritionState = 5;
                break;
            case 152:
                if (height < 138.9)
                    nutritionState = 1;
                else if (height >= 138.9 && height < 142.1)
                    nutritionState = 2;
                else if (height >= 142.1 && height < 159.3)
                    nutritionState = 3;
                else if (height >= 159.3 && height < 161.8)
                    nutritionState = 4;
                else if (height >= 161.8)
                    nutritionState = 5;
                break;
            case 153:
                if (height < 139.3)
                    nutritionState = 1;
                else if (height >= 139.3 && height < 142.5)
                    nutritionState = 2;
                else if (height >= 142.5 && height < 159.6)
                    nutritionState = 3;
                else if (height >= 159.6 && height < 162.1)
                    nutritionState = 4;
                else if (height >= 162.1)
                    nutritionState = 5;
                break;
            case 154:
                if (height < 139.6)
                    nutritionState = 1;
                else if (height >= 139.6 && height < 142.8)
                    nutritionState = 2;
                else if (height >= 142.8 && height < 159.8)
                    nutritionState = 3;
                else if (height >= 159.8 && height < 162.3)
                    nutritionState = 4;
                else if (height >= 162.3)
                    nutritionState = 5;
                break;
            case 155:
                if (height < 140.1)
                    nutritionState = 1;
                else if (height >= 140.1 && height < 143.2)
                    nutritionState = 2;
                else if (height >= 143.2 && height < 160.1)
                    nutritionState = 3;
                else if (height >= 160.1 && height < 162.6)
                    nutritionState = 4;
                else if (height >= 162.6)
                    nutritionState = 5;
                break;
            case 156:
                if (height < 140.5)
                    nutritionState = 1;
                else if (height >= 140.5 && height < 143.5)
                    nutritionState = 2;
                else if (height >= 143.5 && height < 160.3)
                    nutritionState = 3;
                else if (height >= 160.3 && height < 162.8)
                    nutritionState = 4;
                else if (height >= 162.8)
                    nutritionState = 5;
                break;
            case 157:
                if (height < 140.8)
                    nutritionState = 1;
                else if (height >= 140.8 && height < 143.8)
                    nutritionState = 2;
                else if (height >= 143.8 && height < 160.5)
                    nutritionState = 3;
                else if (height >= 160.5 && height < 163)
                    nutritionState = 4;
                else if (height >= 163)
                    nutritionState = 5;
                break;
            case 158:
                if (height < 141.2)
                    nutritionState = 1;
                else if (height >= 141.2 && height < 144.2)
                    nutritionState = 2;
                else if (height >= 144.2 && height < 160.7)
                    nutritionState = 3;
                else if (height >= 160.7 && height < 163.2)
                    nutritionState = 4;
                else if (height >= 163.2)
                    nutritionState = 5;
                break;
            case 159:
                if (height < 141.6)
                    nutritionState = 1;
                else if (height >= 141.6 && height < 144.5)
                    nutritionState = 2;
                else if (height >= 144.5 && height < 160.9)
                    nutritionState = 3;
                else if (height >= 160.9 && height < 163.4)
                    nutritionState = 4;
                else if (height >= 163.4)
                    nutritionState = 5;
                break;
            case 160:
                if (height < 141.9)
                    nutritionState = 1;
                else if (height >= 141.9 && height < 144.8)
                    nutritionState = 2;
                else if (height >= 144.8 && height < 161.1)
                    nutritionState = 3;
                else if (height >= 161.1 && height < 163.6)
                    nutritionState = 4;
                else if (height >= 163.6)
                    nutritionState = 5;
                break;
            case 161:
                if (height < 142.3)
                    nutritionState = 1;
                else if (height >= 142.3 && height < 145.1)
                    nutritionState = 2;
                else if (height >= 145.1 && height < 161.3)
                    nutritionState = 3;
                else if (height >= 161.3 && height < 163.8)
                    nutritionState = 4;
                else if (height >= 163.8)
                    nutritionState = 5;
                break;
            case 162:
                if (height < 142.6)
                    nutritionState = 1;
                else if (height >= 142.6 && height < 145.4)
                    nutritionState = 2;
                else if (height >= 145.4 && height < 161.5)
                    nutritionState = 3;
                else if (height >= 161.5 && height < 164)
                    nutritionState = 4;
                else if (height >= 164)
                    nutritionState = 5;
                break;
            case 163:
                if (height < 143)
                    nutritionState = 1;
                else if (height >= 143 && height < 145.7)
                    nutritionState = 2;
                else if (height >= 145.7 && height < 161.6)
                    nutritionState = 3;
                else if (height >= 161.6 && height < 164.2)
                    nutritionState = 4;
                else if (height >= 164.2)
                    nutritionState = 5;
                break;
            case 164:
                if (height < 143.3)
                    nutritionState = 1;
                else if (height >= 143.3 && height < 145.9)
                    nutritionState = 2;
                else if (height >= 145.9 && height < 161.8)
                    nutritionState = 3;
                else if (height >= 161.8 && height < 164.3)
                    nutritionState = 4;
                else if (height >= 164.3)
                    nutritionState = 5;
                break;
            case 165:
                if (height < 143.6)
                    nutritionState = 1;
                else if (height >= 143.6 && height < 146.2)
                    nutritionState = 2;
                else if (height >= 146.2 && height < 162)
                    nutritionState = 3;
                else if (height >= 162 && height < 164.5)
                    nutritionState = 4;
                else if (height >= 164.5)
                    nutritionState = 5;
                break;
            case 166:
                if (height < 143.9)
                    nutritionState = 1;
                else if (height >= 143.9 && height < 146.5)
                    nutritionState = 2;
                else if (height >= 146.5 && height < 162.1)
                    nutritionState = 3;
                else if (height >= 162.1 && height < 164.6)
                    nutritionState = 4;
                else if (height >= 164.6)
                    nutritionState = 5;
                break;
            case 167:
                if (height < 144.1)
                    nutritionState = 1;
                else if (height >= 144.1 && height < 146.7)
                    nutritionState = 2;
                else if (height >= 146.7 && height < 162.3)
                    nutritionState = 3;
                else if (height >= 162.3 && height < 164.8)
                    nutritionState = 4;
                else if (height >= 164.8)
                    nutritionState = 5;
                break;
            // 168 - 179 ????? (15 ??)
            case 168:
                if (height < 144.4)
                    nutritionState = 1;
                else if (height >= 144.4 && height < 147)
                    nutritionState = 2;
                else if (height >= 147 && height < 162.4)
                    nutritionState = 3;
                else if (height >= 162.4 && height < 164.9)
                    nutritionState = 4;
                else if (height >= 164.9)
                    nutritionState = 5;
                break;
            case 169:
                if (height < 144.6)
                    nutritionState = 1;
                else if (height >= 144.6 && height < 147.1)
                    nutritionState = 2;
                else if (height >= 147.1 && height < 162.5)
                    nutritionState = 3;
                else if (height >= 162.5 && height < 165)
                    nutritionState = 4;
                else if (height >= 165)
                    nutritionState = 5;
                break;
            case 170:
                if (height < 144.7)
                    nutritionState = 1;
                else if (height >= 144.7 && height < 147.3)
                    nutritionState = 2;
                else if (height >= 147.3 && height < 162.6)
                    nutritionState = 3;
                else if (height >= 162.6 && height < 165.1)
                    nutritionState = 4;
                else if (height >= 165.1)
                    nutritionState = 5;
                break;
            case 171:
                if (height < 144.9)
                    nutritionState = 1;
                else if (height >= 144.9 && height < 147.4)
                    nutritionState = 2;
                else if (height >= 147.4 && height < 162.8)
                    nutritionState = 3;
                else if (height >= 162.8 && height < 165.3)
                    nutritionState = 4;
                else if (height >= 165.3)
                    nutritionState = 5;
                break;
            case 172:
                if (height < 145)
                    nutritionState = 1;
                else if (height >= 145 && height < 147.5)
                    nutritionState = 2;
                else if (height >= 147.5 && height < 162.9)
                    nutritionState = 3;
                else if (height >= 162.9 && height < 165.4)
                    nutritionState = 4;
                else if (height >= 165.4)
                    nutritionState = 5;
                break;
            case 173:
                if (height < 145.2)
                    nutritionState = 1;
                else if (height >= 145.2 && height < 147.7)
                    nutritionState = 2;
                else if (height >= 147.7 && height < 163)
                    nutritionState = 3;
                else if (height >= 163 && height < 165.5)
                    nutritionState = 4;
                else if (height >= 165.5)
                    nutritionState = 5;
                break;
            case 174:
                if (height < 145.3)
                    nutritionState = 1;
                else if (height >= 145.3 && height < 147.8)
                    nutritionState = 2;
                else if (height >= 147.8 && height < 163.1)
                    nutritionState = 3;
                else if (height >= 163.1 && height < 165.6)
                    nutritionState = 4;
                else if (height >= 165.6)
                    nutritionState = 5;
                break;
            case 175:
                if (height < 145.4)
                    nutritionState = 1;
                else if (height >= 145.4 && height < 147.9)
                    nutritionState = 2;
                else if (height >= 147.9 && height < 163.2)
                    nutritionState = 3;
                else if (height >= 163.2 && height < 165.7)
                    nutritionState = 4;
                else if (height >= 165.7)
                    nutritionState = 5;
                break;
            case 176:
                if (height < 145.5)
                    nutritionState = 1;
                else if (height >= 145.5 && height < 148)
                    nutritionState = 2;
                else if (height >= 148 && height < 163.2)
                    nutritionState = 3;
                else if (height >= 163.2 && height < 165.7)
                    nutritionState = 4;
                else if (height >= 165.7)
                    nutritionState = 5;
                break;
            case 177:
                if (height < 145.6)
                    nutritionState = 1;
                else if (height >= 145.6 && height < 148.1)
                    nutritionState = 2;
                else if (height >= 148.1 && height < 163.3)
                    nutritionState = 3;
                else if (height >= 163.3 && height < 165.8)
                    nutritionState = 4;
                else if (height >= 165.8)
                    nutritionState = 5;
                break;
            case 178:
                if (height < 145.7)
                    nutritionState = 1;
                else if (height >= 145.7 && height < 148.2)
                    nutritionState = 2;
                else if (height >= 148.2 && height < 163.5)
                    nutritionState = 3;
                else if (height >= 163.5 && height < 165.9)
                    nutritionState = 4;
                else if (height >= 165.9)
                    nutritionState = 5;
                break;
            case 179:
                if (height < 145.8)
                    nutritionState = 1;
                else if (height >= 145.8 && height < 148.3)
                    nutritionState = 2;
                else if (height >= 148.3 && height < 163.5)
                    nutritionState = 3;
                else if (height >= 163.5 && height < 166)
                    nutritionState = 4;
                else if (height >= 166)
                    nutritionState = 5;
                break;
            // 180 - 191 ????? (16 ??)
            case 180:
                if (height < 145.9)
                    nutritionState = 1;
                else if (height >= 145.9 && height < 148.4)
                    nutritionState = 2;
                else if (height >= 148.4 && height < 163.6)
                    nutritionState = 3;
                else if (height >= 163.6 && height < 166.1)
                    nutritionState = 4;
                else if (height >= 166.1)
                    nutritionState = 5;
                break;
            case 181:
                if (height < 145.9)
                    nutritionState = 1;
                else if (height >= 145.9 && height < 148.4)
                    nutritionState = 2;
                else if (height >= 148.4 && height < 163.7)
                    nutritionState = 3;
                else if (height >= 163.7 && height < 166.2)
                    nutritionState = 4;
                else if (height >= 166.2)
                    nutritionState = 5;
                break;
            case 182:
                if (height < 146)
                    nutritionState = 1;
                else if (height >= 146 && height < 148.5)
                    nutritionState = 2;
                else if (height >= 148.5 && height < 163.8)
                    nutritionState = 3;
                else if (height >= 163.8 && height < 166.3)
                    nutritionState = 4;
                else if (height >= 166.3)
                    nutritionState = 5;
                break;
            case 183:
                if (height < 146.1)
                    nutritionState = 1;
                else if (height >= 146.1 && height < 148.6)
                    nutritionState = 2;
                else if (height >= 148.6 && height < 163.8)
                    nutritionState = 3;
                else if (height >= 163.8 && height < 166.3)
                    nutritionState = 4;
                else if (height >= 166.3)
                    nutritionState = 5;
                break;
            case 184:
                if (height < 146.2)
                    nutritionState = 1;
                else if (height >= 146.2 && height < 148.7)
                    nutritionState = 2;
                else if (height >= 148.7 && height < 163.8)
                    nutritionState = 3;
                else if (height >= 163.8 && height < 166.3)
                    nutritionState = 4;
                else if (height >= 166.3)
                    nutritionState = 5;
                break;
            case 185:
                if (height < 146.2)
                    nutritionState = 1;
                else if (height >= 146.2 && height < 148.7)
                    nutritionState = 2;
                else if (height >= 148.7 && height < 163.9)
                    nutritionState = 3;
                else if (height >= 163.9 && height < 166.4)
                    nutritionState = 4;
                else if (height >= 166.4)
                    nutritionState = 5;
                break;
            case 186:
                if (height < 146.3)
                    nutritionState = 1;
                else if (height >= 146.3 && height < 148.8)
                    nutritionState = 2;
                else if (height >= 148.8 && height < 163.9)
                    nutritionState = 3;
                else if (height >= 163.9 && height < 166.4)
                    nutritionState = 4;
                else if (height >= 166.4)
                    nutritionState = 5;
                break;
            case 187:
                if (height < 146.3)
                    nutritionState = 1;
                else if (height >= 146.3 && height < 148.8)
                    nutritionState = 2;
                else if (height >= 148.8 && height < 164)
                    nutritionState = 3;
                else if (height >= 164 && height < 166.5)
                    nutritionState = 4;
                else if (height >= 166.5)
                    nutritionState = 5;
                break;
            case 188:
                if (height < 146.4)
                    nutritionState = 1;
                else if (height >= 146.4 && height < 148.9)
                    nutritionState = 2;
                else if (height >= 148.9 && height < 164)
                    nutritionState = 3;
                else if (height >= 164 && height < 166.5)
                    nutritionState = 4;
                else if (height >= 166.5)
                    nutritionState = 5;
                break;
            case 189:
                if (height < 146.4)
                    nutritionState = 1;
                else if (height >= 146.4 && height < 148.9)
                    nutritionState = 2;
                else if (height >= 148.9 && height < 164.1)
                    nutritionState = 3;
                else if (height >= 164.1 && height < 166.6)
                    nutritionState = 4;
                else if (height >= 166.6)
                    nutritionState = 5;
                break;
            case 190:
            case 191:
                if (height < 146.5)
                    nutritionState = 1;
                else if (height >= 146.5 && height < 149)
                    nutritionState = 2;
                else if (height >= 149 && height < 164.1)
                    nutritionState = 3;
                else if (height >= 164.1 && height < 166.6)
                    nutritionState = 4;
                else if (height >= 166.6)
                    nutritionState = 5;
                break;
            case 192:
            case 193:
            case 194:
                if (height < 146.6)
                    nutritionState = 1;
                else if (height >= 146.6 && height < 149.1)
                    nutritionState = 2;
                else if (height >= 149.1 && height < 164.1)
                    nutritionState = 3;
                else if (height >= 164.1 && height < 166.6)
                    nutritionState = 4;
                else if (height >= 166.6)
                    nutritionState = 5;
                break;
            case 195:
            case 196:
            case 197:
                if (height < 146.7)
                    nutritionState = 1;
                else if (height >= 146.7 && height < 149.2)
                    nutritionState = 2;
                else if (height >= 149.2 && height < 164.2)
                    nutritionState = 3;
                else if (height >= 164.2 && height < 166.7)
                    nutritionState = 4;
                else if (height >= 166.7)
                    nutritionState = 5;
                break;
            case 198:
            case 199:
            case 200:
                if (height < 146.8)
                    nutritionState = 1;
                else if (height >= 146.8 && height < 149.3)
                    nutritionState = 2;
                else if (height >= 149.3 && height < 164.2)
                    nutritionState = 3;
                else if (height >= 164.2 && height < 166.7)
                    nutritionState = 4;
                else if (height >= 166.7)
                    nutritionState = 5;
                break;
            case 201:
            case 202:
                if (height < 146.9)
                    nutritionState = 1;
                else if (height >= 146.9 && height < 149.4)
                    nutritionState = 2;
                else if (height >= 149.4 && height < 164.2)
                    nutritionState = 3;
                else if (height >= 164.2 && height < 166.7)
                    nutritionState = 4;
                else if (height >= 166.7)
                    nutritionState = 5;
                break;
		/*
		 * case 203: if(height < 147) nutritionState = 1; else if(height >= 147
		 * && height < 149.5) nutritionState = 2; else if(height >= 149.5 &&
		 * height < 164.3) nutritionState = 3; else if(height >= 164.3 && height
		 * < 166.7) nutritionState = 4; else if(height >= 166.7) nutritionState
		 * = 5; break;
		 */

            case 204:
                if (height < 147)
                    nutritionState = 1;
                else if (height >= 156.7 && height < 149.5)
                    nutritionState = 2;
                else if (height >= 159.6 && height < 164.3)
                    nutritionState = 3;
                else if (height >= 176.7 && height < 166.7)
                    nutritionState = 4;
                else if (height >= 179.4)
                    nutritionState = 5;
                break;
		/*
		 * case 205| 206: if(height < 147.1) nutritionState = 1; else if(height
		 * >= 147.1 && height < 149.6) nutritionState = 2; else if(height >=
		 * 149.6 && height < 164.3) nutritionState = 3; else if(height >= 164.3
		 * && height < 166.7) nutritionState = 4; else if(height >= 166.7)
		 * nutritionState = 5; break;
		 */
            case 207:
            case 208:
                if (height < 147.2)
                    nutritionState = 1;
                else if (height >= 147.2 && height < 149.6)
                    nutritionState = 2;
                else if (height >= 149.6 && height < 164.3)
                    nutritionState = 3;
                else if (height >= 164.3 && height < 166.7)
                    nutritionState = 4;
                else if (height >= 166.7)
                    nutritionState = 5;
                break;
            case 209:
            case 210:
            case 211:
                if (height < 147.3)
                    nutritionState = 1;
                else if (height >= 147.3 && height < 149.7)
                    nutritionState = 2;
                else if (height >= 149.7 && height < 164.3)
                    nutritionState = 3;
                else if (height >= 164.3 && height < 166.7)
                    nutritionState = 4;
                else if (height >= 166.7)
                    nutritionState = 5;
                break;
            case 212:
            case 213:
            case 214:
            case 215:
                if (height < 147.4)
                    nutritionState = 1;
                else if (height >= 147.4 && height < 149.8)
                    nutritionState = 2;
                else if (height >= 149.8 && height < 164.3)
                    nutritionState = 3;
                else if (height >= 164.3 && height < 166.7)
                    nutritionState = 4;
                else if (height >= 166.7)
                    nutritionState = 5;
                break;

            case 216:
            case 217:
            case 218:
            case 219:
            case 220:
            case 221:
            case 222:
            case 223:
            case 224:
            case 225:
            case 226:
            case 227:
                if (height < 147.4)
                    nutritionState = 1;
                else if (height >= 157.6 && height < 149.8)
                    nutritionState = 2;
                else if (height >= 160.5 && height < 164.3)
                    nutritionState = 3;
                else if (height >= 164.3 && height < 166.7)
                    nutritionState = 4;
                else if (height >= 180)
                    nutritionState = 5;
                break;

            case 12987:
            case 12988:
            case 12989:
                nutritionState = 9;

            default:
                nutritionState = 0;
                break;
        }
        return nutritionState;
    }

    // ��� : ���� / ���˹ѡ
    public static int GetNutritionStateMaleAgeLessThen18AgeWeight(int age,
                                                                  double weight) {
        int nutritionState = 0;

        switch (age) {
            case 0:
                if (weight < 2.7)
                    nutritionState = 1;
                else if (weight < 2.8)
                    nutritionState = 2;
                else if (weight < 4)
                    nutritionState = 3;
                else if (weight < 4.1)
                    nutritionState = 4;
                else if (weight >= 4.1)
                    nutritionState = 5;
                break;
            case 1:
                if (weight < 3.3)
                    nutritionState = 1;
                else if (weight < 3.4)
                    nutritionState = 2;
                else if (weight < 4.8)
                    nutritionState = 3;
                else if (weight < 5.1)
                    nutritionState = 4;
                else if (weight >= 5.1)
                    nutritionState = 5;
                break;
            case 2:
                if (weight < 3.9)
                    nutritionState = 1;
                else if (weight < 4.2)
                    nutritionState = 2;
                else if (weight < 5.6)
                    nutritionState = 3;
                else if (weight < 5.9)
                    nutritionState = 4;
                else if (weight >= 5.9)
                    nutritionState = 5;
                break;
            case 3:
                if (weight < 4.5)
                    nutritionState = 1;
                else if (weight < 4.8)
                    nutritionState = 2;
                else if (weight < 6.5)
                    nutritionState = 3;
                else if (weight < 6.8)
                    nutritionState = 4;
                else if (weight >= 6.8)
                    nutritionState = 5;
                break;
            case 4:
                if (weight < 5)
                    nutritionState = 1;
                else if (weight < 5.3)
                    nutritionState = 2;
                else if (weight < 7.2)
                    nutritionState = 3;
                else if (weight < 7.5)
                    nutritionState = 4;
                else if (weight >= 7.5)
                    nutritionState = 5;
                break;
            case 5:
                if (weight < 5.5)
                    nutritionState = 1;
                else if (weight < 5.8)
                    nutritionState = 2;
                else if (weight < 7.9)
                    nutritionState = 3;
                else if (weight < 8.2)
                    nutritionState = 4;
                else if (weight >= 8.2)
                    nutritionState = 5;
                break;
            case 6:
                if (weight < 6)
                    nutritionState = 1;
                else if (weight < 6.3)
                    nutritionState = 2;
                else if (weight < 8.5)
                    nutritionState = 3;
                else if (weight < 8.9)
                    nutritionState = 4;
                else if (weight >= 8.9)
                    nutritionState = 5;
                break;
            case 7:
                if (weight < 6.4)
                    nutritionState = 1;
                else if (weight < 6.8)
                    nutritionState = 2;
                else if (weight < 9.1)
                    nutritionState = 3;
                else if (weight < 9.5)
                    nutritionState = 4;
                else if (weight >= 9.5)
                    nutritionState = 5;
                break;
            case 8:
                if (weight < 6.8)
                    nutritionState = 1;
                else if (weight < 7.2)
                    nutritionState = 2;
                else if (weight < 9.6)
                    nutritionState = 3;
                else if (weight < 10)
                    nutritionState = 4;
                else if (weight >= 10)
                    nutritionState = 5;
                break;
            case 9:
                if (weight < 7.2)
                    nutritionState = 1;
                else if (weight < 7.6)
                    nutritionState = 2;
                else if (weight < 10)
                    nutritionState = 3;
                else if (weight < 10.4)
                    nutritionState = 4;
                else if (weight >= 10.4)
                    nutritionState = 5;
                break;
            case 10:
                if (weight < 7.5)
                    nutritionState = 1;
                else if (weight < 7.9)
                    nutritionState = 2;
                else if (weight < 10.4)
                    nutritionState = 3;
                else if (weight < 10.8)
                    nutritionState = 4;
                else if (weight >= 10.8)
                    nutritionState = 5;
                break;
            case 11:
                if (weight < 7.7)
                    nutritionState = 1;
                else if (weight < 8.1)
                    nutritionState = 2;
                else if (weight < 10.7)
                    nutritionState = 3;
                else if (weight < 11.2)
                    nutritionState = 4;
                else if (weight >= 11.2)
                    nutritionState = 5;
                break;
            case 12:
                if (weight < 7.9)
                    nutritionState = 1;
                else if (weight < 8.3)
                    nutritionState = 2;
                else if (weight < 11.1)
                    nutritionState = 3;
                else if (weight < 11.6)
                    nutritionState = 4;
                else if (weight >= 11.6)
                    nutritionState = 5;
                break;
            case 13:
                if (weight < 8.1)
                    nutritionState = 1;
                else if (weight < 8.5)
                    nutritionState = 2;
                else if (weight < 11.4)
                    nutritionState = 3;
                else if (weight < 11.9)
                    nutritionState = 4;
                else if (weight >= 11.9)
                    nutritionState = 5;
                break;
            case 14:
                if (weight < 8.3)
                    nutritionState = 1;
                else if (weight < 8.7)
                    nutritionState = 2;
                else if (weight < 11.8)
                    nutritionState = 3;
                else if (weight < 12.3)
                    nutritionState = 4;
                else if (weight >= 12.3)
                    nutritionState = 5;
                break;
            case 15:
                if (weight < 8.4)
                    nutritionState = 1;
                else if (weight < 8.9)
                    nutritionState = 2;
                else if (weight < 12.1)
                    nutritionState = 3;
                else if (weight < 12.6)
                    nutritionState = 4;
                else if (weight >= 12.6)
                    nutritionState = 5;
                break;
            case 16:
                if (weight < 8.6)
                    nutritionState = 1;
                else if (weight < 9.1)
                    nutritionState = 2;
                else if (weight < 12.4)
                    nutritionState = 3;
                else if (weight < 12.9)
                    nutritionState = 4;
                else if (weight >= 12.9)
                    nutritionState = 5;
                break;
            case 17:
                if (weight < 8.8)
                    nutritionState = 1;
                else if (weight < 9.3)
                    nutritionState = 2;
                else if (weight < 12.7)
                    nutritionState = 3;
                else if (weight < 13.2)
                    nutritionState = 4;
                else if (weight >= 13.2)
                    nutritionState = 5;
                break;
            case 18:
                if (weight < 8.9)
                    nutritionState = 1;
                else if (weight < 9.4)
                    nutritionState = 2;
                else if (weight < 13)
                    nutritionState = 3;
                else if (weight < 13.7)
                    nutritionState = 4;
                else if (weight >= 13.7)
                    nutritionState = 5;
                break;
            case 19:
                if (weight < 9.1)
                    nutritionState = 1;
                else if (weight < 9.6)
                    nutritionState = 2;
                else if (weight < 13.3)
                    nutritionState = 3;
                else if (weight < 14)
                    nutritionState = 4;
                else if (weight >= 14)
                    nutritionState = 5;
                break;
            case 20:
                if (weight < 9.3)
                    nutritionState = 1;
                else if (weight < 9.8)
                    nutritionState = 2;
                else if (weight < 13.6)
                    nutritionState = 3;
                else if (weight < 14.3)
                    nutritionState = 4;
                else if (weight >= 14.3)
                    nutritionState = 5;
                break;
            case 21:
                if (weight < 9.4)
                    nutritionState = 1;
                else if (weight < 9.9)
                    nutritionState = 2;
                else if (weight < 13.9)
                    nutritionState = 3;
                else if (weight < 14.6)
                    nutritionState = 4;
                else if (weight >= 14.6)
                    nutritionState = 5;
                break;
            case 22:
                if (weight < 9.6)
                    nutritionState = 1;
                else if (weight < 10.2)
                    nutritionState = 2;
                else if (weight < 14.1)
                    nutritionState = 3;
                else if (weight < 14.8)
                    nutritionState = 4;
                else if (weight >= 14.8)
                    nutritionState = 5;
                break;
            case 23:
                if (weight < 9.6)
                    nutritionState = 1;
                else if (weight < 10.3)
                    nutritionState = 2;
                else if (weight < 14.3)
                    nutritionState = 3;
                else if (weight < 15)
                    nutritionState = 4;
                else if (weight >= 15)
                    nutritionState = 5;
                break;
            case 24:
                if (weight < 9.8)
                    nutritionState = 1;
                else if (weight < 10.5)
                    nutritionState = 2;
                else if (weight < 14.5)
                    nutritionState = 3;
                else if (weight < 15.2)
                    nutritionState = 4;
                else if (weight >= 15.2)
                    nutritionState = 5;
                break;
            case 25:
                if (weight < 9.9)
                    nutritionState = 1;
                else if (weight < 10.6)
                    nutritionState = 2;
                else if (weight < 14.7)
                    nutritionState = 3;
                else if (weight < 15.4)
                    nutritionState = 4;
                else if (weight >= 15.4)
                    nutritionState = 5;
                break;
            case 26:
                if (weight < 10.1)
                    nutritionState = 1;
                else if (weight < 10.8)
                    nutritionState = 2;
                else if (weight < 14.9)
                    nutritionState = 3;
                else if (weight < 15.6)
                    nutritionState = 4;
                else if (weight >= 15.6)
                    nutritionState = 5;
                break;
            case 27:
                if (weight < 10.2)
                    nutritionState = 1;
                else if (weight < 10.9)
                    nutritionState = 2;
                else if (weight < 15.2)
                    nutritionState = 3;
                else if (weight < 15.9)
                    nutritionState = 4;
                else if (weight >= 15.9)
                    nutritionState = 5;
                break;
            case 28:
                if (weight < 10.3)
                    nutritionState = 1;
                else if (weight < 11)
                    nutritionState = 2;
                else if (weight < 15.5)
                    nutritionState = 3;
                else if (weight < 16.2)
                    nutritionState = 4;
                else if (weight >= 16.2)
                    nutritionState = 5;
                break;
            case 29:
                if (weight < 10.5)
                    nutritionState = 1;
                else if (weight < 11.2)
                    nutritionState = 2;
                else if (weight < 15.7)
                    nutritionState = 3;
                else if (weight < 16.4)
                    nutritionState = 4;
                else if (weight >= 16.4)
                    nutritionState = 5;
                break;
            case 30:
                if (weight < 10.6)
                    nutritionState = 1;
                else if (weight < 11.4)
                    nutritionState = 2;
                else if (weight < 15.9)
                    nutritionState = 3;
                else if (weight < 16.7)
                    nutritionState = 4;
                else if (weight >= 16.7)
                    nutritionState = 5;
                break;
            case 31:
                if (weight < 10.7)
                    nutritionState = 1;
                else if (weight < 11.5)
                    nutritionState = 2;
                else if (weight < 16.2)
                    nutritionState = 3;
                else if (weight < 17)
                    nutritionState = 4;
                else if (weight >= 17)
                    nutritionState = 5;
                break;
            case 32:
                if (weight < 10.9)
                    nutritionState = 1;
                else if (weight < 11.7)
                    nutritionState = 2;
                else if (weight < 16.4)
                    nutritionState = 3;
                else if (weight < 17.2)
                    nutritionState = 4;
                else if (weight >= 17.2)
                    nutritionState = 5;
                break;
            case 33:
                if (weight < 11)
                    nutritionState = 1;
                else if (weight < 11.8)
                    nutritionState = 2;
                else if (weight < 16.7)
                    nutritionState = 3;
                else if (weight < 17.5)
                    nutritionState = 4;
                else if (weight >= 17.5)
                    nutritionState = 5;
                break;
            case 34:
                if (weight < 11.1)
                    nutritionState = 1;
                else if (weight < 11.9)
                    nutritionState = 2;
                else if (weight < 16.9)
                    nutritionState = 3;
                else if (weight < 17.7)
                    nutritionState = 4;
                else if (weight >= 17.7)
                    nutritionState = 5;
                break;
            case 35:
                if (weight < 11.2)
                    nutritionState = 1;
                else if (weight < 12)
                    nutritionState = 2;
                else if (weight < 17.2)
                    nutritionState = 3;
                else if (weight < 18)
                    nutritionState = 4;
                else if (weight >= 18)
                    nutritionState = 5;
                break;
            case 36:
                if (weight < 11.3)
                    nutritionState = 1;
                else if (weight < 12.1)
                    nutritionState = 2;
                else if (weight < 17.3)
                    nutritionState = 3;
                else if (weight < 18.2)
                    nutritionState = 4;
                else if (weight >= 18.2)
                    nutritionState = 5;
                break;
            case 37:
                if (weight < 11.4)
                    nutritionState = 1;
                else if (weight < 12.2)
                    nutritionState = 2;
                else if (weight < 17.6)
                    nutritionState = 3;
                else if (weight < 18.5)
                    nutritionState = 4;
                else if (weight >= 18.5)
                    nutritionState = 5;
                break;
            case 38:
                if (weight < 11.6)
                    nutritionState = 1;
                else if (weight < 12.4)
                    nutritionState = 2;
                else if (weight < 17.8)
                    nutritionState = 3;
                else if (weight < 18.7)
                    nutritionState = 4;
                else if (weight >= 18.7)
                    nutritionState = 5;
                break;
            case 39:
                if (weight < 11.7)
                    nutritionState = 1;
                else if (weight < 12.5)
                    nutritionState = 2;
                else if (weight < 18.1)
                    nutritionState = 3;
                else if (weight < 19)
                    nutritionState = 4;
                else if (weight >= 19)
                    nutritionState = 5;
                break;
            case 40:
                if (weight < 11.8)
                    nutritionState = 1;
                else if (weight < 12.6)
                    nutritionState = 2;
                else if (weight < 18.2)
                    nutritionState = 3;
                else if (weight < 19.2)
                    nutritionState = 4;
                else if (weight >= 19.2)
                    nutritionState = 5;
                break;
            case 41:
                if (weight < 11.9)
                    nutritionState = 1;
                else if (weight < 12.7)
                    nutritionState = 2;
                else if (weight < 18.5)
                    nutritionState = 3;
                else if (weight < 19.5)
                    nutritionState = 4;
                else if (weight >= 19.5)
                    nutritionState = 5;
                break;
            case 42:
                if (weight < 12)
                    nutritionState = 1;
                else if (weight < 12.8)
                    nutritionState = 2;
                else if (weight < 18.7)
                    nutritionState = 3;
                else if (weight < 19.8)
                    nutritionState = 4;
                else if (weight >= 19.8)
                    nutritionState = 5;
                break;
            case 43:
                if (weight < 12.2)
                    nutritionState = 1;
                else if (weight < 13)
                    nutritionState = 2;
                else if (weight < 18.9)
                    nutritionState = 3;
                else if (weight < 20.1)
                    nutritionState = 4;
                else if (weight >= 20.1)
                    nutritionState = 5;
                break;
            case 44:
                if (weight < 12.3)
                    nutritionState = 1;
                else if (weight < 13.1)
                    nutritionState = 2;
                else if (weight < 19.1)
                    nutritionState = 3;
                else if (weight < 20.3)
                    nutritionState = 4;
                else if (weight >= 20.3)
                    nutritionState = 5;
                break;
            case 45:
                if (weight < 12.4)
                    nutritionState = 1;
                else if (weight < 13.2)
                    nutritionState = 2;
                else if (weight < 19.4)
                    nutritionState = 3;
                else if (weight < 20.6)
                    nutritionState = 4;
                else if (weight >= 20.6)
                    nutritionState = 5;
                break;
            case 46:
                if (weight < 12.5)
                    nutritionState = 1;
                else if (weight < 13.4)
                    nutritionState = 2;
                else if (weight < 19.6)
                    nutritionState = 3;
                else if (weight < 20.8)
                    nutritionState = 4;
                else if (weight >= 20.8)
                    nutritionState = 5;
                break;
            case 47:
                if (weight < 12.6)
                    nutritionState = 1;
                else if (weight < 13.5)
                    nutritionState = 2;
                else if (weight < 19.8)
                    nutritionState = 3;
                else if (weight < 21)
                    nutritionState = 4;
                else if (weight >= 21)
                    nutritionState = 5;
                break;
            // '48 - 59 ????? (5 ??)
            case 48:
                if (weight < 12.7)
                    nutritionState = 1;
                else if (weight < 13.6)
                    nutritionState = 2;
                else if (weight < 20)
                    nutritionState = 3;
                else if (weight < 21.3)
                    nutritionState = 4;
                else if (weight >= 21.3)
                    nutritionState = 5;
                break;
            case 49:
                if (weight < 12.8)
                    nutritionState = 1;
                else if (weight < 13.7)
                    nutritionState = 2;
                else if (weight < 20.3)
                    nutritionState = 3;
                else if (weight < 21.6)
                    nutritionState = 4;
                else if (weight >= 21.6)
                    nutritionState = 5;
                break;
            case 50:
                if (weight < 12.9)
                    nutritionState = 1;
                else if (weight < 13.8)
                    nutritionState = 2;
                else if (weight < 20.5)
                    nutritionState = 3;
                else if (weight < 21.8)
                    nutritionState = 4;
                else if (weight >= 21.8)
                    nutritionState = 5;
                break;
            case 51:
                if (weight < 13)
                    nutritionState = 1;
                else if (weight < 13.9)
                    nutritionState = 2;
                else if (weight < 20.7)
                    nutritionState = 3;
                else if (weight < 22)
                    nutritionState = 4;
                else if (weight >= 22)
                    nutritionState = 5;
                break;
            case 52:
                if (weight < 13.1)
                    nutritionState = 1;
                else if (weight < 14)
                    nutritionState = 2;
                else if (weight < 20.9)
                    nutritionState = 3;
                else if (weight < 22.2)
                    nutritionState = 4;
                else if (weight >= 22.2)
                    nutritionState = 5;
                break;
            case 53:
                if (weight < 13.2)
                    nutritionState = 1;
                else if (weight < 14.1)
                    nutritionState = 2;
                else if (weight < 21.1)
                    nutritionState = 3;
                else if (weight < 22.2)
                    nutritionState = 4;
                else if (weight >= 22.5)
                    nutritionState = 5;
                break;
            case 54:
                if (weight < 13.3)
                    nutritionState = 1;
                else if (weight < 14.2)
                    nutritionState = 2;
                else if (weight < 21.3)
                    nutritionState = 3;
                else if (weight < 22.7)
                    nutritionState = 4;
                else if (weight >= 22.7)
                    nutritionState = 5;
                break;
            case 55:
                if (weight < 13.5)
                    nutritionState = 1;
                else if (weight < 14.4)
                    nutritionState = 2;
                else if (weight < 21.6)
                    nutritionState = 3;
                else if (weight < 23)
                    nutritionState = 4;
                else if (weight >= 23)
                    nutritionState = 5;
                break;
            case 56:
                if (weight < 13.6)
                    nutritionState = 1;
                else if (weight < 14.5)
                    nutritionState = 2;
                else if (weight < 21.8)
                    nutritionState = 3;
                else if (weight < 23.3)
                    nutritionState = 4;
                else if (weight >= 23.3)
                    nutritionState = 5;
                break;
            case 57:
                if (weight < 13.8)
                    nutritionState = 1;
                else if (weight < 14.7)
                    nutritionState = 2;
                else if (weight < 22)
                    nutritionState = 3;
                else if (weight < 23.6)
                    nutritionState = 4;
                else if (weight >= 23.6)
                    nutritionState = 5;
                break;
            case 58:
                if (weight < 13.9)
                    nutritionState = 1;
                else if (weight < 14.8)
                    nutritionState = 2;
                else if (weight < 22.2)
                    nutritionState = 3;
                else if (weight < 23.8)
                    nutritionState = 4;
                else if (weight >= 23.8)
                    nutritionState = 5;
                break;
            case 59:
                if (weight < 14)
                    nutritionState = 1;
                else if (weight < 14.9)
                    nutritionState = 2;
                else if (weight < 22.5)
                    nutritionState = 3;
                else if (weight < 24.1)
                    nutritionState = 4;
                else if (weight >= 24.1)
                    nutritionState = 5;
                break;
            case 60:
                if (weight < 14.1)
                    nutritionState = 1;
                else if (weight < 15)
                    nutritionState = 2;
                else if (weight < 22.7)
                    nutritionState = 3;
                else if (weight < 24.3)
                    nutritionState = 4;
                else if (weight >= 24.3)
                    nutritionState = 5;
                break;
            case 61:
                if (weight < 14.2)
                    nutritionState = 1;
                else if (weight < 15.1)
                    nutritionState = 2;
                else if (weight < 23)
                    nutritionState = 3;
                else if (weight < 24.6)
                    nutritionState = 4;
                else if (weight >= 24.6)
                    nutritionState = 5;
                break;
            case 62:
                if (weight < 14.4)
                    nutritionState = 1;
                else if (weight < 15.3)
                    nutritionState = 2;
                else if (weight < 23.2)
                    nutritionState = 3;
                else if (weight < 24.8)
                    nutritionState = 4;
                else if (weight >= 24.8)
                    nutritionState = 5;
                break;
            case 63:
                if (weight < 14.5)
                    nutritionState = 1;
                else if (weight < 15.4)
                    nutritionState = 2;
                else if (weight < 23.4)
                    nutritionState = 3;
                else if (weight < 25.1)
                    nutritionState = 4;
                else if (weight >= 25.1)
                    nutritionState = 5;
                break;
            case 64:
                if (weight < 14.6)
                    nutritionState = 1;
                else if (weight < 15.5)
                    nutritionState = 2;
                else if (weight < 23.6)
                    nutritionState = 3;
                else if (weight < 25.3)
                    nutritionState = 4;
                else if (weight >= 25.3)
                    nutritionState = 5;
                break;
            case 65:
                if (weight < 14.7)
                    nutritionState = 1;
                else if (weight < 15.7)
                    nutritionState = 2;
                else if (weight < 23.9)
                    nutritionState = 3;
                else if (weight < 25.6)
                    nutritionState = 4;
                else if (weight >= 25.6)
                    nutritionState = 5;
                break;
            case 66:
                if (weight < 14.8)
                    nutritionState = 1;
                else if (weight < 15.8)
                    nutritionState = 2;
                else if (weight < 24.1)
                    nutritionState = 3;
                else if (weight < 25.8)
                    nutritionState = 4;
                else if (weight >= 25.8)
                    nutritionState = 5;
                break;
            case 67:
                if (weight < 14.9)
                    nutritionState = 1;
                else if (weight < 15.9)
                    nutritionState = 2;
                else if (weight < 24.4)
                    nutritionState = 3;
                else if (weight < 26.1)
                    nutritionState = 4;
                else if (weight >= 26.1)
                    nutritionState = 5;
                break;
            case 68:
                if (weight < 15.1)
                    nutritionState = 1;
                else if (weight < 16.1)
                    nutritionState = 2;
                else if (weight < 24.5)
                    nutritionState = 3;
                else if (weight < 26.3)
                    nutritionState = 4;
                else if (weight >= 26.3)
                    nutritionState = 5;
                break;
            case 69:
                if (weight < 15.2)
                    nutritionState = 1;
                else if (weight < 16.2)
                    nutritionState = 2;
                else if (weight < 24.7)
                    nutritionState = 3;
                else if (weight < 26.5)
                    nutritionState = 4;
                else if (weight >= 26.5)
                    nutritionState = 5;
                break;
            case 70:
                if (weight < 15.4)
                    nutritionState = 1;
                else if (weight < 16.4)
                    nutritionState = 2;
                else if (weight < 25)
                    nutritionState = 3;
                else if (weight < 26.8)
                    nutritionState = 4;
                else if (weight >= 26.8)
                    nutritionState = 5;
                break;
            case 71:
                if (weight < 15.5)
                    nutritionState = 1;
                else if (weight < 16.5)
                    nutritionState = 2;
                else if (weight < 25.3)
                    nutritionState = 3;
                else if (weight < 27.1)
                    nutritionState = 4;
                else if (weight >= 27.1)
                    nutritionState = 5;
                break;
            case 72:
                if (weight < 15.5)
                    nutritionState = 1;
                else if (weight < 16.6)
                    nutritionState = 2;
                else if (weight < 25.5)
                    nutritionState = 3;
                else if (weight < 27.3)
                    nutritionState = 4;
                else if (weight >= 27.3)
                    nutritionState = 5;
                break;
            case 73:
                if (weight < 15.6)
                    nutritionState = 1;
                else if (weight < 16.7)
                    nutritionState = 2;
                else if (weight < 25.7)
                    nutritionState = 3;
                else if (weight < 27.7)
                    nutritionState = 4;
                else if (weight >= 27.7)
                    nutritionState = 5;
                break;
            case 74:
                if (weight < 15.8)
                    nutritionState = 1;
                else if (weight < 16.9)
                    nutritionState = 2;
                else if (weight < 26)
                    nutritionState = 3;
                else if (weight < 28)
                    nutritionState = 4;
                else if (weight >= 28)
                    nutritionState = 5;
                break;
            case 75:
                if (weight < 15.9)
                    nutritionState = 1;
                else if (weight < 17)
                    nutritionState = 2;
                else if (weight < 26.3)
                    nutritionState = 3;
                else if (weight < 28.3)
                    nutritionState = 4;
                else if (weight >= 28.3)
                    nutritionState = 5;
                break;
            case 76:
                if (weight < 16)
                    nutritionState = 1;
                else if (weight < 17.1)
                    nutritionState = 2;
                else if (weight < 26.6)
                    nutritionState = 3;
                else if (weight < 28.6)
                    nutritionState = 4;
                else if (weight >= 28.6)
                    nutritionState = 5;
                break;
            case 77:
                if (weight < 16.1)
                    nutritionState = 1;
                else if (weight < 17.3)
                    nutritionState = 2;
                else if (weight < 26.8)
                    nutritionState = 3;
                else if (weight < 28.9)
                    nutritionState = 4;
                else if (weight >= 28.9)
                    nutritionState = 5;
                break;
            case 78:
                if (weight < 16.2)
                    nutritionState = 1;
                else if (weight < 17.4)
                    nutritionState = 2;
                else if (weight < 27.1)
                    nutritionState = 3;
                else if (weight < 29.2)
                    nutritionState = 4;
                else if (weight >= 29.2)
                    nutritionState = 5;
                break;
            case 79:
                if (weight < 16.4)
                    nutritionState = 1;
                else if (weight < 17.7)
                    nutritionState = 2;
                else if (weight < 27.5)
                    nutritionState = 3;
                else if (weight < 29.6)
                    nutritionState = 4;
                else if (weight >= 29.6)
                    nutritionState = 5;
                break;
            case 80:
                if (weight < 16.5)
                    nutritionState = 1;
                else if (weight < 17.7)
                    nutritionState = 2;
                else if (weight < 27.7)
                    nutritionState = 3;
                else if (weight < 29.9)
                    nutritionState = 4;
                else if (weight >= 29.9)
                    nutritionState = 5;
                break;
            case 81:
                if (weight < 16.7)
                    nutritionState = 1;
                else if (weight < 17.9)
                    nutritionState = 2;
                else if (weight < 28)
                    nutritionState = 3;
                else if (weight < 30.2)
                    nutritionState = 4;
                else if (weight >= 30.2)
                    nutritionState = 5;
                break;
            case 82:
                if (weight < 16.8)
                    nutritionState = 1;
                else if (weight < 18)
                    nutritionState = 2;
                else if (weight < 28.3)
                    nutritionState = 3;
                else if (weight < 30.5)
                    nutritionState = 4;
                else if (weight >= 30.5)
                    nutritionState = 5;
                break;
            case 83:
                if (weight < 16.9)
                    nutritionState = 1;
                else if (weight < 18.1)
                    nutritionState = 2;
                else if (weight < 28.6)
                    nutritionState = 3;
                else if (weight < 30.8)
                    nutritionState = 4;
                else if (weight >= 30.8)
                    nutritionState = 5;
                break;
            // '84 - 95 ????? (8 ??)
            case 84:
                if (weight < 17.1)
                    nutritionState = 1;
                else if (weight < 18.3)
                    nutritionState = 2;
                else if (weight < 28.9)
                    nutritionState = 3;
                else if (weight < 31.1)
                    nutritionState = 4;
                else if (weight >= 31.1)
                    nutritionState = 5;
                break;
            case 85:
                if (weight < 17.2)
                    nutritionState = 1;
                else if (weight < 18.4)
                    nutritionState = 2;
                else if (weight < 29.1)
                    nutritionState = 3;
                else if (weight < 31.5)
                    nutritionState = 4;
                else if (weight >= 31.5)
                    nutritionState = 5;
                break;
            case 86:
                if (weight < 17.3)
                    nutritionState = 1;
                else if (weight < 18.6)
                    nutritionState = 2;
                else if (weight < 29.4)
                    nutritionState = 3;
                else if (weight < 31.8)
                    nutritionState = 4;
                else if (weight >= 31.8)
                    nutritionState = 5;
                break;
            case 87:
                if (weight < 17.5)
                    nutritionState = 1;
                else if (weight < 18.8)
                    nutritionState = 2;
                else if (weight < 29.7)
                    nutritionState = 3;
                else if (weight < 32.1)
                    nutritionState = 4;
                else if (weight >= 32.1)
                    nutritionState = 5;
                break;
            case 88:
                if (weight < 17.6)
                    nutritionState = 1;
                else if (weight < 18.9)
                    nutritionState = 2;
                else if (weight < 30)
                    nutritionState = 3;
                else if (weight < 32.4)
                    nutritionState = 4;
                else if (weight >= 32.4)
                    nutritionState = 5;
                break;
            case 89:
                if (weight < 17.7)
                    nutritionState = 1;
                else if (weight < 19)
                    nutritionState = 2;
                else if (weight < 30.3)
                    nutritionState = 3;
                else if (weight < 32.7)
                    nutritionState = 4;
                else if (weight >= 32.7)
                    nutritionState = 5;
                break;
            case 90:
                if (weight < 17.8)
                    nutritionState = 1;
                else if (weight < 19.1)
                    nutritionState = 2;
                else if (weight < 30.5)
                    nutritionState = 3;
                else if (weight < 33)
                    nutritionState = 4;
                else if (weight >= 33)
                    nutritionState = 5;
                break;
            case 91:
                if (weight < 18)
                    nutritionState = 1;
                else if (weight < 19.3)
                    nutritionState = 2;
                else if (weight < 30.8)
                    nutritionState = 3;
                else if (weight < 33.3)
                    nutritionState = 4;
                else if (weight >= 33.3)
                    nutritionState = 5;
                break;
            case 92:
                if (weight < 18.1)
                    nutritionState = 1;
                else if (weight < 19.4)
                    nutritionState = 2;
                else if (weight < 31)
                    nutritionState = 3;
                else if (weight < 33.5)
                    nutritionState = 4;
                else if (weight >= 33.5)
                    nutritionState = 5;
                break;
            case 93:
                if (weight < 18.2)
                    nutritionState = 1;
                else if (weight < 19.5)
                    nutritionState = 2;
                else if (weight < 31.3)
                    nutritionState = 3;
                else if (weight < 33.8)
                    nutritionState = 4;
                else if (weight >= 33.8)
                    nutritionState = 5;
                break;
            case 94:
                if (weight < 18.3)
                    nutritionState = 1;
                else if (weight < 19.6)
                    nutritionState = 2;
                else if (weight < 31.6)
                    nutritionState = 3;
                else if (weight < 34.2)
                    nutritionState = 4;
                else if (weight >= 34.2)
                    nutritionState = 5;
                break;
            case 95:
                if (weight < 18.4)
                    nutritionState = 1;
                else if (weight < 19.8)
                    nutritionState = 2;
                else if (weight < 31.9)
                    nutritionState = 3;
                else if (weight < 34.5)
                    nutritionState = 4;
                else if (weight >= 34.5)
                    nutritionState = 5;
                break;
            // '96 - 107 ????? (9 ??)
            case 96:
                if (weight < 18.6)
                    nutritionState = 1;
                else if (weight < 20)
                    nutritionState = 2;
                else if (weight < 32.3)
                    nutritionState = 3;
                else if (weight < 35)
                    nutritionState = 4;
                else if (weight >= 35)
                    nutritionState = 5;
                break;
            case 97:
                if (weight < 18.7)
                    nutritionState = 1;
                else if (weight < 20.1)
                    nutritionState = 2;
                else if (weight < 32.7)
                    nutritionState = 3;
                else if (weight < 35.5)
                    nutritionState = 4;
                else if (weight >= 35.5)
                    nutritionState = 5;
                break;
            case 98:
                if (weight < 18.8)
                    nutritionState = 1;
                else if (weight < 20.2)
                    nutritionState = 2;
                else if (weight < 33.1)
                    nutritionState = 3;
                else if (weight < 35.9)
                    nutritionState = 4;
                else if (weight >= 35.9)
                    nutritionState = 5;
                break;
            case 99:
                if (weight < 18.9)
                    nutritionState = 1;
                else if (weight < 20.3)
                    nutritionState = 2;
                else if (weight < 33.4)
                    nutritionState = 3;
                else if (weight < 36.3)
                    nutritionState = 4;
                else if (weight >= 36.3)
                    nutritionState = 5;
                break;
            case 100:
                if (weight < 18.9)
                    nutritionState = 1;
                else if (weight < 20.4)
                    nutritionState = 2;
                else if (weight < 33.8)
                    nutritionState = 3;
                else if (weight < 36.7)
                    nutritionState = 4;
                else if (weight >= 36.7)
                    nutritionState = 5;
                break;
            case 101:
                if (weight < 19)
                    nutritionState = 1;
                else if (weight < 20.6)
                    nutritionState = 2;
                else if (weight < 34.1)
                    nutritionState = 3;
                else if (weight < 37.1)
                    nutritionState = 4;
                else if (weight >= 37.1)
                    nutritionState = 5;
                break;
            case 102:
                if (weight < 19.1)
                    nutritionState = 1;
                else if (weight < 20.7)
                    nutritionState = 2;
                else if (weight < 34.5)
                    nutritionState = 3;
                else if (weight < 37.5)
                    nutritionState = 4;
                else if (weight >= 37.5)
                    nutritionState = 5;
                break;
            case 103:
                if (weight < 19.2)
                    nutritionState = 1;
                else if (weight < 20.8)
                    nutritionState = 2;
                else if (weight < 34.9)
                    nutritionState = 3;
                else if (weight < 37.9)
                    nutritionState = 4;
                else if (weight >= 37.9)
                    nutritionState = 5;
                break;
            case 104:
                if (weight < 19.3)
                    nutritionState = 1;
                else if (weight < 20.9)
                    nutritionState = 2;
                else if (weight < 35.2)
                    nutritionState = 3;
                else if (weight < 38.3)
                    nutritionState = 4;
                else if (weight >= 38.3)
                    nutritionState = 5;
                break;
            case 105:
                if (weight < 19.4)
                    nutritionState = 1;
                else if (weight < 21)
                    nutritionState = 2;
                else if (weight < 35.6)
                    nutritionState = 3;
                else if (weight < 38.8)
                    nutritionState = 4;
                else if (weight >= 38.8)
                    nutritionState = 5;
                break;
            case 106:
                if (weight < 19.5)
                    nutritionState = 1;
                else if (weight < 21.2)
                    nutritionState = 2;
                else if (weight < 36)
                    nutritionState = 3;
                else if (weight < 39.2)
                    nutritionState = 4;
                else if (weight >= 39.2)
                    nutritionState = 5;
                break;
            case 107:
                if (weight < 19.7)
                    nutritionState = 1;
                else if (weight < 21.4)
                    nutritionState = 2;
                else if (weight < 36.3)
                    nutritionState = 3;
                else if (weight < 39.6)
                    nutritionState = 4;
                else if (weight >= 39.6)
                    nutritionState = 5;
                break;
            case 108:
                if (weight < 19.8)
                    nutritionState = 1;
                else if (weight < 21.5)
                    nutritionState = 2;
                else if (weight < 36.7)
                    nutritionState = 3;
                else if (weight < 40)
                    nutritionState = 4;
                else if (weight >= 40)
                    nutritionState = 5;
                break;
            case 109:
                if (weight < 19.9)
                    nutritionState = 1;
                else if (weight < 21.6)
                    nutritionState = 2;
                else if (weight < 37)
                    nutritionState = 3;
                else if (weight < 40.4)
                    nutritionState = 4;
                else if (weight >= 40.4)
                    nutritionState = 5;
                break;
            case 110:
                if (weight < 20)
                    nutritionState = 1;
                else if (weight < 21.8)
                    nutritionState = 2;
                else if (weight < 37.4)
                    nutritionState = 3;
                else if (weight < 40.8)
                    nutritionState = 4;
                else if (weight >= 40.8)
                    nutritionState = 5;
                break;
            case 111:
                if (weight < 20.2)
                    nutritionState = 1;
                else if (weight < 22)
                    nutritionState = 2;
                else if (weight < 37.7)
                    nutritionState = 3;
                else if (weight < 41.1)
                    nutritionState = 4;
                else if (weight >= 41.1)
                    nutritionState = 5;
                break;
            case 112:
                if (weight < 20.3)
                    nutritionState = 1;
                else if (weight < 22.1)
                    nutritionState = 2;
                else if (weight < 38.1)
                    nutritionState = 3;
                else if (weight < 41.5)
                    nutritionState = 4;
                else if (weight >= 41.5)
                    nutritionState = 5;
                break;
            case 113:
                if (weight < 20.4)
                    nutritionState = 1;
                else if (weight < 22.3)
                    nutritionState = 2;
                else if (weight < 38.5)
                    nutritionState = 3;
                else if (weight < 41.9)
                    nutritionState = 4;
                else if (weight >= 41.9)
                    nutritionState = 5;
                break;
            case 114:
                if (weight < 20.6)
                    nutritionState = 1;
                else if (weight < 22.5)
                    nutritionState = 2;
                else if (weight < 38.8)
                    nutritionState = 3;
                else if (weight < 42.3)
                    nutritionState = 4;
                else if (weight >= 42.3)
                    nutritionState = 5;
                break;
            case 115:
                if (weight < 20.7)
                    nutritionState = 1;
                else if (weight < 22.7)
                    nutritionState = 2;
                else if (weight < 39.2)
                    nutritionState = 3;
                else if (weight < 42.7)
                    nutritionState = 4;
                else if (weight >= 42.7)
                    nutritionState = 5;
                break;
            case 116:
                if (weight < 20.9)
                    nutritionState = 1;
                else if (weight < 22.9)
                    nutritionState = 2;
                else if (weight < 39.5)
                    nutritionState = 3;
                else if (weight < 43)
                    nutritionState = 4;
                else if (weight >= 43)
                    nutritionState = 5;
                break;
            case 117:
                if (weight < 21)
                    nutritionState = 1;
                else if (weight < 23)
                    nutritionState = 2;
                else if (weight < 39.9)
                    nutritionState = 3;
                else if (weight < 43.5)
                    nutritionState = 4;
                else if (weight >= 43.5)
                    nutritionState = 5;
                break;

            case 118:

                if (weight < 21.2)
                    nutritionState = 1;
                else if (weight < 23.2)
                    nutritionState = 2;
                else if (weight < 40.2)
                    nutritionState = 3;
                else if (weight < 43.9)
                    nutritionState = 4;
                else if (weight >= 43.9)
                    nutritionState = 5;
                break;

            case 119:
                if (weight < 21.4)
                    nutritionState = 1;
                else if (weight < 23.4)
                    nutritionState = 2;
                else if (weight < 40.6)
                    nutritionState = 3;
                else if (weight < 44.3)
                    nutritionState = 4;
                else if (weight >= 44.3)
                    nutritionState = 5;
                break;
            case 120:
                if (weight < 21.5)
                    nutritionState = 1;
                else if (weight < 23.6)
                    nutritionState = 2;
                else if (weight < 40.9)
                    nutritionState = 3;
                else if (weight < 44.6)
                    nutritionState = 4;
                else if (weight >= 44.6)
                    nutritionState = 5;
                break;
            case 121:
                if (weight < 21.7)
                    nutritionState = 1;
                else if (weight < 23.8)
                    nutritionState = 2;
                else if (weight < 41.3)
                    nutritionState = 3;
                else if (weight < 45)
                    nutritionState = 4;
                else if (weight >= 45)
                    nutritionState = 5;
                break;
            case 122:
                if (weight < 21.8)
                    nutritionState = 1;
                else if (weight < 23.9)
                    nutritionState = 2;
                else if (weight < 41.6)
                    nutritionState = 3;
                else if (weight < 45.4)
                    nutritionState = 4;
                else if (weight >= 45.4)
                    nutritionState = 5;
                break;
            case 123:
                if (weight < 22)
                    nutritionState = 1;
                else if (weight < 24.1)
                    nutritionState = 2;
                else if (weight < 41.9)
                    nutritionState = 3;
                else if (weight < 45.7)
                    nutritionState = 4;
                else if (weight >= 45.7)
                    nutritionState = 5;
                break;
            case 124:
                if (weight < 22.2)
                    nutritionState = 1;
                else if (weight < 24.3)
                    nutritionState = 2;
                else if (weight < 42.3)
                    nutritionState = 3;
                else if (weight < 46.1)
                    nutritionState = 4;
                else if (weight >= 46.1)
                    nutritionState = 5;
                break;
            case 125:
                if (weight < 22.3)
                    nutritionState = 1;
                else if (weight < 24.4)
                    nutritionState = 2;
                else if (weight < 42.6)
                    nutritionState = 3;
                else if (weight < 46.4)
                    nutritionState = 4;
                else if (weight >= 46.4)
                    nutritionState = 5;
                break;
            case 126:
                if (weight < 22.5)
                    nutritionState = 1;
                else if (weight < 24.7)
                    nutritionState = 2;
                else if (weight < 42.9)
                    nutritionState = 3;
                else if (weight < 46.7)
                    nutritionState = 4;
                else if (weight >= 46.7)
                    nutritionState = 5;
                break;
            case 127:
                if (weight < 22.6)
                    nutritionState = 1;
                else if (weight < 24.8)
                    nutritionState = 2;
                else if (weight < 43.2)
                    nutritionState = 3;
                else if (weight < 47.1)
                    nutritionState = 4;
                else if (weight >= 47.1)
                    nutritionState = 5;
                break;
            case 128:
                if (weight < 22.8)
                    nutritionState = 1;
                else if (weight < 25)
                    nutritionState = 2;
                else if (weight < 43.6)
                    nutritionState = 3;
                else if (weight < 47.5)
                    nutritionState = 4;
                else if (weight >= 47.5)
                    nutritionState = 5;
                break;
            case 129:
                if (weight < 22.9)
                    nutritionState = 1;
                else if (weight < 25.1)
                    nutritionState = 2;
                else if (weight < 44)
                    nutritionState = 3;
                else if (weight < 48.1)
                    nutritionState = 4;
                else if (weight >= 48.1)
                    nutritionState = 5;
                break;
            case 130:
                if (weight < 22.9)
                    nutritionState = 1;
                else if (weight < 25.3)
                    nutritionState = 2;
                else if (weight < 44.4)
                    nutritionState = 3;
                else if (weight < 48.5)
                    nutritionState = 4;
                else if (weight >= 48.5)
                    nutritionState = 5;
                break;
            case 131:
                if (weight < 23.1)
                    nutritionState = 1;
                else if (weight < 25.5)
                    nutritionState = 2;
                else if (weight < 44.9)
                    nutritionState = 3;
                else if (weight < 49)
                    nutritionState = 4;
                else if (weight >= 49)
                    nutritionState = 5;
                break;
            case 132:
                if (weight < 23.2)
                    nutritionState = 1;
                else if (weight < 25.6)
                    nutritionState = 2;
                else if (weight < 45.3)
                    nutritionState = 3;
                else if (weight < 49.5)
                    nutritionState = 4;
                else if (weight >= 49.5)
                    nutritionState = 5;
                break;
            case 133:
                if (weight < 23.4)
                    nutritionState = 1;
                else if (weight < 25.8)
                    nutritionState = 2;
                else if (weight < 45.7)
                    nutritionState = 3;
                else if (weight < 49.9)
                    nutritionState = 4;
                else if (weight >= 49.9)
                    nutritionState = 5;
                break;
            case 134:
                if (weight < 23.5)
                    nutritionState = 1;
                else if (weight < 26)
                    nutritionState = 2;
                else if (weight < 46.2)
                    nutritionState = 3;
                else if (weight < 50.4)
                    nutritionState = 4;
                else if (weight >= 50.4)
                    nutritionState = 5;
                break;
            case 135:
                if (weight < 23.7)
                    nutritionState = 1;
                else if (weight < 26.2)
                    nutritionState = 2;
                else if (weight < 46.5)
                    nutritionState = 3;
                else if (weight < 50.8)
                    nutritionState = 4;
                else if (weight >= 50.8)
                    nutritionState = 5;
                break;
            case 136:
                if (weight < 23.8)
                    nutritionState = 1;
                else if (weight < 26.3)
                    nutritionState = 2;
                else if (weight < 46.9)
                    nutritionState = 3;
                else if (weight < 51.2)
                    nutritionState = 4;
                else if (weight >= 51.2)
                    nutritionState = 5;
                break;
            case 137:
                if (weight < 24)
                    nutritionState = 1;
                else if (weight < 26.5)
                    nutritionState = 2;
                else if (weight < 47.3)
                    nutritionState = 3;
                else if (weight < 51.6)
                    nutritionState = 4;
                else if (weight >= 51.6)
                    nutritionState = 5;
                break;
            case 138:
                if (weight < 24.2)
                    nutritionState = 1;
                else if (weight < 26.8)
                    nutritionState = 2;
                else if (weight < 47.7)
                    nutritionState = 3;
                else if (weight < 52)
                    nutritionState = 4;
                else if (weight >= 52)
                    nutritionState = 5;
                break;
            case 139:
                if (weight < 24.4)
                    nutritionState = 1;
                else if (weight < 27)
                    nutritionState = 2;
                else if (weight < 48.1)
                    nutritionState = 3;
                else if (weight < 52.6)
                    nutritionState = 4;
                else if (weight >= 52.6)
                    nutritionState = 5;
                break;
            case 140:
                if (weight < 24.6)
                    nutritionState = 1;
                else if (weight < 27.2)
                    nutritionState = 2;
                else if (weight < 48.5)
                    nutritionState = 3;
                else if (weight < 53)
                    nutritionState = 4;
                else if (weight >= 53)
                    nutritionState = 5;
                break;
            case 141:
                if (weight < 24.7)
                    nutritionState = 1;
                else if (weight < 27.4)
                    nutritionState = 2;
                else if (weight < 48.9)
                    nutritionState = 3;
                else if (weight < 53.4)
                    nutritionState = 4;
                else if (weight >= 53.4)
                    nutritionState = 5;
                break;
            case 142:
                if (weight < 24.7)
                    nutritionState = 1;
                else if (weight < 27.5)
                    nutritionState = 2;
                else if (weight < 49.3)
                    nutritionState = 3;
                else if (weight < 53.8)
                    nutritionState = 4;
                else if (weight >= 53.8)
                    nutritionState = 5;
                break;
            case 143:
                if (weight < 25.1)
                    nutritionState = 1;
                else if (weight < 27.9)
                    nutritionState = 2;
                else if (weight < 49.7)
                    nutritionState = 3;
                else if (weight < 54.2)
                    nutritionState = 4;
                else if (weight >= 54.2)
                    nutritionState = 5;
                break;
            case 144:
                if (weight < 25.3)
                    nutritionState = 1;
                else if (weight < 28.1)
                    nutritionState = 2;
                else if (weight < 50.1)
                    nutritionState = 3;
                else if (weight < 54.6)
                    nutritionState = 4;
                else if (weight >= 54.6)
                    nutritionState = 5;
                break;
            case 145:
                if (weight < 25.5)
                    nutritionState = 1;
                else if (weight < 28.4)
                    nutritionState = 2;
                else if (weight < 50.5)
                    nutritionState = 3;
                else if (weight < 55)
                    nutritionState = 4;
                else if (weight >= 55)
                    nutritionState = 5;
                break;
            case 146:
                if (weight < 25.7)
                    nutritionState = 1;
                else if (weight < 28.6)
                    nutritionState = 2;
                else if (weight < 50.9)
                    nutritionState = 3;
                else if (weight < 55.4)
                    nutritionState = 4;
                else if (weight >= 55.4)
                    nutritionState = 5;
                break;
            case 147:
                if (weight < 25.9)
                    nutritionState = 1;
                else if (weight < 28.8)
                    nutritionState = 2;
                else if (weight < 51.3)
                    nutritionState = 3;
                else if (weight < 55.8)
                    nutritionState = 4;
                else if (weight >= 55.8)
                    nutritionState = 5;
                break;
            case 148:
                if (weight < 26.1)
                    nutritionState = 1;
                else if (weight < 29.1)
                    nutritionState = 2;
                else if (weight < 51.6)
                    nutritionState = 3;
                else if (weight < 56.1)
                    nutritionState = 4;
                else if (weight >= 56.1)
                    nutritionState = 5;
                break;
            case 149:
                if (weight < 26.4)
                    nutritionState = 1;
                else if (weight < 29.4)
                    nutritionState = 2;
                else if (weight < 52)
                    nutritionState = 3;
                else if (weight < 56.5)
                    nutritionState = 4;
                else if (weight >= 56.5)
                    nutritionState = 5;
                break;
            case 150:
                if (weight < 26.6)
                    nutritionState = 1;
                else if (weight < 29.7)
                    nutritionState = 2;
                else if (weight < 52.4)
                    nutritionState = 3;
                else if (weight < 56.9)
                    nutritionState = 4;
                else if (weight >= 56.9)
                    nutritionState = 5;
                break;
            case 151:
                if (weight < 26.9)
                    nutritionState = 1;
                else if (weight < 30)
                    nutritionState = 2;
                else if (weight < 52.8)
                    nutritionState = 3;
                else if (weight < 57.3)
                    nutritionState = 4;
                else if (weight >= 57.3)
                    nutritionState = 5;
                break;
            case 152:
                if (weight < 27)
                    nutritionState = 1;
                else if (weight < 30.2)
                    nutritionState = 2;
                else if (weight < 53.1)
                    nutritionState = 3;
                else if (weight < 57.6)
                    nutritionState = 4;
                else if (weight >= 57.6)
                    nutritionState = 5;
                break;
            case 153:
                if (weight < 27.3)
                    nutritionState = 1;
                else if (weight < 30.5)
                    nutritionState = 2;
                else if (weight < 53.5)
                    nutritionState = 3;
                else if (weight < 58)
                    nutritionState = 4;
                else if (weight >= 58)
                    nutritionState = 5;
                break;
            case 154:
                if (weight < 27.6)
                    nutritionState = 1;
                else if (weight < 30.9)
                    nutritionState = 2;
                else if (weight < 54)
                    nutritionState = 3;
                else if (weight < 58.3)
                    nutritionState = 4;
                else if (weight >= 58.3)
                    nutritionState = 5;
                break;
            case 155:
                if (weight < 27.9)
                    nutritionState = 1;
                else if (weight < 31.2)
                    nutritionState = 2;
                else if (weight < 54.3)
                    nutritionState = 3;
                else if (weight < 58.6)
                    nutritionState = 4;
                else if (weight >= 58.6)
                    nutritionState = 5;
                break;
            case 156:
                if (weight < 28.2)
                    nutritionState = 1;
                else if (weight < 31.6)
                    nutritionState = 2;
                else if (weight < 54.7)
                    nutritionState = 3;
                else if (weight < 59)
                    nutritionState = 4;
                else if (weight >= 59)
                    nutritionState = 5;
                break;
            case 157:
                if (weight < 28.5)
                    nutritionState = 1;
                else if (weight < 31.9)
                    nutritionState = 2;
                else if (weight < 55)
                    nutritionState = 3;
                else if (weight < 59.3)
                    nutritionState = 4;
                else if (weight >= 59.3)
                    nutritionState = 5;
                break;
            case 158:
                if (weight < 28.8)
                    nutritionState = 1;
                else if (weight < 32.2)
                    nutritionState = 2;
                else if (weight < 55.4)
                    nutritionState = 3;
                else if (weight < 59.6)
                    nutritionState = 4;
                else if (weight >= 59.6)
                    nutritionState = 5;
                break;
            case 159:
                if (weight < 29.1)
                    nutritionState = 1;
                else if (weight < 32.5)
                    nutritionState = 2;
                else if (weight < 55.8)
                    nutritionState = 3;
                else if (weight < 60)
                    nutritionState = 4;
                else if (weight >= 60)
                    nutritionState = 5;
                break;
            case 160:
                if (weight < 29.4)
                    nutritionState = 1;
                else if (weight < 32.9)
                    nutritionState = 2;
                else if (weight < 56.1)
                    nutritionState = 3;
                else if (weight < 60.3)
                    nutritionState = 4;
                else if (weight >= 60.3)
                    nutritionState = 5;
                break;
            case 161:
                if (weight < 29.7)
                    nutritionState = 1;
                else if (weight < 33.2)
                    nutritionState = 2;
                else if (weight < 56.4)
                    nutritionState = 3;
                else if (weight < 60.6)
                    nutritionState = 4;
                else if (weight >= 60.6)
                    nutritionState = 5;
                break;
            case 162:
                if (weight < 30)
                    nutritionState = 1;
                else if (weight < 33.5)
                    nutritionState = 2;
                else if (weight < 56.7)
                    nutritionState = 3;
                else if (weight < 60.9)
                    nutritionState = 4;
                else if (weight >= 60.9)
                    nutritionState = 5;
                break;
            case 163:
                if (weight < 30.2)
                    nutritionState = 1;
                else if (weight < 33.9)
                    nutritionState = 2;
                else if (weight < 57.1)
                    nutritionState = 3;
                else if (weight < 61.2)
                    nutritionState = 4;
                else if (weight >= 61.2)
                    nutritionState = 5;
                break;
            case 164:
                if (weight < 30.5)
                    nutritionState = 1;
                else if (weight < 34.2)
                    nutritionState = 2;
                else if (weight < 57.5)
                    nutritionState = 3;
                else if (weight < 61.6)
                    nutritionState = 4;
                else if (weight >= 61.6)
                    nutritionState = 5;
                break;
            case 165:
                if (weight < 30.8)
                    nutritionState = 1;
                else if (weight < 34.5)
                    nutritionState = 2;
                else if (weight < 57.7)
                    nutritionState = 3;
                else if (weight < 61.8)
                    nutritionState = 4;
                else if (weight >= 61.8)
                    nutritionState = 5;
                break;
            case 166:
                if (weight < 31.1)
                    nutritionState = 1;
                else if (weight < 34.8)
                    nutritionState = 2;
                else if (weight < 58)
                    nutritionState = 3;
                else if (weight < 62.1)
                    nutritionState = 4;
                else if (weight >= 62.1)
                    nutritionState = 5;
                break;
            case 167:
                if (weight < 31.5)
                    nutritionState = 1;
                else if (weight < 35.2)
                    nutritionState = 2;
                else if (weight < 58.4)
                    nutritionState = 3;
                else if (weight < 62.4)
                    nutritionState = 4;
                else if (weight >= 62.4)
                    nutritionState = 5;
                break;
            case 168:
                if (weight < 31.9)
                    nutritionState = 1;
                else if (weight < 35.6)
                    nutritionState = 2;
                else if (weight < 58.8)
                    nutritionState = 3;
                else if (weight < 62.8)
                    nutritionState = 4;
                else if (weight >= 62.8)
                    nutritionState = 5;
                break;
            case 169:
                if (weight < 32.3)
                    nutritionState = 1;
                else if (weight < 36)
                    nutritionState = 2;
                else if (weight < 59.1)
                    nutritionState = 3;
                else if (weight < 63)
                    nutritionState = 4;
                else if (weight >= 63)
                    nutritionState = 5;
                break;
            case 170:
                if (weight < 32.7)
                    nutritionState = 1;
                else if (weight < 36.4)
                    nutritionState = 2;
                else if (weight < 59.3)
                    nutritionState = 3;
                else if (weight < 63.2)
                    nutritionState = 4;
                else if (weight >= 63.2)
                    nutritionState = 5;
                break;
            case 171:
                if (weight < 33.1)
                    nutritionState = 1;
                else if (weight < 36.8)
                    nutritionState = 2;
                else if (weight < 59.6)
                    nutritionState = 3;
                else if (weight < 63.5)
                    nutritionState = 4;
                else if (weight >= 63.5)
                    nutritionState = 5;
                break;
            case 172:
                if (weight < 33.5)
                    nutritionState = 1;
                else if (weight < 37.2)
                    nutritionState = 2;
                else if (weight < 59.9)
                    nutritionState = 3;
                else if (weight < 63.7)
                    nutritionState = 4;
                else if (weight >= 63.7)
                    nutritionState = 5;
                break;
            case 173:
                if (weight < 33.9)
                    nutritionState = 1;
                else if (weight < 37.6)
                    nutritionState = 2;
                else if (weight < 60.2)
                    nutritionState = 3;
                else if (weight < 64)
                    nutritionState = 4;
                else if (weight >= 64)
                    nutritionState = 5;
                break;
            case 174:
                if (weight < 34.3)
                    nutritionState = 1;
                else if (weight < 38)
                    nutritionState = 2;
                else if (weight < 60.5)
                    nutritionState = 3;
                else if (weight < 64.3)
                    nutritionState = 4;
                else if (weight >= 64.3)
                    nutritionState = 5;
                break;
            case 175:
                if (weight < 34.6)
                    nutritionState = 1;
                else if (weight < 38.3)
                    nutritionState = 2;
                else if (weight < 60.7)
                    nutritionState = 3;
                else if (weight < 64.5)
                    nutritionState = 4;
                else if (weight >= 64.5)
                    nutritionState = 5;
                break;
            case 176:
                if (weight < 35)
                    nutritionState = 1;
                else if (weight < 38.7)
                    nutritionState = 2;
                else if (weight < 61)
                    nutritionState = 3;
                else if (weight < 64.8)
                    nutritionState = 4;
                else if (weight >= 64.8)
                    nutritionState = 5;
                break;
            case 177:
                if (weight < 35.4)
                    nutritionState = 1;
                else if (weight < 39)
                    nutritionState = 2;
                else if (weight < 61.2)
                    nutritionState = 3;
                else if (weight < 65)
                    nutritionState = 4;
                else if (weight >= 65)
                    nutritionState = 5;
                break;
            case 178:
                if (weight < 35.7)
                    nutritionState = 1;
                else if (weight < 39.3)
                    nutritionState = 2;
                else if (weight < 61.4)
                    nutritionState = 3;
                else if (weight < 65.2)
                    nutritionState = 4;
                else if (weight >= 65.2)
                    nutritionState = 5;
                break;
            case 179:
                if (weight < 36.2)
                    nutritionState = 1;
                else if (weight < 39.7)
                    nutritionState = 2;
                else if (weight < 61.7)
                    nutritionState = 3;
                else if (weight < 65.4)
                    nutritionState = 4;
                else if (weight >= 65.4)
                    nutritionState = 5;
                break;
            case 180:
                if (weight < 36.6)
                    nutritionState = 1;
                else if (weight < 40.1)
                    nutritionState = 2;
                else if (weight < 62)
                    nutritionState = 3;
                else if (weight < 65.7)
                    nutritionState = 4;
                else if (weight >= 65.7)
                    nutritionState = 5;
                break;
            case 181:
                if (weight < 37)
                    nutritionState = 1;
                else if (weight < 40.5)
                    nutritionState = 2;
                else if (weight < 62.2)
                    nutritionState = 3;
                else if (weight < 65.9)
                    nutritionState = 4;
                else if (weight >= 65.9)
                    nutritionState = 5;
                break;
            case 182:
                if (weight < 37.4)
                    nutritionState = 1;
                else if (weight < 40.8)
                    nutritionState = 2;
                else if (weight < 62.4)
                    nutritionState = 3;
                else if (weight < 66.1)
                    nutritionState = 4;
                else if (weight >= 66.1)
                    nutritionState = 5;
                break;
            case 183:
                if (weight < 37.7)
                    nutritionState = 1;
                else if (weight < 41.1)
                    nutritionState = 2;
                else if (weight < 62.6)
                    nutritionState = 3;
                else if (weight < 66.3)
                    nutritionState = 4;
                else if (weight >= 66.3)
                    nutritionState = 5;
                break;
            case 184:
                if (weight < 38.1)
                    nutritionState = 1;
                else if (weight < 41.5)
                    nutritionState = 2;
                else if (weight < 62.7)
                    nutritionState = 3;
                else if (weight < 66.4)
                    nutritionState = 4;
                else if (weight >= 66.4)
                    nutritionState = 5;
                break;
            case 185:
                if (weight < 38.4)
                    nutritionState = 1;
                else if (weight < 41.8)
                    nutritionState = 2;
                else if (weight < 63)
                    nutritionState = 3;
                else if (weight < 66.6)
                    nutritionState = 4;
                else if (weight >= 66.6)
                    nutritionState = 5;
                break;
            case 186:
                if (weight < 38.8)
                    nutritionState = 1;
                else if (weight < 42.2)
                    nutritionState = 2;
                else if (weight < 63.2)
                    nutritionState = 3;
                else if (weight < 66.8)
                    nutritionState = 4;
                else if (weight >= 66.8)
                    nutritionState = 5;
                break;
            case 187:
                if (weight < 39.1)
                    nutritionState = 1;
                else if (weight < 42.5)
                    nutritionState = 2;
                else if (weight < 63.4)
                    nutritionState = 3;
                else if (weight < 66.9)
                    nutritionState = 4;
                else if (weight >= 66.9)
                    nutritionState = 5;
                break;
            case 188:
                if (weight < 39.4)
                    nutritionState = 1;
                else if (weight < 42.8)
                    nutritionState = 2;
                else if (weight < 63.5)
                    nutritionState = 3;
                else if (weight < 67)
                    nutritionState = 4;
                else if (weight >= 67)
                    nutritionState = 5;
                break;
            case 189:
                if (weight < 39.7)
                    nutritionState = 1;
                else if (weight < 43.1)
                    nutritionState = 2;
                else if (weight < 63.7)
                    nutritionState = 3;
                else if (weight < 67.2)
                    nutritionState = 4;
                else if (weight >= 67.2)
                    nutritionState = 5;
                break;
            case 190:
                if (weight < 40)
                    nutritionState = 1;
                else if (weight < 43.3)
                    nutritionState = 2;
                else if (weight < 63.9)
                    nutritionState = 3;
                else if (weight < 67.4)
                    nutritionState = 4;
                else if (weight >= 67.4)
                    nutritionState = 5;
                break;
            case 191:
                if (weight < 40.3)
                    nutritionState = 1;
                else if (weight < 43.6)
                    nutritionState = 2;
                else if (weight < 64)
                    nutritionState = 3;
                else if (weight < 67.5)
                    nutritionState = 4;
                else if (weight >= 67.5)
                    nutritionState = 5;
                break;
            case 192:
                if (weight < 40.5)
                    nutritionState = 1;
                else if (weight < 43.8)
                    nutritionState = 2;
                else if (weight < 64.3)
                    nutritionState = 3;
                else if (weight < 67.7)
                    nutritionState = 4;
                else if (weight >= 67.7)
                    nutritionState = 5;
                break;
            case 193:
                if (weight < 40.8)
                    nutritionState = 1;
                else if (weight < 44.1)
                    nutritionState = 2;
                else if (weight < 64.4)
                    nutritionState = 3;
                else if (weight < 67.8)
                    nutritionState = 4;
                else if (weight >= 67.8)
                    nutritionState = 5;
                break;
            case 194:
                if (weight < 41)
                    nutritionState = 1;
                else if (weight < 44.3)
                    nutritionState = 2;
                else if (weight < 64.6)
                    nutritionState = 3;
                else if (weight < 68)
                    nutritionState = 4;
                else if (weight >= 68)
                    nutritionState = 5;
                break;
            case 195:
                if (weight < 41.2)
                    nutritionState = 1;
                else if (weight < 44.5)
                    nutritionState = 2;
                else if (weight < 64.7)
                    nutritionState = 3;
                else if (weight < 68.1)
                    nutritionState = 4;
                else if (weight >= 68.1)
                    nutritionState = 5;
                break;
            case 196:
                if (weight < 41.4)
                    nutritionState = 1;
                else if (weight < 44.7)
                    nutritionState = 2;
                else if (weight < 64.8)
                    nutritionState = 3;
                else if (weight < 68.2)
                    nutritionState = 4;
                else if (weight >= 68.2)
                    nutritionState = 5;
                break;
            case 197:
                if (weight < 41.6)
                    nutritionState = 1;
                else if (weight < 44.9)
                    nutritionState = 2;
                else if (weight < 65)
                    nutritionState = 3;
                else if (weight < 68.4)
                    nutritionState = 4;
                else if (weight >= 68.4)
                    nutritionState = 5;
                break;
            case 198:
                if (weight < 41.8)
                    nutritionState = 1;
                else if (weight < 45.1)
                    nutritionState = 2;
                else if (weight < 65.1)
                    nutritionState = 3;
                else if (weight < 68.5)
                    nutritionState = 4;
                else if (weight >= 68.5)
                    nutritionState = 5;
                break;
            case 199:
                if (weight < 42)
                    nutritionState = 1;
                else if (weight < 45.3)
                    nutritionState = 2;
                else if (weight < 65.3)
                    nutritionState = 3;
                else if (weight < 68.6)
                    nutritionState = 4;
                else if (weight >= 68.6)
                    nutritionState = 5;
                break;
            case 200:
                if (weight < 42.2)
                    nutritionState = 1;
                else if (weight < 45.5)
                    nutritionState = 2;
                else if (weight < 65.4)
                    nutritionState = 3;
                else if (weight < 68.7)
                    nutritionState = 4;
                else if (weight >= 68.7)
                    nutritionState = 5;
                break;
            case 201:
                if (weight < 42.4)
                    nutritionState = 1;
                else if (weight < 45.7)
                    nutritionState = 2;
                else if (weight < 65.5)
                    nutritionState = 3;
                else if (weight < 68.8)
                    nutritionState = 4;
                else if (weight >= 68.8)
                    nutritionState = 5;
                break;
            case 202:
                if (weight < 42.6)
                    nutritionState = 1;
                else if (weight < 45.9)
                    nutritionState = 2;
                else if (weight < 65.6)
                    nutritionState = 3;
                else if (weight < 68.9)
                    nutritionState = 4;
                else if (weight >= 68.9)
                    nutritionState = 5;
                break;
            case 203:
                if (weight < 42.8)
                    nutritionState = 1;
                else if (weight < 46.1)
                    nutritionState = 2;
                else if (weight < 65.7)
                    nutritionState = 3;
                else if (weight < 69)
                    nutritionState = 4;
                else if (weight >= 69)
                    nutritionState = 5;
                break;
            case 204:
                if (weight < 43)
                    nutritionState = 1;
                else if (weight < 46.3)
                    nutritionState = 2;
                else if (weight < 65.9)
                    nutritionState = 3;
                else if (weight < 69.1)
                    nutritionState = 4;
                else if (weight >= 69.1)
                    nutritionState = 5;
                break;
            case 205:
                if (weight < 43.1)
                    nutritionState = 1;
                else if (weight < 46.4)
                    nutritionState = 2;
                else if (weight < 66)
                    nutritionState = 3;
                else if (weight < 69.2)
                    nutritionState = 4;
                else if (weight >= 69.2)
                    nutritionState = 5;
                break;
            case 206:
                if (weight < 43.3)
                    nutritionState = 1;
                else if (weight < 46.6)
                    nutritionState = 2;
                else if (weight < 61.1)
                    nutritionState = 3;
                else if (weight < 69.3)
                    nutritionState = 4;
                else if (weight >= 69.3)
                    nutritionState = 5;
                break;
            case 207:
                if (weight < 43.5)
                    nutritionState = 1;
                else if (weight < 46.8)
                    nutritionState = 2;
                else if (weight < 66.2)
                    nutritionState = 3;
                else if (weight < 69.4)
                    nutritionState = 4;
                else if (weight >= 69.4)
                    nutritionState = 5;
                break;
            case 208:
                if (weight < 43.7)
                    nutritionState = 1;
                else if (weight < 47)
                    nutritionState = 2;
                else if (weight < 66.3)
                    nutritionState = 3;
                else if (weight < 69.5)
                    nutritionState = 4;
                else if (weight >= 69.5)
                    nutritionState = 5;
                break;
            case 209:
                if (weight < 43.8)
                    nutritionState = 1;
                else if (weight < 47.1)
                    nutritionState = 2;
                else if (weight < 66.4)
                    nutritionState = 3;
                else if (weight < 69.6)
                    nutritionState = 4;
                else if (weight >= 69.6)
                    nutritionState = 5;
                break;
            case 210:
                if (weight < 44)
                    nutritionState = 1;
                else if (weight < 47.2)
                    nutritionState = 2;
                else if (weight < 66.5)
                    nutritionState = 3;
                else if (weight < 69.7)
                    nutritionState = 4;
                else if (weight >= 69.7)
                    nutritionState = 5;
                break;
            case 211:
                if (weight < 44.2)
                    nutritionState = 1;
                else if (weight < 47.4)
                    nutritionState = 2;
                else if (weight < 66.5)
                    nutritionState = 3;
                else if (weight < 69.7)
                    nutritionState = 4;
                else if (weight >= 69.7)
                    nutritionState = 5;
                break;
            case 212:
                if (weight < 44.3)
                    nutritionState = 1;
                else if (weight < 47.5)
                    nutritionState = 2;
                else if (weight < 66.6)
                    nutritionState = 3;
                else if (weight < 69.7)
                    nutritionState = 4;
                else if (weight >= 69.7)
                    nutritionState = 5;
                break;
            case 213:
                if (weight < 44.5)
                    nutritionState = 1;
                else if (weight < 47.7)
                    nutritionState = 2;
                else if (weight < 66.7)
                    nutritionState = 3;
                else if (weight < 69.8)
                    nutritionState = 4;
                else if (weight >= 69.8)
                    nutritionState = 5;
                break;
            case 214:
                if (weight < 44.6)
                    nutritionState = 1;
                else if (weight < 47.8)
                    nutritionState = 2;
                else if (weight < 66.8)
                    nutritionState = 3;
                else if (weight < 69.9)
                    nutritionState = 4;
                else if (weight >= 69.9)
                    nutritionState = 5;
                break;
            case 215:
                if (weight < 44.8)
                    nutritionState = 1;
                else if (weight < 48)
                    nutritionState = 2;
                else if (weight < 66.9)
                    nutritionState = 3;
                else if (weight < 69.9)
                    nutritionState = 4;
                else if (weight >= 69.9)
                    nutritionState = 5;
                break;
            case 216:
                if (weight < 44.9)
                    nutritionState = 1;
                else if (weight < 48.1)
                    nutritionState = 2;
                else if (weight < 67)
                    nutritionState = 3;
                else if (weight < 70)
                    nutritionState = 4;
                else if (weight >= 70)
                    nutritionState = 5;
                break;
            case 217:
                if (weight < 45)
                    nutritionState = 1;
                else if (weight < 48.2)
                    nutritionState = 2;
                else if (weight < 67)
                    nutritionState = 3;
                else if (weight < 70)
                    nutritionState = 4;
                else if (weight >= 70)
                    nutritionState = 5;
                break;
            case 218:
                if (weight < 45.3)
                    nutritionState = 1;
                else if (weight < 48.4)
                    nutritionState = 2;
                else if (weight < 67.1)
                    nutritionState = 3;
                else if (weight < 70.1)
                    nutritionState = 4;
                else if (weight >= 70.1)
                    nutritionState = 5;
                break;
            case 219:
                if (weight < 45.4)
                    nutritionState = 1;
                else if (weight < 48.5)
                    nutritionState = 2;
                else if (weight < 67.1)
                    nutritionState = 3;
                else if (weight < 70.1)
                    nutritionState = 4;
                else if (weight >= 70.1)
                    nutritionState = 5;
                break;
            case 220:
            case 221:
                if (weight < 45.5)
                    nutritionState = 1;
                else if (weight < 48.6)
                    nutritionState = 2;
                else if (weight < 67.2)
                    nutritionState = 3;
                else if (weight < 70.2)
                    nutritionState = 4;
                else if (weight >= 70.2)
                    nutritionState = 5;
                break;
            case 222:
                if (weight < 45.6)
                    nutritionState = 1;
                else if (weight < 48.7)
                    nutritionState = 2;
                else if (weight < 67.3)
                    nutritionState = 3;
                else if (weight < 70.3)
                    nutritionState = 4;
                else if (weight >= 70.3)
                    nutritionState = 5;
                break;
            case 223:
                if (weight < 45.7)
                    nutritionState = 1;
                else if (weight < 48.8)
                    nutritionState = 2;
                else if (weight < 67.3)
                    nutritionState = 3;
                else if (weight < 70.3)
                    nutritionState = 4;
                else if (weight >= 70.3)
                    nutritionState = 5;
                break;
            case 224:
                if (weight < 45.8)
                    nutritionState = 1;
                else if (weight < 48.8)
                    nutritionState = 2;
                else if (weight < 67.4)
                    nutritionState = 3;
                else if (weight < 70.4)
                    nutritionState = 4;
                else if (weight >= 70.4)
                    nutritionState = 5;
                break;
            case 225:
                if (weight < 45.8)
                    nutritionState = 1;
                else if (weight < 48.9)
                    nutritionState = 2;
                else if (weight < 67.4)
                    nutritionState = 3;
                else if (weight < 70.4)
                    nutritionState = 4;
                else if (weight >= 70.4)
                    nutritionState = 5;
                break;
            case 226:
            case 227:
                if (weight < 45.9)
                    nutritionState = 1;
                else if (weight < 48.9)
                    nutritionState = 2;
                else if (weight < 67.5)
                    nutritionState = 3;
                else if (weight < 70.5)
                    nutritionState = 4;
                else if (weight >= 70.5)
                    nutritionState = 5;
                break;
            default:
                nutritionState = 0;
                break;
        }
        return nutritionState;
    }

    // ��� : ���� / ��ǹ�٧
    public static int GetNutritionStateMaleAgeLessThen18AgeHeight(int age,
                                                                  double height) {
        int nutritionState = 0;
        switch (age) {
            case 0:
                if (height < 46.8)
                    nutritionState = 1;
                else if (height < 47.6)
                    nutritionState = 2;
                else if (height < 53.2)
                    nutritionState = 3;
                else if (height < 54.2)
                    nutritionState = 4;
                else if (height >= 54.2)
                    nutritionState = 5;
                break;
            case 1:
                if (height < 49.6)
                    nutritionState = 1;
                else if (height < 50.4)
                    nutritionState = 2;
                else if (height < 56.3)
                    nutritionState = 3;
                else if (height < 57.4)
                    nutritionState = 4;
                else if (height >= 57.4)
                    nutritionState = 5;
                break;
            case 2:
                if (height < 52.3)
                    nutritionState = 1;
                else if (height < 53.2)
                    nutritionState = 2;
                else if (height < 59.2)
                    nutritionState = 3;
                else if (height < 60.4)
                    nutritionState = 4;
                else if (height >= 60.4)
                    nutritionState = 5;
                break;
            case 3:
                if (height < 54.8)
                    nutritionState = 1;
                else if (height < 55.7)
                    nutritionState = 2;
                else if (height < 62)
                    nutritionState = 3;
                else if (height < 63.2)
                    nutritionState = 4;
                else if (height >= 63.2)
                    nutritionState = 5;
                break;
            case 4:
                if (height < 57.2)
                    nutritionState = 1;
                else if (height < 58.1)
                    nutritionState = 2;
                else if (height < 64.7)
                    nutritionState = 3;
                else if (height < 65.9)
                    nutritionState = 4;
                else if (height >= 65.9)
                    nutritionState = 5;
                break;
            case 5:
                if (height < 59.4)
                    nutritionState = 1;
                else if (height < 60.4)
                    nutritionState = 2;
                else if (height < 67.2)
                    nutritionState = 3;
                else if (height < 68.4)
                    nutritionState = 4;
                else if (height >= 68.4)
                    nutritionState = 5;
                break;
            case 6:
                if (height < 61.4)
                    nutritionState = 1;
                else if (height < 62.4)
                    nutritionState = 2;
                else if (height < 69.3)
                    nutritionState = 3;
                else if (height < 70.6)
                    nutritionState = 4;
                else if (height >= 70.6)
                    nutritionState = 5;
                break;
            case 7:
                if (height < 63.1)
                    nutritionState = 1;
                else if (height < 64.2)
                    nutritionState = 2;
                else if (height < 71.4)
                    nutritionState = 3;
                else if (height < 72.7)
                    nutritionState = 4;
                else if (height >= 72.7)
                    nutritionState = 5;
                break;
            case 8:
                if (height < 64.8)
                    nutritionState = 1;
                else if (height < 65.9)
                    nutritionState = 2;
                else if (height < 73.3)
                    nutritionState = 3;
                else if (height < 74.6)
                    nutritionState = 4;
                else if (height >= 74.6)
                    nutritionState = 5;
                break;
            case 9:
                if (height < 66.3)
                    nutritionState = 1;
                else if (height < 67.4)
                    nutritionState = 2;
                else if (height < 75.1)
                    nutritionState = 3;
                else if (height < 76.5)
                    nutritionState = 4;
                else if (height >= 76.5)
                    nutritionState = 5;
                break;
            case 10:
                if (height < 67.7)
                    nutritionState = 1;
                else if (height < 68.9)
                    nutritionState = 2;
                else if (height < 76.8)
                    nutritionState = 3;
                else if (height < 78.3)
                    nutritionState = 4;
                else if (height >= 78.3)
                    nutritionState = 5;
                break;
            case 11:
                if (height < 69.1)
                    nutritionState = 1;
                else if (height < 70.2)
                    nutritionState = 2;
                else if (height < 78.3)
                    nutritionState = 3;
                else if (height < 79.9)
                    nutritionState = 4;
                else if (height >= 79.9)
                    nutritionState = 5;
                break;
            case 12:
                if (height < 70.3)
                    nutritionState = 1;
                else if (height < 71.5)
                    nutritionState = 2;
                else if (height < 79.8)
                    nutritionState = 3;
                else if (height < 81.4)
                    nutritionState = 4;
                else if (height >= 81.4)
                    nutritionState = 5;
                break;
            case 13:
                if (height < 71.4)
                    nutritionState = 1;
                else if (height < 72.6)
                    nutritionState = 2;
                else if (height < 81.1)
                    nutritionState = 3;
                else if (height < 82.8)
                    nutritionState = 4;
                else if (height >= 82.8)
                    nutritionState = 5;
                break;
            case 14:
                if (height < 72.4)
                    nutritionState = 1;
                else if (height < 73.6)
                    nutritionState = 2;
                else if (height < 82.5)
                    nutritionState = 3;
                else if (height < 84.2)
                    nutritionState = 4;
                else if (height >= 84.2)
                    nutritionState = 5;
                break;
            case 15:
                if (height < 73.4)
                    nutritionState = 1;
                else if (height < 74.6)
                    nutritionState = 2;
                else if (height < 83.7)
                    nutritionState = 3;
                else if (height < 85.4)
                    nutritionState = 4;
                else if (height >= 85.4)
                    nutritionState = 5;
                break;
            case 16:
                if (height < 74.3)
                    nutritionState = 1;
                else if (height < 75.6)
                    nutritionState = 2;
                else if (height < 84.8)
                    nutritionState = 3;
                else if (height < 86.6)
                    nutritionState = 4;
                else if (height >= 88.6)
                    nutritionState = 5;
                break;
            case 17:
                if (height < 75.1)
                    nutritionState = 1;
                else if (height < 76.4)
                    nutritionState = 2;
                else if (height < 85.9)
                    nutritionState = 3;
                else if (height < 87.7)
                    nutritionState = 4;
                else if (height >= 87.7)
                    nutritionState = 5;
                break;
            case 18:
                if (height < 75.8)
                    nutritionState = 1;
                else if (height < 77.2)
                    nutritionState = 2;
                else if (height < 87)
                    nutritionState = 3;
                else if (height < 88.8)
                    nutritionState = 4;
                else if (height >= 88.8)
                    nutritionState = 5;
                break;
            case 19:
                if (height < 76.6)
                    nutritionState = 1;
                else if (height < 78)
                    nutritionState = 2;
                else if (height < 88.1)
                    nutritionState = 3;
                else if (height < 90.1)
                    nutritionState = 4;
                else if (height >= 90.1)
                    nutritionState = 5;
                break;
            case 20:
                if (height < 77.3)
                    nutritionState = 1;
                else if (height < 78.8)
                    nutritionState = 2;
                else if (height < 89.4)
                    nutritionState = 3;
                else if (height < 91.4)
                    nutritionState = 4;
                else if (height >= 91.4)
                    nutritionState = 5;
                break;
            case 21:
                if (height < 78)
                    nutritionState = 1;
                else if (height < 79.6)
                    nutritionState = 2;
                else if (height < 90.6)
                    nutritionState = 3;
                else if (height < 92.7)
                    nutritionState = 4;
                else if (height >= 92.7)
                    nutritionState = 5;
                break;
            case 22:
                if (height < 78.8)
                    nutritionState = 1;
                else if (height < 80.4)
                    nutritionState = 2;
                else if (height < 91.9)
                    nutritionState = 3;
                else if (height < 94.1)
                    nutritionState = 4;
                else if (height >= 94.1)
                    nutritionState = 5;
                break;
            case 23:
                if (height < 79.6)
                    nutritionState = 1;
                else if (height < 81.3)
                    nutritionState = 2;
                else if (height < 93.2)
                    nutritionState = 3;
                else if (height < 95.6)
                    nutritionState = 4;
                else if (height >= 95.6)
                    nutritionState = 5;
                break;
            case 24:
                if (height < 81)
                    nutritionState = 1;
                else if (height < 82.5)
                    nutritionState = 2;
                else if (height < 91.6)
                    nutritionState = 3;
                else if (height < 93.1)
                    nutritionState = 4;
                else if (height >= 93.1)
                    nutritionState = 5;
                break;
            case 25:
                if (height < 81.6)
                    nutritionState = 1;
                else if (height < 83.2)
                    nutritionState = 2;
                else if (height < 92.5)
                    nutritionState = 3;
                else if (height < 94.1)
                    nutritionState = 4;
                else if (height >= 94.1)
                    nutritionState = 5;
                break;
            case 26:
                if (height < 82.1)
                    nutritionState = 1;
                else if (height < 83.7)
                    nutritionState = 2;
                else if (height < 93.3)
                    nutritionState = 3;
                else if (height < 94.9)
                    nutritionState = 4;
                else if (height >= 94.9)
                    nutritionState = 5;
                break;
            case 27:
                if (height < 82.7)
                    nutritionState = 1;
                else if (height < 84.3)
                    nutritionState = 2;
                else if (height < 94.1)
                    nutritionState = 3;
                else if (height < 95.8)
                    nutritionState = 4;
                else if (height >= 95.8)
                    nutritionState = 5;
                break;
            case 28:
                if (height < 83.2)
                    nutritionState = 1;
                else if (height < 84.8)
                    nutritionState = 2;
                else if (height < 94.9)
                    nutritionState = 3;
                else if (height < 96.7)
                    nutritionState = 4;
                else if (height >= 96.7)
                    nutritionState = 5;
                break;
            case 29:
                if (height < 83.8)
                    nutritionState = 1;
                else if (height < 85.5)
                    nutritionState = 2;
                else if (height < 95.8)
                    nutritionState = 3;
                else if (height < 97.5)
                    nutritionState = 4;
                else if (height >= 97.5)
                    nutritionState = 5;
                break;
            case 30:
                if (height < 84.3)
                    nutritionState = 1;
                else if (height < 86)
                    nutritionState = 2;
                else if (height < 96.5)
                    nutritionState = 3;
                else if (height < 98.3)
                    nutritionState = 4;
                else if (height >= 98.3)
                    nutritionState = 5;
                break;
            case 31:
                if (height < 84.9)
                    nutritionState = 1;
                else if (height < 86.6)
                    nutritionState = 2;
                else if (height < 97.3)
                    nutritionState = 3;
                else if (height < 99.1)
                    nutritionState = 4;
                else if (height >= 99.1)
                    nutritionState = 5;
                break;
            case 32:
                if (height < 85.4)
                    nutritionState = 1;
                else if (height < 87.1)
                    nutritionState = 2;
                else if (height < 98.1)
                    nutritionState = 3;
                else if (height < 99.9)
                    nutritionState = 4;
                else if (height >= 99.9)
                    nutritionState = 5;
                break;
            case 33:
                if (height < 85.9)
                    nutritionState = 1;
                else if (height < 87.7)
                    nutritionState = 2;
                else if (height < 98.8)
                    nutritionState = 3;
                else if (height < 100.7)
                    nutritionState = 4;
                else if (height >= 100.7)
                    nutritionState = 5;
                break;
            case 34:
                if (height < 86.5)
                    nutritionState = 1;
                else if (height < 88.3)
                    nutritionState = 2;
                else if (height < 99.5)
                    nutritionState = 3;
                else if (height < 101.4)
                    nutritionState = 4;
                else if (height >= 101.4)
                    nutritionState = 5;
                break;
            case 35:
                if (height < 87)
                    nutritionState = 1;
                else if (height < 88.8)
                    nutritionState = 2;
                else if (height < 100.2)
                    nutritionState = 3;
                else if (height < 102.1)
                    nutritionState = 4;
                else if (height >= 102.1)
                    nutritionState = 5;
                break;
            case 36:
                if (height < 87.6)
                    nutritionState = 1;
                else if (height < 89.4)
                    nutritionState = 2;
                else if (height < 100.9)
                    nutritionState = 3;
                else if (height < 102.8)
                    nutritionState = 4;
                else if (height >= 102.8)
                    nutritionState = 5;
                break;
            case 37:
                if (height < 88.1)
                    nutritionState = 1;
                else if (height < 90)
                    nutritionState = 2;
                else if (height < 101.6)
                    nutritionState = 3;
                else if (height < 103.5)
                    nutritionState = 4;
                else if (height >= 103.5)
                    nutritionState = 5;
                break;
            case 38:
                if (height < 88.5)
                    nutritionState = 1;
                else if (height < 90.5)
                    nutritionState = 2;
                else if (height < 102.2)
                    nutritionState = 3;
                else if (height < 104.2)
                    nutritionState = 4;
                else if (height >= 104.2)
                    nutritionState = 5;
                break;
            case 39:
                if (height < 89.1)
                    nutritionState = 1;
                else if (height < 91.1)
                    nutritionState = 2;
                else if (height < 102.8)
                    nutritionState = 3;
                else if (height < 104.8)
                    nutritionState = 4;
                else if (height >= 104.8)
                    nutritionState = 5;
                break;
            case 40:
                if (height < 89.7)
                    nutritionState = 1;
                else if (height < 91.6)
                    nutritionState = 2;
                else if (height < 103.5)
                    nutritionState = 3;
                else if (height < 105.4)
                    nutritionState = 4;
                else if (height >= 105.4)
                    nutritionState = 5;
                break;
            case 41:
                if (height < 90.2)
                    nutritionState = 1;
                else if (height < 92.2)
                    nutritionState = 2;
                else if (height < 104.1)
                    nutritionState = 3;
                else if (height < 106)
                    nutritionState = 4;
                else if (height >= 106)
                    nutritionState = 5;
                break;
            case 42:
                if (height < 90.7)
                    nutritionState = 1;
                else if (height < 92.7)
                    nutritionState = 2;
                else if (height < 104.7)
                    nutritionState = 3;
                else if (height < 106.7)
                    nutritionState = 4;
                else if (height >= 106.7)
                    nutritionState = 5;
                break;
            case 43:
                if (height < 91.2)
                    nutritionState = 1;
                else if (height < 93.2)
                    nutritionState = 2;
                else if (height < 105.3)
                    nutritionState = 3;
                else if (height < 107.3)
                    nutritionState = 4;
                else if (height >= 107.3)
                    nutritionState = 5;
                break;
            case 44:
                if (height < 91.7)
                    nutritionState = 1;
                else if (height < 93.8)
                    nutritionState = 2;
                else if (height < 105.9)
                    nutritionState = 3;
                else if (height < 107.9)
                    nutritionState = 4;
                else if (height >= 107.9)
                    nutritionState = 5;
                break;
            case 45:
                if (height < 92.3)
                    nutritionState = 1;
                else if (height < 94.3)
                    nutritionState = 2;
                else if (height < 106.5)
                    nutritionState = 3;
                else if (height < 108.5)
                    nutritionState = 4;
                else if (height >= 108.5)
                    nutritionState = 5;
                break;
            case 46:
                if (height < 92.8)
                    nutritionState = 1;
                else if (height < 94.8)
                    nutritionState = 2;
                else if (height < 107.1)
                    nutritionState = 3;
                else if (height < 109.1)
                    nutritionState = 4;
                else if (height >= 109.1)
                    nutritionState = 5;
                break;
            case 47:
                if (height < 93.3)
                    nutritionState = 1;
                else if (height < 95.4)
                    nutritionState = 2;
                else if (height < 107.7)
                    nutritionState = 3;
                else if (height < 109.7)
                    nutritionState = 4;
                else if (height >= 109.7)
                    nutritionState = 5;
                break;
            // '48 - 59 ????? (5 ??)
            case 48:
                if (height < 93.8)
                    nutritionState = 1;
                else if (height < 95.9)
                    nutritionState = 2;
                else if (height < 108.3)
                    nutritionState = 3;
                else if (height < 110.3)
                    nutritionState = 4;
                else if (height >= 110.3)
                    nutritionState = 5;
                break;
            case 49:
                if (height < 94.4)
                    nutritionState = 1;
                else if (height < 96.4)
                    nutritionState = 2;
                else if (height < 108.8)
                    nutritionState = 3;
                else if (height < 110.9)
                    nutritionState = 4;
                else if (height >= 110.9)
                    nutritionState = 5;
                break;
            case 50:
                if (height < 94.9)
                    nutritionState = 1;
                else if (height < 96.9)
                    nutritionState = 2;
                else if (height < 109.4)
                    nutritionState = 3;
                else if (height < 111.5)
                    nutritionState = 4;
                else if (height >= 111.5)
                    nutritionState = 5;
                break;
            case 51:
                if (height < 95.4)
                    nutritionState = 1;
                else if (height < 97.5)
                    nutritionState = 2;
                else if (height < 110)
                    nutritionState = 3;
                else if (height < 112.1)
                    nutritionState = 4;
                else if (height >= 112.1)
                    nutritionState = 5;
                break;
            case 52:
                if (height < 96)
                    nutritionState = 1;
                else if (height < 98)
                    nutritionState = 2;
                else if (height < 110.6)
                    nutritionState = 3;
                else if (height < 112.7)
                    nutritionState = 4;
                else if (height >= 112.7)
                    nutritionState = 5;
                break;
            case 53:
                if (height < 96.5)
                    nutritionState = 1;
                else if (height < 98.5)
                    nutritionState = 2;
                else if (height < 111.2)
                    nutritionState = 3;
                else if (height < 113.3)
                    nutritionState = 4;
                else if (height >= 113.3)
                    nutritionState = 5;
                break;
            case 54:
                if (height < 96.9)
                    nutritionState = 1;
                else if (height < 99)
                    nutritionState = 2;
                else if (height < 111.8)
                    nutritionState = 3;
                else if (height < 113.9)
                    nutritionState = 4;
                else if (height >= 113.9)
                    nutritionState = 5;
                break;
            case 55:
                if (height < 97.6)
                    nutritionState = 1;
                else if (height < 99.6)
                    nutritionState = 2;
                else if (height < 112.4)
                    nutritionState = 3;
                else if (height < 114.5)
                    nutritionState = 4;
                else if (height >= 114.5)
                    nutritionState = 5;
                break;
            case 56:
                if (height < 98)
                    nutritionState = 1;
                else if (height < 100.1)
                    nutritionState = 2;
                else if (height < 112.9)
                    nutritionState = 3;
                else if (height < 115.1)
                    nutritionState = 4;
                else if (height >= 115.1)
                    nutritionState = 5;
                break;
            case 57:
                if (height < 98.5)
                    nutritionState = 1;
                else if (height < 100.6)
                    nutritionState = 2;
                else if (height < 113.5)
                    nutritionState = 3;
                else if (height < 115.7)
                    nutritionState = 4;
                else if (height >= 115.7)
                    nutritionState = 5;
                break;
            case 58:
                if (height < 99)
                    nutritionState = 1;
                else if (height < 101.1)
                    nutritionState = 2;
                else if (height < 114.1)
                    nutritionState = 3;
                else if (height < 116.3)
                    nutritionState = 4;
                else if (height >= 116.3)
                    nutritionState = 5;
                break;
            case 59:
                if (height < 99.5)
                    nutritionState = 1;
                else if (height < 101.6)
                    nutritionState = 2;
                else if (height < 114.6)
                    nutritionState = 3;
                else if (height < 116.8)
                    nutritionState = 4;
                else if (height >= 116.8)
                    nutritionState = 5;
                break;
            // '60 - 71 ????? (6 ??)
            case 60:
                if (height < 99.9)
                    nutritionState = 1;
                else if (height < 102)
                    nutritionState = 2;
                else if (height < 115.2)
                    nutritionState = 3;
                else if (height < 117.4)
                    nutritionState = 4;
                else if (height >= 117.4)
                    nutritionState = 5;
                break;
            case 61:
                if (height < 100.4)
                    nutritionState = 1;
                else if (height < 102.5)
                    nutritionState = 2;
                else if (height < 115.7)
                    nutritionState = 3;
                else if (height < 117.9)
                    nutritionState = 4;
                else if (height >= 117.9)
                    nutritionState = 5;
                break;
            case 62:
                if (height < 100.9)
                    nutritionState = 1;
                else if (height < 103)
                    nutritionState = 2;
                else if (height < 116.2)
                    nutritionState = 3;
                else if (height < 118.5)
                    nutritionState = 4;
                else if (height >= 118.5)
                    nutritionState = 5;
                break;
            case 63:
                if (height < 101.4)
                    nutritionState = 1;
                else if (height < 103.5)
                    nutritionState = 2;
                else if (height < 116.8)
                    nutritionState = 3;
                else if (height < 119)
                    nutritionState = 4;
                else if (height >= 119)
                    nutritionState = 5;
                break;
            case 64:
                if (height < 101.8)
                    nutritionState = 1;
                else if (height < 103.9)
                    nutritionState = 2;
                else if (height < 117.3)
                    nutritionState = 3;
                else if (height < 119.5)
                    nutritionState = 4;
                else if (height >= 119.5)
                    nutritionState = 5;
                break;
            case 65:
                if (height < 102.3)
                    nutritionState = 1;
                else if (height < 104.4)
                    nutritionState = 2;
                else if (height < 117.8)
                    nutritionState = 3;
                else if (height < 120.1)
                    nutritionState = 4;
                else if (height >= 120.1)
                    nutritionState = 5;
                break;
            case 66:
                if (height < 102.8)
                    nutritionState = 1;
                else if (height < 104.9)
                    nutritionState = 2;
                else if (height < 118.3)
                    nutritionState = 3;
                else if (height < 120.6)
                    nutritionState = 4;
                else if (height >= 120.6)
                    nutritionState = 5;
                break;
            case 67:
                if (height < 103.2)
                    nutritionState = 1;
                else if (height < 105.4)
                    nutritionState = 2;
                else if (height < 118.8)
                    nutritionState = 3;
                else if (height < 121.1)
                    nutritionState = 4;
                else if (height >= 121.1)
                    nutritionState = 5;
                break;
            case 68:
                if (height < 103.7)
                    nutritionState = 1;
                else if (height < 105.9)
                    nutritionState = 2;
                else if (height < 119.3)
                    nutritionState = 3;
                else if (height < 121.6)
                    nutritionState = 4;
                else if (height >= 121.6)
                    nutritionState = 5;
                break;
            case 69:
                if (height < 104.1)
                    nutritionState = 1;
                else if (height < 106.3)
                    nutritionState = 2;
                else if (height < 119.9)
                    nutritionState = 3;
                else if (height < 122.1)
                    nutritionState = 4;
                else if (height >= 122.1)
                    nutritionState = 5;
                break;
            case 70:
                if (height < 104.6)
                    nutritionState = 1;
                else if (height < 106.8)
                    nutritionState = 2;
                else if (height < 120.4)
                    nutritionState = 3;
                else if (height < 122.7)
                    nutritionState = 4;
                else if (height >= 122.7)
                    nutritionState = 5;
                break;
            case 71:
                if (height < 105)
                    nutritionState = 1;
                else if (height < 107.2)
                    nutritionState = 2;
                else if (height < 120.9)
                    nutritionState = 3;
                else if (height < 123.2)
                    nutritionState = 4;
                else if (height >= 123.2)
                    nutritionState = 5;
                break;
            // '72 - 83 ????? (7 ??)
            case 72:
                if (height < 105.4)
                    nutritionState = 1;
                else if (height < 107.7)
                    nutritionState = 2;
                else if (height < 121.4)
                    nutritionState = 3;
                else if (height < 123.7)
                    nutritionState = 4;
                else if (height >= 123.7)
                    nutritionState = 5;
                break;
            case 73:
                if (height < 105.9)
                    nutritionState = 1;
                else if (height < 108.1)
                    nutritionState = 2;
                else if (height < 122)
                    nutritionState = 3;
                else if (height < 124.3)
                    nutritionState = 4;
                else if (height >= 124.3)
                    nutritionState = 5;
                break;
            case 74:
                if (height < 106.3)
                    nutritionState = 1;
                else if (height < 108.6)
                    nutritionState = 2;
                else if (height < 122.5)
                    nutritionState = 3;
                else if (height < 124.9)
                    nutritionState = 4;
                else if (height >= 124.9)
                    nutritionState = 5;
                break;
            case 75:
                if (height < 106.7)
                    nutritionState = 1;
                else if (height < 109)
                    nutritionState = 2;
                else if (height < 123)
                    nutritionState = 3;
                else if (height < 125.4)
                    nutritionState = 4;
                else if (height >= 125.4)
                    nutritionState = 5;
                break;
            case 76:
                if (height < 107.2)
                    nutritionState = 1;
                else if (height < 109.5)
                    nutritionState = 2;
                else if (height < 123.6)
                    nutritionState = 3;
                else if (height < 126)
                    nutritionState = 4;
                else if (height >= 126)
                    nutritionState = 5;
                break;
            case 77:
                if (height < 107.5)
                    nutritionState = 1;
                else if (height < 109.9)
                    nutritionState = 2;
                else if (height < 124.1)
                    nutritionState = 3;
                else if (height < 126.5)
                    nutritionState = 4;
                else if (height >= 126.5)
                    nutritionState = 5;
                break;
            case 78:
                if (height < 107.9)
                    nutritionState = 1;
                else if (height < 110.3)
                    nutritionState = 2;
                else if (height < 124.6)
                    nutritionState = 3;
                else if (height < 127)
                    nutritionState = 4;
                else if (height >= 127)
                    nutritionState = 5;
                break;
            case 79:
                if (height < 108.3)
                    nutritionState = 1;
                else if (height < 110.7)
                    nutritionState = 2;
                else if (height < 125.1)
                    nutritionState = 3;
                else if (height < 127.5)
                    nutritionState = 4;
                else if (height >= 127.5)
                    nutritionState = 5;
                break;
            case 80:
                if (height < 108.9)
                    nutritionState = 1;
                else if (height < 111.2)
                    nutritionState = 2;
                else if (height < 125.6)
                    nutritionState = 3;
                else if (height < 128)
                    nutritionState = 4;
                else if (height >= 128)
                    nutritionState = 5;
                break;
            case 81:
                if (height < 109.2)
                    nutritionState = 1;
                else if (height < 111.6)
                    nutritionState = 2;
                else if (height < 126.1)
                    nutritionState = 3;
                else if (height < 128.5)
                    nutritionState = 4;
                else if (height >= 128.5)
                    nutritionState = 5;
                break;
            case 82:
                if (height < 109.6)
                    nutritionState = 1;
                else if (height < 112)
                    nutritionState = 2;
                else if (height < 126.5)
                    nutritionState = 3;
                else if (height < 129)
                    nutritionState = 4;
                else if (height >= 129)
                    nutritionState = 5;
                break;
            case 83:
                if (height < 110)
                    nutritionState = 1;
                else if (height < 112.4)
                    nutritionState = 2;
                else if (height < 127)
                    nutritionState = 3;
                else if (height < 129.5)
                    nutritionState = 4;
                else if (height >= 129.5)
                    nutritionState = 5;
                break;
            // '84 - 95 ????? (8 ??)
            case 84:
                if (height < 110.4)
                    nutritionState = 1;
                else if (height < 112.8)
                    nutritionState = 2;
                else if (height < 127.5)
                    nutritionState = 3;
                else if (height < 129.9)
                    nutritionState = 4;
                else if (height >= 129.9)
                    nutritionState = 5;
                break;
            case 85:
                if (height < 110.8)
                    nutritionState = 1;
                else if (height < 113.2)
                    nutritionState = 2;
                else if (height < 127.9)
                    nutritionState = 3;
                else if (height < 130.4)
                    nutritionState = 4;
                else if (height >= 130.4)
                    nutritionState = 5;
                break;
            case 86:
                if (height < 111.1)
                    nutritionState = 1;
                else if (height < 113.6)
                    nutritionState = 2;
                else if (height < 128.4)
                    nutritionState = 3;
                else if (height < 130.9)
                    nutritionState = 4;
                else if (height >= 130.9)
                    nutritionState = 5;
                break;
            case 87:
                if (height < 111.5)
                    nutritionState = 1;
                else if (height < 114)
                    nutritionState = 2;
                else if (height < 128.9)
                    nutritionState = 3;
                else if (height < 131.4)
                    nutritionState = 4;
                else if (height >= 131.4)
                    nutritionState = 5;
                break;
            case 88:
                if (height < 111.9)
                    nutritionState = 1;
                else if (height < 114.4)
                    nutritionState = 2;
                else if (height < 129.4)
                    nutritionState = 3;
                else if (height < 131.9)
                    nutritionState = 4;
                else if (height >= 131.9)
                    nutritionState = 5;
                break;
            case 89:
                if (height < 112.3)
                    nutritionState = 1;
                else if (height < 114.8)
                    nutritionState = 2;
                else if (height < 129.9)
                    nutritionState = 3;
                else if (height < 132.4)
                    nutritionState = 4;
                else if (height >= 132.4)
                    nutritionState = 5;
                break;
            case 90:
                if (height < 112.6)
                    nutritionState = 1;
                else if (height < 115.1)
                    nutritionState = 2;
                else if (height < 130.4)
                    nutritionState = 3;
                else if (height < 132.9)
                    nutritionState = 4;
                else if (height >= 132.9)
                    nutritionState = 5;
                break;
            case 91:
                if (height < 113)
                    nutritionState = 1;
                else if (height < 115.5)
                    nutritionState = 2;
                else if (height < 130.9)
                    nutritionState = 3;
                else if (height < 133.4)
                    nutritionState = 4;
                else if (height >= 133.4)
                    nutritionState = 5;
                break;
            case 92:
                if (height < 113.4)
                    nutritionState = 1;
                else if (height < 115.9)
                    nutritionState = 2;
                else if (height < 131.3)
                    nutritionState = 3;
                else if (height < 133.9)
                    nutritionState = 4;
                else if (height >= 133.9)
                    nutritionState = 5;
                break;
            case 93:
                if (height < 113.7)
                    nutritionState = 1;
                else if (height < 116.3)
                    nutritionState = 2;
                else if (height < 131.8)
                    nutritionState = 3;
                else if (height < 134.4)
                    nutritionState = 4;
                else if (height >= 134.4)
                    nutritionState = 5;
                break;
            case 94:
                if (height < 114.1)
                    nutritionState = 1;
                else if (height < 116.7)
                    nutritionState = 2;
                else if (height < 132.3)
                    nutritionState = 3;
                else if (height < 135)
                    nutritionState = 4;
                else if (height >= 135)
                    nutritionState = 5;
                break;
            case 95:
                if (height < 114.5)
                    nutritionState = 1;
                else if (height < 117.1)
                    nutritionState = 2;
                else if (height < 132.8)
                    nutritionState = 3;
                else if (height < 135.5)
                    nutritionState = 4;
                else if (height >= 135.5)
                    nutritionState = 5;
                break;
            // '96 - 107 ????? (9 ??)
            case 96:
                if (height < 114.8)
                    nutritionState = 1;
                else if (height < 117.4)
                    nutritionState = 2;
                else if (height < 133.3)
                    nutritionState = 3;
                else if (height < 135.9)
                    nutritionState = 4;
                else if (height >= 135.9)
                    nutritionState = 5;
                break;
            case 97:
                if (height < 115.2)
                    nutritionState = 1;
                else if (height < 117.8)
                    nutritionState = 2;
                else if (height < 133.7)
                    nutritionState = 3;
                else if (height < 136.4)
                    nutritionState = 4;
                else if (height >= 136.4)
                    nutritionState = 5;
                break;
            case 98:
                if (height < 115.4)
                    nutritionState = 1;
                else if (height < 118.1)
                    nutritionState = 2;
                else if (height < 134.1)
                    nutritionState = 3;
                else if (height < 136.8)
                    nutritionState = 4;
                else if (height >= 136.8)
                    nutritionState = 5;
                break;
            case 99:
                if (height < 115.9)
                    nutritionState = 1;
                else if (height < 118.5)
                    nutritionState = 2;
                else if (height < 134.6)
                    nutritionState = 3;
                else if (height < 137.2)
                    nutritionState = 4;
                else if (height >= 137.2)
                    nutritionState = 5;
                break;
            case 100:
                if (height < 116.1)
                    nutritionState = 1;
                else if (height < 118.8)
                    nutritionState = 2;
                else if (height < 135)
                    nutritionState = 3;
                else if (height < 137.7)
                    nutritionState = 4;
                else if (height >= 137.7)
                    nutritionState = 5;
                break;
            case 101:
                if (height < 116.6)
                    nutritionState = 1;
                else if (height < 119.2)
                    nutritionState = 2;
                else if (height < 135.4)
                    nutritionState = 3;
                else if (height < 138.1)
                    nutritionState = 4;
                else if (height >= 138.1)
                    nutritionState = 5;
                break;
            case 102:
                if (height < 117)
                    nutritionState = 1;
                else if (height < 119.7)
                    nutritionState = 2;
                else if (height < 135.8)
                    nutritionState = 3;
                else if (height < 138.5)
                    nutritionState = 4;
                else if (height >= 138.5)
                    nutritionState = 5;
                break;
            case 103:
                if (height < 117.3)
                    nutritionState = 1;
                else if (height < 120)
                    nutritionState = 2;
                else if (height < 136.2)
                    nutritionState = 3;
                else if (height < 138.9)
                    nutritionState = 4;
                else if (height >= 138.9)
                    nutritionState = 5;
                break;
            case 104:
                if (height < 117.6)
                    nutritionState = 1;
                else if (height < 120.4)
                    nutritionState = 2;
                else if (height < 136.7)
                    nutritionState = 3;
                else if (height < 139.3)
                    nutritionState = 4;
                else if (height >= 139.3)
                    nutritionState = 5;
                break;
            case 105:
                if (height < 117.9)
                    nutritionState = 1;
                else if (height < 120.7)
                    nutritionState = 2;
                else if (height < 137.1)
                    nutritionState = 3;
                else if (height < 139.8)
                    nutritionState = 4;
                else if (height >= 139.8)
                    nutritionState = 5;
                break;
            case 106:
                if (height < 118.3)
                    nutritionState = 1;
                else if (height < 121.1)
                    nutritionState = 2;
                else if (height < 137.5)
                    nutritionState = 3;
                else if (height < 140.2)
                    nutritionState = 4;
                else if (height >= 140.2)
                    nutritionState = 5;
                break;
            case 107:
                if (height < 118.7)
                    nutritionState = 1;
                else if (height < 121.5)
                    nutritionState = 2;
                else if (height < 137.9)
                    nutritionState = 3;
                else if (height < 140.6)
                    nutritionState = 4;
                else if (height >= 140.6)
                    nutritionState = 5;
                break;
            // '108 - 119 ????? (10 ??)
            case 108:
                if (height < 119)
                    nutritionState = 1;
                else if (height < 121.8)
                    nutritionState = 2;
                else if (height < 138.4)
                    nutritionState = 3;
                else if (height < 141)
                    nutritionState = 4;
                else if (height >= 141)
                    nutritionState = 5;
                break;
            case 109:
                if (height < 119.4)
                    nutritionState = 1;
                else if (height < 122.2)
                    nutritionState = 2;
                else if (height < 138.8)
                    nutritionState = 3;
                else if (height < 141.5)
                    nutritionState = 4;
                else if (height >= 141.5)
                    nutritionState = 5;
                break;
            case 110:
                if (height < 119.7)
                    nutritionState = 1;
                else if (height < 122.6)
                    nutritionState = 2;
                else if (height < 139.2)
                    nutritionState = 3;
                else if (height < 141.9)
                    nutritionState = 4;
                else if (height >= 141.9)
                    nutritionState = 5;
                break;
            case 111:
                if (height < 120.1)
                    nutritionState = 1;
                else if (height < 123)
                    nutritionState = 2;
                else if (height < 139.7)
                    nutritionState = 3;
                else if (height < 142.3)
                    nutritionState = 4;
                else if (height >= 142.3)
                    nutritionState = 5;
                break;
            case 112:
                if (height < 120.4)
                    nutritionState = 1;
                else if (height < 123.3)
                    nutritionState = 2;
                else if (height < 140.1)
                    nutritionState = 3;
                else if (height < 142.8)
                    nutritionState = 4;
                else if (height >= 142.8)
                    nutritionState = 5;
                break;
            case 113:
                if (height < 120.8)
                    nutritionState = 1;
                else if (height < 123.7)
                    nutritionState = 2;
                else if (height < 140.5)
                    nutritionState = 3;
                else if (height < 143.2)
                    nutritionState = 4;
                else if (height >= 143.2)
                    nutritionState = 5;
                break;
            case 114:
                if (height < 121.2)
                    nutritionState = 1;
                else if (height < 124.1)
                    nutritionState = 2;
                else if (height < 140.9)
                    nutritionState = 3;
                else if (height < 143.6)
                    nutritionState = 4;
                else if (height >= 143.6)
                    nutritionState = 5;
                break;
            case 115:
                if (height < 121.5)
                    nutritionState = 1;
                else if (height < 124.4)
                    nutritionState = 2;
                else if (height < 141.4)
                    nutritionState = 3;
                else if (height < 144.1)
                    nutritionState = 4;
                else if (height >= 144.1)
                    nutritionState = 5;
                break;
            case 116:
                if (height < 122)
                    nutritionState = 1;
                else if (height < 124.8)
                    nutritionState = 2;
                else if (height < 141.8)
                    nutritionState = 3;
                else if (height < 144.5)
                    nutritionState = 4;
                else if (height >= 144.5)
                    nutritionState = 5;
                break;
            case 117:
                if (height < 122.2)
                    nutritionState = 1;
                else if (height < 125.1)
                    nutritionState = 2;
                else if (height < 142.2)
                    nutritionState = 3;
                else if (height < 144.9)
                    nutritionState = 4;
                else if (height >= 144.9)
                    nutritionState = 5;
                break;
            case 118:
                if (height < 122.6)
                    nutritionState = 1;
                else if (height < 125.5)
                    nutritionState = 2;
                else if (height < 142.6)
                    nutritionState = 3;
                else if (height < 145.4)
                    nutritionState = 4;
                else if (height >= 145.4)
                    nutritionState = 5;
                break;
            case 119:
                if (height < 123)
                    nutritionState = 1;
                else if (height < 125.9)
                    nutritionState = 2;
                else if (height < 143)
                    nutritionState = 3;
                else if (height < 145.8)
                    nutritionState = 4;
                else if (height >= 145.8)
                    nutritionState = 5;
                break;
            // '120 - 131 ????? (11 ??)
            case 120:
                if (height < 123.3)
                    nutritionState = 1;
                else if (height < 126.2)
                    nutritionState = 2;
                else if (height < 143.5)
                    nutritionState = 3;
                else if (height < 146.3)
                    nutritionState = 4;
                else if (height >= 146.3)
                    nutritionState = 5;
                break;
            case 121:
                if (height < 123.7)
                    nutritionState = 1;
                else if (height < 126.6)
                    nutritionState = 2;
                else if (height < 143.9)
                    nutritionState = 3;
                else if (height < 146.7)
                    nutritionState = 4;
                else if (height >= 146.7)
                    nutritionState = 5;
                break;
            case 122:
                if (height < 124)
                    nutritionState = 1;
                else if (height < 126.9)
                    nutritionState = 2;
                else if (height < 144.3)
                    nutritionState = 3;
                else if (height < 147.2)
                    nutritionState = 4;
                else if (height >= 147.2)
                    nutritionState = 5;
                break;
            case 123:
                if (height < 124.6)
                    nutritionState = 1;
                else if (height < 127.4)
                    nutritionState = 2;
                else if (height < 144.8)
                    nutritionState = 3;
                else if (height < 147.7)
                    nutritionState = 4;
                else if (height >= 147.7)
                    nutritionState = 5;
                break;
            case 124:
                if (height < 124.8)
                    nutritionState = 1;
                else if (height < 127.7)
                    nutritionState = 2;
                else if (height < 145.2)
                    nutritionState = 3;
                else if (height < 148.2)
                    nutritionState = 4;
                else if (height >= 148.2)
                    nutritionState = 5;
                break;
            case 125:
                if (height < 125.2)
                    nutritionState = 1;
                else if (height < 128.1)
                    nutritionState = 2;
                else if (height < 145.7)
                    nutritionState = 3;
                else if (height < 148.7)
                    nutritionState = 4;
                else if (height >= 148.7)
                    nutritionState = 5;
                break;
            case 126:
                if (height < 125.5)
                    nutritionState = 1;
                else if (height < 128.4)
                    nutritionState = 2;
                else if (height < 146.2)
                    nutritionState = 3;
                else if (height < 149.2)
                    nutritionState = 4;
                else if (height >= 149.2)
                    nutritionState = 5;
                break;
            case 127:
                if (height < 125.9)
                    nutritionState = 1;
                else if (height < 128.8)
                    nutritionState = 2;
                else if (height < 146.7)
                    nutritionState = 3;
                else if (height < 149.8)
                    nutritionState = 4;
                else if (height >= 149.8)
                    nutritionState = 5;
                break;
            case 128:
                if (height < 126.3)
                    nutritionState = 1;
                else if (height < 129.1)
                    nutritionState = 2;
                else if (height < 147.3)
                    nutritionState = 3;
                else if (height < 150.4)
                    nutritionState = 4;
                else if (height >= 150.4)
                    nutritionState = 5;
                break;
            case 129:
                if (height < 126.6)
                    nutritionState = 1;
                else if (height < 129.5)
                    nutritionState = 2;
                else if (height < 147.8)
                    nutritionState = 3;
                else if (height < 151)
                    nutritionState = 4;
                else if (height >= 151)
                    nutritionState = 5;
                break;
            case 130:
                if (height < 126.9)
                    nutritionState = 1;
                else if (height < 129.8)
                    nutritionState = 2;
                else if (height < 148.4)
                    nutritionState = 3;
                else if (height < 151.6)
                    nutritionState = 4;
                else if (height >= 151.6)
                    nutritionState = 5;
                break;
            case 131:
                if (height < 127.2)
                    nutritionState = 1;
                else if (height < 130.2)
                    nutritionState = 2;
                else if (height < 149)
                    nutritionState = 3;
                else if (height < 152.2)
                    nutritionState = 4;
                else if (height >= 152.2)
                    nutritionState = 5;
                break;
            // '132 - 143 ????? (12 ??)
            case 132:
                if (height < 127.5)
                    nutritionState = 1;
                else if (height < 130.5)
                    nutritionState = 2;
                else if (height < 149.5)
                    nutritionState = 3;
                else if (height < 152.9)
                    nutritionState = 4;
                else if (height >= 152.9)
                    nutritionState = 5;
                break;
            case 133:
                if (height < 127.9)
                    nutritionState = 1;
                else if (height < 130.9)
                    nutritionState = 2;
                else if (height < 150.2)
                    nutritionState = 3;
                else if (height < 153.5)
                    nutritionState = 4;
                else if (height >= 153.5)
                    nutritionState = 5;
                break;
            case 134:
                if (height < 128.1)
                    nutritionState = 1;
                else if (height < 131.2)
                    nutritionState = 2;
                else if (height < 150.8)
                    nutritionState = 3;
                else if (height < 154.2)
                    nutritionState = 4;
                else if (height >= 154.2)
                    nutritionState = 5;
                break;
            case 135:
                if (height < 128.4)
                    nutritionState = 1;
                else if (height < 131.5)
                    nutritionState = 2;
                else if (height < 151.4)
                    nutritionState = 3;
                else if (height < 154.9)
                    nutritionState = 4;
                else if (height >= 154.9)
                    nutritionState = 5;
                break;
            case 136:
                if (height < 128.9)
                    nutritionState = 1;
                else if (height < 132)
                    nutritionState = 2;
                else if (height < 152)
                    nutritionState = 3;
                else if (height < 155.5)
                    nutritionState = 4;
                else if (height >= 155.5)
                    nutritionState = 5;
                break;
            case 137:
                if (height < 129.1)
                    nutritionState = 1;
                else if (height < 132.3)
                    nutritionState = 2;
                else if (height < 152.6)
                    nutritionState = 3;
                else if (height < 156.2)
                    nutritionState = 4;
                else if (height >= 156.2)
                    nutritionState = 5;
                break;
            case 138:
                if (height < 129.4)
                    nutritionState = 1;
                else if (height < 136.6)
                    nutritionState = 2;
                else if (height < 153.3)
                    nutritionState = 3;
                else if (height < 156.9)
                    nutritionState = 4;
                else if (height >= 156.9)
                    nutritionState = 5;
                break;
            case 139:
                if (height < 129.8)
                    nutritionState = 1;
                else if (height < 133.1)
                    nutritionState = 2;
                else if (height < 153.9)
                    nutritionState = 3;
                else if (height < 157.5)
                    nutritionState = 4;
                else if (height >= 157.5)
                    nutritionState = 5;
                break;
            case 140:
                if (height < 130.1)
                    nutritionState = 1;
                else if (height < 133.4)
                    nutritionState = 2;
                else if (height < 154.5)
                    nutritionState = 3;
                else if (height < 158.2)
                    nutritionState = 4;
                else if (height >= 158.2)
                    nutritionState = 5;
                break;
            case 141:
                if (height < 130.5)
                    nutritionState = 1;
                else if (height < 133.9)
                    nutritionState = 2;
                else if (height < 155.1)
                    nutritionState = 3;
                else if (height < 158.8)
                    nutritionState = 4;
                else if (height >= 158.8)
                    nutritionState = 5;
                break;
            case 142:
                if (height < 130.9)
                    nutritionState = 1;
                else if (height < 134.3)
                    nutritionState = 2;
                else if (height < 155.8)
                    nutritionState = 3;
                else if (height < 159.5)
                    nutritionState = 4;
                else if (height >= 159.5)
                    nutritionState = 5;
                break;
            case 143:
                if (height < 131.2)
                    nutritionState = 1;
                else if (height < 134.6)
                    nutritionState = 2;
                else if (height < 156.4)
                    nutritionState = 3;
                else if (height < 160.1)
                    nutritionState = 4;
                else if (height >= 160.1)
                    nutritionState = 5;
                break;
            // '144 - 155 ????? (13 ??)
            case 144:
                if (height < 131.6)
                    nutritionState = 1;
                else if (height < 135.1)
                    nutritionState = 2;
                else if (height < 157)
                    nutritionState = 3;
                else if (height < 160.8)
                    nutritionState = 4;
                else if (height >= 160.8)
                    nutritionState = 5;
                break;
            case 145:
                if (height < 131.9)
                    nutritionState = 1;
                else if (height < 135.5)
                    nutritionState = 2;
                else if (height < 157.6)
                    nutritionState = 3;
                else if (height < 161.4)
                    nutritionState = 4;
                else if (height >= 161.4)
                    nutritionState = 5;
                break;
            case 146:
                if (height < 132.3)
                    nutritionState = 1;
                else if (height < 136)
                    nutritionState = 2;
                else if (height < 158.3)
                    nutritionState = 3;
                else if (height < 162)
                    nutritionState = 4;
                else if (height >= 162)
                    nutritionState = 5;
                break;
            case 147:
                if (height < 132.7)
                    nutritionState = 1;
                else if (height < 136.4)
                    nutritionState = 2;
                else if (height < 158.9)
                    nutritionState = 3;
                else if (height < 162.6)
                    nutritionState = 4;
                else if (height >= 162.6)
                    nutritionState = 5;
                break;
            case 148:
                if (height < 133.2)
                    nutritionState = 1;
                else if (height < 136.9)
                    nutritionState = 2;
                else if (height < 159.5)
                    nutritionState = 3;
                else if (height < 163.3)
                    nutritionState = 4;
                else if (height >= 163.3)
                    nutritionState = 5;
                break;
            case 149:
                if (height < 133.6)
                    nutritionState = 1;
                else if (height < 137.4)
                    nutritionState = 2;
                else if (height < 160.1)
                    nutritionState = 3;
                else if (height < 163.9)
                    nutritionState = 4;
                else if (height >= 163.9)
                    nutritionState = 5;
                break;
            case 150:
                if (height < 134)
                    nutritionState = 1;
                else if (height < 137.8)
                    nutritionState = 2;
                else if (height < 160.8)
                    nutritionState = 3;
                else if (height < 164.5)
                    nutritionState = 4;
                else if (height >= 164.5)
                    nutritionState = 5;
                break;
            case 151:
                if (height < 134.5)
                    nutritionState = 1;
                else if (height < 138.4)
                    nutritionState = 2;
                else if (height < 161.4)
                    nutritionState = 3;
                else if (height < 165.2)
                    nutritionState = 4;
                else if (height >= 165.2)
                    nutritionState = 5;
                break;
            case 152:
                if (height < 135)
                    nutritionState = 1;
                else if (height < 138.9)
                    nutritionState = 2;
                else if (height < 162)
                    nutritionState = 3;
                else if (height < 165.8)
                    nutritionState = 4;
                else if (height >= 165.8)
                    nutritionState = 5;
                break;
            case 153:
                if (height < 135.4)
                    nutritionState = 1;
                else if (height < 139.3)
                    nutritionState = 2;
                else if (height < 162.7)
                    nutritionState = 3;
                else if (height < 166.4)
                    nutritionState = 4;
                else if (height >= 166.4)
                    nutritionState = 5;
                break;
            case 154:
                if (height < 135.8)
                    nutritionState = 1;
                else if (height < 139.9)
                    nutritionState = 2;
                else if (height < 163.3)
                    nutritionState = 3;
                else if (height < 167)
                    nutritionState = 4;
                else if (height >= 167)
                    nutritionState = 5;
                break;
            case 155:
                if (height < 136.3)
                    nutritionState = 1;
                else if (height < 140.4)
                    nutritionState = 2;
                else if (height < 163.9)
                    nutritionState = 3;
                else if (height < 167.6)
                    nutritionState = 4;
                else if (height >= 167.6)
                    nutritionState = 5;
                break;
            // :6 - 167 ????? (14 ??)
            case 156:
                if (height < 136.8)
                    nutritionState = 1;
                else if (height < 140.9)
                    nutritionState = 2;
                else if (height < 164.5)
                    nutritionState = 3;
                else if (height < 168.3)
                    nutritionState = 4;
                else if (height >= 168.3)
                    nutritionState = 5;
                break;
            case 157:
                if (height < 137.3)
                    nutritionState = 1;
                else if (height < 141.4)
                    nutritionState = 2;
                else if (height < 165.1)
                    nutritionState = 3;
                else if (height < 168.8)
                    nutritionState = 4;
                else if (height >= 168.8)
                    nutritionState = 5;
                break;
            case 158:
                if (height < 137.8)
                    nutritionState = 1;
                else if (height < 142)
                    nutritionState = 2;
                else if (height < 165.6)
                    nutritionState = 3;
                else if (height < 169.3)
                    nutritionState = 4;
                else if (height >= 169.3)
                    nutritionState = 5;
                break;
            case 159:
                if (height < 138.3)
                    nutritionState = 1;
                else if (height < 142.5)
                    nutritionState = 2;
                else if (height < 166.1)
                    nutritionState = 3;
                else if (height < 169.8)
                    nutritionState = 4;
                else if (height >= 169.8)
                    nutritionState = 5;
                break;
            case 160:
                if (height < 138.8)
                    nutritionState = 1;
                else if (height < 143)
                    nutritionState = 2;
                else if (height < 166.7)
                    nutritionState = 3;
                else if (height < 170.3)
                    nutritionState = 4;
                else if (height >= 170.3)
                    nutritionState = 5;
                break;
            case 161:
                if (height < 139.3)
                    nutritionState = 1;
                else if (height < 143.5)
                    nutritionState = 2;
                else if (height < 167.2)
                    nutritionState = 3;
                else if (height < 170.8)
                    nutritionState = 4;
                else if (height >= 170.8)
                    nutritionState = 5;
                break;
            case 162:
                if (height < 139.8)
                    nutritionState = 1;
                else if (height < 144.1)
                    nutritionState = 2;
                else if (height < 167.7)
                    nutritionState = 3;
                else if (height < 171.2)
                    nutritionState = 4;
                else if (height >= 171.2)
                    nutritionState = 5;
                break;
            case 163:
                if (height < 140.4)
                    nutritionState = 1;
                else if (height < 144.7)
                    nutritionState = 2;
                else if (height < 168.1)
                    nutritionState = 3;
                else if (height < 171.6)
                    nutritionState = 4;
                else if (height >= 171.6)
                    nutritionState = 5;
                break;
            case 164:
                if (height < 140.9)
                    nutritionState = 1;
                else if (height < 145.2)
                    nutritionState = 2;
                else if (height < 168.6)
                    nutritionState = 3;
                else if (height < 172)
                    nutritionState = 4;
                else if (height >= 172)
                    nutritionState = 5;
                break;
            case 165:
                if (height < 141.5)
                    nutritionState = 1;
                else if (height < 145.8)
                    nutritionState = 2;
                else if (height < 169)
                    nutritionState = 3;
                else if (height < 172.4)
                    nutritionState = 4;
                else if (height >= 172.4)
                    nutritionState = 5;
                break;
            case 166:
                if (height < 142)
                    nutritionState = 1;
                else if (height < 146.3)
                    nutritionState = 2;
                else if (height < 169.4)
                    nutritionState = 3;
                else if (height < 172.7)
                    nutritionState = 4;
                else if (height >= 172.7)
                    nutritionState = 5;
                break;
            case 167:
                if (height < 142.5)
                    nutritionState = 1;
                else if (height < 146.8)
                    nutritionState = 2;
                else if (height < 169.8)
                    nutritionState = 3;
                else if (height < 173)
                    nutritionState = 4;
                else if (height >= 173)
                    nutritionState = 5;
                break;
            // '168 - 179 ????? (15 ??)
            case 168:
                if (height < 143)
                    nutritionState = 1;
                else if (height < 147.3)
                    nutritionState = 2;
                else if (height < 170.1)
                    nutritionState = 3;
                else if (height < 173.3)
                    nutritionState = 4;
                else if (height >= 173.3)
                    nutritionState = 5;
                break;
            case 169:
                if (height < 143.6)
                    nutritionState = 1;
                else if (height < 147.9)
                    nutritionState = 2;
                else if (height < 170.4)
                    nutritionState = 3;
                else if (height < 173.6)
                    nutritionState = 4;
                else if (height >= 173.6)
                    nutritionState = 5;
                break;
            case 170:
                if (height < 144.1)
                    nutritionState = 1;
                else if (height < 148.4)
                    nutritionState = 2;
                else if (height < 170.7)
                    nutritionState = 3;
                else if (height < 173.8)
                    nutritionState = 4;
                else if (height >= 173.8)
                    nutritionState = 5;
                break;
            case 171:
                if (height < 144.6)
                    nutritionState = 1;
                else if (height < 148.9)
                    nutritionState = 2;
                else if (height < 171)
                    nutritionState = 3;
                else if (height < 174)
                    nutritionState = 4;
                else if (height >= 174)
                    nutritionState = 5;
                break;
            case 172:
                if (height < 145.1)
                    nutritionState = 1;
                else if (height < 149.4)
                    nutritionState = 2;
                else if (height < 171.3)
                    nutritionState = 3;
                else if (height < 174.2)
                    nutritionState = 4;
                else if (height >= 174.2)
                    nutritionState = 5;
                break;
            case 173:
                if (height < 145.6)
                    nutritionState = 1;
                else if (height < 149.9)
                    nutritionState = 2;
                else if (height < 171.5)
                    nutritionState = 3;
                else if (height < 174.5)
                    nutritionState = 4;
                else if (height >= 174.5)
                    nutritionState = 5;
                break;
            case 174:
                if (height < 146.2)
                    nutritionState = 1;
                else if (height < 150.4)
                    nutritionState = 2;
                else if (height < 171.8)
                    nutritionState = 3;
                else if (height < 174.7)
                    nutritionState = 4;
                else if (height >= 174.7)
                    nutritionState = 5;
                break;
            case 175:
                if (height < 146.7)
                    nutritionState = 1;
                else if (height < 150.9)
                    nutritionState = 2;
                else if (height < 172.1)
                    nutritionState = 3;
                else if (height < 174.9)
                    nutritionState = 4;
                else if (height >= 174.9)
                    nutritionState = 5;
                break;
            case 176:
                if (height < 147.3)
                    nutritionState = 1;
                else if (height < 151.4)
                    nutritionState = 2;
                else if (height < 172.3)
                    nutritionState = 3;
                else if (height < 175.2)
                    nutritionState = 4;
                else if (height >= 175.2)
                    nutritionState = 5;
                break;
            case 177:
                if (height < 147.9)
                    nutritionState = 1;
                else if (height < 151.9)
                    nutritionState = 2;
                else if (height < 172.6)
                    nutritionState = 3;
                else if (height < 175.4)
                    nutritionState = 4;
                else if (height >= 175.4)
                    nutritionState = 5;
                break;
            case 178:
                if (height < 148.5)
                    nutritionState = 1;
                else if (height < 152.4)
                    nutritionState = 2;
                else if (height < 172.8)
                    nutritionState = 3;
                else if (height < 175.7)
                    nutritionState = 4;
                else if (height >= 175.7)
                    nutritionState = 5;
                break;
            case 179:
                if (height < 149.1)
                    nutritionState = 1;
                else if (height < 153)
                    nutritionState = 2;
                else if (height < 173.1)
                    nutritionState = 3;
                else if (height < 175.9)
                    nutritionState = 4;
                else if (height >= 175.9)
                    nutritionState = 5;
                break;
            // '180 - 191 ????? (16 ??)
            case 180:
                if (height < 149.8)
                    nutritionState = 1;
                else if (height < 153.5)
                    nutritionState = 2;
                else if (height < 173.3)
                    nutritionState = 3;
                else if (height < 176.1)
                    nutritionState = 4;
                else if (height >= 176.1)
                    nutritionState = 5;
                break;
            case 181:
                if (height < 150.4)
                    nutritionState = 1;
                else if (height < 154)
                    nutritionState = 2;
                else if (height < 173.6)
                    nutritionState = 3;
                else if (height < 176.4)
                    nutritionState = 4;
                else if (height >= 176.4)
                    nutritionState = 5;
                break;
            case 182:
                if (height < 151)
                    nutritionState = 1;
                else if (height < 154.5)
                    nutritionState = 2;
                else if (height < 173.8)
                    nutritionState = 3;
                else if (height < 176.6)
                    nutritionState = 4;
                else if (height >= 176.6)
                    nutritionState = 5;
                break;
            case 183:
                if (height < 151.6)
                    nutritionState = 1;
                else if (height < 155)
                    nutritionState = 2;
                else if (height < 174.1)
                    nutritionState = 3;
                else if (height < 176.9)
                    nutritionState = 4;
                else if (height >= 176.9)
                    nutritionState = 5;
                break;
            case 184:
                if (height < 152.1)
                    nutritionState = 1;
                else if (height < 155.5)
                    nutritionState = 2;
                else if (height < 174.3)
                    nutritionState = 3;
                else if (height < 177.1)
                    nutritionState = 4;
                else if (height >= 177.1)
                    nutritionState = 5;
                break;
            case 185:
                if (height < 152.6)
                    nutritionState = 1;
                else if (height < 155.9)
                    nutritionState = 2;
                else if (height < 174.5)
                    nutritionState = 3;
                else if (height < 177.4)
                    nutritionState = 4;
                else if (height >= 177.4)
                    nutritionState = 5;
                break;
            case 186:
                if (height < 153.1)
                    nutritionState = 1;
                else if (height < 156.4)
                    nutritionState = 2;
                else if (height < 174.8)
                    nutritionState = 3;
                else if (height < 177.6)
                    nutritionState = 4;
                else if (height >= 177.6)
                    nutritionState = 5;
                break;
            case 187:
                if (height < 153.5)
                    nutritionState = 1;
                else if (height < 156.8)
                    nutritionState = 2;
                else if (height < 175)
                    nutritionState = 3;
                else if (height < 177.8)
                    nutritionState = 4;
                else if (height >= 177.8)
                    nutritionState = 5;
                break;
            case 188:
                if (height < 153.9)
                    nutritionState = 1;
                else if (height < 157.1)
                    nutritionState = 2;
                else if (height < 175.2)
                    nutritionState = 3;
                else if (height < 178)
                    nutritionState = 4;
                else if (height >= 178)
                    nutritionState = 5;
                break;
            case 189:
                if (height < 154.3)
                    nutritionState = 1;
                else if (height < 157.4)
                    nutritionState = 2;
                else if (height < 175.4)
                    nutritionState = 3;
                else if (height < 178.2)
                    nutritionState = 4;
                else if (height >= 178.2)
                    nutritionState = 5;
                break;
            case 190:
                if (height < 154.6)
                    nutritionState = 1;
                else if (height < 157.8)
                    nutritionState = 2;
                else if (height < 175.6)
                    nutritionState = 3;
                else if (height < 178.4)
                    nutritionState = 4;
                else if (height >= 178.4)
                    nutritionState = 5;
                break;
            case 191:
                if (height < 155)
                    nutritionState = 1;
                else if (height < 158)
                    nutritionState = 2;
                else if (height < 175.8)
                    nutritionState = 3;
                else if (height < 178.6)
                    nutritionState = 4;
                else if (height >= 178.6)
                    nutritionState = 5;
                break;
            // '192 - 203 ????? (17 ??)
            case 192:
                if (height < 155.2)
                    nutritionState = 1;
                else if (height < 158.3)
                    nutritionState = 2;
                else if (height < 176)
                    nutritionState = 3;
                else if (height < 178.8)
                    nutritionState = 4;
                else if (height >= 178.8)
                    nutritionState = 5;
                break;
            case 193:
                if (height < 155.5)
                    nutritionState = 1;
                else if (height < 158.6)
                    nutritionState = 2;
                else if (height < 176.1)
                    nutritionState = 3;
                else if (height < 178.9)
                    nutritionState = 4;
                else if (height >= 178.9)
                    nutritionState = 5;
                break;
            case 194:
                if (height < 155.7)
                    nutritionState = 1;
                else if (height < 158.8)
                    nutritionState = 2;
                else if (height < 176.2)
                    nutritionState = 3;
                else if (height < 179)
                    nutritionState = 4;
                else if (height >= 179)
                    nutritionState = 5;
                break;
            case 195:
                if (height < 156)
                    nutritionState = 1;
                else if (height < 159)
                    nutritionState = 2;
                else if (height < 176.4)
                    nutritionState = 3;
                else if (height < 179.1)
                    nutritionState = 4;
                else if (height >= 179.1)
                    nutritionState = 5;
                break;
            case 196:
                if (height < 156.2)
                    nutritionState = 1;
                else if (height < 159.2)
                    nutritionState = 2;
                else if (height < 176.5)
                    nutritionState = 3;
                else if (height < 179.2)
                    nutritionState = 4;
                else if (height >= 179.2)
                    nutritionState = 5;
                break;
            case 197:
                if (height < 156.4)
                    nutritionState = 1;
                else if (height < 159.4)
                    nutritionState = 2;
                else if (height < 176.6)
                    nutritionState = 3;
                else if (height < 179.3)
                    nutritionState = 4;
                else if (height >= 179.3)
                    nutritionState = 5;
                break;
            case 198:
                if (height < 156.5)
                    nutritionState = 1;
                else if (height < 159.6)
                    nutritionState = 2;
                else if (height < 176.7)
                    nutritionState = 3;
                else if (height < 179.3)
                    nutritionState = 4;
                else if (height >= 179.3)
                    nutritionState = 5;
                break;
            case 199:
                if (height < 156.7)
                    nutritionState = 1;
                else if (height < 159.7)
                    nutritionState = 2;
                else if (height < 176.8)
                    nutritionState = 3;
                else if (height < 179.4)
                    nutritionState = 4;
                else if (height >= 179.4)
                    nutritionState = 5;
                break;
            case 200:
                if (height < 156.9)
                    nutritionState = 1;
                else if (height < 159.9)
                    nutritionState = 2;
                else if (height < 176.9)
                    nutritionState = 3;
                else if (height < 179.5)
                    nutritionState = 4;
                else if (height >= 179.5)
                    nutritionState = 5;
                break;
            case 201:
                if (height < 157)
                    nutritionState = 1;
                else if (height < 160)
                    nutritionState = 2;
                else if (height < 177)
                    nutritionState = 3;
                else if (height < 179.6)
                    nutritionState = 4;
                else if (height >= 179.6)
                    nutritionState = 5;
                break;
            case 202:
                if (height < 157.2)
                    nutritionState = 1;
                else if (height < 160.1)
                    nutritionState = 2;
                else if (height < 177.1)
                    nutritionState = 3;
                else if (height < 179.7)
                    nutritionState = 4;
                else if (height >= 179.7)
                    nutritionState = 5;
                break;
            case 203:
                if (height < 157.3)
                    nutritionState = 1;
                else if (height < 160.3)
                    nutritionState = 2;
                else if (height < 177.2)
                    nutritionState = 3;
                else if (height < 179.8)
                    nutritionState = 4;
                else if (height >= 179.8)
                    nutritionState = 5;
                break;
            // '204 - 215 ????? (18 ??)
            case 204:
                if (height < 157.4)
                    nutritionState = 1;
                else if (height < 160.4)
                    nutritionState = 2;
                else if (height < 177.3)
                    nutritionState = 3;
                else if (height < 179.9)
                    nutritionState = 4;
                else if (height >= 179.9)
                    nutritionState = 5;
                break;
            case 205:
                if (height < 157.6)
                    nutritionState = 1;
                else if (height < 160.5)
                    nutritionState = 2;
                else if (height < 177.3)
                    nutritionState = 3;
                else if (height < 180)
                    nutritionState = 4;
                else if (height >= 180)
                    nutritionState = 5;
                break;
            case 206:
                if (height < 157.7)
                    nutritionState = 1;
                else if (height < 160.6)
                    nutritionState = 2;
                else if (height < 177.4)
                    nutritionState = 3;
                else if (height < 180)
                    nutritionState = 4;
                else if (height >= 180)
                    nutritionState = 5;
                break;
            case 207:
                if (height < 157.8)
                    nutritionState = 1;
                else if (height < 160.7)
                    nutritionState = 2;
                else if (height < 177.4)
                    nutritionState = 3;
                else if (height < 180.1)
                    nutritionState = 4;
                else if (height >= 180.1)
                    nutritionState = 5;
                break;
            case 208:
                if (height < 157.9)
                    nutritionState = 1;
                else if (height < 160.8)
                    nutritionState = 2;
                else if (height < 177.5)
                    nutritionState = 3;
                else if (height < 180.1)
                    nutritionState = 4;
                else if (height >= 180.1)
                    nutritionState = 5;
                break;
            case 209:
                if (height < 158.1)
                    nutritionState = 1;
                else if (height < 160.9)
                    nutritionState = 2;
                else if (height < 177.5)
                    nutritionState = 3;
                else if (height < 180.2)
                    nutritionState = 4;
                else if (height >= 180.2)
                    nutritionState = 5;
                break;
            case 210:
                if (height < 158.2)
                    nutritionState = 1;
                else if (height < 161)
                    nutritionState = 2;
                else if (height < 177.5)
                    nutritionState = 3;
                else if (height < 180.2)
                    nutritionState = 4;
                else if (height >= 180.2)
                    nutritionState = 5;
                break;
            case 211:
                if (height < 158.3)
                    nutritionState = 1;
                else if (height < 161.1)
                    nutritionState = 2;
                else if (height < 177.5)
                    nutritionState = 3;
                else if (height < 180.2)
                    nutritionState = 4;
                else if (height >= 180.2)
                    nutritionState = 5;
                break;
            case 212:
                if (height < 158.4)
                    nutritionState = 1;
                else if (height < 161.1)
                    nutritionState = 2;
                else if (height < 177.5)
                    nutritionState = 3;
                else if (height < 180.2)
                    nutritionState = 4;
                else if (height >= 180.2)
                    nutritionState = 5;
                break;
            case 213:
                if (height < 158.5)
                    nutritionState = 1;
                else if (height < 161.2)
                    nutritionState = 2;
                else if (height < 177.5)
                    nutritionState = 3;
                else if (height < 180.2)
                    nutritionState = 4;
                else if (height >= 180.2)
                    nutritionState = 5;
                break;
            case 214:
                if (height < 158.6)
                    nutritionState = 1;
                else if (height < 161.3)
                    nutritionState = 2;
                else if (height < 177.6)
                    nutritionState = 3;
                else if (height < 180.2)
                    nutritionState = 4;
                else if (height >= 180.2)
                    nutritionState = 5;
                break;
            case 215:
                if (height < 158.6)
                    nutritionState = 1;
                else if (height < 161.3)
                    nutritionState = 2;
                else if (height < 177.6)
                    nutritionState = 3;
                else if (height < 180.2)
                    nutritionState = 4;
                else if (height >= 180.2)
                    nutritionState = 5;
                break;
            // '216 - 227 ????? (19 ??)
            case 216:
                if (height < 158.7)
                    nutritionState = 1;
                else if (height < 161.4)
                    nutritionState = 2;
                else if (height < 177.6)
                    nutritionState = 3;
                else if (height < 180.2)
                    nutritionState = 4;
                else if (height >= 180.2)
                    nutritionState = 5;
                break;
            case 217:
                if (height < 158.8)
                    nutritionState = 1;
                else if (height < 161.4)
                    nutritionState = 2;
                else if (height < 177.6)
                    nutritionState = 3;
                else if (height < 180.2)
                    nutritionState = 4;
                else if (height >= 180.2)
                    nutritionState = 5;
                break;
            case 218:
                if (height < 158.8)
                    nutritionState = 1;
                else if (height < 161.5)
                    nutritionState = 2;
                else if (height < 177.6)
                    nutritionState = 3;
                else if (height < 180.2)
                    nutritionState = 4;
                else if (height >= 180.2)
                    nutritionState = 5;
                break;
            case 219:
                if (height < 158.9)
                    nutritionState = 1;
                else if (height < 161.5)
                    nutritionState = 2;
                else if (height < 177.6)
                    nutritionState = 3;
                else if (height < 180.2)
                    nutritionState = 4;
                else if (height >= 180.2)
                    nutritionState = 5;
                break;
            case 220:
                if (height < 158.9)
                    nutritionState = 1;
                else if (height < 161.6)
                    nutritionState = 2;
                else if (height < 177.6)
                    nutritionState = 3;
                else if (height < 180.2)
                    nutritionState = 4;
                else if (height >= 180.2)
                    nutritionState = 5;
                break;
            case 221:
            case 222:
            case 223:
                if (height < 159)
                    nutritionState = 1;
                else if (height < 161.6)
                    nutritionState = 2;
                else if (height < 177.7)
                    nutritionState = 3;
                else if (height < 180.3)
                    nutritionState = 4;
                else if (height >= 180.3)
                    nutritionState = 5;
                break;
            case 224:
                if (height < 159)
                    nutritionState = 1;
                else if (height < 161.7)
                    nutritionState = 2;
                else if (height < 177.7)
                    nutritionState = 3;
                else if (height < 180.3)
                    nutritionState = 4;
                else if (height >= 180.3)
                    nutritionState = 5;
                break;
            case 225:
            case 226:
            case 227:
                if (height < 159.1)
                    nutritionState = 1;
                else if (height < 161.7)
                    nutritionState = 2;
                else if (height < 177.7)
                    nutritionState = 3;
                else if (height < 180.3)
                    nutritionState = 4;
                else if (height >= 180.3)
                    nutritionState = 5;
                break;
            case 12987:
            case 12988:
            case 12989: // ??????????????(?????????? ????????????????????????
                nutritionState = 9;
                break; // ???????????
            default:
                nutritionState = 0;
                break; // ???????????
        }
        return nutritionState;
    }

    // ��� : ��ǹ�٧ / ���˹ѡ
    public static int GetNutritionStateMaleAgeLessThen18WeightHeight(
            double weight, int height) {
        int nutritionState = 0;

        switch (height) {
            case 50:
                if (weight < 2.7)
                    nutritionState = 1;
                else if (weight >= 2.7 && weight < 2.8)
                    nutritionState = 2;
                else if (weight >= 2.8 && weight < 3.9)
                    nutritionState = 3;
                else if (weight >= 3.9 && weight < 4)
                    nutritionState = 4;
                else if (weight >= 4 && weight < 4.2)
                    nutritionState = 5;
                else if (weight >= 4.2)
                    nutritionState = 6;
                break;
            case 51:
                if (weight < 2.9)
                    nutritionState = 1;
                else if (weight >= 2.9 && weight < 3.1)
                    nutritionState = 2;
                else if (weight >= 3.1 && weight < 4.1)
                    nutritionState = 3;
                else if (weight >= 4.1 && weight < 4.3)
                    nutritionState = 4;
                else if (weight >= 4.3 && weight < 4.5)
                    nutritionState = 5;
                else if (weight >= 4.5)
                    nutritionState = 6;
            case 52:
                if (weight < 3.1)
                    nutritionState = 1;
                else if (weight >= 3.1 && weight < 3.3)
                    nutritionState = 2;
                else if (weight >= 3.3 && weight < 4.4)
                    nutritionState = 3;
                else if (weight >= 4.4 && weight < 4.6)
                    nutritionState = 4;
                else if (weight >= 4.6 && weight < 4.9)
                    nutritionState = 5;
                else if (weight >= 4.9)
                    nutritionState = 6;
                break;
            case 53:
                if (weight < 3.3)
                    nutritionState = 1;
                else if (weight >= 3.3 && weight < 3.5)
                    nutritionState = 2;
                else if (weight >= 3.5 && weight < 4.7)
                    nutritionState = 3;
                else if (weight >= 4.7 && weight < 4.8)
                    nutritionState = 4;
                else if (weight >= 4.8 && weight < 5.2)
                    nutritionState = 5;
                else if (weight >= 5.2)
                    nutritionState = 6;
                break;
            case 54:
                if (weight < 3.5)
                    nutritionState = 1;
                else if (weight >= 3.5 && weight < 3.7)
                    nutritionState = 2;
                else if (weight >= 3.7 && weight < 5)
                    nutritionState = 3;
                else if (weight >= 5 && weight < 5.1)
                    nutritionState = 4;
                else if (weight >= 5.1 && weight < 5.5)
                    nutritionState = 5;
                else if (weight >= 5.5)
                    nutritionState = 6;
                break;
            case 55:
                if (weight < 3.7)
                    nutritionState = 1;
                else if (weight >= 3.7 && weight < 3.9)
                    nutritionState = 2;
                else if (weight >= 3.9 && weight < 5.2)
                    nutritionState = 3;
                else if (weight >= 5.2 && weight < 5.4)
                    nutritionState = 4;
                else if (weight >= 5.4 && weight < 5.8)
                    nutritionState = 5;
                else if (weight >= 5.8)
                    nutritionState = 6;
                break;
            case 56:
                if (weight < 4)
                    nutritionState = 1;
                else if (weight >= 4 && weight < 4.2)
                    nutritionState = 2;
                else if (weight >= 4.2 && weight < 5.5)
                    nutritionState = 3;
                else if (weight >= 5.5 && weight < 5.7)
                    nutritionState = 4;
                else if (weight >= 5.7 && weight < 6.1)
                    nutritionState = 5;
                else if (weight >= 6.1)
                    nutritionState = 6;
                break;
            case 57:
                if (weight < 4.2)
                    nutritionState = 1;
                else if (weight >= 4.2 && weight < 4.4)
                    nutritionState = 2;
                else if (weight >= 4.4 && weight < 5.8)
                    nutritionState = 3;
                else if (weight >= 5.8 && weight < 6)
                    nutritionState = 4;
                else if (weight >= 6 && weight < 6.4)
                    nutritionState = 5;
                else if (weight >= 6.4)
                    nutritionState = 6;
                break;
            case 58:
                if (weight < 4.4)
                    nutritionState = 1;
                else if (weight >= 4.4 && weight < 4.6)
                    nutritionState = 2;
                else if (weight >= 4.6 && weight < 6.1)
                    nutritionState = 3;
                else if (weight >= 6.1 && weight < 6.3)
                    nutritionState = 4;
                else if (weight >= 6.3 && weight < 6.7)
                    nutritionState = 5;
                else if (weight >= 6.7)
                    nutritionState = 6;
                break;
            case 59:
                if (weight < 4.6)
                    nutritionState = 1;
                else if (weight >= 4.6 && weight < 4.9)
                    nutritionState = 2;
                else if (weight >= 4.9 && weight < 6.4)
                    nutritionState = 3;
                else if (weight >= 6.4 && weight < 6.6)
                    nutritionState = 4;
                else if (weight >= 6.6 && weight < 7)
                    nutritionState = 5;
                else if (weight >= 7)
                    nutritionState = 6;
                break;
            case 60:
                if (weight < 4.8)
                    nutritionState = 1;
                else if (weight >= 4.8 && weight < 5.1)
                    nutritionState = 2;
                else if (weight >= 5.1 && weight < 6.6)
                    nutritionState = 3;
                else if (weight >= 6.6 && weight < 6.9)
                    nutritionState = 4;
                else if (weight >= 6.9 && weight < 7.4)
                    nutritionState = 5;
                else if (weight >= 7.4)
                    nutritionState = 6;
                break;
            case 61:
                if (weight < 5.1)
                    nutritionState = 1;
                else if (weight >= 5.1 && weight < 5.3)
                    nutritionState = 2;
                else if (weight >= 5.3 && weight < 6.9)
                    nutritionState = 3;
                else if (weight >= 6.9 && weight < 7.2)
                    nutritionState = 4;
                else if (weight >= 7.2 && weight < 7.7)
                    nutritionState = 5;
                else if (weight >= 7.7)
                    nutritionState = 6;
                break;
            case 62:
                if (weight < 5.3)
                    nutritionState = 1;
                else if (weight >= 5.3 && weight < 5.5)
                    nutritionState = 2;
                else if (weight >= 5.5 && weight < 7.2)
                    nutritionState = 3;
                else if (weight >= 7.2 && weight < 7.5)
                    nutritionState = 4;
                else if (weight >= 7.5 && weight < 8)
                    nutritionState = 5;
                else if (weight >= 8)
                    nutritionState = 6;
                break;
            case 63:
                if (weight < 5.5)
                    nutritionState = 1;
                else if (weight >= 5.5 && weight < 5.8)
                    nutritionState = 2;
                else if (weight >= 5.8 && weight < 7.5)
                    nutritionState = 3;
                else if (weight >= 7.5 && weight < 7.8)
                    nutritionState = 4;
                else if (weight >= 7.8 && weight < 8.3)
                    nutritionState = 5;
                else if (weight >= 8.3)
                    nutritionState = 6;
                break;
            case 64:
                if (weight < 5.7)
                    nutritionState = 1;
                else if (weight >= 5.7 && weight < 6)
                    nutritionState = 2;
                else if (weight >= 6 && weight < 7.8)
                    nutritionState = 3;
                else if (weight >= 7.8 && weight < 8)
                    nutritionState = 4;
                else if (weight >= 8 && weight < 8.6)
                    nutritionState = 5;
                else if (weight >= 8.6)
                    nutritionState = 6;
                break;
            case 65:
                if (weight < 5.9)
                    nutritionState = 1;
                else if (weight >= 5.9 && weight < 6.2)
                    nutritionState = 2;
                else if (weight >= 6.2 && weight < 8.1)
                    nutritionState = 3;
                else if (weight >= 8.1 && weight < 8.3)
                    nutritionState = 4;
                else if (weight >= 8.3 && weight < 8.9)
                    nutritionState = 5;
                else if (weight >= 8.9)
                    nutritionState = 6;
                break;
            case 66:
                if (weight < 6.2)
                    nutritionState = 1;
                else if (weight >= 6.2 && weight < 6.5)
                    nutritionState = 2;
                else if (weight >= 6.5 && weight < 8.3)
                    nutritionState = 3;
                else if (weight >= 8.3 && weight < 8.6)
                    nutritionState = 4;
                else if (weight >= 8.6 && weight < 9.2)
                    nutritionState = 5;
                else if (weight >= 9.2)
                    nutritionState = 6;
                break;
            case 67:
                if (weight < 6.4)
                    nutritionState = 1;
                else if (weight >= 6.4 && weight < 6.7)
                    nutritionState = 2;
                else if (weight >= 6.7 && weight < 8.6)
                    nutritionState = 3;
                else if (weight >= 8.6 && weight < 8.9)
                    nutritionState = 4;
                else if (weight >= 8.9 && weight < 9.5)
                    nutritionState = 5;
                else if (weight >= 9.5)
                    nutritionState = 6;
                break;
            case 68:
                if (weight < 6.6)
                    nutritionState = 1;
                else if (weight >= 6.6 && weight < 6.9)
                    nutritionState = 2;
                else if (weight >= 6.9 && weight < 8.9)
                    nutritionState = 3;
                else if (weight >= 8.9 && weight < 9.2)
                    nutritionState = 4;
                else if (weight >= 9.2 && weight < 9.8)
                    nutritionState = 5;
                else if (weight >= 9.8)
                    nutritionState = 6;
                break;
            case 69:
                if (weight < 6.8)
                    nutritionState = 1;
                else if (weight >= 6.8 && weight < 7.1)
                    nutritionState = 2;
                else if (weight >= 7.1 && weight < 9.1)
                    nutritionState = 3;
                else if (weight >= 9.1 && weight < 9.5)
                    nutritionState = 4;
                else if (weight >= 9.5 && weight < 10.1)
                    nutritionState = 5;
                else if (weight >= 10.1)
                    nutritionState = 6;
                break;
            case 70:
                if (weight < 7)
                    nutritionState = 1;
                else if (weight >= 7 && weight < 7.4)
                    nutritionState = 2;
                else if (weight >= 7.4 && weight < 9.4)
                    nutritionState = 3;
                else if (weight >= 9.4 && weight < 9.7)
                    nutritionState = 4;
                else if (weight >= 9.7 && weight < 10.4)
                    nutritionState = 5;
                else if (weight >= 10.4)
                    nutritionState = 6;
                break;
            case 71:
                if (weight < 7.2)
                    nutritionState = 1;
                else if (weight >= 7.2 && weight < 7.6)
                    nutritionState = 2;
                else if (weight >= 7.6 && weight < 9.7)
                    nutritionState = 3;
                else if (weight >= 9.7 && weight < 10)
                    nutritionState = 4;
                else if (weight >= 10 && weight < 10.6)
                    nutritionState = 5;
                else if (weight >= 10.6)
                    nutritionState = 6;
                break;
            case 72:
                if (weight < 7.4)
                    nutritionState = 1;
                else if (weight >= 7.4 && weight < 7.8)
                    nutritionState = 2;
                else if (weight >= 7.8 && weight < 9.9)
                    nutritionState = 3;
                else if (weight >= 9.9 && weight < 10.3)
                    nutritionState = 4;
                else if (weight >= 10.3 && weight < 10.9)
                    nutritionState = 5;
                else if (weight >= 10.9)
                    nutritionState = 6;
                break;
            case 73:
                if (weight < 7.7)
                    nutritionState = 1;
                else if (weight >= 7.7 && weight < 8)
                    nutritionState = 2;
                else if (weight >= 8 && weight < 10.2)
                    nutritionState = 3;
                else if (weight >= 10.2 && weight < 10.5)
                    nutritionState = 4;
                else if (weight >= 10.5 && weight < 11.2)
                    nutritionState = 5;
                else if (weight >= 11.2)
                    nutritionState = 6;
                break;
            case 74:
                if (weight < 7.9)
                    nutritionState = 1;
                else if (weight >= 7.9 && weight < 8.2)
                    nutritionState = 2;
                else if (weight >= 8.2 && weight < 10.5)
                    nutritionState = 3;
                else if (weight >= 10.5 && weight < 10.8)
                    nutritionState = 4;
                else if (weight >= 10.8 && weight < 11.5)
                    nutritionState = 5;
                else if (weight >= 11.5)
                    nutritionState = 6;
                break;
            case 75:
                if (weight < 8.1)
                    nutritionState = 1;
                else if (weight >= 8.1 && weight < 8.5)
                    nutritionState = 2;
                else if (weight >= 8.5 && weight < 10.7)
                    nutritionState = 3;
                else if (weight >= 10.7 && weight < 11.1)
                    nutritionState = 4;
                else if (weight >= 11.1 && weight < 11.8)
                    nutritionState = 5;
                else if (weight >= 11.8)
                    nutritionState = 6;
                break;
            case 76:
                if (weight < 8.3)
                    nutritionState = 1;
                else if (weight >= 8.3 && weight < 8.7)
                    nutritionState = 2;
                else if (weight >= 8.7 && weight < 10)
                    nutritionState = 3;
                else if (weight >= 10 && weight < 11.3)
                    nutritionState = 4;
                else if (weight >= 11.3 && weight < 12.1)
                    nutritionState = 5;
                else if (weight >= 12.1)
                    nutritionState = 6;
                break;
            case 77:
                if (weight < 8.5)
                    nutritionState = 1;
                else if (weight >= 8.5 && weight < 8.9)
                    nutritionState = 2;
                else if (weight >= 8.9 && weight < 11.2)
                    nutritionState = 3;
                else if (weight >= 11.2 && weight < 11.6)
                    nutritionState = 4;
                else if (weight >= 11.6 && weight < 12.4)
                    nutritionState = 5;
                else if (weight >= 12.4)
                    nutritionState = 6;
                break;
            case 78:
                if (weight < 8.7)
                    nutritionState = 1;
                else if (weight >= 8.7 && weight < 9.1)
                    nutritionState = 2;
                else if (weight >= 9.1 && weight < 11.5)
                    nutritionState = 3;
                else if (weight >= 11.5 && weight < 11.9)
                    nutritionState = 4;
                else if (weight >= 11.9 && weight < 12.7)
                    nutritionState = 5;
                else if (weight >= 12.7)
                    nutritionState = 6;
                break;
            case 79:
                if (weight < 9)
                    nutritionState = 1;
                else if (weight >= 9 && weight < 9.3)
                    nutritionState = 2;
                else if (weight >= 9.3 && weight < 11.8)
                    nutritionState = 3;
                else if (weight >= 11.8 && weight < 12.2)
                    nutritionState = 4;
                else if (weight >= 12.2 && weight < 13)
                    nutritionState = 5;
                else if (weight >= 13)
                    nutritionState = 6;
                break;
            case 80:
                if (weight < 9.2)
                    nutritionState = 1;
                else if (weight >= 9.2 && weight < 9.6)
                    nutritionState = 2;
                else if (weight >= 9.6 && weight < 12)
                    nutritionState = 3;
                else if (weight >= 12 && weight < 12.5)
                    nutritionState = 4;
                else if (weight >= 12.5 && weight < 13.3)
                    nutritionState = 5;
                else if (weight >= 13.3)
                    nutritionState = 6;
                break;
            case 81:
                if (weight < 9.4)
                    nutritionState = 1;
                else if (weight >= 9.4 && weight < 9.8)
                    nutritionState = 2;
                else if (weight >= 9.8 && weight < 12.3)
                    nutritionState = 3;
                else if (weight >= 12.3 && weight < 12.7)
                    nutritionState = 4;
                else if (weight >= 12.7 && weight < 13.6)
                    nutritionState = 5;
                else if (weight >= 13.6)
                    nutritionState = 6;
                break;
            case 82:
                if (weight < 9.6)
                    nutritionState = 1;
                else if (weight >= 9.6 && weight < 10)
                    nutritionState = 2;
                else if (weight >= 10 && weight < 12.6)
                    nutritionState = 3;
                else if (weight >= 12.6 && weight < 13)
                    nutritionState = 4;
                else if (weight >= 13 && weight < 13.9)
                    nutritionState = 5;
                else if (weight >= 13.9)
                    nutritionState = 6;
                break;
            case 83:
                if (weight < 9.9)
                    nutritionState = 1;
                else if (weight >= 9.9 && weight < 10.2)
                    nutritionState = 2;
                else if (weight >= 10.2 && weight < 12.8)
                    nutritionState = 3;
                else if (weight >= 12.8 && weight < 13.3)
                    nutritionState = 4;
                else if (weight >= 13.3 && weight < 14.2)
                    nutritionState = 5;
                else if (weight >= 14.2)
                    nutritionState = 6;
                break;
            case 84:
                if (weight < 10.1)
                    nutritionState = 1;
                else if (weight >= 10.1 && weight < 10.4)
                    nutritionState = 2;
                else if (weight >= 10.4 && weight < 13.1)
                    nutritionState = 3;
                else if (weight >= 13.1 && weight < 13.6)
                    nutritionState = 4;
                else if (weight >= 13.6 && weight < 14.6)
                    nutritionState = 5;
                else if (weight >= 14.6)
                    nutritionState = 6;
                break;
            case 85:
                if (weight < 10)
                    nutritionState = 1;
                else if (weight >= 10 && weight < 10.4)
                    nutritionState = 2;
                else if (weight >= 10.4 && weight < 14)
                    nutritionState = 3;
                else if (weight >= 14 && weight < 14.8)
                    nutritionState = 4;
                else if (weight >= 14.8 && weight < 16.3)
                    nutritionState = 5;
                else if (weight >= 16.3)
                    nutritionState = 6;
                break;
            case 86:
                if (weight < 10.2)
                    nutritionState = 1;
                else if (weight >= 10.2 && weight < 10.6)
                    nutritionState = 2;
                else if (weight >= 10.6 && weight < 14.2)
                    nutritionState = 3;
                else if (weight >= 14.2 && weight < 15)
                    nutritionState = 4;
                else if (weight >= 15 && weight < 16.5)
                    nutritionState = 5;
                else if (weight >= 16.5)
                    nutritionState = 6;
                break;
            case 87:
                if (weight < 10.4)
                    nutritionState = 1;
                else if (weight >= 10.4 && weight < 10.8)
                    nutritionState = 2;
                else if (weight >= 10.8 && weight < 14.4)
                    nutritionState = 3;
                else if (weight >= 14.4 && weight < 15.2)
                    nutritionState = 4;
                else if (weight >= 15.2 && weight < 16.7)
                    nutritionState = 5;
                else if (weight >= 16.7)
                    nutritionState = 6;
                break;
            case 88:
                if (weight < 10.6)
                    nutritionState = 1;
                else if (weight >= 10.6 && weight < 11)
                    nutritionState = 2;
                else if (weight >= 11 && weight < 14.7)
                    nutritionState = 3;
                else if (weight >= 14.7 && weight < 15.5)
                    nutritionState = 4;
                else if (weight >= 15.5 && weight < 17)
                    nutritionState = 5;
                else if (weight >= 17)
                    nutritionState = 6;
                break;
            case 89:
                if (weight < 10.9)
                    nutritionState = 1;
                else if (weight >= 10.9 && weight < 11.4)
                    nutritionState = 2;
                else if (weight >= 11.4 && weight < 15.1)
                    nutritionState = 3;
                else if (weight >= 15.1 && weight < 15.8)
                    nutritionState = 4;
                else if (weight >= 15.8 && weight < 17.2)
                    nutritionState = 5;
                else if (weight >= 17.2)
                    nutritionState = 6;
                break;
            case 90:
                if (weight < 11.1)
                    nutritionState = 1;
                else if (weight >= 11.1 && weight < 11.6)
                    nutritionState = 2;
                else if (weight >= 11.6 && weight < 15.3)
                    nutritionState = 3;
                else if (weight >= 15.3 && weight < 16)
                    nutritionState = 4;
                else if (weight >= 16 && weight < 17.4)
                    nutritionState = 5;
                else if (weight >= 17.4)
                    nutritionState = 6;
                break;
            case 91:
                if (weight < 11.3)
                    nutritionState = 1;
                else if (weight >= 11.3 && weight < 11.8)
                    nutritionState = 2;
                else if (weight >= 11.8 && weight < 15.6)
                    nutritionState = 3;
                else if (weight >= 15.6 && weight < 16.3)
                    nutritionState = 4;
                else if (weight >= 16.3 && weight < 17.7)
                    nutritionState = 5;
                else if (weight >= 17.7)
                    nutritionState = 6;
                break;
            case 92:
                if (weight < 11.5)
                    nutritionState = 1;
                else if (weight >= 11.5 && weight < 12)
                    nutritionState = 2;
                else if (weight >= 12 && weight < 15.9)
                    nutritionState = 3;
                else if (weight >= 15.9 && weight < 16.6)
                    nutritionState = 4;
                else if (weight >= 16.6 && weight < 18)
                    nutritionState = 5;
                else if (weight >= 18)
                    nutritionState = 6;
                break;
            case 93:
                if (weight < 11.7)
                    nutritionState = 1;
                else if (weight >= 11.7 && weight < 12.2)
                    nutritionState = 2;
                else if (weight >= 12 && weight < 16.1)
                    nutritionState = 3;
                else if (weight >= 16.1 && weight < 16.9)
                    nutritionState = 4;
                else if (weight >= 16.9 && weight < 18.4)
                    nutritionState = 5;
                else if (weight >= 18.4)
                    nutritionState = 6;
                break;
            case 94:
                if (weight < 12)
                    nutritionState = 1;
                else if (weight >= 12 && weight < 12.5)
                    nutritionState = 2;
                else if (weight >= 12.5 && weight < 16.5)
                    nutritionState = 3;
                else if (weight >= 16.5 && weight < 17.3)
                    nutritionState = 4;
                else if (weight >= 17.3 && weight < 18.8)
                    nutritionState = 5;
                else if (weight >= 18.8)
                    nutritionState = 6;
                break;
            case 95:
                if (weight < 12.2)
                    nutritionState = 1;
                else if (weight >= 12.2 && weight < 12.7)
                    nutritionState = 2;
                else if (weight >= 12.7 && weight < 16.8)
                    nutritionState = 3;
                else if (weight >= 16.8 && weight < 17.6)
                    nutritionState = 4;
                else if (weight >= 17.6 && weight < 19.2)
                    nutritionState = 5;
                else if (weight >= 19.2)
                    nutritionState = 6;
                break;
            case 96:
                if (weight < 12.4)
                    nutritionState = 1;
                else if (weight >= 12.4 && weight < 12.9)
                    nutritionState = 2;
                else if (weight >= 12.9 && weight < 17.1)
                    nutritionState = 3;
                else if (weight >= 17.1 && weight < 17.9)
                    nutritionState = 4;
                else if (weight >= 17.9 && weight < 19.5)
                    nutritionState = 5;
                else if (weight >= 19.5)
                    nutritionState = 6;
                break;
            case 97:
                if (weight < 12.6)
                    nutritionState = 1;
                else if (weight >= 12.6 && weight < 13.1)
                    nutritionState = 2;
                else if (weight >= 13.1 && weight < 17.4)
                    nutritionState = 3;
                else if (weight >= 17.4 && weight < 18.2)
                    nutritionState = 4;
                else if (weight >= 18.2 && weight < 19.8)
                    nutritionState = 5;
                else if (weight >= 19.8)
                    nutritionState = 6;
                break;
            case 98:
                if (weight < 12.8)
                    nutritionState = 1;
                else if (weight >= 12.8 && weight < 13.3)
                    nutritionState = 2;
                else if (weight >= 13.3 && weight < 17.7)
                    nutritionState = 3;
                else if (weight >= 17.7 && weight < 18.5)
                    nutritionState = 4;
                else if (weight >= 18.5 && weight < 20.2)
                    nutritionState = 5;
                else if (weight >= 20.2)
                    nutritionState = 6;
                break;
            case 99:
                if (weight < 13.1)
                    nutritionState = 1;
                else if (weight >= 13.1 && weight < 13.6)
                    nutritionState = 2;
                else if (weight >= 13.6 && weight < 18)
                    nutritionState = 3;
                else if (weight >= 18 && weight < 18.8)
                    nutritionState = 4;
                else if (weight >= 18.8 && weight < 20.5)
                    nutritionState = 5;
                else if (weight >= 20.5)
                    nutritionState = 6;
                break;
            case 100:
                if (weight < 13.3)
                    nutritionState = 1;
                else if (weight >= 13.3 && weight < 13.8)
                    nutritionState = 2;
                else if (weight >= 13.8 && weight < 18.3)
                    nutritionState = 3;
                else if (weight >= 18.3 && weight < 19.1)
                    nutritionState = 4;
                else if (weight >= 19.1 && weight < 20.8)
                    nutritionState = 5;
                else if (weight >= 20.8)
                    nutritionState = 6;
                break;
            case 101:
                if (weight < 13.5)
                    nutritionState = 1;
                else if (weight >= 13.5 && weight < 14)
                    nutritionState = 2;
                else if (weight >= 14 && weight < 18.5)
                    nutritionState = 3;
                else if (weight >= 18.5 && weight < 19.4)
                    nutritionState = 4;
                else if (weight >= 19.4 && weight < 21.2)
                    nutritionState = 5;
                else if (weight >= 21.2)
                    nutritionState = 6;
                break;
            case 102:
                if (weight < 13.8)
                    nutritionState = 1;
                else if (weight >= 13.8 && weight < 14.3)
                    nutritionState = 2;
                else if (weight >= 14.3 && weight < 18.8)
                    nutritionState = 3;
                else if (weight >= 18.8 && weight < 19.7)
                    nutritionState = 4;
                else if (weight >= 19.7 && weight < 21.5)
                    nutritionState = 5;
                else if (weight >= 21.5)
                    nutritionState = 6;
                break;
            case 103:
                if (weight < 14)
                    nutritionState = 1;
                else if (weight >= 14 && weight < 14.6)
                    nutritionState = 2;
                else if (weight >= 14.6 && weight < 19.1)
                    nutritionState = 3;
                else if (weight >= 19.1 && weight < 20)
                    nutritionState = 4;
                else if (weight >= 20 && weight < 21.8)
                    nutritionState = 5;
                else if (weight >= 21.8)
                    nutritionState = 6;
                break;
            case 104:
                if (weight < 14.2)
                    nutritionState = 1;
                else if (weight >= 14.2 && weight < 14.8)
                    nutritionState = 2;
                else if (weight >= 14.8 && weight < 19.5)
                    nutritionState = 3;
                else if (weight >= 19.5 && weight < 20.4)
                    nutritionState = 4;
                else if (weight >= 20.4 && weight < 22.3)
                    nutritionState = 5;
                else if (weight >= 22.3)
                    nutritionState = 6;
                break;
            case 105:
                if (weight < 14.5)
                    nutritionState = 1;
                else if (weight >= 14.5 && weight < 15.1)
                    nutritionState = 2;
                else if (weight >= 15.1 && weight < 19.8)
                    nutritionState = 3;
                else if (weight >= 19.8 && weight < 20.7)
                    nutritionState = 4;
                else if (weight >= 20.7 && weight < 22.6)
                    nutritionState = 5;
                else if (weight >= 22.6)
                    nutritionState = 6;
                break;
            case 106:
                if (weight < 14.6)
                    nutritionState = 1;
                else if (weight >= 14.6 && weight < 15.3)
                    nutritionState = 2;
                else if (weight >= 15.3 && weight < 20.2)
                    nutritionState = 3;
                else if (weight >= 20.2 && weight < 21.1)
                    nutritionState = 4;
                else if (weight >= 21.1 && weight < 23)
                    nutritionState = 5;
                else if (weight >= 23)
                    nutritionState = 6;
                break;
            case 107:
                if (weight < 14.9)
                    nutritionState = 1;
                else if (weight >= 14.9 && weight < 15.6)
                    nutritionState = 2;
                else if (weight >= 15.6 && weight < 20.6)
                    nutritionState = 3;
                else if (weight >= 20.6 && weight < 21.5)
                    nutritionState = 4;
                else if (weight >= 21.5 && weight < 23.5)
                    nutritionState = 5;
                else if (weight >= 23.5)
                    nutritionState = 6;
                break;
            case 108:
                if (weight < 15.1)
                    nutritionState = 1;
                else if (weight >= 15.1 && weight < 15.8)
                    nutritionState = 2;
                else if (weight >= 15.8 && weight < 20.9)
                    nutritionState = 3;
                else if (weight >= 20.9 && weight < 21.8)
                    nutritionState = 4;
                else if (weight >= 21.8 && weight < 23.8)
                    nutritionState = 5;
                else if (weight >= 23.8)
                    nutritionState = 6;
                break;
            case 109:
                if (weight < 15.4)
                    nutritionState = 1;
                else if (weight >= 15.4 && weight < 16.1)
                    nutritionState = 2;
                else if (weight >= 16.1 && weight < 21.2)
                    nutritionState = 3;
                else if (weight >= 21.2 && weight < 22.2)
                    nutritionState = 4;
                else if (weight >= 22.2 && weight < 24.3)
                    nutritionState = 5;
                else if (weight >= 24.3)
                    nutritionState = 6;
                break;
            case 110:
                if (weight < 15.7)
                    nutritionState = 1;
                else if (weight >= 15.7 && weight < 16.4)
                    nutritionState = 2;
                else if (weight >= 16.4 && weight < 21.7)
                    nutritionState = 3;
                else if (weight >= 21.7 && weight < 22.8)
                    nutritionState = 4;
                else if (weight >= 22.8 && weight < 24.9)
                    nutritionState = 5;
                else if (weight >= 24.9)
                    nutritionState = 6;
                break;
            case 111:
                if (weight < 16)
                    nutritionState = 1;
                else if (weight >= 16 && weight < 16.7)
                    nutritionState = 2;
                else if (weight >= 16.7 && weight < 21.1)
                    nutritionState = 3;
                else if (weight >= 21.1 && weight < 23.2)
                    nutritionState = 4;
                else if (weight >= 23.2 && weight < 25.3)
                    nutritionState = 5;
                else if (weight >= 25.3)
                    nutritionState = 6;
                break;
            case 112:
                if (weight < 16.2)
                    nutritionState = 1;
                else if (weight >= 16.2 && weight < 16.9)
                    nutritionState = 2;
                else if (weight >= 16.9 && weight < 22.4)
                    nutritionState = 3;
                else if (weight >= 22.4 && weight < 23.6)
                    nutritionState = 4;
                else if (weight >= 23.6 && weight < 25.8)
                    nutritionState = 5;
                else if (weight >= 25.8)
                    nutritionState = 6;
                break;
            case 113:
                if (weight < 16.5)
                    nutritionState = 1;
                else if (weight >= 16.5 && weight < 17.2)
                    nutritionState = 2;
                else if (weight >= 17.2 && weight < 22.9)
                    nutritionState = 3;
                else if (weight >= 22.9 && weight < 24.1)
                    nutritionState = 4;
                else if (weight >= 24.1 && weight < 26.4)
                    nutritionState = 5;
                else if (weight >= 26.4)
                    nutritionState = 6;
                break;
            case 114:
                if (weight < 16.8)
                    nutritionState = 1;
                else if (weight >= 16.8 && weight < 17.5)
                    nutritionState = 2;
                else if (weight >= 17.5 && weight < 23.4)
                    nutritionState = 3;
                else if (weight >= 23.4 && weight < 24.6)
                    nutritionState = 4;
                else if (weight >= 24.6 && weight < 27)
                    nutritionState = 5;
                else if (weight >= 27)
                    nutritionState = 6;
                break;
            case 115:
                if (weight < 17.1)
                    nutritionState = 1;
                else if (weight >= 17.1 && weight < 17.9)
                    nutritionState = 2;
                else if (weight >= 17.9 && weight < 23.9)
                    nutritionState = 3;
                else if (weight >= 23.9 && weight < 25.1)
                    nutritionState = 4;
                else if (weight >= 25.1 && weight < 27.5)
                    nutritionState = 5;
                else if (weight >= 27.5)
                    nutritionState = 6;
                break;
            case 116:
                if (weight < 17.4)
                    nutritionState = 1;
                else if (weight >= 17.4 && weight < 18.2)
                    nutritionState = 2;
                else if (weight >= 18.2 && weight < 24.3)
                    nutritionState = 3;
                else if (weight >= 24.3 && weight < 25.6)
                    nutritionState = 4;
                else if (weight >= 25.6 && weight < 28.2)
                    nutritionState = 5;
                else if (weight >= 28.2)
                    nutritionState = 6;
                break;
            case 117:
                if (weight < 17.7)
                    nutritionState = 1;
                else if (weight >= 17.7 && weight < 18.5)
                    nutritionState = 2;
                else if (weight >= 18.5 && weight < 24.9)
                    nutritionState = 3;
                else if (weight >= 24.9 && weight < 26.2)
                    nutritionState = 4;
                else if (weight >= 26.2 && weight < 28.9)
                    nutritionState = 5;
                else if (weight >= 28.9)
                    nutritionState = 6;
                break;
            case 118:
                if (weight < 18.1)
                    nutritionState = 1;
                else if (weight >= 18.1 && weight < 18.9)
                    nutritionState = 2;
                else if (weight >= 18.9 && weight < 25.3)
                    nutritionState = 3;
                else if (weight >= 25.3 && weight < 26.7)
                    nutritionState = 4;
                else if (weight >= 26.7 && weight < 29.5)
                    nutritionState = 5;
                else if (weight >= 29.5)
                    nutritionState = 6;
                break;
            case 119:
                if (weight < 18.4)
                    nutritionState = 1;
                else if (weight >= 18.4 && weight < 19.2)
                    nutritionState = 2;
                else if (weight >= 19.2 && weight < 25.9)
                    nutritionState = 3;
                else if (weight >= 25.9 && weight < 27.3)
                    nutritionState = 4;
                else if (weight >= 27.3 && weight < 30.2)
                    nutritionState = 5;
                else if (weight >= 30.2)
                    nutritionState = 6;
                break;
            case 120:
                if (weight < 18.7)
                    nutritionState = 1;
                else if (weight >= 18.7 && weight < 19.5)
                    nutritionState = 2;
                else if (weight >= 19.5 && weight < 26.5)
                    nutritionState = 3;
                else if (weight >= 26.5 && weight < 28)
                    nutritionState = 4;
                else if (weight >= 28 && weight < 30.9)
                    nutritionState = 5;
                else if (weight >= 30.9)
                    nutritionState = 6;
                break;
            case 121:
                if (weight < 19)
                    nutritionState = 1;
                else if (weight >= 19 && weight < 19.8)
                    nutritionState = 2;
                else if (weight >= 19.8 && weight < 27)
                    nutritionState = 3;
                else if (weight >= 27 && weight < 28.6)
                    nutritionState = 4;
                else if (weight >= 28.6 && weight < 31.7)
                    nutritionState = 5;
                else if (weight >= 31.7)
                    nutritionState = 6;
                break;
            case 122:
                if (weight < 19.4)
                    nutritionState = 1;
                else if (weight >= 19.4 && weight < 20.2)
                    nutritionState = 2;
                else if (weight >= 20.2 && weight < 27.6)
                    nutritionState = 3;
                else if (weight >= 27.6 && weight < 29.2)
                    nutritionState = 4;
                else if (weight >= 29.2 && weight < 32.4)
                    nutritionState = 5;
                else if (weight >= 32.4)
                    nutritionState = 6;
                break;
            case 123:
                if (weight < 19.7)
                    nutritionState = 1;
                else if (weight >= 19.7 && weight < 20.5)
                    nutritionState = 2;
                else if (weight >= 20.5 && weight < 28.1)
                    nutritionState = 3;
                else if (weight >= 28.1 && weight < 29.8)
                    nutritionState = 4;
                else if (weight >= 29.8 && weight < 33.1)
                    nutritionState = 5;
                else if (weight >= 33.1)
                    nutritionState = 6;
                break;
            case 124:
                if (weight < 20.1)
                    nutritionState = 1;
                else if (weight >= 20.1 && weight < 20.9)
                    nutritionState = 2;
                else if (weight >= 20.9 && weight < 28.8)
                    nutritionState = 3;
                else if (weight >= 28.8 && weight < 30.5)
                    nutritionState = 4;
                else if (weight >= 30.5 && weight < 34)
                    nutritionState = 5;
                else if (weight >= 34)
                    nutritionState = 6;
                break;
            case 125:
                if (weight < 20.4)
                    nutritionState = 1;
                else if (weight >= 20.4 && weight < 21.3)
                    nutritionState = 2;
                else if (weight >= 21.3 && weight < 29.4)
                    nutritionState = 3;
                else if (weight >= 29.4 && weight < 31.1)
                    nutritionState = 4;
                else if (weight >= 31.1 && weight < 34.6)
                    nutritionState = 5;
                else if (weight >= 34.6)
                    nutritionState = 6;
                break;
            case 126:
                if (weight < 20.8)
                    nutritionState = 1;
                else if (weight >= 20.8 && weight < 21.7)
                    nutritionState = 2;
                else if (weight >= 21.7 && weight < 30)
                    nutritionState = 3;
                else if (weight >= 30 && weight < 31.8)
                    nutritionState = 4;
                else if (weight >= 31.8 && weight < 35.4)
                    nutritionState = 5;
                else if (weight >= 35.4)
                    nutritionState = 6;
                break;
            case 127:
                if (weight < 21.2)
                    nutritionState = 1;
                else if (weight >= 21.2 && weight < 22.1)
                    nutritionState = 2;
                else if (weight >= 22.1 && weight < 30.8)
                    nutritionState = 3;
                else if (weight >= 30.8 && weight < 32.7)
                    nutritionState = 4;
                else if (weight >= 32.7 && weight < 36.4)
                    nutritionState = 5;
                else if (weight >= 36.4)
                    nutritionState = 6;
                break;
            case 128:
                if (weight < 21.5)
                    nutritionState = 1;
                else if (weight >= 21.5 && weight < 22.5)
                    nutritionState = 2;
                else if (weight >= 22.5 && weight < 31.4)
                    nutritionState = 3;
                else if (weight >= 31.4 && weight < 33.4)
                    nutritionState = 4;
                else if (weight >= 33.4 && weight < 37.2)
                    nutritionState = 5;
                else if (weight >= 37.2)
                    nutritionState = 6;
                break;
            case 129:
                if (weight < 21.9)
                    nutritionState = 1;
                else if (weight >= 21.9 && weight < 22.9)
                    nutritionState = 2;
                else if (weight >= 22.9 && weight < 32.2)
                    nutritionState = 3;
                else if (weight >= 32.2 && weight < 34.2)
                    nutritionState = 4;
                else if (weight >= 34.2 && weight < 38.2)
                    nutritionState = 5;
                else if (weight >= 38.2)
                    nutritionState = 6;
                break;
            case 130:
                if (weight < 22.2)
                    nutritionState = 1;
                else if (weight >= 22.2 && weight < 23.3)
                    nutritionState = 2;
                else if (weight >= 23.3 && weight < 33)
                    nutritionState = 3;
                else if (weight >= 33 && weight < 35.1)
                    nutritionState = 4;
                else if (weight >= 35.1 && weight < 39.3)
                    nutritionState = 5;
                else if (weight >= 39.3)
                    nutritionState = 6;
                break;
            case 131:
                if (weight < 22.6)
                    nutritionState = 1;
                else if (weight >= 22.6 && weight < 23.8)
                    nutritionState = 2;
                else if (weight >= 23.8 && weight < 33.8)
                    nutritionState = 3;
                else if (weight >= 33.8 && weight < 36)
                    nutritionState = 4;
                else if (weight >= 36 && weight < 40.4)
                    nutritionState = 5;
                else if (weight >= 40.4)
                    nutritionState = 6;
                break;
            case 132:
                if (weight < 23.1)
                    nutritionState = 1;
                else if (weight >= 23.1 && weight < 24.3)
                    nutritionState = 2;
                else if (weight >= 24.3 && weight < 34.7)
                    nutritionState = 3;
                else if (weight >= 34.7 && weight < 36.9)
                    nutritionState = 4;
                else if (weight >= 36.9 && weight < 41.5)
                    nutritionState = 5;
                else if (weight >= 41.5)
                    nutritionState = 6;
                break;
            case 133:
                if (weight < 23.5)
                    nutritionState = 1;
                else if (weight >= 23.5 && weight < 24.7)
                    nutritionState = 2;
                else if (weight >= 24.7 && weight < 35.5)
                    nutritionState = 3;
                else if (weight >= 35.5 && weight < 37.9)
                    nutritionState = 4;
                else if (weight >= 37.9 && weight < 42.6)
                    nutritionState = 5;
                else if (weight >= 42.6)
                    nutritionState = 6;
                break;
            case 134:
                if (weight < 23.9)
                    nutritionState = 1;
                else if (weight >= 23.9 && weight < 25.2)
                    nutritionState = 2;
                else if (weight >= 25.2 && weight < 36.3)
                    nutritionState = 3;
                else if (weight >= 36.3 && weight < 38.8)
                    nutritionState = 4;
                else if (weight >= 38.8 && weight < 43.7)
                    nutritionState = 5;
                else if (weight >= 43.7)
                    nutritionState = 6;
                break;
            case 135:
                if (weight < 24.4)
                    nutritionState = 1;
                else if (weight >= 24.4 && weight < 25.7)
                    nutritionState = 2;
                else if (weight >= 25.7 && weight < 37.2)
                    nutritionState = 3;
                else if (weight >= 37.2 && weight < 39.7)
                    nutritionState = 4;
                else if (weight >= 39.7 && weight < 44.8)
                    nutritionState = 5;
                else if (weight >= 44.8)
                    nutritionState = 6;
                break;
            case 136:
                if (weight < 24.8)
                    nutritionState = 1;
                else if (weight >= 24.8 && weight < 26.1)
                    nutritionState = 2;
                else if (weight >= 26.1 && weight < 38)
                    nutritionState = 3;
                else if (weight >= 38 && weight < 40.6)
                    nutritionState = 4;
                else if (weight >= 40.6 && weight < 45.8)
                    nutritionState = 5;
                else if (weight >= 45.8)
                    nutritionState = 6;
                break;
            case 137:
                if (weight < 25.3)
                    nutritionState = 1;
                else if (weight >= 25.3 && weight < 26.6)
                    nutritionState = 2;
                else if (weight >= 26.6 && weight < 38.9)
                    nutritionState = 3;
                else if (weight >= 38.9 && weight < 41.5)
                    nutritionState = 4;
                else if (weight >= 41.5 && weight < 46.9)
                    nutritionState = 5;
                else if (weight >= 46.9)
                    nutritionState = 6;
                break;
            case 138:
                if (weight < 25.7)
                    nutritionState = 1;
                else if (weight >= 25.7 && weight < 27.1)
                    nutritionState = 2;
                else if (weight >= 27.1 && weight < 39.7)
                    nutritionState = 3;
                else if (weight >= 39.7 && weight < 42.5)
                    nutritionState = 4;
                else if (weight >= 42.5 && weight < 47.9)
                    nutritionState = 5;
                else if (weight >= 47.9)
                    nutritionState = 6;
                break;
            case 139:
                if (weight < 26.2)
                    nutritionState = 1;
                else if (weight >= 26.2 && weight < 27.6)
                    nutritionState = 2;
                else if (weight >= 27.6 && weight < 40.5)
                    nutritionState = 3;
                else if (weight >= 40.5 && weight < 43.3)
                    nutritionState = 4;
                else if (weight >= 43.3 && weight < 48.8)
                    nutritionState = 5;
                else if (weight >= 48.8)
                    nutritionState = 6;
                break;
            case 140:
                if (weight < 26.6)
                    nutritionState = 1;
                else if (weight >= 26.6 && weight < 28.2)
                    nutritionState = 2;
                else if (weight >= 28.2 && weight < 41.3)
                    nutritionState = 3;
                else if (weight >= 41.3 && weight < 44.2)
                    nutritionState = 4;
                else if (weight >= 44.2 && weight < 49.9)
                    nutritionState = 5;
                else if (weight >= 49.9)
                    nutritionState = 6;
                break;
            case 141:
                if (weight < 27.1)
                    nutritionState = 1;
                else if (weight >= 27.1 && weight < 28.7)
                    nutritionState = 2;
                else if (weight >= 28.7 && weight < 42.1)
                    nutritionState = 3;
                else if (weight >= 42.1 && weight < 45)
                    nutritionState = 4;
                else if (weight >= 45 && weight < 50.8)
                    nutritionState = 5;
                else if (weight >= 50.8)
                    nutritionState = 6;
                break;
            case 142:
                if (weight < 27.6)
                    nutritionState = 1;
                else if (weight >= 27.6 && weight < 29.2)
                    nutritionState = 2;
                else if (weight >= 29.2 && weight < 42.9)
                    nutritionState = 3;
                else if (weight >= 42.9 && weight < 45.8)
                    nutritionState = 4;
                else if (weight >= 45.8 && weight < 51.6)
                    nutritionState = 5;
                else if (weight >= 51.6)
                    nutritionState = 6;
                break;
            case 143:
                if (weight < 28.1)
                    nutritionState = 1;
                else if (weight >= 28.1 && weight < 29.7)
                    nutritionState = 2;
                else if (weight >= 29.7 && weight < 43.6)
                    nutritionState = 3;
                else if (weight >= 43.6 && weight < 46.6)
                    nutritionState = 4;
                else if (weight >= 46.6 && weight < 52.6)
                    nutritionState = 5;
                else if (weight >= 52.6)
                    nutritionState = 6;
                break;
            case 144:
                if (weight < 28.6)
                    nutritionState = 1;
                else if (weight >= 28.6 && weight < 30.3)
                    nutritionState = 2;
                else if (weight >= 30.3 && weight < 44.4)
                    nutritionState = 3;
                else if (weight >= 44.4 && weight < 47.4)
                    nutritionState = 4;
                else if (weight >= 47.4 && weight < 53.5)
                    nutritionState = 5;
                else if (weight >= 53.5)
                    nutritionState = 6;
                break;
            case 145:
                if (weight < 29.2)
                    nutritionState = 1;
                else if (weight >= 29.2 && weight < 30.9)
                    nutritionState = 2;
                else if (weight >= 30.9 && weight < 45.3)
                    nutritionState = 3;
                else if (weight >= 45.3 && weight < 48.3)
                    nutritionState = 4;
                else if (weight >= 48.3 && weight < 54.5)
                    nutritionState = 5;
                else if (weight >= 54.5)
                    nutritionState = 6;
                break;
            case 146:
                if (weight < 29.7)
                    nutritionState = 1;
                else if (weight >= 29.7 && weight < 31.4)
                    nutritionState = 2;
                else if (weight >= 31.4 && weight < 46)
                    nutritionState = 3;
                else if (weight >= 46 && weight < 49.1)
                    nutritionState = 4;
                else if (weight >= 49.1 && weight < 55.4)
                    nutritionState = 5;
                else if (weight >= 55.4)
                    nutritionState = 6;
                break;
            case 147:
                if (weight < 30.3)
                    nutritionState = 1;
                else if (weight >= 30.3 && weight < 32)
                    nutritionState = 2;
                else if (weight >= 32 && weight < 46.8)
                    nutritionState = 3;
                else if (weight >= 46.8 && weight < 50)
                    nutritionState = 4;
                else if (weight >= 50 && weight < 56.3)
                    nutritionState = 5;
                else if (weight >= 56.3)
                    nutritionState = 6;
                break;
            case 148:
                if (weight < 30.9)
                    nutritionState = 1;
                else if (weight >= 30.9 && weight < 32.6)
                    nutritionState = 2;
                else if (weight >= 32.6 && weight < 47.7)
                    nutritionState = 3;
                else if (weight >= 47.7 && weight < 50.9)
                    nutritionState = 4;
                else if (weight >= 50.9 && weight < 57.3)
                    nutritionState = 5;
                else if (weight >= 57.3)
                    nutritionState = 6;
                break;
            case 149:
                if (weight < 31.5)
                    nutritionState = 1;
                else if (weight >= 31.5 && weight < 33.3)
                    nutritionState = 2;
                else if (weight >= 33.3 && weight < 48.5)
                    nutritionState = 3;
                else if (weight >= 48.5 && weight < 51.7)
                    nutritionState = 4;
                else if (weight >= 51.7 && weight < 58.1)
                    nutritionState = 5;
                else if (weight >= 58.1)
                    nutritionState = 6;
                break;
            case 150:
                if (weight < 32.1)
                    nutritionState = 1;
                else if (weight >= 32.1 && weight < 33.9)
                    nutritionState = 2;
                else if (weight >= 33.9 && weight < 49.2)
                    nutritionState = 3;
                else if (weight >= 49.2 && weight < 52.5)
                    nutritionState = 4;
                else if (weight >= 52.5 && weight < 59)
                    nutritionState = 5;
                else if (weight >= 59)
                    nutritionState = 6;
                break;
            case 151:
                if (weight < 32.8)
                    nutritionState = 1;
                else if (weight >= 32.8 && weight < 34.6)
                    nutritionState = 2;
                else if (weight >= 34.6 && weight < 50)
                    nutritionState = 3;
                else if (weight >= 50 && weight < 53.3)
                    nutritionState = 4;
                else if (weight >= 53.3 && weight < 59.8)
                    nutritionState = 5;
                else if (weight >= 59.8)
                    nutritionState = 6;
                break;
            case 152:
                if (weight < 33.3)
                    nutritionState = 1;
                else if (weight >= 33.3 && weight < 35.2)
                    nutritionState = 2;
                else if (weight >= 35.2 && weight < 50.8)
                    nutritionState = 3;
                else if (weight >= 50.8 && weight < 51.4)
                    nutritionState = 4;
                else if (weight >= 51.4 && weight < 60.6)
                    nutritionState = 5;
                else if (weight >= 60.6)
                    nutritionState = 6;
                break;
            case 153:
                if (weight < 34)
                    nutritionState = 1;
                else if (weight >= 34 && weight < 36)
                    nutritionState = 2;
                else if (weight >= 36 && weight < 51.6)
                    nutritionState = 3;
                else if (weight >= 51.6 && weight < 54.9)
                    nutritionState = 4;
                else if (weight >= 54.9 && weight < 61.4)
                    nutritionState = 5;
                else if (weight >= 61.4)
                    nutritionState = 6;
                break;
            case 154:
                if (weight < 34.6)
                    nutritionState = 1;
                else if (weight >= 34.6 && weight < 36.6)
                    nutritionState = 2;
                else if (weight >= 36.6 && weight < 52.4)
                    nutritionState = 3;
                else if (weight >= 52.4 && weight < 55.7)
                    nutritionState = 4;
                else if (weight >= 55.7 && weight < 62.2)
                    nutritionState = 5;
                else if (weight >= 62.2)
                    nutritionState = 6;
                break;
            case 155:
                if (weight < 35.3)
                    nutritionState = 1;
                else if (weight >= 35.3 && weight < 37.3)
                    nutritionState = 2;
                else if (weight >= 37.3 && weight < 53.2)
                    nutritionState = 3;
                else if (weight >= 53.2 && weight < 56.5)
                    nutritionState = 4;
                else if (weight >= 56.5 && weight < 63)
                    nutritionState = 5;
                else if (weight >= 63)
                    nutritionState = 6;
                break;
            case 156:
                if (weight < 36)
                    nutritionState = 1;
                else if (weight >= 36 && weight < 38.1)
                    nutritionState = 2;
                else if (weight >= 38.1 && weight < 54.1)
                    nutritionState = 3;
                else if (weight >= 54.1 && weight < 57.3)
                    nutritionState = 4;
                else if (weight >= 57.3 && weight < 63.7)
                    nutritionState = 5;
                else if (weight >= 63.7)
                    nutritionState = 6;
                break;
            case 157:
                if (weight < 36.7)
                    nutritionState = 1;
                else if (weight >= 36.7 && weight < 38.8)
                    nutritionState = 2;
                else if (weight >= 38.8 && weight < 54.9)
                    nutritionState = 3;
                else if (weight >= 54.9 && weight < 58.1)
                    nutritionState = 4;
                else if (weight >= 58.1 && weight < 64.4)
                    nutritionState = 5;
                else if (weight >= 64.4)
                    nutritionState = 6;
                break;
            case 158:
                if (weight < 37.4)
                    nutritionState = 1;
                else if (weight >= 37.4 && weight < 39.6)
                    nutritionState = 2;
                else if (weight >= 39.6 && weight < 55.7)
                    nutritionState = 3;
                else if (weight >= 55.7 && weight < 58.9)
                    nutritionState = 4;
                else if (weight >= 58.9 && weight < 65.2)
                    nutritionState = 5;
                else if (weight >= 65.2)
                    nutritionState = 6;
                break;
            case 159:
                if (weight < 38.1)
                    nutritionState = 1;
                else if (weight >= 38.1 && weight < 40.3)
                    nutritionState = 2;
                else if (weight >= 40.3 && weight < 56.6)
                    nutritionState = 3;
                else if (weight >= 56.6 && weight < 59.8)
                    nutritionState = 4;
                else if (weight >= 59.8 && weight < 66.1)
                    nutritionState = 5;
                else if (weight >= 66.1)
                    nutritionState = 6;
                break;
            case 160:
                if (weight < 38.7)
                    nutritionState = 1;
                else if (weight >= 38.7 && weight < 41.1)
                    nutritionState = 2;
                else if (weight >= 41.1 && weight < 57.4)
                    nutritionState = 3;
                else if (weight >= 57.4 && weight < 60.5)
                    nutritionState = 4;
                else if (weight >= 60.5 && weight < 66.8)
                    nutritionState = 5;
                else if (weight >= 66.8)
                    nutritionState = 6;
                break;
            case 161:
                if (weight < 39.5)
                    nutritionState = 1;
                else if (weight >= 39.5 && weight < 41.9)
                    nutritionState = 2;
                else if (weight >= 41.9 && weight < 58.3)
                    nutritionState = 3;
                else if (weight >= 58.3 && weight < 61.3)
                    nutritionState = 4;
                else if (weight >= 61.3 && weight < 67.5)
                    nutritionState = 5;
                else if (weight >= 67.5)
                    nutritionState = 6;
                break;
            case 162:
                if (weight < 40.2)
                    nutritionState = 1;
                else if (weight >= 40.2 && weight < 42.6)
                    nutritionState = 2;
                else if (weight >= 42.6 && weight < 59.1)
                    nutritionState = 3;
                else if (weight >= 59.1 && weight < 62.1)
                    nutritionState = 4;
                else if (weight >= 62.1 && weight < 68.2)
                    nutritionState = 5;
                else if (weight >= 68.2)
                    nutritionState = 6;
                break;
            case 163:
                if (weight < 40.9)
                    nutritionState = 1;
                else if (weight >= 40.9 && weight < 43.4)
                    nutritionState = 2;
                else if (weight >= 43.4 && weight < 59.9)
                    nutritionState = 3;
                else if (weight >= 59.9 && weight < 62.9)
                    nutritionState = 4;
                else if (weight >= 62.9 && weight < 68.9)
                    nutritionState = 5;
                else if (weight >= 68.9)
                    nutritionState = 6;
                break;
            case 164:
                if (weight < 41.6)
                    nutritionState = 1;
                else if (weight >= 41.6 && weight < 44.1)
                    nutritionState = 2;
                else if (weight >= 44.1 && weight < 60.7)
                    nutritionState = 3;
                else if (weight >= 60.7 && weight < 63.7)
                    nutritionState = 4;
                else if (weight >= 63.7 && weight < 69.7)
                    nutritionState = 5;
                else if (weight >= 69.7)
                    nutritionState = 6;
                break;
            case 165:
                if (weight < 42.3)
                    nutritionState = 1;
                else if (weight >= 42.3 && weight < 44.9)
                    nutritionState = 2;
                else if (weight >= 44.9 && weight < 61.6)
                    nutritionState = 3;
                else if (weight >= 61.6 && weight < 64.5)
                    nutritionState = 4;
                else if (weight >= 64.5 && weight < 70.3)
                    nutritionState = 5;
                else if (weight >= 70.3)
                    nutritionState = 6;
                break;
            case 166:
                if (weight < 43.1)
                    nutritionState = 1;
                else if (weight >= 43.1 && weight < 45.7)
                    nutritionState = 2;
                else if (weight >= 45.7 && weight < 62.4)
                    nutritionState = 3;
                else if (weight >= 62.4 && weight < 65.3)
                    nutritionState = 4;
                else if (weight >= 65.3 && weight < 71.1)
                    nutritionState = 5;
                else if (weight >= 71.1)
                    nutritionState = 6;
                break;
            case 167:
                if (weight < 43.8)
                    nutritionState = 1;
                else if (weight >= 43.8 && weight < 46.4)
                    nutritionState = 2;
                else if (weight >= 46.4 && weight < 63.2)
                    nutritionState = 3;
                else if (weight >= 63.2 && weight < 66.1)
                    nutritionState = 4;
                else if (weight >= 66.1 && weight < 71.8)
                    nutritionState = 5;
                else if (weight >= 71.8)
                    nutritionState = 6;
                break;
            case 168:
                if (weight < 44.5)
                    nutritionState = 1;
                else if (weight >= 44.5 && weight < 47.2)
                    nutritionState = 2;
                else if (weight >= 47.2 && weight < 64)
                    nutritionState = 3;
                else if (weight >= 64 && weight < 66.9)
                    nutritionState = 4;
                else if (weight >= 66.9 && weight < 72.6)
                    nutritionState = 5;
                else if (weight >= 72.6)
                    nutritionState = 6;
                break;
            case 169:
                if (weight < 45.1)
                    nutritionState = 1;
                else if (weight >= 45.1 && weight < 47.9)
                    nutritionState = 2;
                else if (weight >= 47.9 && weight < 64.8)
                    nutritionState = 3;
                else if (weight >= 64.8 && weight < 67.7)
                    nutritionState = 4;
                else if (weight >= 67.7 && weight < 73.4)
                    nutritionState = 5;
                else if (weight >= 73.4)
                    nutritionState = 6;
                break;
            case 170:
                if (weight < 45.8)
                    nutritionState = 1;
                else if (weight >= 45.8 && weight < 48.6)
                    nutritionState = 2;
                else if (weight >= 48.6 && weight < 65.6)
                    nutritionState = 3;
                else if (weight >= 65.6 && weight < 68.4)
                    nutritionState = 4;
                else if (weight >= 68.4 && weight < 73.9)
                    nutritionState = 5;
                else if (weight >= 73.9)
                    nutritionState = 6;
                break;
            case 171:
                if (weight < 46.5)
                    nutritionState = 1;
                else if (weight >= 46.5 && weight < 49.4)
                    nutritionState = 2;
                else if (weight >= 49.4 && weight < 66.4)
                    nutritionState = 3;
                else if (weight >= 66.4 && weight < 69.2)
                    nutritionState = 4;
                else if (weight >= 69.2 && weight < 74.7)
                    nutritionState = 5;
                else if (weight >= 74.7)
                    nutritionState = 6;
                break;
            case 172:
                if (weight < 47.2)
                    nutritionState = 1;
                else if (weight >= 47.2 && weight < 50.1)
                    nutritionState = 2;
                else if (weight >= 50.1 && weight < 67.1)
                    nutritionState = 3;
                else if (weight >= 67.1 && weight < 69.9)
                    nutritionState = 4;
                else if (weight >= 69.9 && weight < 75.4)
                    nutritionState = 5;
                else if (weight >= 75.4)
                    nutritionState = 6;
                break;
            case 173:
                if (weight < 48)
                    nutritionState = 1;
                else if (weight >= 48 && weight < 50.9)
                    nutritionState = 2;
                else if (weight >= 50.9 && weight < 67.8)
                    nutritionState = 3;
                else if (weight >= 67.8 && weight < 70.6)
                    nutritionState = 4;
                else if (weight >= 70.6 && weight < 76)
                    nutritionState = 5;
                else if (weight >= 76)
                    nutritionState = 6;
                break;
            case 174:
                if (weight < 48.7)
                    nutritionState = 1;
                else if (weight >= 48.7 && weight < 51.6)
                    nutritionState = 2;
                else if (weight >= 51.6 && weight < 68.6)
                    nutritionState = 3;
                else if (weight >= 68.6 && weight < 71.2)
                    nutritionState = 4;
                else if (weight >= 71.2 && weight < 76.6)
                    nutritionState = 5;
                else if (weight >= 76.6)
                    nutritionState = 6;
                break;
            case 175:
                if (weight < 49.5)
                    nutritionState = 1;
                else if (weight >= 49.5 && weight < 52.4)
                    nutritionState = 2;
                else if (weight >= 52.4 && weight < 69.3)
                    nutritionState = 3;
                else if (weight >= 69.3 && weight < 71.9)
                    nutritionState = 4;
                else if (weight >= 71.9 && weight < 77.3)
                    nutritionState = 5;
                else if (weight >= 77.3)
                    nutritionState = 6;
                break;
            case 176:
                if (weight < 50.2)
                    nutritionState = 1;
                else if (weight >= 50.2 && weight < 53.1)
                    nutritionState = 2;
                else if (weight >= 53.1 && weight < 70)
                    nutritionState = 3;
                else if (weight >= 70 && weight < 72.6)
                    nutritionState = 4;
                else if (weight >= 72.6 && weight < 78)
                    nutritionState = 5;
                else if (weight >= 78)
                    nutritionState = 6;
                break;
            case 177:
                if (weight < 51)
                    nutritionState = 1;
                else if (weight >= 51 && weight < 53.9)
                    nutritionState = 2;
                else if (weight >= 53.9 && weight < 70.7)
                    nutritionState = 3;
                else if (weight >= 70.7 && weight < 73.3)
                    nutritionState = 4;
                else if (weight >= 73.3 && weight < 78.7)
                    nutritionState = 5;
                else if (weight >= 78.7)
                    nutritionState = 6;
                break;
            case 178:
                if (weight < 51.8)
                    nutritionState = 1;
                else if (weight >= 51.8 && weight < 54.7)
                    nutritionState = 2;
                else if (weight >= 54.7 && weight < 71.3)
                    nutritionState = 3;
                else if (weight >= 71.3 && weight < 73.9)
                    nutritionState = 4;
                else if (weight >= 73.9 && weight < 79.2)
                    nutritionState = 5;
                else if (weight >= 79.2)
                    nutritionState = 6;
                break;
            case 179:
                if (weight < 52.6)
                    nutritionState = 1;
                else if (weight >= 52.6 && weight < 55.5)
                    nutritionState = 2;
                else if (weight >= 55.5 && weight < 72)
                    nutritionState = 3;
                else if (weight >= 72 && weight < 74.6)
                    nutritionState = 4;
                else if (weight >= 74.6 && weight < 79.9)
                    nutritionState = 5;
                else if (weight >= 79.9)
                    nutritionState = 6;
                break;
            case 180:
                if (weight < 53.4)
                    nutritionState = 1;
                else if (weight >= 53.4 && weight < 56.2)
                    nutritionState = 2;
                else if (weight >= 56.2 && weight < 72.5)
                    nutritionState = 3;
                else if (weight >= 72.5 && weight < 75.1)
                    nutritionState = 4;
                else if (weight >= 75.1 && weight < 80.3)
                    nutritionState = 5;
                else if (weight >= 80.3)
                    nutritionState = 6;
                break;
            default:
                nutritionState = 0;
        }
        return nutritionState;
    }

    // ======================

} // end class Setup
