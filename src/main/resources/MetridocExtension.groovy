/*
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import metridoc.console.RunJob
import metridoc.plugins.PropertyPlugin
import metridoc.plugins.RunnerPlugin
import metridoc.plugins.camel.CamelExtensionPlugin
import metridoc.plugins.camel.CamelPlugin
import metridoc.plugins.datasource.DataSourcePlugin
import metridoc.plugins.doi.DoiPlugin
import metridoc.plugins.impl.iterators.DelimitedLineIterator
import metridoc.plugins.impl.iterators.LineIterator
import metridoc.plugins.impl.iterators.SqlIterator
import metridoc.plugins.impl.iterators.XlsIterator
import metridoc.plugins.schema.SchemaPlugin
import metridoc.plugins.sql.IdPlugin
import metridoc.plugins.sql.UrlPlugin
import metridoc.sql.DefaultBulkSqlCalls
import metridoc.utils.ClassUtils
import metridoc.plugins.sushi.SushiPlugin
import metridoc.plugins.table.impl.CSVFileWrapper
import metridoc.plugins.table.impl.JSONFileWrapper
import metridoc.plugins.table.impl.JSONHttpWrapper
import metridoc.plugins.table.impl.MockDatasource
import metridoc.plugins.table.impl.MySQLWrapper
import metridoc.plugins.table.impl.SQLStatementDatasource
import metridoc.plugins.table.impl.XLSFileWrapper

plugins = [
    PropertyPlugin.class,
    RunnerPlugin.class,
    DataSourcePlugin.class,
    SchemaPlugin.class,
    DoiPlugin.class,
    IdPlugin.class,
    UrlPlugin.class,
    DefaultBulkSqlCalls.class,
]


try {
    ClassUtils.defaultClassLoader.loadClass("org.apache.camel.component.file.GenericFile")
    plugins.add(CamelPlugin)
    plugins.add(CamelExtensionPlugin)
    plugins.add(LineIterator)
    plugins.add(XlsIterator)
    plugins.add(SqlIterator)
    plugins.add(DelimitedLineIterator)

} catch (Exception ex) {
}

try {
    ClassUtils.defaultClassLoader.loadClass("org.apache.http.client.HttpClient")
    plugins.add(SushiPlugin)
} catch(Exception ex){}

try {
    ClassUtils.defaultClassLoader.loadClass("org.apache.poi.ss.usermodel.Workbook")
    ClassUtils.defaultClassLoader.loadClass("au.com.bytecode.opencsv.CSVParser")
    plugins.add CSVFileWrapper.class
    plugins.add JSONFileWrapper.class
    plugins.add JSONHttpWrapper.class
    plugins.add MockDatasource.class
    plugins.add MySQLWrapper.class
    plugins.add SQLStatementDatasource.class
    plugins.add XLSFileWrapper.class
} catch (Exception ex) {}

consoleMapping = [runJob: RunJob]

