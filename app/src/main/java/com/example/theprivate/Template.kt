package com.example.theprivate

import java.io.*
import kotlin.math.*
import android.app.*
import android.text.*
import android.view.*
import android.widget.*
import android.content.*
import android.graphics.*
import android.app.AlertDialog
import androidx.fragment.app.*
import androidx.appcompat.app.*
import androidx.core.view.setPadding
import de.hdodenhof.circleimageview.CircleImageView

class Template(var activity:AppCompatActivity) : Serializable {
    // px를 dp로 변환하기 위한 숫자
    var context = activity as Context
    var density:Float = context.resources.displayMetrics.density
    var den:Int = Math.round(density)
    lateinit var agent:Informer
    var noAlarm = false

    fun getNoArticle():LinearLayout {
        var pan = LinearLayout(context)
        var params0 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        pan.layoutParams = params0
        var target:TextView = TextView(context)
        var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        params.setMargins(0, 10*den, 0, 0)
        target.gravity = Gravity.CENTER_HORIZONTAL
        target.layoutParams = params
        target.text = if(noAlarm == false) "게시글이 없습니다.  게시글을 등록해주세요." else "알림이 없습니다."
        pan.addView(target)
        return pan
    }

    fun createArticleTemplate(profile:Bitmap?, name:String, date:String, contentText:String, artId:Int, img1:Bitmap?, img2:Bitmap?, img3:Bitmap?, img4:Bitmap?, requestCode:Int, checkHide:Int=0, highlight:Int=0):LinearLayout {

        var base = LinearLayout(context)
        var params0 = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params0.setMargins(0, 0, 0, 10*den)
        base.layoutParams = params0
        base.orientation = LinearLayout.VERTICAL
        base.setBackgroundColor(Color.WHITE)


        var upper = LinearLayout(context)
        upper.orientation = LinearLayout.VERTICAL
        var params0_1 = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        upper.setPadding(12*den, 12*den, 12*den, 12*den)


        //header
        var header = LinearLayout(context)
        var params1 = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params1.setMargins(0, 0, 0, 7*den)

        var civ = CircleImageView(context)
        var params2 = LinearLayout.LayoutParams(45*den, 45*den)
        params2.weight = 1.0F
        civ.layoutParams = params2
        if(profile == null) civ.setImageResource(R.drawable.profile)
        else civ.setImageBitmap(profile)
        header.addView(civ)

            // rest
        var rest = LinearLayout(context)
        var params3 = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params3.weight = 20.0F
        rest.setPadding(8*den, 0, 0, 0)
        rest.orientation = LinearLayout.VERTICAL

        var tv1 = TextView(context)
        var params4 = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        tv1.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        tv1.setTextSize(18.0F)
        tv1.setTextColor(Color.BLACK)
        tv1.text = name

        var tv2 = TextView(context)
        tv2.setTextSize(12.0F)
        tv2.setTextColor(Color.BLACK)
        tv2.text = date

        rest.addView(tv1, params4)
        rest.addView(tv2, params4)

        header.addView(rest, params3)

        var dot3 = ImageView(context)  // 헤더의 우단 점 3개 메뉴
        // 점 3개를 누르면 메뉴가 나오도록
        dot3.setOnClickListener {
            var dig = AlertDialog.Builder(context)
            var labels = arrayOf<String>("글 수정", "글 삭제")
            dig.setItems(labels){dialog, pos ->
                when(pos) {
                    0 -> {
                        var intent:Intent = Intent(context, RevisePostActivity::class.java)
                        intent.putExtra("articleId", artId)
                        intent.putExtra("agent", agent)
                        intent.putExtra("requestCode", requestCode)
                        context.startActivity(intent)
                    }
                    1 -> {
                        var dao = ArchiveDAO(context, agent.user!!.id)
                        var resRow = dao.deleteData(artId)
                        if(resRow == 1) {
                            Toast.makeText(context, "게시물 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                            agent.requestCode = requestCode
                            activity.finish()
                            activity.overridePendingTransition(0, 0)
                            var intent:Intent = activity.intent
                            activity.startActivity(intent)
                            activity.overridePendingTransition(0, 0)
                        }
                    }
                }
            }
            dig.show()
        }
        var params5 = LinearLayout.LayoutParams(25*den, 25*den)
        params5.weight = 2.0F
        dot3.setImageResource(R.drawable.dot3)
        header.addView(dot3, params5)
        //header

        // body
        var body = LinearLayout(context)
        body.orientation = LinearLayout.VERTICAL
        body.gravity = Gravity.CENTER
        body.setBackgroundColor(Color.WHITE)

        var tv3 = TextView(context)
        var params6 = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        tv3.ellipsize = TextUtils.TruncateAt.END
        tv3.maxLines = 12
        tv3.text = contentText
        tv3.setTextColor(Color.BLACK)
        body.addView(tv3, params6)

        var tv4 = TextView(context) // 게시글 id를 저장해놓았다가 CRUD에 사용하려는 용도. 당연히 안보인다.
        tv4.visibility = ViewGroup.GONE
        tv4.text = artId.toString()
        body.addView(tv4, params6)
        // body

        //photoZone
        var photoZone = LinearLayout(context)
        photoZone.orientation = LinearLayout.VERTICAL

        var photoBlock1 = LinearLayout(context)
        var params7_0 = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params7_0.weight = 1.0F
        photoBlock1.orientation = LinearLayout.HORIZONTAL

        var ivblock1 = ImageView(context)
        var params7 = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200*den)
        params7.weight = 1.0F
        if(img1 != null) ivblock1.setImageBitmap(img1)
        else ivblock1.visibility = ViewGroup.GONE
        var ivblock2 = ImageView(context)
        if(img2 != null) ivblock2.setImageBitmap(img2)
        else ivblock2.visibility = ViewGroup.GONE
        photoBlock1.addView(ivblock1, params7)
        photoBlock1.addView(ivblock2, params7)

        var photoBlock2 = LinearLayout(context)
        photoBlock2.orientation = LinearLayout.HORIZONTAL
        var ivblock3 = ImageView(context)
        if(img3 != null) ivblock3.setImageBitmap(img3)
        else ivblock3.visibility = ViewGroup.GONE
        var ivblock4 = ImageView(context)
        if(img4 != null) ivblock4.setImageBitmap(img4)
        else ivblock4.visibility = ViewGroup.GONE
        photoBlock2.addView(ivblock3, params7)
        photoBlock2.addView(ivblock4, params7)

        photoZone.addView(photoBlock1, params7_0)
        photoZone.addView(photoBlock2, params7_0)
        //photoZone
        
        //최종 컨테이너인 upper에 싹다 붙임
        upper.addView(header, params1)
        upper.addView(body, params1)
        upper.addView(photoZone, params6)


        // lower
        var lower = LinearLayout(context)
        var params8 = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 45*den)
        lower.gravity = Gravity.CENTER
        lower.orientation = LinearLayout.HORIZONTAL

