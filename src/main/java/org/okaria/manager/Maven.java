package org.okaria.manager;

import java.util.ArrayList;
import java.util.List;

import org.okaria.R;

public class Maven {
	static String CENTRAL_MAVEN = "http://central.maven.org/maven2";
	public static String MAVEN_REPOSITORY = R.UserHome + R.sprtr + ".m2" + R.sprtr + "repository" ;
	
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
		else
			this.baseUrl = baseUrl;
	}
	
	@Override
	public String toString() {
		return "Maven[groupId=" + groupId + " artifactId=" + artifactId + " version=" + version + "]";
	}
	
	
	public List<String> generateURLS() {
		List<String> urls = new ArrayList<>();
		String path = resolvePath();
		urls.add(baseUrl + path + jar() );
		urls.add(baseUrl + path + pom() );
		urls.add(baseUrl + path + sources() );
		urls.add(baseUrl + path + javadoc() );
		
		urls.add(baseUrl + path + sha1(jar()));
		urls.add(baseUrl + path + sha1(pom()));
		urls.add(baseUrl + path + sha1(sources()));
		urls.add(baseUrl + path + sha1(javadoc()));
		return urls;
	}
	
	public List<Item.Builder> generateBuilder(String savePath) {
		
		String urlpath = baseUrl + resolvePath();
		if(savePath == null) savePath = MAVEN_REPOSITORY;
		List<Item.Builder> builders = new ArrayList<>();
		Item.Builder b = new Item.Builder();
		b.url(urlpath + jar());
		b.savepath(savePath);
		b.filename(jar());
		builders.add(b);
		
		b = new Item.Builder();
		b.url(urlpath + pom());
		b.savepath(savePath);
		b.filename(pom());
		builders.add(b);
		
		b = new Item.Builder();
		b.url(urlpath + sources());
		b.savepath(savePath);
		b.filename(sources());
		builders.add(b);
		
		b = new Item.Builder();
		b.url(urlpath + javadoc());
		b.savepath(savePath);
		b.filename(javadoc());
		builders.add(b);
		
		b = new Item.Builder();
		b.url(urlpath + sha1(jar()));
		b.savepath(savePath);
		b.filename(sha1(jar()));
		builders.add(b);
		
		b = new Item.Builder();
		b.url(urlpath + sha1(pom()));
		b.savepath(savePath);
		b.filename(sha1(pom()));
		builders.add(b);
		
		b = new Item.Builder();
		b.url(urlpath + sha1(sources()));
		b.savepath(savePath);
		b.filename(sha1(sources()));
		builders.add(b);
		
		b = new Item.Builder();
		b.url(urlpath + sha1(javadoc()));
		b.savepath(savePath);
		b.filename(sha1(javadoc()));
		builders.add(b);
		
		return builders;
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
