package com.example.theprivate

import java.io.*
import java.util.*
import android.content.*
import android.graphics.*
import android.database.sqlite.*
import android.util.Log

class ArchiveDB(var context:Context, var user_id:String) : SQLiteOpenHelper(context, "private", null, 1) {
    override fun onCreate(db:SQLiteDatabase?) {
        db!!.execSQL("create table if not exists ${user_id}_Archive(id integer not null primary key autoincrement, content text not null, writeDate text not null, " +
                          "checkHide integer default 0, highlight integer default 0, pic1 blob, pic2 blob, pic3 blob, pic4 blob)")
    }
    override fun onUpgrade(db:SQLiteDatabase?, prevVersion:Int, curVersion:Int) {
        db!!.execSQL("drop table if exists ${user_id}_Archive")
        onCreate(db)
    }
}

class ArchiveDAO(var context:Context, var user_id:String) {
    lateinit var helper:ArchiveDB
    init { helper = ArchiveDB(context, user_id) }

    // int swit : 0 - all / 1 - not hide / 2 - highlighted / 3 includes photo / 4 - only text information
    fun getAllArticleMap(swit:Int = 0, articleId:Int=-1):MutableMap<String, Any> {
        var res:MutableMap<String, Any> = mutableMapOf<String, Any>()
        var lists:MutableList<ArchiveVO> = mutableListOf<ArchiveVO>()
        var reader = helper.readableDatabase
        var sql = ""
        when(swit) {
            0 -> {sql = "select * from ${user_id}_Archive order by id desc"}
            1 -> {sql = "select * from ${user_id}_Archive where checkHide=0 order by id desc"}
            2 -> {sql = "select * from ${user_id}_Archive where highlight=1 order by id desc"}
            3 -> {sql = "select * from ${user_id}_Archive where pic1 is not null or pic2 is not null or pic3 is not null or pic4 is not null order by id desc"}
            4 -> {sql = "select id, content, writeDate from ${user_id}_Archive order by id desc"}
            5 -> {sql = "select * from ${user_id}_Archive where id=${articleId}"}
        }
        var cursor = reader.rawQuery(sql, null)
        var count = 0
        while(cursor.moveToNext()) {
            var temp:ArchiveVO? = null
            if(swit == 4) temp = ArchiveVO(cursor.getInt(0), cursor.getString(1), cursor.getString(2), 0, 0, null, null, null, null)
            else temp = ArchiveVO(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getBlob(5), cursor.getBlob(6), cursor.getBlob(7), cursor.getBlob(8))
            lists.add(temp)
            count++
        }
        cursor.close()
        reader.close()
        res.put("numOfRows", count)
        res.put("archive", lists)
        return res
    }

    // 모든 포스트 글만 가져옵니다.
    fun getArticles():MutableMap<String, Any> {
        var result:MutableMap<String, Any> = mutableMapOf<String, Any>()
        var map = getAllArticleMap(4)
        result.put("numOfRows", map.get("numOfRows") as Int)
        result.put("articleList", map.get("archive") as MutableList<ArchiveVO>)
        return result
    }

    fun getOneArticle(artId:Int):MutableMap<String, Any> {
        var result:MutableMap<String, Any> = mutableMapOf<String, Any>()
        var map = getAllArticleMap(5, articleId=artId)
        result.put("numOfRows", map.get("numOfRows") as Int)
        result.put("articleList", map.get("archive") as MutableList<ArchiveVO>)
        return result
    }

