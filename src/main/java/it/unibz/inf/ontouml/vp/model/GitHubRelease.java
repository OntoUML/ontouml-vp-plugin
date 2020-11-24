package it.unibz.inf.ontouml.vp.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class GitHubRelease {

  private static final String PROP_TAG_NAME = "tag_name";
  private static final String PROP_CREATED_AT = "created_at";
  private static final String PROP_PRERELEASE = "prerelease";
  private static final String PROP_ASSETS = "assets";
  private static final String PROP_ID = "id";

  @Expose() private String tagName;

  // Since Gson shows problems with the default serialization of ZonedDateTime we rely on strings
  @Expose() private String createdAt;

  @Expose() private boolean isPrerelease;

  @Expose() private List<GitHubReleaseAsset> assets;

  @Expose() private GitHubReleaseAsset installationFileAsset;

  @SerializedName("releaseId")
  @Expose()
  private String id;

  public GitHubRelease() {
    tagName = null;
    createdAt = null;
    isPrerelease = false;
    assets = null;
    installationFileAsset = null;
    id = null;
  }

  public GitHubRelease(JsonObject source) {
    if (source == null) {
      throw new NullPointerException();
    }

    tagName = source.get(PROP_TAG_NAME).getAsString();
    createdAt = source.get(PROP_CREATED_AT).getAsString();
    isPrerelease = source.get(PROP_PRERELEASE).getAsBoolean();
    id = source.get(PROP_ID).getAsString();

    assets = new ArrayList<>();
    installationFileAsset = null;

    JsonArray assetsArray = source.get(PROP_ASSETS).getAsJsonArray();
    assetsArray.forEach(
        item -> {
          GitHubReleaseAsset asset =
              item.isJsonObject() ? new GitHubReleaseAsset(item.getAsJsonObject()) : null;

          if (asset != null) {
            assets.add(asset);
          }

          if (asset.isInstallationFileAsset()) {
            installationFileAsset = asset;
          }
        });
  }

  public ZonedDateTime getCreatedAt() {
    if (createdAt != null) return ZonedDateTime.parse(createdAt);

    return null;
  }

  public boolean isPrerelease() {
    return isPrerelease;
  }

  public String getTagName() {
    return tagName;
  }

  public List<GitHubReleaseAsset> getAssets() {
    return assets;
  }

  public GitHubReleaseAsset getInstallationFileAsset() {
    return installationFileAsset;
  }

  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof GitHubRelease ? getId().equals(((GitHubRelease) obj).getId()) : false;
  }
}
