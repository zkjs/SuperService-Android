package com.zkjinshi.superservice.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import com.zkjinshi.superservice.R;

/**
 * 自定义提示对话框
 * 开发者：dujiande
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CustomImgDialog extends Dialog {

	public CustomImgDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomImgDialog(Context context) {
		super(context);
	}

	public static class Builder {

		private Context context;
		private Uri imagePath = null;
		private String positiveButtonText;

		private OnClickListener positiveButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}


		public Builder setMessageIcon(Uri imagePath) {
			this.imagePath = imagePath;
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

		public Uri getImagePath() {
			return imagePath;
		}

		public void setImagePath(Uri imagePath) {
			this.imagePath = imagePath;
		}

		public CustomImgDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final CustomImgDialog dialog = new CustomImgDialog(context,
					R.style.customDialog);
			View layout = inflater.inflate(R.layout.custom_img_dialog, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			if (positiveButtonText != null) {
				((TextView) layout.findViewById(R.id.btnTv))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((TextView) layout.findViewById(R.id.btnTv))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.btnTv).setVisibility(
						View.GONE);
			}
			if (imagePath != null) {
				SimpleDraweeView imgSdv = (SimpleDraweeView)layout.findViewById(R.id.img_sdv);
				imgSdv.setImageURI(imagePath);
			}
			dialog.setContentView(layout);
			return dialog;
		}
	}
}