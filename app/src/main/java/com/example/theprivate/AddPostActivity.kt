package com.example.theprivate

import java.io.*
import android.os.*
import androidx.appcompat.app.*
import android.widget.*
import android.view.*
import android.app.*
import android.app.AlertDialog
import android.content.*
import android.graphics.*
import android.provider.*
import android.util.*
import android.net.*
import android.icu.text.*
import java.util.*
import de.hdodenhof.circleimageview.*

open class AddPostActivity : AppCompatActivity() {
    private var already:Boolean = false
    public var selectedImg:ImageView? = null
    private lateinit var photo_l2:LinearLayout
    public lateinit var imgs:Array<ImageView?>
    public var uploaded:MutableList<String> = mutableListOf<String>()
    private lateinit var agent:Informer
    public lateinit var uploadBitmap:Array<Bitmap?>
    private lateinit var dao:ArchiveDAO
    lateinit var content:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_post)
        supportActionBar!!.title = "Create post"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        agent = intent!!.getSerializableExtra("agent") as Informer
        dao = ArchiveDAO(this, agent.user!!.id)
        println("login : ${agent.user!!.name}")

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
        add_photo.setOnClickListener {
            if(already == false) {
                photo_l1.visibility = ViewGroup.VISIBLE
                already = true
            }
        }
        var ids = intArrayOf(R.id.post_img1, R.id.post_img2, R.id.post_img3, R.id.post_img4)
        uploadBitmap = arrayOfNulls<Bitmap?>(ids.size)
        imgs = arrayOfNulls<ImageView>(ids.size)
        var ref:Reference = Reference(this, agent)
        for(i:Int in 0..(ids.size-1) step 1) {
            imgs[i] = findViewById<ImageView>(ids[i])
            imgs[i]!!.setOnClickListener(ref.ImgClickListener(i))
        }

        content = findViewById<EditText>(R.id.post_body)
        var submit = findViewById<Button>(R.id.post_submit)
        submit.setOnClickListener(ref.PostBtnClickListener(dao))
    }

    override fun onOptionsItemSelected(item:MenuItem):Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("welcome back! ---> ", "프래그먼트 ㄹ: 온 액티비티 리절트!")
        if(requestCode == 348) {
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
                if(selectedImg!!.id == R.id.post_img2) {
                    photo_l2.visibility = ViewGroup.VISIBLE
                }
            }
        }

    }

}