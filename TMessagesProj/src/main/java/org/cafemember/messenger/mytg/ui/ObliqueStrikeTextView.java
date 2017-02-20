package org.cafemember.messenger.mytg.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class ObliqueStrikeTextView extends TextView{

	private int dividerColor = Color.RED;
	private Paint paint;
	Context context;

	public ObliqueStrikeTextView(Context context,int color) {
		super(context);
		dividerColor = color;
		this.context = context;
		init(context);
	}

	public ObliqueStrikeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ObliqueStrikeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	public void setDividerColor(int color){
		dividerColor = color;
		init(context);
		invalidate();
	}

	private void init(Context context) {
///		Resources resources = context.getResources();
		// replace with your color

		paint = new Paint();
		paint.setColor(dividerColor);
		// replace with your desired width
		paint.setStrokeWidth(4);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawLine(0,getHeight()-20 , getWidth(), 20, paint);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);
	}

	//    @Override
//    public void setText(CharSequence text, BufferType type) {
//        String str = text.toString();
//        if(str != null && !G.isDiscountOn) {
//            int x;
//            if( (x= str.indexOf("\n")) != -1)
//                str = str.substring(0,x);
//        }
//        super.setText(str, type);
//    }
}
