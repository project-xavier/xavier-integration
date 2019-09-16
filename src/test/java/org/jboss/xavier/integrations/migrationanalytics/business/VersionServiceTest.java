package org.jboss.xavier.integrations.migrationanalytics.business;

import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.jboss.xavier.Application;
import org.jboss.xavier.integrations.migrationanalytics.business.versioning.ManifestPathVersion;
import org.jboss.xavier.integrations.migrationanalytics.business.versioning.VersionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = {Application.class})
@UseAdviceWith // Disables automatic start of Camel context
@ActiveProfiles("test")
public class VersionServiceTest {
    @Inject
    VersionService versionServiceBean;

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
        Map<String,String> properties = new HashMap<>();
        properties.put("cloudforms.manifest.v1_0.primero", "val1");
        properties.put("cloudforms.manifest.v1_1.primero", "val2");
        properties.put("cloudforms.manifest.v1_1_2.primero","val3");
        properties.put("cloudforms.manifest.v2.primero","val4");
        properties.put("cloudforms.manifest.v1_2.primero","val5");
        properties.put("cloudforms.manifest.v2_2.primero","val6");
        properties.put("cloudforms.manifest.v2_1_3.primero","val7");
        properties.put("cloudforms.manifest.v3_1_3.segundo","val8");
        versionService.setProperties(properties);
        assertThat(versionService.getFallbackVersionPath("1_0_0", "primero")).isEqualToIgnoringCase("1_0_0");
        assertThat(versionService.getFallbackVersionPath("1_3", "primero")).isEqualToIgnoringCase("1_2_0");
    }

    @Test
    public void getFallbackVersion_PropertiesFileGive_ReturnClosesHighestVersionPaths() {
        assertThat(versionServiceBean.getPropertyWithFallbackVersion("10_3_0", "vmworkloadinventory.providerPath")).isEqualToIgnoringCase("providerPath_v10_2_3");
        assertThat(versionServiceBean.getPropertyWithFallbackVersion("0_2_0", "hypervisor.cpuTotalCoresPath")).isEqualToIgnoringCase("cpu_total_cores");
        assertThat(versionServiceBean.getPropertyWithFallbackVersion("20_0_80", "vmworkloadinventory.providerPath")).isEqualToIgnoringCase("providerPath_v20");
        assertThat(versionServiceBean.getPropertyWithFallbackVersion("30_0_80", "vmworkloadinventory.providerPath")).isEqualToIgnoringCase("providerPath_v20_1_2");
    }



}
