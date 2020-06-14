package org.ariia.mvc.router;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Routes {
	
	private String path;
	private List<Routes> children;
	
	public Routes(String path){
		this.path = Objects.requireNonNull(path);
		this.children = new ArrayList<>();
	}
	
	public Routes(String path, String... children){
		this(path);
		this.routes(children);
	}
	
	public Routes routes(String... children){
		for (String child : children) {
			this.children.add(new Routes(child));
		}
		return this;
	}
	
	public Routes routes(Routes... children){
		for (Routes child : children) {
			this.children.add(child);
		}
		return this;
	}
	
	public String getPath() {
		return path;
	}
	
	public List<Routes> getChildren() {
		return children;
	}
	
	public List<String>  paths() {
		return paths(this);
	}
	
	private List<String>  paths(Routes root) {
		List<String> paths = new ArrayList<>();
		paths.add(root.path);
		for (Routes route : root.children) {
			for (String subPath : route.paths()) {
				paths.add(path + '/' + subPath);
			}
		}
		return paths;
	}
	
	public Stream<String> childRoutes() {
		return children.stream()
				.map(this::paths)
				.flatMap(List::stream);
	}
	
	public List<String> getChildRoutes() {
		return children.stream()
				.map(this::paths)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}
	
	public boolean lookupRoute(String path) {
		if (Objects.isNull(path)) {
			return false;
		}
		
		if (path.startsWith(this.path)) {
			if (Objects.equals(this.path, path)) {
				return true;
			} else {
				String subPath = path.replaceFirst(this.path, "");
				if(subPath.startsWith("/")) {
					subPath = subPath.substring(1);
				}
				for (Routes route : children) {
					if(route.lookupRoute(subPath)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
