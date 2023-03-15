package org.ariia.config;

import lombok.Getter;
import lombok.Setter;
import org.ariia.args.Argument;
import org.ariia.util.R;

import java.util.Objects;

@Setter
@Getter
public class Properties {

    private int retries = 0;
    private String defaultSaveDirectory = R.CurrentDirectory();
    private int maxActiveDownloadPool = 4;
    private int rangePoolNum = 8;
    private int maxBufferPool = 64;        //	8 * 4 * 2

    public Properties() {
    }

    public Properties(Argument arguments) {
        updateArguments(arguments);
    }

    public void updateProperties(Properties properties) {
        properties = Objects.requireNonNull(properties, "properties is null");
        R.mkdir(properties.defaultSaveDirectory);
        this.defaultSaveDirectory = Objects.requireNonNull(properties.defaultSaveDirectory, "defaultSaveDirectory is null");
        this.maxActiveDownloadPool = Objects.requireNonNull(properties.maxActiveDownloadPool, "maxActiveDownloadPool is null");
        this.maxBufferPool = Objects.requireNonNull(properties.maxBufferPool, "maxBufferPool is null");
        this.rangePoolNum = Objects.requireNonNull(properties.rangePoolNum, "rangePoolNum is null");
        this.retries = Objects.requireNonNull(properties.retries, "retries is null");
    }

    public void updateArguments(Argument arguments) {
        if (arguments.isTries()) {
            retries = arguments.getTries();
        }
        if (arguments.isConnection()) {
            rangePoolNum = arguments.getNumberOfConnection();
        }
        if (arguments.isMaxItem()) {
            maxActiveDownloadPool = arguments.getMaxItem();
        }
        maxBufferPool = maxActiveDownloadPool * rangePoolNum * 2;
        if (arguments.isSavePath()) {
            defaultSaveDirectory = arguments.getSavePath();
        }
        R.mkdir(defaultSaveDirectory);
    }

}
