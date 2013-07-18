package com.BrascomeTechnologies.Viewer;

import java.nio.ByteBuffer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;

// Class for Editor message parsing

class MessageParser {

	private ByteBuffer bytes;
	private Bitmap backupBitmap;
	private static byte[] bitmapBytes = new byte[1024 * 1024 * 4];
	private boolean imageComplete;

	public MessageParser(int size) {
		bytes = ByteBuffer.allocateDirect(size);
		bytes.limit(size);
		bytes.position(0);
		backupBitmap = Bitmap.createBitmap(100, 100, Config.ARGB_8888);
		backupBitmap.eraseColor(0xffffffff);
		imageComplete = true;
	}

	public ByteBuffer getByteBuffer() {
		return bytes;
	}

	public boolean isImageComplete() {
		return imageComplete;
	}

	public Bitmap getBitmap (int dataSize) {
		try {
			System.out.println("entered getBitmap()");
			int intImageWidth = bytes.getInt();
			int intImageHeight = bytes.getInt();
			int compressedSize = bytes.getInt();
			
			System.out.println("bitmap dimensions (" + intImageWidth + "x" + intImageHeight +")");
			System.out.println("dataSize size = " + dataSize);
			System.out.println("compressed size = " + compressedSize);

			if (dataSize < compressedSize + 16)
			{
				System.out.println("bad image size. data size (" + dataSize + ") should be less than compressedSize (" + compressedSize + ") + 16");
				imageComplete = false;
				return null;
			}

			bytes.get(bitmapBytes, 0, compressedSize);

			Bitmap b = BitmapFactory.decodeByteArray(bitmapBytes, 0, compressedSize);

			if (b == null) {
				System.out.println("bad format");
				imageComplete = false;
				return null;
			}

			int newPos = dataSize - bytes.position ();
			bytes.compact ();
			bytes.position (newPos);
			imageComplete = true;

			return b;
		} catch (Exception e) {
			imageComplete = false;
			System.out.println("getBitmap exception : " + e.toString());
		}

		return null;
	}

	public synchronized int getMessageType() {
		int mt = bytes.getInt();

		switch (mt) {
			case AndroidRemote.MESSAGE_IMAGE:
				return mt;
			default:
				break;
		}

		return 0;
	}
}
