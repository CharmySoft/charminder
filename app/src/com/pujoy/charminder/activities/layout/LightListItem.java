package com.pujoy.charminder.activities.layout;

import com.pujoy.charminder.R;
import com.pujoy.charminder.other.C;
import com.pujoy.charminder.other.G;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LightListItem extends RelativeLayout {
	String sText;
	public LightListItem(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.StringStyleable, 0, 0);
		sText = array.getString(R.styleable.StringStyleable_text);
		array.recycle();

		this.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent motionEvent) {
				switch (motionEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundColor(C.COLOR_LIGHTBLUE_WD);
					break;
				case MotionEvent.ACTION_UP:
					v.setBackgroundColor(C.COLOR_LIGHTBLUE);
					break;
				}
				return false;
			}
		});
		this.setOnClickListener((OnClickListener) G.context);

	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		LayoutInflater.from(getContext()).inflate(R.layout.fragment_light_list_item,
				this, true);
		((TextView) findViewById(R.id.light_list_item_text)).setText(sText);
	}
}
