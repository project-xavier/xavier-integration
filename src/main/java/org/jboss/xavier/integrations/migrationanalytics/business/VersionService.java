package org.jboss.xavier.integrations.migrationanalytics.business;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;

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

    public int compareVersion(String versionA, String versionB) {
        int major = Integer.valueOf(versionA.split("\\.")[0]).compareTo(Integer.valueOf(versionB.split("\\.")[0]));
        int minor = Integer.valueOf(versionA.split("\\.")[1]).compareTo(Integer.valueOf(versionB.split("\\.")[1]));
        int patch = Integer.valueOf(versionA.split("\\.")[2]).compareTo(Integer.valueOf(versionB.split("\\.")[2]));

        return (major != 0) ? major : (minor != 0) ? minor : patch;
    }

    public Comparator<String> sortVersions() {
        return Comparator.comparing((String n) -> Integer.valueOf(n.split(".")[0]))
                .thenComparing(n -> Integer.valueOf(n.split(".")[1]))
                .thenComparing(n -> Integer.valueOf(n.split(".")[2]));
    }


    public String getPropertyWithFallbackVersion(String payloadVersion, String path) {
        String fallbackVersion = "";
        List<String> properties = StreamSupport.stream(((AbstractEnvironment) env).getPropertySources().spliterator(), false)
                .filter(ps -> ps instanceof EnumerablePropertySource)
                .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());

        properties
                .stream()
                .filter(e -> e.matches("cloudforms\\.manifest\\.v[0-9_]*\\." + path))
                .map(e -> e.split(".")[2])
                .map(e -> e.replace("v", "").replace("_", "."))
                .map(e -> expandVersion(e))
                .sorted(sortVersions())
                .filter(e -> compareVersion(payloadVersion, e) <= 0 )
                .findFirst();


        return env.getProperty("cloudforms.manifest." + payloadVersion + path, env.getProperty("cloudforms.manifest." + fallbackVersion + path));
    }

    public String expandVersion(String e) {
        return (String) IntStream.range(1, 2 - e.split(".").length).mapToObj(f -> ".0").collect(Collectors.joining());
    }
}
