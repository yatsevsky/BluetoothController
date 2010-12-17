package com.bluetooth;

import java.util.Set;

import com.bluetooth.manager.BluetoothDevicesReceiver;
import com.bluetooth.manager.BluetoothHandler;
import com.bluetooth.manager.BluetoothManager;
import com.bluetooth.manager.tools.Logger;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class DemoActivity extends Activity
{
    private static final int  REQUEST_ENABLE_BLUETOOTH = 0;
    private static final int  DISCOVERABLE_DURATION    = 300;

    private BluetoothAdapter  bluetoothAdapter;
    private BroadcastReceiver broadcastReceiver;
    private BluetoothManager  bluetoothManager;

    private Logger            logger                   = new Logger(this);

    public BluetoothManager getBluetoothManager()
    {
        return this.bluetoothManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        this.bluetoothManager = new BluetoothManager(new BluetoothHandler());

        findViewById(R.id.button_listen).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getBluetoothManager().listen();
            }
        });

        findViewById(R.id.button_stop_listening).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getBluetoothManager().stopListening();
            }
        });

        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (this.bluetoothAdapter == null)
        {
            throw new NullPointerException();
        }

        if (!this.bluetoothAdapter.isEnabled())
        {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
        }

        if (this.bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
        {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION);
            startActivity(intent);
        }

        Set<BluetoothDevice> bluetoothDevices = this.bluetoothAdapter.getBondedDevices();

        if (!bluetoothDevices.isEmpty())
        {
            for (BluetoothDevice bluetoothDevice : bluetoothDevices)
            {
                this.logger.d("Bounded device founded = " + bluetoothDevice.getName() + ", address = [" + bluetoothDevice.getAddress() + "]");
            }
        }

        this.broadcastReceiver = new BluetoothDevicesReceiver();

        IntentFilter intentFilter;
        intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(this.broadcastReceiver, intentFilter);
        intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(this.broadcastReceiver, intentFilter);

        this.bluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(this.broadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode == Activity.RESULT_OK)
        {
            // TODO Auto-generated method stub
        }

        // private static final int REQUEST_CONNECT_DEVICE = 1;
        // private static final String EXTRA_DEVICE_ADDRESS =

        // "EXTRA_DEVICE_ADDRESS";
        // in this activity------------------------------------------
        // Intent intent = new Intent(this, /*Activity*/);
        // startActivityForResult(intent, REQUEST_CONNECT_DEVICE);

        // in activity start from this-------------------------------
        // String address = "";
        // Intent intent = new Intent();
        // intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
        // setResult(Activity.RESULT_OK, intent);
        // finish();

        // in this activity -----------------------------------------
        // if (requestCode == REQUEST_CONNECT_DEVICE && resultCode ==
        // Activity.RESULT_OK)
        // {
        // String address = data.getExtras().getString(EXTRA_DEVICE_ADDRESS);
        // BluetoothDevice bluetoothDevice =
        // bluetoothAdapter.getRemoteDevice(address);
        // }
        // ----------------------------------------------------------
    }
}
