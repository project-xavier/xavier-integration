package org.jboss.xavier.integrations;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringBootEndToEndTestContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	@Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        try {
            TestContainersInfrastructure containersInfrastructure = new TestContainersInfrastructure();
            containersInfrastructure.createAndStartDockerContainers();
            Thread.sleep(10000);

            EnvironmentTestUtils.addEnvironment("test", configurableApplicationContext.getEnvironment(),
                    "amq.server=" + containersInfrastructure.getActivemq().getContainerIpAddress(),
                    "amq.port=" + containersInfrastructure.getActivemq().getMappedPort(61616),
                    "minio.host=" + containersInfrastructure.getContainerHost(containersInfrastructure.getMinio(), 9000),
                    "insights.upload.host=" + containersInfrastructure.getContainerHost(containersInfrastructure.getIngress()),
                    "insights.properties=yearOverYearGrowthRatePercentage,percentageOfHypervisorsMigratedOnYear1,percentageOfHypervisorsMigratedOnYear2,percentageOfHypervisorsMigratedOnYear3,reportName,reportDescription",
//                    "camel.component.servlet.mapping.context-path=/api/xavier/*",
                    "insights.kafka.host=" + containersInfrastructure.getKafka().getBootstrapServers(),
                    "postgresql.service.name=" + containersInfrastructure.getPostgreSQL().getContainerIpAddress(),
                    "postgresql.service.port=" + containersInfrastructure.getPostgreSQL().getFirstMappedPort(),
                    "spring.datasource.username=" + containersInfrastructure.getPostgreSQL().getUsername(),
                    "spring.datasource.password=" + containersInfrastructure.getPostgreSQL().getPassword(),
                    "S3_HOST=" + containersInfrastructure.getLocalstack().getEndpointConfiguration(S3).getServiceEndpoint(),
                    "S3_REGION="+ containersInfrastructure.getLocalstack().getEndpointConfiguration(S3).getSigningRegion(),
                    "kieserver.devel-service=" + containersInfrastructure.getHostForKie() + "/kie-server",
                    "spring.datasource.url = jdbc:postgresql://" + containersInfrastructure.getContainerHost(containersInfrastructure.getPostgreSQL()) + "/sampledb" ,
                    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL9Dialect" ,
                    "thread.concurrentConsumers=3",
                    "insights.rbac.path=/api/v1/access/",
                    "insights.rbac.host=" + "http://" + containersInfrastructure.getContainerHost(containersInfrastructure.getRbacServer(), 8000));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}