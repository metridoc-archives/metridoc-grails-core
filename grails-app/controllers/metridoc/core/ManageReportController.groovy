package metridoc.core

class ManageReportController {

    def manageReportService

    static accessControl = {
        role(name:  "ROLE_ADMIN")
    }

    def index() {
        [controllerDetails: manageReportService.controllerDetails]
    }
}
