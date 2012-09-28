package metridoc.utils

import org.junit.Test
import com.google.visualization.datasource.datatable.DataTable
import com.google.visualization.datasource.datatable.ColumnDescription
import com.google.visualization.datasource.datatable.value.ValueType
import com.google.visualization.datasource.datatable.TableRow
import com.google.visualization.datasource.datatable.TableCell
import com.google.visualization.datasource.render.CsvRenderer
import com.ibm.icu.util.ULocale
import org.junit.Before

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/27/12
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
class CSVStreamerUtilsTest {

    def dataTable
    def streamingCsv

    @Before
    void buildDataTable() {
        dataTable = new DataTable()

        dataTable.addColumn(new ColumnDescription("foo", ValueType.NUMBER, "foo label"))
        dataTable.addColumn(new ColumnDescription("bar", ValueType.NUMBER, "bar label"))
        dataTable.addColumn(new ColumnDescription("baz", ValueType.TEXT, "baz label"))

        dataTable.addRows(
                [
                        row([cell(1), cell(2), cell("blam")]),
                        row([cell(2), cell(5), cell("blammmo")])
                ]
        )

        streamingCsv = CSVStreamerUtils.convertDataTable(dataTable, true)
    }

    @Test
    void "converting datatable to string gives the same answer from google's renderer if it is the first item"() {
        def googleCsv = CsvRenderer.renderDataTable(dataTable, ULocale.ENGLISH, null)
        assert googleCsv
        assert streamingCsv
        assert googleCsv == streamingCsv
        println streamingCsv
    }

    @Test
    void "if it is not the first batch then the labels are not returned"() {

    }


    def row(cells) {
        new TableRow(cells: cells)
    }

    def cell(value) {
        new TableCell(value)
    }
}
