package com.ee5.mobile.SupportClasses;

public interface IFrameBuilder {

    static byte[] getSsidDataFrame(byte[] ssid, int sequence) {
        byte[] dataFrame = new byte[6 + ssid.length];
        dataFrame[0] = (byte) 00_000100; //Type & Subtype
        dataFrame[1] = (byte) 000_00000; //Frame control with 3MSB reserved
        dataFrame[2] = (byte) sequence;
        dataFrame[3] = (byte) ssid.length;
        return new byte[0];
    }

    static byte[] getPassDataFrame(byte[] pass) {
        return new byte[0];
    }

    static byte[] getConnectToAPControlFrame() {
        return new byte[0];
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
