package com.augustin26.studyingistiming

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val config: RealmConfiguration = RealmConfiguration.Builder()
            .name("studyDB.realm") // 생성할 realm 파일 이름 지정
            .deleteRealmIfMigrationNeeded()
            .build()

        Realm.setDefaultConfiguration(config)
    }
}