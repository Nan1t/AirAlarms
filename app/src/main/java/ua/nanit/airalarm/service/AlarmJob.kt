package ua.nanit.airalarm.service

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobWorkItem

class AlarmJob : JobScheduler() {

    override fun schedule(p0: JobInfo): Int {
        TODO("Not yet implemented")
    }

    override fun enqueue(p0: JobInfo, p1: JobWorkItem): Int {
        TODO("Not yet implemented")
    }

    override fun cancel(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun cancelAll() {
        TODO("Not yet implemented")
    }

    override fun getAllPendingJobs(): MutableList<JobInfo> {
        TODO("Not yet implemented")
    }

    override fun getPendingJob(p0: Int): JobInfo? {
        TODO("Not yet implemented")
    }
}