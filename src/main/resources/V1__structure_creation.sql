--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.10
-- Dumped by pg_dump version 9.6.10

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: analysis_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.analysis_model (
    id bigint NOT NULL,
    payload_name character varying(255),
    report_description character varying(255),
    report_name character varying(255),
    status character varying(255),
    inserted timestamp without time zone,
    last_update timestamp without time zone,
    owner character varying(255),
    payload_storage_id character varying(255),
    owner_account_number character varying(255)
);


ALTER TABLE public.analysis_model OWNER TO   ${user};

--
-- Name: analysis_model_sequence; Type: SEQUENCE; Schema: public; Owner: userEN2
--

CREATE SEQUENCE public.analysis_model_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.analysis_model_sequence OWNER TO   ${user};

--
-- Name: app_identifier_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.app_identifier_model (
    id bigint NOT NULL,
    group_name character varying(255),
    identifier character varying(255),
    name character varying(255),
    priority integer,
    version character varying(255)
);


ALTER TABLE public.app_identifier_model OWNER TO   ${user};

--
-- Name: complexity_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.complexity_model (
    easy integer,
    medium integer,
    unknown integer,
    report_id bigint NOT NULL,
    hard integer,
    unsupported integer
);


ALTER TABLE public.complexity_model OWNER TO   ${user};

--
-- Name: environment_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.environment_model (
    deal_indicator integer,
    growth_rate_percentage double precision,
    hypervisors integer,
    open_stack_indicator boolean,
    source_product_indicator integer,
    year1hypervisor integer,
    year2hypervisor integer,
    year3hypervisor integer,
    report_id bigint NOT NULL
);


ALTER TABLE public.environment_model OWNER TO   ${user};

--
-- Name: flag_assessment_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.flag_assessment_model (
    flag character varying(255) NOT NULL,
    os_name character varying(255) NOT NULL,
    assessment character varying(255),
    flag_label character varying(255)
);


ALTER TABLE public.flag_assessment_model OWNER TO   ${user};

--
-- Name: flag_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.flag_model (
    id bigint NOT NULL,
    clusters integer,
    flag character varying(255),
    os_name character varying(255),
    vms integer,
    report_id bigint
);


ALTER TABLE public.flag_model OWNER TO   ${user};

--
-- Name: flagmodel_sequence; Type: SEQUENCE; Schema: public; Owner: userEN2
--

CREATE SEQUENCE public.flagmodel_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.flagmodel_sequence OWNER TO   ${user};

--
-- Name: initial_savings_estimation_report_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.initial_savings_estimation_report_model (
    id bigint NOT NULL,
    creation_date timestamp without time zone,
    customer_id character varying(255),
    file_name character varying(255),
    analysis_id bigint
);


ALTER TABLE public.initial_savings_estimation_report_model OWNER TO   ${user};

--
-- Name: initialsavingsestimationreport_sequence; Type: SEQUENCE; Schema: public; Owner: userEN2
--

CREATE SEQUENCE public.initialsavingsestimationreport_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.initialsavingsestimationreport_sequence OWNER TO   ${user};

--
-- Name: inputdatamodel_id_seq; Type: SEQUENCE; Schema: public; Owner: userEN2
--

CREATE SEQUENCE public.inputdatamodel_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.inputdatamodel_id_seq OWNER TO   ${user};

--
-- Name: osinformation_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.osinformation_model (
    id bigint NOT NULL,
    os_family character varying(255),
    priority integer,
    total integer,
    version character varying(255),
    report_id bigint
);


ALTER TABLE public.osinformation_model OWNER TO   ${user};

--
-- Name: osinformationmodel_sequence; Type: SEQUENCE; Schema: public; Owner: userEN2
--

CREATE SEQUENCE public.osinformationmodel_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.osinformationmodel_sequence OWNER TO   ${user};

--
-- Name: recommended_targetsimsmodel; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.recommended_targetsimsmodel (
    id bigint NOT NULL,
    osp integer,
    rhel integer,
    rhv integer,
    total integer,
    report_id bigint,
    ocp integer,
    jbosseap integer,
    openjdk integer
);


ALTER TABLE public.recommended_targetsimsmodel OWNER TO   ${user};

