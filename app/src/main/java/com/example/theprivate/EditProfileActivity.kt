package com.example.theprivate

import android.os.*
import androidx.appcompat.app.*
import android.widget.*
import android.view.*
import android.app.*
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.*
import de.hdodenhof.circleimageview.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var agent:Informer
    private lateinit var ref:Reference
    private lateinit var imgs:Array<ImageView?>
    private lateinit var name:TextView
    private lateinit var bio:TextView
    private lateinit var location:TextView
    private lateinit var birth:TextView
    private lateinit var work:TextView
    private lateinit var school1:TextView
    private lateinit var school2:TextView
    private lateinit var school3:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)
        supportActionBar!!.title = "Edit Profile"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        agent = intent.getSerializableExtra("agent") as Informer
        ref = Reference(this, agent)
        Log.i("user", agent.user!!.name)

        var base:ScrollView = findViewById<ScrollView>(R.id.edit_base)
        name = findViewById<TextView>(R.id.edit_name)
        bio = findViewById<TextView>(R.id.edit_bio)
        location = findViewById<TextView>(R.id.edit_loc)
        birth = findViewById<TextView>(R.id.edit_birth)
        work = findViewById<TextView>(R.id.edit_work)
        school1 = findViewById<TextView>(R.id.edit_school1)
        school2 = findViewById<TextView>(R.id.edit_school2)
        school3 = findViewById<TextView>(R.id.edit_school3)

        var ids = intArrayOf(R.id.edit_cover, R.id.edit_profile)
        imgs = ref.getImagesAndSet(base, ids, 8998)
        imgs = ref.getImagesAndSet(base, ids, 8998, proRef=imgs[1], covRef=imgs[0])

        name.text = agent.user!!.name
        bio.text = if(agent.user!!.comment != null) agent.user!!.comment else ""
        location.text = if(agent.user!!.location != null) agent.user!!.location else ""
        birth.text = if(agent.user!!.birth != null) agent.user!!.birth else ""
        work.text = if(agent.user!!.work != null) agent.user!!.work else ""
        school1.text = if(agent.user!!.school1 != null) agent.user!!.school1 else ""
        school2.text = if(agent.user!!.school2 != null) agent.user!!.school2 else ""
        school3.text = if(agent.user!!.school3 != null) agent.user!!.school3 else ""

        var ids2 = intArrayOf(R.id.edit_name_btn, R.id.edit_bio_btn, R.id.edit_loc_btn, R.id.edit_birth_btn, R.id.edit_work_btn, R.id.edit_school1_btn, R.id.edit_school2_btn, R.id.edit_school3_btn)
        var labels2 = arrayOf<String>("nickname", "bio", "loc", "birth", "work", "school1", "school2", "school3")
        var btns = arrayOfNulls<View?>(8)
        var intent:Intent = Intent(applicationContext, EditActivity::class.java)
        intent.putExtra("agent", agent)
        for(i:Int in 0..7 step 1) {
            btns[i] = if(i in 0..1) findViewById<TextView>(ids2[i]) else findViewById<ImageView>(ids2[i])
            btns[i]!!.setOnClickListener{
                intent.putExtra("what", labels2[i])
                startActivityForResult(intent, 9000 + i)
            }
        }

    }

    override fun onOptionsItemSelected(item:MenuItem):Boolean {
        if(item.itemId == android.R.id.home) {
            var resIntent:Intent = Intent(applicationContext, CoreActivity::class.java)
            resIntent.putExtra("agent", agent)
            setResult(Activity.RESULT_OK, resIntent)
            finish()
        }
        return true
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_CANCELED) {
            onResume()
        }
        if(resultCode == Activity.RESULT_OK) {
            var revise = data!!.getBooleanExtra("revise", false)
            var newAgent = Informer()
            if((revise == true) or (requestCode in 8998..8999)) {
                if((requestCode in 8998..8999) == false) newAgent = data!!.getSerializableExtra("agent") as Informer
                when(requestCode) {
                    8998, 8999 -> { // 8998 : 커버 / 8999 : 프로필
                       var bitmap = ref.imagesActivityResult(requestCode, resultCode, data, 8998, 8999)
                        if(bitmap != null) {
                            if(requestCode == 8998) imgs[0]!!.setImageBitmap(bitmap)
                            else imgs[1]!!.setImageBitmap(bitmap)
                        }
                    }
                    9000 -> {
                        agent.user!!.name = newAgent.user!!.name
                        name.text = agent.user!!.name
                    }
                    9001 -> {
                        agent.user!!.comment = newAgent.user!!.comment
                        bio.text = agent.user!!.comment
                    }
                    9002 -> {
                        agent.user!!.location = newAgent.user!!.location
                        location.text = agent.user!!.location
                    }
                    9003 -> {
                        agent.user!!.birth = newAgent.user!!.birth
                        birth.text = agent.user!!.birth
                    }
                    9004 -> {
                        agent.user!!.work = newAgent.user!!.work
                        work.text = agent.user!!.work
                    }
                    9005 -> {
                        agent.user!!.school1 = newAgent.user!!.school1
                        school1.text = agent.user!!.school1
                    }
                    9006 -> {
                        agent.user!!.school2 = newAgent.user!!.school2
                        school2.text = agent.user!!.school2
                    }
                    9007 -> {
                        agent.user!!.school3 = newAgent.user!!.school3
                        school3.text = agent.user!!.school3
                    }
                }
            }
        } else Toast.makeText(this, "오청 처리가 취소되었습니다.", Toast.LENGTH_SHORT).show()

    }

}