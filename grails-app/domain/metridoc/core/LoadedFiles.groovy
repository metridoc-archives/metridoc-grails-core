package metridoc.core

class LoadedFiles {

    boolean loaded
    boolean processed
    String sha256
    Byte[] documentContent
    String fileType
    String fileGroup
    String fileName
    Date documentDate
    Date loadingDate

    static constraints = {
        sha256 size: (32..32)
        fileName maxSize: 256
        fileType maxSize:  50
        fileGroup maxSize: 256
        documentContent nullable: true
        documentDate nullable: true
    }
}
