package com.example.webviewsample.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.webviewsample.SingletonHolder
import net.sqlcipher.database.SupportFactory

@Database(entities = [Code::class], version = 1, exportSchema = false)
@TypeConverters(TimestampConverter::class)
abstract class MyAppDatabase : RoomDatabase() {

    abstract fun codeDao(): CodeDao?

    companion object : SingletonHolder<MyAppDatabase, Context>({

        val b = byteArrayOf(112, 97, 115, 115, 119, 111, 114, 100)

        Room.databaseBuilder(it.applicationContext, MyAppDatabase::class.java, "skylarkDb")
                .createFromAsset("databases/userDb.db")
                .fallbackToDestructiveMigration() //이전 DB 삭제 하고 새로 생성
                .allowMainThreadQueries() // 메인쓰레드에서 쿼리 허용
                .openHelperFactory(SupportFactory(b))
                .build()

    })

}