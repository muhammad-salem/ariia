package org.ariia.filecheck;

import org.ariia.core.api.service.DownloadService;
import org.ariia.items.Item;
import org.ariia.logging.Log;
import org.ariia.range.RangeInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CheckManager {


    public static void CheckItem(String url, int chunkSize, DownloadService downloadService) {

        Item item = downloadService.itemStream()
                .filter(metaData -> metaData.getItem().getUrl().equals(url))
                .findFirst()
                .get().getItem();
        Log.trace(CheckManager.class, "found file", item.liteString());
        long[][] arr;
        try {
            RangeChecker checker;
            if (chunkSize < 1) checker = new RangeChecker(item.path());
            else checker = new RangeChecker(item.path(), chunkSize);
            arr = checker.channelFormatLong();
            checker.close();
        } catch (IOException e) {
            Log.error(CheckManager.class, "IO Exception", e.getMessage());
            return;
        }

//			List<Long[]> valuse = Utils.jsonList( Long[].class,  result );
        List<Long[]> valuse = new ArrayList<>(arr.length);
        for (long[] ls : arr) {
            valuse.add(new Long[]{ls[0], ls[1]});
        }
        //System.out.println(valuse);


        Iterator<Long[]> iter = valuse.iterator();
        while (iter.hasNext()) {
            long[][] info = new long[8][2];
            for (int i = 0; i < info.length; i++) {
                if (iter.hasNext()) {
                    Long[] ls = iter.next();
                    System.out.println(Arrays.toString(ls));
                    info[i] = new long[]{ls[0].longValue(), ls[1].longValue()};
                } else {
                    info[i] = new long[]{0l, 0l};
                }
            }

            Item copy = item.getCopy();
            copy.setRangeInfo(new RangeInfo(item.getRangeInfo().getFileLength(), info));
            copy.getRangeInfo().checkRanges();
            copy.getRangeInfo().avoidMissedBytes();
            copy.getRangeInfo().oneCycleDataUpdate();
            downloadService.download(copy);
            Log.info(CheckManager.class, "add item", copy.toString());
        }

    }

    public static void downloadPieces(String url, int[] downloadPieces, int chunkSize, DownloadService downloadService) {
        if (chunkSize < 1) {
            // set to default 512 KB
            chunkSize = 512 * 1024;
        }

        long[][] downoad = new long[downloadPieces.length][2];
        for (int i = 0; i < downoad.length; i++) {
            if (downloadPieces[i] > -1) {
                downoad[i][0] = downloadPieces[i] * (long) chunkSize;
                downoad[i][1] = (downloadPieces[i] + 1) * (long) chunkSize;
            } else {
                downoad[i][0] = 0l;
                downoad[i][1] = 0l;
            }
        }

        Item search = downloadService.itemStream()
                .filter(metaData -> metaData.getItem().getUrl().equals(url))
                .findFirst()
                .get().getItem();
        Log.trace(CheckManager.class, "found file chick", search.liteString());
        RangeInfo info = new RangeInfo(search.getRangeInfo().getFileLength(), downoad);
        info.oneCycleDataUpdate();
        Item copy = search.getCopy();
        copy.setRangeInfo(info);
        downloadService.download(copy);
        Log.info(CheckManager.class, "add item", copy.toString());
    }

}
