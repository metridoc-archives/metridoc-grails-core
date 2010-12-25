/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.config;

/**
 *
 * @author tbarker
 */
public enum Environment {TEST, DEVELOPMENT, STAGING, PRODUCTION;

    private static Environment currentEnvironment = PRODUCTION;

    public static void set(Environment environment) {
        currentEnvironment = environment;
    }

    public static Environment get() {
        return currentEnvironment;
    }

}
