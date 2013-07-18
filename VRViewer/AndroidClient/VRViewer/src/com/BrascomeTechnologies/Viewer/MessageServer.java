package com.BrascomeTechnologies.Viewer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.WindowManager;

public class MessageServer extends Thread implements SensorEventListener {

	private SensorManager sensorManager;
	private ServerSocketChannel sschannel;
	private SocketChannel currentClient;
	private boolean sendOptions;
	private static MessageServer instance = null;
	
	private float[] mags = new float[3];
	private float[] accels = new float[3];
	private float[] RotationMat = new float[9];
	private float[] InclinationMat = new float[9];
	private float[] attitude = new float[3];


	public MessageServer() {
		instance = this;
		sschannel = null;
		currentClient = null;
		sensorManager = (SensorManager)Remote.getInstance().getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
		sendOptions = true;
	}

	public static MessageServer getInstance() {
		return instance;
	}

	// Main server loop
    public void run() {
    	try {
    		System.out.println("starting message server");
    		sschannel = ServerSocketChannel.open();
    		sschannel.configureBlocking(true);
    		sschannel.socket().bind(new InetSocketAddress(AndroidRemote.PORT));

    		for (;;) {
    			try {
    				System.out.println("waiting for connection");
    				// Block until connection is accepted
    				currentClient = sschannel.accept();
    				System.out.println("got connection");
    				
    				if (currentClient == null) {
    					System.out.println("No socket");
    				} else {
    					// Run server-client communication loop
    					setupSocketIO();

    					sendOptions = true;

    					MainView.getInstance().setImageSynced(false);
    				}
    			} catch (Exception e) {
    				System.out.println("Exception: " + e.getMessage());
    			}
    		}
    	} catch (Exception e) {
    		System.out.println("Socket exception");
    	}
    }

    private void setupSocketIO() throws Exception {
    	System.out.println("enter setupSocketIO");
    	Selector selector = null;

    	try {
    		selector = Selector.open();
    		currentClient.configureBlocking(false);
    		currentClient.register(selector, currentClient.validOps());
    	} catch (IOException e) {
    		throw e;
    	}

    	Remote.getInstance().setEditorConnected(true);

    	for (;;) {
    		System.out.println("enter for loop");
    		try {
    			selector.select();
    		} catch (Exception e) {
				Remote.getInstance().setEditorConnected(false);
    			return;
    		}

    		Iterator<SelectionKey> it = selector.selectedKeys().iterator();

    		while (it.hasNext()) {
    			System.out.println("enter while loop");
    			SelectionKey selKey = it.next();

    			it.remove();

    			try {
    				processIOSelection(selKey);
    			} catch (IOException e) {
    				selKey.cancel();
					Remote.getInstance().setEditorConnected(false);
    				return;
    			} catch (EditorDisconnectedException e) {
					Remote.getInstance().setEditorConnected(false);
    				return;
    			}
    		}
    	}
    }

    private void processIOSelection(SelectionKey selKey) throws Exception {
    	System.out.println("enter processIOSelection");
    	try {
    		Thread.sleep(5, 0);
    	} catch (Exception e) {
    	}

    	if (selKey.isValid()) {
    		if (selKey.isConnectable()) {
    			System.out.println("isConnectable");
    			// Do nothing
    			selKey.cancel();
    		} else if (selKey.isReadable()) {
    			System.out.println("isReadable");
    			SocketChannel schan = (SocketChannel)selKey.channel();
    			readInput(schan);
    		} else if (selKey.isWritable()) {
    			System.out.println("isWritable");
    			SocketChannel schan = (SocketChannel)selKey.channel();
    			writeOutput(schan);
    		}
    	}
    }

    private synchronized void readInput(SocketChannel schan) throws Exception {
    	System.out.println("enter readInput");
    	MessageParser mp = AndroidRemote.getInputData();
    	ByteBuffer readb = mp.getByteBuffer();

    	try {
    		int bytesRead = schan.read(readb);

    		if (bytesRead == -1) {
    			schan.close();
    			Remote.getInstance().setEditorConnected(false);
    			throw new EditorDisconnectedException();
    		} else {
				int pos = readb.position();
				System.out.println("pos = " + pos);
				readb.position(0);

				int intMsgType = mp.getMessageType();
				System.out.println("msg type : " + intMsgType);
				
				
    			switch (intMsgType) {
    				case AndroidRemote.MESSAGE_IMAGE:
    					if (!updateImage (mp, pos)) {
							readb.position(pos);
						}

    					break;
    				default:
    					break;
    			}
    		}
    	} catch (IOException e) {
    		throw e;
    	}
    }

