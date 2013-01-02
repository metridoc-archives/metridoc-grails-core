package metridoc.core

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
        email email: true, blank: false, unique: ['scope']
        scope blank: false
    }

    static List<NotificationEmails> convertToEmails(String scope, String emails) {
        def emailList = convertEmailsToList(emails)
        def result = []
        emailList.each {
            result << new NotificationEmails(email: it, scope: scope)
        }

        return result
    }

    /**
     *
     * @param scope scope of the notification
     * @param emails emails separated by whitespace to store
     * @return all failed emails
     */
    static String[] storeEmails(String scope, String emails) {
        def failures = []
        NotificationEmails.withNewTransaction {
            convertToEmails(scope, emails).each {
                def alreadyStored = NotificationEmails.findByScopeAndEmail(scope, it.email)
                if (!alreadyStored) {
                    if (!it.save()) {
                        failures << it.email
                    }
                }
            }

            def emailList = convertEmailsToList(emails) as Set
            NotificationEmails.findAllByScope(scope).each {
                if (!emailList.contains(it.email)) {
                    NotificationEmails.get(it.id).delete()
                }
            }
        }

        return failures as String[]
    }

    static List<String> getEmailsByScope(String scope) {
        List<String> result = []
        def notificationEmails = NotificationEmails.findAllByScope(scope)
        if (notificationEmails) {
            notificationEmails.each {
                result << it.email
            }
        }
        return result
    }

    private static String[] convertEmailsToList(String emails) {
        if (emails) {
            def trimmed = emails.trim()
            if (trimmed) {
                return trimmed.split(/\s+/)
            }
        }

        return [] as String[]
    }

    private static String convertListToString(List<NotificationEmails> emails) {
        def result = new StrBuilder()
        emails.each {
            result.appendln(it.email)
        }

        return result.toString()
    }
}
