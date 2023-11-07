package com.example.theprivate

import java.util.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.view.*
import android.app.*
import android.content.*
import android.graphics.*
import android.util.Log

class MainActivity : AppCompatActivity() {
    private lateinit var mdao:MemberDAO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        mdao = MemberDAO(this)
        supportActionBar!!.hide()

        var memberDBOpener:MemberDB = MemberDB(this)
        var memberDB = memberDBOpener.writableDatabase
        memberDBOpener.onCreate(memberDB)
        memberDB.close()
        memberDBOpener.close()

        var user_id = findViewById<EditText>(R.id.user_id)
        var user_pw = findViewById<EditText>(R.id.user_pw)

        var login = findViewById<Button>(R.id.login)
        login.setOnClickListener {
            var id = user_id.text.toString()
            var pw = user_pw.text.toString()
            if(((id == null) or (id == "")) or ((pw == null) or (pw == ""))) {
                Toast.makeText(applicationContext, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                var res:HashMap<String, Any> = mdao.selectOneTarget(id, pw)
                if((res.get("numOfRows") as Int) == 0) {
                    Toast.makeText(applicationContext, "회원 정보가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.i("initial  login ----> ", "1")
                    var agent:Informer = Informer()
                    agent.user = (res.get("member")!! as MemberVO)
                    var intent = Intent(this, CoreActivity::class.java)
                    intent.putExtra("agent", agent)

                    var alarmDBOpener:AlarmDB = AlarmDB(this, agent.user!!.id)
                    var alarmDB = alarmDBOpener.writableDatabase
                    alarmDBOpener.onCreate(alarmDB)
                    alarmDB.close()
                    alarmDBOpener.close()


                    var alarmDAO:AlarmDAO = AlarmDAO(this, agent.user!!.id)
                    var curDate = Converter.changeDateToFormat(Date())
                    var newAlarm = AlarmVO(-1, curDate, "you logined.")
                    alarmDAO.insertAlarm(newAlarm)
                    alarmDAO.helper.close()

                    Toast.makeText(applicationContext, agent.user!!.name + "님! 환영합니다!", Toast.LENGTH_SHORT).show()
                    Log.i("initial  login ----> ", "4")
                    startActivity(intent)
                    Log.i("initial  login ----> ", "5")
                }
            }
        }

        var signup:TextView = findViewById<TextView>(R.id.signup)
        signup.setOnClickListener {
            var intent = Intent(applicationContext, SignupActivity::class.java)
            startActivity(intent)
        }

        var findidpw:TextView = findViewById<TextView>(R.id.findidpw)
        findidpw.setOnClickListener {
            var intent = Intent(applicationContext, FindIdActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("onSaveIntanceState ----> ", "MainActivity : 세이브드 인스턴스 스테이트!!! clearing.....")
        outState.clear()
    }
}