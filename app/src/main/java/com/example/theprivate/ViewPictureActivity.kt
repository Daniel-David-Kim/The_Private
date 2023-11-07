package com.example.theprivate

import androidx.appcompat.app.*
import android.os.*
import java.util.*
import android.view.*
import android.widget.*
import android.content.*
import android.graphics.*
import androidx.fragment.*
import androidx.fragment.app.*
import androidx.viewpager2.widget.*
import androidx.viewpager2.adapter.*
import de.hdodenhof.circleimageview.*

class ViewPictureActivity : AppCompatActivity() {
    private lateinit var agent:Informer

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photobook)
        supportActionBar!!.hide()

        var data:Intent = intent
        agent = data.getSerializableExtra("agent") as Informer
        var requestWord = data.getStringExtra("requestWord")

        var profile = findViewById<CircleImageView>(R.id.phbook_profile)
        var options:BitmapFactory.Options = BitmapFactory.Options()
        options.inSampleSize = 4
        if(agent.user!!.profile != null) {
            var bitmap = BitmapFactory.decodeByteArray(agent.user!!.profile, 0, agent.user!!.profile!!.size, options)
            var scaled = Bitmap.createScaledBitmap(bitmap, 450, 450, true)
            profile.setImageBitmap(scaled)
        }
        var name = findViewById<TextView>(R.id.phbook_name)
        name.text = agent.user!!.name
        var retBtn = findViewById<ImageView>(R.id.phbook_return)
        retBtn.setOnClickListener {
            finish()
        }

        var vp2 = findViewById<ViewPager2>(R.id.phbook_vp2)

        var mList = mutableListOf<Bitmap>()
        if(requestWord == "cover") {
            var temp = agent.user!!.cover
            var bitmap = BitmapFactory.decodeByteArray(agent.user!!.cover, 0, agent.user!!.cover!!.size, options)
            mList.add(bitmap)
        } else {
            var temp = agent.user!!.profile
            var bitmap = BitmapFactory.decodeByteArray(agent.user!!.profile, 0, agent.user!!.profile!!.size, options)
            mList.add(bitmap)
        }
        var pfa = PhotoFragAdapter(this, agent, getMemberPic=true, memberList=mList)
        vp2.adapter = pfa
    }
}