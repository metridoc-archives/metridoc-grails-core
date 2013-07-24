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

    restartRunner {
        resource id: 'js',
                url: [dir: "restart", file: "restart.js", plugin: "metridocCore"]
    }

    codeMirror {
        resource id: 'coreJs',
                url: [dir: "components/codemirror/lib", file: "codemirror.js", plugin: "metridocCore"]
        resource id: 'coreCss',
                url: [dir: "components/codemirror/lib", file: "codemirror.css", plugin: "metridocCore"]
        resource id: 'addOnMatchBrackets',
                url: [dir: "components/codemirror/addon/edit", file: "matchbrackets.js", plugin: "metridocCore"]
        resource id: 'codeWindowCss',
                url: [dir: "codeMirror", file: "codeMirror.css", plugin: "metridocCore"]
    }

    codeMirrorShell {
        dependsOn 'codeMirror'
        resource id: 'shellMode',
                url: [dir: "components/codemirror/mode/shell", file: "shell.js", plugin: "metridocCore"]
        resource id: 'activateShell',
                url: [dir: "shell", file: "shell.js", plugin: "metridocCore"]
    }

    codeMirrorGroovy {
        dependsOn 'codeMirror'
        resource id: 'groovyMode',
                url: [dir: "components/codemirror/mode/groovy", file: "groovy.js", plugin: "metridocCore"]
        resource id: 'activateShell',
                url: [dir: "groovy", file: "groovy.js", plugin: "metridocCore"]
    }

    home {
        dependsOn 'jquery, font-awesome'
        resource id: 'js',
                url: [plugin: "metridocCore", dir: 'home/js', file: 'home.js'],
                attrs: [type: 'js']
    }

    manageConfig {
        dependsOn 'codeMirrorGroovy'
        resource id: 'js',
                url: [dir: "manageConfig", file: "manageConfig.js", plugin: "metridocCore"],
                attrs: [type: "js"]
    }

    manageReport {
        dependsOn 'jquery, bootstrap, font-awesome'
        resource id: 'css',
                url: [plugin: "metridocCore", dir: 'manageReport/css', file: 'manageReport.css'],
                attrs: [type: 'css']
        resource id: 'js',
                url: [plugin: "metridocCore", dir: 'manageReport/js', file: 'manageReport.js'],
                attrs: [type: 'js']

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
        dependsOn 'jquery, bootstrap, font-awesome'
        resource id: 'appJs',
                url: [plugin: "metridocCore", dir: 'js', file: 'application.js'],
                attrs: [type: 'js']
        resource id: 'faviconIco',
                url: [plugin: "metridocCore", dir: 'images', file: 'favicon.ico']
        resource id: 'mainCss',
                url: [plugin: "metridocCore", dir: 'css', file: 'main.css'],
                attrs: [type: 'css']
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