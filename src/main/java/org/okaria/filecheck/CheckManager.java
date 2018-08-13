package org.okaria.filecheck;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.log.concurrent.Log;
import org.okaria.R;
import org.okaria.Utils;
import org.okaria.manager.Item;
import org.okaria.okhttp.service.ServiceManager;
import org.okaria.range.RangeInfo;

public class CheckManager {

	
	public static void CheckItem(String url, int chunkSize, ServiceManager manager) {
		
			Item item = manager.getItemStore().searchByUrl(url);
			
			String result = R.getConfigPath("check" + R.sprtr + item.getFilename() + ".json");
			
				try {
					RangeChecker checker = new RangeChecker(item.path(), chunkSize);
					R.mkParentDir(result);
					checker.channelFormat(result);
					checker.close();
				} catch (IOException e) {
					Log.error(CheckManager.class, "IO Exception", e.getMessage());
					return;
				}
				
				List<Long[]> valuse = Utils.jsonList( Long[].class,  result );
				
				
			Iterator<Long[]> iter = valuse.iterator();
			while (iter.hasNext()) {
				long[][] info = new long[8][2];
				for (int i = 0; i < info.length; i++) {
					if(iter.hasNext()) {
						Long[] ls =  iter.next();
						System.out.println(Arrays.toString(ls));
						info[i] = new long[] {ls[0], ls[1]};
					}else {
						info[i] = new long[] {0l, 0l};
					}
				}
				
				Item copy = item.copy();
				copy.setRangeInfo(new RangeInfo(item.getRangeInfo().getFileLength(), info));
				copy.getRangeInfo().checkRanges();
				copy.getRangeInfo().avoidMissedBytes();
				copy.getRangeInfo().oneCycleDataUpdate();
				copy.setCacheFile(null);
				manager.warrpItem(copy);
				Log.info(CheckManager.class, "add item", copy.toString());
			}
			
			
			
			
		
		
		
	}

}
