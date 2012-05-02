package testFiles.metridoc.scripts

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 4/28/12
 * Time: 12:20 PM
 * To change this template use File | Settings | File Templates.
 */

dropFooTable = {
    sql.execute("drop table foo if exists")
}

createFooTable = {
    sql.execute("drop table foo if exists")
    sql.execute("create table foo (bar int)")
}

insertIntoFoo = {int number ->
    sql.execute("insert into foo values (${number})")
}

selectCountInFoo = {
    sql.firstRow("select count(*) as total from foo").total
}