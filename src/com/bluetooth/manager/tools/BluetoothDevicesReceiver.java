package com.bluetooth.manager.tools;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothDevicesReceiver extends BroadcastReceiver
{
    private Logger logger = new Logger(this);

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if (BluetoothDevice.ACTION_FOUND.equals(action))
        {
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED)
            {
                this.logger.d("Device founded: " + bluetoothDevice.getName() + ", address: [" + bluetoothDevice.getAddress() + "]");
            }
        }
        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
        {
            this.logger.d("Discovery finished");
        }
    }
}
