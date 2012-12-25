package metridoc.core

class NotificationEmailsService {
    def mailService
    def grailsApplication

    boolean mailIsEnabled() {
        grailsApplication.config.grails.mail
    }

    void sendEmail(String scope, String message, String subject) {
        def emails = NotificationEmails.getEmailsByScope(scope)
        if (emails) {
            doSendEmail(emasils as String[], message, subject)
        } else {
            log.warn "Could not send the email with subject [$subject] since there were no emails"
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
