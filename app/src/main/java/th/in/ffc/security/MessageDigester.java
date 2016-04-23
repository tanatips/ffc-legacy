/**
 * **********************************************************************
 * _ _ _
 * ( _ _  |
 * _ _ _ _  | |
 * (_ _ _  | |_|
 * _     _   _ _ _ _     _ _ _   _ _ _ _ _   _ _ _ _     _ _ _   | |
 * |  \  | | |  _ _ _|   /  _ _| |_ _   _ _| |  _ _ _|   /  _ _|  | |
 * | | \ | | | |_ _ _   /  /         | |     | |_ _ _   /  /      |_|
 * | |\ \| | |  _ _ _| (  (          | |     |  _ _ _| (  (
 * | | \ | | | |_ _ _   \  \_ _      | |     | |_ _ _   \  \_ _
 * |_|  \__| |_ _ _ _|   \_ _ _|     |_|     |_ _ _ _|   \_ _ _|
 * a member of NSTDA, @Thailand
 * <p/>
 * ***********************************************************************
 * <p/>
 * <p/>
 * FFC-Plus Project
 * <p/>
 * Copyright (C) 2010-2012 National Electronics and Computer Technology Center
 * All Rights Reserved.
 * <p/>
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package th.in.ffc.security;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * Helper Class with static method for manage about Hash encryption suck as MD5
 * or SHA-256
 *
 * @version 1.0
 * @author Piruin Panichphol
 *
 * @since Family Folder Collector 1.75
 *
 */
public class MessageDigester {

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static byte[] getSha256(String input) {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-256");
            md.update(input.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getSha256String(String input) {
        byte[] bSha256 = getSha256(input);
        if (bSha256 != null) {

            return Base64.encodeToString(bSha256, Base64.DEFAULT).trim();
        } else {
            return null;
        }
    }

    public static byte[] getMD5(String input) {
        try {
            byte[] bytesOfMessage = input.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(bytesOfMessage);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }

    }


    public static String getMd5String(String text) {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("MD5");
            byte[] md5hash = new byte[32];
            md.update(text.getBytes(), 0, text.length());
            md5hash = md.digest();
            return convertToHex(md5hash).trim();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

}
