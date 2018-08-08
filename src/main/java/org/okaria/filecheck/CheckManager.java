package org.okaria.filecheck;

import java.io.IOException;

import org.log.concurrent.Log;
import org.okaria.R;
import org.okaria.Utils;
import org.okaria.manager.Item;
import org.okaria.okhttp.service.ServiceManager;
import org.okaria.range.RangeInfo;

public class CheckManager {

	public static void CheckItem(String url, ServiceManager manager) {
		
		try {
			Item item = manager.getItemStore().searchByUrl(url);
			
			String result = R.getConfigPath("check" + R.sprtr + item.getFilename() + ".json");
			long[][] valuse = Utils.fromJson(result, long[][].class);
			if(valuse == null) {
				RangeChecker checker = new RangeChecker(item.path());
				R.mkParentDir(result);
				checker.channelrFormat(result);
				checker.close();
				valuse = Utils.fromJson(long[][].class, result);
			}
			
			for (int i = 0; i < valuse.length/8; i += 8) {
				long[][] info = new long[8][2];
				System.arraycopy(valuse, i, info, 0, 8);
				Item copy = item.copy();
				copy.setRangeInfo(new RangeInfo(item.getRangeInfo().getFileLength(), info));
				copy.getRangeInfo().oneCycleDataUpdate();
				copy.setCacheFile(null);
				manager.warrpItem(copy);
				Log.info(copy.toString());
			}
			
			
			
			
		} catch (IOException e) {
			Log.error(CheckManager.class, "IO Exception", e.getMessage());
		}
		
		
	}

}
