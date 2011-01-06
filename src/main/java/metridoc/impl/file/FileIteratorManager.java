/**
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metridoc.impl.file;

import metridoc.file.IteratorCreator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.Validate;

/**
 *
 * @author tbarker
 */
public class FileIteratorManager {

    private final Map<String, IteratorCreator> iteratorCreatorsByName = new HashMap<String, IteratorCreator>();

    public FileIteratorManager(List<IteratorCreator> iteratorCreators) {
        this(iteratorCreators.toArray(new IteratorCreator[iteratorCreators.size()]));
    }

    public FileIteratorManager(IteratorCreator... iteratorCreators) {
        String message = "iteratorCreators cannot be null, empty or contain null elements";
        Validate.notEmpty(iteratorCreators, message);
        Validate.noNullElements(iteratorCreators, message);

        for (IteratorCreator iteratorCreator : iteratorCreators) {
            String name = iteratorCreator.getName();
            if (iteratorCreatorsByName.containsKey(name)) {
                throw NoUniqueIteratorCreatorException.noUniqueCreatorForName(name);
            }
            iteratorCreatorsByName.put(name, iteratorCreator);
        }
    }

    public IteratorCreator getIteratorCreatorByFileExtension(String extension) {

        IteratorCreator result = null;
        for (IteratorCreator iteratorCreator : iteratorCreatorsByName.values()) {
            if (iteratorCreator.supportsExtension(extension)) {
                if (result != null) {
                    throw NoUniqueIteratorCreatorException.noUniqueCreatorForExtension(extension);
                }
                result = iteratorCreator;
            }
        }

        return result;
    }

    public IteratorCreator getIteratorCreatorByName(String name) {
        return iteratorCreatorsByName.get(name);
    }

    public static class NoUniqueIteratorCreatorException extends RuntimeException {

        private final String description;
        private final boolean extensionException;

        private NoUniqueIteratorCreatorException(String description, boolean extensionException) {
            this.description = description;
            this.extensionException = extensionException;
        }
        
        public static NoUniqueIteratorCreatorException noUniqueCreatorForExtension(String extension) {
            return new NoUniqueIteratorCreatorException(extension, true);
        }

        public static NoUniqueIteratorCreatorException noUniqueCreatorForName(String name) {
            return new NoUniqueIteratorCreatorException(name, false);
        }

        @Override
        public String getMessage() {

            String message;
            if (extensionException) {
                message = "there are multiple IteratorCreators for extension " + description;
            } else {
                message = "there are multiple IteratorCreators with the name " + description;

            }

            return message;
        }
    }
}
