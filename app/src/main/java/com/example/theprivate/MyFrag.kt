package com.example.theprivate

import android.app.*
import android.app.AlertDialog
import android.content.*
import android.graphics.*
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import de.hdodenhof.circleimageview.CircleImageView
import java.io.*

// 메인화면의 프래그먼트들.
class MyFrag : Fragment {
    private lateinit var members:MemberDAO
    private lateinit var archive:ArchiveDAO
    private lateinit var alarms:AlarmDAO
    private lateinit var coreContext: Context
    private lateinit var template:Template
    private lateinit var agent:Informer
    private lateinit var coreAct:CoreActivity
    private lateinit var ref:Reference
    //  11111
    private lateinit var posts:MutableList<LinearLayout>
    //

    constructor() : super()
    constructor(coreContext:CoreActivity) : super() {
        this.coreAct = coreContext
        this.coreContext = coreContext
        template = Template(coreContext)
    }
    lateinit var thisView: View
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var data = arguments
        thisView = inflater.inflate(data!!.getInt("layInt"), container, false)
        members = MemberDAO(coreAct)
        this.agent = data!!.getSerializable("agent") as Informer

        // db에 변동이 생기면 곧바로 user에 적용할 수 있도록 members 테이블에 접속하여 유저의 정보를 가져와 갱신합니다.
        this.agent.user = members.selectOneTargetById(agent.user!!.id).get("member") as MemberVO
        ref = Reference(coreAct, agent)

        template.agent = this.agent  // 1-1-1
        var hideAll = agent.user!!.hideAll
        var hideChecked = agent.user!!.hideChecked

        archive = ArchiveDAO(coreContext, agent.user!!.id)
        alarms = AlarmDAO(coreContext, agent.user!!.id)

        //  11111
        posts = mutableListOf<LinearLayout>() // *****

        var res:MutableMap<String, Any> = mutableMapOf<String, Any>()
        if(hideChecked == 1) res = archive.getAllArticleMap(1)
        else if(hideAll == 1) res.put("numOfRows", 0)
        else res = archive.getAllArticleMap()

        if(res.get("archive") != null) {
        for(article in (res.get("archive") as MutableList<ArchiveVO>)) {
            var options:BitmapFactory.Options = BitmapFactory.Options()
            options.inSampleSize = 4
            var bitmaps:Array<Bitmap?> = arrayOfNulls<Bitmap?>(4)
            var arrs:Array<ByteArray?> = arrayOf<ByteArray?>(article.pic1, article.pic2, article.pic3, article.pic4)
            for(i:Int in 0..(arrs.size-1) step 1) {
                if(arrs[i] == null) bitmaps[i] = null
                else {
                    bitmaps[i] = BitmapFactory.decodeByteArray(arrs[i], 0, arrs[i]!!.size)
                    bitmaps[i] = Bitmap.createScaledBitmap(bitmaps[i]!!, 500, 500, true)
                }
            }
            var profile: Bitmap? = null
            if(agent.user!!.profile != null) profile = BitmapFactory.decodeByteArray(agent.user!!.profile, 0, agent.user!!.profile!!.size)
            posts.add(template.createArticleTemplate(profile, agent.user!!.name, article.writeDate, article.content, article.id, bitmaps[0], bitmaps[1], bitmaps[2], bitmaps[3], 608, article.checkHide, article.highlight))
        }
        }
        //

