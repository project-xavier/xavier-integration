package org.jboss.xavier.integrations;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.MountableFile;

public class TestContainersInfrastructure {
    private Logger logger = LoggerFactory.getLogger(TestContainersInfrastructure.class);

    private String ingressCommitHash = "3ea33a8d793c2154f7cfa12057ca005c5f6031fa"; // 2019-11-11
    private String insightsRbacCommitHash = "a55b610a1385f0f6d3188b08710ec6a5890a97f6"; // 2020-02-05

    private GenericContainer kie_server;

    private PostgreSQLContainer postgreSQL;

    private LocalStackContainer localstack;

    private GenericContainer activemq;

    private GenericContainer minio;

    private GenericContainer createbuckets;

    private KafkaContainer kafka;

    private GenericContainer ingress;

    private GenericContainer rbacPostgreSQL;

    private GenericContainer rbacServer;

    private GenericContainer<?> getRBACServerContainer(Network rbacNetwork) {
        return new GenericContainer<>(new ImageFromDockerfile()
                .withDockerfile(Paths.get("src/test/resources/insights-rbac/insightsRbac_Dockerfile")))
                        .withNetwork(rbacNetwork).withNetworkAliases("rbac").withExposedPorts(8000)
                        .withEnv("DATABASE_SERVICE_NAME", "POSTGRES_SQL").withEnv("DATABASE_ENGINE", "postgresql")
                        .withEnv("DATABASE_NAME", "rb_database").withEnv("DATABASE_USER", "rbac_username")
                        .withEnv("DATABASE_PASSWORD", "rbac_password").withEnv("POSTGRES_SQL_SERVICE_HOST", "rbac_db")
                        .withEnv("POSTGRES_SQL_SERVICE_PORT", "5432");
    }

    private GenericContainer getRBACPostgreSQLContainer(Network rbacNetwork) {
        return new PostgreSQLContainer().withDatabaseName("rb_database").withUsername("rbac_username")
                .withPassword("rbac_password").withNetwork(rbacNetwork).withNetworkAliases("rbac_db");
    }

