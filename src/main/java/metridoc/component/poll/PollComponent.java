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

package metridoc.component.poll;

import java.util.Map;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

/**
 *
 * @author Thomas Barker
 */
public class PollComponent extends DefaultComponent{

    /*
     * TODO: The poll enrich dsl, which this is based on, has an option for an aggregation strategy.  We should discuss
     * whether or not we should include support for this.  Since extractions
     *
     */

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        return new PollEndpoint(uri, this);
    }

}
