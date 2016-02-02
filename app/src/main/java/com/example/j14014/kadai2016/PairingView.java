package com.example.j14014.kadai2016;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class PairingView extends Activity implements OnClickListener {

    // ログ用タグ
    private static final String TAG = "BLUETOOTH_SAMPLE";

    // Bluetoothアダプタ
    private BluetoothAdapter mAdapter;

    // ペアリング済みBluetoothDevice名を入れるArray
    private ArrayList<BluetoothDevice> mDevices;

    // Button1
    private Button mButton1;

    // Button2
    private Button mButton2;

    // Button3
    private Button mButton3;

    // Button4
    private Button mButton4;

    // SPPのUUID
    private UUID MY_UUID = UUID.fromString("1111111-0000-1000-1111-00AAEECCAAFF");

    // ServerThread
    private ServerThread serverThread;

    // ClientThread
    private ClientThread clientThread;

    // 自分のBluetooth端末の名前
    private static final String NAME = "BLUETOOTH_ANDROID";

    // 接続時のデータ送受信処理のためのThread
    private ConnectedThread connection;

    // EditText(Inputを受け取る)
    private EditText editText;

    // TextView
    private TextView textView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pairing_main);

        // Get Device List Button
        mButton1 = (Button) findViewById(R.id.button1);
        mButton1.setOnClickListener(this);

        // Start Server Button
        mButton2 = (Button) findViewById(R.id.button2);
        mButton2.setOnClickListener(this);

        // Start Client Button
        mButton3 = (Button) findViewById(R.id.button3);
        mButton3.setOnClickListener(this);

        // Game View Button
        mButton4 = (Button) findViewById(R.id.button4);
        mButton4.setOnClickListener(this);

        //受信したメッセージを描画
        textView = (TextView) findViewById(R.id.TextView01);

        /*
        //フルスクリーン
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        gameView = new GameView(this);
        setContentView(gameView);
        */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        // ペアリング済みデバイスリストを取得する
        if (view.equals(mButton1)) {
            mDevices = new ArrayList<BluetoothDevice>();
            mAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> devices = mAdapter.getBondedDevices();

            // ペアリング済みデバイスのリスト
            for (BluetoothDevice device : devices) {
                mDevices.add(device);
                // Toastで表示する
                Toast.makeText(this, "Name:" + device.getName(), Toast.LENGTH_LONG).show();
            }
        }
        // Serverを起動
        else if (view.equals(mButton2)) {
            serverThread = new ServerThread();
            serverThread.start();
        }
        // Clientを起動
        else if (view.equals(mButton3)) {
            if (mDevices != null) {
                for (int i = 0; i < mDevices.size(); i++) {
                    clientThread = new ClientThread(mDevices.get(i));
                    clientThread.start();
                }
            }
        }
        // Game View 起動
        else if (view.equals(mButton4)) {
            Intent battle = new Intent(PairingView.this, MainActivity.class);
            startActivity(battle);
        }


    }

    // Server Thread
    private class ServerThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public ServerThread() {
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUIDでSPPのUUIDを指定
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        // Runメソッド
        public void run() {
            BluetoothSocket socket = null;

            // Whileループの中で常時Clientからの接続待機でPolling
            while (true) {
                Log.i(TAG, "Polling");
                try {
                    socket = mmServerSocket.accept();

                } catch (Exception e) {
                    break;
                }

                // Clientが接続するとsocketがnullではなくなる
                if (socket != null) {
                    // 接続されると呼び出される
                    manageConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // 接続が終了する時呼ばれる
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
            }
        }
    }

    // Client用のThread
    private class ClientThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ClientThread(BluetoothDevice device) {

            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // SPPのUUIDを指定
                // この処理には android.permission.BLUETOOTH_ADMIN のパーミッションが必要
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (Exception e) {
                Log.i(TAG, "Error:" + e);
            }
            mmSocket = tmp;
        }

        public void run() {

            // Discoveryモードを終了する
            mAdapter.cancelDiscovery();

            try {
                // サーバに接続
                mmSocket.connect();
            } catch (IOException connectException) {

                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }

            // 接続されると呼び出される
            manageConnectedSocket(mmSocket);
        }

        // 接続を終了する際に呼ばれる
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Server, Client共通 接続が確立した際に呼び出される
     */
    public void manageConnectedSocket(BluetoothSocket socket) {
        Log.i(TAG, "Connection");
        connection = new ConnectedThread(socket);
        connection.start();
    }

    /**
     * 接続確立時のデータ送受信用のThread
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.i(TAG, "ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            // データ受信用
            mmInStream = tmpIn;
            // データ送信用
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "ConnectionThread#run()");
            byte[] buffer = new byte[1024];
            int bytes;

            // Whileループで入力が入ってくるのを常時待機
            while (true) {
                try {
                    // InputStreamから値を取得
                    bytes = mmInStream.read(buffer);
                    // 取得したデータをStringの変換
                    String readMsg = new String(buffer, 0, bytes, "UTF-8");
                    // Logに表示
                    Log.d(TAG, "GET: " + readMsg);

                    // Hanlder経由で画面に描画
                    Message msg = new Message();
                    msg.obj = readMsg;
                    mHandler.sendMessage(msg);

                } catch (IOException e) {
                    break;
                }
            }
        }

        /**
         * 書き込み処理
         */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /**
         * キャンセル時に呼ばれる
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            String readMsg = (String) msg.obj;
            textView.setText(readMsg);
            textView.invalidate();
        }
    };

}