    private GenericContainer getIngressContainer(Network network) {
        return new GenericContainer(new ImageFromDockerfile()
                .withDockerfile(Paths.get("src/test/resources/insights-ingress-go/Dockerfile"))).withExposedPorts(3000)
                        .withNetwork(network).withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("INGRESS-LOG"))
                        .withEnv("AWS_ACCESS_KEY_ID", "BQA2GEXO711FVBVXDWKM")
                        .withEnv("AWS_SECRET_ACCESS_KEY", "uvgz3LCwWM3e400cDkQIH/y1Y4xgU4iV91CwFSPC")
                        .withEnv("AWS_REGION", "us-east-1").withEnv("INGRESS_STAGEBUCKET", "insights-upload-perma")
                        .withEnv("INGRESS_REJECTBUCKET", "insights-upload-rejected")
                        .withEnv("INGRESS_INVENTORYURL", "http://inventory:8080/api/inventory/v1/hosts")
                        .withEnv("INGRESS_VALIDTOPICS", "xavier,testareno,advisortestareno,advisor")
                        .withEnv("OPENSHIFT_BUILD_COMMIT", "woopwoop").withEnv("INGRESS_MINIODEV", "true")
                        .withEnv("INGRESS_MINIOACCESSKEY", "BQA2GEXO711FVBVXDWKM")
                        .withEnv("INGRESS_MINIOSECRETKEY", "uvgz3LCwWM3e400cDkQIH/y1Y4xgU4iV91CwFSPC")
                        .withEnv("INGRESS_MINIOENDPOINT", "minio:9000").withEnv("INGRESS_KAFKABROKERS", "kafka:9092");
    }

    private KafkaContainer getKafkaContainer(Network network) {
        return new KafkaContainer().withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("KAFKA-LOG"))
                .withNetworkAliases("kafka").withNetwork(network);
    }

    private GenericContainer<?> getMinioCreateBucketsContainer(Network network, GenericContainer minio) {
        return new GenericContainer<>("minio/mc").dependsOn(minio).withNetwork(network)
                .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("MINIO-MC-LOG"))
                .withCopyFileToContainer(MountableFile.forClasspathResource("minio-bucket-creation-commands.sh"), "/")
                .withCreateContainerCmdModifier(createContainerCmd -> createContainerCmd.withEntrypoint("sh",
                        "/minio-bucket-creation-commands.sh", "minio:9000"));
    }

    private GenericContainer getMinioContainer(Network network) {
        return new GenericContainer<>("minio/minio").withCommand("server /data").withExposedPorts(9000)
                .withNetworkAliases("minio").withNetwork(network)
                .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("MINIO-LOG"))
                .withEnv("MINIO_ACCESS_KEY", "BQA2GEXO711FVBVXDWKM")
                .withEnv("MINIO_SECRET_KEY", "uvgz3LCwWM3e400cDkQIH/y1Y4xgU4iV91CwFSPC");
    }

    private LocalStackContainer getLocalstackContainer() {
        return new LocalStackContainer().withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("AWS-LOG"))
                .withServices(S3);
    }

    private PostgreSQLContainer getPostgreSQLContainer() {
        return new PostgreSQLContainer().withDatabaseName("sampledb").withUsername("admin").withPassword("redhat");
    }

    private GenericContainer<?> getKIEContainer() {
        return new GenericContainer<>("jboss/kie-server-showcase:7.18.0.Final").withNetworkAliases("kie-server")
                .withExposedPorts(8080).withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("KIE-LOG"))
                .withEnv("KIE_SERVER_ID", "analytics-kieserver").withEnv("KIE_ADMIN_USER", "kieserver")
                .withEnv("KIE_ADMIN_PWD", "kieserver1!").withEnv("KIE_SERVER_MODE", "DEVELOPMENT")
                .withEnv("KIE_MAVEN_REPO", "https://oss.sonatype.org/content/repositories/snapshots")
                .withEnv("KIE_REPOSITORY", "https://repository.jboss.org/nexus/content/groups/public-jboss")
                .withEnv("KIE_SERVER_CONTROLLER_PWD", "admin").withEnv("KIE_SERVER_CONTROLLER_USER", "admin")
                .withEnv("KIE_SERVER_LOCATION", "http://kie-server:8080/kie-server/services/rest/server")
                .withEnv("KIE_SERVER_PWD", "kieserver1!").withEnv("KIE_SERVER_USER", "kieserver");
    }

    private GenericContainer getActiveMQContainer() {
        return (GenericContainer) new GenericContainer<>("vromero/activemq-artemis").withExposedPorts(61616, 8161)
                .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("AMQ-LOG")).withEnv("DISABLE_SECURITY", "true")
                .withEnv("BROKER_CONFIG_GLOBAL_MAX_SIZE", "50000").withEnv("BROKER_CONFIG_MAX_SIZE_BYTES", "50000")
                .withEnv("BROKER_CONFIG_MAX_DISK_USAGE", "100");
    }

    @NotNull
    public String getContainerHost(GenericContainer container, Integer port) {
        return container.getContainerIpAddress() + ":" + container.getMappedPort(port);
    }

    @NotNull
    public String getContainerHost(GenericContainer container) {
        return container.getContainerIpAddress() + ":" + container.getFirstMappedPort();
    }

    public void createAndStartDockerContainers() throws IOException, InterruptedException {
        cloneIngressRepoAndUnzip();
        cloneInsightsRbacRepo_UnzipAndConfigure();

        activemq = getActiveMQContainer();
        activemq.start();

        kie_server = getKIEContainer();
        kie_server.start();

        postgreSQL = getPostgreSQLContainer();
        postgreSQL.start();

        localstack = getLocalstackContainer();
        localstack.start();

        Network network = Network.newNetwork();

        minio = getMinioContainer(network);
        minio.start();

        Thread.sleep(5000);
        createbuckets = getMinioCreateBucketsContainer(network, minio);
        createbuckets.start();

        kafka = getKafkaContainer(network);
        kafka.start();

        ingress = getIngressContainer(network);
        ingress.start();

        Network rbacNetwork = Network.newNetwork();
        rbacPostgreSQL = getRBACPostgreSQLContainer(rbacNetwork);
        rbacPostgreSQL.start();

        rbacServer = getRBACServerContainer(rbacNetwork);
        rbacServer.start();

        Thread.sleep(5000);
        importProjectIntoKIE();
    }

    private void cloneIngressRepoAndUnzip() throws IOException {
        // downloading, unzipping, renaming
        String ingressRepoZipURL = "https://github.com/RedHatInsights/insights-ingress-go/archive/" + ingressCommitHash
                + ".zip";
        File compressedFile = new File("src/test/resources/ingressRepo.zip");
        FileUtils.copyURLToFile(new URL(ingressRepoZipURL), compressedFile, 1000, 10000);
        unzipFile(compressedFile, "src/test/resources");

        // we rename the directory because we had issues with Docker and the long folder
        FileUtils.moveDirectory(new File("src/test/resources/insights-ingress-go-" + ingressCommitHash),
                new File("src/test/resources/insights-ingress-go"));
    }

    private void cloneInsightsRbacRepo_UnzipAndConfigure() throws IOException {
        // downloading, unzipping, renaming
        String insightsRbacRepoZipURL = "https://github.com/RedHatInsights/insights-rbac/archive/"
                + insightsRbacCommitHash + ".zip";
        File compressedFile = new File("src/test/resources/insightsRbacRepo.zip");
        FileUtils.copyURLToFile(new URL(insightsRbacRepoZipURL), compressedFile, 1000, 10000);
        unzipFile(compressedFile, "src/test/resources");

        // we rename the directory because we had issues with Docker and the long folder
        FileUtils.moveDirectory(new File("src/test/resources/insights-rbac-" + insightsRbacCommitHash),
                new File("src/test/resources/insights-rbac"));

        // Use custom Dockerfile
        FileUtils.copyFile(new File("src/test/resources/insightsRbac_Dockerfile"),
                new File("src/test/resources/insights-rbac/insightsRbac_Dockerfile"));

        // Configure default system roles for application=migration-analytics
        FileUtils.copyFile(new File("src/test/resources/insightsRbac_roleDefinitions.json"),
                new File("src/test/resources/insights-rbac/rbac/management/role/definitions/migration-analytics.json"));
    }

    private void unzipFile(File file, String outputDir) throws IOException {
        java.util.zip.ZipFile zipFile = new ZipFile(file);
        try {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    entryDestination.mkdirs();
                } else {
                    entryDestination.getParentFile().mkdirs();
                    InputStream in = zipFile.getInputStream(entry);
                    OutputStream out = new FileOutputStream(entryDestination);
                    IOUtils.copy(in, out);
                    in.close();
                    out.close();
                }
            }
        } finally {
            zipFile.close();
        }
    }

    public String getHostForKie() {
        return kie_server.getContainerIpAddress() + ":" + kie_server.getFirstMappedPort();
    }

    private void importProjectIntoKIE() throws InterruptedException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl("no-cache");
        headers.set("Authorization", "Basic YWRtaW46YWRtaW4="); // admin:admin

        String kieRestURL = "http://" + getHostForKie() + "/kie-server/services/rest/";

        // KIE Container Creation
        HttpHeaders kieheaders = new HttpHeaders();
        kieheaders.setContentType(MediaType.APPLICATION_JSON);
        kieheaders.set("Authorization", "Basic a2llc2VydmVyOmtpZXNlcnZlcjEh");
        kieheaders.setCacheControl("no-cache");
        String kieContainerBody = "{\"container-id\" : \"xavier-analytics_0.0.1-SNAPSHOT\",\"release-id\" : {\"group-id\" : \"org.jboss.xavier\",\"artifact-id\" : \"xavier-analytics\",\"version\" : \"0.0.1-SNAPSHOT\" } }";
        try {
            new RestTemplate().exchange(kieRestURL + "server/containers/xavier-analytics_0.0.1-SNAPSHOT",
                    HttpMethod.PUT, new HttpEntity<>(kieContainerBody, kieheaders), String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
    }

    public GenericContainer getKie_server() {
        return kie_server;
    }

    public PostgreSQLContainer getPostgreSQL() {
        return postgreSQL;
    }

    public LocalStackContainer getLocalstack() {
        return localstack;
    }

    public GenericContainer getActivemq() {
        return activemq;
    }

    public GenericContainer getMinio() {
        return minio;
    }

    public GenericContainer getCreatebuckets() {
        return createbuckets;
    }

    public KafkaContainer getKafka() {
        return kafka;
    }

    public GenericContainer getIngress() {
        return ingress;
    }

    public GenericContainer getRbacPostgreSQL() {
        return rbacPostgreSQL;
    }

    public GenericContainer getRbacServer() {
        return rbacServer;
    }

}