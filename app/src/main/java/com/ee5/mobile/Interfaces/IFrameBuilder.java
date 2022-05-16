package com.ee5.mobile.Interfaces;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface IFrameBuilder {
    public static final String TAG = "IFrameBuilder";

    static byte[] getSsidDataFrame(byte[] ssid, int sequence) {
        byte[] dataFrame = new byte[6 + ssid.length];
        dataFrame[0] = (byte) 0x01; //Type & Subtype
        dataFrame[0] = (byte) ((0x02<<2) | dataFrame[0]);
        dataFrame[1] = (byte) 0x00; //Frame control with 3MSB reserved
        dataFrame[2] = (byte) sequence;
        dataFrame[3] = (byte) ssid.length;
        int i =0;
        while (i<ssid.length) {
            dataFrame[4+i] = ssid[i];
            i++;
        }
        return dataFrame;
    }

    static byte[] getSsidDataFrame(String ssid, int sequence) {
        byte[] dataFrame = new byte[6 + ssid.length()];
        dataFrame[0] = (byte) 0x01; //Type & Subtype
        dataFrame[0] = (byte) ((0x02<<2) | dataFrame[0]);
        dataFrame[1] = (byte) 0x00; //Frame control with 3MSB reserved
        dataFrame[2] = (byte) sequence;
        dataFrame[3] = (byte) ssid.length();
        AtomicInteger incr = new AtomicInteger();
        ssid.chars().forEach(ch -> {dataFrame[4+ incr.getAndIncrement()] = (byte) ch;});
        return dataFrame;
    }

    static byte[] getCustomDataFrame(String text, int sequence) {
        byte[] dataFrame = new byte[6 + text.length()];
        dataFrame[0] = (byte) 0x01; //Type & Subtype
        dataFrame[0] = (byte) ((0x13<<2) | dataFrame[0]);
        dataFrame[1] = (byte) 0x00; //Frame control with 3MSB reserved
        dataFrame[2] = (byte) sequence;
        dataFrame[3] = (byte) text.length();
        AtomicInteger incr = new AtomicInteger();
        text.chars().forEach(ch -> {dataFrame[4+ incr.getAndIncrement()] = (byte) ch;});
        return dataFrame;
    }

    static byte[] getPassDataFrame(String pass, int sequence) {
        byte[] dataFrame = new byte[6 + pass.length()];
        dataFrame[0] = (byte) 0x01; //Type & Subtype
        dataFrame[0] = (byte) ((0x03<<2) | dataFrame[0]);
        dataFrame[1] = (byte) 0x00; //Frame control with 3MSB reserved
        dataFrame[2] = (byte) sequence;
        dataFrame[3] = (byte) pass.length();
        AtomicInteger incr = new AtomicInteger();
        pass.chars().forEach(ch -> {dataFrame[4+ incr.getAndIncrement()] = (byte) ch;});
        return dataFrame;
    }

    static byte[] getSecurityModeControlFrame(int sequence) {
        byte[] dataFrame = new byte[6];
        dataFrame[0] = (byte) 0x00; //Type & Subtype
        dataFrame[0] = (byte) ((0x01<<2) | dataFrame[0]);
        dataFrame[1] = (byte) 0x00; //Frame control with 3MSB reserved
        dataFrame[2] = (byte) sequence;
        dataFrame[3] = (byte) 0x01;
        dataFrame[4] = (byte) 0x00; //no checksum and no encryption
        return dataFrame;
    }

    static byte[] getConnectToAPControlFrame(int sequence) {
        byte[] dataFrame = new byte[6];
        dataFrame[0] = (byte) 0x00; //Type & Subtype
        dataFrame[0] = (byte) ((0x03<<2) | dataFrame[0]);
        dataFrame[1] = (byte) 0x00; //Frame control with 3MSB reserved
        dataFrame[2] = (byte) sequence;
        return dataFrame;
    }

    static byte[] getOpmodeControlFrame(int sequence) {
        byte[] dataFrame = new byte[6];
        dataFrame[0] = (byte) 0x00; //Type & Subtype
        dataFrame[0] = (byte) ((0x02<<2) | dataFrame[0]);
        dataFrame[1] = (byte) 0x00; //Frame control with 3MSB reserved
        dataFrame[2] = (byte) sequence;
        dataFrame[3] = (byte) 0x01;
        dataFrame[4] = (byte) 0x01;
        return dataFrame;
    }

}

/*2bit - Type [b0x] 			//control or data frame
            6bit - Subtype [bxx xxxx] 	//0x4 for ssid send, 0x5 pass send

            8bit - Frame Control
    b'xxx00000

            8bit - sequence control -> increment by 1 for each data frame

            8bit - length of data excluding checksum

    xbit - data

    ?bit - checksum     */
