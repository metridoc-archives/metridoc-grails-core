package testFiles.metridoc.utils

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 5/30/11
 * Time: 2:31 PM
 */

environments {
    test {
        foobar = "bar"
        metridoc {
            override = true
        }
    }

    development {
        foobar = "baz"
    }
}

metridoc {
    test {
        baz = "from foo.groovy"
        foobar = "foobar from foo.groovy"
    }

    override = false
}

