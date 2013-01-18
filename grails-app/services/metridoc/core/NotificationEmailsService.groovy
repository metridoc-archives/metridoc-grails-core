package metridoc.core

class NotificationEmailsService {
    def mailService
    def grailsApplication

    static boolean emailDisabledFromSystemProperty() {
        def emailDisabled = System.getProperty("metridoc.email.disabled", "false")
        Boolean.valueOf(emailDisabled)
    }

    boolean mailIsEnabled() {
        grailsApplication.config.grails.mail
    }

    void sendEmail(String scope, String message, String subject) {
        if (!emailDisabledFromSystemProperty()) {
            def emails = NotificationEmails.getEmailsByScope(scope)
            if (emails) {
                doSendEmail(emasils as String[], message, subject)
            } else {
                log.warn "Could not send the email with subject [$subject] since there were no emails"
            }
        } else {
            log.info "email notification with subject [$subject] not sent since emails are disabled"
        }
    }

    private doSendEmail(String[] recipients, String message, String subject) {
        if (mailIsEnabled()) {
            mailService.sendMail {
                to recipients
                subject subject
                body message
            }
        } else {
            log.warn("Could not send the email with subject [$subject] since mail is not set up correctly")
        }
    }

}
