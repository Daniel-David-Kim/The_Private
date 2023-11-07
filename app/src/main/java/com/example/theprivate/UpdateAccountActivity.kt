package com.example.theprivate

import android.app.Activity
import android.os.*
import androidx.appcompat.app.*
import android.widget.*
import android.view.*
import android.content.*
import android.util.*

class UpdateAccountActivity : AppCompatActivity() {
    private lateinit var agent:Informer
    private lateinit var mdao:MemberDAO
    private lateinit var result:HashMap<String, Any>
    private lateinit var dig:AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_account)
        supportActionBar!!.title = "Update Account"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mdao = MemberDAO(this)
        result = mdao.selectAllByMap()

        var data = intent
        agent = intent.getSerializableExtra("agent") as Informer
        Log.i("Login test -> ", agent!!.user!!.id + " : " + agent!!.user!!.name)

        var pw1ip = findViewById<EditText>(R.id.acc_newpw1)
        var pw2ip = findViewById<EditText>(R.id.acc_newpw2)
        var contactip = findViewById<EditText>(R.id.acc_newcontact)
        contactip.setText(agent.user!!.contact)
        var findqip = findViewById<EditText>(R.id.acc_newfindq)
        findqip.setText(agent.user!!.findq)
        var findaip = findViewById<EditText>(R.id.acc_newfinda)
        findaip.setText(agent.user!!.finda)

        // 수정 버튼
        var submit = findViewById<Button>(R.id.acc_newsubmit)
        submit.setOnClickListener {
            // 입력한 값 추출
            var pw1 = pw1ip.text.toString()
            var pw2 = pw2ip.text.toString()
            var contact = contactip.text.toString()
            var findq = findqip.text.toString()
            var finda = findaip.text.toString()
            if((pw1 == null || pw1 == "")&&(pw2 == null || pw2 == "")&&(contact==agent.user!!.contact)&&(findq==agent.user!!.findq)&&(finda==agent.user!!.finda)) Toast.makeText(applicationContext, "변경사항을 입력해주세요.", Toast.LENGTH_SHORT).show()
            else if(pw1 != pw2) Toast.makeText(applicationContext, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            else if((pw1 != null && pw1 != "")&&(pw2 != null && pw2 != "")&&(pw1==pw2)&&pw1.length < 8) Toast.makeText(applicationContext, "비밀번호는 8자 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
            else if((pw1 != null && pw1 != "")&&(pw2 != null && pw2 != "")&&(pw1==pw2)&&pw1 == agent.user!!.pwd) Toast.makeText(applicationContext, "비밀번호가 이전 비밀번호랑 같습니다.", Toast.LENGTH_SHORT).show()
            else if(Validation.contactFormValidate(contact) == false) {
                dig = AlertDialog.Builder(this)
                dig.setIcon(R.mipmap.icon3)
                var msg = ""
                dig.setPositiveButton("확인", null)
                dig.setTitle("Error!")
                msg = "휴대폰 번호 형식에 맞지 않습니다.\n\n010-X000-0000 혹은 011-X00-0000\n(X은 1-9 사이의 정수, 0은 0-9 사이의 정수)\n\n다시 입력해주세요."
                dig.setMessage(msg)
                dig.show()
            } else if((contact != agent.user!!.contact)&&(Validation.contactValidate(contact, (result.get("contacts") as ArrayList<String>)) == false)) {
                dig = AlertDialog.Builder(this)
                dig.setIcon(R.mipmap.icon3)
                var msg = ""
                dig.setPositiveButton("확인", null)
                dig.setTitle("Error!")
                msg = "중복되는 번호입니다.\n다시 입력해주세요."
                dig.setMessage(msg)
                dig.show()
            } else { // 위의 유효성 체크를 다 통과했을 때
                var diView = View.inflate(this@UpdateAccountActivity, R.layout.dialog, null)
                dig = AlertDialog.Builder(this@UpdateAccountActivity)
                dig.setIcon(R.mipmap.icon3)
                dig.setTitle("사용자 확인을 위해 비밀번호를 입력해주세요.")
                dig.setView(diView)
                dig.setPositiveButton("확인", {dialog, which ->
                    var pwip = diView.findViewById<EditText>(R.id.dig_pw)
                    var pw = pwip.text.toString()
                    if(pw == null || pw == "") Toast.makeText(applicationContext, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    else if(pw != agent.user!!.pwd) Toast.makeText(applicationContext, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                    else { // 정보를 알맞게 변경하고 확인 비밀번호도 맞춘 경우 : 가장 정상적ㄹ
                        //Toast.makeText(applicationContext, "합격!", Toast.LENGTH_SHORT).show()
                        var idxArr = arrayOfNulls<Int?>(17)
                        var objArr = arrayOfNulls<Any?>(17)
                        // pw, contact, findq, finda
                        if((pw1 != null && pw1 != "")&&(pw2 != null && pw2 != "")&&(pw1==pw2)&&pw1 != agent.user!!.pwd) { // pw : 이전과 같지 않다면
                            idxArr[1] = 1; objArr[1] = pw1
                        }
                        if(contact != agent.user!!.contact) { // contact : 이전과 같지 않다면
                            idxArr[3] = 1; objArr[3] = contact
                        }
                        if(findq != agent.user!!.findq) { // findq
                            idxArr[4] = 1; objArr[4] = findq
                        }
                        if(finda != agent.user!!.finda) { // finda
                            idxArr[5] = 1; objArr[5] = finda
                        }

                        var resRow = mdao.updateData(idxArr, objArr, agent.user!!.id)
                        var resIntent:Intent = Intent(applicationContext, EditProfileActivity::class.java)
                        if(resRow == 1) {
                            Toast.makeText(this, "계정 정보가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            resIntent.putExtra("update", true)
                            setResult(Activity.RESULT_OK, resIntent)
                        } else {
                            Toast.makeText(this, "계정 정보 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                            resIntent.putExtra("update", false)
                            setResult(Activity.RESULT_OK, resIntent)
                        }
                        finish()
                    }
                })
                dig.setNegativeButton("취소", null)
                dig.show()
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