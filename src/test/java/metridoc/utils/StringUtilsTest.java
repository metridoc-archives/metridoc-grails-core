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
package metridoc.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 4/6/11
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringUtilsTest {

    @Test
    public void testNormalizeStopWords() {
        String textToNormalize = "spring Affordable a's some text able about above winter";
        String expectedResult = "spring affordable text winter";
        assertEquals(expectedResult, StringUtils.normalize(textToNormalize));
    }

    @Test
    public void testNormalizePunctuation() {
        String textToNormalize = "Spring&Winter";
        String expectedResult = "springwinter";
        assertEquals(expectedResult, StringUtils.normalize(textToNormalize));

        textToNormalize = "Spring&[Winter]&(summer)";
        expectedResult = "springwintersummer";
        assertEquals(expectedResult, StringUtils.normalize(textToNormalize));

        textToNormalize = "Spr/ing!\"Wi:n;te..r\"-sum'mer";
        expectedResult = "springwintersummer";
        assertEquals(expectedResult, StringUtils.normalize(textToNormalize));
    }

    @Test
    public void testNormalizeSpace() {
        String textToNormalize = "spring    winter summer    ";
        String expectedResult = "spring winter summer";
        assertEquals(expectedResult, StringUtils.normalize(textToNormalize));
    }

    @Test
    public void testNormalize() {
        //.?!:;_-()[]...í"/,
        String textToNormalize = "Spring is here!...[(right? )]:;-_\"Winter\" ' /Summer";
        String expectedResult = "spring winter summer";
        assertEquals(expectedResult, StringUtils.normalize(textToNormalize));
    }
}
