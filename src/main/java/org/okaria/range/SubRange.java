package org.okaria.range;

public class SubRange {

	/**
	 * 2 MegaBytes
	 */
	private static final int LengthFactor = 2 * 1024 * 1024;

	public long getContentLengthFromContentRange(String range) {
		int x = range.indexOf("/");
		String length = range.substring(x + 1, range.length());
		long l = Long.valueOf(length);
		return l;
	}

	public static long[][] initRangeStreamer(int parts, long length, boolean isUnknowLength, long downloaded) {

		if (isUnknowLength || length < LengthFactor) {
			long[][] ranges = new long[1][2];
			if (isUnknowLength) {
				length = 0;
			}
			ranges[0][0] = downloaded;
			ranges[0][1] = length;

			return ranges;
		} else {

			long[][] ranges = new long[parts][2];

			ranges[0][0] = 0;
			ranges[0][1] = LengthFactor;

			ranges[parts - 1][0] = length - LengthFactor;
			ranges[parts - 1][1] = length - 1;

			length = length - (2 * LengthFactor);
			downloaded = LengthFactor;
			long sub = length / parts;

			for (int i = 1; i < parts - 1; i++) {
				// ranges[i][0] = sub * i;
				// ranges[i][1] = sub * (i + 1) - 1;
				// ranges[i][2] = 0;

				ranges[i][0] = downloaded + (sub * i);
				ranges[i][1] = downloaded + (sub * (i + 1)) - 1;
			}
			ranges[parts - 2][1] = ranges[parts - 1][0] - 1;
			return ranges;
		}
	}

	public static long[][] stream(long length, int parts) {
		return stream(0, length, parts);
	}

	public static long[][] stream(long startByte, long endByte, int parts) {
		if(parts < 3) return subrange(startByte, endByte, parts);
		long[][] ranges = new long[parts][2];
		ranges[0][0] = 0;
		ranges[0][1] = LengthFactor;

		ranges[parts - 1][0] = endByte - LengthFactor;
		ranges[parts - 1][1] = endByte - 1;

		long divBytes = endByte - (2 * LengthFactor);

		long sub = divBytes / parts;

		for (int i = 1; i < parts - 1; i++) {
			ranges[i][0] = ranges[0][1] + (sub * (i - 1));
			ranges[i][1] = ranges[0][1] + (sub * (i)) - 1;
		}
		ranges[parts - 2][1] = ranges[parts - 1][0] - 1;
		return ranges;

	}

	public static long[][] initRangeNormal(int splitCount, int length, int downloaded, boolean isUnknowLength) {
		long[][] ranges;
		if (isUnknowLength || length > LengthFactor) {
			if (isUnknowLength) {
				length = 0;
			}
			ranges = new long[1][2];
			ranges[0][0] = downloaded;
			ranges[0][1] = length;

		} else {
			ranges = new long[splitCount][2];
			long sub = length / splitCount;
			for (int i = 0; i < splitCount; i++) {
				ranges[i][0] = sub * i;
				ranges[i][1] = sub * (i + 1) - 1;
			}
			ranges[splitCount - 1][1] = length - 1;
		}
		return ranges;

	}

	public static long[][] subrange(long length, int splitCount, int from, int to) {
		long[][] ranges = new long[splitCount][2];
		long sub = length / splitCount;
		for (int i = from; i <= to; i++) {
			ranges[i][0] = sub * i;
			ranges[i][1] = sub * (i + 1) - 1;
		}
		ranges[splitCount - 1][1] = length - 1;
		return ranges;
	}

	/**
	 * get sub range from {from} to the end.
	 * 
	 * @param length
	 * @param splitCount
	 * @param from
	 * @return
	 */
	public static long[][] subrange(long length, int splitCount, int srart) {
		long[][] ranges = new long[splitCount][2];
		long sub = length / splitCount;
		for (int i = srart; i < splitCount; i++) {
			ranges[i][0] = sub * i;
			ranges[i][1] = sub * (i + 1) - 1;
		}
		ranges[splitCount - 1][1] = length - 1;
		return ranges;
	}

	public static long[][] subrange(long length, long downloaded, int splitCount, int srart) {
		long[][] ranges = new long[splitCount][2];
		long sub = (length - downloaded) / splitCount;
		for (int i = srart; i < splitCount; i++) {
			ranges[i][0] = downloaded + (sub * i);
			ranges[i][1] = downloaded + (sub * (i + 1)) - 1;
		}
		ranges[splitCount - 1][1] = length - 1;
		return ranges;
	}

	public static long[][] subrangeEqual(long length, long downloaded, long splitLength, int srartNumber) {

		int numOfParts = (int) ((length - downloaded) / splitLength);

		long[][] ranges = new long[numOfParts][2];
		long sub = (length - downloaded) / numOfParts;
		for (int i = srartNumber; i < numOfParts; i++) {
			ranges[i][0] = downloaded + 1 + (sub * i);
			ranges[i][1] = sub * (i + 1) - 1;
		}
		ranges[numOfParts - 1][1] = length - 1;
		return ranges;
	}

	public static boolean isUnknowingLength(long[][] ranges) {
		return ranges[0][1] == 0;
	}

	public static long[] subrange(long downloded) {
		return subrange(downloded, 0l);
	}

	public static long[] subrange(long start, long end) {
		long[] range = new long[2];
		range[0] = start;
		range[1] = end;
		return range;
	}

	public static long[][] subrange(long[] ls, int numOfParts) {
		return subrange(ls[0], ls[1], numOfParts);
	}
	public static long[][] subrange(long startByte, long endByte, int numOfParts) {
		long[][] ranges = new long[numOfParts][2];
		long sub = (endByte - startByte) / numOfParts;
		for (int i = 0; i < numOfParts; i++) {
			ranges[i][0] = startByte + (sub * i);
			ranges[i][1] = startByte + (sub * (i + 1)) - 1;
		}
		ranges[numOfParts - 1][1] = endByte;
		return ranges;
	}

	
	public static long[][] mksubrange(long length) {
//		if(length == -1) return new long[][] {{0, Long.MAX_VALUE}};
		return new long[][] {{0, length}};
	}

	public static long[][] subrange(long length, int numOfParts) {
		return subrange(0, length, numOfParts);
	}

}
