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

/**
 * @author Tommy Barker
 */
public class StrBuilder {
    private StringBuilder stringBuilder = new StringBuilder();
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public StrBuilder appendNewLine() {
        stringBuilder.append(LINE_SEPARATOR);
        return this;
    }

    public StrBuilder appendln(String line) {
        stringBuilder.append(line);
        appendNewLine();

        return this;
    }

    public StrBuilder append(String text) {
        stringBuilder.append(text);
        return this;
    }

    public StrBuilder chop() {
        int size = stringBuilder.length();
        if (size > 0) {
            stringBuilder.deleteCharAt(size - 1);
        }

        return this;
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
