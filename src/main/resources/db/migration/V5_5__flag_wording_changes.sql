UPDATE flag_assessment_model
SET flag = 'Passthrough device' , flag_label = 'Passthrough device detected.' , assessment= 'Passthrough devices are not supported by OpenShift Virtualization. The VM cannot be migrated unless the passthrough device is removed.'
WHERE flag = 'Passthrough Device';

UPDATE flag_assessment_model
SET flag = 'VM-Host affinity' , flag_label = 'VM-Host affinity detected.' , assessment= 'VM-Host affinity is not supported by OpenShift Virtualization. The VM can be migrated but it will not have this feature in the target environment.'
WHERE flag = 'VM/Host Affinity Configured';

UPDATE flag_assessment_model
SET flag = 'CPU/memory hotplug' , flag_label = 'CPU/memory hotplug.' , assessment = 'Review VM CPU or memory configuration after migration.'
WHERE flag = 'CPU/Memory hotplug';

UPDATE flag_assessment_model
SET flag = 'CPU affinity' , flag_label = 'CPU affinity detected.' , assessment= 'CPU affinity is not supported by OpenShift Virtualization. The VM can be migrated but it will not have this feature in the target environment.'
WHERE flag = 'CPU Affinity';

UPDATE flag_assessment_model
SET flag = 'NUMA node affinity' , flag_label = 'NUMA node affinity detected.' , assessment= 'NUMA node affinity is not supported by OpenShift Virtualization. The VM can be migrated but it will not have this feature in the target environment.'
WHERE flag = 'NUMA Node Affinity';

UPDATE flag_assessment_model
SET flag = 'UEFI secure boot' , flag_label = 'UEFI secure boot detected.' , assessment= 'UEFI secure boot is not supported by OpenShift Virtualization. The VM cannot be migrated unless UEFI secure boot is disabled.'
WHERE flag = 'UEFI Boot';

UPDATE flag_assessment_model
SET flag_label = 'VMware DRS detected.' , assessment = 'VMware Distributed Resource Scheduler is not supported by OpenShift Virtualization. The VM can be migrated but it will not have this feature in the target environment.'
WHERE flag = 'VMWare DRS';

UPDATE flag_assessment_model
SET flag = 'HA' , flag_label = 'VM running on HA host detected.' , assessment= 'HA is not supported by OpenShift Virtualization. The VM can be migrated but it will not have this feature in the target environment.'
WHERE flag = 'VM HA';

UPDATE flag_assessment_model
SET flag = 'Memory ballooning' , flag_label = 'Memory ballooning detected.' , assessment= 'Memory ballooning is not supported by OpenShift Virtualization. The VM can be migrated but it will not have this feature in the target environment.'
WHERE flag = 'Ballooned memory';

UPDATE flag_assessment_model
SET flag_label = 'Encrypted disk detected.' , assessment = 'Encrypted disks are not supported by OpenShift Virtualization.  The VM cannot be migrated unless the disk is unencrypted.'
WHERE flag = 'Encrypted disk';

UPDATE flag_assessment_model
SET flag = 'Opaque network' , flag_label = 'Opaque network detected.' , assessment= 'Opaque networks are not supported by OpenShift Virtualization and may require post migration VM reconfiguration. The VM can be migrated but the network must be configured after migration.'
WHERE flag = 'Opaque Network';

UPDATE flag_assessment_model
SET flag = 'USB controller' , flag_label = 'USB controller detected.' , assessment = 'USB controllers are not supported by OpenShift Virtualization. The VM can be migrated but the devices attached to the USB controller will not be migrated.'
WHERE flag = 'USB device';