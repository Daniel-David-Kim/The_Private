package com.example.theprivate

import androidx.appcompat.app.*
import android.os.*
import android.view.*
import android.util.*
import android.widget.*
import android.content.*
import android.graphics.*
import de.hdodenhof.circleimageview.*

class ViewPostActivity : AppCompatActivity() {
    private lateinit var agent:Informer

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_post)
        supportActionBar!!.title = "View post"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        var data:Intent = intent
        agent = data.getSerializableExtra("agent") as Informer

        var articleId = data.getIntExtra("article_id", -1)

        var dao:ArchiveDAO = ArchiveDAO(this, agent.user!!.id)
        var articleMap = dao.getOneArticle(articleId)
        var articleList = articleMap.get("articleList") as MutableList<ArchiveVO>
        var article = articleList.get(0)

        var profile = findViewById<CircleImageView>(R.id.viewp_profile)
        var options:BitmapFactory.Options = BitmapFactory.Options()
        options.inSampleSize = 4
        if(agent.user!!.profile != null) {
            var bitmap = BitmapFactory.decodeByteArray(agent.user!!.profile, 0, agent.user!!.profile!!.size, options)
            var scaled = Bitmap.createScaledBitmap(bitmap, 450, 450, true)
            profile.setImageBitmap(scaled)
        }
        var name = findViewById<TextView>(R.id.viewp_name)
        name.text = agent.user!!.name
        var date = findViewById<TextView>(R.id.viewp_date)
        date.text = article.writeDate
        var body = findViewById<TextView>(R.id.viewp_body)
        body.text = article.content
        var pic1 = findViewById<ImageView>(R.id.viewp_pic1)
        if(article.pic1 != null) {
            var bitmap = BitmapFactory.decodeByteArray(article.pic1, 0, article.pic1!!.size)
            pic1.setImageBitmap(bitmap)
        }
        var pic2 = findViewById<ImageView>(R.id.viewp_pic2)
        if(article.pic2 != null) {
            var bitmap = BitmapFactory.decodeByteArray(article.pic2, 0, article.pic2!!.size)
            pic2.setImageBitmap(bitmap)
        }
        var pic3 = findViewById<ImageView>(R.id.viewp_pic3)
        if(article.pic3 != null) {
            var bitmap = BitmapFactory.decodeByteArray(article.pic3, 0, article.pic3!!.size)
            pic3.setImageBitmap(bitmap)
        }
        var pic4 = findViewById<ImageView>(R.id.viewp_pic4)
        if(article.pic4 != null) {
            var bitmap = BitmapFactory.decodeByteArray(article.pic4, 0, article.pic4!!.size)
            pic4.setImageBitmap(bitmap)
        }
    }

    override fun onOptionsItemSelected(item:MenuItem):Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

}