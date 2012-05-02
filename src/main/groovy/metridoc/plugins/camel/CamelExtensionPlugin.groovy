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
package metridoc.plugins.camel

import metridoc.camel.processor.ClosureProcessor
import metridoc.plugins.Plugin
import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.language.groovy.CamelGroovyMethods
import org.apache.camel.model.FilterDefinition
import org.apache.camel.model.ChoiceDefinition
import metridoc.camel.impl.iterator.LineIteratorCreator
import org.apache.camel.builder.ExpressionBuilder
import org.apache.camel.Expression
import org.apache.camel.model.SplitDefinition
import metridoc.camel.impl.iterator.ExcelIteratorCreator
import org.apache.camel.model.AggregateDefinition
import metridoc.camel.aggregator.BodyAggregator
import metridoc.camel.aggregator.InflightAggregationWrapper

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/12/11
 * Time: 10:12 AM
 */
@Plugin(category = "camel")
class CamelExtensionPlugin {
    static ProcessorDefinition process(ProcessorDefinition self, Closure process) {
        return self.process(new ClosureProcessor(process))
    }

    static FilterDefinition filter(ProcessorDefinition self, Closure filter) {
        return CamelGroovyMethods.filter(self, filter)
    }

    static ChoiceDefinition when(ChoiceDefinition self, Closure filter) {
        return CamelGroovyMethods.when(self, filter)
    }

    static SplitDefinition splitByLine(ProcessorDefinition self) {
        Expression bean = ExpressionBuilder.beanExpression(LineIteratorCreator.class, "create");
        return self.split(bean).streaming()
    }

    /**
     * @deprecated this will eventually be replaced by the iterator plugin
     * @param self route definition
     * @return
     */
    static SplitDefinition splitByXlsRecord(ProcessorDefinition self) {
        Expression bean = ExpressionBuilder.beanExpression(ExcelIteratorCreator.class, "create");
        return self.split(bean).streaming()
    }

    static AggregateDefinition aggregateBody(ProcessorDefinition self) {
        def expression = ExpressionBuilder.constantExpression(true)
        return self.aggregate(expression, new InflightAggregationWrapper(new BodyAggregator())).completionSize(500).completionTimeout(500)
    }

    static AggregateDefinition aggregateBody(ProcessorDefinition self, int size) {
        def expression = ExpressionBuilder.constantExpression(true)
        return self.aggregate(expression, new InflightAggregationWrapper(new BodyAggregator())).completionSize(size).completionTimeout(500)
    }

    static AggregateDefinition aggregateBody(ProcessorDefinition self, int size, long timeout) {
        def expression = ExpressionBuilder.constantExpression(true)
        return self.aggregate(expression, new InflightAggregationWrapper(new BodyAggregator())).completionSize(size).completionTimeout(timeout)
    }

}