    private synchronized void writeOutput(SocketChannel schan) throws Exception {
    	System.out.println("enter writeOutput");
    	MessageFormatter mf = AndroidRemote.getOutputData();
    	ByteBuffer writeb = mf.getByteBuffer();

    	if (sendOptions) {
    		queueOptionsMessage();
    		sendOptions = false;
    	}

    	if (writeb.limit() == 0) {
    		// Nothing to do
    		return;
    	}

		try {
			int bytesWritten = currentClient.write(writeb);

			if (bytesWritten < 0) {
				schan.close();
				Remote.getInstance().setEditorConnected(false);
				throw new EditorDisconnectedException();
			}
		} catch (IOException e) {
			throw e;
		}
    }

    // Reimplemented SensorEventListener.onAccuracyChanged
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	// Reimplemented SensorEventListener.onSensorChanged
	public void onSensorChanged(SensorEvent event) {
		int intSensorDataType = event.sensor.getType();
		System.out.println("got sensor data of type " + intSensorDataType);
		if (Remote.getInstance().isEditorConnected()) {
			if (intSensorDataType == Sensor.TYPE_ACCELEROMETER) {
				System.out.println("processing TYPE_ACCELEROMETER");
				final float mg = -SensorManager.STANDARD_GRAVITY;
				float values[] = event.values;
				accels = event.values;
				float v1 = values[0] / mg;
				float v2 = values[1] / mg;
				float v3 = values[2] / mg;
				queueAccelerometerEvent(v1, v2, v3, (int)event.timestamp);
			}
			if (intSensorDataType == Sensor.TYPE_MAGNETIC_FIELD ) {
				System.out.println("processing TYPE_MAGNETIC_FIELD");
				final float mg = SensorManager.MAGNETIC_FIELD_EARTH_MAX;
				float values[] = event.values;
				mags = event.values;
				float v1 = values[0] / mg;
				float v2 = values[1] / mg;
				float v3 = values[2] / mg;
				System.out.println("mag field(" + v1 + "," + v2 + ","  + v3 + ")" );
				queueMagnotometerEvent(v1, v2, v3, (int)event.timestamp);			
			}
            SensorManager.getRotationMatrix(RotationMat,InclinationMat, accels, mags);
            SensorManager.getOrientation(RotationMat, attitude);
            queueAttitudeEvent(attitude[0], attitude[1], attitude[2], (int)event.timestamp);
		}
	}

	// Event queues

	public void queueOptionsMessage() {
		Display display = ((WindowManager)Remote.getInstance().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		AndroidRemote.getOutputData().addOptionsMessage(width, height);
	}

	public void queueTouchEvent(float x, float y, long tstamp, int pointer, int action) {
		AndroidRemote.getOutputData().addTouchMessage(x, y, tstamp, pointer, action);
	}

	public void queueTrackBallEvent(float x, float y) {
		AndroidRemote.getOutputData().addTrackBallMessage(x, y);
	}

	public void queueKeyEvent(int key, int state, int unicode) {
		AndroidRemote.getOutputData().addKeyMessage(key, state, unicode);
	}

	private void queueAccelerometerEvent(float x, float y, float z, int timestamp) {
		AndroidRemote.getOutputData().addAccelerometerMessage(x, y, z, timestamp);
	}

	private void queueMagnotometerEvent(float x, float y, float z, int timestamp) {
		AndroidRemote.getOutputData().addMagnotometerMessage(x, y, z, timestamp);
	}
	
	private void queueAttitudeEvent(float x, float y, float z, int timestamp) {
		AndroidRemote.getOutputData().addAttitudeMessage(x, y, z, timestamp);
	}

	
	private boolean updateImage (MessageParser mp, int dataSize) {
		Bitmap bmp = mp.getBitmap (dataSize);

		if (bmp != null) {
			MainView mv = MainView.getInstance();
			mv.setFrameImage(bmp);
			return true;
		}

		return false;
	}
}
