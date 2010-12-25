/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.db;

/**
 *
 * @author tbarker9
 */
public interface BeanSqlBuilder {
    String buildInsertSql(Class<?> clazz);
}
