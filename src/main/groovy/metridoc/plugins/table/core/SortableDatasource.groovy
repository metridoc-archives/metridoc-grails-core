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
	This is a marker interface, in the same spirit as Wrappable
Datasource.  This allows sorting operations to be performed in addition
to the other operations.  The sorting is done eagerly, so the entire 
underlying datasource must be in memory in order to this.  This is potentially
a more expensive operation, so use this interface carefully.
*/
interface SortableDatasource extends FilterableDatasource{
}


