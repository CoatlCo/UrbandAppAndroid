/**
 * Filename:        BluetoothUtils.java
 * Created:         Thu, 11 Aug 2016
 * Author:          Erick Rivera
 * Description:
 * Revisions:       Thu, 11 Aug 2016 by Erick Rivera
 * 					Mon, 14 Nov 2016 by Erick Rivera
 *
 <!--
 Copyright 2014 - 2016 Centro de Investigacion y Desarrollo COATL S.A. de C.V.
 All rights reserved.

 IMPORTANT: Your use of this Software is limited to those specific rights
 granted under the terms of a software license agreement (SLA) or
 non-disclosure agreement (NDA) between the user
 who got the software (the "Licensee") and the "Centro de Investigacion
 y Desarrollo COATL S.A. de C.V." (the "Licensor").

 You may not use this Software unless you agree to abide by the terms of the
 License (SLA and/or NDA). The License limits your use, and you acknowledge,
 that the Software may not be modified, copied or distributed.
 Other than for the foregoing purpose, you may not use, reproduce, copy,
 prepare derivative works of, modify, distribute, perform, display or sell this
 Software and/or its documentation for any purpose.

 YOU FURTHER ACKNOWLEDGE AND AGREE THAT THE SOFTWARE AND DOCUMENTATION ARE
 PROVIDED ``AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY, TITLE,
 NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT SHALL
 Centro de Investigacion y Desarrollo COATL S.A. de C.V. OR ITS LICENSORS BE
 LIABLE OR OBLIGATED UNDER CONTRACT, NEGLIGENCE, STRICT LIABILITY,
 CONTRIBUTION, BREACH OF WARRANTY, OR OTHER LEGAL EQUITABLE THEORY ANY DIRECT
 OR INDIRECT DAMAGES OR EXPENSES INCLUDING BUT NOT LIMITED TO ANY INCIDENTAL,
 SPECIAL, INDIRECT, PUNITIVE OR CONSEQUENTIAL DAMAGES, LOST PROFITS OR LOST
 DATA, COST OF PROCUREMENT OF SUBSTITUTE GOODS, TECHNOLOGY, SERVICES, OR ANY
 CLAIMS BY THIRD PARTIES (INCLUDING BUT NOT LIMITED TO ANY DEFENSE THEREOF),
 OR OTHER SIMILAR COSTS.

 Should you have any questions regarding your right to use this Software,
 contact Centro de Investigacion y Desarrollo COATL S.A. de C.V. at
 http://coatl.co/
 -->
 */
package dev.coatl.co.urband_basic.view.utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import dev.coatl.co.urband_basic.model.objects.BluetoothGattAttributes;
import dev.coatl.co.urband_basic.presenter.activities.ActivityHomePanel;
import dev.coatl.co.urband_basic.presenter.fragments.FragmentMenuPanel;
import dev.coatl.co.urband_basic.presenter.services.BluetoothService;

public class BluetoothUtils  {

    private final static String TAG = BluetoothUtils.class.getSimpleName();

    private static BluetoothService bluetoothService;

    private static BluetoothGattCharacteristic mNotifyCharacteristic;
    private static BluetoothGattCharacteristic mGestureNotifyCharacteristic;

    public BluetoothUtils()
        {
            DebugUtils.logInfo(TAG, "On BluetoothUtils(Context context)");

        }

    public BluetoothService getBluetoothService()
        {
            return bluetoothService;
        }

    public void setBluetoothServiceToNULL()
        {
            bluetoothService = null;
        }

    public static ServiceConnection serviceConnection = new ServiceConnection()
        {
            @Override
            protected Object clone() throws CloneNotSupportedException
                {
                    DebugUtils.logInfo(TAG, "Debug enters into clone()");
                    return super.clone();
                }

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service)
                {
                    DebugUtils.logInfo(TAG, "Debug enters into onServiceConnected()");
                    bluetoothService = ((BluetoothService.LocalBinder) service).getService();
                    if (!bluetoothService.initialize())
                        {
                            DebugUtils.logInfo(TAG, "Unable to initialize Bluetooth");
                        }
                    bluetoothService.connect(BluetoothService.deviceAddress);
                }

