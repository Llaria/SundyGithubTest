//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package AclasLSToolSdk;

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
    private final String tag = "AclasLSTool";
    private final String strVer = "V2.004";
    private DatagramSocket m_udpSocket = null;
    private String m_strIp = null;
    private final int m_iPortSend = 6275;
    private final int m_iPortRcv = 6270;
    private AclasLsToolListener m_listener = null;
    private boolean bFlagInit = false;
    private udpThread m_threadUdp = null;
    private final byte[] AclasFlag = new byte[]{65, 67, 76, 65, 83, 0};
    private final byte[] AclasFlagScr = new byte[]{3, 5, 1, 4, 12, 4, 3, 4, 1, 4, 0};
    private int m_iTotal = 0;
    private int m_iSuccess = 0;
    private int m_iFailed = 0;
    private Semaphore semaphore = null;
    private int iTimeOut = 30000;

    public AclasLSTool() {
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
            } catch (Exception var4) {
            }

            this.m_threadUdp = null;
        }

        if (this.m_udpSocket != null) {
            this.m_udpSocket.close();
            this.m_udpSocket = null;
        }

        this.m_threadUdp = new udpThread();
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
            } catch (Exception var2) {
            }

            this.m_threadUdp = null;
        }

        if (this.m_udpSocket != null) {
            this.m_udpSocket.close();
            this.m_udpSocket = null;
        }

    }

    private void sendPluData(ArrayList<St_Plu> plus) {
        int iLen = plus.size();
        this.m_iTotal = iLen;
        this.m_iSuccess = 0;
        this.m_iFailed = 0;

        for(int i = 0; i < iLen; ++i) {
            byte[] data = this.packDataPlu((St_Plu)plus.get(i));
            if (this.m_threadUdp != null && this.bFlagInit) {
                this.m_threadUdp.appendData(data);
            }
        }

    }

    public void sendPluFile(String path) {
        this.sendPluTxtPri(path);
    }

    private void sendPluTxtPri(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                ArrayList array = new ArrayList();

                while(true) {
                    String line = reader.readLine();
                    if (line == null || line.length() < 6) {
                        if (array.size() > 0) {
                            this.sendPluData(array);

                        }
                        break;
                    }


                    String[] list = line.split("\t");
                    int iSize = list.length;
                    if (iSize >= 10) {
                        St_Plu plu = new St_Plu();
                        byte iIndex = 0;

                        try {
                            int var13 = iIndex + 1;
                            plu.iPluNo = Integer.parseInt(list[iIndex]);
                            plu.strName = list[var13++];
                            plu.dPrice = Double.parseDouble(list[var13++]);
                            plu.ucUnit = Byte.parseByte(list[var13++]);
                            plu.ucBarcodeType = (byte)Integer.parseInt(list[var13++]);
                            plu.ucLabelNo = Byte.parseByte(list[var13++]);
                            plu.ucDepart = Byte.parseByte(list[var13++]);
                            plu.ucPkgRange = Byte.parseByte(list[var13++]);
                            plu.ucPkgType = Byte.parseByte(list[var13++]);
                            plu.dPkgWeight = Double.parseDouble(list[var13++]);
                            array.add(plu);
                        } catch (Exception var11) {

                        }
                    } else {

                    }
                }
            } catch (Exception var12) {

            }
        } else {
            this.m_iTotal = -1;

        }

    }

    private void parseData(byte[] data) {
        if (data != null) {
            int iLen = data.length;
            boolean bTmpFlagInit;
            int i;
            if (data[0] == this.AclasFlag[0]) {
                bTmpFlagInit = true;

                for(i = 0; i < this.AclasFlag.length - 1; ++i) {
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

                for(i = 0; i < this.AclasFlagScr.length - 1; ++i) {
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
            } else {
                String str;
                if (data[0] == -49) {
                    ++this.m_iSuccess;
                    if (--this.m_iTotal == 0) {
                        if (this.m_listener != null) {
                            str = "Complete " + (this.m_iSuccess > 0 ? "Success:" + String.valueOf(this.m_iSuccess) : "") + (this.m_iFailed > 0 ? "Failed:" + String.valueOf(this.m_iFailed) : "");
                            this.m_listener.onSendData(true, str);
                        }

                        if (this.semaphore != null) {
                            this.semaphore.release();
                        }
                    }
                } else if (data[0] == -50) {
                    ++this.m_iFailed;
                    --this.m_iTotal;
                    if (this.m_listener != null) {
                        int iNum = this.transFromBcd(data, 1, 4);
                        this.m_listener.onSendData(false, String.valueOf(iNum));
                    }

                    if (this.m_iTotal == 0) {
                        if (this.m_listener != null) {
                            str = "Complete " + (this.m_iSuccess > 0 ? "Success:" + String.valueOf(this.m_iSuccess) : "") + (this.m_iFailed > 0 ? " Failed:" + String.valueOf(this.m_iFailed) : "");
                            this.m_listener.onSendData(true, str);
                        }

                        if (this.semaphore != null) {
                            this.semaphore.release();
                        }
                    }
                }
            }
        }

    }

    private int transFromBcd(byte[] data, int iIndexStart, int iLen) {
        int iRet = 0;
        if (data != null && data.length >= iLen) {
            for(int i = 0; i < iLen; ++i) {
                iRet = (int)((double)iRet + (double)(data[iIndexStart + iLen - 1 - i] & 15) * Math.pow(10.0D, (double)(i * 2 + 0)));
                iRet = (int)((double)iRet + (double)(data[iIndexStart + iLen - 1 - i] >> 4 & 15) * Math.pow(10.0D, (double)(i * 2 + 1)));
            }
        }

        return iRet;
    }

    private byte[] transToBcd(int iVal, int iLen) {
        byte[] data = new byte[iLen];
        String str = String.valueOf(iVal);
        byte[] tmp = str.getBytes();
        int iTmp = tmp.length;
        if (iTmp <= iLen * 2) {
            for(int i = 0; i < iLen; ++i) {
                data[iLen - 1 - i] = 0;
                if (iTmp - 1 - i * 2 - 1 >= 0) {
                    data[iLen - 1 - i] = (byte)(data[iLen - 1 - i] + (tmp[iTmp - 1 - i * 2 - 1] - 48 << 4));
                }

                if (iTmp - 1 - i * 2 - 0 >= 0) {
                    data[iLen - 1 - i] = (byte)(data[iLen - 1 - i] + (tmp[iTmp - 1 - i * 2 - 0] - 48));
                }
            }
        }

        return data;
    }

    private byte intToHex(int iVal) {
        return (byte)(iVal / 10 * 16 + iVal % 10);
    }

    private byte[] packDataPlu(St_Plu plu) {
        byte[] data = new byte[128];
        int iPos = 0;
        iPos = iPos + 1;
        data[iPos] = -49;
        byte[] code = this.transToBcd(plu.iPluNo, 4);
        data[iPos++] = code[0];
        data[iPos++] = code[1];
        data[iPos++] = code[2];
        data[iPos++] = code[3];

        byte[] price;
        int i;
        try {
            price = plu.strName.getBytes("GB18030");

            for(i = 0; i < price.length; ++i) {
                data[iPos + i] = price[i];
            }
        } catch (Exception var7) {

        }

        iPos += 41;
        price = this.transToBcd((int)(plu.dPrice * 100.0D), 4);

        for(i = 0; i < 4; ++i) {
            data[iPos + i] = price[i];
        }

        iPos += 4;
        data[iPos++] = plu.ucUnit;
        data[iPos++] = plu.ucBarcodeType;
        data[iPos++] = plu.ucLabelNo;
        data[iPos++] = this.intToHex(plu.ucDepart);
        data[iPos++] = plu.ucPkgRange;
        data[iPos++] = plu.ucPkgType;
        byte[] weight = this.transToBcd((int)(plu.dPkgWeight * 1000.0D), 3);
        data[iPos++] = weight[0];
        data[iPos++] = weight[1];
        data[iPos++] = weight[2];
        data[127] = 59;
        return data;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src != null && src.length > 0) {
            for(int i = 0; i < src.length; ++i) {
                int v = src[i] & 255;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }

                stringBuilder.append(hv);
                stringBuilder.append(" ");
            }

            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    public int sendPluTxtSyn(String ip, String path) {
        int iRet = -1;
        if (ip.length() > 0 && path.length() > 0) {
            if (this.semaphore == null) {
                this.semaphore = new Semaphore(0);
            }

            try {
                this.semaphore.acquire(this.semaphore.availablePermits());
            } catch (InterruptedException var7) {
                var7.printStackTrace();
            }

            if (!this.bFlagInit) {
                this.Init(ip, (AclasLsToolListener)null);

                try {
                    this.semaphore.tryAcquire(1, (long)this.iTimeOut, TimeUnit.MILLISECONDS);
                } catch (InterruptedException var6) {
                    var6.printStackTrace();
                }
            }

            if (this.bFlagInit) {
                this.sendPluTxtPri(path);

                try {
                    this.semaphore.tryAcquire(1, (long)(this.iTimeOut * 2), TimeUnit.MILLISECONDS);
                } catch (InterruptedException var5) {
                    var5.printStackTrace();
                }

                iRet = this.m_iTotal == 0 && this.m_iFailed == 0 ? 0 : (this.m_iFailed > 0 ? this.m_iFailed : -1);
            }
        }

        return iRet;
    }

    public interface AclasLsToolListener {
        void onInit(boolean var1, String var2);

        void onSendData(boolean var1, String var2);

        void onError(String var1);
    }

    private static class St_Plu {
        public int iPluNo;
        public String strName;
        public double dPrice;
        public byte ucUnit;
        public byte ucBarcodeType;
        public byte ucLabelNo;
        public byte ucDepart;
        public byte ucPkgRange;
        public byte ucPkgType;
        public double dPkgWeight;

        public St_Plu() {
        }
    }

    private class udpThread extends Thread {
        ReentrantLock bufferLock;
        boolean runflag;
        InetAddress address;
        private byte[] dataRcv;
        ArrayList<byte[]> listData;

        private udpThread() {
            this.bufferLock = new ReentrantLock();
            this.runflag = false;
            this.address = null;
            this.dataRcv = new byte[32];
            this.listData = new ArrayList();
        }

        public synchronized void appendData(byte[] data) {
            this.bufferLock.lock();
            this.listData.add(data);
            this.bufferLock.unlock();
        }

        public synchronized byte[] getData() {
            this.bufferLock.lock();
            byte[] data = (byte[])null;
            int iLen = this.listData.size();
            if (iLen > 0) {
                data = (byte[])this.listData.get(iLen - 1);
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
                } catch (Exception var2) {
                    AclasLSTool.this.m_udpSocket = null;
                    var2.printStackTrace();
                    if (AclasLSTool.this.m_listener != null) {
                        AclasLSTool.this.m_listener.onInit(false, var2.toString());
                    }

                    if (AclasLSTool.this.semaphore != null) {
                        AclasLSTool.this.semaphore.release();
                    }

                }
            }

        }

        private boolean sendData() {
            boolean bFlag = false;
            if (AclasLSTool.this.m_udpSocket != null) {
                byte[] data = this.getData();
                if (data != null) {
                    DatagramPacket packet = new DatagramPacket(data, data.length, this.address, 6275);

                    try {
                        AclasLSTool.this.m_udpSocket.send(packet);
                        bFlag = true;
                    } catch (SocketException var5) {

                    } catch (UnknownHostException var6) {

                    } catch (IOException var7) {

                    }
                }
            }

            return bFlag;
        }

        private byte[] readData() {
            byte[] data = (byte[])null;
            if (AclasLSTool.this.m_udpSocket != null) {
                int iRcvLen = this.dataRcv.length;

                for(int i = 0; i < iRcvLen; ++i) {
                    this.dataRcv[i] = 0;
                }

                DatagramPacket packet = new DatagramPacket(this.dataRcv, iRcvLen);

                try {
                    AclasLSTool.this.m_udpSocket.receive(packet);
                    data = packet.getData();
                } catch (Exception var5) {
                    var5.printStackTrace();

                    if (AclasLSTool.this.m_listener != null) {
                        AclasLSTool.this.m_listener.onError(var5.toString());
                    }
                }
            }

            return data;
        }

        public void run() {
            super.run();
            this.initSocket();

            while(this.runflag && AclasLSTool.this.m_udpSocket != null) {
                if (this.sendData()) {
                    try {
                        sleep(5L);
                    } catch (Exception var2) {
                    }

                    byte[] data = this.readData();
                    if (data != null && data.length > 0) {
                        AclasLSTool.this.parseData(data);
                    }
                }
            }

        }
    }
}
