package org.ariia.core.api.request;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface StreamingClientRequest extends ClientRequest {
	
	@Override
    default Response get(String url, Long startRange, Long endRange, Map<String, List<String>> headers) throws IOException {

		Map<String, List<String>> headersCopy = new HashMap<>(headers);
		if (Objects.nonNull(startRange) || startRange > -1) {
			headersCopy.put("Range", Arrays.asList("bytes=" + startRange + "-"));
		}
		return executeRequest("GET", url, headers);
	}
}
