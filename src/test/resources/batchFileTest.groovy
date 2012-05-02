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
sql {
    runFoo {
        order = 1.5
        runIt {
            sql = "insert into baz(foo, bar) values (4, 5)"
        }
        runSomethingElse {
            sql = "insert into baz(foo, bar) values (10,2)"
        }
    }

    runBar {
        order = 1
        runMeFirst {
            sql = "insert into blah(foo, bar) values ('hey', 'man')"
        }
    }

    willExclude {
        runThis {
            sql = "update blah set foo='fooba'"
        }
    }
}