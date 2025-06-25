package th.in.ffc.smartcardreader;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.acs.smartcard.Reader;
import com.facebook.react.bridge.*;
 
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Base64;

public class ThaiIDReaderModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private UsbManager usbManager;
    private UsbDevice device;
    private Reader reader;
    private PendingIntent permissionIntent;

    private static final String ACTION_USB_PERMISSION = "com.auth.USB_PERMISSION";

    private static final byte[] APDU_SELECT = {
            (byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x08,
            (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x54, (byte) 0x48, (byte) 0x00, (byte) 0x01
    };
    private static final byte[] GET_RESPONSE = {(byte)0x00, (byte)0xc0, (byte)0x00, (byte)0x00};
    private static final byte[] READ_CID = {(byte)0x80, (byte)0xB0, (byte)0x00, (byte)0x04, (byte)0x02, (byte)0x00, (byte)0x0D};
    private static final byte[] READ_TH_FULL_NAME = {(byte)0x80, (byte)0xb0, (byte)0x00, (byte)0x11, (byte)0x02, (byte)0x00, (byte)0x64};
    private static final byte[] READ_EN_FULL_NAME = {(byte)0x80, (byte)0xb0, (byte)0x00, (byte)0x75, (byte)0x02, (byte)0x00, (byte)0x64};
    private static final byte[] READ_DATE_OF_BIRTH = {(byte)0x80, (byte)0xb0, (byte)0x00, (byte)0xD9, (byte)0x02, (byte)0x00, (byte)0x08};
    private static final byte[] READ_GENDER = {(byte)0x80, (byte)0xb0, (byte)0x00, (byte)0xE1, (byte)0x02, (byte)0x00, (byte)0x01};
    private static final byte[] READ_ADDRESS = {(byte)0x80, (byte)0xb0, (byte)0x15, (byte)0x79, (byte)0x02, (byte)0x00, (byte)0x64};
    private static final byte[] READ_CARD_ISSUER = {(byte)0x80, (byte)0xb0, (byte)0x00, (byte)0xF6, (byte)0x02, (byte)0x00, (byte)0x64};
    private static final byte[] READ_ISSUE_DATE = {(byte)0x80, (byte)0xb0, (byte)0x01, (byte)0x67, (byte)0x02, (byte)0x00, (byte)0x08};
    private static final byte[] READ_EXPIRE_DATE = {(byte)0x80, (byte)0xb0, (byte)0x01, (byte)0x6F, (byte)0x02, (byte)0x00, (byte)0x08};
    private static final byte[][] READ_CARD_PHOTO = {
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x01, (byte)0x7B, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x02, (byte)0x7A, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x03, (byte)0x79, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x04, (byte)0x78, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x05, (byte)0x77, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x06, (byte)0x76, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x07, (byte)0x75, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x08, (byte)0x74, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x09, (byte)0x73, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x0A, (byte)0x72, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x0B, (byte)0x71, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x0C, (byte)0x70, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x0D, (byte)0x6F, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x0E, (byte)0x6E, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x0F, (byte)0x6D, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x10, (byte)0x6C, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x11, (byte)0x6B, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x12, (byte)0x6A, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x13, (byte)0x69, (byte)0x02, (byte)0x00, (byte)0xFF },
            new byte[]{ (byte)0x80, (byte)0xB0, (byte)0x14, (byte)0x68, (byte)0x02, (byte)0x00, (byte)0xFF }
    };
    private Promise readCallback;

    public ThaiIDReaderModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.usbManager = (UsbManager) reactContext.getSystemService(Context.USB_SERVICE);
        this.reader = new Reader(usbManager);
        Intent intent = new Intent(ACTION_USB_PERMISSION);
        intent.setPackage(reactContext.getPackageName());  // Make intent explicit
        permissionIntent = PendingIntent.getBroadcast(
                reactContext, 0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        reactContext.registerReceiver(usbReceiver, filter, Context.RECEIVER_EXPORTED);
    }

    @NonNull
    @Override
    public String getName() {
        return "ThaiIDReader";
    }

    @ReactMethod
    public void readCard(Promise promise) {
        for (UsbDevice dev : usbManager.getDeviceList().values()) {
            if (dev.getDeviceClass() == UsbConstants.USB_CLASS_CSCID ||
                    (dev.getDeviceClass() == UsbConstants.USB_CLASS_PER_INTERFACE &&
                            dev.getInterface(0).getInterfaceClass() == UsbConstants.USB_CLASS_CSCID)) {
                device = dev;
                if (usbManager.hasPermission(device)) {
                    Log.d("ThaiIDReader", "Already have USB permission");
                    readCallback = promise;
                    new Thread(this::readDataFromCard).start();
                } else {
                    Log.d("ThaiIDReader", "Requesting USB permission");
                    usbManager.requestPermission(device, permissionIntent);
                    readCallback = promise;
                }
                return;
            }
        }
        promise.reject("NO_DEVICE", "READER_NOT_FOUND");
    }


    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
                synchronized (this) {
                    Log.d("ThaiIDReader", "USB permission result: " + intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false));
                    Log.d("ThaiIDReader", "Device: " + intent.getParcelableExtra(UsbManager.EXTRA_DEVICE));

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        new Thread(() -> readDataFromCard()).start();
                    } else {
                        if (readCallback != null) {
                            readCallback.reject("PERMISSION_DENIED", "PERMISSION_DENIED");
                        }
                    }
                }
            }
        }
    };

    private void readDataFromCard() {
        try {
            UsbDeviceConnection connection = usbManager.openDevice(device);
            Log.d("ThaiIDReader", device.toString());
            if (connection == null) {
                Log.e("ThaiIDReader", "openDevice returned null — no permission or device not connected");
                if (readCallback != null) {
                    readCallback.reject("CONNECTION_FAILED", "Could not open USB device connection");
                }
                return;
            }
            Log.d("ThaiIDReader", "USB device connection opened");

            reader.open(device);
            Thread.sleep(500);

            // Check if card is present
            int status = reader.getState(0);
            if ((status & Reader.CARD_PRESENT) == 0) {
                if (readCallback != null) {
                    readCallback.reject("CARD_NOT_FOUND", "CARD_NOT_FOUND");
                }
                reader.close();
                return;
            }

            reader.power(0, Reader.CARD_WARM_RESET);
            reader.setProtocol(0, Reader.PROTOCOL_T0 | Reader.PROTOCOL_T1);
            String readerName = reader.getReaderName();

            byte[] buffer = new byte[256];
            int len;

            len = reader.transmit(0, APDU_SELECT, APDU_SELECT.length, buffer, buffer.length);

            String cid = getUTF8FromAsciiBytes(sendCommand(READ_CID));
            String thFullName = getUTF8FromAsciiBytes(sendCommand(READ_TH_FULL_NAME));
            String enFullName = getUTF8FromAsciiBytes(sendCommand(READ_EN_FULL_NAME));
            String address = getUTF8FromAsciiBytes(sendCommand(READ_ADDRESS));
            String gender = getUTF8FromAsciiBytes(sendCommand(READ_GENDER));
            String dateOfBirth = getUTF8FromAsciiBytes(sendCommand(READ_DATE_OF_BIRTH));
            String cardIssuer = getUTF8FromAsciiBytes(sendCommand(READ_CARD_ISSUER));
            String issueDate = getUTF8FromAsciiBytes(sendCommand(READ_ISSUE_DATE));
            String expireDate = getUTF8FromAsciiBytes(sendCommand(READ_EXPIRE_DATE));
            String photoBase64 =  sendCommandPhoto();
            reader.close();
            WritableMap result = Arguments.createMap();
            String info =cid+"#"+thFullName+"#"+enFullName+"#"+address+"#"+gender+"#"+dateOfBirth+"#"+cardIssuer+"#"+issueDate+"#"+expireDate;
            info = info.replaceAll("\\s+","").replaceAll("\\x90", "");
            result.putString("info", info);
            result.putString("photo", photoBase64);

            if (readCallback != null) {
                readCallback.resolve(result);
            }

        } catch (Exception e) {
            if (readCallback != null) {
                readCallback.reject("ERROR", "Error reading card: " + e.getMessage());
            }
        }
    }
    private byte[] sendCommand(byte[] command) {
        byte[] buffer = new byte[256];
        byte[] response = new byte[256];
        int len;

        try {
            // Send command and get initial response
            len = reader.transmit(0, command, command.length, buffer, buffer.length);

            // Get the actual data with GET RESPONSE command
            // Get the SW1 (status) byte from the initial response
            byte sw1 = buffer[len - 2];
            // If SW1 indicates more data (0x61), then get remaining data
            if (sw1 == (byte)0x61) {
                // Modify the Le byte (last byte) of GET RESPONSE command with remaining length
                command = new byte[]{GET_RESPONSE[0], GET_RESPONSE[1], GET_RESPONSE[2], GET_RESPONSE[3], buffer[len - 1]};
                len = reader.transmit(0, command, command.length, buffer, buffer.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; // or handle accordingly
        }

        // Copy only the actual response bytes
        System.arraycopy(buffer, 0, response, 0, len);
        byte[] result = new byte[len];
        System.arraycopy(response, 0, result, 0, len);

        return result;
    }
    private String getUTF8FromAsciiBytes(byte[] asciiBytes){
        String response = new String(asciiBytes, Charset.forName("TIS-620")).trim();
        return response;

    }
    private String sendCommandPhoto() {
        byte[] response = new byte[5120];
        int offset = 0;

        try {
            for (byte[] command : READ_CARD_PHOTO) {
                byte[] buffer = new byte[2048];
                int len = reader.transmit(0, command, command.length, buffer, buffer.length);

                if (len < 2) {
                    throw new Exception("Invalid response length");
                }

                byte sw1 = buffer[len - 2];
                byte sw2 = buffer[len - 1];

                // If response has more data available
                if (sw1 == (byte) 0x61) {

                    command = new byte[]{GET_RESPONSE[0], GET_RESPONSE[1], GET_RESPONSE[2], GET_RESPONSE[3],sw2};
                    len = reader.transmit(0, command, command.length, buffer, buffer.length);

                    if (len < 2) {
                        throw new Exception("GET RESPONSE failed, len < 2");
                    }

                    sw1 = buffer[len - 2];
                    sw2 = buffer[len - 1];
                }

                if (sw1 != (byte) 0x90 || sw2 != (byte) 0x00) {
                    throw new Exception(String.format("Unexpected status word: %02X %02X", sw1, sw2));
                }

                int dataLen = len - 2;
                if (offset + dataLen > response.length) {
                    throw new Exception("Photo data exceeds buffer size");
                }

                System.arraycopy(buffer, 0, response, offset, dataLen);
                offset += dataLen;
            }

            if (offset == 0) {
                throw new Exception("No photo data received");
            }

            byte[] photoData = new byte[offset];
            System.arraycopy(response, 0, photoData, 0, offset);

            return Base64.getEncoder().encodeToString(photoData);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            return null;
        }
    }
}
