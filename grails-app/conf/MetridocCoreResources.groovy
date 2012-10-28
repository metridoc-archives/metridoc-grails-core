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

    profile {
        dependsOn 'jquery-ui'
        resource id: 'css',
            url: [plugin: "metridocCore", dir: 'profile/css', file: 'profile.css'],
            attrs: [type: 'css']
        resource id: 'js',
            url: [plugin: "metridocCore", dir: 'profile/js', file: 'profile.js'],
            attrs: [type: 'js']
    }

    user {
        dependsOn 'jquery-ui'
        resource id: 'css',
            url: [plugin: "metridocCore", dir: 'user/css', file: 'user.css'],
            attrs: [type: 'css']
        resource id: 'js',
            url: [plugin: "metridocCore", dir: 'user/js', file: 'user.js'],
            attrs: [type: 'js']
    }

    role {
        dependsOn 'jquery-ui'
        resource id: 'css',
            url: [plugin: "metridocCore", dir: 'role/css', file: 'role.css'],
            attrs: [type: 'css']
        resource id: 'js',
            url: [plugin: "metridocCore", dir: 'role/js', file: 'role.js'],
            attrs: [type: 'js']
    }

    application {
        dependsOn 'jquery-ui', 'jquery'
        resource id: 'appJs',
            url: [plugin: "metridocCore", dir: 'js', file: 'application.js'],
            attrs: [type: 'js']
        resource id: 'mobileCss',
            url: [plugin: "metridocCore", dir: 'css', file: 'mobile.css'],
            attrs: [type: 'css']
        resource id: 'mainCss',
            url: [plugin: "metridocCore", dir: 'css', file: 'main.css'],
            attrs: [type: 'css']
        resource id: 'navBarCss',
            url: [plugin: "metridocCore", dir: 'css', file: 'navBar.css'],
            attrs: [type: 'css']
        resource id: 'faviconIco',
            url: [plugin: "metridocCore", dir: 'images', file: 'favicon.ico']

    }

    admin {
        dependsOn 'jquery-ui'
        resource id: 'adminCss',
            url: [plugin: "metridocCore", dir: 'admin/css', file: 'admin.css'],
            attrs: [type: 'css']
        resource id: 'adminJs',
            url: [plugin: "metridocCore", dir: 'admin/js', file: 'admin.js'],
            attrs: [type: 'js']
    }

    overrides {
        'jquery-theme' {
            resource id: 'theme',
                url: [plugin: "metridocCore", dir: '/jquery-ui/forrest-dialog/css/custom-theme', file: 'jquery-ui-1.8.23.custom.css']
        }
    }

    manageReports {
        dependsOn 'jquery-ui'
        resource id: 'manageReportsCss',
            url: [plugin: "metridocCore", dir: 'manageReports/css', file: 'manageReports.css'],
            attrs: [type: 'css']
        resource id: 'manageReportsJs',
            url: [plugin: "metridocCore", dir: 'manageReports/js', file: 'manageReports.js'],
            attrs: [type: 'js']
    }

    login {
        dependsOn 'jquery-ui'
        resource id: 'loginCss',
            url: [plugin: "metridocCore", dir: 'auth/css', file: 'login.css'],
            attrs: [type: 'css']
    }

    status {
        dependsOn 'jquery-ui'
    }

    quartz {
        dependsOn 'jquery-ui'
        resource id: 'quartzJs',
            url: [plugin: "metridocCore", dir: "quartz/js", file: 'quartz.js'],
            attrs: [type: 'js']
    }

    log {
        dependsOn 'jquery-ui'
        resource id: 'css', disposition: 'head',
            url: [plugin: "metridocCore", dir: 'log/css', file: 'log.css'],
            attrs: [type: 'css']
        resource id: 'js', disposition: 'head',
            url: [plugin: "metridocCore", dir: 'log/js', file: 'log.js'],
            attrs: [type: 'js']
    }

    accessInfo {
        dependsOn 'jquery-ui'
        resource id: 'js', disposition: 'head',
            url: [plugin: "metridocCore", dir: 'accessInfo', file: 'accessInfo.js'],
            attrs: [type: 'js']
    }
}