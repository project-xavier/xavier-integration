package org.jboss.xavier.integrations.migrationanalytics.business.versioning;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Named
public class VersionService {
    @Inject
    private Environment env;

    @Getter
    @Setter
    private List<String> properties;

    @PostConstruct
    public void init() {
        properties = getAllProperties();
    }

    public String getPropertyWithFallbackVersion(String payloadVersion, String path) {
        String fallbackVersion = getFallbackVersionPath(payloadVersion, path);

        return env.getProperty("cloudforms.manifest." + payloadVersion + path, env.getProperty("cloudforms.manifest.v" + fallbackVersion + path));
    }

    public String getFallbackVersionPath(String payloadVersion, String path) {

        return properties
                    .stream()
                    .filter(e -> e.matches("cloudforms\\.manifest\\.v[0-9_]*\\." + path))
                    .map(e -> e.split("\\.")[2])
                    .map(e -> e.replace("v", ""))
                    .map(this::expandVersion)
                    .map(ManifestPathVersion::new)
                    .sorted(Comparator.reverseOrder())
                    .filter(e -> e.compareTo(expandVersion(payloadVersion)) <= 0 )
                    .findFirst()
                    .map(ManifestPathVersion::getVersion)
                    .orElse("v0_0_0");
    }

    private List<String> getAllProperties() {
        return StreamSupport.stream(((AbstractEnvironment) env).getPropertySources().spliterator(), false)
                    .filter(ps -> ps instanceof EnumerablePropertySource)
                    .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
                    .flatMap(Arrays::stream)
                    .filter(e -> e.startsWith("cloudforms.manifest."))
                    .collect(Collectors.toList());
    }

    public String expandVersion(String e) {
        return e.concat(IntStream.range(1, 4 - e.split("_").length).mapToObj(f -> "_0").collect(Collectors.joining()));
    }
}
