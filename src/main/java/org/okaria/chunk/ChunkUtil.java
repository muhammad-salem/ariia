package org.okaria.chunk;

import org.okaria.Utils;

public interface ChunkUtil extends Chunk {
	
	@Override
	default int getChunkCount() {
		int count = (int) (getFileLength() / getChunkLength());
		count += getFileLength() % getChunkLength() > 0 ? 1 : 0;
		return count ;
	}
	
	@Override
	default int peekChunk() {
		return getRemainingChunks().peek();
	}
	@Override
	default int getNextChunk() {
		int index = peekChunk();
		markDownloading(index);
		return index;
	}
	@Override
	default long[] rangeOf(int index) {
		long[] ls = new long[] {index * getChunkLength(), (index+1) * getChunkLength()};
		if(ls[0]<0 || ls[0] > getFileLength()) throw new Error("index is not inrange of this file length");
		if(ls[1] > getFileLength() ) ls[1] = getFileLength()-1;
		return ls;
	}
	@Override
	default long[] rangeOfNextChunk() {
		int next = getNextChunk();
		return rangeOf(next);
	}
	
	@Override
	default long startOfChunk(int index) {
		return index * getChunkLength();
	}
	
	@Override
	default long limitOfChunk(int index) {
		return (index+1) * getChunkLength();
	}
	
	@Override
	default long startOfNextChunk() {
		int next = getNextChunk();
		return startOfChunk(next);
	}
	
	@Override
	default int indexOfPosition(long position) {
		return (int) (position/getChunkLength());
	}
	
	@Override
	default void markDownloading(int index) {
		getDownloadingChunks().add(index);
		getRemainingChunks().remove((Object)index);
		getCompleteChunks().remove((Object)index);
	}
	
	@Override
	default void markComplete(int index) {
		getCompleteChunks().add(index);
		getDownloadingChunks().remove((Object)index);
		getRemainingChunks().remove((Object)index);
	}
	
	@Override
	default boolean isFinish() {
		return getRemainingChunks().isEmpty();
	}
	
	@Override
	default long getCompleteLength() {
		return getCompleteChunks().size() * getChunkLength();
	}
	
	@Override
	default long getRemainingLength() {
		return getRemainingChunks().size() * getChunkLength();
	}
	
	@Override
	default String getFileLengthMB() {
		return Utils.fileLengthUnite(getFileLength());
	}
	@Override
	default String getCompleteLengthMB() {
		return Utils.fileLengthUnite(getCompleteLength());
	}
	@Override
	default String getRemainingLengthMB() {
		return Utils.fileLengthUnite(getRemainingLength());
	}
}
