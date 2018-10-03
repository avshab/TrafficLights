package com.example.ashab.trafficlights;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;

import java.io.OutputStream;

public class ActivityControl extends AppCompatActivity implements View.OnClickListener {

    private final static String HIGH_STR = "1";
    private final static String LOW_STR = "0";
    private ConnectedThread threadCommand;
    private TrafficLight trafficLight;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        Button redButton = (Button)findViewById(R.id.button_red);
        Button yellowButton = (Button)findViewById(R.id.button_yellow);
        Button greenButton = (Button)findViewById(R.id.button_green);
        redButton.setOnClickListener(this);
        yellowButton.setOnClickListener(this);
        greenButton.setOnClickListener(this);

        threadCommand = new ConnectedThread(MainActivity.clientSocket);
        threadCommand.run();
        trafficLight = new TrafficLight();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_red:
                trafficLight.redStr = (trafficLight.redStr == LOW_STR)?HIGH_STR:LOW_STR;
                break;
            case R.id.button_yellow:
                trafficLight.yellowStr = (trafficLight.yellowStr == LOW_STR)?HIGH_STR:LOW_STR;
                break;
            case R.id.button_green:
                trafficLight.greenStr = (trafficLight.greenStr == LOW_STR)?HIGH_STR:LOW_STR;
                break;
        }
        threadCommand.sendCommand();
    }

    private class TrafficLight{
        String redStr = LOW_STR;
        String yellowStr = LOW_STR;
        String greenStr = LOW_STR;

        String GetCommandStr(){
            return redStr + yellowStr + greenStr;
        }
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket btSocket) {
            //Get socket
            this.socket = btSocket;
            //Stream to send to Arduino
            OutputStream os = null;
            try {
                os = socket.getOutputStream();
            } catch (Exception e) {
            }
            outputStream = os;
        }

        public void run() {

        }

        public void sendCommand() {
            try {
                outputStream.write("+".getBytes());
                outputStream.write(trafficLight.GetCommandStr().getBytes());
                System.out.println(trafficLight.GetCommandStr());
            } catch (Exception e) {
            }
        }
    }
}


