INSERT INTO flag_assessment_model
(flag, os_name, assessment, flag_label) VALUES ('SR-IOV Network Interface', '', 'OpenShift Virtualization does not support SR-IOV.', 'SR-IOV network interface detected') ON CONFLICT DO NOTHING;
