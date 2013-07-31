package metridoc.core

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder

import java.util.concurrent.TimeUnit

class RestService {

    Cache<String, String> restCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build()

    def addToRestCache(key, name) {
        restCache.put(key, name)
    }

    def getFromRestCache(key) {
        if (key) return restCache.getIfPresent(key)
        else return null
    }

    def hasCommonRoles(userRoles, controllerRoles) {
        for (ur in userRoles) {
            for (cr in controllerRoles) {
                if (ur == cr) return true
            }
        }
        return false
    }
}
