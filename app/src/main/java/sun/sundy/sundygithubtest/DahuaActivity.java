package sun.sundy.sundygithubtest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import sun.sundy.sundygithubtest.socket.SocketActivity;
import sun.sundy.sundygithubtest.utils.AclasLSTool;


public class DahuaActivity extends AppCompatActivity {

    final private String tag = "AclasLSToolDemo";
    private AclasLSTool tools = null;
    private AclasLSTool tools2 = null;

    private Button btnSend = null;
    private Button btnInit = null;
    private Button btnInit2 = null;
    private Button btnSendSyn = null;
    private Button btnUnInit = null;
    private Button btnSocket = null;
    private Timer timer = null;
    private int iBtnTimeout = 30000;

    private void OperateTimeout(boolean bFlag) {
        if (bFlag) {
            btnSend.setEnabled(false);
            if (timer == null) {
                timer = new Timer();
            }
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    Message msg = gui_show.obtainMessage();
                    msg.what = 4;
                    gui_show.sendMessage(msg);
                }
            }, iBtnTimeout);
        } else {
            btnSend.setEnabled(true);
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dahua);
        Init();

        Log.d(tag, "sdk init complete");
//		sendData();
    }

    @SuppressLint("HandlerLeak")
    Handler gui_show = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (msg.arg1 == 1) {
                        btnSend.setEnabled(true);
                        btnInit.setEnabled(false);
                        btnUnInit.setEnabled(true);
                    }
                    Toast.makeText(DahuaActivity.this, "Init " + (msg.arg1 == 1 ? "success " : "failed " + msg.obj.toString()), Toast.LENGTH_SHORT).show();
                    break;
                case 1:

                    Toast.makeText(DahuaActivity.this, "Send Plu Data " + (msg.arg1 == 1 ? "success " : "failed ") + msg.obj, Toast.LENGTH_SHORT).show();
                    OperateTimeout(false);
                    break;
                case 2:

                    Toast.makeText(DahuaActivity.this, "On error " + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case 3:

                    Toast.makeText(DahuaActivity.this, "On send txt syn " + (msg.arg1 == 0 ? "sucess" : "failed"), Toast.LENGTH_SHORT).show();
                case 4:
                    OperateTimeout(false);
                    break;
            }
        }
    };

    private void Init() {
        btnUnInit = (Button) findViewById(R.id.btn_uninit);
        btnUnInit.setEnabled(false);
        btnUnInit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (tools != null) {
                    tools.UnInit();
                    tools = null;
                    btnInit.setEnabled(true);
                    btnSend.setEnabled(false);
                }
            }
        });

        btnSocket = findViewById(R.id.btn_socket);
        btnSocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TcpClient.startClient("10.67.146.111",4001);
