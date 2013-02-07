package metridoc.core

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/7/13
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */
class JobInteruptionException extends Exception{

    JobInteruptionException(String jobName) {
        super("Job ${jobName} was manually interrupted")
    }
}
