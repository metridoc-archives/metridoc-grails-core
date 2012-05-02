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
package metridoc.plugins.table.core

/**
Note - see "making_an_ls_datasource" in the docs directory for an in depth exapmle
of how to implement this.  Alternatively, you could look at any of the classes in 
metridoc.plugs.table.impl
*/
interface Datasource {
	/**
		This funciton is supposed to provide the inputs at each stage.
It is intended to return a hash of hashes.  The highest level hash may use any
of the following keys:

	global - This is for settings that should be the same for any data source of
this type.  This value will be used for the datasource throughout the application.  Some
examples of this would be a base directory of a file resource, of the size of a connecion
pool for a database driver.

	instance - This is for settings that are specific to each instance.  For example,
a database url, or a specific file location would go here.

	request - These are for request level inputs.  They are designed to be set by
the consumer of the datasource, and can change all of the time.  This is where the inputs
for a custom datasource should be set.

	Now, each of the subhashes is a collection of keys, and the default value to use for the
keys.  See some of the classes that implement this interface to get a sense of how it is used.
	*/
	public Object getInputs();

	/**
		This function is used to provide a description of the datasource.  For example,
it provides a list of column names and types for each table in a MySQL Datasource, and it provides
the list of column headers in a CSV or Excel datasource.
	*/
	public Object profile();

	/**
		This function iterates over a list of objects, based on how the implementation 
interprets the builder object.
	*/
	public Object each(Object builder,Object closure);

        /**
	The accessors for the global configs
	*/
	public void setGlobals(globals)
	public Object getGlobals()

}
