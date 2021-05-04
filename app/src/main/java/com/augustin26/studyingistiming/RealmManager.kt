package com.augustin26.studyingistiming

import android.util.Log
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Dday : RealmObject() {
    @PrimaryKey
    var id: Int = 1
    var day: Long = 0
}

open class DdayContent : RealmObject() {
    @PrimaryKey
    var id: Int = 1
    var content: String = "디데이"
}

class RealmManager(val realm: Realm) {

    fun findDay(id: Int): Dday? {
        realm.beginTransaction()
        val result = realm.where(Dday::class.java).equalTo("id", id).findFirst()
        realm.commitTransaction()
        return result
    }

    fun findContent(id: Int): DdayContent? {
        return realm.where(DdayContent::class.java).equalTo("id", id).findFirst()
    }

    fun updateDay(id: Int, curdata: Dday) {
        try {
            realm.beginTransaction()

            val data = findDay(id)
            data?.day = curdata.day

            realm.commitTransaction()
        }catch (e:Exception) {
            Log.e("realm","updateDay error")
        }
    }

    fun updateContent(id: Int, curdata: DdayContent) {
        try {
            realm.beginTransaction()

            val data = findContent(id)
            data?.content = curdata.content

            realm.commitTransaction()
        }catch (e:Exception) {
            Log.e("realm","updateContent error")
        }
    }
}