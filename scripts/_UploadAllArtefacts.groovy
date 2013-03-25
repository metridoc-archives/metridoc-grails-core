import org.apache.ivy.util.ChecksumHelper

includeTargets << new File("${releasePluginDir}/scripts/_GrailsMaven.groovy")

target(uploadZipPlugin: "The description of the script goes here!") {

}

target(uploadBinaryPlugin: "The description of the script goes here!") {
    // TODO: Implement script here
}

target(uploadSource: "The description of the script goes here!") {

    depends(init) //calls init target from _GrailsMaven.groovy
    int indexOfZip = pluginZip.lastIndexOf(".zip")
    String sourceFile = pluginZip.substring(0, indexOfZip) + "-sources.jar"
    pluginInfo.packaging = "binary"
    generatePom()
    ant.copy(file: pluginZip, toFile: sourceFile)
    grailsConsole.info "using pom file: $pomFileLocation"
    artifact.mvn {
        arg(value: "gpg:sign-and-deploy-file")
        arg(value: "-DrepositoryId=localTargetRepo")
        arg(value: "-Durl=file://${projectTargetDir}/repo")
        arg(value: "-Dpom=$pomFileLocation")
        arg(value: "-Dfile=$sourceFile")
        arg(value: "-Dclassifier=sources")
        arg(value: "-Dgpg.passphrase=metridoc")

    }
}

target(uploadJavadoc: "The description of the script goes here!") {
    // TODO: Implement script here
}



private installOrDeploy(File file, ext, boolean deploy, String classifier, repos = [:]) {
    if (!deploy) {
        ant.checksum file: pomFileLocation, algorithm: "sha1", todir: projectTargetDir
        ant.checksum file: file, algorithm: "sha1", todir: projectTargetDir
    }

    grailsConsole.info "printing pom file location"
    grailsConsole.info pomFileLocation
    def pomCheck = generateChecksum(new File(pomFileLocation))
    def fileCheck = generateChecksum(file)

    artifact."${ deploy ? 'deploy' : 'install' }"(file: file) {
        if (isPlugin) {
            attach file: "${basedir}/plugin.xml", type: "xml", classifier: classifier
        }

        if (!deploy) {
            attach file: "${projectTargetDir}/pom.xml.sha1", type: "pom.sha1"
            attach file: "${projectTargetDir}/${file.name}.sha1", type: "${ext}.sha1"
        }

        pom(file: pomFileLocation)
        if (repos.remote) {
            def repo = repos.remote
            if (repo.configurer) {
                remoteRepository(repo.args, repo.configurer)
            } else {
                remoteRepository(repo.args)
            }
        }
        if (repos.local) {
            localRepository(path: repos.local)
        }

    }
}

private generateChecksum(File file) {
    def checksum = new File("${file.parentFile.absolutePath}/${file.name}.sha1")
    checksum.write ChecksumHelper.computeAsString(file, "sha1")
    return checksum
}