/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.file;

import edu.upennlib.metridoc.file.IteratorCreator;
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
