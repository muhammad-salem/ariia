package org.okaria.range;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel.MapMode;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface ChunkedFile {

//	void setLength(long length);
//	long getLength();
//	
//	void setFilePath(String filePath);
//	String getFilePathe();
//	
//	
//	void initFile();
	
	default boolean initFile(String pathname,long fileLength, long chunkLength) {
		File file = new File(pathname);
		file.mkdirs();	
		return initExistsFile(file, fileLength, chunkLength);
	}
	default boolean initExistsFile(File file, long fileLength, long chunkLength) {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "rw");
			raf.setLength(fileLength);
			initChunkedFile(raf, chunkLength);
			raf.close();
		} catch (IOException e) {
			return false;
		}finally {
			if(raf != null) {
				try { raf.close(); } catch (Exception ignore) { /*ignore*/ }
			}
		}
		
		return false;
	}
	
	default boolean initChunkedFile(RandomAccessFile raf, long chunkLength) throws IOException {
		return initChunkedFile(raf, 0, raf.length(), chunkLength);
	}
/*	
		MappedByteBuffer map = raf.getChannel().map(MapMode.READ_WRITE, 0, raf.length());
		long length = raf.length();
		byte b = 0;
		
		if(Long.MAX_VALUE - length > Integer.MAX_VALUE - length ) {
			
		}
		else if(length > Integer.MAX_VALUE) {
			
		}

		for (int index = 0; index < length; index += chunkLength) {
			map.put(index, b);
		}
		
*/		
	
	
	
	default boolean initChunkedFile(RandomAccessFile raf,final long start, final long end, long chunkLength) {
		long length = end - start;
		boolean result = true;
		float times = length / Integer.MAX_VALUE;
		times = (long)( ((long) times) + ( ( times - (long)times  > 0 ) ? 1 : 0) ) ; 
		long start_t = start;
		MappedByteBuffer map;
		for (long i = 0; i < times ; i++) {
			
			try {
				if(i< times-1) {
					map = raf.getChannel().map(MapMode.READ_WRITE, start_t, Integer.MAX_VALUE-1);
				}else {
					map = raf.getChannel().map(MapMode.READ_WRITE, start_t, length);						
				}
				result = initChunkedFile(map, chunkLength);
				if(! result) {
					return false;
				}
				start_t += Integer.MAX_VALUE;
				length -= Integer.MAX_VALUE;
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}
/*		
		//if(Long.MAX_VALUE - length > Integer.MAX_VALUE - length ) {
		if(length   - Integer.MAX_VALUE > 0 ) {
			
			
		}
		else {
			MappedByteBuffer map;
			try {
				map = raf.getChannel().map(MapMode.READ_WRITE, start, length);
			} catch (IOException e) {
				return false;
			}
			return initChunkedFile(map, chunkLength);
		}
			
*/		
		

	default boolean initChunkedFile(MappedByteBuffer map, long chunkLength) {
		long length = map.limit();
		//if(Long.MAX_VALUE - length > Integer.MAX_VALUE - length ) return false;
		byte ZERO_BYTE = 0;
		try {
			for (int index = 0; index < length; index += chunkLength) {
				map.put(index, ZERO_BYTE);
			}
		} catch (IndexOutOfBoundsException | ReadOnlyBufferException e) {
			 return false;
		}
		return true;
	}
	
	
	
	default Map<Long, Long> listToMap(List<Long> list, long chunkLength){
		Map<Long, Long> map = new LinkedHashMap<>();
//		long start = list.remove(0);
//		long end = list.remove(0);
//		// bad case while { [(Long.MAX_VALUE/Integer.MAX_VALUE)-Integer.MAX_VALUE;] / Integer.MAX_VALUE}
//		int size = list.size();
//		
//		for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
//			Long long1 = (Long) iterator.next();
//			iterator.remove();
//			//fggdfg
//		}
		
		return map;
	}
	
	
	
	default List<Long>  getEmptyChunks(String pathname, long chunkLength) {
		File file = new File(pathname);
		if(! file.exists()) return Collections.emptyList();
		return getEmptyChunks(file, chunkLength);
	}
	default List<Long>  getEmptyChunks(File file , long chunkLength) {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "r");
			return emptyList(raf, chunkLength);
		} catch (IOException e) {
			/* ignore to just get what you already have */
		}finally {
			if(raf != null) {
				try { raf.close(); } catch (Exception ignore) { /*ignore*/ }
			}
		}
		return Collections.emptyList();
	}
	
	
	
	default List<Long>  emptyList(RandomAccessFile raf, long chunkLength) throws IOException {
		return emptyList(raf, 0, raf.length(), chunkLength);
	}
	
	default List<Long>  emptyList(RandomAccessFile raf,final long start, final long end, long chunkLength) {
		long length = end - start;
		float times = length / Integer.MAX_VALUE;
		times = (long)( ((long) times) + ( ( times - (long)times  > 0 ) ? 1 : 0) ) ; 
		long start_t = start;
		MappedByteBuffer map;
		List<Long> chunks = new LinkedList<>();
		List<Long> temp = new LinkedList<>();
		for (long i = 0; i < times ; i++) {
			
			try {
				if(i< times-1) {
					map = raf.getChannel().map(MapMode.READ_WRITE, start_t, Integer.MAX_VALUE-1);
				}else {
					map = raf.getChannel().map(MapMode.READ_WRITE, start_t, length);						
				}
				temp = emptyChunkFaster(map, start_t, chunkLength);
				chunks.addAll(temp);
				temp.clear();
				start_t += Integer.MAX_VALUE;
				length -= Integer.MAX_VALUE;
			} catch (IOException e) {
				
			}
		}
		return chunks;
	}
	
	default List<Long> emptyChunkFaster(MappedByteBuffer map,final long skipedLength, long chunkLength) {
		List<Long> chunks = emptyChunk(map, chunkLength);
		chunks.forEach(ch ->{ch += skipedLength;});
		return chunks;
	}
	
	default List<Long> emptyChunk(MappedByteBuffer map, long chunkLength) {
		long length = map.limit();
		List<Long> chunks = new LinkedList<>();
		
		byte ZERO_BYTE = 0;
		byte read_byte = -1;
		try {
			for (int index = 0; index < length; index += chunkLength) {
				//map.put(index, ZERO_BYTE);
				read_byte = map.get(index);
				if(read_byte == ZERO_BYTE) {
					chunks.add((long)index );
				}
			}
		} catch (IndexOutOfBoundsException | ReadOnlyBufferException e) {
			 //return chunks;
		}
		return chunks;
	}
	
	
	
	default List<Long> emptyChunk(MappedByteBuffer map,final long skipedLength, long chunkLength) {
		long length = map.limit();
		List<Long> chunks = new LinkedList<>();
		
		byte ZERO_BYTE = 0;
		byte read_byte = -1;
		try {
			for (int index = 0; index < length; index += chunkLength) {
				//map.put(index, ZERO_BYTE);
				read_byte = map.get(index);
				if(read_byte == ZERO_BYTE) {
					chunks.add(skipedLength + index );
				}
			}
		} catch (IndexOutOfBoundsException | ReadOnlyBufferException e) {
			 //return chunks;
		}
		return chunks;
	}
	
	
	
	
	
}
