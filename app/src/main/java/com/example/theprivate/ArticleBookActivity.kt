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

class ArticleBookActivity : AppCompatActivity() {
    private lateinit var agent:Informer

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.articlebook)
        supportActionBar!!.hide()

        var data:Intent = intent
        agent = data.getSerializableExtra("agent") as Informer

        var vp2 = findViewById<ViewPager2>(R.id.artbook_vp2)
        var afa:ArticleFragAdapter = ArticleFragAdapter(this, agent)

        vp2.adapter = afa
    }
}