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
package metridoc.camel.routing

import com.sun.net.httpserver.HttpServer
import java.util.concurrent.TimeUnit
import metridoc.dsl.JobBuilder
import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.Processor
import org.apache.camel.component.file.GenericFile
import org.apache.camel.component.mock.MockEndpoint
import org.junit.Before
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/4/11
 * Time: 10:25 PM
 */
class RoutingTest {

    def static mixinPerformed = false
    def static boolean doFooCalled = false
    def static boolean doBarCalled = false
    def static boolean fooClosureCalled = false;
    def static boolean fooCalled = false
    HttpServer httpServer
    private String baseDir

    def static reset() {
        doBarCalled = false
        doFooCalled = false
        fooClosureCalled = false
    }

    @Before
    def void setup() {
        baseDir = "target/test-classes/testFiles/metridoc/camel/component/poll/jobBuilderTest"
        if (!new File("${baseDir}/file1").exists()) {
            baseDir = "plugins/metridoc-camel-plugin/${baseDir}"
            assert new File("${baseDir}/file1").exists()
        }
    }

    @Test
    def void testBasicRouteCall() {
        JobBuilder.run("toMock") {
            job("toMock") {
                def mock = getEndpoint("mock:endBasic")
                mock.reset()
                mock.expectedMessageCount(1)
                runRoute "mock:endBasic"
                mock.assertIsSatisfied()
            }
        }

    }

    @Test
    def void testFullRoute() {
        JobBuilder.run("fooRoute") {
            job("fooRoute") {
                def mock = getEndpoint("mock:endFull")
                mock.reset()
                runRoute {
                    def baseDir = "target/test-classes/testFiles/metridoc/camel/component/poll/jobBuilderTest"
                    if (!new File("${baseDir}/file1").exists()) {
                        baseDir = "plugins/metridoc-camel-plugin/${baseDir}"
                    }
                    mock.expectedMessageCount(1)
                    from("file://${baseDir}?noop=true&initialDelay=0").threads(4).aggregateBody(4, 2000).to("mock:endFull")

                }
                mock.assertIsSatisfied()
            }
        }

    }

    @Test
    def void testDirectRouteWithNoShutdown() {
        def jobs = {

            job("loadJob") {
                runRoute {
                    from("direct:start").aggregateBody().to("mock:endDirect")
                    runRoute "direct:start", "a"
                    runRoute "direct:start", "b"
                    testJob "main", jobs, "mock:endDirect", 1
                }
            }

            job("main") {
                runJobs("loadJob")
            }

        }
    }

    @Test
    def void testMultipleJobs() {
        JobBuilder.run("runAll") {
            job("runAll") {
                def mock = getEndpoint("mock:endMult")
                mock.reset()
                mock.expectedMessageCount(2)
                loadProperties "testFiles/metridoc/dsl/properties/foo" //loads mock.end from foo-test.properties
                runJobs "foo", "bar"
                mock.assertIsSatisfied()
            }

            job("foo") {
                runRoute mock.end
            }

            job("bar") {
                runRoute mock.end
            }
        }
    }

    @Test
    def void testPollingFromEmptyDirectory() {
        JobBuilder.run("pollFiles") {
            pollFiles {
                MockEndpoint mock = getEndpoint("mock:endPoll")
                mock.reset()
                runRoute {
                    def uri = "file:${baseDir}/nothing?noop=true&maxMessages=2"
                    from(uri).to("mock:endPoll")
                }
                mock.await(1, TimeUnit.SECONDS)
                assert mock.exchanges.size() == 0
            }
        }
    }

    @Test
    def void testPollingFiles() {
        JobBuilder.run("pollFiles") {
            pollFiles {

                MockEndpoint mock = getEndpoint("mock:endPolling")
                mock.reset()
                mock.whenAnyExchangeReceived(
                    new Processor() {
                        void process(Exchange exchange) {
                            assert exchange.in.body instanceof GenericFile
                        }
                    }
                )
                mock.expectedMessageCount(2)

                runRoute {
                    def uri = "file:${baseDir}?noop=true&maxMessages=2"
                    from(uri).to("mock:endPolling")
                }

                mock.assertIsSatisfied()
            }
        }
    }

    @Test
    def void testSplitterExceptions() {

        def originalOut = System.out
        System.out = new PrintStream(new ByteArrayOutputStream())

        JobBuilder.run("runAll") {

            runAll {
                runJobs "setup", "run"
            }

            setup {
                routeBuilder = runRoute(shutdown: false) {
                    errorHandler(loggingErrorHandler().level(LoggingLevel.DEBUG))
                    from("direct:start").split(bean(ErrorIterator.class, "create")).to("mock:end")
                }
            }

            run {

                try {
                    runRoute "direct:start"
                    assert false: "exception should have occurred"
                } catch (Exception ex) {}


                assert routeBuilder.firstException
            }
        }

        System.out = originalOut
    }

    @Test
    def void testSedaBasedRouting() {
        JobBuilder.run {
            defaultJob {
                MockEndpoint mock = getEndpoint("mock:endSeda")
                mock.reset()
                mock.expectedMessageCount(1)
                runJobs "createRoute"
                runRoute "seda:testSedaRouting"
                mock.assertIsSatisfied()
            }

            createRoute {
                runRoute {
                    from("seda:testSedaRouting").to("mock:endSeda")
                }
            }
        }
    }

    @Test
    def void testPossibleThreadLockWithAggregatorAndSplitters() {
        JobBuilder.run {
            defaultJob {
                MockEndpoint mock = getEndpoint("mock:endAggSplit")
                mock.reset()
                mock.expectedMessageCount(1)
                runJobs "createRoute"
                runRoute "direct:testThreadLock", ["first", "second", "third"]
                mock.assertIsSatisfied()
            }

            createRoute {
                runRoute {
                    from("direct:processSplit").aggregateBody().to("mock:endAggSplit")
                    from("direct:testThreadLock").split(body()).parallelProcessing().to("direct:processSplit")
                }
            }
        }
    }

    static class ErrorIterator implements Iterator {
        boolean hasNext() {
            throw new RuntimeException("error in has next")
        }

        Object next() {
            throw new RuntimeException("error in next")
        }

        void remove() {
            throw new RuntimeException("error in remove")
        }

        def ErrorIterator create(Exchange exchange) {
            return new ErrorIterator()
        }

    }
}
