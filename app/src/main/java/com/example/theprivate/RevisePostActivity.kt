package com.example.theprivate

import android.app.Activity
import android.content.Intent
import android.os.*
import androidx.appcompat.app.*
import android.util.*
import android.widget.*
import android.view.*
import android.graphics.*
import android.provider.MediaStore
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.util.*

class RevisePostActivity : AddPostActivity() {
    private var already:Boolean = false
    private lateinit var agent:Informer
    var article_id:Int = -1
    private lateinit var dao:ArchiveDAO
    private lateinit var photo_l2:LinearLayout
    var imgChanged = false
    lateinit var prevContentStr:String

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_post)
        supportActionBar!!.title = "Revise post"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        agent = intent.getSerializableExtra("agent") as Informer
        article_id = intent.getIntExtra("articleId", -1)
        dao = ArchiveDAO(this, agent.user!!.id)
        var requestCode = intent.getIntExtra("requestCode", -1)
        agent.requestCode = requestCode
        Log.i("user", agent.user!!.name)

        var profile = findViewById<CircleImageView>(R.id.post_profile)
        var options:BitmapFactory.Options = BitmapFactory.Options()
        options.inSampleSize = 4
        if(agent.user!!.profile != null) {
            var bitmap = BitmapFactory.decodeByteArray(agent.user!!.profile, 0, agent.user!!.profile!!.size, options)
            var scaled = Bitmap.createScaledBitmap(bitmap, 450, 450, true)
            profile.setImageBitmap(scaled)
        }

        var name = findViewById<TextView>(R.id.post_name)
        name.text = agent.user!!.name

        var photo_l1 = findViewById<LinearLayout>(R.id.post_photo_b1)
        photo_l2 = findViewById<LinearLayout>(R.id.post_photo_b2)
        var add_photo = findViewById<Button>(R.id.post_add_photo)


        var article:ArchiveVO = dao.selectOneTarget(article_id)
        var textfield:EditText = findViewById<EditText>(R.id.post_body)
        textfield.setText(article.content)

        var ids = intArrayOf(R.id.post_img1, R.id.post_img2, R.id.post_img3, R.id.post_img4)
        imgs = arrayOfNulls<ImageView?>(ids.size)
        uploadBitmap = arrayOfNulls<Bitmap?>(ids.size)
        var ref:Reference = Reference(this, agent)
        for(i:Int in 0..(ids.size-1) step 1) {
            imgs[i] = findViewById<ImageView>(ids[i])
            imgs[i]!!.setOnClickListener(ref.ImgClickListener(i))
        }

        // 실험용
        Log.i("Revise : requestcode  ---> ", agent.requestCode.toString())

        content = findViewById<EditText>(R.id.post_body)
        prevContentStr = content.text.toString()
        var bytes = arrayOf<ByteArray?>(article.pic1, article.pic2, article.pic3, article.pic4)
        var bitmaps = arrayOfNulls<Bitmap?>(bytes.size)
        var count = 0
        for(i:Int in 0..(bytes.size-1) step 1) {
            if(bytes[i] != null) {
                bitmaps[i] = BitmapFactory.decodeByteArray(bytes[i], 0, bytes[i]!!.size)
                count++
            } else break
        }
        var row_l1 = findViewById<LinearLayout>(R.id.post_photo_b1)
        var row_l2 = findViewById<LinearLayout>(R.id.post_photo_b2)
        if(count > 0) row_l1.visibility = LinearLayout.VISIBLE
        if(count >= 2) row_l2.visibility = LinearLayout.VISIBLE
        for(i:Int in 0..(bitmaps.size-1) step 1) {
            if(bitmaps[i] != null) {
                imgs[i]!!.setImageBitmap(bitmaps[i])
                uploadBitmap[i] = bitmaps[i]
                uploaded.add(i.toString())
            } else break
        }
        add_photo.setOnClickListener {
            if(already == false) {
                photo_l1.visibility = ViewGroup.VISIBLE
                already = true
            }
        }

        var revise = findViewById<Button>(R.id.post_submit)
        revise.setOnClickListener(ref.PostBtnClickListener(dao))

    }

    override fun onOptionsItemSelected(item:MenuItem):Boolean {
        if(item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 349) {
            if(resultCode == Activity.RESULT_CANCELED) {
                uploaded.removeAt(uploaded.size-1)
                this.onRestart()
            } else {
                var resUri = data!!.data
                var bitmap:Bitmap? = null
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, resUri!!))
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, resUri!!)
                }

                uploadBitmap[imgs.indexOf(selectedImg)] = bitmap
                selectedImg!!.setImageBitmap(bitmap)
                imgChanged = true
                if(selectedImg!!.id == R.id.post_img2) {
                    photo_l2.visibility = ViewGroup.VISIBLE
                }
            }
        }

    }

}