/**
 * Filename:        FragmentMenuPanel.java
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
package dev.coatl.co.urband_basic.presenter.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dev.coatl.co.urband_basic.R;
import dev.coatl.co.urband_basic.presenter.activities.ActivityHomePanel;
import dev.coatl.co.urband_basic.view.utils.DebugUtils;

import static dev.coatl.co.urband_basic.view.utils.ColorUtils.showDialogPicker;

public class FragmentMenuPanel extends Fragment implements OnClickListener{

	private final static String TAG = FragmentMenuPanel.class.getSimpleName();

    private ListView lv;
    public Activity mActivity;
    private List<String> fruits_list = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

	public SeekBar VL1_skBr;
	public SeekBar VL2_skBr;
	public SeekBar RGBIntensity1_skBr;
	public SeekBar RGBIntensity2_skBr;

    public SeekBar seekBar1;
    public SeekBar seekBar2;
    public SeekBar seekBar3;
    public SeekBar seekBar4;
    public SeekBar seekBar5;

    public FragmentMenuPanel(){
		DebugUtils.logInfo(TAG, "FragmentMenuPanel constructor");
    }

	@Override
	public void onStart() {
		super.onStart();
	}

    public void updateContent(String s){
        lv.setAdapter(arrayAdapter);
        fruits_list.add(s);
        scrollMyListViewToBottom();
    }

    private void scrollMyListViewToBottom() {
        lv.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                lv.setSelection(arrayAdapter.getCount() - 1);
            }
        });
    }


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/* Initialize layout */
		View rootView = inflater.inflate(R.layout.fragment_menu_panel, null);

		VL1_skBr = (SeekBar) rootView.findViewById(R.id.VL1_skBr);
		VL2_skBr = (SeekBar) rootView.findViewById(R.id.VL2_skBr);
		RGBIntensity1_skBr = (SeekBar) rootView.findViewById(R.id.RGBIntensity1_skBr);
		RGBIntensity2_skBr = (SeekBar) rootView.findViewById(R.id.RGBIntensity2_skBr);

        seekBar1 = (SeekBar) rootView.findViewById(R.id.seekBar1);
		seekBar2 = (SeekBar) rootView.findViewById(R.id.seekBar2);
		seekBar3 = (SeekBar) rootView.findViewById(R.id.seekBar3);
		seekBar4 = (SeekBar) rootView.findViewById(R.id.seekBar4);
		seekBar5 = (SeekBar) rootView.findViewById(R.id.seekBar5);

        lv = (ListView) rootView.findViewById(R.id.log_console_LstView);
        arrayAdapter = new ArrayAdapter<>(this.getContext(), R.layout.simple_console_log, fruits_list);
        lv.setAdapter(arrayAdapter);

		final TextView mVL1_txtVw = (TextView) rootView.findViewById(R.id.VL1_txtVw);
		final TextView mVL2_txtVw = (TextView) rootView.findViewById(R.id.VL2_txtVw);
		final TextView mRGBIntensity1_txtVw = (TextView) rootView.findViewById(R.id.RGBIntensity1_txtVw);
		final TextView mRGBIntensity2_txtVw = (TextView) rootView.findViewById(R.id.RGBIntensity2_txtVw);

		final TextView textView1 = (TextView) rootView.findViewById(R.id.textView1);
		final TextView textView2 = (TextView) rootView.findViewById(R.id.textView2);
		final TextView textView3 = (TextView) rootView.findViewById(R.id.textView3);
		final TextView textView4 = (TextView) rootView.findViewById(R.id.textView4);
		final TextView textView5 = (TextView) rootView.findViewById(R.id.textView5);

		final TextView txtVwURBAND = (TextView) rootView.findViewById(R.id.txtVwURBAND);

		VL1_skBr.setProgress(0);
		VL2_skBr.setProgress(0);
		RGBIntensity1_skBr.setProgress(100);
		RGBIntensity2_skBr.setProgress(8);

		seekBar1.setProgress(100);
		seekBar2.setProgress(120);
		seekBar3.setProgress(140);
		seekBar4.setProgress(160);
		seekBar5.setProgress(180);

		int mProgress = VL1_skBr.getProgress();
		mVL1_txtVw.setText(String.format(Locale.US, "%d", mProgress));

		mProgress = VL2_skBr.getProgress();
		mVL2_txtVw.setText(String.format(Locale.US, "%d", mProgress));

		mProgress = RGBIntensity1_skBr.getProgress();
		mRGBIntensity1_txtVw.setText(String.format(Locale.US, "%d", mProgress));

		mProgress = RGBIntensity2_skBr.getProgress();
		mRGBIntensity2_txtVw.setText(String.format(Locale.US, "%d", mProgress));

		mProgress = seekBar1.getProgress();
		textView1.setText(String.format(Locale.US, "%d", mProgress));

		mProgress = seekBar2.getProgress();
		textView2.setText(String.format(Locale.US, "%d", mProgress));

		mProgress = seekBar3.getProgress();
		textView3.setText(String.format(Locale.US, "%d", mProgress));

		mProgress = seekBar4.getProgress();
		textView4.setText(String.format(Locale.US, "%d", mProgress));

		mProgress = seekBar5.getProgress();
		textView5.setText(String.format(Locale.US, "%d", mProgress));

		txtVwURBAND.setOnClickListener(this);

		VL1_skBr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				mVL1_txtVw.setText(Integer.toString(progress));
			}

		});

		VL2_skBr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				mVL2_txtVw.setText(Integer.toString(progress));
			}

		});

		RGBIntensity1_skBr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				mRGBIntensity1_txtVw.setText(Integer.toString(progress));
			}

		});

		RGBIntensity2_skBr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				mRGBIntensity2_txtVw.setText(Integer.toString(progress));
			}

		});

		//set change listener
        final SeekBar.OnSeekBarChangeListener onSeekBarChangeListener1 = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                DebugUtils.logInfo("onProgressChanged --- 1: " + Integer.toString(progress) + " --------------------------------------------------------------");
                textView1.setText(Integer.toString(progress));
            }

        };

        seekBar1.setOnSeekBarChangeListener(onSeekBarChangeListener1);

		seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				DebugUtils.logInfo("onProgressChanged ---- 2: " + Integer.toString(progress) + " -------------------------------------------------------------");
				textView2.setText(Integer.toString(progress));
			}

		});

		seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				DebugUtils.logInfo("onProgressChanged --- 3: " + Integer.toString(progress) + "--------------------------------------------------------------");
				textView3.setText(Integer.toString(progress));
			}

		});

		seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				DebugUtils.logInfo("onProgressChanged ---- 4: " + Integer.toString(progress) + " -------------------------------------------------------------");
				textView4.setText(Integer.toString(progress));
			}

		});

		seekBar5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				DebugUtils.logInfo("onProgressChanged 5: " + Integer.toString(progress) + " -------------------------------------------------------------");
				textView5.setText(Integer.toString(progress));
			}

		});

		mActivity = getActivity();
		/*Returns generates view */
		return rootView;
	}



	@Override
	public void onClick(View v)
		{
			/* Gets the ID of the clicked object */
			int id = v.getId();
			DebugUtils.logInfo(TAG,Integer.toString(id));
			showDialogPicker((ActivityHomePanel) mActivity);
		}


}