            @Override
            public void onServiceDisconnected(ComponentName componentName)
                {
                    DebugUtils.logInfo(TAG, "Debug enters into onServiceDisconnected()");
                    bluetoothService = null;
                }
        };

    public ServiceConnection getServiceConnection()
        {
            DebugUtils.logInfo(TAG, "Debug enters into getServiceConnection()");
            return serviceConnection;
        }

    public final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
                {
                    final String action = intent.getAction();
                    if (BluetoothService.ACTION_GATT_CONNECTED.equals(action))
                        {
                            BluetoothService.statusUrband = BluetoothService.STATUS_URBAND_CONNECT;
                            DebugUtils.logInfo(TAG, "ACTION_GATT_CONNECTED --> status of Urband: " + BluetoothService.statusUrband);
                        }
                    else if (BluetoothService.ACTION_GATT_DISCONNECTED.equals(action))
                        {
                            BluetoothService.statusUrband = BluetoothService.STATUS_URBAND_DISCONNECT;
                            ((ActivityHomePanel) context).invalidateOptionsMenu();
                            DebugUtils.logInfo(TAG, "ACTION_GATT_DISCONNECTED --> status of Urband: " + BluetoothService.statusUrband);
                        }
                    else if (BluetoothService.ACTION_MTU_CHANGED.equals(action))
                    {
                        DebugUtils.logInfo(TAG, "ACTION_MTU_CHANGED ---------");

                    }
                    else if (BluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))
                        {
                            DebugUtils.logInfo(TAG, "ACTION_GATT_SERVICES_DISCOVERED ---------");
                            // battery_voltage_data();
                            try
                                {
                                    Thread.sleep(900);
                                }
                            catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                            gesture_recognition_data();

                        }
                    else if (BluetoothService.ACTION_DATA_AVAILABLE.equals(action))
                        {
                            //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                            //DebugUtils.logInfo(TAG, "ACTION_DATA_AVAILABLE, length: " + intent.getStringExtra(BluetoothService.EXTRA_DATA).length());
                            //DebugUtils.logInfo(TAG, "ACTION_DATA_AVAILABLE, data: " + intent.getStringExtra(BluetoothService.EXTRA_DATA));
                        }
                    else if (BluetoothService.ACTION_GESTURE_DETECTED.equals(action))
                        {
                            final String gesture = intent.getStringExtra(BluetoothService.EXTRA_DATA);
                            final FragmentMenuPanel fg_menu = ((FragmentMenuPanel) ActivityHomePanel.getActivityHomePanel().getSupportFragmentManager().findFragmentByTag("fg_menu"));

                            final Date dt = new Date();
                            final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS aa", Locale.US);
                            String time1 = sdf.format(dt);

                            switch (gesture)
                                {
                                    case BluetoothService.GESTURE_D_HORIZONTAL:
                                        DebugUtils.logInfo(TAG, BluetoothService.GESTURE_D_HORIZONTAL);
                                        fg_menu.updateContent(BluetoothService.GESTURE_D_HORIZONTAL + " " + time1);
                                        break;
                                    case BluetoothService.GESTURE_D_TAP:
                                        DebugUtils.logInfo(TAG, BluetoothService.GESTURE_D_TAP);
                                        fg_menu.updateContent(BluetoothService.GESTURE_D_TAP + " " + time1);
                                        break;
                                    case BluetoothService.GESTURE_D_WRIST:
                                        DebugUtils.logInfo(TAG, BluetoothService.GESTURE_D_WRIST);
                                        fg_menu.updateContent(BluetoothService.GESTURE_D_WRIST + " " + time1);
                                        break;
                                    default:
                                        DebugUtils.logInfo(TAG, "other gesture/motion detected: " + gesture);
                                        fg_menu.updateContent(gesture  + " " + time1);
                                        break;
                                }
                        }
                    else if (BluetoothService.ACTION_DATA_BATTERY.equals(action))
                        {

                        }
                    else if(BluetoothDevice.ACTION_UUID.equals(action))
                        {
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                            if (uuidExtra != null)
                                {
                                    for (Parcelable uuid : uuidExtra)
                                        {
                                            DebugUtils.logInfo(TAG, "Device: " + device.getName() + " ( " + device.getAddress() + " ) - Service: " + uuid.toString());
                                        }
                                }
                        }
                }
        };


    public IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothService.ACTION_MTU_CHANGED);
        intentFilter.addAction(BluetoothService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothService.ACTION_GESTURE_DETECTED);
        intentFilter.addAction(BluetoothService.ACTION_DATA_BATTERY);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }

    public static void battery_voltage_data()
        {
            DebugUtils.logInfo(TAG, "--- battery_voltage_data()");
            if (BluetoothService.mBluetoothGatt == null)
                {
                    DebugUtils.logError(TAG, "lost connection battery");
                }
            BluetoothGattService dataService = BluetoothService.mBluetoothGatt.getService(UUID.fromString(BluetoothGattAttributes.URBAND_GESTURE_SERVICE));
            if(dataService == null)
                {
                    DebugUtils.logInfo(TAG, "URBAND service not found!");
                    return;
                }
            BluetoothGattCharacteristic dataChar = dataService.getCharacteristic(UUID.fromString(BluetoothGattAttributes.UGS_DEV2));
            if(dataChar == null)
                {
                    DebugUtils.logInfo(TAG, "Voltage data characteristic not found!");
                    return;
                }
            try
                {
                    final int charaProp = dataChar.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0)
                        {
                            DebugUtils.logInfo(TAG, "***debug: read Voltage data");
                            bluetoothService.setCharacteristicNotification(dataChar, false);
                            if (mNotifyCharacteristic != null)
                                {
                                    DebugUtils.logInfo(TAG, "***debug: clean Voltage data");
                                    bluetoothService.setCharacteristicNotification(dataChar, false);
                                    mNotifyCharacteristic = null;
                                }
                            bluetoothService.readCharacteristic(dataChar);
                        }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)
                        {
                            DebugUtils.logInfo(TAG, "***debug: notify Voltage data");
                            mNotifyCharacteristic = dataChar;
                            bluetoothService.setCharacteristicNotification(mNotifyCharacteristic, true);
                        }
                }
            catch (Exception e)
            {
                e.printStackTrace();
                DebugUtils.logError(TAG, " ..... error on gattCharacteristics Voltage data");
            }
        }

    public static void gesture_recognition_data()
        {
            DebugUtils.logInfo(TAG, "--- gesture_recognition_data()");
            if (BluetoothService.mBluetoothGatt == null)
                {
                    DebugUtils.logError(TAG, "lost connection gesture_recognition_data");
                }
            BluetoothGattService dataServiceGesture = BluetoothService.mBluetoothGatt.getService(UUID.fromString(BluetoothGattAttributes.URBAND_GESTURE_SERVICE));
            if(dataServiceGesture == null)
                {
                    DebugUtils.logInfo(TAG, "URBAND service not found! gesture_recognition_data");
                    return;
                }
            BluetoothGattCharacteristic dataChar = dataServiceGesture.getCharacteristic(UUID.fromString(BluetoothGattAttributes.UGS_GESTURE));
            if(dataChar == null)
                {
                    DebugUtils.logInfo(TAG, "gesture recognition data characteristic not found!");
                    return;
                }
            try
                {
                    final int charaProp = dataChar.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0)
                        {
                            DebugUtils.logInfo(TAG, "***debug: read gesture recognition data");
                            bluetoothService.setCharacteristicNotification(dataChar, false);
                            if (mGestureNotifyCharacteristic != null)
                                {
                                    DebugUtils.logInfo(TAG, "***debug: clean gesture recognition data");
                                    bluetoothService.setCharacteristicNotification(dataChar, false);
                                    mGestureNotifyCharacteristic = null;
                                }
                            bluetoothService.readCharacteristic(dataChar);
                        }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)
                        {
                            DebugUtils.logInfo(TAG, "***debug: notify gesture recognition data");
                            mGestureNotifyCharacteristic = dataChar;
                            bluetoothService.setCharacteristicNotification(mGestureNotifyCharacteristic, true);
                        }
                }
            catch (Exception e)
                {
                    e.printStackTrace();
                    DebugUtils.logError(TAG, " ..... error on gattCharacteristics gesture recognition data");
                }
        }
    }