--
-- Name: recommendedtargetsims_sequence; Type: SEQUENCE; Schema: public; Owner: userEN2
--

CREATE SEQUENCE public.recommendedtargetsims_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.recommendedtargetsims_sequence OWNER TO   ${user};

--
-- Name: report_data_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.report_data_model (
    id bigint NOT NULL,
    creation_date timestamp without time zone,
    customer_id character varying(255),
    file_name character varying(255),
    number_of_hosts integer,
    total_disk_space bigint,
    total_price integer
);


ALTER TABLE public.report_data_model OWNER TO   ${user};

--
-- Name: rhvadditional_container_capacity_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.rhvadditional_container_capacity_model (
    rhv_container_from double precision,
    rhv_container_high double precision,
    rhv_container_likely double precision,
    rhv_container_low double precision,
    rhv_container_to double precision,
    report_id bigint NOT NULL
);


ALTER TABLE public.rhvadditional_container_capacity_model OWNER TO   ${user};

--
-- Name: rhvorder_form_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.rhvorder_form_model (
    year1rhv_order double precision,
    year1rhv_order_consult double precision,
    year1rhv_order_consult_discount_value double precision,
    year1rhv_order_consult_list_value double precision,
    year1rhv_order_consult_total_value double precision,
    year1rhv_order_discount_value double precision,
    year1rhv_order_grand_total double precision,
    year1rhv_order_learning_subs double precision,
    year1rhv_order_learning_subs_discount_value double precision,
    year1rhv_order_learning_subs_list_value double precision,
    year1rhv_order_learning_subs_total_value double precision,
    year1rhv_order_list_value double precision,
    year1rhv_order_sku double precision,
    year1rhv_ordertande double precision,
    year1rhv_ordertandediscount_value double precision,
    year1rhv_ordertandetotal_value double precision,
    year1rhv_ordertandevalue double precision,
    year1rhv_order_total_value double precision,
    year2rhv_order double precision,
    year2rhv_order_discount_value double precision,
    year2rhv_order_grand_total double precision,
    year2rhv_order_list_value double precision,
    year2rhv_order_sku double precision,
    year2rhv_order_total_value double precision,
    year3rhv_order double precision,
    year3rhv_order_discount_value double precision,
    year3rhv_order_grand_total double precision,
    year3rhv_order_list_value double precision,
    year3rhv_order_sku double precision,
    year3rhv_order_total_value double precision,
    report_id bigint NOT NULL
);


ALTER TABLE public.rhvorder_form_model OWNER TO   ${user};

--
-- Name: rhvramp_up_costs_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.rhvramp_up_costs_model (
    rhv_switch_consult_value double precision,
    rhv_switch_learning_subs_value double precision,
    rhv_switchtandevalue double precision,
    year1rhv_comp_subs double precision,
    year1rhv_comp_subs_growth double precision,
    year1rhv_grand_total_growth_value double precision,
    year1rhv_paid_subs double precision,
    year1rhv_paid_subs_growth double precision,
    year1rhv_per_server_growth_value double precision,
    year1rhv_per_server_value double precision,
    year1rhv_servers double precision,
    year1rhv_servers_growth double precision,
    year1rhv_total_growth_value double precision,
    year1rhv_total_value double precision,
    year2rhv_comp_subs double precision,
    year2rhv_comp_subs_growth double precision,
    year2rhv_grand_total_growth_value double precision,
    year2rhv_paid_subs double precision,
    year2rhv_paid_subs_growth double precision,
    year2rhv_per_server_growth_value double precision,
    year2rhv_per_server_value double precision,
    year2rhv_servers double precision,
    year2rhv_servers_growth double precision,
    year2rhv_total_growth_value double precision,
    year2rhv_total_value double precision,
    year3rhv_comp_subs double precision,
    year3rhv_comp_subs_growth double precision,
    year3rhv_grand_total_growth_value double precision,
    year3rhv_paid_subs double precision,
    year3rhv_paid_subs_growth double precision,
    year3rhv_per_server_growth_value double precision,
    year3rhv_per_server_value double precision,
    year3rhv_servers double precision,
    year3rhv_servers_growth double precision,
    year3rhv_total_growth_value double precision,
    year3rhv_total_value double precision,
    report_id bigint NOT NULL
);


