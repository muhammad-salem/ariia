package org.ariia.filecheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.LinkedHashMap;

public class FileChecker implements FileCheck {
	
	public LinkedHashMap<Long, Long> empityRanges(File file, long filelength, long skip) {
		
		
		try (	RandomAccessFile raf =  new RandomAccessFile(file, "r");
				FileChannel channel = raf.getChannel()
			){
			MappedByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, raf.length());
			
			for (int i = 0; i < raf.length(); i++) {
				byte b = buffer.get();
				
				char c = (char) b;
				if (c == '\0') {
					System.out.print(Byte.toString(b));
					System.out.print(" ");
					System.out.print(c);
					System.out.println("\nfile empyte at pos: " + i);
				}
				
			}
			
			raf.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		return null;
	}
	

	@Override
	public LinkedHashMap<Long, Long> getEmpityRange(File file, long filelength, long skip) {
		LinkedHashMap<Long, Long> map = new LinkedHashMap<Long, Long>();
		long start = 0, end = 0;
		FileReader reader = null;
		try {
			reader = new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (long i = 0; i < filelength; i += skip) {
			System.out.print(i + " ");
			try {

				// start
				char c = (char) reader.read();
				if (c == '\00') {
					start = i;

					// end
					for (; i < filelength; i += skip) {
						c = (char) reader.read();
						if (c == '\00') {
							end = i;
						} else {
							map.put(start, end);
							System.out.println("s:" + start + " e:" + end);
							break;
						}
					}
				}

			} catch (Exception e) {
				// e.printStackTrace();
				// System.out.println();
			} finally {
				try {
					reader.close();
				} catch (Exception e2) {
				}
			}

		}

		return map;
	}

	public LinkedHashMap<Long, Long> getEmpityRange(String file, long[] rng, long skip) {
		return getEmpityRange(new File(file), rng[0], rng[1], skip);
	}

	public LinkedHashMap<Long, Long> getEmpityRange(File file, long[] rng, long skip) {
		return getEmpityRange(file, rng[0], rng[1], skip);
	}

	public LinkedHashMap<Long, Long> getEmpityRange(String file, long startBit, long endBit, long skip) {
		return getEmpityRange(new File(file), startBit, endBit, skip);
	}

	public LinkedHashMap<Long, Long> getEmpityRange(File file, long startBit, long endBit, long skip) {
		LinkedHashMap<Long, Long> map = new LinkedHashMap<Long, Long>();
		long start = 0, end = 0;
		FileReader reader = null;
		try {
			reader = new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (long i = startBit; i < endBit; i += skip) {
			System.out.println(i + " ");
			try {

				// start
				char c = (char) reader.read();
				if (c == '\00') {
					start = i;

					// end
					for (; i < endBit; i += skip) {
						c = (char) reader.read();
						if (c == '\00') {
							end = i;
						} else {
							map.put(start, end);
							System.out.println("s:" + start + " e:" + end);
							break;
						}
					}
				}

			} catch (Exception e) {
				// e.printStackTrace();
				// System.out.println();
			} finally {
				try {
					reader.close();
				} catch (Exception e2) {
				}
			}

		}

		return map;
	}
}
