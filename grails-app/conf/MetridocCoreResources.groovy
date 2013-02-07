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

    codeMirror {
        resource id: 'coreJs',
                url: [dir: "components/codemirror/lib", file: "codemirror.js", plugin: "metridocCore"],
                attrs: [type: "js"]
        resource id: 'coreCss',
                url: [dir: "components/codemirror/lib", file: "codemirror.css", plugin: "metridocCore"],
                attrs: [type: "css"]
        resource id: 'addOnMatchBrackets',
                url: [dir: "components/codemirror/addon/edit", file: "matchbrackets.js", plugin: "metridocCore"],
                attrs: [type: "js"]
    }

    codeMirrorGroovy {
        dependsOn 'codeMirror'
        resource id: 'addOnMatchBrackets',
                url: [dir: "components/codemirror/mode/groovy", file: "groovy.js", plugin: "metridocCore"],
                attrs: [type: "js"]
    }

    manageConfig {
        dependsOn 'codeMirrorGroovy'
        resource id: 'js',
                url: [dir: "manageConfig", file: "manageConfig.js", plugin: "metridocCore"],
                attrs: [type: "js"]
    }

    jquery {
        resource id: 'js',
                url: [plugin: "metridocCore", dir: "components/jquery", file: "jquery.js"],
                attrs: [type: "js"]
    }

    profile {
        dependsOn 'jquery'
        resource id: 'css',
                url: [plugin: "metridocCore", dir: 'profile/css', file: 'profile.css'],
                attrs: [type: 'css']
        resource id: 'js',
                url: [plugin: "metridocCore", dir: 'profile/js', file: 'profile.js'],
                attrs: [type: 'js']
    }

    user {
        dependsOn 'jquery'
        resource id: 'css',
                url: [plugin: "metridocCore", dir: 'user/css', file: 'user.css'],
                attrs: [type: 'css']
        resource id: 'js',
                url: [plugin: "metridocCore", dir: 'user/js', file: 'user.js'],
                attrs: [type: 'js']
    }

    role {
        dependsOn 'jquery'
        resource id: 'css',
                url: [plugin: "metridocCore", dir: 'role/css', file: 'role.css'],
                attrs: [type: 'css']
        resource id: 'js',
                url: [plugin: "metridocCore", dir: 'role/js', file: 'role.js'],
                attrs: [type: 'js']
    }

    application {
        dependsOn 'jquery'
        resource id: 'appJs',
                url: [plugin: "metridocCore", dir: 'js', file: 'application.js'],
                attrs: [type: 'js']
        resource id: 'faviconIco',
                url: [plugin: "metridocCore", dir: 'images', file: 'favicon.ico']
        resource id: 'bootStrapCss',
                url: [plugin: "metridocCore", dir: 'components/bootstrap.css/css', file: 'bootstrap.css'],
                attrs: [type: 'css']
        resource id: 'bootStrapResonsiveCss',
                url: [plugin: "metridocCore", dir: 'components/bootstrap.css/css', file: 'bootstrap-responsive.css'],
                attrs: [type: 'css']
        resource id: "bootStrapJs",
                url: [plugin: "metridocCore", dir: 'components/bootstrap.css/js', file: 'bootstrap.js'],
                attrs: [type: 'js']
        resource id: 'fontAwesome',
                url: [plugin: "metridocCore", dir: 'components/font-awesome/css', file: "font-awesome.css"],
                attrs: [type: 'css']
        resource id: 'mainCss',
                url: [plugin: "metridocCore", dir: 'css', file: 'main.css'],
                attrs: [type: 'css']
    }

    overrides {
        'jquery-theme' {
            resource id: 'theme',
                    url: [plugin: "metridocCore", dir: '/jquery-ui/forrest-dialog/css/custom-theme', file: 'jquery-ui-1.8.23.custom.css']
        }
    }

    login {
        dependsOn 'jquery'
        resource id: 'loginCss',
                url: [plugin: "metridocCore", dir: 'auth/css', file: 'login.css'],
                attrs: [type: 'css']
    }

    status {
        dependsOn 'jquery'
    }

    quartz {
        dependsOn 'jquery'
        resource id: 'quartzMonitorJs',
                url: [plugin: "metridocCore", dir: "quartz/js", file: 'quartz-monitor.js'],
                attrs: [type: 'js']
        resource id: 'quartzCountdownJs',
                url: [plugin: "metridocCore", dir: "quartz/js", file: 'jquery.countdown.js'],
                attrs: [type: 'js']
        resource id: 'quartzColorJs',
                url: [plugin: "metridocCore", dir: "quartz/js", file: 'jquery.color.js'],
                attrs: [type: 'js']
        resource id: 'quartzClockJs',
                url: [plugin: "metridocCore", dir: "quartz/js", file: 'jquery.clock.js'],
                attrs: [type: 'js']
        resource id: 'quartzClockCss',
                url: [plugin: "metridocCore", dir: "quartz/css", file: 'jquery.clock.css'],
                attrs: [type: 'css']
        resource id: 'quartzCountdownCss',
                url: [plugin: "metridocCore", dir: "quartz/css", file: 'jquery.countdown.css'],
                attrs: [type: 'css']
        resource id: 'quartzMonitorCss',
                url: [plugin: "metridocCore", dir: "quartz/css", file: 'quartz-monitor.css'],
                attrs: [type: 'css']
    }

    log {
        dependsOn 'jquery'
        resource id: 'css',
                url: [plugin: "metridocCore", dir: 'log/css', file: 'log.css'],
                attrs: [type: 'css']
        resource id: 'js',
                url: [plugin: "metridocCore", dir: 'log/js', file: 'log.js'],
                attrs: [type: 'js']
    }
}