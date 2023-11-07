package com.example.theprivate

import java.io.*
import android.util.*
import android.content.*
import android.database.*
import android.database.sqlite.*
import android.widget.Toast

class MemberDB(var context:Context) : SQLiteOpenHelper(context, "private", null, 1) {
    override fun onCreate(db:SQLiteDatabase?) {
        db!!.execSQL("create table if not exists members(id text not null primary key, pwd text not null, " +
                "name text not null, contact text not null, findq text not null, finda text not null," +
                "profile blob, cover blob, hideAll integer default 0, hideChecked integer default 0, comment text, work text, location text, school1 text, school2 text, school3 text, birth text)")
        // hideAll : 모든 게시글을 감추도록 설정 여부 / hideChecked : 체크한 일부 게시글만 감추도록 설정 여부 : 0은 false, 1은 true
    }
    override fun onUpgrade(db:SQLiteDatabase?, prevVersion:Int, curVersion:Int) {
        db!!.execSQL("drop table if exists members")
        onCreate(db)
    }
}

class MemberDAO(var context:Context) {
    var helper:MemberDB
    init{ helper = MemberDB(context) }
    fun selectAllByMap() : HashMap<String, Any> {
        var map:HashMap<String, Any> = HashMap<String, Any>()
        var executor = helper.readableDatabase
        var cursor = executor.rawQuery("select * from members", null)
        var members:MutableList<MemberVO>  = mutableListOf<MemberVO>()
        var ids:MutableList<String> = mutableListOf<String>()
        var contacts:ArrayList<String> = ArrayList<String>()
        while(cursor.moveToNext()) {
            members.add(MemberVO(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getBlob(6), cursor.getBlob(7), cursor.getInt(8), cursor.getInt(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15), cursor.getString(16)) )
            ids.add(cursor.getString(0))
            contacts.add(cursor.getString(3))
        }
        cursor.close()
        executor.close()
        map.put("members", members)
        map.put("ids", ids)
        map.put("contacts", contacts)
        return map
    }

    fun insertData(newMember:MemberVO):Long { // 가입할 때
        var executor = helper.writableDatabase
        var stmt = executor.compileStatement("insert into members values(?, ?, ?, ?, ?, ?, null, null, 0, 0, null, null, null, null, null, null, null)")
        stmt.bindString(1, newMember.id)
        stmt.bindString(2, newMember.pwd)
        stmt.bindString(3, newMember.name)
        stmt.bindString(4, newMember.contact)
        stmt.bindString(5, newMember.findq)
        stmt.bindString(6, newMember.finda)
        var res = stmt.executeInsert()
        stmt.close()
        executor.close()
        return res
    }

    fun updateData(idxArr:Array<Int?>, objArr:Array<Any?>, user_id:String):Int {
        var labels = arrayOf<String>("id", "pwd", "name", "contact", "findq", "finda", "profile", "cover", "hideAll", "hideChecked", "comment", "work", "location", "school1", "school2", "school3", "birth")
        //                             0     1      2         3         4        5        6           7        8            9            10       11         12         13         14         15        16
        var writer = helper.writableDatabase
        var updatedIdx = mutableListOf<Int>()
        var sql:StringBuffer = StringBuffer("update members set ")
        for(i:Int in 0..(idxArr.size-1) step 1) {
            if(idxArr[i] != null) {
                sql.append(labels[i] + "=?")
                sql.append(", ")
                updatedIdx.add(i + 1)
            }
        }
        var sqlStr:String = sql.substring(0, sql.length-2)
        sqlStr += " where id='${user_id}'"
        Log.i("GIN : sql ----------> " ,  sqlStr)
        //Toast.makeText(context, sqlStr, Toast.LENGTH_SHORT).show()
        var stmt = writer.compileStatement(sqlStr)
        for(j:Int in 1..updatedIdx.size) {
            var targetIdx = updatedIdx.get(j-1)
            if(targetIdx in 7..8) {
                Log.i("GIN : bindRoad ----------> " ,  j.toString())
                if(objArr[targetIdx-1] != null) stmt.bindBlob(j, (objArr[targetIdx-1] as ByteArray?)!!)
                else stmt.bindNull(j)
            }
            else if(targetIdx in 9..10) stmt.bindLong(j, (objArr[targetIdx-1] as Int?)!!.toLong())
            else stmt.bindString(j, (objArr[targetIdx-1] as String?)!!)
        }
        var res = stmt.executeUpdateDelete()
        stmt.close()
        writer.close()
        return res
        //return 1
    }

    fun selectOneTarget(user_id:String, user_pw:String):HashMap<String, Any> {
        var temp:HashMap<String, Any> = HashMap<String, Any>()
        var executor = helper.readableDatabase
        var params = arrayOf<String>(user_id, user_pw)
        var cursor = executor.rawQuery("select * from members where id=? and pwd=?", params)
        var count = 0
        var target:MemberVO? = null
        while(cursor.moveToNext()) {
            target = MemberVO(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getBlob(6), cursor.getBlob(7), cursor.getInt(8), cursor.getInt(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15), cursor.getString(16))
            count++
        }
        cursor.close()
        executor.close()
        temp.put("numOfRows", count)
        if(target != null) temp.put("member", target!!)
        return temp
    }

    fun selectOneTargetById(user_id:String):HashMap<String, Any> {
        var temp:HashMap<String, Any> = HashMap<String, Any>()
        var executor = helper.readableDatabase
        var params = arrayOf<String>(user_id)
        var cursor = executor.rawQuery("select * from members where id=?", params)
        var count = 0
        var target:MemberVO? = null
        while(cursor.moveToNext()) {
            target = MemberVO(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getBlob(6), cursor.getBlob(7), cursor.getInt(8), cursor.getInt(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15), cursor.getString(16))
            count++
        }
        cursor.close()
        executor.close()
        temp.put("numOfRows", count)
        if(target != null) temp.put("member", target!!)
        return temp
    }

    fun findId(user_name:String, user_contact:String):HashMap<String, Any> {
        var temp:HashMap<String, Any> = HashMap<String, Any>()
        var executor = helper.readableDatabase
        var params = arrayOf<String>(user_name, user_contact)
        var cursor = executor.rawQuery("select * from members where name=? and contact=?", params)
        var count = 0
        var target:MemberVO? = null
        while(cursor.moveToNext()) {
            target = MemberVO(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getBlob(6), cursor.getBlob(7), cursor.getInt(8), cursor.getInt(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15), cursor.getString(16))
            count++
        }
        cursor.close()
        executor.close()
        temp.put("numOfRows", count)
        if(target != null) temp.put("member", target!!)
        return temp
    }
}

class MemberVO(var id:String, var pwd:String, var name:String, var contact:String, var findq:String, var finda:String, var profile:ByteArray? = null, var cover:ByteArray? = null, var hideAll:Int? = null, var hideChecked:Int? = null, var comment:String? = null, var work:String? = null, var location:String? = null, var school1:String? = null, var school2:String? = null, var school3:String? = null, var birth:String? = null) : Serializable