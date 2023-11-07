package com.example.theprivate

import android.content.*
import android.os.*
import java.util.*
import android.util.*
import android.view.*
import android.widget.*
import android.graphics.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.*
import androidx.viewpager2.adapter.*
import com.google.android.material.tabs.*
import androidx.appcompat.app.*

// 갤러리에 올라가는 사진들을 전달하는 어댑터.
//class MyAdapt(var context: Context, var user_id:String) : BaseAdapter() {
class MyAdapt(var act: AppCompatActivity, var user_id:String, var agent:Informer) : BaseAdapter() {
    lateinit var context:Context
    init{
        context = act.applicationContext
    }
    var dao:ArchiveDAO = ArchiveDAO(context, user_id)
    lateinit var tempMap:MutableMap<String, Any>
    lateinit var imgList:MutableList<Bitmap>
    lateinit var scaledImgList:MutableList<Bitmap>
    init{
        tempMap = dao.getPics(10, true, true)
        imgList = tempMap.get("bitmapList") as ArrayList<Bitmap>
        scaledImgList = tempMap.get("scaledBitmapList") as MutableList<Bitmap>
    }
    override fun getCount() = imgList.size
    override fun getView(pos:Int, prevView: View?, parentView: ViewGroup?): View {
        var card: CardView = CardView(context)
        card.radius = 10.0F
        var params1 = Gallery.LayoutParams(300, 600)
        card.layoutParams = params1
        card.setPadding(50, 50, 50, 50)

        var img: ImageView = ImageView(context)
        img.setImageBitmap(scaledImgList[pos])
        img.scaleType = ImageView.ScaleType.FIT_XY
        img.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        card.addView(img)

        // 카드 이미지를 누르면 위의 imgList들을 토대로 포토북을 보여줍니다.
        card.setOnClickListener {
            var intent:Intent = Intent(act, PhotoBookActivity::class.java)
            if((tempMap.get("numOfRows") as Int) == 0) {
                Toast.makeText(context, "이미지가 없습니다.", Toast.LENGTH_SHORT).show()
            } else {
                intent.putExtra("requestWord", "10Photos")
                intent.putExtra("agent", agent)
                act.startActivity(intent)
            }
        }
        return card
    }
    override fun getItem(pos:Int) = 0
    override fun getItemId(pos:Int) = 0L
}

// 메인 화면의 뷰페이저2에 프래그먼트를 연결하는 프래그먼트 스테이트 어댑터
class FragAdapter(var fragAct: FragmentActivity, var agent:Informer) : FragmentStateAdapter(fragAct) {
    override fun getItemCount() = 4
    override fun createFragment(position:Int): Fragment {
        var value = -1
        when(position) {
            0 -> {value = R.layout.home}
            1 -> {value = R.layout.my_page}
            2 -> {value = R.layout.notifications}
            3 -> {value = R.layout.menupage}
        }
        var targetFrag = MyFrag(fragAct as CoreActivity)
        var data = Bundle()
        data.putInt("layInt", value)
        data.putSerializable("agent", agent)
        targetFrag.arguments = data
        return targetFrag
    }

    // for test
    public fun update() {
        notifyDataSetChanged()
    }
}

// 메인 화면의 탭레이아웃에 프래그먼트를 연결하는 탭레이아웃의 stretegy
class MyMediator : TabLayoutMediator.TabConfigurationStrategy {
    var iconIds = intArrayOf(R.drawable.home1, R.drawable.profile, R.drawable.alarm1, R.drawable.menu1)
    override fun onConfigureTab(tab: TabLayout.Tab, position:Int) {
        Log.i("compile -> ", "tab medi!") //
        tab.setIcon(iconIds[position])
    }
}

// 포토북의 뷰페이저2에 프래그먼트를 연결하는 프래그먼트 스테이트 어댑터
class PhotoFragAdapter(var fragAct:FragmentActivity, var agent:Informer, var num:Int=-1, var getHighlight:Boolean=false, var getMemberPic:Boolean=false, var memberList:MutableList<Bitmap>?=null) : FragmentStateAdapter(fragAct) {
    lateinit var dao:ArchiveDAO
    lateinit var imgList:MutableList<Bitmap>
    init{
        if(getMemberPic == true) dao = ArchiveDAO(fragAct as ViewPictureActivity, agent.user!!.id)
        else dao = ArchiveDAO(fragAct as PhotoBookActivity, agent.user!!.id)
        var temp:MutableMap<String, Any> = dao.getPics(num, getHighlight)
        if(getMemberPic == true) imgList = memberList!!
        else imgList = temp.get("bitmapList") as MutableList<Bitmap>
    }
    override fun getItemCount() = imgList.size
    override fun createFragment(pos:Int):Fragment {
        var newFrag:PhotoFrag = PhotoFrag(imgList.get(pos))
        return newFrag
    }
}

// 아티클북의 뷰페이저2에 프래그먼트를 연결하는 프래그먼트 스테이트 어댑터
class ArticleFragAdapter(var fragAct:FragmentActivity, var agent:Informer) : FragmentStateAdapter(fragAct) {
    var dao:ArchiveDAO = ArchiveDAO(fragAct as ArticleBookActivity, agent.user!!.id)
    lateinit var artList:MutableList<ArchiveVO>
    init{
        var temp:MutableMap<String, Any> = dao.getArticles()
        artList = temp.get("articleList") as MutableList<ArchiveVO>
    }
    override fun getItemCount() = artList.size
    override fun createFragment(pos:Int):Fragment {
        var newFrag:ArticleFrag = ArticleFrag(agent, artList.get(pos).writeDate, artList.get(pos).content, fragAct as ArticleBookActivity)
        return newFrag
    }
}