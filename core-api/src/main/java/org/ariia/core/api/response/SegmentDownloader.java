package org.ariia.core.api.response;

import org.ariia.core.api.request.ClientRequest;
import org.ariia.core.api.request.Response;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.core.api.writer.SegmentWriter;
import org.ariia.items.Item;
import org.ariia.logging.Logger;
import org.network.speed.report.SpeedMonitor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;


public class SegmentDownloader implements Downloader, ContentLength {

    private static Logger log = Logger.create(SegmentDownloader.class);


    ClientRequest clientRequest;
    SegmentWriter segmentWriter;

    public SegmentDownloader(ClientRequest clientRequest, SegmentWriter segmentWriter) {
        super();
        this.clientRequest = clientRequest;
        this.segmentWriter = segmentWriter;
    }

    @Override
    public boolean downloadTask(ItemMetaData metaData, int index, SpeedMonitor... monitors) {
        var item = metaData.getItem();
        if (item.getRangeInfo().isFinish(index)) return true;

        try (var response = clientRequest.get(item, index)) {

            if (metaData.getRangeInfo().isStreaming()) {
                Optional<String> contentLength = response.firstValue("Content-Length");
                if (contentLength.isPresent()) {
                    updateLength(metaData.getRangeInfo(), contentLength.get());
                }

            }
            if (response.code() / 100 != 2) {
                log.warn(item.getFilename(),
                        "response.code = " + response.code() + ' ' + response.responseMessage()
                                + "\nurl = " + response.requestUrl()
                                + "\nindex = " + index + "\t" + Arrays.toString(item.getRangeInfo().indexOf(index)));
                return false;
            }
//			else if(response.code() == 416) //416 Range Not Satisfiable
//			{
//				return true;
//			}

            segmentWriter.writeResponse(
                    response.bodyBytes(),
                    metaData,
                    index,
                    item.getRangeInfo().startOfIndex(index),
                    item.getRangeInfo().limitOfIndex(index),
                    monitors);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
