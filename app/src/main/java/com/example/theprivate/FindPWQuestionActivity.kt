package com.example.theprivate

import android.os.*
import androidx.appcompat.app.*
import android.widget.*
import android.view.*
import android.app.*
import androidx.appcompat.app.AlertDialog

class FindPWQuestionActivity : AppCompatActivity() {
    private lateinit var mdao:MemberDAO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.findpw_question)
        supportActionBar!!.title = "Question"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mdao = MemberDAO(this)

        var data = intent
        var idText = data.getStringExtra("findpw")
        var result:HashMap<String, Any> = mdao.selectOneTargetById(idText!!)

        var memberInfo:MemberVO? = result.get("member") as MemberVO?

        var question = findViewById<TextView>(R.id.findpwque_que)
        question.text = memberInfo!!.findq
        var answer = findViewById<EditText>(R.id.findpwque_ans)

        var submit = findViewById<Button>(R.id.findpwque_submit)
        submit.setOnClickListener {
            var ansText = answer.text.toString()
            if(ansText == null || ansText == "")
                Toast.makeText(applicationContext, "비밀번호 찾기 질문의 답을 입력해주세요.", Toast.LENGTH_SHORT).show()
            else {
                var dig = AlertDialog.Builder(this)
                dig.setIcon(R.mipmap.icon3)
                var msg = ""
                if(ansText == memberInfo!!.finda) {
                    dig.setTitle("Info")
                    dig.setPositiveButton("확인", null)
                    msg = "비밀번호는 ${memberInfo!!.pwd}입니다."
                    dig.setMessage(msg)
                    dig.show()
                } else Toast.makeText(applicationContext, "회원 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        if(item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

}