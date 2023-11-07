package com.example.theprivate

import androidx.appcompat.app.*
import android.app.*
import android.app.AlertDialog
import android.content.*
import android.graphics.*
import android.os.Build
import android.provider.*
import android.util.Log
import android.widget.*
import android.view.*
import android.view.View.*
import de.hdodenhof.circleimageview.CircleImageView
import java.io.*
import java.util.*

class Reference(var activity:AppCompatActivity, var agent:Informer) {

    inner class ImgClickListener : OnClickListener {
        private var i:Int = -7
        public constructor(i:Int) { this.i = i }
        override fun onClick(view:View?) {
            var addPost = activity as AddPostActivity
            var isit = false
            if((addPost.uploaded.size == 0) and (i > 0)) {
                Toast.makeText(activity, "먼저 첫번째 사진을 등록해주세요.", Toast.LENGTH_SHORT).show()
            } else if(addPost.uploaded.size > 0) {
                if (addPost.uploaded.contains(i.toString())) { // 올렸던 사진을 한번 더 누르면 삭제하거나 바꾸기

                    var dig = AlertDialog.Builder(activity)
                    var labels = arrayOf<String>("사진 바꾸기", "사진 삭제하기")
                    dig.setItems(labels) { dialog, pos ->
                        when(pos) {
                            0 -> {
                                addPost.selectedImg = addPost.imgs[i]!!
                                var intent = Intent(Intent.ACTION_PICK)
                                intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
                                activity.startActivityForResult(intent, 348)
                            }
                            1 -> {
                                if((i < 3) and addPost.uploaded.contains((i+1).toString())) {
                                    Toast.makeText(activity, "가장 최근 사진부터 삭제해주세요.", Toast.LENGTH_SHORT).show()
                                } else {
                                    addPost.imgs[i]!!.setImageResource(R.drawable.plus1)
                                    addPost.uploadBitmap[i] = null
                                    addPost.uploaded.remove(i.toString())
                                    if(addPost is RevisePostActivity) addPost.imgChanged = true
                                }
                            }
                        }
                    }
                    dig.show()
                } else if((i > 0) and !addPost.uploaded.contains((i-1).toString())) {
                    Toast.makeText(activity, "이전 사진부터 등록해주세요.", Toast.LENGTH_SHORT).show()
                } else isit = true
            } else isit = true
            if(isit == true) {
                addPost.selectedImg = addPost.imgs[i]!!
                var intent: Intent = Intent(Intent.ACTION_PICK)
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
                addPost.uploaded.add(i.toString())
                if(addPost is RevisePostActivity) activity.startActivityForResult(intent, 349)
                else activity.startActivityForResult(intent, 348)
            }
        }
    }

