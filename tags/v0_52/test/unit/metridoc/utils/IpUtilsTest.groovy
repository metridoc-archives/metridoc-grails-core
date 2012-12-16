package metridoc.utils

/**
 * Created with IntelliJ IDEA.
 * User: weizhuowu
 * Date: 9/7/12
 * Time: 10:42 AM
 * To change this template use File | Settings | File Templates.
 */

import org.junit.Test

class IpUtilsTest {

    @Test
    void "test that an ip is an ip"() {
        assert IpUtils.isIp("1.1.1.1")
    }

    @Test(expected=RuntimeException.class)
    void "test whether a RuntimeException is thrown if Ip is invalid"() throws java.lang.RuntimeException{
        IpUtils.ip("111.11")
    }

    @Test
    void "test ip is between given two ips inclusively"() {
        assert IpUtils.ip("125.125.125.125").isBetween("1.200.200.200","200.1.1.1")
        assert !IpUtils.ip("1.200.200.200").isBetween("1.1.200.1", "1.200.200.1")
    }

}
