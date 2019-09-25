package org.okaria.filecheck;

import java.io.File;
import java.util.LinkedHashMap;

public interface FileCheck {

	default long[][] getEmpitySpaces(String pathname) {
		File file = new File(pathname);
		return getEmpitySpaces(file, file.length());
	}

	default long[][] getEmpitySpaces(File file) {
		return getEmpitySpaces(file, file.length());
	}

	default long[][] getEmpitySpaces(String pathname, long filelength) {
		return getEmpitySpaces(new File(pathname), filelength);
	}

	default long[][] getEmpitySpaces(File pathname, long filelength) {
		return getEmpitySpaces(pathname, filelength, 1);
	}

	default long[][] getEmpitySpacesSkip(String pathname, long skip) {
		File file = new File(pathname);
		return getEmpitySpaces(file, file.length(), skip);
	}

	default long[][] getEmpitySpacesSkip(File file, long skip) {
		return getEmpitySpaces(file, file.length(), skip);
	}

	default long[][] getEmpitySpaces(String pathname, long filelength, long skip) {
		return getEmpitySpaces(new File(pathname), filelength, skip);
	}

	default long[][] getEmpitySpaces(File pathname, long filelength, long skip) {
		LinkedHashMap<Long, Long> map = getEmpityRange(pathname, filelength, skip);
		long[][] arrays = new long[map.size()][2];
		int index = 0;
		for (Long ls : map.keySet()) {
			arrays[index][0] = ls;
			arrays[index++][1] = map.get(ls);
		}
		return arrays;
	}

	default LinkedHashMap<Long, Long> getEmpityRange(String pathname) {
		File file = new File(pathname);
		return getEmpityRange(file, file.length());
	}

	default LinkedHashMap<Long, Long> getEmpityRange(File file) {
		return getEmpityRange(file, file.length());
	}

	default LinkedHashMap<Long, Long> getEmpityRange(String pathname, long filelength) {
		return getEmpityRange(new File(pathname), filelength);
	}

	default LinkedHashMap<Long, Long> getEmpityRange(File pathname, long filelength) {
		return getEmpityRange(pathname, filelength, 1);
	}

	default LinkedHashMap<Long, Long> getEmpityRangeSkip(String pathname, long skip) {
		File file = new File(pathname);
		return getEmpityRange(file, file.length(), skip);
	}

	default LinkedHashMap<Long, Long> getEmpityRangeSkip(File file, long skip) {
		return getEmpityRange(file, file.length(), skip);
	}

	default LinkedHashMap<Long, Long> getEmpityRange(String pathname, long filelength, long skip) {
		return getEmpityRange(new File(pathname), filelength, skip);
	}

	LinkedHashMap<Long, Long> getEmpityRange(File pathname, long filelength, long skip);

}
