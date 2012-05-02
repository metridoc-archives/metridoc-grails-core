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

import org.junit.Test
import java.sql.Types;
import static metridoc.utils.SqlUtils.*

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/2/11
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class SqlUtilsTest {

    @Test
    void testGettingDate() {
        assert getSqlType("2004-01-1") == Types.DATE
    }

    @Test
    void testGettingDateTime() {
        assert getSqlType("2004-01-1 5:05:06") == Types.TIMESTAMP
    }

    @Test
    void testNormalVarchar() {
        assert getSqlType("blah") == Types.VARCHAR
    }

    @Test
    void varcharTrumpsDate() {
        assert getBestSqlType(Types.DATE, Types.VARCHAR) == Types.VARCHAR
        assert getBestSqlType(Types.DATE, "bar") == Types.VARCHAR
    }

    @Test
    void varcharTrumpsTimeStamp() {
        assert getBestSqlType(Types.TIMESTAMP, Types.VARCHAR) == Types.VARCHAR
        assert getBestSqlType(Types.TIMESTAMP, "foo") == Types.VARCHAR
    }

    @Test
    void bigIntTrumpsInteger() {
        assert getBestSqlType(Types.INTEGER, Types.BIGINT) == Types.BIGINT
        assert getBestSqlType(Types.INTEGER, 1L) == Types.BIGINT
    }

    @Test
    void dateAndIntReturnsVarchar() {
        assert getBestSqlType(Types.DATE, Types.BIGINT) == Types.VARCHAR
        assert getBestSqlType(Types.DATE, 1L) == Types.VARCHAR
    }

    @Test
    void doubleTrumpsBigInt() {
        assert getBestSqlType(Types.DOUBLE, Types.BIGINT) == Types.DOUBLE
    }

    @Test
    void timeStampTrumpsDate() {
        assert getBestSqlType(Types.DATE, "2011-1-1 1:1:1") == Types.TIMESTAMP
    }


}
