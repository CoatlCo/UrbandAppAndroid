/**
 * Filename:        ColorUtils.java
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

import android.bluetooth.BluetoothGattCharacteristic;
import android.graphics.Color;
import android.os.SystemClock;

import java.util.UUID;

import dev.coatl.co.urband_basic.model.objects.BluetoothGattAttributes;
import dev.coatl.co.urband_basic.presenter.activities.ActivityHomePanel;
import dev.coatl.co.urband_basic.presenter.fragments.FragmentMenuPanel;
import dev.coatl.co.urband_basic.presenter.services.BluetoothService;
import dev.coatl.co.urband_basic.view.widgets.colorpicker.ColorPicker.OnColorChangedListener;
import dev.coatl.co.urband_basic.view.widgets.colorpicker.DialogColorPicker;

public class ColorUtils {
	
	private static DialogColorPicker dialog;
	
	public static String integerToHexColor(int color){
		return String.format("%06X", (0xFFFFFF & color));
	}
	
	public static int hexToIntegerColor(String colorString){
		return Color.parseColor(colorString);
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}
	
	public static void showDialogPicker(final ActivityHomePanel activity)
		{
			dialog = new DialogColorPicker(activity, Color.RED);
			dialog.setOnColorChangedListener(new OnColorChangedListener() {
			@Override
			public void onColorChanged(int color)
				{
					String hexColor = ColorUtils.integerToHexColor(color);
					final byte[] RGBV = new byte[12] ;
					final FragmentMenuPanel fg_menu = ((FragmentMenuPanel) ActivityHomePanel.getActivityHomePanel().getSupportFragmentManager().findFragmentByTag("fg_menu"));

					RGBV[0] = (byte) fg_menu.VL1_skBr.getProgress();; // Vibrator L1
					RGBV[1] = (byte) fg_menu.VL2_skBr.getProgress();; // Vibrator L2
					RGBV[2] = (byte) fg_menu.RGBIntensity1_skBr.getProgress();; // RGB Intensity L1
					RGBV[3] = (byte) fg_menu.RGBIntensity2_skBr.getProgress();; // RGB Intensity L2

					RGBV[4] = (byte) fg_menu.seekBar1.getProgress(); // T1 (attack)
					RGBV[5] = (byte) fg_menu.seekBar2.getProgress(); // T2 (level 1 to level 2)
					RGBV[6] = (byte) fg_menu.seekBar3.getProgress(); // T3 (sustain)
					RGBV[7] = (byte) fg_menu.seekBar4.getProgress(); // T4 (release)
					RGBV[8] = (byte) fg_menu.seekBar5.getProgress(); // T5 (off time between repetitions)

					final byte[] bytes = hexStringToByteArray(hexColor);
					RGBV[9]  = (byte) (bytes[0] & 0xFF); // RED
					RGBV[10] = (byte) (bytes[1] & 0xFF); // GREEN
					RGBV[11] = (byte) (bytes[2] & 0xFF); // BLUE

					// Write haptic configuration characteristic
					final UUID UUID_UHS_cfg = UUID.fromString(BluetoothGattAttributes.UHS_hapticsCfg);
					final UUID UUID_UHS = UUID.fromString(BluetoothGattAttributes.URBAND_HAPTICS_SERVICE);
					final BluetoothGattCharacteristic characteristic = BluetoothService.mBluetoothGatt.getService(UUID_UHS).getCharacteristic(UUID_UHS_cfg);
					BluetoothService.writeCharacteristic(characteristic, RGBV);

					// Wait for next connection
					SystemClock.sleep(300);

					final BluetoothGattCharacteristic characteristic_ctl = BluetoothService.mBluetoothGatt.getService(UUID.fromString(BluetoothGattAttributes.URBAND_HAPTICS_SERVICE)).getCharacteristic(UUID.fromString(BluetoothGattAttributes.UHS_hapticsCtl));
					final byte[] ctl = new byte[1];
					ctl[0] = (byte) 0x03;
					BluetoothService.writeCharacteristic(characteristic_ctl, ctl);
					DebugUtils.logInfo(hexColor);
				}
			});
		dialog.show();
		}
}
