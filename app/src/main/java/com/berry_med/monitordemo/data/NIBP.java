package com.berry_med.monitordemo.data;

import th.in.ffc.util.Log;

/**
 * Created by ZXX on 2016/8/3.
 */

public class NIBP {
    // Add constants for status codes
    public static final int STATUS_FINISHED = 0;
    public static final int STATUS_TESTING = 1;
    public static final int STATUS_STOPPED = 2;
    public static final int STATUS_PRESSURE_HIGH = 3;
    public static final int STATUS_CUFF_LOOSE = 4;
    public static final int STATUS_TIME_TOO_LONG = 5;
    public static final int STATUS_ERROR = 6;
    public static final int STATUS_DISTURB = 7;
    public static final int STATUS_NO_RESULT = 8;
    public static final int STATUS_INIT = 9;
    public static final int STATUS_INIT_FINISHED = 10;
    public int HIGH_PRESSURE_INVALID = 0;
    public int LOW_PRESSURE_INVALID  = 0;

    private int highPressure;
    private int meanPressure;
    private int lowPressure;
    private int cuffPressure;
    private int status;
    // Add human-readable status message
    public String getStatusMessage() {
        int codeValue = (status >> 2) & 0x0F; // Extract bits 2-5
        switch(codeValue) {
            case STATUS_FINISHED: return "Measurement complete";
            case STATUS_TESTING: return "Measuring...";
            case STATUS_STOPPED: return "Measurement stopped";
            case STATUS_PRESSURE_HIGH: return "Pressure too high";
            case STATUS_CUFF_LOOSE: return "Cuff too loose";
            case STATUS_TIME_TOO_LONG: return "Measurement time too long";
            case STATUS_ERROR: return "Measurement error";
            case STATUS_DISTURB: return "Signal disturbed";
            case STATUS_NO_RESULT: return "Unable to determine result";
            case STATUS_INIT: return "Initializing";
            case STATUS_INIT_FINISHED: return "Ready";
            default: return "Unknown status";
        }
    }

    public NIBP(int highPressure, int meanPressure, int lowPressure, int cuffPressure, int status) {
        this.highPressure = highPressure;
        this.meanPressure = meanPressure;
        this.lowPressure = lowPressure;
        this.cuffPressure = cuffPressure;
        this.status = status;
        Log.d("NIBP====>","highPressure:"+highPressure
                +", meanPressure:"+meanPressure
                +", lowPressure:"+lowPressure
                +", cuffPressure:"+cuffPressure
                +", status:"+status
        );
    }

    public int getMeanPressure() {
        return meanPressure;
    }

    public int getHighPressure() {
        return highPressure;
    }

    public int getLowPressure() {
        return lowPressure;
    }

    public int getCuffPressure() {
        return cuffPressure;
    }

    public int getStatus() {
        return status;
    }

//    @Override
//    public String toString() {
//        return  "Cuff:" +  (cuffPressure!=0 ? cuffPressure: "- -") + "\r\n" +
//                "High:"+  (highPressure!=0 ? highPressure: "- -") +
//                " Low:" +  (lowPressure !=0 ? lowPressure : "- -") +
//                " Mean:"+  (meanPressure!=0 ? meanPressure: "- -");
//    }
    @Override
    public String toString() {
        String statusMsg = getStatusMessage();
        if (((status >> 2) & 0x0F) != STATUS_FINISHED) {
            return "Cuff:" + (cuffPressure != 0 ? cuffPressure : "- -") + "\r\n" + statusMsg;
        }
        return "Cuff:" + (cuffPressure != 0 ? cuffPressure : "- -") + "\r\n" +
                "High:" + (highPressure != 0 ? highPressure : "- -") +
                " Low:" + (lowPressure != 0 ? lowPressure : "- -") +
                " Mean:" + (meanPressure != 0 ? meanPressure : "- -");
    }
}
