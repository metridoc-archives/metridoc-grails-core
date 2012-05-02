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

import metridoc.utils.DataSourceUtils
/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 2/13/12
 * Time: 11:17 AM
 */

foo {
    dataSource = DataSourceUtils.embeddedDataSource()
}

bar {
    dataSource {
        url = DataSourceUtils.EMBEDDED_DATA_SOURCE_URL
    }
}