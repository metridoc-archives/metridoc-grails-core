package metridoc.core

class FlashFilters {

    def filters = {
        all(controller:'*', action:'*') {
            before = {
                flash.alerts = flash.alerts ?: [] as Set
                flash.warnings = flash.warnings ?: [] as Set
                flash.messages = flash.messages ?: [] as Set
                flash.infos = flash.infos ?: [] as Set

                return true
            }
        }
    }
}
