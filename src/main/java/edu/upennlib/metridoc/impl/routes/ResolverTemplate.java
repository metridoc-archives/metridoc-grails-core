/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.routes;

import edu.upennlib.metridoc.routes.MetridocRouteTemplate;

/**
 *
 * @author tbarker
 */
public class ResolverTemplate extends MetridocRouteTemplate{

    public String[] resolvers;

    public String[] getResolvers() {
        return resolvers;
    }

    public void setResolvers(String[] resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public void route() throws Exception {
        from(getStartFrom()).multicast(new BodyAggregator()).to(resolvers).end().to(getEndTo());
    }

    public ResolverTemplate resolvers(String... resolvers) {
        this.resolvers = resolvers;
        return this;
    }

}
