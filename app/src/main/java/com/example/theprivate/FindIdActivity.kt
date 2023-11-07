package com.example.theprivate

import android.app.*
import android.content.*
import androidx.appcompat.app.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog

class FindIdActivity : AppCompatActivity() {
    private lateinit var mdao:MemberDAO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.findid)
        supportActionBar!!.title = "Find Id"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mdao = MemberDAO(this)

        var name = findViewById<EditText>(R.id.findid_name)
        var contact = findViewById<EditText>(R.id.findid_contact)

        var submit = findViewById<Button>(R.id.findid_submit)
        submit.setOnClickListener {
            var dig = AlertDialog.Builder(this)
            dig.setIcon(R.mipmap.icon3)
            var msg = ""
            dig.setPositiveButton("확인", null)
            var nameText = name.text.toString()
            var contactText = contact.text.toString()

            // 하나도 안입력했을 때
            if((nameText == null || nameText == "")||(contactText == null || contactText == ""))
                Toast.makeText(applicationContext, "모든 입력사항은 필수입니다.", Toast.LENGTH_SHORT).show()
            else if(Validation.contactFormValidate(contactText) == false) {
                dig.setTitle("Error!")
                msg = "휴대폰 번호 형식에 맞지 않습니다.\n\n010-X000-0000 혹은 011-X00-0000\n(X은 1-9 사이의 정수, 0은 0-9 사이의 정수)\n\n다시 입력해주세요."
                dig.setMessage(msg)
                dig.show()
            } else { // 제대로 입력했을 때
                var result:HashMap<String, Any> = mdao.findId(nameText, contactText)
                if((result.get("numOfRows") as Int) == 0) Toast.makeText(applicationContext, "회원 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                else {
                    var target = result.get("member") as MemberVO?
                    dig.setTitle("Info")
                    msg = "아이디는 ${target!!.id}입니다."
                    dig.setMessage(msg)
                    dig.show()
                }
            }
        }
        var findpw = findViewById<TextView>(R.id.findid_next)
        findpw.setOnClickListener{
            var intent:Intent = Intent(applicationContext, FindPWActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        if(item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

}