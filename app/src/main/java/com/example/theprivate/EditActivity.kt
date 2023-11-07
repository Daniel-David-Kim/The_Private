package com.example.theprivate

import java.util.*
import androidx.appcompat.app.*
import android.os.*
import de.hdodenhof.circleimageview.*
import android.widget.*
import android.view.*
import android.graphics.*
import android.content.*
import android.app.*
import android.util.Log
import androidx.annotation.RequiresApi

class EditActivity : AppCompatActivity() {
    lateinit var agent:Informer
    private lateinit var members:MemberDAO

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        var intent = intent
        agent = intent.getSerializableExtra("agent") as Informer
        var category = intent.getStringExtra("what")
        members = MemberDAO(this)
        supportActionBar!!.title = "Edit Profile"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        agent = intent.getSerializableExtra("agent") as Informer
        Log.i("user", agent.user!!.name)

        var fetches = arrayOf<String>("loc", "work", "school1", "school2", "school3")
        if(category == "bio") {
            setContentView(R.layout.edit_bio)
            var profile = findViewById<CircleImageView>(R.id.edbio_profile)
            if(agent.user!!.profile != null) {
                var bitmap = BitmapFactory.decodeByteArray(agent.user!!.profile, 0, agent.user!!.profile!!.size)
                profile.setImageBitmap(bitmap)
            }
            var name = findViewById<TextView>(R.id.edbio_name)
            name.text = agent.user!!.name
            var submit = findViewById<Button>(R.id.edbio_submit)
            var textfield = findViewById<EditText>(R.id.edbio_bio)
            if((agent.user!!.comment != null) and !agent.user!!.comment.equals("")) {
                textfield.setText(agent.user!!.comment)
            }
            submit.setOnClickListener {
                if(textfield.text.toString() == agent.user!!.comment) {
                    Toast.makeText(applicationContext, "변경사항을 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    var idxArr = arrayOfNulls<Int?>(17)
                    var objArr = arrayOfNulls<Any?>(17)
                    idxArr[10] = 1
                    objArr[10] = textfield.text.toString()

                    var resRow = members.updateData(idxArr, objArr, agent.user!!.id)
                    var resIntent:Intent = Intent(applicationContext, EditProfileActivity::class.java)
                    if(resRow == 1) {
                        Toast.makeText(this, "bio가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        agent.user!!.comment = textfield.text.toString()
                        resIntent.putExtra("revise", true)
                        resIntent.putExtra("agent", agent)
                        setResult(Activity.RESULT_OK, resIntent)
                    } else {
                        Toast.makeText(this, "bio 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        resIntent.putExtra("revise", false)
                        resIntent.putExtra("agent", agent)
                        setResult(Activity.RESULT_OK, resIntent)
                    }
                    finish()
                }
            }

        } else if(category == "nickname") {
            setContentView(R.layout.edit_name)
            var name = findViewById<EditText>(R.id.edname_name)
            var submit = findViewById<Button>(R.id.edname_submit)
            name.setText(agent.user!!.name)
            submit.setOnClickListener {
                if((name.text == null) or name.text.toString().equals("")){
                    Toast.makeText(applicationContext, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else if(name.text.toString() == agent.user!!.name) {
                    Toast.makeText(applicationContext, "변경사항을 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    var idxArr = arrayOfNulls<Int?>(17)
                    var objArr = arrayOfNulls<Any?>(17)
                    idxArr[2] = 1
                    objArr[2] = name.text.toString()

                    var resRow = members.updateData(idxArr, objArr, agent.user!!.id)
                    var resIntent:Intent = Intent(applicationContext, EditProfileActivity::class.java)
                    if(resRow == 1) {
                        Toast.makeText(this, "이름이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        agent.user!!.name = name.text.toString()
                        resIntent.putExtra("revise", true)
                        resIntent.putExtra("agent", agent)
                        setResult(Activity.RESULT_OK, resIntent)
                    } else {
                        Toast.makeText(this, "이름 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        resIntent.putExtra("revise", false)
                        resIntent.putExtra("agent", agent)
                        setResult(Activity.RESULT_OK, resIntent)
                    }
                    finish()
                }
            }
        } else if(category in fetches) {
            setContentView(R.layout.edit_name)
            var title = findViewById<TextView>(R.id.edname_title)
            when(category) {
                "loc" -> {title.text = "거주하시는 지역을 알려주세요!"}
                "work" -> {title.text = "거주하시는 지역을 알려주세요!"}
                "school1", "school2", "school3" -> {title.text = "다니시는, 혹은 다니셨던 학교 이름이 무엇인가요?"}
            }
            var content = findViewById<TextView>(R.id.edname_content)
            when(category) {
                "loc" -> {content.text = "회원 정보의 거주지 항목에 저장됩니다!"}
                "work" -> {content.text = "회원 정보의 직장 항목에 저장됩니다!"}
                "school1", "school2", "school3" -> {content.text = "회원 정보의 학교 항목에 저장됩니다!"}
            }

            var loc = findViewById<EditText>(R.id.edname_name)
            when(category) {
                "loc" -> {
                    loc.hint = "Location"
                    if((agent.user!!.location != null) and !agent.user!!.location.equals("")) loc.setText(agent.user!!.location)
                }
                "work" -> {
                    loc.hint = "Workplace"
                    if((agent.user!!.work != null) and !agent.user!!.work.equals("")) loc.setText(agent.user!!.work)
                }
                "school1", "school2", "school3" -> {
                    loc.hint = "School"
                    if(category == "school1") if((agent.user!!.school1 != null) and !agent.user!!.school1.equals("")) loc.setText(agent.user!!.school1)
                    else if(category == "school2") if((agent.user!!.school2 != null) and !agent.user!!.school2.equals("")) loc.setText(agent.user!!.school2)
                    else if(category == "school3") if((agent.user!!.school3 != null) and !agent.user!!.school3.equals("")) loc.setText(agent.user!!.school3)
                }
            }

            var noChange = false
            var submit = findViewById<Button>(R.id.edname_submit)
            submit.setOnClickListener {
                when(category) {
                    "loc" -> {
                        if(loc.text.toString() == agent.user!!.location) {
                            Toast.makeText(applicationContext, "변경사항을 입력해주세요.", Toast.LENGTH_SHORT).show()
                            noChange = true
                        }
                    }
                    "work" -> {
                        if(loc.text.toString() == agent.user!!.work) {
                            Toast.makeText(applicationContext, "변경사항을 입력해주세요.", Toast.LENGTH_SHORT).show()
                            noChange = true
                        }
                    }
                    "school1", "school2", "school3" -> {
                        if(category == "school1") {
                            if(loc.text.toString() == agent.user!!.school1) {
                                Toast.makeText(applicationContext, "변경사항을 입력해주세요.", Toast.LENGTH_SHORT).show()
                                noChange = true
                            }
                        } else if(category == "school2") {
                            if(loc.text.toString() == agent.user!!.school2) {
                                Toast.makeText(applicationContext, "변경사항을 입력해주세요.", Toast.LENGTH_SHORT).show()
                                noChange = true
                            }
                        } else if(category == "school3") {
                            if(loc.text.toString() == agent.user!!.school3) {
                                Toast.makeText(applicationContext, "변경사항을 입력해주세요.", Toast.LENGTH_SHORT).show()
                                noChange = true
                            }
                        }
                    }
                }

                if(noChange == false) {
                    var idxArr = arrayOfNulls<Int?>(17)
                    var objArr = arrayOfNulls<Any?>(17)
                    var idx:Int = -1
                    var label:String = ""
                    when(category) {
                        "loc" -> { idx = 12; label = "위치 정보"}
                        "work" -> { idx = 11; label = "직장 정보"}
                        "school1" -> { idx = 13; label = "학교 정보"}
                        "school2" -> { idx = 14; label = "학교 정보"}
                        "school3" -> { idx = 15; label = "학교 정보"}
                    }
                    idxArr[idx] = 1
                    objArr[idx] = loc.text.toString()


                    var resRow = members.updateData(idxArr, objArr, agent.user!!.id)
                    var resIntent:Intent = Intent(applicationContext, EditProfileActivity::class.java)
                    if(resRow == 1) {
                        Toast.makeText(this, "${label}가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        when(category) {
                            "loc" -> { agent.user!!.location = loc.text.toString() }
                            "work" -> { agent.user!!.work = loc.text.toString() }
                            "school1" -> { agent.user!!.school1 = loc.text.toString() }
                            "school2" -> { agent.user!!.school2 = loc.text.toString() }
                            "school3" -> { agent.user!!.school3 = loc.text.toString() }
                        }
                        resIntent.putExtra("revise", true)
                        resIntent.putExtra("agent", agent)
                        setResult(Activity.RESULT_OK, resIntent)
                    } else {
                        Toast.makeText(this, "${label} 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        resIntent.putExtra("revise", false)
                        resIntent.putExtra("agent", agent)
                        setResult(Activity.RESULT_OK, resIntent)
                    }
                    finish()
                }
            }
        } else if(category == "birth") {
            setContentView(R.layout.edit_birth)
            var dp = findViewById<DatePicker>(R.id.edbirth_birth)
            var submit = findViewById<Button>(R.id.edbirth_submit)
            var date = ""
            dp.setOnDateChangedListener {view, year, month, day ->
                var dateStr = String.format("%d-%d-%d", year, month, day)
                date = Converter.changeStrToFormat(dateStr)
            }
            submit.setOnClickListener{
                if((date == "") or (date == agent.user!!.birth)) {
                    Toast.makeText(applicationContext, "변경사항을 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    var idxArr = arrayOfNulls<Int?>(17)
                    var objArr = arrayOfNulls<Any?>(17)
                    idxArr[16] = 1
                    objArr[16] = date

                    var resRow = members.updateData(idxArr, objArr, agent.user!!.id)
                    var resIntent:Intent = Intent(applicationContext, EditProfileActivity::class.java)
                    if(resRow == 1) {
                        Toast.makeText(this, "생일이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        agent.user!!.birth = date
                        resIntent.putExtra("revise", true)
                        resIntent.putExtra("agent", agent)
                        setResult(Activity.RESULT_OK, resIntent)
                    } else {
                        Toast.makeText(this, "생일 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        resIntent.putExtra("revise", false)
                        resIntent.putExtra("agent", agent)
                        setResult(Activity.RESULT_OK, resIntent)
                    }
                    finish()
                }
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

}