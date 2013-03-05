package metridoc.core

import grails.util.Environment
import metridoc.utils.JobTrigger
import org.quartz.CronScheduleBuilder
import org.quartz.SimpleScheduleBuilder
import static metridoc.utils.JobTrigger.*

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 3/4/13
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */
class JobDetails {

    String config
    String template
    String jobName
    JobTrigger jobTrigger
    String cron
    String url
    String classToRun
    String description

    static mapping = {
        template(index: 'job_details_template_idx')
    }

    static constraints = {
        config(nullable: true)
        jobName(unique: true)
        cron(nullable: true)
        description(nullable: true, maxSize: Integer.MAX_VALUE)
        url(nullable: true)
        classToRun(nullable: true)
        config(
                nullable: true,
                maxSize: Integer.MAX_VALUE,
                validator: {
                    if (it) {
                        try {
                            new URL(it)
                            return true
                        } catch (MalformedURLException e) {
                        }
                        def exception = getConfigException(it)
                        if (exception) {
                            return "invalid.config"
                        }
                        return true
                    }

                    return true
                }
        )
    }

    org.quartz.Trigger convertTriggerToQuartzTrigger() {

        switch(jobTrigger) {
            case NEVER:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMinutes(1).build()
            case EVERY_MINUTE:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMinutes(1).build()
            case EVERY_5_MINUTES:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMinutes(5).build()
            case EVERY_10_MINUTES:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMinutes(10).build()
            case EVERY_15_MINUTES:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMinutes(15).build()
            case EVERY_30_MINUTES:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMinutes(30).build()
            case EVERY_HOUR:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInHours(1).build()
            case EVERY_6_HOURS:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInHours(6).build()
            case EVERY_12_HOURS:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInHours(12).build()
            case EVERY_DAY:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInHours(24).build()
            case MIDNIGHT:
                return CronScheduleBuilder.cronSchedule("0 0 0 * * ?").build()
            case TIME_1_00:
                return CronScheduleBuilder.cronSchedule("0 0 1 * * ?").build()
            case TIME_2_00:
                return CronScheduleBuilder.cronSchedule("0 0 2 * * ?").build()
            case TIME_3_00:
                return CronScheduleBuilder.cronSchedule("0 0 3 * * ?").build()
            case TIME_4_00:
                return CronScheduleBuilder.cronSchedule("0 0 4 * * ?").build()
            case TIME_5_00:
                return CronScheduleBuilder.cronSchedule("0 0 5 * * ?").build()
            case TIME_6_00:
                return CronScheduleBuilder.cronSchedule("0 0 6 * * ?").build()
            case TIME_7_00:
                return CronScheduleBuilder.cronSchedule("0 0 7 * * ?").build()
            case TIME_8_00:
                return CronScheduleBuilder.cronSchedule("0 0 8 * * ?").build()
            case TIME_9_00:
                return CronScheduleBuilder.cronSchedule("0 0 9 * * ?").build()
            case TIME_10_00:
                return CronScheduleBuilder.cronSchedule("0 0 10 * * ?").build()
            case TIME_11_00:
                return CronScheduleBuilder.cronSchedule("0 0 11 * * ?").build()
            case TIME_12_00:
                return CronScheduleBuilder.cronSchedule("0 0 12 * * ?").build()
            case TIME_13_00:
                return CronScheduleBuilder.cronSchedule("0 0 13 * * ?").build()
            case TIME_14_00:
                return CronScheduleBuilder.cronSchedule("0 0 14 * * ?").build()
            case TIME_15_00:
                return CronScheduleBuilder.cronSchedule("0 0 15 * * ?").build()
            case TIME_16_00:
                return CronScheduleBuilder.cronSchedule("0 0 16 * * ?").build()
            case TIME_17_00:
                return CronScheduleBuilder.cronSchedule("0 0 17 * * ?").build()
            case TIME_18_00:
                return CronScheduleBuilder.cronSchedule("0 0 18 * * ?").build()
            case TIME_19_00:
                return CronScheduleBuilder.cronSchedule("0 0 19 * * ?").build()
            case TIME_20_00:
                return CronScheduleBuilder.cronSchedule("0 0 20 * * ?").build()
            case TIME_21_00:
                return CronScheduleBuilder.cronSchedule("0 0 21 * * ?").build()
            case TIME_22_00:
                return CronScheduleBuilder.cronSchedule("0 0 22 * * ?").build()
            case TIME_23_00:
                return CronScheduleBuilder.cronSchedule("0 0 23 * * ?").build()
            default:
                throw new IllegalArgumentException("$jobTrigger is not supportted")
        }
    }

    static Throwable getConfigException(String config) {
        try {
            new ConfigSlurper().parse(config)
            return null
        } catch (Throwable e) {
            e.stackTrace.each {

            }
            return e
        }
    }

    boolean configIsAUrl() {
        if (config == null) return false

        try {
            new URL(config)
            return true
        } catch (MalformedURLException e) {
            return false
        }
    }

    ConfigObject generateConfigObject() {
        if (config) {
            def environment = Environment.current.name
            def slurper = new ConfigSlurper(environment)
            if (configIsAUrl()) {
                return slurper.parse(new URL(config))
            }
            def parsedConfig = slurper.parse(config)
            return parsedConfig
        }

        return null
    }
}
