package org.ariia.cli;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LunchMode {
    DISABLE_DOWNLOAD_DISABLE_PAUSE(Boolean.FALSE, Boolean.FALSE),
    DISABLE_DOWNLOAD_ALLOW_PAUSE(Boolean.FALSE, Boolean.TRUE),
    ALLOW_DOWNLOAD_DISABLE_PAUSE(Boolean.TRUE, Boolean.FALSE),
    ALLOW_DOWNLOAD_ALLOW_PAUSE(Boolean.TRUE, Boolean.TRUE);

    private boolean allowDownload;
    private boolean allowPause;

}
