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

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Helper Class for manage about cryptography version 2 - add ProgessListener
 * for tracking operation
 *
 * @author Piruin Panichphol	1.0-1.5
 * @author Nuuoeiz R. Lukmuang	0.9
 * @version 1.5
 * @since Family Folder Collector 1.5
 */
public class Cryptographer {
    Cipher ecipher;
    Cipher dcipher;


    private boolean stop = false;
    private static final String KEY = "wis@nectec";

    public static final int ALGORITHM_AES = 0;
    public static final int ALGORITHM_DES = 1;
    @Deprecated
    public static final int ALGORITHM_TEA = 2;
    public static final int ALGORITHM_FFC = 3;
    public static final int ALGORITHM_BWF = 4;

    public static final String AES_INSTANCE = "AES/CBC/PKCS5Padding";
    public static final String DES_INSTANCE = "DES/CBC/PKCS5Padding";
    public static final String BWF_INSTANCE = "Blowfish/CFB/NoPadding";


    static final byte[] mAESiv = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04,
            0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
    static final byte[] mDESiv = new byte[]{(byte) 0x8E, 0x12, 0x39,
            (byte) 0x9C, 0x07, 0x72, 0x6F, 0x5A};

    static final byte[] mBWFiv = new byte[]{0x00, (byte) 0x80, 0x01, 0x61,
            (byte) 0xF8, 0x17};


    private int mCryptoMode;
    private TheBoysAlgorithm FFC;
    private ProgessListener mProgess;


    public Cryptographer(String key, int Algorithm) {
        mCryptoMode = Algorithm;
        switch (Algorithm) {
            case ALGORITHM_BWF: {
                SecretKeySpec skey = new SecretKeySpec(MessageDigester.getMD5(key),
                        "Blowfish");
                this.setupParameter(skey, BWF_INSTANCE, null);
                break;
            }

            case ALGORITHM_AES: {
                SecretKeySpec skey = new SecretKeySpec(MessageDigester.getMD5(key),
                        "AES");

                this.setupParameter(skey, AES_INSTANCE, mAESiv);
                break;
            }
            case ALGORITHM_DES: {
                byte[] keyData = key.getBytes();
                SecretKeySpec skey = new SecretKeySpec(keyData, 0,
                        DESKeySpec.DES_KEY_LEN, "DES");
                this.setupParameter(skey, DES_INSTANCE, mDESiv);
                break;
            }
            case ALGORITHM_FFC: {
                FFC = new TheBoysAlgorithm(key);
                // Log.d("FFC_Algorithm", "Start_FFC_Algorithm");
                break;
            }
            default:

                throw new IllegalArgumentException("Invalid Algorithm Code");

        }
    }

    public static final String getKey() {
        return Cryptographer.KEY;
    }

    public void setProgessListener(ProgessListener listener) {

        this.mProgess = listener;
    }

    private void setupParameter(SecretKey key, String instance, byte[] iv) {


        AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
        try {
            ecipher = Cipher.getInstance(instance);
            dcipher = Cipher.getInstance(instance);


            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        } catch (InvalidKeyException ike) {
            ike.printStackTrace();
        } catch (NoSuchAlgorithmException nae) {
            nae.printStackTrace();
        } catch (NoSuchPaddingException npe) {
            npe.printStackTrace();
        } catch (InvalidAlgorithmParameterException iae) {
            iae.printStackTrace();
        }
    }


    // Buffer used to transport the bytes from one stream to another
    byte[] buf = new byte[1024];


    public boolean encrypt(File input, File output) {
        try {
            return this.encrypt(new FileInputStream(input),
                    new FileOutputStream(output));
        } catch (FileNotFoundException fnf) {
            return false;
        }
    }

    public boolean encrypt(InputStream in, OutputStream out) {
        int size = 0;

        try {
            final int ODD = 0;
            final int EVEN = 1;
            // Bytes written to out will be encrypted
            if (mCryptoMode == ALGORITHM_FFC) {
                int read = 1;
                byte[] bytes = new byte[TheBoysAlgorithm.KEY_LENGTH];
                Log.w("FFC", "key_new length = " + TheBoysAlgorithm.KEY_LENGTH);
                int round = ODD;
                while ((read = in.read(bytes)) != -1) {


                    if (stop) {
                        in.close();
                        out.close();
                        return false;
                    }

                    switch (round) {
                        case ODD:

                            byte[] theByteArray;
                            theByteArray = FFC.encryptByte(bytes);
                            out.write(theByteArray, 0, read);
                            round = EVEN;

                            break;
                        case EVEN:

                            out.write(bytes, 0, read);
                            round = ODD;
                            break;
                    }


                    if (this.mProgess != null) {
                        size += read;
                        mProgess.onCryptoProgessUpdate(size, true);
                    }

                }

            } else {

                out = new CipherOutputStream(out, ecipher);
                // Read in the plain text bytes and write to out to encrypt
                int numRead = 0;
                while ((numRead = in.read(buf)) >= 0) {
                    if (stop) {
                        in.close();
                        out.close();
                        return false;
                    }

                    out.write(buf, 0, numRead);
                    if (this.mProgess != null) {
                        size += numRead;
                        mProgess.onCryptoProgessUpdate(size, true);

                    }
                }
            }
            in.close();
            out.close();


            if (this.mProgess != null)
                mProgess.onCryptoProgessUpdate(size, false);
            return true;

        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean decrypt(File input, File output) {
        try {
            return this.decrypt(new FileInputStream(input),
                    new FileOutputStream(output));
        } catch (FileNotFoundException fnf) {
            return false;
        }
    }

    public boolean decrypt(InputStream in, OutputStream out) {

        if (mCryptoMode == ALGORITHM_FFC) {
            mCryptoMode = ALGORITHM_FFC;
            return encrypt(in, out);
        } else {

            int size = 0;
            try {
                // Bytes read from in will be Decrypted
                in = new CipherInputStream(in, dcipher);
                // Read in the Decrypted bytes and write the clear text to out
                int numRead = 0;
                while ((numRead = in.read(buf)) >= 0) {

                    if (stop) {
                        in.close();
                        out.close();
                        return false;
                    }

                    out.write(buf, 0, numRead);
                    if (this.mProgess != null) {
                        size += numRead;
                        mProgess.onCryptoProgessUpdate(size, true);

                    }
                }
                in.close();
                out.close();

                if (this.mProgess != null)
                    mProgess.onCryptoProgessUpdate(size, false);
                return true;

            } catch (java.io.IOException e) {
                e.printStackTrace();
                return false;

            }
        }
    }

    public void close() {
        ecipher = null;
        dcipher = null;
        FFC = null;
        mProgess = null;
    }


    public void stop() {
        this.stop = true;
    }


    public static interface ProgessListener {

        /**
         * @param size    size of enrypted / decryptd file
         * @param running is process still running
         * @since 1.0
         */
        public void onCryptoProgessUpdate(int size, boolean running);
    }


}
