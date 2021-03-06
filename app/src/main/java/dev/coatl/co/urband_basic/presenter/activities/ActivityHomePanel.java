/**
 * Filename:        ActivityHomePanel.java
 * Created:         Thu, 11 Aug 2016
 * Author:          Erick Rivera
 * Description:     This file contains implementation for main activity application.
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

package dev.coatl.co.urband_basic.presenter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import dev.coatl.co.urband_basic.R;
import dev.coatl.co.urband_basic.presenter.fragments.FragmentMenuPanel;
import dev.coatl.co.urband_basic.presenter.services.BluetoothService;
import dev.coatl.co.urband_basic.view.utils.BluetoothUtils;
import dev.coatl.co.urband_basic.view.utils.DebugUtils;

public class ActivityHomePanel extends FragmentActivity
	{
		private final static String TAG = ActivityHomePanel.class.getSimpleName();

		private BluetoothUtils bluetoothUtils;
        private static ActivityHomePanel mActivity;

		@Override
		protected void onStart()
			{
				super.onStart();
			}

		@Override
		protected void onDestroy()
			{
				super.onDestroy();
				DebugUtils.logInfo(TAG, "--- onDestroy()");
				if(BluetoothService.statusUrband.equals(BluetoothService.STATUS_URBAND_CONNECT))
					{
						unbindService(bluetoothUtils.getServiceConnection());
						bluetoothUtils.setBluetoothServiceToNULL();
						unregisterReceiver(bluetoothUtils.gattUpdateReceiver);
					}
				else
					{
						DebugUtils.logInfo(TAG, "debug: Urband disconnect...");
						unregisterReceiver(bluetoothUtils.gattUpdateReceiver);
					}
				BluetoothService.statusBluetooth = false;
				BluetoothService.statusUrband = BluetoothService.STATUS_URBAND_DEFAULT;
				BluetoothService.enabledSubscription = false;
			}

		@Override
		protected void onResume()
			{
				super.onResume();
				DebugUtils.logInfo(TAG, "--- onResume()");
				registerReceiver(bluetoothUtils.gattUpdateReceiver, bluetoothUtils.makeGattUpdateIntentFilter());
				DebugUtils.logInfo(TAG, "status of Urband m: " + BluetoothService.statusUrband);
				if ((bluetoothUtils.getBluetoothService() != null) && (!BluetoothService.statusBluetooth))
					{
						DebugUtils.logInfo(TAG, "onResume(): not yet connected, now trying to connect");
						final boolean result = bluetoothUtils.getBluetoothService().connect(BluetoothService.deviceAddress);
						DebugUtils.logInfo(TAG, "onResume(): Connect request result= " + result);
					}
			}

		@Override
		protected void onCreate(Bundle savedInstanceState)
			{
			    DebugUtils.logInfo(TAG, "Enter intro onCreate(Bundle savedInstanceState)");
				super.onCreate(savedInstanceState);
                mActivity = this;
				setContentView(R.layout.activity_homepanel);
				if (!BluetoothService.statusBluetooth)
					{
						setBluetoothConnection();
					}
				goToMenuPanel();
			} /* End onCreate  */

        public static ActivityHomePanel getActivityHomePanel(){
            return mActivity;
        }

		private void setBluetoothConnection()
			{
				DebugUtils.logInfo(TAG, "--- setBluetoothConnection()");
                
				Intent intent = getIntent();
				BluetoothService.deviceAddress = intent.getStringExtra(StartSearchDevice.EXTRAS_DEVICE_ADDRESS);
				DebugUtils.logInfo(TAG, "connect to address " + BluetoothService.deviceAddress);
				bluetoothUtils = new BluetoothUtils();
				bindService(new Intent(this, BluetoothService.class), bluetoothUtils.getServiceConnection(), BIND_AUTO_CREATE);
			}

		public void goToMenuPanel()
			{
				changeFragment(new FragmentMenuPanel(), "fg_menu");
			}

		public void changeFragment(Fragment fragment, String tag)
			{
				getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.fragment_container, fragment, tag)
					.commit();
			}

		@Override
		public void onBackPressed()
			{
						DebugUtils.logInfo("onBackPressed", "Back pressed");
						super.onBackPressed();
			}
	}
