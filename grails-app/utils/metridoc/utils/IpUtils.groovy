package metridoc.utils
/**
 *

 * This was added in hopes of doing some ip authentication.  Should use an outside appliance for that instead
  @deprecated
 */
class IpUtils {

    long  ip;

    static def isIp(String ip) {
        ip ==~ /\b(?:\d{1,3}\.){3}\d{1,3}\b/
    }


    static IpUtils ip(String ip){
        if(!isIp(ip)){
            throw new RuntimeException("Given Ip is not a valid IpAddress")
        }
        Scanner sc = new Scanner(ip).useDelimiter("\\.");
        long ipInLong = (sc.nextLong() << 24) +(sc.nextLong() << 16) +(sc.nextLong() << 8) +(sc.nextLong());
        new IpUtils(ip:ipInLong);
    }

    boolean isBetween(String ipMin, String ipMax){
        if(!isIp(ipMin)||!isIp(ipMax)){
            throw new RuntimeException("Given Ip is not a valid IpAddress")
        }
        return (this.ip >= ip(ipMin).getIp())&&(this.ip <= ip(ipMax).getIp())
    }


}