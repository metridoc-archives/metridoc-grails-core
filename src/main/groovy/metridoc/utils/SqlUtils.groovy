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
package metridoc.utils

import java.sql.Types

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/2/11
 * Time: 12:42 PM
 * To change this template use File | Settings | File Templates.
 */
class SqlUtils {

    public static final String DATE_TEXT = "\\d{4}-\\d\\d?-\\d\\d?"
    public static final String DATE_TIME_TEXT = "${DATE_TEXT} \\d\\d?:\\d\\d?:\\d\\d?"

    private static final DATE = /${DATE_TEXT}/
    private static final DATE_TIME = /${DATE_TIME_TEXT}/
    private static Map<Integer, Integer> orderedTypes = new HashMap<Integer, Integer>()

    //numeric values are positive, the rest are negative
    static {
        orderedTypes.put(Types.VARCHAR, -30)
        orderedTypes.put(Types.TIMESTAMP, -20)
        orderedTypes.put(Types.DATE, -10)
        orderedTypes.put(Types.BOOLEAN, 10)
        orderedTypes.put(Types.DOUBLE, 20)
        orderedTypes.put(Types.BIGINT, 30)
        orderedTypes.put(Types.INTEGER, 40)
    }

    static int getSqlType(long data) {
        return Types.BIGINT
    }

    static int getSqlType(double data) {
        return Types.DOUBLE
    }

    static int getSqlType(Date data) {
        return Types.TIMESTAMP
    }

    /**
     *
     * @param data
     * @return {@link Types#DATE} if it is in the form of YYYY-MM-DD (month and day DO NOT need to be 2 digits),
     *  {@link Types#TIMESTAMP} if it is in the form of YYYY-MM-DD hh:mm:ss (all two digit numbers can be one digit
     *  as wel), or {@link Types#VARCHAR} if it is not a date
     */
    static int getSqlType(String data) {

        def m = data =~ DATE_TIME

        if(m.matches()) {
            return Types.TIMESTAMP
        }

        m = data =~ DATE
        if(m.matches()) {
            return Types.DATE
        }

        return Types.VARCHAR
    }

    static int getSqlType(boolean data) {
        return Types.BOOLEAN
    }

    static int getBestSqlType(int type1, int type2) {
        int order1 = orderedTypes.get(type1)
        int order2 = orderedTypes.get(type2)

        boolean mixed = order1 * order2 < 0

        if(mixed) {
            return Types.VARCHAR
        }

        boolean type1TakesPriorityOverType2 = order1 < order2
        if(type1TakesPriorityOverType2) {
            return type1
        }

        return type2
    }

    static int getBestSqlType(int type, Object data) {
        int type2 = getSqlType(data)
        getBestSqlType(type, type2)
    }


}
