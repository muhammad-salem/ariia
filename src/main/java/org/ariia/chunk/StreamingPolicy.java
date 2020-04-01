package org.ariia.chunk;

public interface StreamingPolicy extends Chunk {
	
	default void buildRemaining() {
		
		
	}
	default void convertStreamToKnowingFileLength(long complete) {
		
	}

}