        if(data!!.getInt("layInt") == R.layout.home) { // 홈 탭에 위치하면

            // 상단 갤러리 바 설정
            var gallery = thisView.findViewById<Gallery>(R.id.gallery01)
            //gallery.adapter = MyAdapt(this.requireContext(), agent!!.user!!.id)
            gallery.adapter = MyAdapt(coreAct, agent!!.user!!.id, agent)

            var res:MutableMap<String, Any> = mutableMapOf<String, Any>()
            if(hideChecked == 1) res = archive.getAllArticleMap(1)
            else if(hideAll == 1) res.put("numOfRows", 0)
            else res = archive.getAllArticleMap()


            //  11111
            if((res.get("numOfRows") as Int) == 0) (thisView as ScrollView).findViewById<LinearLayout>(R.id.home_base).addView(template.getNoArticle())
            else {
                for(post in posts) (thisView as ScrollView).findViewById<LinearLayout>(R.id.home_base).addView(post)
            }
            //

        } else if(data!!.getInt("layInt") == R.layout.my_page) { // 나의 페이지 탭에 위치하면
            var nickname =  thisView.findViewById<TextView>(R.id.my_nickname)
            var comment =  thisView.findViewById<TextView>(R.id.my_intro)
            nickname.text = agent.user!!.name
            comment.text = agent.user!!.comment

            var my_loc = thisView.findViewById<LinearLayout>(R.id.my_loc)
            if((agent.user!!.location != null) and (agent.user!!.location != "")) {
                my_loc.visibility = LinearLayout.VISIBLE
                (my_loc.getChildAt(2) as TextView).text = agent.user!!.location
            }

            var my_birth = thisView.findViewById<LinearLayout>(R.id.my_birth)
            if((agent.user!!.birth != null) and (agent.user!!.birth != "")) {
                my_birth.visibility = LinearLayout.VISIBLE
                (my_birth.getChildAt(2) as TextView).text = agent.user!!.birth
            }

            var my_work = thisView.findViewById<LinearLayout>(R.id.my_work)
            if((agent.user!!.work != null) and (agent.user!!.work != "")) {
                my_work.visibility = LinearLayout.VISIBLE
                (my_work.getChildAt(2) as TextView).text = agent.user!!.work
            }

            var my_school1 = thisView.findViewById<LinearLayout>(R.id.my_school1)
            if((agent.user!!.school1 != null) and (agent.user!!.school1 != "")) {
                my_school1.visibility = LinearLayout.VISIBLE
                (my_school1.getChildAt(2) as TextView).text = agent.user!!.school1
            }

            var my_school2 = thisView.findViewById<LinearLayout>(R.id.my_school2)
            if((agent.user!!.school2 != null) and (agent.user!!.school2 != "")) {
                my_school2.visibility = LinearLayout.VISIBLE
                (my_school2.getChildAt(2) as TextView).text = agent.user!!.school2
            }

            var my_school3 = thisView.findViewById<LinearLayout>(R.id.my_school3)
            if((agent.user!!.school3 != null) and (agent.user!!.school3 != "")) {
                my_school3.visibility = LinearLayout.VISIBLE
                (my_school3.getChildAt(2) as TextView).text = agent.user!!.school3
            }

////////////////////////////////////////
            var ids = intArrayOf(R.id.my_cover, R.id.profileExpand)
            var memberImgs = ref.getImagesAndSet(thisView, ids, 1702, this)
            memberImgs = ref.getImagesAndSet(thisView, ids, 1702, this, proRef=memberImgs[1], covRef=memberImgs[0])
///////////////////////////////////////

            var myEditBtn = thisView.findViewById<Button>(R.id.my_profile_edit)
            myEditBtn.setOnClickListener {
                agent.requestCode = 609
                var intent = Intent(this.coreContext, EditProfileActivity::class.java)
                intent.putExtra("agent", agent)
                startActivityForResult(intent, 709)
            }

            var posting = thisView.findViewById<Button>(R.id.my_post) // add post 버튼
            posting.setOnClickListener{
                agent.requestCode = 609
                var intent = Intent(this.coreContext, AddPostActivity::class.java)
                intent.putExtra("agent", agent)
                startActivityForResult(intent, 609)
            }

            var res:MutableMap<String, Any> = mutableMapOf<String, Any>()
            if(hideChecked == 1) res = archive.getAllArticleMap(1)
            else if(hideAll == 1) res.put("numOfRows", 0)
            else res = archive.getAllArticleMap()


            //  11111
            if((res.get("numOfRows") as Int) == 0) (thisView as ScrollView).findViewById<LinearLayout>(R.id.my_base).addView(template.getNoArticle())
            else {
                for(post in posts) (thisView as ScrollView).findViewById<LinearLayout>(R.id.my_base).addView(post)
            }
            //

        } else if(data!!.getInt("layInt") == R.layout.notifications) { // 알림 탭에 위치하면
            var res:MutableMap<String, Any> = alarms.getAllAlarmMap()
            if((res.get("numOfRows") as Int) == 0) {
                template.noAlarm = true
                (thisView as ScrollView).findViewById<LinearLayout>(R.id.note_base).addView(template.getNoArticle())
                template.noAlarm = false
            } else {
                // agent.requestCode = 610
                var profile: Bitmap? = null
                if(agent.user!!.profile != null) profile = BitmapFactory.decodeByteArray(agent.user!!.profile, 0, agent.user!!.profile!!.size)
                for(alarm in (res.get("alarm") as MutableList<AlarmVO>)) {
                    (thisView as ScrollView).findViewById<LinearLayout>(R.id.note_base).addView(template.createAlarmTemplate(profile, alarm.content, alarm.writeDate, alarm.id))
                }
            }

        } else if(data!!.getInt("layInt") == R.layout.menupage) { // 메뉴 페이지 탭에 위치하면
            var profileTab = (thisView as ScrollView).findViewById<LinearLayout>(R.id.menu_profile)
            profileTab.setOnClickListener { coreAct.vp2.setCurrentItem(1) }

            coreAct.registerForContextMenu(profileTab)

            // 계정 정보 업데이트 버튼
            var update_account = (thisView as ScrollView).findViewById<Button>(R.id.menu_update)
            update_account.setOnClickListener {
                var intent:Intent = Intent(coreAct, UpdateAccountActivity::class.java)
                intent.putExtra("agent", agent)
                startActivityForResult(intent, 2022)
            }

            var profile = (thisView as ScrollView).findViewById<CircleImageView>(R.id.menu_profilepic)
            if(agent.user!!.profile != null) {
                var bitmap = BitmapFactory.decodeByteArray(agent.user!!.profile, 0, agent.user!!.profile!!.size)
                profile.setImageBitmap(bitmap)
            }
            var nickname = (thisView as ScrollView).findViewById<TextView>(R.id.menu_nickname)
            nickname.text = agent.user!!.name
            var logout = (thisView as ScrollView).findViewById<Button>(R.id.menu_logout)
            logout.setOnClickListener {
                archive.helper.close()
                alarms.helper.close()
                agent.user = null

                var intent:Intent = Intent(coreAct, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                coreAct.finish()
            }

            var photobook = (thisView as ScrollView).findViewById<LinearLayout>(R.id.photobook_btn)
            photobook.setOnClickListener {
                var intent:Intent = Intent(this.coreContext, PhotoBookActivity::class.java)
                // 일반 포토북 요청은 Default
                var map = archive.getPics(-1, false)
                if((map.get("numOfRows") as Int) == 0) {
                    Toast.makeText(coreContext, "저장된 이미지가 없습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    intent.putExtra("requestWord", "Default")
                    intent.putExtra("agent", agent)
                    startActivity(intent)
                }
            }
            var articlebook = (thisView as ScrollView).findViewById<LinearLayout>(R.id.articlebook_btn)
            articlebook.setOnClickListener {
                var intent:Intent = Intent(this.coreContext, ArticleBookActivity::class.java)
                var map = archive.getArticles()
                if((map.get("numOfRows") as Int) == 0) {
                    Toast.makeText(coreContext, "저장된 게시글이 없습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    intent.putExtra("agent", agent)
                    startActivity(intent)
                }
            }
        }
        return thisView
    }

    override fun onActivityResult(requestCode: Int, responseCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, responseCode, data)
        // 여기 지점에서 분기 처리 : requestCode가 안걸리면 밑으로 넘어가게
        Log.i("onActivityResult ----> ", "MyFrag  : 온 액티비티 리절트!!!!!! 프래그먼트!")

        ref.imagesActivityResult(requestCode, responseCode, data, 1702, 1703)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("onSaveIntanceState ----> ", "MyFrag : 세이브드 인스턴스 스테이트!!! clearing.....")
        outState.clear()
    }

    override fun onResume() {
        super.onResume()
        Log.i("onSaveIntanceState ----> ", "MyFrag  : 온 레주메!!!!!! 프래그먼트!")
    }

}

// 포토북 화면의 프래그먼트들.
class PhotoFrag : Fragment {
    private lateinit var curBitmap:Bitmap
    constructor() : super()
    constructor(curBitmap:Bitmap) {
        this.curBitmap = curBitmap
    }
    override fun onCreate(savedInstanceState:Bundle?) {super.onCreate(savedInstanceState)}
    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?, savedInstanceState:Bundle?):View? {
        var thisView = inflater.inflate(R.layout.photo_frag, container, false)
        var fragImage = (thisView as LinearLayout).findViewById<ImageView>(R.id.fragImage)
        fragImage.setImageBitmap(this.curBitmap)
        return thisView
    }
}

