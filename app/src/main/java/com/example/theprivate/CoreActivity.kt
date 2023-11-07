package com.example.theprivate

import android.app.Activity
import android.os.*
import androidx.appcompat.app.*
import android.widget.*
import android.content.*
import android.graphics.*
import java.io.*
import android.util.*
import android.view.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.*
import androidx.viewpager2.widget.*
import androidx.appcompat.widget.*

class CoreActivity : AppCompatActivity(), Serializable {//}, TabLayout.OnTabSelectedListener {
    lateinit var tablay:TabLayout
    private var agent:Informer? = null
    lateinit var fa:FragAdapter
    lateinit var vp2:ViewPager2
    lateinit var sp_main:LinearLayout

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("initial  Core ----> ", "1")
        setContentView(R.layout.activity_main)
        var intent:Intent = intent
        agent = intent.getSerializableExtra("agent") as Informer

        var archiveDBOpener:ArchiveDB = ArchiveDB(applicationContext, agent!!.user!!.id)
        var archiveDB = archiveDBOpener.writableDatabase
        archiveDBOpener.onCreate(archiveDB)
        archiveDB.close()
        archiveDBOpener.close()

        supportActionBar!!.setIcon(R.drawable.small1)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = ""


        Log.i("Login test -> ", agent!!.user!!.id + " : " + agent!!.user!!.name)
        vp2 = findViewById<ViewPager2>(R.id.vp2)
        vp2.registerOnPageChangeCallback(object:ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.i("tabnum ---->" ,  position.toString())
                var num:Int = -1
                when(position) {
                    0 -> {num = 608}
                    1 -> {num = 609}
                    2 -> {num = 610}
                    3 -> {num = 611}
                }
                agent!!.requestCode = num

            }
        })

        sp_main = findViewById<LinearLayout>(R.id.scroll_main)
        fa = FragAdapter(this, agent!!)
        vp2.adapter = fa
        tablay = findViewById<TabLayout>(R.id.tabs)
        tablay.setBackgroundColor(Color.WHITE)
        tablay.setSelectedTabIndicatorColor(applicationContext.getColor(R.color.main_color))
        //container = findViewById<LinearLayout>(R.id.container)
        TabLayoutMediator(tablay, vp2, MyMediator()).attach()
    }

    override fun onResume() { // agent의 requestCode가 무엇인지에 따라서 다른 탭으로 이동한다.
        super.onResume()
        Log.i("CoreActivity -----> ", "레주메")
        var idx:Int = 0
        if(agent!!.requestCode == 609) idx = 1
        else if(agent!!.requestCode == 610) idx = 2
        else if(agent!!.requestCode == 611) idx = 3
        vp2.setCurrentItem(idx)
    }

    public override fun onRestart() {
        super.onRestart()
        Log.i("CoreActivity -----> ", "리수따뚜")
        
        // 다시 돌아올 때마다 액티비티를 자동 새로고침하는 로직
        finish()
        overridePendingTransition(0, 0)
        var intent:Intent = intent
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        var hideAll = agent!!.user!!.hideAll
        var hideChecked = agent!!.user!!.hideChecked
        var sm1:SubMenu = menu!!.addSubMenu("0")
        var sm2:SubMenu = menu!!.addSubMenu("1")
        var sm3:SubMenu = menu!!.addSubMenu("2")
        var sm4:SubMenu = menu!!.addSubMenu("3")
        if(hideChecked == 1) sm4.add(0, 555, 0, "핀 숨기기 해제")
        else sm4.add(0, 555, 0, "핀 숨기기")
        var sm5:SubMenu = menu!!.addSubMenu("4")
        var sm7:SubMenu = menu!!.addSubMenu("5")
        if(hideAll == 1)  sm7.add(0, 777, 0, "숨기기 해제")
        else sm7.add(0, 777, 0, "숨기기")
        var sm8:SubMenu = menu!!.addSubMenu("6")
        var sm9:SubMenu = menu!!.addSubMenu("7")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        super.onContextItemSelected(item)
        Log.i("context selected", "-----> in! (Core)")
        when(item.itemId) {
            555, 777 -> {
                var members:MemberDAO = MemberDAO(this)
                var hideAll = agent!!.user!!.hideAll
                var hideChecked = agent!!.user!!.hideChecked
                var idx:Int = -1
                var label = ""
                when(item.itemId) {
                    555 -> {idx = 9; label = "핀 숨기기 상태가 변경되었습니다."}
                    777 -> {idx = 8; label = "숨기기 상태가 변경되었습니다."}
                }
                var idxArr = arrayOfNulls<Int?>(17)
                var objArr = arrayOfNulls<Any?>(17)
                idxArr[idx] = 1
                var change:Int = -1
                when(item.itemId) {
                    555 -> {
                        if(hideChecked == 1) change = 0
                        else {
                            if(hideAll == 1) {
                                Toast.makeText(this, "먼저 모두 숨기기를 해제해주세요.", Toast.LENGTH_SHORT).show()
                                return true
                            }
                            change = 1

                        }
                        agent!!.user!!.hideChecked = change
                    }
                    777 -> {
                        if(hideAll == 1) change = 0
                        else {
                            if(hideChecked == 1) {
                                Toast.makeText(this, "먼저 핀 숨기기를 해제해주세요.", Toast.LENGTH_SHORT).show()
                                return true
                            }
                            change = 1
                        }
                        agent!!.user!!.hideAll = change
                    }
                }
                objArr[idx] = change
                var resRow = members.updateData(idxArr, objArr, agent!!.user!!.id)
                if(resRow == 1) Toast.makeText(this, label, Toast.LENGTH_SHORT).show()
                else Toast.makeText(this, label, Toast.LENGTH_SHORT).show()
                //finish()

                onRestart()
                return true
            }
        }
        return true
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("onSaveIntanceState ----> ", "CoreActivity : 세이브 인스턴스 스테이트!!! clearing.....")
        outState.clear()
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("welcome back! ---> ", "코어 액티비티 : 온 액티비티 리절트!")

        if(resultCode == Activity.RESULT_OK) {
            Log.i("welcome back! ---> ", "코어 액티비티 : 온 액티비티 리절트! 이상 무!")
            if(data!!.getSerializableExtra("agent") != null) {
                agent = data!!.getSerializableExtra("agent") as Informer
                fa.agent = agent!!
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("CoreActivity --------->  ", "onDestroy : 액티비티 파괴!")
    }

}