/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.db;

import java.util.List;

/**
 *
 * @author tbarker9
 */
public interface BeanDao {
    void insert(Object bean);
    <T> List<T> getAll(Class<T> clazz);
}
