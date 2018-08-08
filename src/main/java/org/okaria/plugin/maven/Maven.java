package org.okaria.plugin.maven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.okaria.R;
import org.okaria.manager.Item;

public class Maven {
	static String CENTRAL_MAVEN = "http://central.maven.org/maven2";
	public static String MAVEN_REPOSITORY = R.UserHome + R.sprtr + ".m2" + R.sprtr + "repository" ;
	
	static String FILE[] = new String[]
			{".jar", ".pom", "-javadoc.jar", "-sources.jar", ".aar", "-docs.zip", "-spi-javadoc.jar", "-test-sources.jar", "-tests.jar"};
	
	static String CERTIFACATES[] = new String[] {".sha1", ".asc", ".md5", ".asc.sha1", ".asc.md5"};
	
	String baseUrl;
	String groupId;
	String artifactId;
	String version;
	
	
	
	public Maven() {
		this.baseUrl = CENTRAL_MAVEN;
	}
	
	public Maven(String baseUrl) {
		if(baseUrl == null)
			this.baseUrl = CENTRAL_MAVEN;
		else {
			
			int i = baseUrl.indexOf("//");
			String[] parts = baseUrl.substring( i+2 ).split("/");
			if(parts.length == 2) {
				this.baseUrl = baseUrl;
			}
			else if(parts.length > 2) {
				this.baseUrl = baseUrl.substring(0, i) + "//" + parts[0] + '/' + parts[1];
			}
			if(parts.length > 2) {
				int pomIndex = parts.length;
				if(parts[parts.length-1].contains(".pom")) {
					setVersion(parts[parts.length-2]);
					setArtifactId(parts[parts.length-3]);
					pomIndex = parts.length-3;
					
				}else {
					setVersion(parts[parts.length-1]);
					setArtifactId(parts[parts.length-2]);
					pomIndex = parts.length-2;
				}
				
				String groubid = "";
				for (int j = 2; j < pomIndex; j++) {
					groubid += parts[j] + '.';
				}
				setGroupId(groubid.substring(0, groubid.length()-1));
			}
		}
		
	}
	
	@Override
	public String toString() {
		return "Maven[groupId=" + groupId + " artifactId=" + artifactId + " version=" + version + "]";
	}
	
	public List<Item> createBuilder(String savePath) {
		Map<String, String> map = urls();
		List<Item> builders = new ArrayList<>();
		for (String name : map.keySet()) {
			Item item = new Item();
			item.setUrl(map.get(name));
			item.setFolder(savePath);
			item.setFilename(name);
			builders.add(item);
		}
		return builders;
	}
	
	public Map<String, String> urls(){
		String bUrl = baseUrl + resolvePath();
		HashMap<String, String> map = new HashMap<>();
		for (String ext : FILE) {
			String name = fileName(ext);
			String url = bUrl + name;
			map.put(name, url);
			for (String crt : CERTIFACATES) {
				map.put(name+crt, url+crt);
			}
		}
		return map;
	}
	
	
	public String fileName(String ext) {
		return artifactId + '-' + version + ext;
	}
	
	public String jar() {
		return artifactId + '-' + version + ".jar";
	}
	public String pom() {
		return artifactId + '-' + version + ".pom";
	}
	public String sources() {
		return artifactId + '-' + version + "-sources.jar";
	}
	public String javadoc() {
		return artifactId + '-' + version + "-javadoc.jar";
	}
	public String sha1(String name ) {
		return name + ".sha1";
	}
	
	
	public String resolvePath() {
		return '/' + groupId.replace('.', '/') + '/' + artifactId + '/' + version + '/';
	}



	public String getBaseUrl() {
		return baseUrl;
	}
	
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	

	
}
