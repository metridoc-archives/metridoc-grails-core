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
package metridoc.workflows

import groovy.util.logging.Slf4j
import javax.sql.DataSource
import metridoc.plugins.sql.SqlPlus

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 2/22/12
 * Time: 3:13 PM
 */
@Slf4j
class SqlNormalizer {
    DataSource dataSource
    SqlPlus sqlPlus
    String sourceTable

    private List<Destination> _destinations = []

    SqlPlus getSqlPlus() {
        if (sqlPlus) {
            return sqlPlus
        }

        return new SqlPlus(dataSource)
    }

    void setDestinations(Map<String, Map<String, Object>> destinations) {
        destinations.each {String descriptor, Map<String, Object> value ->
            if (!value.containsKey("table")) {
                value.table = descriptor
            }
            _destinations.add(new Destination(value))
        }
    }

    void setDestination(Map<String, Object> destination) {
        _destinations.add(new Destination(destination))
    }

    def run() {
        sqlPlus = getSqlPlus()

        _destinations.each {Destination destination ->
            if (destination.sql) {
                log.info("normalizing to {} with sql {}", destination.table, destination.sql)
                sqlPlus.execute(destination.sql)
            } else {
                log.info("normalizing to {} from stage table {} with columns {}", destination.table, sourceTable, destination.columns)
                if (destination.columnMap) {
                    if (destination.key) {
                        sqlPlus.bulkInsertNoDup(sourceTable, destination.table, destination.key, destination.columnMap)
                    } else {
                        sqlPlus.bulkInsert(sourceTable, destination.table, destination.columnMap)
                    }
                } else {
                    if (destination.key) {
                        sqlPlus.bulkInsertNoDup(sourceTable, destination.table, destination.key, destination.columns)
                    } else {
                        sqlPlus.bulkInsert(sourceTable, destination.table, destination.columns)
                    }
                }
            }
        }
        return null;
    }
}

class Destination {
    List<String> columns
    Map<String, String> columnMap
    String key
    String table
    String sql

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Destination that = (Destination) o

        if (columns != that.columns) return false
        if (key != that.key) return false
        if (table != that.table) return false

        return true
    }

    int hashCode() {
        int result
        result = (columns != null ? columns.hashCode() : 0)
        result = 31 * result + (key != null ? key.hashCode() : 0)
        result = 31 * result + (table != null ? table.hashCode() : 0)
        return result
    }


}