ALTER TABLE public.rhvramp_up_costs_model OWNER TO   ${user};

--
-- Name: rhvsavings_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.rhvsavings_model (
    rhv_save_from_value double precision,
    rhv_save_high_value double precision,
    rhv_save_likely_value double precision,
    rhv_save_low_value double precision,
    rhv_save_to_value double precision,
    report_id bigint NOT NULL
);


ALTER TABLE public.rhvsavings_model OWNER TO   ${user};

--
-- Name: rhvyear_by_year_costs_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.rhvyear_by_year_costs_model (
    year1rhv_budget_freed_high_value double precision,
    year1rhv_budget_freed_likely_value double precision,
    year1rhv_budget_freed_low_value double precision,
    year1rhv_grand_total_value double precision,
    year2rhv_budget_freed_high_value double precision,
    year2rhv_budget_freed_likely_value double precision,
    year2rhv_budget_freed_low_value double precision,
    year2rhv_grand_total_value double precision,
    year3rhv_budget_freed_high_value double precision,
    year3rhv_budget_freed_likely_value double precision,
    year3rhv_budget_freed_low_value double precision,
    year3rhv_grand_total_value double precision,
    report_id bigint NOT NULL
);


ALTER TABLE public.rhvyear_by_year_costs_model OWNER TO   ${user};

--
-- Name: scan_run_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.scan_run_model (
    id bigint NOT NULL,
    date timestamp without time zone,
    target character varying(255),
    type boolean,
    report_id bigint,
    smart_state_enabled boolean
);


ALTER TABLE public.scan_run_model OWNER TO   ${user};

--
-- Name: scanrunmodel_sequence; Type: SEQUENCE; Schema: public; Owner: userEN2
--

CREATE SEQUENCE public.scanrunmodel_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.scanrunmodel_sequence OWNER TO   ${user};

--
-- Name: source_costs_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.source_costs_model (
    source_license_value double precision,
    source_maintenance_value double precision,
    source_newelaindicator integer,
    source_new_high_value double precision,
    source_new_likely_value double precision,
    source_new_low_value double precision,
    source_renew_high_value double precision,
    source_renew_likely_value double precision,
    source_renew_low_value double precision,
    tot_source_maintenance_value double precision,
    tot_source_value double precision,
    total_source_value double precision,
    year1server integer,
    year1source_maintenance_value double precision,
    year1source_value double precision,
    year2server integer,
    year2source_maintenance_value double precision,
    year2source_value double precision,
    year3server integer,
    year3source_maintenance_value double precision,
    year3source_value double precision,
    report_id bigint NOT NULL
);


ALTER TABLE public.source_costs_model OWNER TO   ${user};

--
-- Name: source_ramp_down_costs_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.source_ramp_down_costs_model (
    year1servers_off_source integer,
    year1source_active_license integer,
    year1source_maintenance_per_server_value double precision,
    year1source_maintenance_total_value double precision,
    year1source_paid_maintenance integer,
    year2servers_off_source integer,
    year2source_active_license integer,
    year2source_maintenance_per_server_value double precision,
    year2source_maintenance_total_value double precision,
    year2source_paid_maintenance integer,
    year3servers_off_source integer,
    year3source_active_license integer,
    year3source_maintenance_per_server_value double precision,
    year3source_maintenance_total_value double precision,
    year3source_paid_maintenance integer,
    report_id bigint NOT NULL
);


ALTER TABLE public.source_ramp_down_costs_model OWNER TO   ${user};

--
-- Name: summary_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.summary_model (
    id bigint NOT NULL,
    clusters integer,
    provider character varying(255),
    sockets bigint,
    vms integer,
    report_id bigint,
    hosts integer,
    product character varying(255),
    version character varying(255)
);


ALTER TABLE public.summary_model OWNER TO   ${user};

--
-- Name: summarymodel_sequence; Type: SEQUENCE; Schema: public; Owner: userEN2
--

CREATE SEQUENCE public.summarymodel_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.summarymodel_sequence OWNER TO   ${user};

