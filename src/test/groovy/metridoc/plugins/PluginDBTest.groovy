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
package metridoc.plugins

import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/3/11
 * Time: 2:58 PM
 */
public class PluginDBTest {

    @Test
    void testAddingPlugin() {
        def db = new PluginDB()
        db.addPlugin(Foo.class)
        def plugins = db.getPlugins("job")
        assert 1 == plugins.size()
        assert Foo.class == plugins.get(0)
    }

    @Test
    void testAddingSamePluginTwice() {
        def db = new PluginDB()
        db.addPlugin(Foo.class)
        db.addPlugin(Foo.class)//Duplication is intentional
        def plugins = db.getPlugins("job")
        assert 1 == plugins.size()
        assert Foo.class == plugins.get(0)
    }

    @Test
    void testAddingSameNameDifferentCategories() {
        def db = new PluginDB()
        db.addPlugin(Foo.class)
        db.addPlugin(Cheers.class)
        def plugins = db.getPlugins("job")
        assert 1 == plugins.size()
        assert Foo.class == plugins.get(0)

        plugins = db.getPlugins("tv_shows")
        assert 1 == plugins.size()
        assert Cheers.class == plugins.get(0)
    }

    @Test
    void testGetCategories() {
        def db = new PluginDB()
        db.addPlugin(Foo.class)
        db.addPlugin(Cheers.class)
        assert ["job", "tv_shows"] == db.getCategories()
    }

    @Test(expected = IllegalArgumentException.class)
    void testThrowsExceptionWithNameCollision() {
        def db = new PluginDB()
        db.addPlugin(Foo.class)
        db.addPlugin(FooDuplicate.class)
    }

    @Test(expected = IllegalArgumentException.class)
    void testThrowsExceptionIfNotAPlugin() {
        def db = new PluginDB()
        db.addPlugin(String.class)
    }

    @Test
    void getInstanceReturnsTheSameDbEachTime() {
        assert PluginDB.getInstance() == PluginDB.getInstance()
    }

    @Test
    void getPluginFromName() {
        PluginDB.getInstance().addPlugin(Foo.class)
        assert PluginDB.getInstance().getPlugin("job", "bar") == Foo.class
    }
}

@Plugin(category = "job", name = "bar")
class Foo {}

@Plugin(category = "job", name = "bar")
class FooDuplicate {}

@Plugin(category = "tv_shows", name = "bar")
class Cheers {}
