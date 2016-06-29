package net.bluetoothviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.cezar.bluetoothlibrary.*;
import com.example.cezar.bluetoothlibrary.MessageHandler;
import com.example.cezar.bluetoothlibrary.NullDeviceConnector;
import com.example.switchlistener.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class BluetoothNetFlix extends Activity
{
    private final Handler handComandos = new Handler();
    private boolean saindoDoSistema = false;
    private String vs_NomeArquivo = "Comandos.txt";
    private String vs_AppJaCarregado  = "App.txt";
    private Button btnAtivarNetFlix;
    private Button btnConf ;
    private Button btnConn ;
    private static final String TAG = BluetoothNetFlix.class.getSimpleName();
    private static final boolean D = true;
    private NotificationManager mNotificationManager;
    private AudioManager mAudioManager;
    private int mNotificationId = -1;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int MENU_SETTINGS = 4;
    private static final String SAVED_PENDING_REQUEST_ENABLE_BT = "PENDING_REQUEST_ENABLE_BT";
    private TextView mStatusView;
    private DeviceConnector mDeviceConnector = new NullDeviceConnector();
    private boolean pendingRequestEnableBt = false;
    private String deviceName;
    private boolean connected;
    private final StringBuilder recording = new StringBuilder();
    private Hashtable<Integer, String> hs_Comandos;
    private int contadorComandos = 0;
    private boolean configurandoBotoes = false;
    private String[] vetComandos;
    private int contadorGeralComandos = 0;
    private int valorDelay = 0;

    private Runnable threadEnviaComando = new Runnable()
    {
        public void run()
        {
            if((contadorGeralComandos +1 ) < vetComandos.length)
            {
                sendMessage("0x" + vetComandos[contadorGeralComandos]);
                mStatusView.setText(getResources().getString(R.string.enviandoComando) +
                                    vetComandos[contadorGeralComandos] + "...");
                contadorGeralComandos++;
            }else{
                mStatusView.setText(getResources().getString(R.string.envioConcluido));
                handComandos.removeCallbacks(this);
                Vibrator v1 = (Vibrator) getApplicationContext().getSystemService(
                        getApplicationContext().VIBRATOR_SERVICE);
                v1.vibrate(500);
                activateDoNotDisturbMode();
                contadorGeralComandos = 0;
            }
        }
    };

    public void alteraStatusBotoes()
    {
        File file = new File(Environment.getExternalStorageDirectory(),vs_NomeArquivo);
        btnAtivarNetFlix.setVisibility(View.VISIBLE);
        btnConn.setVisibility(View.GONE);
        btnConf.setVisibility(View.VISIBLE);

        btnAtivarNetFlix.setBackgroundResource(R.drawable.botao);
        btnConf.setBackgroundResource(R.drawable.botao);

        if(!file.exists())
        {
            configurandoBotoes = true;
            btnAtivarNetFlix.setText(getResources().getString(R.string.cadastraBotao));
            btnConf.setText(getResources().getString(R.string.finalizaCadastro));
        }else{
            btnAtivarNetFlix.setText(getResources().getString(R.string.iniciar));
            btnConf.setText(getResources().getString(R.string.reconfigurarBotões));
            btnConf.setBackgroundResource(R.drawable.botao);
            configurandoBotoes = false;
        }
    }
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageHandler.MSG_CONNECTED:
                    alteraStatusBotoes();
                    connected = true;
                    recording.setLength(0);
                    deviceName = msg.obj.toString();
                    mStatusView.setText(getResources().getString(R.string.conectado));
                    break;
                case MessageHandler.MSG_CONNECTING:
                    connected = false;
                    mStatusView.setText(formatStatusMessage(R.string.btstatus_connecting_to_fmt, msg.obj));
                    break;
                case MessageHandler.MSG_NOT_CONNECTED:
                    if(saindoDoSistema)
                       deactivateDoNotDisturbMode();

                    connected = false;
                    mStatusView.setText(R.string.btstatus_not_connected);
                    break;
                case MessageHandler.MSG_CONNECTION_FAILED:
                    connected = false;
                    mStatusView.setText(R.string.btstatus_not_connected);
                    break;
                case MessageHandler.MSG_CONNECTION_LOST:
                    connected = false;
                    mStatusView.setText(R.string.btstatus_not_connected);
                    break;
                case MessageHandler.MSG_LINE_READ:
                    configuraComandosRecebidos(msg.obj.toString());
                    break;
            }
        }
    };

    private void configuraComandosRecebidos(String vs_Comando)
    {
        switch (vs_Comando)
        {
            case "ENVIADO":
                // Geralmente televisão demora um pouco para ligar
                // aí o primeiro comando tem um delay maior para pular para os outros.
                if(contadorGeralComandos == 1) valorDelay = 20000;
                else valorDelay = 2000;
                handComandos.postDelayed(threadEnviaComando,valorDelay);
                break;
            case "SILENCIE":
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(
                        getApplicationContext().VIBRATOR_SERVICE);
                v.vibrate(500);
                break;
            default:
                mStatusView.setText(getResources().getString(R.string.comandoRecebido) + vs_Comando);
                hs_Comandos.put(contadorComandos,vs_Comando);
                btnAtivarNetFlix.setBackgroundResource(R.drawable.botao);
                contadorComandos++;
                break;
        }
    }
    public void configuarControles(View v)
    {
        if(configurandoBotoes)
        {
            StringBuilder vs_Arquivo = new StringBuilder();
            for (int i = 0; i < hs_Comandos.size(); i++)
            {
                vs_Arquivo.append(hs_Comandos.get(i));
                vs_Arquivo.append(";");
            }
            gravarArquivo(vs_Arquivo.toString(), vs_NomeArquivo);
            mStatusView.setText(getResources().getString(R.string.comandosGravados));
            alteraStatusBotoes();
        }else{
            mostraSimNao();
        }
    }
    public void sair(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.msgSair))
                .setMessage(getResources().getString(R.string.duvidaSair))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getResources().getString(R.string.sim),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                saindoDoSistema = true;
                                disconnectDevices();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.nao), null)
                .show();
    }
    private void mostraSimNao()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.reconfigurarBotões))
                .setMessage(getResources().getString(R.string.duvidaReconfigurar))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getResources().getString(R.string.sim),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                File file = new File(Environment.getExternalStorageDirectory(),
                                            vs_NomeArquivo);
                                file.delete();
                                mStatusView.setText(getResources().getString(R.string.reConf));
                                alteraStatusBotoes();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.nao), null)
                .show();
    }
    public static void gravarArquivo(String vs_conteudo,String aNomeArquivo)
    {
        try {
            FileOutputStream fos;
            File arq = new File(Environment.getExternalStorageDirectory(),aNomeArquivo);
            byte[] vetBytes = vs_conteudo.getBytes();
            fos = new FileOutputStream(arq);
            fos.write(vetBytes);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-g enerated catch block
            e.printStackTrace();
        }
    }
    private void createNotification(int drawableResource, String title, String text)
    {
        NotificationCompat.Builder builder = (NotificationCompat.Builder)
                 new NotificationCompat.Builder(this)
                .setSmallIcon(drawableResource)
                .setContentTitle(title)
                .setContentText(text);

        Intent resultIntent = new Intent(this,BluetoothNetFlix.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(BluetoothNetFlix.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                                                          PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        mNotificationId = 412;
        mNotificationManager.notify(mNotificationId, notification);
    }
    public void ativarNetFlix(View v)
    {
        if(configurandoBotoes)
        {
            sendMessage("CONTROLE");
            btnAtivarNetFlix.setBackgroundColor(Color.BLACK);
        }else{
            StringBuilder vs_comandos = lerArquivo();
            vetComandos = vs_comandos.toString().split(";");
            handComandos.postDelayed(threadEnviaComando, 0);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        hs_Comandos = new Hashtable<Integer,String>();
        mNotificationManager = (NotificationManager)  getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        mAudioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);

        if (savedInstanceState != null)
        {
            pendingRequestEnableBt = savedInstanceState.getBoolean(SAVED_PENDING_REQUEST_ENABLE_BT);
        }
        setContentView(R.layout.netflix_switch);

        btnAtivarNetFlix = (Button) findViewById(R.id.btnAtivaNet);
        btnConf = (Button) findViewById(R.id.btnConfiguraNet);
        btnConn = (Button) findViewById(R.id.btnConectaNet);

        mStatusView = (TextView) findViewById(R.id.btStatusNet);
        requestEnableBluetooth();
    }
    public StringBuilder lerArquivo()
    {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard,vs_NomeArquivo);
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text;
    }
    protected void activateDoNotDisturbMode()
    {
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        createNotification(android.R.drawable.ic_lock_silent_mode,
                getResources().getString(R.string.naopertube),
                getResources().getString(R.string.chamadasBloqueadas));
    }
    protected void deactivateDoNotDisturbMode()
    {
        if (mNotificationId == 412)
        {
            mNotificationManager.cancel(mNotificationId);
        }
        mNotificationId = -1;
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        this.finish();
    }
    public void conectar(View v)
    {
        startDeviceListActivity();
    }
    private void startDeviceListActivity()
    {
        Intent intent = new Intent(this, DeviceListActivity.class);
        intent.putExtra(DeviceListActivity.EXTRA_MOCK_DEVICES_ENABLED, true);
        startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
    }
    private void requestEnableBluetooth()
    {
        if (!isBluetoothAdapterEnabled() && !pendingRequestEnableBt) {
            pendingRequestEnableBt = true;
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }
    private boolean isBluetoothAdapterEnabled() {
        return getBluetoothAdapter().isEnabled();
    }

    private BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDeviceConnector.disconnect();
    }

    private void sendMessage(CharSequence chars)
    {
        if (chars.length() > 0) {
            mDeviceConnector.sendAsciiMessage(chars);
        }
    }
    private String formatStatusMessage(int formatResId, Object obj)
    {
        String deviceName = (String) obj;
        return getString(formatResId, deviceName);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String connectorTypeMsgId =
                                        DeviceListActivity.Message.DeviceConnectorType.toString();
                    DeviceListActivity.ConnectorType connectorType =
                            (DeviceListActivity.ConnectorType)
                                        data.getSerializableExtra(connectorTypeMsgId);
                    MessageHandler messageHandler = new MessageHandlerImpl(mHandler);
                    switch (connectorType)
                    {
                        case Bluetooth:
                            String addressMsgId = DeviceListActivity.Message.BluetoothAddress.toString();
                            String address = data.getStringExtra(addressMsgId);
                            mDeviceConnector = new BluetoothDeviceConnector(messageHandler, address);
                            break;
                        default:
                            return;
                    }
                    mDeviceConnector.connect();
                }
                break;
            case REQUEST_ENABLE_BT:
                pendingRequestEnableBt = false;
                break;
        }
    }
    private void startActivityForResult(Class<?> cls, int requestCode) {
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivityForResult(intent, requestCode);
    }
    private void disconnectDevices()
    {
        mDeviceConnector.disconnect();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_PENDING_REQUEST_ENABLE_BT, pendingRequestEnableBt);
    }
}
