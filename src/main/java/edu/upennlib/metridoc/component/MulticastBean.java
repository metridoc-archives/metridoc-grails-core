package edu.upennlib.metridoc.component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 *    
 * Marks class as a multicast bean.  All multicast beans must have a default no arg constructor
 * and at least 1 {@link Receive} annotated method.  Essentially incoming messages are multicasted
 * to all methods with a {@link Receive} annotation and all results are sent to a {@link Reduce}
 * endpoint if it exists.  If a {@link Reduce} annotation does not exist, the output is added to a 
 * {@link List} based on the specified order of the {@link Receive} annotation.  If no order is 
 * specified, answers are randomly added to a list.
 *
 * @author tbarker
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MulticastBean {}
