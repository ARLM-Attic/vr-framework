package com.BrascomeTechnologies.Viewer;

import java.nio.ByteBuffer;

public class MessageFormatter {

	private ByteBuffer bytes;

	public MessageFormatter(int size) {
		bytes = ByteBuffer.allocateDirect(size);
		bytes.clear();
	}

	public synchronized byte[] getBytes() {
		return bytes.array();
	}

	public static ByteBuffer clone (ByteBuffer original)
	{
		ByteBuffer clone = ByteBuffer.allocate (original.capacity ());
		original.rewind ();  // copy from the beginning
		clone.put (original);
		original.rewind ();
		clone.flip ();
		return clone;
	}

	// Returns a copy of bytes and resets buffer to the beginning
	public synchronized ByteBuffer getByteBuffer() {
		ByteBuffer bb = bytes.duplicate();
		bb.flip();
		bytes.clear();
		return clone (bb);
	}

	private void ensureBufferSpace(int byteCount) {
		int bytesRemaining = bytes.limit() - bytes.position();

		if (bytesRemaining < byteCount) {
			getByteBuffer();
		}
	}

	public synchronized void addTouchMessage(float x, float y, long timestamp, int pointer, int action) {
		ensureBufferSpace(28);

		bytes.putInt(AndroidRemote.MESSAGE_TOUCH_INPUT);
		bytes.putFloat(x);
		bytes.putFloat(y);
		bytes.putLong(timestamp);
		bytes.putInt(pointer);
		bytes.putInt(action);
	}

	public synchronized void addAccelerometerMessage(float x, float y, float z, int timestamp) {
		ensureBufferSpace(20);
		
		bytes.putInt(AndroidRemote.MESSAGE_ACCELEROMETER_INPUT);
		bytes.putFloat(x);
		bytes.putFloat(y);
		bytes.putFloat(z);
		bytes.putInt(timestamp);
	}
	
	public synchronized void addMagnotometerMessage(float x, float y, float z, int timestamp) {
		ensureBufferSpace(20);
		
		bytes.putInt(AndroidRemote.MESSAGE_MAGNOTOMETER_INPUT);
		bytes.putFloat(x);
		bytes.putFloat(y);
		bytes.putFloat(z);
		bytes.putInt(timestamp);
	}

	public synchronized void addAttitudeMessage(float x, float y, float z, int timestamp) {
		ensureBufferSpace(20);
		
		bytes.putInt(AndroidRemote.MESSAGE_ATTITUDE_INPUT);
		bytes.putFloat(x);
		bytes.putFloat(y);
		bytes.putFloat(z);
		bytes.putInt(timestamp);
	}


	public synchronized void addTrackBallMessage(float x, float y) {
		ensureBufferSpace(12);

		bytes.putInt(AndroidRemote.MESSAGE_TRACKBALL_INPUT);
		bytes.putFloat(x);
		bytes.putFloat(y);
	}

	public synchronized void addOptionsMessage(int screenWidth, int screenHeight) {
		ensureBufferSpace(12);

		bytes.putInt(AndroidRemote.MESSAGE_OPTIONS);
		bytes.putInt(screenWidth);
		bytes.putInt(screenHeight);
	}

	public synchronized void addKeyMessage(int key, int state, int unicode) {
		ensureBufferSpace(16);

		bytes.putInt(AndroidRemote.MESSAGE_KEY);
		bytes.putInt(state);
		bytes.putInt(key);
		bytes.putInt(unicode);
	}
}
