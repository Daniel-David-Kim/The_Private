package com.example.theprivate

import java.io.*
import android.content.*
import android.database.*
import android.database.sqlite.*

class AlarmDB(var context:Context, var user_id:String) : SQLiteOpenHelper(context, "private", null, 1) {
    override fun onCreate(db:SQLiteDatabase?) {
        db!!.execSQL("create table if not exists ${user_id}_Alarm(id integer not null primary key autoincrement," +
                          "writeDate text not null, content text not null)")
    }
    override fun onUpgrade(db:SQLiteDatabase?, prevVersion:Int, curVersion:Int) {
        db!!.execSQL("drop table if exists ${user_id}_Alarm")
        onCreate(db)
    }
}

class AlarmDAO(var context:Context, var user_id:String) {
    lateinit var helper:AlarmDB
    init{ helper = AlarmDB(context, user_id) }

    fun getAllAlarmMap():MutableMap<String, Any> {
        var result:MutableMap<String, Any> = mutableMapOf<String, Any>()
        var lists = mutableListOf<AlarmVO>()
        var reader = helper.readableDatabase
        var cursor = reader.rawQuery("select * from ${user_id}_Alarm order by id desc", null)
        var count:Int = 0
        while(cursor.moveToNext()) {
            var temp = AlarmVO(cursor.getInt(0), cursor.getString(1), cursor.getString(2))
            lists.add(temp)
            count++
        }
        cursor.close()
        reader.close()
        result.put("numOfRows", count)
        result.put("alarm", lists)
        return result
    }

    fun insertAlarm(data:AlarmVO):Long {
        var writer = helper.writableDatabase
        var stmt = writer.compileStatement("insert into ${user_id}_Alarm (writeDate, content) values (?, ?)")
        stmt.bindString(1, data.writeDate)
        stmt.bindString(2, data.content)
        var res = stmt.executeInsert()
        stmt.close()
        writer.close()
        return res
    }

    fun deleteAlarm(alarmId:Int):Int {
        var writer = helper.writableDatabase
        var stmt = writer.compileStatement("delete from ${user_id}_Alarm where id=?")
        stmt.bindLong(1, alarmId.toLong())
        var resRow = stmt.executeUpdateDelete()
        writer.execSQL("update sqlite_sequence set seq=(select max(id) from ${user_id}_Alarm) where name='${user_id}_Alarm'")
        stmt.close()
        writer.close()
        return resRow
    }


}

class AlarmVO(var id:Int, var writeDate:String, var content:String) : Serializable