//                TcpClient.startClient("192.168.153.2",6000);
                startActivity(new Intent(DahuaActivity.this, SocketActivity.class));
            }
        });

        btnInit = (Button) findViewById(R.id.btn_init);
        btnInit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btnSendSyn.setEnabled(false);
                tools = new AclasLSTool();
                String strVer = tools.getVersion();
                Log.d(tag, "Version:" + strVer);
                EditText etIp = (EditText) findViewById(R.id.etIp);
                String ipString = etIp.getText().toString();
                tools.Init(ipString, new AclasLSTool.AclasLsToolListener() {
                    @Override
                    public void onInit(boolean bFlag, String arg1) {
                        Log.d(tag, "onInit:" + bFlag + "原因：" + arg1);

                        Message msg_paperstatus = gui_show.obtainMessage();
                        msg_paperstatus.what = 0;
                        msg_paperstatus.arg1 = bFlag ? 1 : 0;
                        msg_paperstatus.obj = arg1;
                        gui_show.sendMessage(msg_paperstatus);
                    }

                    @Override
                    public void onSendData(boolean bFlag, String info) {

                        Log.d(tag, "onSendData:" + bFlag + " PLU NO:" + info);

                        Message msg_paperstatus = gui_show.obtainMessage();
                        msg_paperstatus.what = 1;
                        msg_paperstatus.arg1 = bFlag ? 1 : 0;
                        msg_paperstatus.obj = new String(info);
                        gui_show.sendMessage(msg_paperstatus);
                    }

                    @Override
                    public void onError(String info) {

                        Log.d(tag, "onError:" + info);

                        Message msg_paperstatus = gui_show.obtainMessage();
                        msg_paperstatus.what = 2;
                        msg_paperstatus.obj = new String(info);
                        gui_show.sendMessage(msg_paperstatus);
                    }
                });
            }
        });

        btnInit2 = (Button) findViewById(R.id.btn_init2);
        btnInit2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tools2 = new AclasLSTool();
                tools.UnInit();
                tools2.UnInit();
                String strVer = tools2.getVersion();
                Log.d(tag, "Version2:" + strVer);
                EditText etIp = (EditText) findViewById(R.id.etIp);
                String ipString = etIp.getText().toString();
                tools2.Init(ipString, new AclasLSTool.AclasLsToolListener() {
                    @Override
                    public void onInit(boolean bFlag, String arg1) {
                        Log.d(tag, "onInit2:" + bFlag + "原因：" + arg1);

                        Message msg_paperstatus = gui_show.obtainMessage();
                        msg_paperstatus.what = 0;
                        msg_paperstatus.arg1 = bFlag ? 1 : 0;
                        gui_show.sendMessage(msg_paperstatus);
                    }

                    @Override
                    public void onSendData(boolean bFlag, String info) {

                        Log.d(tag, "onSendData2:" + bFlag + " PLU NO:" + info);

                        Message msg_paperstatus = gui_show.obtainMessage();
                        msg_paperstatus.what = 1;
                        msg_paperstatus.arg1 = bFlag ? 1 : 0;
                        msg_paperstatus.obj = new String(info);
                        gui_show.sendMessage(msg_paperstatus);
                    }

                    @Override
                    public void onError(String info) {

                        Log.d(tag, "onError2:" + info);

                        Message msg_paperstatus = gui_show.obtainMessage();
                        msg_paperstatus.what = 2;
                        msg_paperstatus.obj = new String(info);
                        gui_show.sendMessage(msg_paperstatus);
                    }
                });
            }
        });


        btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setEnabled(false);
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //sendData();
                OperateTimeout(true);
                System.out.println("开始发送===AclasLSToolDemo====");
                sendTxt();
            }
        });

        btnSendSyn = (Button) findViewById(R.id.btn_sendSyn);
        btnSendSyn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                btnInit.setEnabled(false);

                EditText etPath = (EditText) findViewById(R.id.etPath);
                String strPath = etPath.getText().toString();
                EditText etIp = (EditText) findViewById(R.id.etIp);
                String ipString = etIp.getText().toString();
                if (tools == null) {
                    tools = new AclasLSTool();
                }
                int iRet = tools.sendPluTxtSyn(ipString, strPath);


                Message msg_paperstatus = gui_show.obtainMessage();
                msg_paperstatus.what = 3;
                msg_paperstatus.arg1 = iRet;
                gui_show.sendMessage(msg_paperstatus);
            }
        });


    }

    public void sendTxt() {
//        tools.sendPluFile("/sdcard/Download/code.txt");
        EditText etPath = (EditText) findViewById(R.id.etPath);
        String strPath = etPath.getText().toString();
        tools.sendPluFile(strPath);
    }
//	public void sendData() {
//		ArrayList<AclasLSTool$St_Plu> list = new ArrayList<AclasLSTool$S_Plu>();
//
//		for(int i=0;i<200;i++){
//
//			AclasLSTool$St_Plu plu0 = new AclasLSTool$St_Plu();
//
//			plu0.iPluNo		= 900123+i;
//			plu0.strName     = "APPLE"+String.valueOf(i);
//			plu0.dPrice		= 12.34+i;
//			plu0.ucUnit		= 0x04;
//			plu0.ucBarcodeType	= (byte)i;
//			plu0.ucLabelNo		= (byte)((i+1)%32);
//			plu0.ucDepart		= (byte)((i+1)%99);
//			plu0.ucPkgRange		= 0;
//			plu0.ucPkgType		= (byte)(i%4);
//			plu0.dPkgWeight		= 1.234+i;
//
//			list.add(plu0);
//		}
//
////		St_Plu plu0 = new St_Plu();
////
////		St_Plu plu1 = new St_Plu();
////
////		plu0.iPluNo		= 900123;
////		plu0.strName     = "APPLE";
////		plu0.dPrice		= 12.34;
////		plu0.ucUnit		= 0x04;
////
////
////		plu1.iPluNo		= 950012;
////		plu1.strName     = "BEER";
////		plu1.dPrice		= 5.50;
////		plu1.ucUnit		= 0x0a;
////
////		list.add(plu0);
////		list.add(plu1);
//
//		tools.sendPluData(list);
//	}
}
