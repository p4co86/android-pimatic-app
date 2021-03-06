package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.DeviceAttribute;
import com.dgmltn.pimatic.util.Events;
import com.dgmltn.pimatic.util.SpannableBuilder;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by doug on 6/6/15.
 */
public class DefaultDeviceView extends DeviceView {

	@Bind(R.id.device_name)
	TextView vName;

	@Bind(R.id.device_content)
	TextView vContent;

	public static DeviceViewMapper.Matcher matcher = new DeviceViewMapper.Matcher() {

		@Override
		public boolean matches(Device d) {
			return true;
		}

		@Override
		public int getLayoutResId() {
			return R.layout.view_default_device;
		}

	};

	public DefaultDeviceView(Context context) {
		super(context);
	}

	public DefaultDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DefaultDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(21)
	public DefaultDeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ButterKnife.bind(this);
	}

	@Subscribe
	public void otto(Events.DeviceChanged e) {
		super.otto(e);
	}

	public void bind() {
		vName.setText(device.name);
		vContent.setText(getSpannedString(getContext()));
	}

	public SpannableStringBuilder getSpannedString(Context context) {
		SpannableBuilder builder = new SpannableBuilder(context);

		for (DeviceAttribute attr : device.attributes) {
			if (attr.hidden) {
				continue;
			}

			String unit = attr.unit;

			if (!TextUtils.isEmpty(attr.acronym)) {
				builder.append(attr.acronym.toUpperCase() + "\u00A0",
					new StyleSpan(android.graphics.Typeface.BOLD),
					new ForegroundColorSpan(Color.GRAY),
					new RelativeSizeSpan(0.8f));
			}
			if (TextUtils.isEmpty(attr.value)) {
				String unknown = context.getString(
					"number".equals(attr.type)
						? R.string.unknown_number
						: R.string.Unknown
				);
				builder.append(unknown,	new ForegroundColorSpan(Color.GRAY));
			}
			else if ("number".equals(attr.type)) {
				double d = Double.parseDouble(attr.value);
				d = Math.round(d * 100) / 100d;
				if (attr.unit != null && "B".equals(attr.unit)) {
					long bytes = (long) d;
					int base = 1000;
					if (bytes < base) {
						builder.append(attr.value);
					}
					else {
						// http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
						int exp = (int) (Math.log(bytes) / Math.log(base));
						unit = "KMGTPE".charAt(exp - 1) + "B";
						builder.append(String.format("%.2f", bytes / Math.pow(base, exp)));
					}
				}
				else {
					builder.append(Double.toString(d));
				}
			}
			else {
				builder.append(attr.value);
			}
			if (!TextUtils.isEmpty(unit)) {
				builder.append(unit, new ForegroundColorSpan(Color.GRAY));
			}
			builder.append(" ");
		}
		return builder.build();
	}
}
