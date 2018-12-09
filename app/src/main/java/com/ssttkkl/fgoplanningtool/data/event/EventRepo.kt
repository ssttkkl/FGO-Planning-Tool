package com.ssttkkl.fgoplanningtool.data.event

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ssttkkl.fgoplanningtool.data.HowToPerform
import com.ssttkkl.fgoplanningtool.data.RepoDatabase
import com.ssttkkl.fgoplanningtool.data.perform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap


class EventRepo(private val database: RepoDatabase) : Observer<Any> {
    private val cache = ConcurrentHashMap<String, Event>()

    val liveData = MutableLiveData<Collection<Event>>()

    init {
        database.normalEventsDao.allLiveData.observeForever(this@EventRepo)
        database.lotteryEventsDao.allLiveData.observeForever(this@EventRepo)
    }

    override fun onChanged(t: Any?) {
        GlobalScope.launch(Dispatchers.IO) {
            synchronized(cache) {
                cache.clear()
                cache.putAll(database.normalEventsDao.all.associate { it.codename to it })
                cache.putAll(database.lotteryEventsDao.all.associate { it.codename to it })
                liveData.postValue(all)
            }
        }
    }

    val all: Collection<Event>
        get() = cache.values

    operator fun get(codename: String): Event? {
        return cache[codename] ?: runBlocking(Dispatchers.IO) {
            database.normalEventsDao.getByCodename(codename)
                    ?: database.lotteryEventsDao.getByCodename(codename)
        }
    }

    fun insert(events: Collection<Event>, howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        perform(howToPerform) {
            database.normalEventsDao.insert(events.mapNotNull { it as? NormalEvent })
            database.lotteryEventsDao.insert(events.mapNotNull { it as? LotteryEvent })
        }
    }

    fun remove(codenames: Collection<String>, howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        perform(howToPerform) {
            codenames.forEach { codename ->
                val event = get(codename)
                when (event) {
                    is NormalEvent -> database.normalEventsDao.remove(event)
                    is LotteryEvent -> database.lotteryEventsDao.remove(event)
                }
            }
        }
    }

    fun clear(howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        perform(howToPerform) {
            database.normalEventsDao.remove(database.normalEventsDao.all)
            database.lotteryEventsDao.remove(database.lotteryEventsDao.all)
        }
    }

    fun insert(event: Event, howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        insert(listOf(event), howToPerform)
    }

    fun remove(codename: String, howToPerform: HowToPerform = HowToPerform.RunBlocking) {
        remove(listOf(codename), howToPerform)
    }
}