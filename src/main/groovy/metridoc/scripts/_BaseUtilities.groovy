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
package metridoc.scripts

hasVariable = {String variableName ->
    binding.variables.containsKey(variableName)
}

checkVariablesExist = {List<String> variables, targetName ->
    variables.each {
        assert binding.variables.containsKey(it): "The target ${targetName} requires variable ${it} to function"
    }
}

class HashWithFailOnMissingEntry extends HashMap {

    HashWithFailOnMissingEntry() {
    }

    HashWithFailOnMissingEntry(Map map) {
        super(map)
    }

    @Override
    Object get(Object o) {
        if (!this.containsKey(o)) {
            throw new MissingPropertyException(o, HashWithFailOnMissingEntry)
        }

        else super.get(o)
    }
}