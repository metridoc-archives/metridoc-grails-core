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
modules = {

    changePassword {
         dependsOn 'jquery-ui'
         resource id:'css',
                 url: [plugin: "metridoCore", dir: 'changePassword/css', file: 'changePassword.css']
         resource id:'js',
                 url: [plugin: "metridocCore", dir: 'changePassword/js', file: 'changePassword.js']
    }

    user {
        dependsOn 'jquery-ui'
        resource id: 'css',
            url: [plugin: "metridocCore", dir: 'user/css', file: 'user.css']
        resource id: 'js',
            url: [plugin: "metridocCore", dir: 'user/js', file: 'user.js']
    }

    role {
        dependsOn 'jquery-ui'
        resource id: 'css',
            url: [plugin: "metridocCore", dir: 'role/css', file: 'role.css']
        resource id: 'js',
            url: [plugin: "metridocCore", dir: 'role/js', file: 'role.js']
    }

    application {
        dependsOn 'jquery-ui', 'jquery'
        resource id: 'appJs',
            url: [plugin: "metridocCore", dir: 'js', file: 'application.js']
        resource id: 'mobileCss',
            url: [plugin: "metridocCore", dir: 'css', file: 'mobile.css']
        resource id: 'mainCss',
            url: [plugin: "metridocCore", dir: 'css', file: 'main.css']
        resource id: 'navBarCss',
            url: [plugin: "metridocCore", dir: 'css', file: 'navBar.css']
        resource id: 'faviconIco',
            url: [plugin: "metridocCore", dir: 'images', file: 'favicon.ico']

    }

    admin {
        dependsOn 'jquery-ui'
        resource id: 'adminCss',
            url: [plugin: "metridocCore", dir: 'admin/css', file: 'admin.css']
        resource id: 'adminJs',
            url: [plugin: "metridocCore", dir: 'admin/js', file: 'admin.js']
    }

    overrides {
        'jquery-theme' {
            resource id: 'theme',
                url: [plugin: "metridocCore", dir: '/jquery-ui/forrest-dialog/css/custom-theme', file: 'jquery-ui-1.8.22.custom.css']
        }
    }

    manageReports {
        dependsOn 'jquery-ui'
        resource id: 'manageReportsCss',
            url: [plugin: "metridocCore", dir: 'manageReports/css', file: 'manageReports.css']
        resource id: 'manageReportsJs',
            url: [plugin: "metridocCore", dir: 'manageReports/js', file: 'manageReports.js']
    }

    login {
        dependsOn: 'jquery-ui'
        resource id: 'loginCss',
            url: [plugin: "metridocCore", dir: 'auth/css', file: 'login.css']
    }
}