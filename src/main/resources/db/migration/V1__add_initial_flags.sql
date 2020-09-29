INSERT INTO flag_assessment_model 
(flag, os_name, assessment, flag_label) 
VALUES
('RDM','','Raw devices may require manual data migration','RDM - Raw Device Mapping Disks') ON CONFLICT DO NOTHING;

INSERT INTO flag_assessment_model
(flag, os_name, assessment, flag_label)
VALUES
('>4 vNICs','','More than 4 NICs in one VM, review carefully network configuration','>4 vNICs - More than 4 network interfaces in one VM') ON CONFLICT DO NOTHING;

INSERT INTO flag_assessment_model
(flag, os_name, assessment, flag_label)
VALUES
('Shared Disk','','Shared disks may require manual steps to perform the migration','Shared Disk - Disks being simultaneously accessed by more than one VM') ON CONFLICT DO NOTHING;

INSERT INTO flag_assessment_model
(flag, os_name, assessment, flag_label)
VALUES
('CPU Affinity','','Once migrated the VM will require a CPU configuration review','CPU affinity assigns specific Host CPU configuration to the VM') ON CONFLICT DO NOTHING;

INSERT INTO flag_assessment_model
(flag, os_name, assessment, flag_label)
VALUES
('CPU/Memory hotplug','','Once migrated the VM will require a CPU/Memory configuration review','CPU/Memory hotplug makes possible change, without powering it off, the amount of memory and cores assigned to the VM') ON CONFLICT DO NOTHING;

