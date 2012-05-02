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

import groovy.util.logging.Slf4j
import metridoc.utils.SystemUtils
import metridoc.workflows.validation.Validator

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 2/20/12
 * Time: 4:40 PM
 */
@Slf4j
class HashSqlValidator implements Validator<Map<String, Object>> {

    private Map<String, SqlPayloadCheck> payloadChecks = [:]
    public static final String VALUE_CANNOT_BE_NULL = "value cannot be null"
    public static final String SIZE_EXCEEDS_MAX_LENGTH = "character size exceeds the maximum length"
    public static final String INVALID_CHARACTER_PATTERN = "value does not conform to the expected pattern"
    public static final String CUSTOM_VALIDATION_ERROR = "Validation Error"
    public static final String NEW_LINE = SystemUtils.LINE_SEPARATOR

    void setValidationMap(Map<String, Map> checks) {
        checks.each {key, value ->
            def check = new SqlPayloadCheck(value) //an exception would occur here if the hash does not make sense
            this.payloadChecks.put(key, check)
        }
    }

    /**
     * compares the actual data type against the expected data type
     *
     * @param value - actual data type
     * @param check - expected data type
     */
    static boolean checkType(value, SqlPayloadCheck check) {
        def type = check.type
        if (type) {
            return type.isInstance(value)
        }

        return true
    }

    /**
     * compares the actual length against the maximum valid length
     *
     * @param value - actual length
     * @param check - maximum valid length
     */
    static void checkLength(value, SqlPayloadCheck check, Map payload) {
        if (value instanceof Integer) {
            def len = check.length
            if (!(value <= (len))) {
                assert value <= (len): validLengthErrorMessage(value, len) + getPrettyRecord(payload)
            }
        } else if (value instanceof String) {
            def len = check.length
            if (!(value.length() <= len)) {
                assert value.length() <= len: validLengthErrorMessage(value, len) + getPrettyRecord(payload)
            }
        }
    }

    /**
     * checks setting for allowable null values
     *
     * @param check - map containing validation rules
     *
     * @return returns true if null value is allowed; false if not null is enforced
     */
    static checkNullable(SqlPayloadCheck check, Map payload, String columnName) {
        if (!check.nullable) {
            assert check.nullable: validNullErrorMessage(columnName) + getPrettyRecord(payload)
        }
    }

    static String validNullErrorMessage(String columnName, Map payload) {
        return "column ${columnName} cannot be null" + getPrettyRecord(payload)
    }

    /**
     * checks a value against a custom validator
     * the custom validator should return true(Valid) or false(Invalid)
     *
     * @param value - value to validate
     * @param check - custom validator
     */
    static void checkCustom(value, SqlPayloadCheck check, Map payload) {
        if (!check.custom(value)) {
            assert check.custom(value): validCustomErrorMessage(value) + getPrettyRecord(payload)
        }
    }

    /**
     * compares the value's character pattern against the expected pattern
     *
     * @param value - input string to validate
     * @param check - map containing expected pattern
     */
    static void checkRegex(value, SqlPayloadCheck check, Map payload) {
        def pattern = ~check.regex
        if (!(value ==~ pattern)) {
            assert value ==~ pattern: validRegexErrorMessage(value, check.regex) + getPrettyRecord(payload)
        }
    }

    static String validRegexErrorMessage(value, String regex) {
        return "$value value does not conform to the expected pattern of $regex"
    }

    static String validTypeErrorMessage(value, Class type) {
        return "value $value is not of type $type"
    }

    static String validLengthErrorMessage(value, Integer length) {
        return "value $value character size exceeds the maximum length of $length"
    }

    static String validCustomErrorMessage(value) {
        return "Validation Error in $value"
    }

    void validate(Map<String, Object> payload) {
        def localPayloadChecks = new HashMap(payloadChecks)
        payload.each { columnName, value ->
            def check = localPayloadChecks.remove(columnName)
            if (check) {
                if (value == null) {
                    checkNullable(check, payload, columnName)
                } else {
                    if (check.type) {
                        if (!checkType((Object) value, check)) {
                            try {
                                payload[columnName] = value.asType(check.type)
                            } catch (Exception e) {
                                assert checkType(value, check): validTypeErrorMessage(value, check.type) + getPrettyRecord(payload)
                            }
                        }
                    }
                    if (check.length) checkLength(value, check, payload)
                    if (check.regex) checkRegex(value, check, payload)
                    if (check.custom) checkCustom(value, check, payload)
                }
            } else {
                log.debug("no validation for {}", columnName)
            }
        }


        if (!localPayloadChecks.isEmpty()) {
            def message = "payload did not contain columns ${localPayloadChecks.keySet()}${getPrettyRecord(payload)}"
            assert localPayloadChecks.isEmpty(): message
        }
    }

    static String getPrettyRecord(Map<String, Object> record) {
        def result = new StringBuilder()
        def tab = "    "
        result.append(NEW_LINE)
        result.append("record {")
        result.append(NEW_LINE)
        record.each {key, value ->
            result.append("${tab}${key}: $value")
            result.append(NEW_LINE)
        }
        result.append("}")
        result.append(NEW_LINE)
    }
}

/**
 * Although not necessary per say, it helps us explicitly declare what checks we can do.  So if someone were to
 * accidentally try to add a check that did not exist an error would be thrown instead of ignoring it if we were to use
 * a plain hash
 */
class SqlPayloadCheck {
    Boolean nullable = true
    String regex
    Class type
    Closure custom
    Integer length
}