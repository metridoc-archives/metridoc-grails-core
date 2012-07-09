/*
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package metridoc.jenkins

class JenkinsService {

    def grailsApplication
    /**
     * check if Jenkins exists in "userHome/.metridoc/jenkins/"
     * and if exists, check whether it is running
     * @return string describing the current state of jenkins
     */
    def checkJenkins() {
        /*check whether jenkins exists*/
        def userHome = System.getProperty("user.home")
        def jenkinsDirectory = "${userHome}/.metridoc/jenkins"
        new File(jenkinsDirectory).mkdirs()
        File file = new File("${jenkinsDirectory}/jenkins.war")

        if (!file.exists()) {
            return "noJenkins"
        } else {
            log.info("file exists")
            /*check whether jenkins is running*/
            try {
                def tries = 20
                def connected = false

                (1..tries).each {int theTry ->
                    try {
                        def connection = new URL("http://localhost:8090").openConnection()
                        connection.readTimeout = 5000
                        connection.connectTimeout = 5000
                        connection.connect()
                        connected = true//jenkins is running
                    } catch (Exception e) {//jenkins is not running
                        log.warn "Could not connect on try ${theTry}"
                    }
                    Thread.sleep 500
                }

                if (!connected) {//jenkins is running
                    return "jenkinsNotRun"
                } else {//enable "runJenkins" button
                    return "jenkinsRunning"
                }
            } catch (Exception e) {
                log.error "Could not start jenkins", e
                return "Could not start jenkins"
            }
        }
    }
    /**
     * download jenkins.war from given URL to "userHome/.metridoc/jenkins/"
     * @return  string of downloading info
     */
    def downloadJenkins() {

        def userHome = System.getProperty("user.home")
        def jenkinsDirectory = "${userHome}/.metridoc/jenkins"
        new File(jenkinsDirectory).mkdirs()
        File jenkinsWar = new File("${jenkinsDirectory}/jenkins.war")

        if (!jenkinsWar.exists()) {
            try {
                def out = new BufferedOutputStream(new FileOutputStream(jenkinsWar))
                out << new URL("http://mirrors.jenkins-ci.org/war/latest/jenkins.war").openStream()
                out.close()
                return "jenkinsNotRun"
            } catch (Exception e) {
                return  "oops, ${e.message}"
            }
        } else {
            return "file exists already"
        }
    }

    /**
     * Run jenkins on port # 8090
     * @return  string of jenkins' running info
     */
    def runJenkins() {
        def userHome = System.getProperty("user.home")
        def jenkinsWar = "${userHome}/.metridoc/jenkins/jenkins.war"

        try {
            def command = "java -jar ${jenkinsWar} --httpPort=8090"
            log.info "about to execute ${command}"
            command.execute()
            return "jenkinsRunning"
        } catch (Exception e) {
            log.error "Could not start jenkins", e
            return "Could not start jenkins"
        }
    }
}
