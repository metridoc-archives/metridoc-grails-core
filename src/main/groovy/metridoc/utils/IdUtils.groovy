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

import java.security.MessageDigest

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/27/11
 * Time: 3:51 PM
 *
 * Basic id utilities to generate random ids and also assign an md5 value to data that might be large and difficult
 * to query (useful for concatenated natural keys)
 *
 */
class IdUtils {

    static byte[] md5(String delimiter, String... itemsToConcatenate) {
        String concatenated = concat(delimiter, itemsToConcatenate)
        return md5(concatenated)
    }

    private static String concat(String delimiter, String... itemsToConcatenate) {

        if(itemsToConcatenate.size() == 1) {
            return itemsToConcatenate[0]
        }
        def result = new StringBuilder()
        itemsToConcatenate.each {
            result.append(it)
            result.append(delimiter)
        }

        return result
    }

    static byte[] md5(String data) {
        MessageDigest digest = MessageDigest.getInstance("MD5")
        digest.update(data.bytes)
        return digest.digest()
    }

    static byte[] uuid() {
        UUID uuid = UUID.randomUUID()
        long most = uuid.mostSignificantBits
        long least = uuid.leastSignificantBits

        return convertAndCombineLongs(most, least)
    }

    private static byte[] convertAndCombineLongs(long mostSignificantBits, long leastSignificantBits) {
        def result = []
        def bytesToAdd = convertLong(mostSignificantBits)

        bytesToAdd.each {
            result.add(it)
        }

        bytesToAdd = convertLong(leastSignificantBits)

        bytesToAdd.each {
            result.add(it)
        }

        return result as byte[]
    }

    private static byte[] convertLong(long numberToConvert) {
        def bos = new ByteArrayOutputStream(8)
        def dos = new DataOutputStream(bos)
        dos.writeLong(numberToConvert)
        dos.flush()
        return bos.toByteArray()
    }
}
