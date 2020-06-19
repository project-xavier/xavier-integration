/*
* Requires: https://github.com/RedHatInsights/insights-pipeline-lib
*/

@Library("github.com/RedHatInsights/insights-pipeline-lib@v3") _

if (env.CHANGE_ID) {
    execSmokeTest(
        // the service-set/component for this app in e2e-deploy "buildfactory"
        ocDeployerBuilderPath: "xavier/xavier-integration",
    
        // the service-set/component for this app in e2e-deploy "templates"
        ocDeployerComponentPath: "xavier/xavier-integration",
    
        // the service sets to deploy into the test environment
        ocDeployerServiceSets: "platform-mq,rbac,ingress,xavier",
    
        // the iqe plugins to install for the test
        iqePlugins: ["iqe-migration_analytics-plugin"],
    
        // the pytest marker to use when calling `iqe tests all`
        pytestMarker: "ma_smoke",
    
        // optional id for an IQE configuration file stored as a secret in Jenkins
        //configFileCredentialsId: "myId",
    )
}
