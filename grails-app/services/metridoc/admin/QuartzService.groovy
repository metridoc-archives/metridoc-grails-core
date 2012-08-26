package metridoc.admin

class QuartzService {

    static final MAX_LIMIT = 100
    static final MAX_MINIMUM = 10

    def listWorkflows(params) {

    }

    def getMax(params) {
        def max = params.max

        max = max ? max : MAX_MINIMUM
        max < MAX_LIMIT ? max : MAX_LIMIT
    }

    private static listOrderedWorkflows(params, workflows) {
        def result = []
        def order = params.order

        if(order) {
            def map = new TreeMap()
            workflows.each {
                map.put(it.name, it)
            }

            result.addAll map.values()

            if(order == "desc") {
                result = result.reverse()
            }
        } else {
            result = workflows
        }

        return result
    }
}
