
package com.bluetooth.manager;

import com.bluetooth.manager.tools.Logger;

import android.os.Handler;
import android.os.Message;

public class BluetoothHandler extends Handler
{
    private final Logger logger = new Logger(this);

    @Override
    public void handleMessage(Message message)
    {
        switch (message.what)
        {
            case BluetoothManager.MESSAGE_STATE_CHANGE :
                switch (message.arg1)
                {
                    case BluetoothManager.STATE_PENDING :
                        this.logger.d("Manager changed state: pending");
                        break;
                    case BluetoothManager.STATE_LISTENING :
                        this.logger.d("Manager changed state: listening");
                        break;
                    case BluetoothManager.STATE_CONNECTING :
                        this.logger.d("Manager changed state: connecting");
                        break;
                    case BluetoothManager.STATE_CONNECTED :
                        this.logger.d("Manager changed state: connected");
                        break;
                }
                break;
            case BluetoothManager.MESSAGE_WRITE_DATA :
                byte[] writeBuffer = (byte[]) message.obj;
                String writeMessage = new String(writeBuffer);
                this.logger.d(writeMessage);
                break;
            case BluetoothManager.MESSAGE_READ_DATA :
                byte[] readBuffer = (byte[]) message.obj;
                String readMessage = new String(readBuffer, 0, message.arg1);
                this.logger.d(readMessage);
                break;
            case BluetoothManager.MESSAGE_MESSAGE :
                String m = message.getData().getString(BluetoothManager.MESSAGE_TAG);
                this.logger.d(m);
                break;
        }
    }
}
