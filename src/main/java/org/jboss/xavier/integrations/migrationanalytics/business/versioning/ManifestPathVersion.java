package org.jboss.xavier.integrations.migrationanalytics.business.versioning;

import lombok.Data;

@Data
public class ManifestPathVersion implements Comparable<ManifestPathVersion> {
    private final String version;

    @Override
    public int compareTo(ManifestPathVersion versionB) {
        int major = Integer.valueOf(version.split("_")[0]).compareTo(Integer.valueOf(versionB.getVersion().split("_")[0]));
        int minor = Integer.valueOf(version.split("_")[1]).compareTo(Integer.valueOf(versionB.getVersion().split("_")[1]));
        int patch = Integer.valueOf(version.split("_")[2]).compareTo(Integer.valueOf(versionB.getVersion().split("_")[2]));

        return (major != 0) ? major : (minor != 0) ? minor : patch;
    }

    public int compareTo(String versionB) {
        return compareTo(new ManifestPathVersion(versionB));
    }

}