        var ib1 = ImageView(context)
        var params9 = LinearLayout.LayoutParams(280*den, 30*den)
        params9.weight = 1.0F

        // 이상하게 생각하지 말 것! agent는 이미 MyFrag에서 프래그먼트를 생성할 때 할당해주었다! MyFrag의 1-1-1 여기서!
        var highlightC = highlight
        var archiveDAO = ArchiveDAO(context, agent.user!!.id)
        if(highlight == 0) ib1.setImageResource(R.drawable.highlight)
        else ib1.setImageResource(R.drawable.highlight_c)
        ib1.setOnClickListener {
            var resRow = -1
            var change = -1
            if(highlightC == 0) change = 1
            else change = 0
            highlightC = change
            resRow = archiveDAO.updateSwitch(1, artId, change)
            if(resRow == 1) {
                if(change == 0) {
                    Toast.makeText(context, "하이라이트가 해제되었습니다.", Toast.LENGTH_SHORT).show()
                    ib1.setImageResource(R.drawable.highlight)
                } else {
                    Toast.makeText(context, "하이라이트로 설정되었습니다.", Toast.LENGTH_SHORT).show()
                    ib1.setImageResource(R.drawable.highlight_c)
                }
            } else Toast.makeText(context, "토글에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
        ib1.setPadding(4*den)

        var pinC = checkHide
        var ib2 = ImageView(context)
        if(checkHide == 0) ib2.setImageResource(R.drawable.pin)
        else ib2.setImageResource(R.drawable.pinned)
        ib2.setOnClickListener {
            var resRow = -1
            var change = -1
            if(pinC == 0) change = 1
            else change = 0
            pinC = change
            resRow = archiveDAO.updateSwitch(2, artId, change)
            if(resRow == 1) {
                if(change == 0) {
                    Toast.makeText(context, "핀이 해제되었습니다.", Toast.LENGTH_SHORT).show()
                    ib2.setImageResource(R.drawable.pin)
                } else {
                    Toast.makeText(context, "핀이 설정되었습니다.", Toast.LENGTH_SHORT).show()
                    ib2.setImageResource(R.drawable.pinned)
                }
            } else Toast.makeText(context, "토글에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
        ib2.setPadding(4*den)


        lower.addView(ib1, params9)
        lower.addView(ib2, params9)
        //lower

        // 베이스에 upper과 lower 다 추가
        base.addView(upper, params0_1)
        base.addView(lower, params8)

        base.setOnClickListener {
            var intent = Intent(context, ViewPostActivity::class.java)
            intent.putExtra("agent", agent)
            intent.putExtra("article_id", artId)
            activity.startActivity(intent)
        }

        return base
    }

    fun createAlarmTemplate(profile:Bitmap?, content:String, date:String, almId:Int):LinearLayout {
        var base = LinearLayout(context)
        var param1 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        param1.setMargins(0, 0, 0, 7*den)
        base.orientation = LinearLayout.HORIZONTAL
        base.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
        base.weightSum = 39.0F
        base.layoutParams = param1

        var civ:CircleImageView = CircleImageView(context)
        var param2 = LinearLayout.LayoutParams(0,75*den)
        param2.weight = 10.0F
        if(profile == null) civ.setImageResource(R.drawable.profile)
        else civ.setImageBitmap(profile)
        base.addView(civ, param2)

        var contentPane:LinearLayout = LinearLayout(context)
        var param3 = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        param3.weight = 25.0F
        contentPane.orientation = LinearLayout.VERTICAL

        var tv1 = TextView(context)
        var param4 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        tv1.ellipsize = TextUtils.TruncateAt.END
        tv1.maxLines = 3
        tv1.text = content
        tv1.setTextColor(Color.BLACK)
        contentPane.addView(tv1, param4)

        var tv2 = TextView(context)
        var param5 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        param5.setMargins(0, 5*den, 0, 0)
        tv2.text = date
        tv2.setTextColor(context.getColor(R.color.gray_50))
        tv2.textSize = 13.0F
        contentPane.addView(tv2, param5)

        base.addView(contentPane, param3)

        var iv = ImageView(context)
        var param7 = LinearLayout.LayoutParams(0, 20*den)
        param7.weight = 2.0F
        iv.setImageResource(R.mipmap.dot)
        iv.setOnClickListener {
            var dig = AlertDialog.Builder(context)
            var labels = arrayOf<String>("알림 삭제")
            dig.setItems(labels){dialog, pos ->
                when(pos) {
                    0 -> {
                        var dao = AlarmDAO(context, agent.user!!.id)
                        var resRow = dao.deleteAlarm(almId)
                        if(resRow == 1) {
                            Toast.makeText(context, "알림 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()


                            agent.requestCode = 610
                            activity.finish()
                            activity.overridePendingTransition(0, 0)
                            var intent:Intent = activity.intent
                            activity.startActivity(intent)
                            activity.overridePendingTransition(0, 0)
                        }
                    }
                }
            }
            dig.show()
        }
        base.addView(iv, param7)

        var tv3 = TextView(context)
        tv3.text = almId.toString()
        tv3.visibility = LinearLayout.GONE
        base.addView(tv3, param4)

        return base
    }

}