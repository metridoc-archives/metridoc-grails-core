package metridoc.admin

import org.apache.commons.lang.math.RandomUtils

class AuthService {

    def dateById = [:]
    public static final FIFTEEN_MINUTES = 1000 * 60 * 15
    def resetableUserById = [:]

    def canReset(id) {
        canReset(id, new Date().getTime())
    }

    def addUserById(id, user) {
        resetableUserById[id] = user
    }

    def getUserById(id) {
        resetableUserById.remove(id)
    }

    private canReset(id, now) {
        def date = dateById.remove(id)

        if (date) {
            def validTime = now < (date.time + FIFTEEN_MINUTES)
            def dateNotNull = date != null
            def canReset = dateNotNull && validTime

            return canReset
        }

        return false
    }

    def addResetLink() {
        def id = RandomUtils.nextInt()
        dateById[id] = new Date()

        return id
    }
}
