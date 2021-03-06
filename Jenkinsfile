import hudson.tasks.test.AbstractTestResultAction
import hudson.model.Actionable

properties([
        [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', daysToKeepStr: '2', numToKeepStr: '2']]
])
def label = "worker-${UUID.randomUUID().toString()}"
def deployingBranches = [
        "master"
]
def dockerBranches = [
        "master"
]

def relevantBranch(thisBranch, branches) {
    for (br in branches) {
        if (thisBranch == br) {
            return true;
        }
    }
    return false;
}

@NonCPS
def getTestSummary = { ->
    def testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
    def summary = ""
    if (testResultAction != null) {
        def total = testResultAction.getTotalCount()
        def failed = testResultAction.getFailCount()
        def skipped = testResultAction.getSkipCount()
        summary = "Test results: Passed: " + (total - failed - skipped) + (", Failed: " + failed) + (", Skipped: " + skipped)
    } else {
        summary = "No tests found"
    }
    return summary
}

podTemplate(label: label,
        containers: [
                containerTemplate(name: 'maven', image: 'eu.gcr.io/axoniq-devops/maven-axoniq:11',
                        command: 'cat', ttyEnabled: true,
                        resourceRequestCpu: '1000m', resourceLimitCpu: '1000m',
                        resourceRequestMemory: '3200Mi', resourceLimitMemory: '4Gi',
                        envVars: [
                                envVar(key: 'MAVEN_OPTS', value: '-Xmx3200m -Djavax.net.ssl.trustStore=/docker-java-home/lib/security/cacerts -Djavax.net.ssl.trustStorePassword=changeit'),
                                envVar(key: 'MVN_BLD', value: '-B -s /maven_settings/settings.xml')
                        ]),
                containerTemplate(name: 'gcloud', image: 'eu.gcr.io/axoniq-devops/gcloud-axoniq:latest', alwaysPullImage: true,
                        command: 'cat', ttyEnabled: true)
        ],
        volumes: [
                hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
                secretVolume(secretName: 'cacerts', mountPath: '/docker-java-home/lib/security'),
                secretVolume(secretName: 'dockercfg', mountPath: '/dockercfg'),
                secretVolume(secretName: 'jenkins-nexus', mountPath: '/nexus_settings'),
                secretVolume(secretName: 'test-settings', mountPath: '/axoniq'),
                secretVolume(secretName: 'maven-settings', mountPath: '/maven_settings')
        ]) {
    node(label) {
        def myRepo = checkout scm
        def gitCommit = myRepo.GIT_COMMIT
        def gitBranch = myRepo.GIT_BRANCH
        def shortGitCommit = "${gitCommit[0..10]}"

        def tag = sh(returnStdout: true, script: "git tag --contains | head -1").trim()
        def onTag = sh(returnStdout: true, script: "git tag --points-at HEAD | head -1").trim()
        def releaseBuild = (!tag.isEmpty() && tag.equals(onTag))

        pom = readMavenPom file: 'pom.xml'
        def pomVersion = pom.version
        def runVersion = pom.version.replaceAll("\\.", "-").toLowerCase()
        def imgVersion = runVersion + "-" + new java.text.SimpleDateFormat("yyyyMMdd-HHmmss-S").format(new java.util.Date())

        stage ('Project setup') {
            container("maven") {
                sh """
                        cat /maven_settings/*xml >./settings.xml
                        axoniq-templater -s ./settings.xml -P docker -pom pom.xml -env AXONIQ -envDot -q -dump >jenkins-build.properties
                    """
            }
        }
        def props = readProperties file: 'jenkins-build.properties'
        def gcloudRegistry = props ['gcloud.registry']
        def gcloudProjectName = props ['gcloud.project.name']
        def imageName = gcloudRegistry + "/" + gcloudProjectName + "/" + pom.artifactId + ":" + imgVersion

        def slackReport = "Maven build for AxonIQ Hotel Demo ${pomVersion} (branch \"${gitBranch}\")."

        def mavenTarget = "clean verify"

        stage ('Maven build') {
            container("maven") {
                try {
                    sh """
                            /opt/axoniq/bin/docker-credential-gcr config --token-source=env,store
                            /opt/axoniq/bin/docker-credential-gcr configure-docker
                        """
                    if (relevantBranch(gitBranch, deployingBranches)) {                // Deploy artifacts to Nexus for some branches
                        mavenTarget = "clean deploy"
                    }
                    if (relevantBranch(gitBranch, dockerBranches)) {
                        mavenTarget = "-Pdocker " + mavenTarget
                    }
                    mavenTarget = "-Pcoverage " + mavenTarget
                    try {
                        sh "mvn \${MVN_BLD} -Dmaven.test.failure.ignore ${mavenTarget}"

                        if (relevantBranch(gitBranch, deployingBranches)) {                // Deploy artifacts to Nexus for some branches
                            slackReport = slackReport + "\nDeployed to dev-nexus"
                        }
                    }
                    catch (err) {
                        slackReport = slackReport + "\nMaven build FAILED!"
                        throw err
                    }
                    finally {
                        junit '**/target/surefire-reports/TEST-*.xml'
                        slackReport = slackReport + "\n" + getTestSummary()
                    }
                }
                finally {
                    junit '**/target/surefire-reports/TEST-*.xml'
                }
            }
        }

        def sonarOptions = gitBranch.equals("master") ? "" : "-Dsonar.branch.name=${gitBranch}"
        if (gitBranch.startsWith("PR-") && env.CHANGE_ID) {
            sonarOptions = "-Dsonar.pullrequest.branch=" + gitBranch + " -Dsonar.pullrequest.key=" + env.CHANGE_ID
        }
        stage ('Run SonarQube') {
            container("maven") {
                sh "mvn \${MVN_BLD} -DskipTests ${sonarOptions}  -Psonar sonar:sonar"
                slackReport = slackReport + "\nSources analyzed in SonarQube."
            }
        }

        slackSend(message: slackReport, channel: "#demo-app")
    }
}
