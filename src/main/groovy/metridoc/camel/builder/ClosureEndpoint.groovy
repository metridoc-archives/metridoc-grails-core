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
package metridoc.camel.builder

import org.apache.camel.impl.DefaultEndpoint
import org.apache.camel.Producer
import org.apache.camel.Consumer
import org.apache.camel.Processor
import org.apache.camel.Endpoint
import org.apache.camel.impl.DefaultProducer
import groovy.util.logging.Slf4j
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.Exchange

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 1/4/12
 * Time: 2:25 PM
*/
class ClosureEndpoint extends DefaultEndpoint{
    Closure closure
    
    Producer createProducer() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    Consumer createConsumer(Processor processor) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    boolean isSingleton() {
        return false  //To change body of implemented methods use File | Settings | File Templates.
    }
}

@Slf4j
class ClosureConsumer implements Consumer {

    Closure consumerClosure
    Endpoint endpoint
    Processor processor
    
    void start() {
        def clone = consumerClosure.clone()
        clone.delegate = new ClosureConsumerDelegate()
        clone.resolveStrategy = Closure.DELEGATE_FIRST
        def result = clone()

        if(result instanceof Iterable && clone.delegate.iterate) {
            result.each {
                handleExchange(it)
            }
        } else {
            handleExchange(result)
        }
    }

    private void handleExchange(body) {
        def exchange = new DefaultExchange(endpoint.camelContext)
        exchange.in.setBody(body)
        processor.process(exchange)
    }

    void stop() {
        //do nothing
    }
}

class ClosureProducer extends DefaultProducer {

    Closure producerClosure
    
    ClosureProducer(Endpoint endpoint) {
        super(endpoint)
    }

    void process(Exchange exchange) {
        Closure clone = producerClosure.clone()
        clone.delegate = new ClosureProducerDelegate(exchange: exchange)
        clone.setResolveStrategy(Closure.DELEGATE_FIRST)
        clone()
    }
}

class ClosureConsumerDelegate {
    boolean iterate = true
}

class ClosureProducerDelegate {
    Exchange exchange
}