package com.ee5.mobile.SupportClasses;

public interface IFrameBuilder {
    int sequence = 0;

    static byte[] getSsidDataFrame(byte[] ssid) {
        byte[] dataFrame = new byte[6 + ssid.length];
        dataFrame[0] =
        return new byte[0];
    }

    static byte[] getPassDataFrame(byte[] pass) {
        return new byte[0];
    }

    static byte[] getConnectToAPControlFrame() {

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
