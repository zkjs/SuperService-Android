package com.zkjinshi.superservice.pad.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zkjinshi.base.R;

/**
 * 自定义提示对话框
 * 开发者：杜健德
 * 日期：2016/5/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CustomInputDialog extends Dialog {

	public CustomInputDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomInputDialog(Context context) {
		super(context);
	}

	public static class Builder {

		private Context context;
		private String title;
		private String message;
		private int gravity = -1;
		private String tint;
		private int visibility = -1;
		private String positiveButtonText;
		private String negativeButtonText;

		public EditText inputEt;

		private OnClickListener positiveButtonClickListener,
				negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		public Builder setTint(String tint) {
			this.tint = tint;
			return this;
		}

		public Builder setTint(int tint) {
			this.tint = (String) context.getText(tint);
			return this;
		}

		public Builder setGravity(int gravity) {
			this.gravity = gravity;
			return this;
		}



		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setPositiveButton(int positiveButtonText,
										 OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
										 OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
										 OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
										 OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public CustomInputDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final CustomInputDialog dialog = new CustomInputDialog(context,
					R.style.customDialog);
			View layout = inflater.inflate(R.layout.custom_input_dialog, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			if(TextUtils.isEmpty(title)){
				layout.findViewById(R.id.dialogTitle).setVisibility(View.GONE);
			}else{
				((TextView) layout.findViewById(R.id.dialogTitle)).setText(title);
			}
			if (positiveButtonText != null) {
				((TextView) layout.findViewById(R.id.dialogRightBtn))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((TextView) layout.findViewById(R.id.dialogRightBtn))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.dialogRightBtn).setVisibility(
						View.GONE);
			}
			if (negativeButtonText != null) {
				((TextView) layout.findViewById(R.id.dialogLeftBtn))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((TextView) layout.findViewById(R.id.dialogLeftBtn))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.dialogLeftBtn)
						.setVisibility(View.GONE);
			}
			if (message != null) {
				((TextView) layout.findViewById(R.id.dialogContent))
						.setVisibility(View.VISIBLE);
				((TextView) layout.findViewById(R.id.dialogContent))
						.setText(message);
			}else {
				((TextView) layout.findViewById(R.id.dialogContent))
						.setVisibility(View.GONE);
			}

			if (gravity != -1) {
				((LinearLayout) layout.findViewById(R.id.dialogText))
						.setGravity(gravity);
			}
			inputEt = (EditText)layout.findViewById(R.id.input_et);
			if(!TextUtils.isEmpty(tint)){
				inputEt.setHint(tint);
			}
			dialog.setContentView(layout);
			return dialog;
		}
	}
}