--
-- Name: workload_inventory_report_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.workload_inventory_report_model (
    id bigint NOT NULL,
    cluster character varying(255),
    complexity character varying(255),
    cpu_cores integer,
    creation_date timestamp without time zone,
    datacenter character varying(255),
    disk_space bigint,
    memory bigint,
    os_description character varying(255),
    os_name character varying(255),
    provider character varying(255),
    vm_name character varying(255),
    analysis_id bigint,
    host_name character varying(255),
    product character varying(255),
    version character varying(255),
    ssa_enabled boolean,
    insights_enabled boolean,
    os_family character varying(255)
);


ALTER TABLE public.workload_inventory_report_model OWNER TO   ${user};

--
-- Name: workload_inventory_report_model_flagsims; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.workload_inventory_report_model_flagsims (
    workload_inventory_report_model_id bigint NOT NULL,
    flagsims character varying(255)
);


ALTER TABLE public.workload_inventory_report_model_flagsims OWNER TO   ${user};

--
-- Name: workload_inventory_report_model_recommended_targetsims; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.workload_inventory_report_model_recommended_targetsims (
    workload_inventory_report_model_id bigint NOT NULL,
    recommended_targetsims character varying(255)
);


ALTER TABLE public.workload_inventory_report_model_recommended_targetsims OWNER TO   ${user};

--
-- Name: workload_inventory_report_model_workloads; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.workload_inventory_report_model_workloads (
    workload_inventory_report_model_id bigint NOT NULL,
    workloads character varying(255)
);


ALTER TABLE public.workload_inventory_report_model_workloads OWNER TO   ${user};

--
-- Name: workload_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.workload_model (
    id bigint NOT NULL,
    clusters integer,
    os_name character varying(255),
    vms integer,
    workload character varying(255),
    report_id bigint
);


ALTER TABLE public.workload_model OWNER TO   ${user};

--
-- Name: workload_summary_report_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.workload_summary_report_model (
    id bigint NOT NULL,
    analysis_id bigint
);


ALTER TABLE public.workload_summary_report_model OWNER TO   ${user};

--
-- Name: workloadinventoryreport_sequence; Type: SEQUENCE; Schema: public; Owner: userEN2
--

CREATE SEQUENCE public.workloadinventoryreport_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.workloadinventoryreport_sequence OWNER TO   ${user};

--
-- Name: workloadmodel_sequence; Type: SEQUENCE; Schema: public; Owner: userEN2
--

CREATE SEQUENCE public.workloadmodel_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.workloadmodel_sequence OWNER TO   ${user};

--
-- Name: workloads_application_platforms_detected_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.workloads_application_platforms_detected_model (
    id bigint NOT NULL,
    name character varying(255),
    total integer,
    version character varying(255),
    report_id bigint,
    priority integer
);


ALTER TABLE public.workloads_application_platforms_detected_model OWNER TO   ${user};

--
-- Name: workloads_detectedostype_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.workloads_detectedostype_model (
    id bigint NOT NULL,
    os_name character varying(255),
    total integer,
    report_id bigint
);


ALTER TABLE public.workloads_detectedostype_model OWNER TO   ${user};

--
-- Name: workloads_java_runtime_detected_model; Type: TABLE; Schema: public; Owner: userEN2
--

CREATE TABLE public.workloads_java_runtime_detected_model (
    id bigint NOT NULL,
    total integer,
    vendor character varying(255),
    version character varying(255),
    report_id bigint,
    priority integer
);


ALTER TABLE public.workloads_java_runtime_detected_model OWNER TO   ${user};

--
-- Name: workloadsapplicationplatformsdetectedmodel_sequence; Type: SEQUENCE; Schema: public; Owner: userEN2
--

CREATE SEQUENCE public.workloadsapplicationplatformsdetectedmodel_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.workloadsapplicationplatformsdetectedmodel_sequence OWNER TO   ${user};

--
-- Name: workloadsdetectedostypemodel_sequence; Type: SEQUENCE; Schema: public; Owner: userEN2
--

CREATE SEQUENCE public.workloadsdetectedostypemodel_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.workloadsdetectedostypemodel_sequence OWNER TO   ${user};

--
-- Name: workloadsjavaruntimedetectedmodel_sequence; Type: SEQUENCE; Schema: public; Owner: userEN2
--

