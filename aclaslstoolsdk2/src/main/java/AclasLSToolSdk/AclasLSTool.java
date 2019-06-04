package AclasLSToolSdk;

import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class AclasLSTool {
    private final byte[] AclasFlag;
    private final byte[] AclasFlagScr;
    private boolean bFlagInit = false;
    private int iTimeOut;
    private int m_iFailed;
    private final int m_iPortRcv = 6270;
    private final int m_iPortSend = 6275;
    private int m_iSuccess;
    private int m_iTotal;
    private AclasLsToolListener m_listener = null;
    private String m_strIp = null;
    private udpThread m_threadUdp = null;
    private DatagramSocket m_udpSocket = null;
    private Semaphore semaphore;
    private final String strVer = "V2.004";
    private final String tag = "AclasLSTool";

    public interface AclasLsToolListener {
        void onError(String str);

        void onInit(boolean z, String str);

        void onSendData(boolean z, String str);
    }

    private static class St_Plu {
        public double dPkgWeight;
        public double dPrice;
        public int iPluNo;
        public String strName;
        public byte ucBarcodeType;
        public byte ucDepart;
        public byte ucLabelNo;
        public byte ucPkgRange;
        public byte ucPkgType;
        public byte ucUnit;
    }

    private class udpThread extends Thread {
        InetAddress address;
        ReentrantLock bufferLock;
        private byte[] dataRcv;
        ArrayList<byte[]> listData;
        boolean runflag;

        private udpThread() {
            this.bufferLock = new ReentrantLock();
            this.runflag = false;
            this.address = null;
            this.dataRcv = new byte[32];
            this.listData = new ArrayList();
        }

        /* synthetic */ udpThread(AclasLSTool aclasLSTool, udpThread udpthread) {
            this();
        }

        public synchronized void appendData(byte[] data) {
            this.bufferLock.lock();
            this.listData.add(data);
            this.bufferLock.unlock();
        }

        public synchronized byte[] getData() {
            byte[] data;
            this.bufferLock.lock();
            data = null;
            int iLen = this.listData.size();
            if (iLen > 0) {
                data = (byte[]) this.listData.get(iLen - 1);
                this.listData.remove(iLen - 1);
            }
            this.bufferLock.unlock();
            return data;
        }

        private void initSocket() {
            if (AclasLSTool.this.m_udpSocket == null) {
                try {
                    AclasLSTool.this.m_udpSocket = new DatagramSocket(6270);
                    AclasLSTool.this.m_udpSocket.setSoTimeout(AclasLSTool.this.iTimeOut);
                    this.address = InetAddress.getByName(AclasLSTool.this.m_strIp);
                } catch (Exception e) {
                    AclasLSTool.this.m_udpSocket = null;
                    e.printStackTrace();
                    if (AclasLSTool.this.m_listener != null) {
                        AclasLSTool.this.m_listener.onInit(false, e.toString());
                    }
                    if (AclasLSTool.this.semaphore != null) {
                        AclasLSTool.this.semaphore.release();
                    }
                    Log.e("AclasLSTool", "initSocket exception");
                }
            }
        }

        private boolean sendData() {
            if (AclasLSTool.this.m_udpSocket == null) {
                return false;
            }
            byte[] data = getData();
            if (data == null) {
                return false;
            }
            try {
                AclasLSTool.this.m_udpSocket.send(new DatagramPacket(data, data.length, this.address, 6275));
                return true;
            } catch (SocketException e) {
                Log.e("AclasLSTool", "sendData SocketException");
                return false;
            } catch (UnknownHostException e2) {
                Log.e("AclasLSTool", "sendData UnknownHostException");
                return false;
            } catch (IOException e3) {
                Log.e("AclasLSTool", "sendData IOException");
                return false;
            }
        }

        private byte[] readData() {
            byte[] data = null;
            if (AclasLSTool.this.m_udpSocket == null) {
                return data;
            }
            int iRcvLen = this.dataRcv.length;
            for (int i = 0; i < iRcvLen; i++) {
                this.dataRcv[i] = (byte) 0;
            }
            DatagramPacket packet = new DatagramPacket(this.dataRcv, iRcvLen);
            try {
                AclasLSTool.this.m_udpSocket.receive(packet);
                return packet.getData();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("AclasLSTool", "readData Exception!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                if (AclasLSTool.this.m_listener == null) {
                    return data;
                }
                AclasLSTool.this.m_listener.onError(e.toString());
                return data;
            }
        }

        public void run() {
            super.run();
            initSocket();
            while (this.runflag && AclasLSTool.this.m_udpSocket != null) {
                if (sendData()) {
                    try {
                        sleep(5);
                    } catch (Exception e) {
                    }
                    byte[] data = readData();
                    if (data != null && data.length > 0) {
                        AclasLSTool.this.parseData(data);
                    }
                }
            }
        }
    }

    public AclasLSTool() {
        byte[] bArr = new byte[6];
        bArr[0] = (byte) 65;
        bArr[1] = (byte) 67;
        bArr[2] = (byte) 76;
        bArr[3] = (byte) 65;
        bArr[4] = (byte) 83;
        this.AclasFlag = bArr;
        bArr = new byte[11];
        bArr[0] = (byte) 3;
        bArr[1] = (byte) 5;
        bArr[2] = (byte) 1;
        bArr[3] = (byte) 4;
        bArr[4] = (byte) 12;
        bArr[5] = (byte) 4;
        bArr[6] = (byte) 3;
        bArr[7] = (byte) 4;
        bArr[8] = (byte) 1;
        bArr[9] = (byte) 4;
        this.AclasFlagScr = bArr;
        this.m_iTotal = 0;
        this.m_iSuccess = 0;
        this.m_iFailed = 0;
        this.semaphore = null;
        this.iTimeOut = 30000;
    }

    public String getVersion() {
        return "V2.004";
    }

    public void Init(String ip, AclasLsToolListener listen) {
        this.m_listener = listen;
        this.m_strIp = ip;
        this.m_iTotal = 0;
        this.bFlagInit = false;
        if (this.m_threadUdp != null) {
            this.m_threadUdp.runflag = false;
            try {
                this.m_threadUdp.join();
            } catch (Exception e) {
            }
            this.m_threadUdp = null;
        }
        if (this.m_udpSocket != null) {
            this.m_udpSocket.close();
            this.m_udpSocket = null;
        }
        this.m_threadUdp = new udpThread(this, null);
        this.m_threadUdp.appendData(this.AclasFlag);
        this.m_threadUdp.runflag = true;
        this.m_threadUdp.start();
    }

    public void UnInit() {
        if (this.semaphore != null) {
            this.semaphore.release(100);
        }
        if (this.m_threadUdp != null) {
            this.m_threadUdp.runflag = false;
            try {
                this.m_threadUdp.join();
            } catch (Exception e) {
            }
            this.m_threadUdp = null;
        }
        if (this.m_udpSocket != null) {
            this.m_udpSocket.close();
            this.m_udpSocket = null;
        }
    }

    public void sendPluData(ArrayList<St_Plu> plus) {
        int iLen = plus.size();
        this.m_iTotal = iLen;
        this.m_iSuccess = 0;
        this.m_iFailed = 0;
        for (int i = 0; i < iLen; i++) {
            byte[] data = packDataPlu((St_Plu) plus.get(i));
            if (this.m_threadUdp != null && this.bFlagInit) {
                this.m_threadUdp.appendData(data);
            }
        }
    }

    public void sendPluFile(String path) {
        sendPluTxtPri(path);
    }

    private void sendPluTxtPri(String path) {
        Exception e;
        File file = new File(path);
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                ArrayList<St_Plu> array = new ArrayList();
                while (true) {
                    String line = reader.readLine();
                    if (line != null && line.length() >= 6) {
                        Log.d("AclasLSTool", "sendTxt read line:" + line);
                        String[] list = line.split("\t");
                        int iSize = list.length;
                        if (iSize >= 10) {
                            St_Plu plu = new St_Plu();
                            int iIndex = 0 + 1;
                            int iIndex2;
                            try {
                                plu.iPluNo = Integer.parseInt(list[0]);
                                iIndex2 = iIndex + 1;
                                try {
                                    plu.strName = list[iIndex];
                                    iIndex = iIndex2 + 1;
                                    plu.dPrice = Double.parseDouble(list[iIndex2]);
                                    iIndex2 = iIndex + 1;
                                    plu.ucUnit = Byte.parseByte(list[iIndex]);
                                    iIndex = iIndex2 + 1;
                                    plu.ucBarcodeType = (byte) Integer.parseInt(list[iIndex2]);
                                    iIndex2 = iIndex + 1;
                                    plu.ucLabelNo = Byte.parseByte(list[iIndex]);
                                    iIndex = iIndex2 + 1;
                                    plu.ucDepart = Byte.parseByte(list[iIndex2]);
                                    iIndex2 = iIndex + 1;
                                    plu.ucPkgRange = Byte.parseByte(list[iIndex]);
                                    iIndex = iIndex2 + 1;
                                    plu.ucPkgType = Byte.parseByte(list[iIndex2]);
                                    iIndex2 = iIndex + 1;
                                    plu.dPkgWeight = Double.parseDouble(list[iIndex]);
                                    array.add(plu);
                                } catch (Exception e2) {
                                    e = e2;
                                    Log.e("AclasLSTool", "sendPluFile parse date exception:" + e.toString());
                                }
                            } catch (Exception e3) {
                                e = e3;
                                iIndex2 = iIndex;
                                Log.e("AclasLSTool", "sendPluFile parse date exception:" + e.toString());
                            }
                        } else {
                            Log.e("AclasLSTool", "plu error size:" + iSize);
                        }
                    }else {
                        break;
                    }
                }
                if (array.size() > 0) {
                    sendPluData(array);
                    Log.d("AclasLSTool", "plu txt size:" + array.size());
                    return;
                }
                return;
            } catch (Exception e4) {
                Log.e("AclasLSTool", "sendPluTxtPri Exception:" + e4.toString());
                return;
            }
        }
        this.m_iTotal = -1;
        Log.e("AclasLSTool", "sendTxt not exist file:" + path);
    }

    private void parseData(byte[] data) {
        if (data != null) {
            int iLen = data.length;
            boolean bTmpFlagInit;
            int i;
            if (data[0] == this.AclasFlag[0]) {
                bTmpFlagInit = true;
                for (i = 0; i < this.AclasFlag.length - 1; i++) {
                    if (data[i] != this.AclasFlag[i]) {
                        bTmpFlagInit = false;
                        break;
                    }
                }
                if (bTmpFlagInit) {
                    this.m_threadUdp.appendData(this.AclasFlagScr);
                } else if (this.m_listener != null) {
                    this.m_listener.onInit(bTmpFlagInit, "");
                }
            } else if (data[0] == this.AclasFlagScr[0]) {
                bTmpFlagInit = true;
                for (i = 0; i < this.AclasFlagScr.length - 1; i++) {
                    if (data[i] != this.AclasFlagScr[i]) {
                        bTmpFlagInit = false;
                        break;
                    }
                }
                this.bFlagInit = bTmpFlagInit;
                if (this.m_listener != null) {
                    this.m_listener.onInit(bTmpFlagInit, "");
                }
                if (this.semaphore != null) {
                    this.semaphore.release();
                }
            } else if (data[0] == (byte) -49) {
                this.m_iSuccess++;
                int i2 = this.m_iTotal - 1;
                this.m_iTotal = i2;
                if (i2 == 0) {
                    if (this.m_listener != null) {
                        this.m_listener.onSendData(true, "Complete " + (this.m_iSuccess > 0 ? "Success:" + String.valueOf(this.m_iSuccess) : "") + (this.m_iFailed > 0 ? "Failed:" + String.valueOf(this.m_iFailed) : ""));
                    }
                    if (this.semaphore != null) {
                        this.semaphore.release();
                    }
                }
            } else if (data[0] == (byte) -50) {
                this.m_iFailed++;
                this.m_iTotal--;
                if (this.m_listener != null) {
                    this.m_listener.onSendData(false, String.valueOf(transFromBcd(data, 1, 4)));
                }
                if (this.m_iTotal == 0) {
                    if (this.m_listener != null) {
                        this.m_listener.onSendData(true, "Complete " + (this.m_iSuccess > 0 ? "Success:" + String.valueOf(this.m_iSuccess) : "") + (this.m_iFailed > 0 ? " Failed:" + String.valueOf(this.m_iFailed) : ""));
                    }
                    if (this.semaphore != null) {
                        this.semaphore.release();
                    }
                }
            }
        }
    }

    private int transFromBcd(byte[] data, int iIndexStart, int iLen) {
        int iRet = 0;
        if (data != null && data.length >= iLen) {
            for (int i = 0; i < iLen; i++) {
                iRet = (int) (((double) ((int) (((double) iRet) + (((double) (data[((iIndexStart + iLen) - 1) - i] & 15)) * Math.pow(10.0d, (double) ((i * 2) + 0)))))) + (((double) ((data[((iIndexStart + iLen) - 1) - i] >> 4) & 15)) * Math.pow(10.0d, (double) ((i * 2) + 1))));
            }
        }
        return iRet;
    }

    private byte[] transToBcd(int iVal, int iLen) {
        byte[] data = new byte[iLen];
        byte[] tmp = String.valueOf(iVal).getBytes();
        int iTmp = tmp.length;
        if (iTmp <= iLen * 2) {
            for (int i = 0; i < iLen; i++) {
                int i2;
                data[(iLen - 1) - i] = (byte) 0;
                if (((iTmp - 1) - (i * 2)) - 1 >= 0) {
                    i2 = (iLen - 1) - i;
                    data[i2] = (byte) (data[i2] + ((tmp[((iTmp - 1) - (i * 2)) - 1] - 48) << 4));
                }
                if (((iTmp - 1) - (i * 2)) + 0 >= 0) {
                    i2 = (iLen - 1) - i;
                    data[i2] = (byte) (data[i2] + (tmp[((iTmp - 1) - (i * 2)) + 0] - 48));
                }
            }
        }
        return data;
    }

    private byte intToHex(int iVal) {
        return (byte) (((iVal / 10) * 16) + (iVal % 10));
    }

    private byte[] packDataPlu(St_Plu plu) {
        int i;
        byte[] data = new byte[128];
        int i2 = 0 + 1;
        data[0] = (byte) -49;
        byte[] code = transToBcd(plu.iPluNo, 4);
        int i3 = i2 + 1;
        data[i2] = code[0];
        i2 = i3 + 1;
        data[i3] = code[1];
        i3 = i2 + 1;
        data[i2] = code[2];
        i2 = i3 + 1;
        data[i3] = code[3];
        try {
            byte[] name = plu.strName.getBytes("GB18030");
            for (i = 0; i < name.length; i++) {
                data[i + 5] = name[i];
            }
        } catch (Exception e) {
            Log.e("AclasLSTool", "getBytes(gb18030) exception!!!!!!!!!!!!!");
        }
        i3 = i2 + 41;
        byte[] price = transToBcd((int) (plu.dPrice * 100.0d), 4);
        for (i = 0; i < 4; i++) {
            data[i + 46] = price[i];
        }
        i3 += 4;
        i2 = i3 + 1;
        data[i3] = plu.ucUnit;
        i3 = i2 + 1;
        data[i2] = plu.ucBarcodeType;
        i2 = i3 + 1;
        data[i3] = plu.ucLabelNo;
        i3 = i2 + 1;
        data[i2] = intToHex(plu.ucDepart);
        i2 = i3 + 1;
        data[i3] = plu.ucPkgRange;
        i3 = i2 + 1;
        data[i2] = plu.ucPkgType;
        byte[] weight = transToBcd((int) (plu.dPkgWeight * 1000.0d), 3);
        i2 = i3 + 1;
        data[i3] = weight[0];
        i3 = i2 + 1;
        data[i2] = weight[1];
        i2 = i3 + 1;
        data[i3] = weight[2];
        data[127] = (byte) 59;
        return data;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            String hv = Integer.toHexString(b & 255);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    public int sendPluTxtSyn(String ip, String path) {
        if (ip.length() <= 0 || path.length() <= 0) {
            return -1;
        }
        if (this.semaphore == null) {
            this.semaphore = new Semaphore(0);
        }
        try {
            this.semaphore.acquire(this.semaphore.availablePermits());
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        if (!this.bFlagInit) {
            Init(ip, null);
            try {
                this.semaphore.tryAcquire(1, (long) this.iTimeOut, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!this.bFlagInit) {
            return -1;
        }
        sendPluTxtPri(path);
        try {
            this.semaphore.tryAcquire(1, (long) (this.iTimeOut * 2), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
        if (this.m_iTotal == 0 && this.m_iFailed == 0) {
            return 0;
        }
        return this.m_iFailed > 0 ? this.m_iFailed : -1;
    }
}