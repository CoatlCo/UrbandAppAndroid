/**
 * Filename:        BluetoothService.java
 * Created:         24/08/2016.
 * Author:          Erick Rivera
 * Description:
 * Revisions:       Mon, 14 Nov 2016 by Erick Rivera
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

package dev.coatl.co.urband_basic.presenter.services;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import dev.coatl.co.urband_basic.model.objects.BluetoothGattAttributes;
import dev.coatl.co.urband_basic.presenter.activities.ActivityHomePanel;
import dev.coatl.co.urband_basic.view.utils.DebugUtils;

import static dev.coatl.co.urband_basic.model.objects.BluetoothGattAttributes.URBAND_SECURITY_SERVICE;


public class BluetoothService extends Service
    {
        private final static String             TAG                             = BluetoothService.class.getSimpleName();

        public final static String              ACTION_GATT_CONNECTED           = "ACTION_GATT_CONNECTED";
        public final static String              ACTION_GATT_DISCONNECTED        = "ACTION_GATT_DISCONNECTED";
        public final static String              ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
        public final static String              ACTION_MTU_CHANGED              = "ACTION_MTU_CHANGED";
        public final static String              ACTION_DATA_AVAILABLE           = "ACTION_DATA_AVAILABLE";
        public final static String              ACTION_DATA_BATTERY             = "ACTION_DATA_BATTERY";
        public final static String              ACTION_GESTURE_DETECTED         = "ACTION_GESTURE_DETECTED";

        public final static String              EXTRA_DATA                      = "EXTRA_DATA";
        public final static String              GESTURE_D_HORIZONTAL            = "D_HORIZONTAL";
        public final static String              GESTURE_D_TAP                   = "D_TAP";
        public final static String              GESTURE_D_WRIST                 = "D_WRIST";




        public static       String              STATUS_URBAND_DEFAULT           = "urbandDefault";
        public static       String              STATUS_URBAND_CONNECT           = "urbandConnect";
        public static       String              STATUS_URBAND_DISCONNECT        = "urbandDisconnect";

        public static       String              statusUrband                    = STATUS_URBAND_DEFAULT;
        public static       boolean             statusBluetooth                 = false;
        public static       boolean             enabledSubscription             = false;


        private             BluetoothManager    mBluetoothManager;
        private static      BluetoothAdapter    mBluetoothAdapter;
        private             String              mBluetoothDeviceAddress;
        public static       BluetoothGatt       mBluetoothGatt;
        private             Handler             handler;
        public static       String              deviceAddress;

        @Override
        public void onCreate()
            {
                DebugUtils.logInfo(TAG, "on onCreate()");
                super.onCreate();
                handler = new Handler();
            }

        // Implements callback methods for GATT events that the app cares about. For
        // example,
        // connection change and services discovered.
        private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
            {
                @Override
                public void onConnectionStateChange(android.bluetooth.BluetoothGatt gatt, int status, int newState)
                    {
                        String intentAction;
                        if (newState == BluetoothProfile.STATE_CONNECTED)
                            {
                                // Attempts to discover services after successful connection.
                                if(mBluetoothGatt.discoverServices())
                                    {
                                        DebugUtils.logInfo(TAG, "Service discovery resulted TRUE");
                                    }
                                else
                                    {
                                        DebugUtils.logInfo(TAG, "Service discovery resulted FALSE");
                                    }

                                // broadcast gatt's action
                                intentAction = ACTION_GATT_CONNECTED;
                                broadcastUpdate(intentAction);

                                DebugUtils.logInfo(TAG, "Connected to GATT server.");
                                showToast("Connected to Urband GATT Server");
                            }
                        else if (newState == BluetoothProfile.STATE_DISCONNECTED)
                            {
                                intentAction = ACTION_GATT_DISCONNECTED;
                                DebugUtils.logInfo(TAG, "Disconnected from GATT server.");
                                broadcastUpdate(intentAction);

                                handler.post(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                            {
                                                /* Vibrates to notify the user */
                                                Vibrator vib = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                                vib.vibrate(900);
                                            }
                                    });
                                showToast("Disconnected from Urband GATT Server");
                                ActivityHomePanel.getActivityHomePanel().finish();
                            }
                    }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
                    {
                        DebugUtils.logInfo(TAG, "Enter into onCharacteristicWrite");
                        super.onCharacteristicWrite(gatt, characteristic, status);

                        // writeCharacteristic(characteristic, (byte) status);

                    }

                @Override
                public void onServicesDiscovered(android.bluetooth.BluetoothGatt gatt, int status)
                    {
                        DebugUtils.logInfo(TAG, "Enter into onServicesDiscovered");
                        if (status == android.bluetooth.BluetoothGatt.GATT_SUCCESS)
                            {
                                DebugUtils.logInfo(TAG, "onServicesDiscovered SUCCESS");
                                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                                SystemClock.sleep(600);

                                BluetoothGattCharacteristic mChar = (gatt.getService(UUID.fromString(URBAND_SECURITY_SERVICE))).getCharacteristic(UUID.fromString(BluetoothGattAttributes.USS_URBAND_TOKEN));
                                gatt.readCharacteristic(mChar);
                            }
                        else
                            {
                                DebugUtils.logInfo(TAG, "onServicesDiscovered received status = " + status);
                            }
                        //getFIFO();
                        //DebugUtils.logInfo(TAG, "getFIFO() called");
                    }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
                    {
                        DebugUtils.logInfo(TAG, "Enter into onCharacteristicRead with status: " + status);
                        if (status == android.bluetooth.BluetoothGatt.GATT_SUCCESS)
                            {
                                DebugUtils.logInfo(TAG, "onCharacteristicRead SUCCESS");
                                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                            }
                    }


                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
                    {
                        // DebugUtils.logInfo(TAG, "Enter into onCharacteristicChanged ACTION_DATA_AVAILABLE");
                        enabledSubscription = true;
                        broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                    }

                @Override
                public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                    super.onMtuChanged(gatt, mtu, status);
                    DebugUtils.logInfo(TAG, "This happens in response to requestMtu() function: " + Integer.toString(status) + ", mtu = " + Integer.toString(mtu));
                    enabledSubscription = true;
                    broadcastUpdate(ACTION_MTU_CHANGED);
                }
            };

        private void broadcastUpdate(final String action)
            {
                final Intent intent = new Intent(action);
                sendBroadcast(intent);
            }

        private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic)
            {
                final Intent intent = new Intent(action);
                StringBuilder stringBuilder;
                DebugUtils.logInfo(TAG, "UUIID's Characteristic is " + characteristic.getUuid().toString());

                if (UUID.fromString(BluetoothGattAttributes.UGS_DEV2).equals(characteristic.getUuid()))
                    {
                        DebugUtils.logInfo(TAG, "UGS_DEV2 UUIID: " + characteristic.getUuid().toString());
                        final byte[] data = characteristic.getValue();
                        if (data != null && data.length > 0)
                            {
                                stringBuilder = new StringBuilder(data.length);
                                for (byte byteChar : data)
                                    {
                                        stringBuilder.append(String.format("%02X", byteChar));
                                    }
                                intent.putExtra(EXTRA_DATA, new String(data));
                                DebugUtils.logInfo(TAG, "Return value of UGS_DEV2 is: " + Integer.parseInt(stringBuilder.toString(), 16) );

                            }
                    }
                else if (UUID.fromString(BluetoothGattAttributes.UGS_GESTURE).equals(characteristic.getUuid()))
                    {
                        DebugUtils.logInfo(TAG, "UUIID's UGS_GESTURE is " + characteristic.getUuid().toString());
                        final byte[] data = characteristic.getValue();
                        if (data != null && data.length > 0)
                        {
                            stringBuilder = new StringBuilder(data.length);
                            for (byte byteChar : data)
                                {
                                    stringBuilder.append(String.format("%02X", byteChar));
                                }
                            if(data[0] == 17) {
                                final byte[] bytes = new byte[1];
                                bytes[0] = 0;
                                writeCharacteristic(characteristic, bytes);
                                }
                            else
                                {
                                    actionGesture(stringBuilder.toString());
                                }
                            DebugUtils.logInfo(TAG, "Data received at characteristic UGS_GESTURE is: " + stringBuilder.toString());
                        }
                    }
                else if (UUID.fromString(BluetoothGattAttributes.USS_URBAND_TOKEN).equals(characteristic.getUuid()))
                    {
                        final byte[] value = characteristic.getValue();

                        DebugUtils.logInfo(TAG, "Encrypted token received at characteristic USS_URBAND_TOKEN is: " + bytesToHex(value));

                        // TODO send URBAND_ID and URBAND_TOKEN to web service to get token_res
                        // Temporally token_res = {0} will open the lock
                        final byte[] token_res = new byte[16];
                        for (int i = 0; i < 16; i++)
                            {
                                token_res[i] = (byte) 0x00;
                            }

                        // Write token_res into characteristic
                        writeCharacteristic(characteristic, token_res);
                    }

                else if (UUID.fromString(BluetoothGattAttributes.USS_URBAND_ID).equals(characteristic.getUuid()))
                    {
                        final byte[] value = characteristic.getValue();
                        DebugUtils.logInfo(TAG, "URBAND ID received at characteristic USS_URBAND_ID is: " + bytesToHex(value));
                    }
                sendBroadcast(intent);
            }

        public static String bytesToHex(byte[] in) {
            final StringBuilder builder = new StringBuilder();
            for(byte b : in) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        }

        private void actionGesture(String gesture)
            {
                final String D_HORIZONTAL = "14";
                final String D_TAP = "15";
                final String D_WRIST = "16";
                if(BluetoothService.enabledSubscription)
                    {
                        final Intent intent = new Intent(ACTION_GESTURE_DETECTED);
                        switch (gesture)
                            {
                                case D_HORIZONTAL:
                                    DebugUtils.logInfo(TAG, GESTURE_D_HORIZONTAL);
                                    intent.putExtra(EXTRA_DATA, GESTURE_D_HORIZONTAL);

                                    break;
                                case D_TAP:
                                    DebugUtils.logInfo(TAG, GESTURE_D_TAP);
                                    intent.putExtra(EXTRA_DATA, GESTURE_D_TAP);
                                    break;
                                case D_WRIST:
                                    DebugUtils.logInfo(TAG, GESTURE_D_WRIST);
                                    intent.putExtra(EXTRA_DATA, GESTURE_D_WRIST);
                                    break;
                                default:
                                    DebugUtils.logInfo(TAG, "other gesture/motion detected: " + gesture);
                                    intent.putExtra(EXTRA_DATA, gesture);
                                    break;
                            }
                        sendBroadcast(intent);
                    }
            }

        private void showToast(final String msg)
            {
                new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                            {
                                handler.post(new Runnable()
                                             {
                                                 @Override
                                                 public void run()
                                                    {
                                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                                    }
                                             }
                                );
                            }
                    }
                ).start();
            }


        public class LocalBinder extends Binder
            {
                public BluetoothService getService()
                    {
                        DebugUtils.logInfo(TAG, "On getService()");
                        return BluetoothService.this;
                    }
            }

        @Override
        public IBinder onBind(Intent intent)
            {
                DebugUtils.logInfo(TAG, "On onBind(Intent intent)");
                return mBinder;
            }

        @Override
        public boolean onUnbind(Intent intent)
            {
                // After using a given device, you should make sure that
                // BluetoothGatt.close() is called
                // such that resources are cleaned up properly. In this particular
                // example, close() is
                // invoked when the UI is disconnected from the Service.
                DebugUtils.logInfo(TAG, "On onUnbind()");
                close();
                return super.onUnbind(intent);
            }

        private final IBinder mBinder = new LocalBinder();

        /**
         * Initializes a reference to the local Bluetooth adapter.
         *
         * @return Return true if the initialization is successful.
         */
        public boolean initialize()
            {
                // For API level 18 and above, get a reference to BluetoothAdapter
                // through
                // BluetoothManager.
                DebugUtils.logInfo(TAG, "On initialize()");
                if (mBluetoothManager == null)
                    {
                        DebugUtils.logInfo(TAG, "BluetoothManager == null");
                        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                        if (mBluetoothManager == null)
                            {
                                DebugUtils.logError(TAG, "Unable to initialize BluetoothManager.");
                                return false;
                            }
                    }
                mBluetoothAdapter = mBluetoothManager.getAdapter();
                if (mBluetoothAdapter == null)
                    {
                        DebugUtils.logError(TAG, "Unable to obtain a BluetoothAdapter.");
                        return false;
                    }
                return true;
            }

        /**
         * Connects to the GATT server hosted on the Bluetooth LE device.
         *
         * @param address
         *            The device address of the destination device.
         *
         * @return Return true if the connection is initiated successfully. The
         *         connection result is reported asynchronously through the
         *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
         *         callback.
         */
        public boolean connect(final String address)
            {
                DebugUtils.logInfo(TAG, "On connect(final String address)");
                statusBluetooth = true;
                if (mBluetoothAdapter == null || address == null)
                    {
                        DebugUtils.logInfo(TAG, "BluetoothAdapter not initialized or unspecified address.");
                        return false;
                    }
                // Previously connected device. Try to reconnect.
                if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null)
                    {
                        DebugUtils.logInfo(TAG, "Trying to use an existing mBluetoothGatt for connection.");
                        if (mBluetoothGatt.connect())
                            {
                                return true;
                            }
                        else
                            {
                                return false;
                            }
                    }
                final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                if (device == null)
                    {
                        DebugUtils.logInfo(TAG, "Device not found.  Unable to connect.");
                        return false;
                    }
                // We want to directly connect to the device, so we are setting the
                // autoConnect
                // parameter to false.
                mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
                DebugUtils.logInfo(TAG, "Trying to create a new connection.");
                mBluetoothDeviceAddress = address;
                return true;
            }

        /**
         * Disconnects an existing connection or cancel a pending connection. The
         * disconnection result is reported asynchronously through the
         * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
         * callback.
         */
        public void disconnect()
            {
                DebugUtils.logInfo(TAG, "On disconnect()");
                statusBluetooth = false;
                if (mBluetoothAdapter == null || mBluetoothGatt == null)
                    {
                        DebugUtils.logInfo(TAG, "BluetoothAdapter not initialized");
                        return;
                    }
                mBluetoothGatt.disconnect();
            }

        /**
         * After using a given BLE device, the app must call this method to ensure
         * resources are released properly.
         */
        public void close()
            {
                DebugUtils.logInfo(TAG, "On close()");
                if (mBluetoothGatt == null)
                    {
                        return;
                    }
                mBluetoothGatt.close();
                mBluetoothGatt = null;
            }

        /**
         * Request a read on a given {@code BluetoothGattCharacteristic}. The read
         * result is reported asynchronously through the
         * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
         * callback.
         *
         * @param characteristic
         *            The characteristic to read from.
         */

        public void readCharacteristic(BluetoothGattCharacteristic characteristic)
            {
                DebugUtils.logInfo(TAG, "On readCharacteristic(BluetoothGattCharacteristic characteristic)");
                if (mBluetoothAdapter == null || mBluetoothGatt == null)
                    {
                        Log.w(TAG, "BluetoothAdapter not initialized");
                        return;
                    }
                mBluetoothGatt.readCharacteristic(characteristic);
            }

        /**
         * Enables or disables notification on a give characteristic.
         *
         * @param characteristic
         *            Characteristic to act on.
         * @param enabled
         *            If true, enable notification. False otherwise.
         */

        public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled)
            {
                DebugUtils.logInfo(TAG, "On setCharacteristicNotification()");
                if (mBluetoothAdapter == null || mBluetoothGatt == null)
                    {
                        Log.w(TAG, "BluetoothAdapter not initialized");
                        return;
                    }
                mBluetoothGatt.setCharacteristicNotification(characteristic, false);
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(BluetoothGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue((enabled) ? BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE:BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
                mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
/*
                UUID uuid = UUID.fromString(BluetoothGattAttributes.CLIENT_CHARACTERISTIC_CONFIG);
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(uuid);
                if(enabled)
                    {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    }
                else
                    {
                        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                    }
                mBluetoothGatt.writeDescriptor(descriptor);
*/
            }

        public void setCharacteristicIndication(BluetoothGattCharacteristic characteristic, boolean enabled)
            {
                DebugUtils.logInfo(TAG, "On setCharacteristicIndication()");
                if (mBluetoothAdapter == null || mBluetoothGatt == null)
                    {
                        Log.w(TAG, "BluetoothAdapter not initialized");
                        return;
                    }
                DebugUtils.logInfo(TAG, "setCharacteristicIndication");
                UUID uuid = UUID.fromString(BluetoothGattAttributes.CLIENT_CHARACTERISTIC_CONFIG);
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(uuid);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
                mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            }

        /**
         * Retrieves a list of supported GATT services on the connected device. This
         * should be invoked only after {@code BluetoothGatt#discoverServices()}
         * completes successfully.
         *
         * @return A {@code List} of supported services.
         */
        public List<BluetoothGattService> getSupportedGattServices()
            {
                if (mBluetoothGatt == null)
                    {
                        return null;
                    }
                return mBluetoothGatt.getServices();
            }

        public static void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] data )
            {
                if (mBluetoothAdapter == null || mBluetoothGatt == null)
                    {
                        Log.w(TAG, "BluetoothAdapter not initialized");
                        return;
                    }
                if (characteristic == null)
                    {
                        Log.w(TAG, "Send characteristic not found");
                    }
                if (characteristic != null) {
                    characteristic.setValue(data);
                }
                boolean status = mBluetoothGatt.writeCharacteristic(characteristic);
            }

        public void getBattery()
            {
                if (mBluetoothGatt == null)
                    {
                        DebugUtils.logError(TAG, "lost connection");
                    }
                BluetoothGattService batteryService = mBluetoothGatt.getService(UUID.fromString(BluetoothGattAttributes.BATTERY_SERVICE));
                if(batteryService == null)
                    {
                        DebugUtils.logInfo(TAG, "Battery service not found!");
                        return;
                    }
                BluetoothGattCharacteristic batteryLevel = batteryService.getCharacteristic(UUID.fromString(BluetoothGattAttributes.BATTERY_CHARACTERISTIC_CHARGE));
                if(batteryLevel == null)
                    {
                        DebugUtils.logInfo(TAG, "Battery level not found!");
                        return;
                    }
                mBluetoothGatt.readCharacteristic(batteryLevel);
                mBluetoothGatt.setCharacteristicNotification(batteryLevel, true);
            }

        public void getFIFO()
        {
            if (mBluetoothGatt == null)
            {
                DebugUtils.logError(TAG, "lost connection");
            }
            BluetoothGattService batteryService = mBluetoothGatt.getService(UUID.fromString(BluetoothGattAttributes.URBAND_GESTURE_SERVICE));
            if(batteryService == null)
            {
                DebugUtils.logInfo(TAG, "fifo service not found!");
                return;
            }
            BluetoothGattCharacteristic FIFO = batteryService.getCharacteristic(UUID.fromString(BluetoothGattAttributes.UGS_DEV2));
            if(FIFO == null)
            {
                DebugUtils.logInfo(TAG, "fifo level not found!");
                return;
            }
            mBluetoothGatt.readCharacteristic(FIFO);
            mBluetoothGatt.setCharacteristicNotification(FIFO, true);
        }
    }
