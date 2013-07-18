package com.BrascomeTechnologies.Viewer;

// Class for static global data

public class AndroidRemote {

	public static final int MESSAGE_IMAGE = 5;
	public static final int MESSAGE_TOUCH_INPUT = 1;
	public static final int MESSAGE_ACCELEROMETER_INPUT = 2;
	public static final int MESSAGE_TRACKBALL_INPUT = 3;
	public static final int MESSAGE_OPTIONS = 6;
	public static final int MESSAGE_KEY = 7;
	public static final int MESSAGE_MAGNOTOMETER_INPUT = 8;
	public static final int MESSAGE_ATTITUDE_INPUT = 9;

	public static final int PORT = 50005;

	// Use 64k buffer for output (touch/trackball events, etc.)
	// and 768k buffer for input (images, status messages).
	private static final int OUTPUT_DATA_SIZE = 1024 * 64;
	private static final int INPUT_DATA_SIZE = 1024 * 768;

	private static MessageFormatter outputData;
	private static MessageParser inputData;

	static {
		outputData = new MessageFormatter(OUTPUT_DATA_SIZE);
		inputData = new MessageParser(INPUT_DATA_SIZE);
	}

	public static MessageFormatter getOutputData() {
		return outputData;
	}

	public static MessageParser getInputData() {
		return inputData;
	}
}