// 아티클북 화면의 프래그먼트들.
class ArticleFrag : Fragment {
    private lateinit var agent:Informer
    private lateinit var date:String
    private lateinit var content:String
    private lateinit var artAct:ArticleBookActivity

    constructor() : super()
    constructor(agent: Informer, date:String, content:String, artAct:ArticleBookActivity) {
        this.agent = agent
        this.date = date
        this.content = content
        this.artAct = artAct
    }
    override fun onCreate(savedInstanceState:Bundle?) {super.onCreate(savedInstanceState)}
    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?, savedInstanceState:Bundle?):View? {
        var thisView = inflater.inflate(R.layout.article_frag, container, false)
        var profile = (thisView as ScrollView).findViewById<ImageView>(R.id.artbook_profile)
        var name = (thisView as ScrollView).findViewById<TextView>(R.id.artbook_name)
        var date = (thisView as ScrollView).findViewById<TextView>(R.id.artbook_date)
        var content = (thisView as ScrollView).findViewById<TextView>(R.id.artbook_body)
        var options:BitmapFactory.Options = BitmapFactory.Options()
        options.inSampleSize = 4
        if(agent.user!!.profile != null) {
            var bitmap = BitmapFactory.decodeByteArray(agent.user!!.profile, 0, agent.user!!.profile!!.size, options)
            var scaled = Bitmap.createScaledBitmap(bitmap, 450, 450, true)
            profile.setImageBitmap(scaled)
        }
        name.text = agent.user!!.name
        date.text = this.date
        content.text = this.content
        var retBtn = (thisView as ScrollView).findViewById<ImageView>(R.id.artbook_return)
        retBtn.setOnClickListener {
            artAct.finish()
        }
        return thisView
    }
}