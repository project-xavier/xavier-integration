package org.jboss.xavier.integrations.migrationanalytics.business;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class VersionServiceTest {
    @Test
    public void compareVersion_FewVersionNumbersGiven_ReturnsCorrectComparisson() {
      VersionService versionService = new VersionService();
      assertThat(versionService.compareVersion("1.0.0", "1.0.0")).isEqualTo(0);
      assertThat(versionService.compareVersion("2.0.0", "1.0.0")).isEqualTo(1);
      assertThat(versionService.compareVersion("1.0.0", "2.0.0")).isEqualTo(-1);
      assertThat(versionService.compareVersion("1.1.0", "2.0.0")).isEqualTo(-1);
      assertThat(versionService.compareVersion("1.1.0", "1.2.0")).isEqualTo(-1);
      assertThat(versionService.compareVersion("1.2.0", "1.1.0")).isEqualTo(1);
      assertThat(versionService.compareVersion("1.2.1", "1.1.0")).isEqualTo(1);
      assertThat(versionService.compareVersion("1.2.1", "1.2.2")).isEqualTo(-1);
      assertThat(versionService.compareVersion("1.2.1", "2.1.2")).isEqualTo(-1);
    }

}
