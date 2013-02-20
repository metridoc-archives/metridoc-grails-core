package metridoc.core

import metridoc.trigger.Trigger
import org.quartz.CronScheduleBuilder
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerKey

import static metridoc.trigger.Trigger.EVERY_MINUTE

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/18/13
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
class JobSchedule {
    String triggerName
    Trigger triggerType

    static constraints = {
        triggerName(unique: true)
    }

    org.quartz.Trigger convertTriggerToQuartzTrigger() {

        switch(triggerType) {
            case Trigger.NEVER:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMinutes(1).build()
            case EVERY_MINUTE:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMinutes(1).build()
            case Trigger.EVERY_5_MINUTES:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMinutes(5).build()
            case Trigger.EVERY_10_MINUTES:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMinutes(10).build()
            case Trigger.EVERY_15_MINUTES:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMinutes(15).build()
            case Trigger.EVERY_30_MINUTES:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMinutes(30).build()
            case Trigger.EVERY_HOUR:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInHours(1).build()
            case Trigger.EVERY_6_HOURS:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInHours(6).build()
            case Trigger.EVERY_12_HOURS:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInHours(12).build()
            case Trigger.EVERY_DAY:
                return SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInHours(24).build()
            case Trigger.MIDNIGHT:
                return CronScheduleBuilder.cronSchedule("0 0 0 * * ?").build()
            case Trigger.TIME_1_00:
                return CronScheduleBuilder.cronSchedule("0 0 1 * * ?").build()
            case Trigger.TIME_2_00:
                return CronScheduleBuilder.cronSchedule("0 0 2 * * ?").build()
            case Trigger.TIME_3_00:
                return CronScheduleBuilder.cronSchedule("0 0 3 * * ?").build()
            case Trigger.TIME_4_00:
                return CronScheduleBuilder.cronSchedule("0 0 4 * * ?").build()
            case Trigger.TIME_5_00:
                return CronScheduleBuilder.cronSchedule("0 0 5 * * ?").build()
            case Trigger.TIME_6_00:
                return CronScheduleBuilder.cronSchedule("0 0 6 * * ?").build()
            case Trigger.TIME_7_00:
                return CronScheduleBuilder.cronSchedule("0 0 7 * * ?").build()
            case Trigger.TIME_8_00:
                return CronScheduleBuilder.cronSchedule("0 0 8 * * ?").build()
            case Trigger.TIME_9_00:
                return CronScheduleBuilder.cronSchedule("0 0 9 * * ?").build()
            case Trigger.TIME_10_00:
                return CronScheduleBuilder.cronSchedule("0 0 10 * * ?").build()
            case Trigger.TIME_11_00:
                return CronScheduleBuilder.cronSchedule("0 0 11 * * ?").build()
            case Trigger.TIME_12_00:
                return CronScheduleBuilder.cronSchedule("0 0 12 * * ?").build()
            case Trigger.TIME_13_00:
                return CronScheduleBuilder.cronSchedule("0 0 13 * * ?").build()
            case Trigger.TIME_14_00:
                return CronScheduleBuilder.cronSchedule("0 0 14 * * ?").build()
            case Trigger.TIME_15_00:
                return CronScheduleBuilder.cronSchedule("0 0 15 * * ?").build()
            case Trigger.TIME_16_00:
                return CronScheduleBuilder.cronSchedule("0 0 16 * * ?").build()
            case Trigger.TIME_17_00:
                return CronScheduleBuilder.cronSchedule("0 0 17 * * ?").build()
            case Trigger.TIME_18_00:
                return CronScheduleBuilder.cronSchedule("0 0 18 * * ?").build()
            case Trigger.TIME_19_00:
                return CronScheduleBuilder.cronSchedule("0 0 19 * * ?").build()
            case Trigger.TIME_20_00:
                return CronScheduleBuilder.cronSchedule("0 0 20 * * ?").build()
            case Trigger.TIME_21_00:
                return CronScheduleBuilder.cronSchedule("0 0 21 * * ?").build()
            case Trigger.TIME_22_00:
                return CronScheduleBuilder.cronSchedule("0 0 22 * * ?").build()
            case Trigger.TIME_23_00:
                return CronScheduleBuilder.cronSchedule("0 0 23 * * ?").build()
            default:
                throw new IllegalArgumentException("$triggerType is not supportted")
        }
    }

}
