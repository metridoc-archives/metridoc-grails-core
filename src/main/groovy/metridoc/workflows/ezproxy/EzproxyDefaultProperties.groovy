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
package metridoc.workflows.ezproxy

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 6/15/12
 * Time: 2:56 PM
 */

urlsToSearch = [
    ez_sfx_resources: "/elinks.",
    ez_doi_resources: "/doi/",
    ez_pmid_resources: "pmid:[0-9]{8}",
]

defaultValidationMapItem = [type: String, length: 32]

defaultKeyType = [type: byte[]]

defaultValidationMap = [
    ref_url_key: defaultKeyType,
    url_key: defaultKeyType,
    source_file_key: defaultKeyType,
    ezproxy_id_key: defaultKeyType,
    response_key: defaultKeyType,
    agent_key: defaultKeyType,
    patron_address_key: defaultKeyType,
    patron_id_key: defaultKeyType,
    patron_ip_key: defaultKeyType,
    patron_ip: [
        regex: /\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b/,
        type: String
    ],
    city: defaultValidationMapItem,
    http_method: [
        length: 12,
        type: String
    ],
    state: [
        length: 2,
        type: String
    ],
    agent: [
        length: 1024,
        type: String
    ],
    source_file: defaultValidationMapItem,
    line_num: [
        type: Integer,
    ],
    country: defaultValidationMapItem,
    patron_id: defaultValidationMapItem,
    http_status: [
        type: Integer,
    ],
    response_size: [
        type: Integer,
    ],
    ref_url: [
        type: String,
        length: 2000
    ],
    url: [
        type: String,
        length: 2000
    ],
    ezproxy_id: defaultValidationMapItem,
]