package it.unibz.inf.ontouml.vp.utils;

import com.google.gson.JsonObject;

public class GitHubReleaseAsset {
	
	final public static String PROP_NAME = "name";
	final public static String PROP_ID = "id";
	final public static String PROP_BROWSER_DOWNLOAD_URL = "browser_download_url";
	
	public JsonObject source;
	
	GitHubReleaseAsset(JsonObject source) {
		if(source == null) {
			throw new NullPointerException();
		} else {
			this.source = source;
		}
	}
	
	public String getName() {
		return source.get(PROP_NAME).getAsString();
	}
	
	public String getDownloadUrl() {
		return source.get(PROP_BROWSER_DOWNLOAD_URL).getAsString();
	}
	
	public String getId() {
		return source.get(PROP_ID).getAsString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof GitHubRelease ? this.getId().equals(((GitHubRelease) obj).getId()) : false;
	}

}