    inner class PostBtnClickListener : OnClickListener {
        private lateinit var dao:ArchiveDAO
        public constructor(dao:ArchiveDAO) {
            this.dao = dao
        }
        override fun onClick(view:View?) {
            var addPost = activity as AddPostActivity
            var contentStr = addPost.content.text.toString()
            var dateStr = Converter.changeDateToFormat(Date())
            if((addPost.uploadBitmap[0] == null) and ((contentStr == null) or (contentStr == ""))) {
                Toast.makeText(activity, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if((addPost is RevisePostActivity)&&((addPost.imgChanged == false) and (addPost.prevContentStr == contentStr))) {
                Toast.makeText(activity, "변경 사항을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                var uploadByte = arrayOfNulls<ByteArray>(addPost.uploadBitmap.size)
                for(i:Int in 0..(addPost.uploadBitmap.size-1) step 1) {
                    if(addPost.uploadBitmap[i] != null) {
                        var stream = ByteArrayOutputStream()
                        addPost.uploadBitmap[i]!!.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                        var byte = stream.toByteArray()
                        uploadByte[i] = byte
                    } else break
                }
                var article = ArchiveVO(-1, contentStr, dateStr, 0, 0, uploadByte[0], uploadByte[1], uploadByte[2], uploadByte[3])

                var resRow:Long = -1L
                var resRow2:Int = -1
                if(addPost is RevisePostActivity) resRow2 = dao.updateData(article, addPost.article_id)
                else resRow = dao.insertData(article)
                if((((addPost is RevisePostActivity) == false)&&(resRow != -1L)) or ((addPost is RevisePostActivity) && (resRow2 == 1))) {
                    var res:String = if(addPost is RevisePostActivity) "수정" else "등록"
///////////////////
                    var resAlm:String = if(addPost is RevisePostActivity) "revised" else "wrote"
                    var alarmDAO:AlarmDAO = AlarmDAO(activity, agent.user!!.id)
                    var curDate = Converter.changeDateToFormat(Date())
                    var newAlarm = AlarmVO(-1, curDate, "you ${resAlm} the post. : '${if(contentStr.length < 45) contentStr.substring(0) else contentStr.substring(0, 45)}'")
                    alarmDAO.insertAlarm(newAlarm)
//////////////////////
                    Toast.makeText(activity, "게시글이 ${res}되었습니다.", Toast.LENGTH_SHORT).show()
                    var outIntent = Intent(activity, CoreActivity::class.java)
                    outIntent.putExtra("agent", agent)
                    var tabNum:Int = if(addPost is RevisePostActivity) 0 else 1
                    outIntent.putExtra("tabTo", tabNum)

                    activity.setResult(Activity.RESULT_OK, outIntent)
                    activity.finish()
                }
            }

        }
    }

    fun getImagesAndSet(thisView:View, ids:IntArray, requestCode:Int, frag:MyFrag?=null, proRef:View?=null, covRef:View?=null):Array<ImageView?> {
        var imgs = arrayOfNulls<ImageView?>(2)
        var options:BitmapFactory.Options = BitmapFactory.Options()
        options.inSampleSize = 4
        var labels = arrayOf<String>("View picture", "Edit Picture", "Delete Picture")
        var dig = AlertDialog.Builder(activity)
        for(i:Int in 0..1 step 1) {
            if(i == 0) { // i == 0 이면 커버 사진
                imgs[i] = thisView.findViewById<ImageView>(ids[i])
                if(agent.user!!.cover != null) {
                    var bitmap = BitmapFactory.decodeByteArray(agent.user!!.cover, 0, agent.user!!.cover!!.size, options)
                    var scaled = Bitmap.createScaledBitmap(bitmap, 500, 500, true)
                    imgs[i]!!.setImageBitmap(scaled)
                }
            } else { // i == 1 이면 프로필 사진
                imgs[i] = thisView.findViewById<CircleImageView>(ids[i])
                if(agent.user!!.profile != null) {
                    var bitmap = BitmapFactory.decodeByteArray(agent.user!!.profile, 0, agent.user!!.profile!!.size, options)
                    var scaled = Bitmap.createScaledBitmap(bitmap, 450, 450, true)
                    imgs[i]!!.setImageBitmap(scaled)
                }
            }
            imgs[i]!!.setOnClickListener {
                dig.setItems(labels) {dig, pos ->
                    when(pos) {
                        0 -> { // view cover/profile
                            var intent:Intent = Intent(activity, ViewPictureActivity::class.java)
                            intent.putExtra("agent", agent)
                            if(i == 0) intent.putExtra("requestWord", "cover")
                            else intent.putExtra("requestWord", "profile")
                            if(i == 0) {
                                if(agent.user!!.cover != null) activity.startActivity(intent)
                                else Toast.makeText(activity, "커버 사진이 없습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                if(agent.user!!.profile != null) activity.startActivity(intent)
                                else Toast.makeText(activity, "프로필 사진이 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        1 -> { // change cover/profile
                            var intent = Intent(Intent.ACTION_PICK)
                            intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
                            if(frag != null) frag.startActivityForResult(intent, requestCode + i)
                            else activity.startActivityForResult(intent, requestCode + i)
                        }
                        2 -> { // delete cover/profileㄹ
                            var members = MemberDAO(activity)
                            var idxArr = arrayOfNulls<Int>(17)
                            var objArr = arrayOfNulls<Any>(17)

                            // 0 : cover
                            var idx:Int = -1; var resRow:Int = -1
                            if(i == 0) idx = 7
                            else idx = 6
                            idxArr[idx] = 1
                            objArr[idx] = null

                            if(i == 0) {
                                if(agent.user!!.cover != null) {
                                    resRow = members.updateData(idxArr, objArr, agent.user!!.id)
                                    if(resRow == 1) Toast.makeText(activity, "커버 사진이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                    else Toast.makeText(activity, "커버 사진 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                                } else Toast.makeText(activity, "커버 사진이 없습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                if(agent.user!!.profile != null) {
                                    resRow = members.updateData(idxArr, objArr, agent.user!!.id)
                                    if(resRow == 1) Toast.makeText(activity, "프로필 사진이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                    else Toast.makeText(activity, "프로필 사진 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                                } else Toast.makeText(activity, "프로필 사진이 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                            if((proRef != null) or (covRef != null)) {
                                if(i == 0) {
                                    (covRef as ImageView).setImageResource(R.drawable.gray_50)
                                    agent.user!!.cover = null
                                } else {
                                    (proRef as CircleImageView).setImageResource(R.drawable.sphere)
                                    agent.user!!.profile = null
                                }
                            }

                        }
                    }
                }
                dig.show()
            }
        }
        return imgs
    }

    fun imagesActivityResult(requestCode:Int, responseCode:Int, data:Intent?, start:Int, end:Int):Bitmap? {
        var bitmap: Bitmap? = null
        if(requestCode in start..end) {
            if(responseCode == Activity.RESULT_CANCELED) {
                Log.i("진입점 -> ", "금지!")}
            if(responseCode == Activity.RESULT_OK) {
                var resUri = data!!.data


                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) bitmap = ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(activity.contentResolver, resUri!!))
                else bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, resUri!!)
                var bstream = ByteArrayOutputStream()

                var members:MemberDAO = MemberDAO(activity)
                var idxArr = arrayOfNulls<Int>(17)
                var objArr = arrayOfNulls<Any>(17)
                if(requestCode == start) {
                    bitmap!!.compress(Bitmap.CompressFormat.JPEG, 70, bstream)
                    var byteArr = bstream.toByteArray()
                    idxArr[7] = 1
                    objArr[7] = byteArr
                    agent.user!!.cover = byteArr
                    var resRow = members.updateData(idxArr, objArr, agent.user!!.id)
                    if(resRow == 1) Toast.makeText(activity, "커버 사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(activity, "커버 사진 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, bstream)
                    var byteArr = bstream.toByteArray()
                    idxArr[6] = 1
                    objArr[6] = byteArr
                    agent.user!!.profile = byteArr
                    var resRow = members.updateData(idxArr, objArr, agent.user!!.id)
                    if(resRow == 1) Toast.makeText(activity, "프로필 사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(activity, "프로필 사진 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
                members.helper.close()
            }
        }
        return bitmap
    }

}