package com.bluetooth.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.bluetooth.manager.tools.Logger;

import android.bluetooth.BluetoothSocket;

public class ConnectedThread extends Thread
{
    private final Logger           logger           = new Logger(this);
    private final BluetoothManager bluetoothManager;
    private final BluetoothSocket  bluetoothSocket;
    private final InputStream      inputStream;
    private final OutputStream     outputStream;

    private static final int       READ_BUFFER_SIZE = 1 * 1024;

    // private static final int WRITE_BUFFER_SIZE = 4 * 1024;

    public ConnectedThread(BluetoothManager bluetoothManager, BluetoothSocket bluetoothSocket)
    {
        setName("ConnectedThread");
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
        this.logger.d("Start connected thread");
        byte[] readBuffer = new byte[READ_BUFFER_SIZE];
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
        this.logger.d("Stop connected thread");
    }

    void write(byte[] writeBuffer)
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