CREATE SEQUENCE public.workloadsjavaruntimedetectedmodel_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.workloadsjavaruntimedetectedmodel_sequence OWNER TO   ${user};

--
-- Name: workloadsummaryreport_sequence; Type: SEQUENCE; Schema: public; Owner: userEN2
--

CREATE SEQUENCE public.workloadsummaryreport_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.workloadsummaryreport_sequence OWNER TO   ${user};

--
-- Name: worloadinventoryreport_sequence; Type: SEQUENCE; Schema: public; Owner: userEN2
--

CREATE SEQUENCE public.worloadinventoryreport_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.worloadinventoryreport_sequence OWNER TO   ${user};

COPY public.flag_assessment_model (flag, os_name, assessment, flag_label) FROM stdin;
RDM		Raw devices may require manual data migration	RDM - Raw Device Mapping Disks
>4 vNICs		More than 4 NICs in one VM, review carefully network configuration	>4 vNICs - More than 4 network interfaces in one VM
Shared Disk		Shared disks may require manual steps to perform the migration	Shared Disk - Disks being simultaneously accessed by more than one VM
CPU Affinity		Once migrated the VM will require a CPU configuration review	CPU affinity assigns specific Host CPU configuration to the VM
CPU/Memory hotplug		Once migrated the VM will require a CPU/Memory configuration review	CPU/Memory hotplug makes possible change, without powering it off, the amount of memory and cores assigned to the VM
\.


--
-- Name: analysis_model analysis_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.analysis_model
    ADD CONSTRAINT analysis_model_pkey PRIMARY KEY (id);


--
-- Name: app_identifier_model app_identifier_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.app_identifier_model
    ADD CONSTRAINT app_identifier_model_pkey PRIMARY KEY (id);


--
-- Name: complexity_model complexity_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.complexity_model
    ADD CONSTRAINT complexity_model_pkey PRIMARY KEY (report_id);


--
-- Name: environment_model environment_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.environment_model
    ADD CONSTRAINT environment_model_pkey PRIMARY KEY (report_id);


--
-- Name: flag_assessment_model flag_assessment_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.flag_assessment_model
    ADD CONSTRAINT flag_assessment_model_pkey PRIMARY KEY (flag, os_name);


--
-- Name: flag_model flag_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.flag_model
    ADD CONSTRAINT flag_model_pkey PRIMARY KEY (id);


--
-- Name: initial_savings_estimation_report_model initial_savings_estimation_report_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.initial_savings_estimation_report_model
    ADD CONSTRAINT initial_savings_estimation_report_model_pkey PRIMARY KEY (id);


--
-- Name: osinformation_model osinformation_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.osinformation_model
    ADD CONSTRAINT osinformation_model_pkey PRIMARY KEY (id);


--
-- Name: recommended_targetsimsmodel recommended_targetsimsmodel_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.recommended_targetsimsmodel
    ADD CONSTRAINT recommended_targetsimsmodel_pkey PRIMARY KEY (id);


--
-- Name: report_data_model report_data_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.report_data_model
    ADD CONSTRAINT report_data_model_pkey PRIMARY KEY (id);


--
-- Name: rhvadditional_container_capacity_model rhvadditional_container_capacity_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.rhvadditional_container_capacity_model
    ADD CONSTRAINT rhvadditional_container_capacity_model_pkey PRIMARY KEY (report_id);


--
-- Name: rhvorder_form_model rhvorder_form_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.rhvorder_form_model
    ADD CONSTRAINT rhvorder_form_model_pkey PRIMARY KEY (report_id);


--
-- Name: rhvramp_up_costs_model rhvramp_up_costs_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.rhvramp_up_costs_model
    ADD CONSTRAINT rhvramp_up_costs_model_pkey PRIMARY KEY (report_id);


--
-- Name: rhvsavings_model rhvsavings_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.rhvsavings_model
    ADD CONSTRAINT rhvsavings_model_pkey PRIMARY KEY (report_id);


--
-- Name: rhvyear_by_year_costs_model rhvyear_by_year_costs_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.rhvyear_by_year_costs_model
    ADD CONSTRAINT rhvyear_by_year_costs_model_pkey PRIMARY KEY (report_id);


