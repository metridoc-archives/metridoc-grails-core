package metridoc.core

import org.apache.commons.lang.SystemUtils
import org.apache.commons.lang.text.StrBuilder

/**
 * contains all emails related to various operations in metridoc such as job failures
 */
class NotificationEmails {
    /**
     * scope of the email... ie what kind of notification is this
     */
    String scope
    String email


    static constraints = {
        email email: true, blank:false, unique: ['scope']
        scope blank: false
    }

    def storeEmails(String emails) {

    }

    private static String[] convertEmailsToList(String emails) {
        emails.trim().split(/\s+/)
    }

    private static String convertListToString(List<NotificationEmails> emails) {
        def result = new StrBuilder()
        emails.each {
            result.appendln(it.email)
        }

        return result.toString()
    }
}
