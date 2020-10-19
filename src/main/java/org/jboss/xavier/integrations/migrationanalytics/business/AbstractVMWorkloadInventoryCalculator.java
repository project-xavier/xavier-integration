package org.jboss.xavier.integrations.migrationanalytics.business;

import com.jayway.jsonpath.DocumentContext;
import lombok.extern.slf4j.Slf4j;
import org.jboss.xavier.integrations.migrationanalytics.business.issuehandling.AnalysisIssuesHandler;
import org.jboss.xavier.integrations.migrationanalytics.business.versioning.ManifestVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractVMWorkloadInventoryCalculator {

    public static final String VMPATH = "vmworkloadinventory.vmPath";
    public static final String CLUSTERPATH = "vmworkloadinventory.clusterPath";
    public static final String DATACENTERPATH = "vmworkloadinventory.datacenterPath";
    public static final String PROVIDERPATH = "vmworkloadinventory.providerPath";
    public static final String GUESTOSFULLNAMEPATH = "vmworkloadinventory.guestOSPath";
    public static final String GUESTOSFULLNAME_FALLBACKPATH = "vmworkloadinventory.guestOSFallbackPath";
    public static final String VMNAMEPATH = "vmworkloadinventory.vmNamePath";
    public static final String NUMCPUPATH = "vmworkloadinventory.numCpuPath";
    public static final String NUMCORESPERSOCKETPATH = "vmworkloadinventory.numCoresPerSocketPath";
    public static final String HASRDMDISKPATH = "vmworkloadinventory.hasRDMDiskPath";
    public static final String MEMORYHOTADDENABLEDPATH = "vmworkloadinventory.memoryHotAddEnabledPath";
    public static final String CPUHOTADDENABLEDPATH = "vmworkloadinventory.cpuHotAddEnabledPath";
    public static final String CPUHOTREMOVEENABLEDPATH = "vmworkloadinventory.cpuHotRemoveEnabledPath";
    public static final String RAMSIZEINBYTES = "vmworkloadinventory.ramSizeInBytesPath";
    public static final String CPUAFFINITYPATH = "vmworkloadinventory.cpuAffinityPath";
    public static final String NICSPATH = "vmworkloadinventory.nicsPath";
    public static final String PRODUCTNAMEPATH = "vmworkloadinventory.productNamePath";
    public static final String PRODUCTNAME_FALLBACKPATH = "vmworkloadinventory.productNameFallbackPath";
    public static final String DISKSIZEPATH = "vmworkloadinventory.diskSizePath";
    public static final String EMSCLUSTERIDPATH = "vmworkloadinventory.emsClusterIdPath";
    public static final String VMEMSCLUSTERPATH = "vmworkloadinventory.vmEmsClusterPath";
    public static final String VMDISKSFILENAMESPATH = "vmworkloadinventory.vmDiskFileNamesPath";
    public static final String SYSTEMSERVICESNAMESPATH = "vmworkloadinventory.systemServicesNamesPath";
    public static final String FILESCONTENTPATH = "vmworkloadinventory.filesContentPath";
    public static final String FILESCONTENTPATH_FILENAME = "vmworkloadinventory.filesContentPathName";
    public static final String FILESCONTENTPATH_CONTENTS = "vmworkloadinventory.filesContentPathContents";
    public static final String PRODUCTPATH = "vmworkloadinventory.productPath";
    public static final String VERSIONPATH = "vmworkloadinventory.versionPath";
    public static final String HOSTNAMEPATH = "vmworkloadinventory.hostNamePath";
    public static final String VMDISKSPATH = "vmworkloadinventory.vmDisksPath";
    public static final String DATACOLLECTEDON = "datacollectedon" ;
    public static final String USEDDISKSTORAGEPATH = "vmworkloadinventory.usedDiskSpacePath";
	  public static final String USBCONTROLLERS = "vmworkloadinventory.hasUSBcontroller";
    public static final String HASPASSTHROUGHDEVICEPATH = "vmworkloadinventory.hasPassthroughDevice";
    public static final String HASVMAFFINITYCONFIG = "vmworkloadinventory.hasVmAffinityConfig";
    public static final String NUMANODEAFFINITY = "vmworkloadinventory.numaNodeAffinity";
    public static final String FIRMWARE = "vmworkloadinventory.firmware";
    public static final String HASVMDRSCONFIG = "vmworkloadinventory.hasVmDrsConfig";
    public static final String HASVMHACONFIG = "vmworkloadinventory.hasVmHaConfig";
    public static final String BALLOONEDMEMORY = "vmworkloadinventory.balloonedMemory";
    public static final String HASENCRYPTEDDISK = "vmworkloadinventory.hasEncryptedDisk";
    public static final String HASOPAQUENETWORK = "vmworkloadinventory.hasOpaqueNetwork";
    public static final String HASSRIOVNIC = "vmworkloadinventory.hasSrIovNic";

    @Autowired
    protected Environment env;

    @Inject
    protected ManifestVersionService manifestVersionService;

    @Inject
    protected AnalysisIssuesHandler analysisIssuesHandler;

    protected DocumentContext jsonParsed;
    protected String manifestVersion;
    protected Date scanRunDate;

    protected Map<String, String> readMapValuesFromExpandedEnvVarPath(String envVarPath, Map vmStructMap, String keyfield, String valuefield) {
        String expandParamsInPath = getExpandedPath(envVarPath, vmStructMap);
        Map<String,String> files = new HashMap<>();
        try {
            List<List<Map>> value = jsonParsed.read(expandParamsInPath);
            value.stream().flatMap(Collection::stream).collect(Collectors.toList()).forEach(e-> files.put((String) e.get(keyfield), (String) e.get(valuefield)));
        } catch (Exception e) {
            analysisIssuesHandler.record(vmStructMap.get("_analysisId").toString(), "VM", vmStructMap.get("name").toString(), expandParamsInPath, e.getMessage());
        }
        return files;
    }

    protected <T> T readValueFromExpandedEnvVarPath(String envVarPath, Map vmStructMap, Class type) {
        String expandParamsInPath = getExpandedPath(envVarPath, vmStructMap);

        Object value;

        try {
            value = jsonParsed.read(expandParamsInPath);
            if (value instanceof Collection) {
                value = ((List<T>) value).get(0);
            }
            if (Long.class.isAssignableFrom(type)) {
                value = ((Number) value).longValue();
            } else if (Integer.class.isAssignableFrom(type)) {
                value = ((Number) value).intValue();
            } 
        } catch (Exception e) {
            value = null;
            analysisIssuesHandler.record(vmStructMap.get("_analysisId").toString(), "VM", vmStructMap.get("name").toString(), expandParamsInPath, e.getMessage());
        }
        return (T) value;
    }

    protected <T> T readValueFromExpandedEnvVarPath(String envVarPath, Map vmStructMap) {
        return readValueFromExpandedEnvVarPath(envVarPath, vmStructMap, Object.class);
    }

    protected <T> List<T> readListValuesFromExpandedEnvVarPath(String envVarPath, Map vmStructMap) {
        String pathWithExpandedParams = getExpandedPath(envVarPath, vmStructMap);

        try {
            Object value = jsonParsed.read(pathWithExpandedParams);
            if (value instanceof Collection) {
                return new ArrayList<>((List<T>) value);
            } else {
                return Collections.singletonList((T) value);
            }
        } catch (Exception e) {
            analysisIssuesHandler.record(vmStructMap.get("_analysisId").toString(), "VM", vmStructMap.get("name").toString(), pathWithExpandedParams, e.getMessage());
            return Collections.emptyList();
        }
    }

    protected String getExpandedPath(String envVarPath, Map vmStructMap) {
        String path = manifestVersionService.getPropertyWithFallbackVersion(manifestVersion, envVarPath);
        return expandParamsInPath(path, vmStructMap);
    }

    protected String expandParamsInPath(String path, Map vmStructMap) {
        Pattern p = Pattern.compile("\\{[a-zA-Z1-9_]+\\}");
        Matcher m = p.matcher(path);
        while (m.find() && vmStructMap != null) {
            String key = m.group().substring(1, m.group().length() - 1);
            String value = (vmStructMap.containsKey(key) && vmStructMap.get(key) != null) ? vmStructMap.get(key).toString() : "";
            path = path.replace(m.group(), value);
        }

        return path;
    }
}