--
-- Name: scan_run_model scan_run_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.scan_run_model
    ADD CONSTRAINT scan_run_model_pkey PRIMARY KEY (id);


--
-- Name: source_costs_model source_costs_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.source_costs_model
    ADD CONSTRAINT source_costs_model_pkey PRIMARY KEY (report_id);


--
-- Name: source_ramp_down_costs_model source_ramp_down_costs_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.source_ramp_down_costs_model
    ADD CONSTRAINT source_ramp_down_costs_model_pkey PRIMARY KEY (report_id);


--
-- Name: summary_model summary_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.summary_model
    ADD CONSTRAINT summary_model_pkey PRIMARY KEY (id);


--
-- Name: workload_inventory_report_model workload_inventory_report_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.workload_inventory_report_model
    ADD CONSTRAINT workload_inventory_report_model_pkey PRIMARY KEY (id);


--
-- Name: workload_model workload_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.workload_model
    ADD CONSTRAINT workload_model_pkey PRIMARY KEY (id);


--
-- Name: workload_summary_report_model workload_summary_report_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.workload_summary_report_model
    ADD CONSTRAINT workload_summary_report_model_pkey PRIMARY KEY (id);


--
-- Name: workloads_application_platforms_detected_model workloads_application_platforms_detected_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.workloads_application_platforms_detected_model
    ADD CONSTRAINT workloads_application_platforms_detected_model_pkey PRIMARY KEY (id);


--
-- Name: workloads_detectedostype_model workloads_detectedostype_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.workloads_detectedostype_model
    ADD CONSTRAINT workloads_detectedostype_model_pkey PRIMARY KEY (id);


--
-- Name: workloads_java_runtime_detected_model workloads_java_runtime_detected_model_pkey; Type: CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.workloads_java_runtime_detected_model
    ADD CONSTRAINT workloads_java_runtime_detected_model_pkey PRIMARY KEY (id);


--
-- Name: flagmodel_flag_index; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX flagmodel_flag_index ON public.flag_model USING btree (flag);


--
-- Name: flagmodel_osname_index; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX flagmodel_osname_index ON public.flag_model USING btree (os_name);


--
-- Name: flagmodel_report_id_index; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX flagmodel_report_id_index ON public.flag_model USING btree (report_id);


--
-- Name: flagmodel_vms_index; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX flagmodel_vms_index ON public.flag_model USING btree (vms);


--
-- Name: idx987l691nougmpl5xsuqcul2rb; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX idx987l691nougmpl5xsuqcul2rb ON public.workload_inventory_report_model_workloads USING btree (workload_inventory_report_model_id);


--
-- Name: idxmww1xxwvx08usuo4q9cxwdbtx; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX idxmww1xxwvx08usuo4q9cxwdbtx ON public.workload_inventory_report_model_flagsims USING btree (workload_inventory_report_model_id);


--
-- Name: idxqdh1777qnt8tqwht3d0fuw3y7; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX idxqdh1777qnt8tqwht3d0fuw3y7 ON public.workload_inventory_report_model_recommended_targetsims USING btree (workload_inventory_report_model_id);


--
-- Name: initialsavingsestimationreportmodel_analysis_id_index; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX initialsavingsestimationreportmodel_analysis_id_index ON public.initial_savings_estimation_report_model USING btree (analysis_id);


--
-- Name: workloadinventoryreportmodel_analysis_id_index; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX workloadinventoryreportmodel_analysis_id_index ON public.workload_inventory_report_model USING btree (analysis_id);


--
-- Name: workloadinventoryreportmodel_complexity_index; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX workloadinventoryreportmodel_complexity_index ON public.workload_inventory_report_model USING btree (complexity);


--
-- Name: workloadinventoryreportmodel_os_name_index; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX workloadinventoryreportmodel_os_name_index ON public.workload_inventory_report_model USING btree (os_name);


--
-- Name: workloadinventoryreportmodel_vm_name_index; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX workloadinventoryreportmodel_vm_name_index ON public.workload_inventory_report_model USING btree (vm_name);


--
-- Name: workloadmodel_osname_index; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX workloadmodel_osname_index ON public.workload_model USING btree (os_name);


