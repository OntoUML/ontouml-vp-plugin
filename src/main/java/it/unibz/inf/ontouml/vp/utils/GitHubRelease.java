package it.unibz.inf.ontouml.vp.utils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GitHubRelease {
	
	final public static String PROP_TAG_NAME = "tag_name";
	final public static String PROP_CREATED_AT = "created_at";
	final public static String PROP_PRERELEASE = "prerelease";
	final public static String PROP_ASSETS = "assets";
	final public static String PROP_ID = "id";

	public JsonObject source;
	
	GitHubRelease(JsonObject source) {
		if(source == null) {
			throw new NullPointerException();
		} else {
			this.source = source;
		}
	}
	
	public ZonedDateTime getCreatedAt() {
		return ZonedDateTime.parse(source.get(PROP_CREATED_AT).getAsString());
	}
	
	public boolean isPrerelease() {
		return source.get(PROP_PRERELEASE).getAsBoolean();
	}
	
	public String getTagName() {
		return source.get(PROP_TAG_NAME).getAsString();
	}
	
	private List<GitHubReleaseAsset> getAssets() {
		final JsonArray assets = source.get(PROP_ASSETS).getAsJsonArray();
		final List<GitHubReleaseAsset> assetsList = new ArrayList<>();
		
		assets.forEach(asset -> assetsList.add(new GitHubReleaseAsset(asset.getAsJsonObject())));
		
		return assetsList;
	}
	
	public GitHubReleaseAsset getPluginAsset() {
		for(GitHubReleaseAsset asset : getAssets() ) {
			final String name = asset.getName();
			if(name.contains("ontouml") && name.endsWith(".zip")) {
				return asset;
			}
		}
		
		return null;
	}

	public String getId() {
		return source.get(PROP_ID).getAsString();
	}
	
}
