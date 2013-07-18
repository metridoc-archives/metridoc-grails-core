import org.apache.commons.lang.RandomStringUtils

class MetridocCoreBootStrap {

    def initAuthService
    def homeService

    def init = { servletContext ->
        initAuthService.init()
        homeService.bootStrapApplications()

        File testDir = new File(System.getProperty("user.home") + "/.metridoc")
        File key = new File(System.getProperty("user.home") + "/.metridoc/mkey")
        if (!testDir.exists()) {
            testDir.mkdirs();
        }
        if (!key.exists()) {
            log.info "Generating mkey"
            new File(System.getProperty("user.home") + "/.metridoc/mkey").withWriter { out ->
                out << RandomStringUtils.randomAlphanumeric(64)
            }
        }
    }

    def destroy = {
    }
}