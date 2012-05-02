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
package metridoc.workflows.validation.impl

import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 2/20/12
 * Time: 5:03 PM
 */
class HashSqlValidatorTest {

    @Test
    void testLengthOnString() {
        def record = [firstName: "blam", lastName: "shazam", gender: "M", age: 10]
        def validator = new HashSqlValidator(
            validationMap:
                [
                    firstName: [length: 2, type: String, nullable: false, regex: "[A-Za-z]*"],
                    lastName: [length: 20, type: String, nullable: false, regex: "[A-Za-z]*"],
                    gender: [length: 1, type: String],
                    age: [type: Integer]
                ]
        )
        try {
            validator.validate(record)
            assert false: "error should have occurred"
        } catch (AssertionError assertionError) {
            assert assertionError.message.contains("character size exceeds the maximum length of")
        }
    }

    @Test
    void testBasicHashValidation() {
        def record = [firstName: "blam", lastName: "shazam", gender: "M", age: 10]

        def validator = new HashSqlValidator(
            validationMap:
                [
                    firstName: [length: 20, type: String, nullable: false, regex: "[A-Za-z]*"],
                    lastName: [length: 20, type: String, nullable: false, regex: "[A-Za-z]*"],
                    gender: [length: 1, type: String],
                    age: [type: Integer]
                ]
        )
        validator.validate(record)

        validator = new HashSqlValidator(
            validationMap: [ip_address: [regex: /\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b/]]
        )

        record = [ip_address: "115.201.108.176"]
        validator.validate(record)
    }

    @Test
    void testCheckType() {
        String value = "foo"
        assert HashSqlValidator.checkType(value, new SqlPayloadCheck(type: String))
        assert !HashSqlValidator.checkType(value, new SqlPayloadCheck(type: Integer))
    }


    @Test
    void testCheckLength() {
        def value = 5

        HashSqlValidator.checkLength(value, new SqlPayloadCheck(length: 7), [:])
        try {
            HashSqlValidator.checkLength(value, new SqlPayloadCheck(length: 3), [:])
            assert false: "exception should have occurred"
        } catch (AssertionError assertionError) {
            assert assertionError.message.contains(HashSqlValidator.SIZE_EXCEEDS_MAX_LENGTH)
        }
    }

    @Test
    void testCheckRegex() {
        HashSqlValidator.checkRegex("aBcDe", new SqlPayloadCheck(regex: "[A-Za-z]*"), [:])
        try {
            HashSqlValidator.checkRegex("ac5ee", new SqlPayloadCheck(regex: "[A-Za-z]*"), [:])
            assert false: "exception should have occurred"
        } catch (AssertionError assertionError) {
            assert assertionError.message.contains(HashSqlValidator.INVALID_CHARACTER_PATTERN)
        }
    }

    @Test
    void testCheckCustom() {
        def myValidator = { val -> val > 0 }

        HashSqlValidator.checkCustom(42, new SqlPayloadCheck(custom: myValidator), [:])
        try {
            HashSqlValidator.checkCustom(0, new SqlPayloadCheck(custom: myValidator), [:])
            assert false: "exception should have occurred"
        } catch (AssertionError assertionError) {
            assert assertionError.message.contains(HashSqlValidator.CUSTOM_VALIDATION_ERROR)
        }
    }
}