--
-- Name: workloadmodel_report_id_index; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX workloadmodel_report_id_index ON public.workload_model USING btree (report_id);


--
-- Name: workloadmodel_vms_index; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX workloadmodel_vms_index ON public.workload_model USING btree (vms);


--
-- Name: workloadmodel_workload_index; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX workloadmodel_workload_index ON public.workload_model USING btree (workload);


--
-- Name: workloadsummaryreportmodel_analysis_id_index; Type: INDEX; Schema: public; Owner: userEN2
--

CREATE INDEX workloadsummaryreportmodel_analysis_id_index ON public.workload_summary_report_model USING btree (analysis_id);


--
-- Name: summary_model fk100xe0vtc0reerqsxu7mjht6a; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.summary_model
    ADD CONSTRAINT fk100xe0vtc0reerqsxu7mjht6a FOREIGN KEY (report_id) REFERENCES public.workload_summary_report_model(id);


--
-- Name: osinformation_model fk145ohc6uwmttmb28ddmvv9dw2; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.osinformation_model
    ADD CONSTRAINT fk145ohc6uwmttmb28ddmvv9dw2 FOREIGN KEY (report_id) REFERENCES public.workload_summary_report_model(id);


--
-- Name: complexity_model fk2fby5psm3bnq0g616eb0fr8g6; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.complexity_model
    ADD CONSTRAINT fk2fby5psm3bnq0g616eb0fr8g6 FOREIGN KEY (report_id) REFERENCES public.workload_summary_report_model(id);


--
-- Name: workload_inventory_report_model_flagsims fk3s1ojk7bhugrpd6c5ashtbra3; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.workload_inventory_report_model_flagsims
    ADD CONSTRAINT fk3s1ojk7bhugrpd6c5ashtbra3 FOREIGN KEY (workload_inventory_report_model_id) REFERENCES public.workload_inventory_report_model(id);


--
-- Name: workloads_java_runtime_detected_model fk6c41mvv7o3nl6f6ory75lct9; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.workloads_java_runtime_detected_model
    ADD CONSTRAINT fk6c41mvv7o3nl6f6ory75lct9 FOREIGN KEY (report_id) REFERENCES public.workload_summary_report_model(id);


--
-- Name: source_ramp_down_costs_model fk73mwxxnl75ce3b3c40wadhhp8; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.source_ramp_down_costs_model
    ADD CONSTRAINT fk73mwxxnl75ce3b3c40wadhhp8 FOREIGN KEY (report_id) REFERENCES public.initial_savings_estimation_report_model(id);


--
-- Name: rhvyear_by_year_costs_model fk8fucuw91xd1j836rb62jmie0s; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.rhvyear_by_year_costs_model
    ADD CONSTRAINT fk8fucuw91xd1j836rb62jmie0s FOREIGN KEY (report_id) REFERENCES public.initial_savings_estimation_report_model(id);


--
-- Name: workload_summary_report_model fk9fwvli5o9uxtrkfe5l0by3u1i; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.workload_summary_report_model
    ADD CONSTRAINT fk9fwvli5o9uxtrkfe5l0by3u1i FOREIGN KEY (analysis_id) REFERENCES public.analysis_model(id);


--
-- Name: workload_inventory_report_model_recommended_targetsims fkd7iiycs5wo2wru58ua7fwvlm; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.workload_inventory_report_model_recommended_targetsims
    ADD CONSTRAINT fkd7iiycs5wo2wru58ua7fwvlm FOREIGN KEY (workload_inventory_report_model_id) REFERENCES public.workload_inventory_report_model(id);


--
-- Name: recommended_targetsimsmodel fkdecwm0kfb478k49gibj4j0c39; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.recommended_targetsimsmodel
    ADD CONSTRAINT fkdecwm0kfb478k49gibj4j0c39 FOREIGN KEY (report_id) REFERENCES public.workload_summary_report_model(id);


--
-- Name: rhvsavings_model fke80vd465lnjmbgoycdtn1i8ue; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.rhvsavings_model
    ADD CONSTRAINT fke80vd465lnjmbgoycdtn1i8ue FOREIGN KEY (report_id) REFERENCES public.initial_savings_estimation_report_model(id);


