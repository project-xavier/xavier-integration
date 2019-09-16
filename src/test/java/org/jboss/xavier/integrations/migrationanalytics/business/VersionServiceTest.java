package org.jboss.xavier.integrations.migrationanalytics.business;

import org.jboss.xavier.integrations.migrationanalytics.business.versioning.ManifestPathVersion;
import org.jboss.xavier.integrations.migrationanalytics.business.versioning.VersionService;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class VersionServiceTest {
    @Test
    public void compareVersion_FewVersionNumbersGiven_ReturnsCorrectComparisson() {
      assertThat(new ManifestPathVersion("1_0_0").compareTo("1_0_0")).isEqualTo(0);
      assertThat(new ManifestPathVersion("2_0_0").compareTo( "1_0_0")).isEqualTo(1);
      assertThat(new ManifestPathVersion("1_0_0").compareTo( "2_0_0")).isEqualTo(-1);
      assertThat(new ManifestPathVersion("1_1_0").compareTo( "2_0_0")).isEqualTo(-1);
      assertThat(new ManifestPathVersion("1_1_0").compareTo( "1_2_0")).isEqualTo(-1);
      assertThat(new ManifestPathVersion("1_2_0").compareTo( "1_1_0")).isEqualTo(1);
      assertThat(new ManifestPathVersion("1_2_1").compareTo( "1_1_0")).isEqualTo(1);
      assertThat(new ManifestPathVersion("1_2_1").compareTo( "1_2_2")).isEqualTo(-1);
      assertThat(new ManifestPathVersion("1_2_1").compareTo( "2_1_2")).isEqualTo(-1);
    }

    @Test
    public void sortVersions_FewVersionNumbersGiven_ReturnsCorrectOrder() {
        VersionService versionService = new VersionService();
        List<String> mylist = Arrays.asList("2_1_1","1_0_0", "1_8_2","1_1_0", "2_2_0");
        List<ManifestPathVersion> mySortedList = mylist.stream().map(ManifestPathVersion::new).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        assertThat(mySortedList.get(mySortedList.size() -1)).isEqualTo(new ManifestPathVersion("1_0_0"));
        assertThat(mySortedList.get(0)).isEqualTo(new ManifestPathVersion("2_2_0"));
    }

    @Test
    public void expandVersion_fewShortAndLargeVersionsGiven_ReturnsFullVersionTexts() {
        VersionService versionService = new VersionService();
        assertThat(versionService.expandVersion("v1")).isEqualToIgnoringCase("v1_0_0");
        assertThat(versionService.expandVersion("v1_0")).isEqualToIgnoringCase("v1_0_0");
        assertThat(versionService.expandVersion("v1_0_0")).isEqualToIgnoringCase("v1_0_0");
    }

    @Test
    public void getFallbackVersion_FewShortAndLongVersions_ReturnsClosestHighestVersions() {
        VersionService versionService = new VersionService();
        versionService.setProperties(Arrays.asList("cloudforms.manifest.v1_0.primero",
                                                   "cloudforms.manifest.v1_1.primero",
                                                   "cloudforms.manifest.v1_1_2.primero",
                                                   "cloudforms.manifest.v2.primero",
                                                   "cloudforms.manifest.v1_2.primero",
                                                   "cloudforms.manifest.v2_2.primero",
                                                   "cloudforms.manifest.v2_1_3.primero",
                                                   "cloudforms.manifest.v3_1_3.segundo"));
        assertThat(versionService.getFallbackVersionPath("1_0_0", "primero")).isEqualToIgnoringCase("1_0_0");
        assertThat(versionService.getFallbackVersionPath("1_3", "primero")).isEqualToIgnoringCase("1_2_0");
    }


}
