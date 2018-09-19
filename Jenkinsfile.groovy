import hudson.tasks.test.AbstractTestResultAction
import java.text.SimpleDateFormat

properties([
    // Only keep the last few build data in Jenkins
    buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10')),
    // Dropdown style parameters for which test configuration to use
    parameters([
        choice(choices: ['na_cr', 'test', 'stage'], description: '', name: 'environment'),
        choice(choices: ['headless', 'chrome', 'edge', 'ie'], description: '', name: 'browser'),
        choice(choices: ['bvt', 'functional', 'smoke', 'simulator'], description: '', name: 'group'),
		choice(choices: ['2', '1', '0'], description: '', name: 'retry')])
])

// For web tests, we use the node tag 'webautomation'
// iOS & Android tests will use 'ios-connected'
// Note: temporarily set to windows until Mac issues are resolved
node('win_node1') {
    cleanWs()
    def mvnHome
    stage('Preparation') {
        // Get some code from a git repository
        git branch: 'ruf', url: 'https://github.com/deepakarorawins/DeeAutomationPOC.git'

        // Get the Maven tool.
        // ** NOTE: This 'M3' Maven tool is configured
        // **       in the global configuration.
        mvnHome = tool 'M3'
    }
    stage('Build') {
        // Run the maven build
        // Note the sonar properties are required and will be unique for each type of tests
        // e.g. onekey-automation-web, onekey-automation-ios, onekey-automation-android
        if (isUnix()) {
            sh "'${mvnHome}/bin/mvn' clean test "
        } else {
            bat(/"${mvnHome}\bin\mvn" clean test -Dsurefire.suiteXmlFiles=.\/testng.xml/)
        }
    }
/*    stage('Archive') {
        def version = "${BUILD_NUMBER}-${new SimpleDateFormat('yyyy.MM.dd__HH.mm.ss').format(new Date())}"
		def nexusUrl = "https://build.milwaukeetool.com/nexus/repository/onekey-test-automation/reports/android/${version}"
		
		
		// Upload the files to Nexus for download
		httpRequest httpMode: 'PUT',
			url: "${nexusUrl}/extentReport.html",
			authentication: 'Nexus',
			requestBody: readFile('extentReport.html')
		httpRequest httpMode: 'PUT',
			url: "${nexusUrl}/logfile.log",
			authentication: 'Nexus',
			requestBody: readFile('logfile.log')
		httpRequest httpMode: 'PUT',
			url: "${nexusUrl}/logFinalStatus.log",
			authentication: 'Nexus',
			requestBody: readFile('logFinalStatus.log')

		//upload each of the screenshots
	def screenshots = findFiles(glob: 'screenshots/*')
		for(image in screenshots) {
			withCredentials([usernameColonPassword(credentialsId: 'Nexus', variable: 'nexusup')]) {
				//httpRequest isn't working for non-text files so just using curl for now even though it won't work on Windows
				sh "curl -v -u ${nexusup} --upload-file screenshots/${image.name} ${nexusUrl}/screenshots/${image.name}"
			}
		}

		def emailBody, emailSubject, attachLog = false
		def artifactUrl =  "https://build.milwaukeetool.com/nexus/service/rest/repository/browse/onekey-test-automation/reports/android/${version}/".toString()
		switch(currentBuild.result){
			case "UNSTABLE":
				emailBody = "Attached are the test results.\n ${testStatuses()}\n View test results here ${env.BUILD_URL}/testReport/\n View report from here \n${artifactUrl}"
				emailSubject = "Android Automation job ${env.JOB_NAME} marked as unstable"
				break
			case "SUCCESS":
				emailBody = "Attached are the test results.\n ${testStatuses()}\n View report from here \n${artifactUrl}\nLink to job ${env.BUILD_URL}"
				emailSubject = "Android Automation job ${env.JOB_NAME} completed successfully"
				break
			case "FAILURE":
				emailBody = "Attached are the test results.\n ${testStatuses()}\n Link to job ${env.BUILD_URL}"
				emailSubject = "Android Automation job ${env.JOB_NAME} marked as failed"
				attachLog = true
				break
			default:
				break
		}

            // Send an email based on the status of the build
            emailext attachLog: attachLog,
                body: emailBody,
                compressLog: true,
                replyTo: 'noreply@milwaukeetool.com',
                subject: emailSubject,
                to: 'OKAUTO-Report@milwaukeetool.com'*/
        }
    


/**
 * Get tests results as a string for use in the email
 * @return
 * Test Status:
 *   Passed: N, Failed: N  / �N, Skipped: N
 */
@NonCPS
def testStatuses() {
    def testStatus = ""
    AbstractTestResultAction testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
    if (testResultAction != null) {
        def total = testResultAction.totalCount
        def failed = testResultAction.failCount
        def skipped = testResultAction.skipCount
        def passed = total - failed - skipped
        testStatus = "Test Status:\n  Passed: ${passed}, Failed: ${failed} ${testResultAction.failureDiffString}, Skipped: ${skipped}"

        if (failed == 0) {
            currentBuild.result = 'SUCCESS'
        }
    }
    return testStatus
}
