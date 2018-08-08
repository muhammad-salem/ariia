package org.okaria.plugin.saveitoffline;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class OfflineObject {

	String title;
	String thumbnail;
	List<Quality> urls;

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public List<Quality> getUrls() {
		return urls;
	}
	public void setUrls(List<Quality> urls) {
		this.urls = urls;
	}
	
	public Stream<Quality> get720p() {
		return urls.stream().filter(new Predicate<Quality>() {
			@Override
			public boolean test(Quality t) {
				return t.is720p();
			}
		});
	}
	
	public Stream<Quality> get720pVideo() {
		return urls.stream().filter(new Predicate<Quality>() {
			@Override
			public boolean test(Quality t) {
				return t.is720p() & ! t.no_video();
			}
		});
	}

	public Stream<Quality> get1080p() {
		return urls.stream().filter(new Predicate<Quality>() {
			@Override
			public boolean test(Quality t) {
				return t.is1080p();
			}
		});
	}
	
	public Stream<Quality> get1080pVideo() {
		return urls.stream().filter(new Predicate<Quality>() {
			@Override
			public boolean test(Quality t) {
				return t.is1080p() & ! t.no_video();
			}
		});
	}
	
	// Quality 
	public static class Quality {

		String label; // ": "1080p",
		String id; // ": "https://www.saveitoffline.com/get/?i=TEST",
		String size; // ": "x",
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getSize() {
			return size;
		}
		public void setSize(String size) {
			this.size = size;
		}
		
		@Override
		public String toString() {
			return label + ' ' + "(size: " + size +"): " + id;
		}
		
		public String filename() {
			return label + ' ' + "(size: " + size +")";
		}
		
		public boolean mp4() {
			return isLabelContains("mp4");
		}
		public boolean webm() {
			return isLabelContains("webm");
		}
		public boolean _3gp() {
			return isLabelContains("3gp");
		}
		public boolean m4a() {
			return isLabelContains("m4a");
		}
		public boolean mp3() {
			return isLabelContains("mp3");
		}
		
		public boolean no_sound() {
			return isLabelContains("no sound");
		}
		
		public boolean no_video() {
			return isLabelContains("no video");
		}
		
		public boolean is1080p() {
			return isLabelContains("1080p");
		}
		
		public boolean is720p() {
			return isLabelContains("720p");
		}
		public boolean is480p() {
			return isLabelContains("480p");
		}
		public boolean is360p() {
			return isLabelContains("360p");
		}
		public boolean is240p() {
			return isLabelContains("240p");
		}
		public boolean is144pp() {
			return isLabelContains("144p");
		}
		
		public boolean is160kbps() {
			return isLabelContains("160 kbps");
		}
		public boolean is128kbps() {
			return isLabelContains("128 kbps");
		}
		public boolean is70kbps() {
			return isLabelContains("70 kbps");
		}
		public boolean is50kbps() {
			return isLabelContains("50 kbps");
		}
		
		private boolean isLabelContains(String media) {
			return label.toLowerCase().contains(media);
		}
		
/*
 * 
360p - mp4 (size: x): http://www.saveitoffline.com/get/?i=rmZejKJzqVJtDZnmns02fKuG0QBZcF7W&u=xbavt06ws3oqwUCu7h8Wf7p3PFzw3wWG
360p - webm (size: x): http://www.saveitoffline.com/get/?i=rmZejKJzqVJtDZnmns02fKuG0QBZcF7W&u=pR0DIAI9bxBvUmEah0CjCyEobBK3NzND
240p - 3gp (size: x): http://www.saveitoffline.com/get/?i=rmZejKJzqVJtDZnmns02fKuG0QBZcF7W&u=hRGJHX4ilB5PQeXHV41qJiHPpUskFOql
144p - 3gp (size: x): http://www.saveitoffline.com/get/?i=rmZejKJzqVJtDZnmns02fKuG0QBZcF7W&u=dvKUn1y8qQ3ER24jz6Mo6gKTk8IZJQwq
(video - no sound) 360p - mp4 (size: x): http://www.saveitoffline.com/get/?i=rmZejKJzqVJtDZnmns02fKuG0QBZcF7W&u=pMK3hEMF1UScqoeYwbUmI1pjH93HrJf9
(video - no sound) 240p - mp4 (size: x): http://www.saveitoffline.com/get/?i=rmZejKJzqVJtDZnmns02fKuG0QBZcF7W&u=aVIo7HEaViKNyKIZeJEkABPjvH74nYFs
(video - no sound) 144p - mp4 (size: x): http://www.saveitoffline.com/get/?i=rmZejKJzqVJtDZnmns02fKuG0QBZcF7W&u=gNT5uGLCmewIu7E0SgFCH10UsHOIwKLY
(video - no sound) 360p - webm (size: x): http://www.saveitoffline.com/get/?i=rmZejKJzqVJtDZnmns02fKuG0QBZcF7W&u=hhc6zT2Pw2VKdtOd6iBY6tTeyCOObq4y
(video - no sound) 240p - webm (size: x): http://www.saveitoffline.com/get/?i=rmZejKJzqVJtDZnmns02fKuG0QBZcF7W&u=jLz9j8R3YF6w9egHEZNQIgK4wx5nQVFK
(video - no sound) 144p - webm (size: x): http://www.saveitoffline.com/get/?i=rmZejKJzqVJtDZnmns02fKuG0QBZcF7W&u=YuYC8qW9tAeHrzfdmlKYK6X8UHiQWlyI
(audio - no video) webm (160 kbps) (size: x): http://www.saveitoffline.com/get/?i=rmZejKJzqVJtDZnmns02fKuG0QBZcF7W&u=26RxLuh6OD0kGMPMvRCijCjnGieicDcO
(audio - no video) m4a (128 kbps) (size: x): http://www.saveitoffline.com/get/?i=rmZejKJzqVJtDZnmns02fKuG0QBZcF7W&u=xVKNRO5P4Hw0kR4Q358Oa1rxWykjoPtE
(audio - no video) webm (128 kbps) (size: x): http://www.saveitoffline.com/get/?i=rmZejKJzqVJtDZnmns02fKuG0QBZcF7W&u=TAZGshP6lGZ9EDWvB2JDkBPltzTBFYQR
(audio - no video) webm (70 kbps) (size: x): http://www.saveitoffline.com/get/?i=rmZejKJzqVJtDZnmns02fKuG0QBZcF7W&u=wjC2kPGEklQ3eMHWwmBL9lKmAzPBIvcW
(audio - no video) webm (50 kbps) (size: x): http://www.saveitoffline.com/get/?i=rmZejKJzqVJtDZnmns02fKuG0QBZcF7W&u=JTFN5CMrmR5uJaPGKa8bkxWilW6hzgHT
 */
	}

}
