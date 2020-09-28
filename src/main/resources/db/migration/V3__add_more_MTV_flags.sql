INSERT INTO flag_assessment_model
(flag, os_name, assessment, flag_label) VALUES ('VM/Host Affinity Configured', '', 'Affinity rules have been detected that will not translate over to OpenShift Virtualization', 'VM affinity rule detected');

INSERT INTO flag_assessment_model
(flag, os_name, assessment, flag_label) VALUES ('Numa Node Affinity', '', 'NUMA node affinity detected which is not supported in OpenShift Virtualization', 'NUMA node affinity detected');

INSERT INTO flag_assessment_model
(flag, os_name, assessment, flag_label) VALUES ('UEFI Boot', '', 'UEFI boot configuration detected which is not supported in OpenShift Virtualization', 'UEFI boot configured');
