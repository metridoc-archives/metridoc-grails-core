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

import groovy.sql.Sql

/**
 * script used to update lending-specific demographic information
 *
 * User: pkeavney
 * Date: 3/15/12
 */

    static update() { demographics.metaClass.invokeConstructor() }

    Sql sql = Sql.newInstance( repository )

    String lenderCodeStmt = "select distinct lender_code from ill_lender_group"
    String locAbbrevStmt = "select id, abbrev from ill_location where length(trim(abbrev))>0"
    String locationStmt = "select id, upper(location) as loc from ill_location"

    Sql locSql = Sql.newInstance( repository )
    Sql codeSql = Sql.newInstance( repository )
    Sql addrSql = Sql.newInstance( repository )
    Sql demogSql = Sql.newInstance( repository )

    states = [:]
    locations = [:]

    //println "loading abbreviations..."
    locSql.eachRow( locAbbrevStmt ) { states[it.abbrev] = it.id }
    //println "loading locations..."
    locSql.eachRow( locationStmt ) { locations[it.loc] = it.id }

    // process each lender code
    print "\n updating demographics..."
    codeSql.eachRow( lenderCodeStmt ) {
        print '.'
        String code = it.lender_code
        String lenderAddrStmt = "select replace(address,';',' ') addr from ill_lender_info where address is not NULL and lender_code = '" + code + "'"
        // cycle through each address associated with lender code
        addrSql.eachRow( lenderAddrStmt ) {
             //println "isolating individual strings making up address..."
            // isolate individual strings making up address
            List address = it.addr.toString().toUpperCase().tokenize(' ')
             //println "attempting to identify matching locations..."
            // attempt to identify matching locations
            address.intersect(locations.keySet()).find { x ->
                // println "setting location " + code + " to " + x
                demogSql.execute( "update ill_lender_group set demographic = '"+locations.get(x)+"' where lender_code = '"+code+"'" )
            }
            address.intersect(states.keySet()).find { x ->
                // println "setting state " + code + " to " + x
                demogSql.execute( "update ill_lender_group set demographic = '"+states.get(x)+"' where lender_code = '"+code+"'" )
            }
        }
    }
    println "\n demographic update complete"
