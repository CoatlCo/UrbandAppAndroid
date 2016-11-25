/*
 * Copyright 2012 Lars Werkman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.coatl.co.urband_basic.view.widgets.colorpicker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout.LayoutParams;

import dev.coatl.co.urband_basic.R;
import dev.coatl.co.urband_basic.view.widgets.colorpicker.ColorPicker.OnColorChangedListener;

public class DialogColorPicker extends Dialog implements OnClickListener{

	private View rooview;
	private View button_cancel;
	private View button_accept;
	private ColorPicker color_picker;
	private OnColorChangedListener onColorChangedListener;
	
	public DialogColorPicker(Context context, int color) {
		super(context);
		init(context, color);
	}
	
	@SuppressLint("InflateParams")
	private void init(Context context, int color){
		
		rooview = LayoutInflater.from(context).inflate(R.layout.view_color_picker, null);
		button_cancel = rooview.findViewById(R.id.button_cancel);
		button_accept = rooview.findViewById(R.id.button_accept);
		color_picker = (ColorPicker)rooview.findViewById(R.id.color_picker);

			color_picker.setColor(color);
			button_cancel.setOnClickListener(this);
			button_accept.setOnClickListener(this);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(rooview, new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		getWindow().setBackgroundDrawable(new ColorDrawable(color_picker.getOldCenterColor()));
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.button_accept){
			if(onColorChangedListener != null)
			{
				color_picker.setOldCenterColor(color_picker.getOldCenterColor());
				onColorChangedListener.onColorChanged(color_picker.getColor());
			}
				
			dismiss();
		}else if(id == R.id.button_cancel){
			cancel();
		}
	}
	
	public void setOnColorChangedListener(OnColorChangedListener onColorChangedListener) {
		this.onColorChangedListener = onColorChangedListener;
	}
	
}