--
-- Name: scan_run_model fkel02hyknbp5geiavkqrcxaly4; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.scan_run_model
    ADD CONSTRAINT fkel02hyknbp5geiavkqrcxaly4 FOREIGN KEY (report_id) REFERENCES public.workload_summary_report_model(id);


--
-- Name: workload_inventory_report_model_workloads fki3y8y8ay7b3ytpn6c1tuvobmi; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.workload_inventory_report_model_workloads
    ADD CONSTRAINT fki3y8y8ay7b3ytpn6c1tuvobmi FOREIGN KEY (workload_inventory_report_model_id) REFERENCES public.workload_inventory_report_model(id);


--
-- Name: source_costs_model fkihdx6w6n0lo3a71rpmuhky575; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.source_costs_model
    ADD CONSTRAINT fkihdx6w6n0lo3a71rpmuhky575 FOREIGN KEY (report_id) REFERENCES public.initial_savings_estimation_report_model(id);


--
-- Name: rhvramp_up_costs_model fkoslmm7i38kv4icba1r6qyw8cm; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.rhvramp_up_costs_model
    ADD CONSTRAINT fkoslmm7i38kv4icba1r6qyw8cm FOREIGN KEY (report_id) REFERENCES public.initial_savings_estimation_report_model(id);


--
-- Name: rhvadditional_container_capacity_model fkotfqewoougykl9nlp3dwc28fi; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.rhvadditional_container_capacity_model
    ADD CONSTRAINT fkotfqewoougykl9nlp3dwc28fi FOREIGN KEY (report_id) REFERENCES public.initial_savings_estimation_report_model(id);


--
-- Name: workloads_detectedostype_model fkq4f0uanmmw7y6jiltinqeu4xs; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.workloads_detectedostype_model
    ADD CONSTRAINT fkq4f0uanmmw7y6jiltinqeu4xs FOREIGN KEY (report_id) REFERENCES public.workload_summary_report_model(id);


--
-- Name: rhvorder_form_model fkq8hwj05qae2ahry13xq806i2a; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.rhvorder_form_model
    ADD CONSTRAINT fkq8hwj05qae2ahry13xq806i2a FOREIGN KEY (report_id) REFERENCES public.initial_savings_estimation_report_model(id);


--
-- Name: workloads_application_platforms_detected_model fkqk1pmogvaa84dotwy1y1cwfsg; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.workloads_application_platforms_detected_model
    ADD CONSTRAINT fkqk1pmogvaa84dotwy1y1cwfsg FOREIGN KEY (report_id) REFERENCES public.workload_summary_report_model(id);


--
-- Name: flag_model fkqmwntwsvmvkey0x7i6sjohkhx; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.flag_model
    ADD CONSTRAINT fkqmwntwsvmvkey0x7i6sjohkhx FOREIGN KEY (report_id) REFERENCES public.workload_summary_report_model(id);


--
-- Name: initial_savings_estimation_report_model fkr5kbsi7lqpyh3au90jbid500c; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.initial_savings_estimation_report_model
    ADD CONSTRAINT fkr5kbsi7lqpyh3au90jbid500c FOREIGN KEY (analysis_id) REFERENCES public.analysis_model(id);


--
-- Name: workload_model fkrpayw4oxsp9k7iot8ea6v3484; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.workload_model
    ADD CONSTRAINT fkrpayw4oxsp9k7iot8ea6v3484 FOREIGN KEY (report_id) REFERENCES public.workload_summary_report_model(id);


--
-- Name: environment_model fkswxhtvnlogc2dswg4ltf1vqpo; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.environment_model
    ADD CONSTRAINT fkswxhtvnlogc2dswg4ltf1vqpo FOREIGN KEY (report_id) REFERENCES public.initial_savings_estimation_report_model(id);


--
-- Name: workload_inventory_report_model fkt966e5qfj0hwmb0oecel9qbyw; Type: FK CONSTRAINT; Schema: public; Owner: userEN2
--

ALTER TABLE ONLY public.workload_inventory_report_model
    ADD CONSTRAINT fkt966e5qfj0hwmb0oecel9qbyw FOREIGN KEY (analysis_id) REFERENCES public.analysis_model(id);


--
-- PostgreSQL database dump complete
--
