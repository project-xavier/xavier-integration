package org.jboss.xavier.analytics.pojo.output.workload.summary;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class JavaRuntimeIdentityModel implements Serializable {

    private String vendor;
    private String version;

    public JavaRuntimeIdentityModel() {
    }

    public JavaRuntimeIdentityModel(String vendor, String version) {
        this.vendor = vendor;
        this.version = version;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String flag) {
        this.vendor = flag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String osName) {
        this.version = osName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavaRuntimeIdentityModel that = (JavaRuntimeIdentityModel) o;
        return vendor.equals(that.vendor) &&
                version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vendor, version);
    }
}
