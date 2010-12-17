package com.bluetooth.manager.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.bluetooth.manager.BluetoothManager;
import com.bluetooth.manager.tools.Logger;

import android.bluetooth.BluetoothSocket;

public class ConnectedThread extends Thread
{
    private final Logger           logger = new Logger(this);
    private final BluetoothManager bluetoothManager;
    private final BluetoothSocket  bluetoothSocket;
    private final InputStream      inputStream;
    private final OutputStream     outputStream;

    public ConnectedThread(BluetoothManager bluetoothManager, BluetoothSocket bluetoothSocket)
    {
        this.bluetoothManager = bluetoothManager;
        this.bluetoothSocket = bluetoothSocket;
        InputStream is = null;
        OutputStream os = null;
        try
        {
            is = bluetoothSocket.getInputStream();
            os = bluetoothSocket.getOutputStream();
        }
        catch (IOException e)
        {
            this.logger.ex(e);
        }
        this.inputStream = is;
        this.outputStream = os;
    }

    @Override
    public void run()
    {
        byte[] readBuffer = new byte[1024];
        int bytes;
        while (true)
        {
            try
            {
                bytes = this.inputStream.read(readBuffer);
                this.bluetoothManager.readNotification(bytes, readBuffer);
            }
            catch (IOException e)
            {
                this.logger.ex(e);
                this.bluetoothManager.connectionLost();
                break;
            }
        }
    }

    public void write(byte[] writeBuffer)
    {
        try
        {
            this.outputStream.write(writeBuffer);
            this.bluetoothManager.writeNotification(writeBuffer);
        }
        catch (IOException e)
        {
            this.logger.ex(e);
        }
    }

    public void cancel()
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
