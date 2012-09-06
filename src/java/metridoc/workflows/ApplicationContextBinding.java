package metridoc.workflows;

import groovy.lang.Binding;
import groovy.lang.MissingPropertyException;
import org.springframework.context.ApplicationContext;

/**
 * Makes all variables in an application context available to a binding.  Anything stored in the {@link Binding} is used
 * first, then the {@link ApplicationContext} is searched
 */
public class ApplicationContextBinding extends Binding {

    private ApplicationContext applicationContext;

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getVariable(String name) {

        MissingPropertyException ex = null;
        Object variable = null;
        try {
            variable = super.getVariable(name);
        } catch (MissingPropertyException e) {
            ex = e;
        }

        if(ex != null) {
            if (applicationContext.containsBean(name)) {
                variable = applicationContext.getBean(name);
                ex = null;
            }
        }

        if(ex != null) {
            throw ex;
        }

        return variable;
    }

    @Override
    public boolean hasVariable(String name) {
        boolean hasVariable = super.hasVariable(name);
        if(!hasVariable) {
            hasVariable = applicationContext.containsBean(name);
        }

        return hasVariable;
    }
}
