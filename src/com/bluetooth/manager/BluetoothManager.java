package com.bluetooth.manager;

import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class BluetoothManager
{
    public static final int    STATE_PENDING        = 0;
    public static final int    STATE_LISTENING      = 1;
    public static final int    STATE_CONNECTING     = 2;
    public static final int    STATE_CONNECTED      = 3;

    public static final int    MESSAGE_STATE_CHANGE = 0;
    public static final int    MESSAGE_READ_DATA    = 1;
    public static final int    MESSAGE_WRITE_DATA   = 2;
    public static final int    MESSAGE_MESSAGE      = 3;

    public static final String name                 = BluetoothManager.class.getSimpleName();
    public static final UUID   uuid                 = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    public static final String MESSAGE_TAG          = "MESSAGE_TAG";

    private final Handler      handler;
    private AcceptThread       acceptThread;
    private ConnectThread      connectThread;
    private ConnectedThread    connectedThread;
    private int                state;

    public BluetoothManager(Handler handler)
    {
        this.handler = handler;
        setState(STATE_PENDING);
    }

    public void listen()
    {
        if (getState() != STATE_PENDING)
        {
            return;
        }
        setState(STATE_LISTENING);
        this.acceptThread = new AcceptThread(this);
        this.acceptThread.start();
    }

    public void stopListening()
    {
        if (getState() != STATE_LISTENING)
        {
            return;
        }
        setState(STATE_PENDING);
        this.acceptThread.cancel();
    }

    public void connect(BluetoothDevice bluetoothDevice)
    {
        if (getState() != STATE_PENDING)
        {
            return;
        }
        setState(STATE_CONNECTING);
        this.connectThread = new ConnectThread(this, bluetoothDevice);
        this.connectThread.start();
    }

    public void disconnect()
    {
        if (getState() != STATE_CONNECTED)
        {
            return;
        }
        setState(STATE_PENDING);
        this.connectedThread.cancel();
    }

    void connected(BluetoothSocket bluetoothSocket)
    {
        if (getState() != STATE_CONNECTING)
        {
            return;
        }
        setState(STATE_CONNECTED);
        this.connectedThread = new ConnectedThread(this, bluetoothSocket);
        this.connectedThread.start();
    }

    void acceptionFailed()
    {
        if (getState() != STATE_LISTENING)
        {
            return;
        }
        setState(STATE_PENDING);

        Message message = this.handler.obtainMessage(MESSAGE_MESSAGE);
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_TAG, "Unable to accept device");
        message.setData(bundle);
        this.handler.sendMessage(message);
    }

    void connectionFailed()
    {
        if (getState() != STATE_CONNECTING)
        {
            return;
        }
        setState(STATE_PENDING);

        Message message = this.handler.obtainMessage(MESSAGE_MESSAGE);
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_TAG, "Unable to connect device");
        message.setData(bundle);
        this.handler.sendMessage(message);
    }

    void connectionLost()
    {
        if (getState() != STATE_CONNECTED)
        {
            return;
        }
        setState(STATE_PENDING);

        Message message = this.handler.obtainMessage(MESSAGE_MESSAGE);
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_TAG, "Device connection was lost");
        message.setData(bundle);
        this.handler.sendMessage(message);
    }

    void writeNotification(byte[] writeBuffer)
    {
        this.handler.obtainMessage(BluetoothManager.MESSAGE_WRITE_DATA, -1, -1, writeBuffer).sendToTarget();
    }

    void readNotification(int bytes, byte[] readBuffer)
    {
        this.handler.obtainMessage(BluetoothManager.MESSAGE_READ_DATA, bytes, -1, readBuffer).sendToTarget();
    }

    private int getState()
    {
        return this.state;
    }

    private void setState(int state)
    {
        this.state = state;
        this.handler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

}
