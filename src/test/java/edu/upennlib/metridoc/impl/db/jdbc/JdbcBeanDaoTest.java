/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.db.jdbc;

import edu.upennlib.metridoc.impl.db.jdbc.JdbcBeanDao.JdbcInserts;
import edu.upennlib.metridoc.test.JdbcTest;
import javax.sql.DataSource;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

/**
 *
 * @author tbarker9
 */
public class JdbcBeanDaoTest extends JdbcTest{

    @Test
    public void getJdbcInsertsThrowsErrorIfThereIsNoDefaultDataSource() {
        try {
            new JdbcBeanDao().getJdbcInserts();
            fail("exception should have ocurred");
        } catch (IllegalStateException e) {
            assertEquals(JdbcBeanDao.NO_DATA_SOURCE_ERROR, e.getMessage());
        }
    }

    @Test
    public void getJdbcInsertsIsNeverNull() {
        assertNotNull(new JdbcBeanDao().withDataSource(dataSource()).getJdbcInserts());
    }

    @Test
    public void getSimpleTemplateIsNeverNull() {
        assertNotNull(new JdbcBeanDao().withDataSource(dataSource()).getSimpleTemplate());
    }

    @Test
    public void canInsertAndRetrieve() {
        JdbcBeanDao dao = new JdbcBeanDao().withDataSource(dataSource());
        dao.insert(new FooBar("joe", 5));
        dao.insert(new FooBar("mike", 6));
        assertEquals(2, dao.getAll(FooBar.class).size());
    }

    @Test
    public void addInsertsAnNewInsertAndItself() {
        JdbcInserts insert = new JdbcInserts().add(FooBar.class, new SimpleJdbcInsert(createMock(DataSource.class)));
        assertTrue(insert.containsKey(FooBar.class));
    }

    @Test
    public void cannotInsertNullValuesInAdd() {
        try {
            new JdbcInserts().add(FooBar.class, null);
            fail("exception should have occurred");
        } catch (Exception e) {
            assertEquals(JdbcInserts.NO_INSERT_ERROR, e.getMessage());
        }

        try {
            new JdbcInserts().add(null, new SimpleJdbcInsert(createMock(DataSource.class)));
            fail("exception should have occurred");
        } catch (Exception e) {
            assertEquals(JdbcInserts.NO_CLASS_ERROR, e.getMessage());
        }
    }

    @Test
    public void getByClassDoesNotReturnNull() {
        assertNotNull(new JdbcInserts().withDataSource(dataSource()).getSimpleInsert(FooBar.class));
    }

    @Test
    public void tableNameFromSingleWordClassIsTheLowerCaseNameOfTheClass() {
        assertEquals("string", JdbcBeanDao.getTableNameFromClass(String.class));
    }

    @Test
    public void tableNameFromMultiWordClassConvertsCamelCaseToUnderscores() {
        assertEquals("foo_bar", JdbcBeanDao.getTableNameFromClass(FooBar.class));
    }

    @Override
    public String[] getCreateTableScripts() {
        return new String[]{
            "create table foo_bar(id identity, first_name varchar(50), age integer)"
        };
    }

    public static class FooBar {
        private long id;
        private String firstName;
        private int age;

        public FooBar(String firstName, int age) {
            this.firstName = firstName;
            this.age = age;
        }

        public FooBar() {
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
    }
}