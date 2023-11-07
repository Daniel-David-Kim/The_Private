package com.example.theprivate

import androidx.appcompat.app.*
import android.os.*
import android.widget.*
import android.view.*
import android.util.*

class SignupActivity : AppCompatActivity() {
    private lateinit var mdao:MemberDAO
    private lateinit var result:HashMap<String, Any>
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        mdao = MemberDAO(this)
        result = mdao.selectAllByMap()
        supportActionBar!!.hide()

        var retBtn = findViewById<ImageView>(R.id.signup_ret)
        retBtn.setOnClickListener {
            finish()
        }
        var ids:IntArray = intArrayOf(R.id.signup_id, R.id.signup_pw1, R.id.signup_pw2, R.id.signup_name, R.id.signup_contact, R.id.signup_findq, R.id.signup_finda, R.id.signup_submit)
        var views:Array<View?> = arrayOfNulls<View?>(8)
        for(i:Int in 0..(views.size-1) step 1) views[i] = findViewById<View>(ids[i])

        views[7]!!.setOnClickListener {
            var iparr = arrayOfNulls<String?>(7)
            for(i:Int in 0..(iparr.size-1) step 1) iparr[i] = (views[i] as EditText).text.toString()
            var dig = AlertDialog.Builder(this)
            dig.setIcon(R.mipmap.icon3)
            dig.setTitle("Error!")
            var msg = ""
            dig.setPositiveButton("확인", null)
            var no_data:Boolean = false

            for(i:String? in iparr) {
                if((i == null) or (i == "")) {
                    msg = "모든 입력항목은 필수입니다."
                    dig.setMessage(msg)
                    dig.show()
                    no_data = true
                }
            }
            if(no_data == false) {
                if(Validation.idValidate(iparr[0]!!, result.get("ids") as MutableList<String>) == false) {
                    msg = "중복되는 아이디입니다.\n다시 입력하세요."
                    dig.setMessage(msg)
                    dig.show()
                } else if(iparr[1] != iparr[2]) {
                    msg = "비밀번호가 일치하지 않습니다.\n다시 입력해주세요."
                    dig.setMessage(msg)
                    dig.show()
                } else if (iparr[1]!!.length < 8) {
                    msg = "비밀번호는 8자 이상이어야 합니다.\n다시 입력해주세요."
                    dig.setMessage(msg)
                    dig.show()
                } else if (Validation.contactFormValidate(iparr[4]!!) == false) {
                    msg = "휴대폰 번호 형식에 맞지 않습니다.\n\n010-X000-0000 혹은 011-X00-0000\n(X은 1-9 사이의 정수, 0은 0-9 사이의 정수)\n\n다시 입력해주세요."
                    dig.setMessage(msg)
                    dig.show()
                } else if (Validation.contactValidate(iparr[4]!!, (result.get("contacts") as ArrayList<String>)) == false) {
                    msg = "중복되는 번호입니다.\n다시 입력해주세요."
                    dig.setMessage(msg)
                    dig.show()
                } else {
                    var newMember:MemberVO = MemberVO(iparr[0]!!, iparr[1]!!, iparr[3]!!, iparr[4]!!, iparr[5]!!, iparr[6]!!)
                    var lastRow = mdao.insertData(newMember)
                    if(lastRow != -1L) {
                        var archiveDBOpener:ArchiveDB = ArchiveDB(this, iparr[0]!!)
                        var archiveDB = archiveDBOpener.writableDatabase
                        archiveDBOpener.onCreate(archiveDB)

                        Toast.makeText(applicationContext, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else Toast.makeText(applicationContext, "데이터 삽입에 실패했습니다....", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}