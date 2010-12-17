package com.bluetooth.manager;

import java.io.IOException;

import com.bluetooth.manager.tools.Logger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ConnectThread extends Thread
{
    private final Logger           logger = new Logger(this);
    private final BluetoothManager bluetoothManager;
    private final BluetoothDevice  bluetoothDevice;
    private final BluetoothSocket  bluetoothSocket;

    public ConnectThread(BluetoothManager bluetoothManager, BluetoothDevice bluetoothDevice)
    {
        setName("ConnectThread");
        this.bluetoothManager = bluetoothManager;
        this.bluetoothDevice = bluetoothDevice;
        BluetoothSocket bs = null;
        try
        {
            bs = this.bluetoothDevice.createRfcommSocketToServiceRecord(BluetoothManager.uuid);
        }
        catch (IOException e)
        {
            this.logger.ex(e);
        }
        this.bluetoothSocket = bs;
    }

    @Override
    public void run()
    {
        this.logger.d("Start connect thread");
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        try
        {
            this.bluetoothSocket.connect();
        }
        catch (IOException e)
        {
            this.logger.ex(e);
            this.bluetoothManager.connectionFailed();
            try
            {
                this.bluetoothSocket.close();
            }
            catch (IOException e2)
            {
                this.logger.ex(e2);
            }
            this.logger.d("Stop connect thread because of exception");
            return;
        }
        this.logger.d("Stop connect thread");
        this.bluetoothManager.connected(this.bluetoothSocket);
    }

    void cancel()
    {
        try
        {
            this.bluetoothSocket.close();
        }
        catch (IOException e)
        {
            this.logger.ex(e);
        }
    }
}
