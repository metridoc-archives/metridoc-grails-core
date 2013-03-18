includeTargets << new File(metridocCorePluginDir, "scripts/_UploadAllArtefacts.groovy")

target(main: "Uploads all maven artefacts, includes plugin, binary plugin, source files and javadoc files") {
    depends(uploadSource)
}

setDefaultTarget(main)
