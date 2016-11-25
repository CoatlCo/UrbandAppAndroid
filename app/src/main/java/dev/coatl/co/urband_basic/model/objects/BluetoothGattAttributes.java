/**
 * Filename:        BluetoothGattAttributes.java
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
package dev.coatl.co.urband_basic.model.objects;
import java.util.HashMap;


public class BluetoothGattAttributes
    {
        private static HashMap<String, String> attributes = new HashMap();
        public static String CLIENT_CHARACTERISTIC_CONFIG   = "00002902-0000-1000-8000-00805f9b34fb";

        public static String BATTERY_SERVICE                = "0000180f-0000-1000-8000-00805f9b34fb";
        public static String BATTERY_CHARACTERISTIC_CHARGE  = "00002a19-0000-1000-8000-00805f9b34fb";

        public static String URBAND_GESTURE_SERVICE         = "0000fa00-0000-1000-8000-00805f9b34fb";
        public static String UGS_GESTURE                    = "0000fa01-0000-1000-8000-00805f9b34fb";
        public static String UGS_DEV0                       = "0000fa08-0000-1000-8000-00805f9b34fb"; // 1 bytes  // coeff offset
        public static String UGS_DEV1                       = "0000fa09-0000-1000-8000-00805f9b34fb"; // 16 bytes // data for update coeff
        public static String UGS_DEV2                       = "0000fa0A-0000-1000-8000-00805f9b34fb"; // 4 bytes <--- debug battery voltage

        public static String URBAND_HAPTICS_SERVICE         = "0000fb00-0000-1000-8000-00805f9b34fb";
        public static String UHS_hapticsCtl                 = "0000fb01-0000-1000-8000-00805f9b34fb";
        public static String UHS_hapticsCfg                 = "0000fb02-0000-1000-8000-00805f9b34fb"; // 12 bytes array

        public static String URBAND_SECURITY_SERVICE        = "0000fc00-0000-1000-8000-00805f9b34fb";
        public static String USS_URBAND_ID                     = "0000fc01-0000-1000-8000-00805f9b34fb";
        public static String USS_URBAND_TOKEN                      = "0000fc02-0000-1000-8000-00805f9b34fb";
    }