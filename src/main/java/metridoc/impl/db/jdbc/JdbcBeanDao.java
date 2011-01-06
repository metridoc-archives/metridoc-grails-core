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
package metridoc.impl.db.jdbc;

import metridoc.db.BeanDao;
import metridoc.utils.StateAssert;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.Validate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 *
 * @author tbarker9
 */
public class JdbcBeanDao implements BeanDao {

    private JdbcInserts jdbcInserts;
    private SimpleJdbcTemplate jdbcTemplate;
    private DataSource defaultDataSource;
    protected static final String NO_DATA_SOURCE_ERROR = "a default datasource has not been set";
    private static final String SELECT_ALL = "select * from %s";
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("([A-Z])");

    public JdbcBeanDao withDataSource(DataSource dataSource) {
        defaultDataSource = dataSource;
        return this;
    }

    protected SimpleJdbcTemplate getSimpleTemplate() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new SimpleJdbcTemplate(getDefaultDataSource());
        }
        return jdbcTemplate;
    }

    protected JdbcInserts getJdbcInserts() {
        if (jdbcInserts == null) {
            StateAssert.notNull(defaultDataSource, NO_DATA_SOURCE_ERROR);
            jdbcInserts = new JdbcInserts().withDataSource(defaultDataSource);
        }
        return jdbcInserts;
    }

    public DataSource getDefaultDataSource() {
        return defaultDataSource;
    }

    public void setDefaultDataSource(DataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    @Override
    public void insert(Object bean) {
        SimpleJdbcInsert simpleInsert = getJdbcInserts().getSimpleInsert(bean.getClass());
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(bean);
        Number number = simpleInsert.executeAndReturnKey(parameters);

        try {
            PropertyUtils.setProperty(bean, "id", number.longValue());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public <T> List<T> getAll(Class<T> clazz) {
        String tableName = getTableNameFromClass(clazz);
        String sql = String.format(SELECT_ALL, tableName);

        return getSimpleTemplate().query(sql, BeanPropertyRowMapper.newInstance(clazz));
    }

    protected static String getTableNameFromClass(Class clazz) {
        String tableName = clazz.getSimpleName();
        Matcher m = UPPERCASE_PATTERN.matcher(tableName);

        if (m.lookingAt()) {
            String foundItem = m.group(1);
            String replacement = foundItem.toLowerCase();
            tableName = tableName.replaceFirst(foundItem, replacement);
            m = UPPERCASE_PATTERN.matcher(tableName);
        }

        while (m.find()) {
            String foundItem = m.group(1);
            String replacement = "_" + foundItem.toLowerCase();
            tableName = tableName.replaceAll(foundItem, replacement);
            m = UPPERCASE_PATTERN.matcher(tableName);
        }

        return tableName;
    }

    public static class JdbcInserts extends HashMap<Class, SimpleJdbcInsert> {

        protected static final String NO_INSERT_ERROR = "jdbcInsert must not be null";
        protected static final String NO_CLASS_ERROR = "class must not be null";
        private DataSource defaultDataSource;

        public DataSource getDefaultDataSource() {
            return defaultDataSource;
        }

        public void setDefaultDataSource(DataSource defaultDataSource) {
            this.defaultDataSource = defaultDataSource;
        }

        public JdbcInserts withDataSource(DataSource dataSource) {
            this.defaultDataSource = dataSource;
            return this;
        }

        public JdbcInserts add(Class clazz, SimpleJdbcInsert jdbcInsert) {
            Validate.notNull(jdbcInsert, NO_INSERT_ERROR);
            Validate.notNull(clazz, NO_CLASS_ERROR);
            this.put(clazz, jdbcInsert);
            return this;
        }

        public SimpleJdbcInsert getSimpleInsert(Class<?> clazz) {

            if (doesNotContainClass(clazz)) {
                SimpleJdbcInsert jdbcInsert =
                        new SimpleJdbcInsert(defaultDataSource).withTableName(getTableNameFromClass(clazz)).usingGeneratedKeyColumns("id");
                add(clazz, jdbcInsert);
            }

            return get(clazz);
        }

        private boolean doesNotContainClass(Class<?> clazz) {
            return !this.containsKey(clazz);
        }
    }
}
