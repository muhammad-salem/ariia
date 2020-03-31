package org.aria.chunk;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChunkInfo implements ChunkUtil, StreamingPolicy {

	protected int  chunkLength;
	protected long fileLength;
	protected long streamStart = -2;
	
	protected LinkedList<Integer> complete;
	protected LinkedList<Integer> download;
	protected ConcurrentLinkedQueue<Integer> remaining;
	
	public ChunkInfo() {  fileLength = -1; streamStart = -2; chunkLength = 512*1024;}
	public ChunkInfo(long fileLength) { this(fileLength, 512*1024);}
	public ChunkInfo(long fileLength, int chunkLength) 
		{ this.fileLength = fileLength; initCollection(); buildRemaining();}
	
	void initCollection() {
		complete = new LinkedList<>();
		download = new LinkedList<>();
		remaining = new ConcurrentLinkedQueue<>();
	}
	
	@Override
	public int getChunkLength() { return chunkLength; }
	@Override
	public List<Integer> getCompleteChunks() { return complete; }
	@Override
	public List<Integer> getDownloadingChunks() { return download; }
	@Override
	public Queue<Integer> getRemainingChunks() { return remaining; }

	@Override
	public long getFileLength() { return fileLength; }

	@Override
	public void updateFileLength(long length) { 
		if(isStreaming()) {
			this.fileLength = length;
			initCollection();
			convertStreamToKnowingFileLength(streamStart);
		}
		else {
			this.fileLength = length; 
		}
	}
	@Override
	public boolean isStreaming() { return fileLength == -1 & streamStart > -1; }
	@Override
	public long getStreamStart() { return streamStart; }
	@Override
	public void updateStreamStart(long position) { this.streamStart = position; }

	
	@Override
	public String toString() {
		return super.toString();
	}
	
}
