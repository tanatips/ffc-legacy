/* ***********************************************************************
 *                                                                 _ _ _
 *                                                               ( _ _  |
 *                                                           _ _ _ _  | |
 *                                                          (_ _ _  | |_|
 *  _     _   _ _ _ _     _ _ _   _ _ _ _ _   _ _ _ _     _ _ _   | | 
 * |  \  | | |  _ _ _|   /  _ _| |_ _   _ _| |  _ _ _|   /  _ _|  | |
 * | | \ | | | |_ _ _   /  /         | |     | |_ _ _   /  /      |_|
 * | |\ \| | |  _ _ _| (  (          | |     |  _ _ _| (  (    
 * | | \ | | | |_ _ _   \  \_ _      | |     | |_ _ _   \  \_ _ 
 * |_|  \__| |_ _ _ _|   \_ _ _|     |_|     |_ _ _ _|   \_ _ _| 
 *  a member of NSTDA, @Thailand
 *  
 * ***********************************************************************
 *
 *
 * FFC-Plus Project
 *
 * Copyright (C) 2010-2012 National Electronics and Computer Technology Center
 * All Rights Reserved.
 * 
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 * 
 */

package th.in.ffc.security;

import th.in.ffc.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Custom encryption algorithm for balance about Time and Security
 *
 * @author Nuuoeiz R. Lukmuang
 *         <p/>
 *         <<<<<<< HEAD
 * @version 1.0
 * @since 1.0
 * >>>>>>> a2f84ac756f5c1a54281f81b0283942d2a95f780
 */
public class TheBoysAlgorithm {

    public static final int KEY_LENGTH = 262144;
    private String Key = "";


    TheBoysAlgorithm(String key) {

        String key2;
        MessageDigest md;
        if (key.length() <= 8) {
//	    MessageDigester MD_5 = new MessageDigester();
//	    try {
//		key = MD_5.getMd5String(key);
//	    } catch (NoSuchAlgorithmException e) {
//		e.printStackTrace();
//	    }

            throw new IllegalArgumentException("Key too short!");
        }

        byte[] byteData = null;
        try {

            md = MessageDigest.getInstance("SHA-512");
            md.update(key.getBytes());

            byteData = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            String hex = Integer.toHexString(0xff & byteData[i]);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        key2 = hexString.toString();
        // Log.w("FFC", "key2 = "+key2.length());
        Key = key2;
        for (int i = 0; i <= 10; i++) {
            Key = Key + Key;
            // Log.w("FFC", "KEY_NEW "+i+Key);
        }
        Log.e("Boys", "Key Lenght = " + Key.getBytes().length);
    }

    public byte[] encryptByte(byte[] in) {
        byte[] keyByte = Key.getBytes();
        byte[] out = new byte[keyByte.length];
        for (int i = 0; i < keyByte.length; i++) {
            out[i] = (byte) (in[i] ^ keyByte[i]);
        }
        return out;
    }

}
