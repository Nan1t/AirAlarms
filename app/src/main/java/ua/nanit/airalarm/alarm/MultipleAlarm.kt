package ua.nanit.airalarm.alarm

class MultipleAlarm(private vararg val alarms: Alarm): Alarm {

    override fun alarm() {
        alarms.forEach(Alarm::alarm)
    }

    override fun allClear() {
        alarms.forEach(Alarm::allClear)
    }

    override fun stop() {
        alarms.forEach(Alarm::stop)
    }
}
