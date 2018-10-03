package com.example.ashab.trafficlights;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BluetoothAdapter bluetoothAdapter;
    private ListView listView;
    private ArrayList<String> pairedDeviceArrayList;
    private ArrayAdapter<String> pairedDeviceAdapter;
    public static BluetoothSocket clientSocket;
    private Button buttonStartControl;
    private Button buttonStartFind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStartFind = (Button) findViewById(R.id.button_start_find);
        buttonStartFind.setOnClickListener(this);
        buttonStartControl = (Button) findViewById(R.id.button_start_control);
        buttonStartControl.setOnClickListener(this);
        buttonStartControl.setEnabled(false);
        listView = (ListView) findViewById(R.id.list_device);

    }

    private boolean permissionGranted() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.BLUETOOTH) == PermissionChecker.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_ADMIN) == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN}, 0);
            return false;
        }
    }

    private boolean bluetoothEnabled() {
        if(bluetoothAdapter.isEnabled()) {
            return true;
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
            return false;
        }
    }

    private void findArduino() {
        //Get devices list
        Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            pairedDeviceArrayList = new ArrayList<>();
            for(BluetoothDevice device: pairedDevice) {
                pairedDeviceArrayList.add(device.getAddress() + "/" + device.getName());
                System.out.println(pairedDeviceArrayList);
            }
        }

        pairedDeviceAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_device, R.id.item_device_textView, pairedDeviceArrayList);
        listView.setAdapter(pairedDeviceAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemMAC =  listView.getItemAtPosition(i).toString().split("/", 2)[0];
                BluetoothDevice connectDevice = bluetoothAdapter.getRemoteDevice(itemMAC);
                try {
                    Method m = connectDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                    clientSocket = (BluetoothSocket) m.invoke(connectDevice, 1);
                    clientSocket.connect();
                    if(clientSocket.isConnected()) {
                        bluetoothAdapter.cancelDiscovery();
                        buttonStartControl.setEnabled(true);
                    }
                } catch(Exception e) {
                    e.getStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start_find:
                if (permissionGranted()) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (bluetoothEnabled()) {
                        findArduino();
                    }
                }
                break;
            case R.id.button_start_control:
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ActivityControl.class);
                startActivity(intent);
                break;

        }
    }
}