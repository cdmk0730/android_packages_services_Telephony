/*
 * Copyright (C) 2013 The MoKee OpenSource Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.phone;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;

public class CustomSMSCSettingPreference extends DialogPreference {

	public CustomSMSCSettingPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;
		mPhone = PhoneFactory.getDefaultPhone();
	}

	private Context mContext;

	private static final int EVENT_QUERY_SMSC_DONE = 1005;
	private static final int EVENT_UPDATE_SMSC_DONE = 1006;

	private Phone mPhone = null;

	private EditText etSMSC;
	private Button btnUpdate;

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		super.onPrepareDialogBuilder(builder);
		builder.setView(initCustomDialogView());
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
	}

	@Override
	protected void showDialog(Bundle state) {
		super.showDialog(state);
	}

	private View initCustomDialogView() {
		LinearLayout layoutSMSCMain = new LinearLayout(mContext);
		layoutSMSCMain.setOrientation(LinearLayout.VERTICAL);
		etSMSC = new EditText(mContext);
		etSMSC.setGravity(Gravity.CENTER_HORIZONTAL);
		etSMSC.setSingleLine(true);
		layoutSMSCMain.addView(etSMSC);
		refreshSmsc();
		LinearLayout layoutSMSCButton = new LinearLayout(mContext);
		layoutSMSCButton.setOrientation(LinearLayout.HORIZONTAL);
		layoutSMSCButton.setGravity(Gravity.CENTER_HORIZONTAL);
		Button btnRefresh = new Button(mContext);
		btnRefresh.setText(R.string.smsc_refresh);
		btnRefresh.setWidth(150);
		btnRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				refreshSmsc();
			}
		});
		layoutSMSCButton.addView(btnRefresh);
		btnUpdate = new Button(mContext);
		btnUpdate.setText(R.string.smsc_update);
		btnUpdate.setWidth(150);
		btnUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mPhone.setSmscAddress(etSMSC.getText().toString(),
				mHandler.obtainMessage(EVENT_UPDATE_SMSC_DONE));
			}
		});
		layoutSMSCButton.addView(btnUpdate);
		layoutSMSCMain.addView(layoutSMSCButton);
		return layoutSMSCMain;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			AsyncResult ar;
			switch (msg.what) {
			case EVENT_QUERY_SMSC_DONE:
				ar = (AsyncResult) msg.obj;
				if (ar.exception != null) {
					etSMSC.setText("refresh error");
				} else {
					etSMSC.setText((String) ar.result);
				}
				break;
			case EVENT_UPDATE_SMSC_DONE:
				btnUpdate.setEnabled(true);
				ar = (AsyncResult) msg.obj;
				if (ar.exception != null) {
					etSMSC.setText("update error");
				}
				break;
			default:
				break;
			}
		}
	};

	private void refreshSmsc() {
		mPhone.getSmscAddress(mHandler.obtainMessage(EVENT_QUERY_SMSC_DONE));
	}
}