    // prototype4에서 새로 추가. num에 입력한 갯수만큼 사진을 담은 리스트를 맵에 바인딩해 가져옵니다. 사진 갯수는 맵의 numOfRows 키의 값으로 바인딩되어 있습니다. / getHighlight를 true로 하면, 하이라이트 처리한 사진들만 모아서 가져옵니다.
    // 포토북의 이미지들은 크기를 조절할 필오 없이 있는 크기 그대로 보여주면 되므로 기본적으로 이미지 크기 로직은 제거합니다. 그러나, 필요하면 조절할 수 있도록 getScaled을 추가합니다. getScaled가 true이면 이미지 크기가 조절됩니다..
    fun getPics(num:Int=0, getHighlight:Boolean=false, getScaled:Boolean=false):MutableMap<String, Any> {
        var result:MutableMap<String, Any> = mutableMapOf<String, Any>()
        var bitmapList:MutableList<Bitmap> = mutableListOf<Bitmap>()
        var scaledBitmapList:MutableList<Bitmap> = mutableListOf<Bitmap>()
        var targetCount = num
        var count = 0
        var map = getAllArticleMap(3)
        var rawList = map.get("archive") as MutableList<ArchiveVO>
        for(article:ArchiveVO in rawList) {
            if(getHighlight == true)
                if(article.highlight == 0) continue

            if(article.pic1 != null) {
                var bitmap = BitmapFactory.decodeByteArray(article.pic1, 0, article.pic1!!.size)
                if(getScaled == true) {
                    var scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true)
                    scaledBitmapList.add(scaledBitmap)
                }
                bitmapList.add(bitmap)
                count++
            }
            if(count == targetCount) break
            if(article.pic2 != null) {
                var bitmap = BitmapFactory.decodeByteArray(article.pic2, 0, article.pic2!!.size)
                if(getScaled == true) {
                    var scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true)
                    scaledBitmapList.add(scaledBitmap)
                }
                bitmapList.add(bitmap)
                count++
            }
            if(count == targetCount) break
            if(article.pic3 != null) {
                var bitmap = BitmapFactory.decodeByteArray(article.pic3, 0, article.pic3!!.size)
                if(getScaled == true) {
                    var scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true)
                    scaledBitmapList.add(scaledBitmap)
                }
                bitmapList.add(bitmap)
                count++
            }
            if(count == targetCount) break
            if(article.pic4 != null) {
                var bitmap = BitmapFactory.decodeByteArray(article.pic4, 0, article.pic4!!.size)
                if(getScaled == true) {
                    var scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true)
                    scaledBitmapList.add(scaledBitmap)
                }
                bitmapList.add(bitmap)
                count++
            }
            if(count == targetCount) break
        }
        result.put("numOfRows", count)
        result.put("bitmapList", bitmapList)
        result.put("scaledBitmapList", scaledBitmapList)
        return result
    }

    fun insertData(article:ArchiveVO):Long {
        var writer = helper.writableDatabase
        var stmt = writer.compileStatement("insert into ${user_id}_Archive (content, writeDate, checkHide, highlight, pic1, pic2, pic3, pic4) values (?, ?, 0, 0, ?, ?, ?, ?)")
        stmt.bindString(1, article.content)
        stmt.bindString(2, article.writeDate)
        if(article.pic1 != null) stmt.bindBlob(3, article.pic1) else stmt.bindNull(3)
        if(article.pic2 != null) stmt.bindBlob(4, article.pic2) else stmt.bindNull(4)
        if(article.pic3 != null) stmt.bindBlob(5, article.pic3) else stmt.bindNull(5)
        if(article.pic4 != null) stmt.bindBlob(6, article.pic4) else stmt.bindNull(6)
       // stmt.bindBlob(4, article.pic2)
        //stmt.bindBlob(5, article.pic3)
        //stmt.bindBlob(6, article.pic4)
        var res = stmt.executeInsert()
        stmt.close()
        writer.close()
        return res
    }

    fun updateData(article:ArchiveVO, article_id:Int):Int {
        var writer = helper.writableDatabase
        var stmt = writer.compileStatement("update ${user_id}_Archive set content=?, pic1=?, pic2=?, pic3=?, pic4=? where id=?")
        stmt.bindString(1, article.content)
        if(article.pic1 != null) stmt.bindBlob(2, article.pic1) else stmt.bindNull(2)
        if(article.pic2 != null) stmt.bindBlob(3, article.pic2) else stmt.bindNull(3)
        if(article.pic3 != null) stmt.bindBlob(4, article.pic3) else stmt.bindNull(4)
        if(article.pic4 != null) stmt.bindBlob(5, article.pic4) else stmt.bindNull(5)
        stmt.bindLong(6, article_id.toLong())
        var res = stmt.executeUpdateDelete()
        stmt.close()
        writer.close()
        return res
    }

    fun updateSwitch(swit:Int, article_id:Int, change:Int):Int { // 1은 highlight, 2는 checkHide(pin)
        var columnName = ""
        if(swit == 1) columnName = "highlight"
        else columnName = "checkHide"
        var writer = helper.writableDatabase
        var stmt = writer.compileStatement("update ${user_id}_Archive set ${columnName}=? where id=?")
        stmt.bindLong(1, change.toLong())
        stmt.bindLong(2, article_id.toLong())
        var res = stmt.executeUpdateDelete()
        stmt.close()
        writer.close()
        return res
    }

    fun deleteData(articleNo:Int):Int {
        var writer = helper.writableDatabase
        var stmt = writer.compileStatement("delete from ${user_id}_Archive where id=?")
        stmt.bindLong(1, articleNo.toLong())
        var rowRes = stmt.executeUpdateDelete()
        writer.execSQL("update sqlite_sequence set seq=(select max(id) from ${user_id}_Archive) where name='${user_id}_Archive'")
        stmt.close()
        writer.close()
        return rowRes
    }


    fun selectOneTarget(article_id:Int):ArchiveVO {
        var res:ArchiveVO? = null
        var reader = helper.readableDatabase
        var cursor = reader.rawQuery("select * from ${user_id}_Archive where id=${article_id}", null)
        while(cursor.moveToNext()) {
            res = ArchiveVO(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getBlob(5), cursor.getBlob(6), cursor.getBlob(7), cursor.getBlob(8))
        }
        cursor.close()
        reader.close()
        return res!!
    }
}

class ArchiveVO(var id:Int, var content:String, var writeDate:String, var checkHide:Int, var highlight:Int, var pic1:ByteArray? = null, var pic2:ByteArray? = null, var pic3:ByteArray? = null, var pic4:ByteArray? = null) : Serializable