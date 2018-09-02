package org.okaria.chunk;

import java.util.List;
import java.util.Queue;

public interface Chunk {
	
	int getChunkLength();
	int getChunkCount();
	
	List<Integer>  getCompleteChunks();
	List<Integer>  getDownloadingChunks();
	Queue<Integer> getRemainingChunks();
	
	int peekChunk();
	int	getNextChunk();
	
	long[] rangeOf(int index);
	long[] rangeOfNextChunk();
	long   startOfChunk(int index);
	long   limitOfChunk(int index);
	long   startOfNextChunk();
	int    indexOfPosition(long position);

	void markDownloading(int index);
	void markComplete(int index);
	
	long getFileLength();
	long getCompleteLength();
	long getRemainingLength();
	void updateFileLength(long length);
	
	
	String getFileLengthMB();
	String getCompleteLengthMB();
	String getRemainingLengthMB();
	
	
	boolean isFinish();
	boolean isStreaming();
	long getStreamStart();
	void updateStreamStart(long position);
	

}
