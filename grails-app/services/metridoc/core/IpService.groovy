package metridoc.core

class IpService {

    /**
     * tests if an ip address is within a range.  The range is inclusive, so low and hi ips are included
     *
     * @param low low ip
     * @param hi hi ip
     * @param ipToCheck ip to check
     * @return whether ipToCheck is in range or not
     */
    def ipInRange(low, hi, ipToCheck) {
        checkIps(low, hi, ipToCheck)

    }

    def ipToLong(ip) {

    }

    def isIp(ip) {
        ip ==~ /\b(?:\d{1,3}\.){3}\d{1,3}\b/
    }

    def checkIps(Object[] args) {
        args.each {
            assert isIp(it) : "$it is not an ip address, must be in the form xxx.xxxx.xxx.xxx"
        }
    }
}
