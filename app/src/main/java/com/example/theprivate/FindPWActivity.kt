package com.example.theprivate

import android.os.*
import androidx.appcompat.app.*
import android.widget.*
import android.view.*
import android.app.*
import android.content.*
import androidx.appcompat.app.AlertDialog

class FindPWActivity : AppCompatActivity() {
    private lateinit var mdao:MemberDAO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.findpw)
        supportActionBar!!.title = "Find Password"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mdao = MemberDAO(this)

        var idip = findViewById<EditText>(R.id.findpw_id)

        var submit = findViewById<Button>(R.id.findpw_submit)
        submit.setOnClickListener {
            var idText = idip.text.toString()
            if(idText == null || idText == "")
                Toast.makeText(applicationContext, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
            else {
                var result:HashMap<String, Any> = mdao.selectOneTargetById(idText)
                if((result.get("numOfRows") as Int) == 0) Toast.makeText(applicationContext, "회원 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                else {
                    var intent:Intent = Intent(applicationContext, FindPWQuestionActivity::class.java)
                    intent.putExtra("findpw", idText)
                    startActivity(intent)
                }
            }
        }

        var findid = findViewById<TextView>(R.id.findpw_findid)
        findid.setOnClickListener {
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        if(item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

}