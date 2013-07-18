package com.BrascomeTechnologies.Viewer;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.view.WindowManager.LayoutParams;

public class Remote extends Activity {

	static int keyMap[];

    static {
        keyMap = new int[KeyEvent.getMaxKeyCode()];

        for (int i = 0; i < KeyEvent.getMaxKeyCode(); ++i) {
            keyMap[i] = 0;
        }

        keyMap[KeyEvent.KEYCODE_0] = '0';
        keyMap[KeyEvent.KEYCODE_1] = '1';
        keyMap[KeyEvent.KEYCODE_2] = '2';
        keyMap[KeyEvent.KEYCODE_3] = '3';
        keyMap[KeyEvent.KEYCODE_4] = '4';
        keyMap[KeyEvent.KEYCODE_5] = '5';
        keyMap[KeyEvent.KEYCODE_6] = '6';
        keyMap[KeyEvent.KEYCODE_7] = '7';
        keyMap[KeyEvent.KEYCODE_8] = '8';
        keyMap[KeyEvent.KEYCODE_9] = '9';
        keyMap[KeyEvent.KEYCODE_A] = 'a';
        keyMap[KeyEvent.KEYCODE_B] = 'b';
        keyMap[KeyEvent.KEYCODE_C] = 'c';
        keyMap[KeyEvent.KEYCODE_D] = 'd';
        keyMap[KeyEvent.KEYCODE_E] = 'e';
        keyMap[KeyEvent.KEYCODE_F] = 'f';
        keyMap[KeyEvent.KEYCODE_G] = 'g';
        keyMap[KeyEvent.KEYCODE_H] = 'h';
        keyMap[KeyEvent.KEYCODE_I] = 'i';
        keyMap[KeyEvent.KEYCODE_J] = 'j';
        keyMap[KeyEvent.KEYCODE_K] = 'k';
        keyMap[KeyEvent.KEYCODE_L] = 'l';
        keyMap[KeyEvent.KEYCODE_M] = 'm';
        keyMap[KeyEvent.KEYCODE_N] = 'n';
        keyMap[KeyEvent.KEYCODE_O] = 'o';
        keyMap[KeyEvent.KEYCODE_P] = 'p';
        keyMap[KeyEvent.KEYCODE_Q] = 'q';
        keyMap[KeyEvent.KEYCODE_R] = 'r';
        keyMap[KeyEvent.KEYCODE_S] = 's';
        keyMap[KeyEvent.KEYCODE_T] = 't';
        keyMap[KeyEvent.KEYCODE_U] = 'u';
        keyMap[KeyEvent.KEYCODE_V] = 'v';
        keyMap[KeyEvent.KEYCODE_W] = 'w';
        keyMap[KeyEvent.KEYCODE_X] = 'x';
        keyMap[KeyEvent.KEYCODE_Y] = 'y';
        keyMap[KeyEvent.KEYCODE_Z] = 'z';

        keyMap[KeyEvent.KEYCODE_APOSTROPHE] = '\'';
        keyMap[KeyEvent.KEYCODE_AT] = '@';
        keyMap[KeyEvent.KEYCODE_BACKSLASH] = '\\';
        keyMap[KeyEvent.KEYCODE_COMMA] = ',';
        keyMap[KeyEvent.KEYCODE_ENTER] = '\n';
        keyMap[KeyEvent.KEYCODE_EQUALS] = '=';
        keyMap[KeyEvent.KEYCODE_GRAVE] = '`';
        keyMap[KeyEvent.KEYCODE_LEFT_BRACKET] = '[';
        keyMap[KeyEvent.KEYCODE_MINUS] = '-';
        keyMap[KeyEvent.KEYCODE_PERIOD] = '.';
        keyMap[KeyEvent.KEYCODE_PLUS] = '+';
        keyMap[KeyEvent.KEYCODE_POUND] = '#';
        keyMap[KeyEvent.KEYCODE_RIGHT_BRACKET] = ']';
        keyMap[KeyEvent.KEYCODE_SEMICOLON] = ';';
        keyMap[KeyEvent.KEYCODE_SLASH] = '/';
        keyMap[KeyEvent.KEYCODE_SPACE] = ' ';
        keyMap[KeyEvent.KEYCODE_STAR] = '*';
        keyMap[KeyEvent.KEYCODE_TAB] = '\t';

        // These few buddies are taken from an enum in InputManager.h.
        // Too few of them (yet?) to bother me about these magic numbers.
        keyMap[KeyEvent.KEYCODE_SHIFT_LEFT] = 304;
        keyMap[KeyEvent.KEYCODE_SHIFT_RIGHT] = 303;
        keyMap[KeyEvent.KEYCODE_ALT_LEFT] = 308;
        keyMap[KeyEvent.KEYCODE_ALT_RIGHT] = 307;
        keyMap[KeyEvent.KEYCODE_DEL] = 8; // SDLK_BACKSPACE
        keyMap[KeyEvent.KEYCODE_DPAD_DOWN] = 274;
        keyMap[KeyEvent.KEYCODE_DPAD_UP] = 273;
        keyMap[KeyEvent.KEYCODE_DPAD_LEFT] = 276;
        keyMap[KeyEvent.KEYCODE_DPAD_RIGHT] = 275;
        keyMap[KeyEvent.KEYCODE_BACK] = 27; // SDLK_ESCAPE (same as Tegra mapping)
        keyMap[KeyEvent.KEYCODE_MENU] = 319; // SDLK_MENU
        keyMap[KeyEvent.KEYCODE_POWER] = 320; // SDLK_POWER
        keyMap[KeyEvent.KEYCODE_HOME] = 278; // SDLK_HOME
        keyMap[KeyEvent.KEYCODE_SOFT_LEFT] = 310; // SDLK_LMETA
        keyMap[KeyEvent.KEYCODE_SOFT_RIGHT] = 309; // SDLK_RMETA

        // F1 is intentionally skipped as it is mapped to MENU on Tegra 
        keyMap[KeyEvent.KEYCODE_SEARCH] = 283; // SDLK_F2
        keyMap[KeyEvent.KEYCODE_CALL] = 284; // SDLK_F3 (same as Tegra mapping)
        keyMap[KeyEvent.KEYCODE_ENDCALL] = 285;	// SDLK_F4 (same as Tegra mapping)
        keyMap[KeyEvent.KEYCODE_CAMERA] = 286; // SDLK_F5
        keyMap[KeyEvent.KEYCODE_FOCUS] = 287; // SDLK_F6 (usually same button as CAMERA)
        keyMap[KeyEvent.KEYCODE_HEADSETHOOK] = 288; // SDLK_F7 (key on the headset cable)
	}

