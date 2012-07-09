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
package metridoc.counter

import org.junit.Test

class CounterServiceTest {

    @Test
    void "test default matching pattern for counter"() {
        def service = new CounterService()
        assert "foo/counterJR1_2008_123.xls" ==~ service.filePattern(2008, "JR1")
        assert "foo/counterJR1_2008.xls" ==~ service.filePattern(2008, "JR1")
        assert "/foo/bar/counterJR1_2008.xlsx" ==~ service.filePattern(2008, "JR1")
        assert !("foo/counterJR1_2009.xlsx" ==~ service.filePattern(2008, "JR1"))
    }

    @Test
    void "given a file list, year and report, the most recent file will be chosen"() {
        assert "foo/counterJR1_2008_2.xls" == CounterService.getFileName(
            2008,
            "JR1",
            [
                "foo/counterJR1_2009.xls",
                "foo/counterJR1_2008_1.xls",
                "foo/counterJR1_2008_2.xls"
            ]
        )
    }

    @Test
    void "make sure everything works regardless of using xls or xlsx"() {
        assert "foo/counterJR1_2008_2.xlsx" == CounterService.getFileName(
            2008,
            "JR1",
            [
                "foo/counterJR1_2008_1.xls",
                "foo/counterJR1_2008_2.xlsx"
            ]
        )

        assert "foo/counterJR1_2008_2.xls" == CounterService.getFileName(
            2008,
            "JR1",
            [
                "foo/counterJR1_2008_1.xlsx",
                "foo/counterJR1_2008_2.xls"
            ]
        )
    }
}
