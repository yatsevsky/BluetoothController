package com.bluetooth.manager;

import java.io.IOException;

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
            this.bluetoothManager.acceptionFailed();
            this.logger.d("Stop accept thread because of exception");
            return;
        }
        this.logger.d("Stop accept thread");
        this.bluetoothManager.connected(bluetoothSocket);
    }

    void cancel()
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
