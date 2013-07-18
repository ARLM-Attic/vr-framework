package com.BrascomeTechnologies.Viewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class MainView extends View {

	private Bitmap logo;
	private Bitmap currentBitmap;
	private boolean imageSynced;
	private int screenWidth;
	private int screenHeight;
	private static MainView instance = null;

	public MainView(Context context) {
		super(context);
		instance = this;
		Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
		logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
		currentBitmap = null;
		imageSynced = false;
	}

	public static MainView getInstance() {
		return instance;
	}

	protected void onDraw(Canvas canvas) {
    	if (!imageSynced) {
    		Paint blackPaint = new Paint();
    		blackPaint.setARGB(255, 0, 0, 0);
        	canvas.drawPaint(blackPaint);
    		canvas.drawBitmap(logo, 0.0f, 0.0f, blackPaint);
    		Paint whitePaint = new Paint();
    		whitePaint.setARGB(255, 255, 255, 255);
    		whitePaint.setTextSize(26.0f);
    		whitePaint.setSubpixelText(true);
    		whitePaint.setAntiAlias(true);
    		canvas.drawText("Connect this device with", 320.0f, 60.0f, whitePaint);
    		canvas.drawText("USB cable to your computer and", 320.0f, 100.0f, whitePaint);
    		canvas.drawText("play the game in editor", 320.0f, 140.0f, whitePaint);
    	} else {
    		if (currentBitmap != null) {
    			Paint blackPaint = new Paint();
    			blackPaint.setARGB(255, 0, 0, 0);
    			Rect src = new Rect(0, 0, currentBitmap.getWidth(), currentBitmap.getHeight());
    			Rect dst = new Rect(0, 0, screenWidth, screenHeight);
    			canvas.drawBitmap(currentBitmap, src, dst, blackPaint);
    		}
    	}
    }

	public void setFrameImage(Bitmap bmp) {
		currentBitmap = bmp;
		imageSynced = true;
		System.gc();
		postInvalidate();
	}

	public void setImageSynced(boolean synced) {
		if (imageSynced != synced) {
			imageSynced = synced;
			postInvalidate();
		}
	}
}
