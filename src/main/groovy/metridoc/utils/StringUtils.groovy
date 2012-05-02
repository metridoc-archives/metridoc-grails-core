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


import static metridoc.utils.CollectionUtils.Lists

/**
 * @author Tommy Barker
 */
public class StringUtils {
    private static List<String> stopWords;
    public static final String STOP_WORD_FILE = "stopwords.txt";

    public static final String EMPTY = "";

    private static List<String> getStopWords() {
        if (stopWords == null) {
            stopWords = Lists.createArrayList();
            InputStream stream = StringUtils.class.getClassLoader().getResourceAsStream(STOP_WORD_FILE);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            String line;

            try {
                while ((line = bufferedReader.readLine()) != null) {
                    stopWords.add(line.trim());
                }
            } catch (IOException e) {
                throw new RuntimeException("Exception occurred loading the stop words file 'stopwords.txt'");
            } finally {
                IOUtils.closeQuietly(bufferedReader);
            }
        }

        return stopWords;
    }

    public static String normalize(String text) {
        String result = text.toLowerCase();
        for (int i = 0; i < getStopWords().size(); i++) {
            result = result.replaceAll("\\b" + stopWords.get(i) + "\\b\\s*", "");
        }
        //Remove punctuation:  .?!:;-_()[]...'"/,
        String regexp = "[.?!:;\\-_()\\[\\]'\"/,&]";
        result = result.replaceAll(regexp, "");
        result = result.replaceAll("\\s+", " ");
        return result.trim();
    }
}
