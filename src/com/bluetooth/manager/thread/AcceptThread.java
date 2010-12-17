package com.bluetooth.manager.thread;

import java.io.IOException;

import com.bluetooth.manager.BluetoothManager;
import com.bluetooth.manager.tools.Logger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class AcceptThread extends Thread
{
    private final Logger                logger = new Logger(this);
    private final BluetoothManager      bluetoothManager;
    private final BluetoothServerSocket bluetoothServerSocket;

    public AcceptThread(BluetoothManager bluetoothManager)
    {
        setName("AcceptThread");

        this.logger.d("Creating accept thread");

        this.bluetoothManager = bluetoothManager;

        BluetoothServerSocket bss = null;
        try
        {
            bss = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(BluetoothManager.name, BluetoothManager.uuid);
        }
        catch (IOException e)
        {
            this.logger.ex(e);
        }
        this.bluetoothServerSocket = bss;
    }

    @Override
    public void run()
    {
        this.logger.d("Start accept thread");

        BluetoothSocket bluetoothSocket = null;
        try
        {
            bluetoothSocket = this.bluetoothServerSocket.accept();
        }
        catch (IOException e)
        {
            this.logger.ex(e);
        }
        if (bluetoothSocket != null)
        {
            synchronized (this.bluetoothManager)
            {
                if (this.bluetoothManager.getState() != BluetoothManager.STATE_CONNECTING)
                {
                    try
                    {
                        bluetoothSocket.close();
                    }
                    catch (IOException e)
                    {
                        this.logger.ex(e);
                    }
                    return;
                }
                this.bluetoothManager.connected(bluetoothSocket);
            }
        }
        this.logger.d("Stop accept thread");
    }

    public void cancel()
    {
        try
        {
            this.bluetoothServerSocket.close();
        }
        catch (IOException e)
        {
            this.logger.ex(e);
        }
    }
}
