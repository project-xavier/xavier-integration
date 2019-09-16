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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Named
public class VersionService {
    @Inject
    private Environment env;

    @Getter
    @Setter
    private Map<String, String> properties;

    @PostConstruct
    public void init() {
        properties = getAllProperties();
    }

    public String getPropertyWithFallbackVersion(String payloadVersion, String path) {
        String fallbackVersion = getFallbackVersionPath(payloadVersion, path);

        return properties.get("cloudforms.manifest.v" + fallbackVersion + "." + path);
    }

    public String getFallbackVersionPath(String payloadVersion, String path) {
        ManifestPathVersion fallbackversion = properties.keySet()
                .stream()
                .filter(e -> e.matches("cloudforms.manifest.v[0-9_]*." + path))
                .map(e -> e.split("\\.")[2])
                .map(e -> e.replace("v", ""))
                .map(this::expandVersion)
                .map(ManifestPathVersion::new)
                .sorted(Comparator.reverseOrder())
                .filter(e -> isVersionEqualOrLower(payloadVersion, e))
                .findFirst()
                .orElse(new ManifestPathVersion("0_0_0"));

        return fallbackversion.getVersion();
    }

    private boolean isVersionEqualOrLower(String payloadVersion, ManifestPathVersion e) {
        return e.compareTo(expandVersion(payloadVersion)) <= 0;
    }

    private Map<String, String> getAllProperties() {
        return StreamSupport.stream(((AbstractEnvironment) env).getPropertySources().spliterator(), false)
                    .filter(ps -> ps instanceof EnumerablePropertySource)
                    .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
                    .flatMap(Arrays::stream)
                .filter(e -> e.matches("cloudforms.manifest.v[0-9_]*.*"))
                .collect(Collectors.toMap( this::getKey, e-> env.getProperty(e)));
    }

    private String getKey(String property) {
        String[] elements = property.split("\\.");
        return property.replace(elements[2], "v" + expandVersion(elements[2].replace("v", "")));
    }

    public String expandVersion(String e) {
        return e.concat(IntStream.range(1, 4 - e.split("_").length).mapToObj(f -> "_0").collect(Collectors.joining()));
    }
}