	private boolean editorConnected;
	private static Remote instance = null;

	private static final int inputPhaseMap[] = {
		0, // Down
		3, // Up
		1, // Move
		4, // Cancel
		4, // Outside
		0, // Pointer down
		3, // Pointer up
	   ~0  // Unknown
	};

	public static Remote getInstance() {
		return instance;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	System.out.println("starting main activity");
    	instance = this;
    	new MessageServer();
    	new MainView(this);
    	setEditorConnected(false);

		java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
		java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        setContentView(MainView.getInstance());
        MessageServer.getInstance().start();
        System.out.println("started main activity");
    }

    public boolean onTouchEvent(MotionEvent event) {
    	if (isEditorConnected()) {
    		int action = event.getAction() & MotionEvent.ACTION_MASK;
    		int index = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >>
    			MotionEvent.ACTION_POINTER_ID_SHIFT;

    		int pointerCount = event.getPointerCount();
    		int historySize = event.getHistorySize();

    		for (int i = 0; i < pointerCount; i++) {
    			for (int j = 0; j < historySize; j++) {
    				float x = event.getHistoricalX(i, j);
    				float y = event.getHistoricalY(i, j);
    				long tstamp = event.getEventTime();
    				int pointer = event.getPointerId(i);
    				MessageServer.getInstance().queueTouchEvent(x, y, tstamp, pointer, inputPhaseMap[MotionEvent.ACTION_MOVE]);
    			}

    			int pointerAction = (i == index) ? action : MotionEvent.ACTION_MOVE;
    			float x = event.getX(i);
    			float y = event.getY(i);
    			long tstamp = event.getEventTime();
    			int pointer = event.getPointerId(i);
    			MessageServer.getInstance().queueTouchEvent(x, y, tstamp, pointer, inputPhaseMap[pointerAction]);
    		}
    	}

    	return true;
    }

    public boolean onTrackballEvent(MotionEvent event) {
    	if (isEditorConnected()) {
    		float x = event.getX();
    		float y = event.getY();
    		MessageServer.getInstance().queueTrackBallEvent(x, y);
    	}

    	return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            return super.onKeyDown(keyCode, event);
        }
        
        // if (mIsShowingInput)
        //	return super.onKeyDown (keyCode, event);

		// if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
		//	mGlView.queueEvent(new Runnable(){ public void run(){ nativeJoyButtonState (0, 0, 1); } } );
		//	return true;
		// }

		int key = keyMap[keyCode];
		int unicode = event.getUnicodeChar();

		MessageServer.getInstance().queueKeyEvent(key, 1, unicode);

		return true;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            return super.onKeyUp(keyCode, event);
        }
        
        // if (mIsShowingInput)
        //	return super.onKeyDown (keyCode, event);

		// if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
		//	mGlView.queueEvent(new Runnable(){ public void run(){ nativeJoyButtonState (0, 0, 1); } } );
		//	return true;
		// }

		int key = keyMap[keyCode];
		int unicode = event.getUnicodeChar();

		MessageServer.getInstance().queueKeyEvent(key, 0, unicode);

		return true;
    }

    public boolean isEditorConnected() {
    	return editorConnected;
    }

    public void setEditorConnected(boolean connected) {
    	editorConnected = connected;
    }
}
