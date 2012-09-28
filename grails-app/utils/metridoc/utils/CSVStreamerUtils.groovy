package metridoc.utils

import com.google.visualization.datasource.render.CsvRenderer

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/27/12
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
class CSVStreamerUtils {

    def static convertDataTable(dataTable, isFirstBatch) {
        return convertDataTable(dataTable, isFirstBatch, null, null)
    }

    def static convertDataTable(dataTable, isFirstBatch, uLocale, separator) {
        CsvRenderer.renderDataTable(dataTable, uLocale, separator)
    }
}
