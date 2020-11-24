package it.unibz.inf.ontouml.vp.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GitHubReleaseAsset {

  private static final String PROP_NAME = "name";
  private static final String PROP_ID = "id";
  private static final String PROP_BROWSER_DOWNLOAD_URL = "browser_download_url";
  private static final String PROP_CONTENT_TYPE = "content_type";

  private static final String CONTENT_TYPE_APPLICATION_ZIP = "application/zip";

  //	public JsonObject source;

  @Expose() private String name;

  @SerializedName("assetId")
  @Expose()
  private String id;

  @Expose() private String downloadUrl;

  @Expose() private String contentType;

  public GitHubReleaseAsset() {
    name = null;
    id = null;
    downloadUrl = null;
    contentType = null;
  }

  public GitHubReleaseAsset(JsonObject source) {
    if (source == null) {
      throw new NullPointerException();
    }

    name = source.get(PROP_NAME).getAsString();
    id = source.get(PROP_ID).getAsString();
    downloadUrl = source.get(PROP_BROWSER_DOWNLOAD_URL).getAsString();
    contentType = source.get(PROP_CONTENT_TYPE).getAsString();
  }

  public String getName() {
    return name;
  }

  public String getDownloadUrl() {
    return downloadUrl;
  }

  public String getId() {
    return id;
  }

  public String getContentType() {
    return contentType;
  }

  public boolean isInstallationFileAsset() {
    return getContentType().equals(CONTENT_TYPE_APPLICATION_ZIP);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof GitHubRelease ? getId().equals(((GitHubRelease) obj).getId()) : false;
  }
}
