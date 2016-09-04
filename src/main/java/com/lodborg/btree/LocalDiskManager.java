package com.lodborg.btree;

import java.io.*;

public class LocalDiskManager<T extends Serializable> implements SegmentManager<T> {
	private File dir;

	public LocalDiskManager(String path){
		this.dir = new File(path);
		if (!dir.exists() || !dir.isDirectory())
			throw new IllegalArgumentException("The specified path does not exist or is not a directory.");
	}

	@Override
	public Segment<T> fetch(long index) {
		File file = getSegmentFileFromIndex(index);
		if (!file.exists())
			return null;
		ObjectInputStream stream = null;
		try {
			stream = new ObjectInputStream(new FileInputStream(file));
			return (Segment<T>) stream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			return null;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean persist(Segment<T> segment) {
		File file = getSegmentFileFromIndex(segment.id);
		try {
			ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
			stream.writeObject(segment);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	@Override
	public void remove(long index) {
		File file = getSegmentFileFromIndex(index);
		if (file.exists())
			file.delete();
	}

	private File getSegmentFileFromIndex(long index){
		return new File(dir.getAbsolutePath() + File.separator + "segment_"+index+".srl");
	}
}
