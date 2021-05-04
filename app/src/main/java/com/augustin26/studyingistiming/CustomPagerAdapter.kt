package com.augustin26.studyingistiming

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class CustomPagerAdapter : PagerAdapter() {

    //데이터베이스 연결
    var helper:DBHelper? = null
    var views = listOf<View>()

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as View
    }

    override fun getCount(): Int {
        return views.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = views.get(position)
        container.addView(view)
        return view
    }

    //destroyItem 메서드의 역할
    //뷰페이저는 기본적으로 한 번에 3개의 페이지를 생상하기 때문에 속하지 않는 페이지는 삭제해서 메모리 효율을 높인다.
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "A"
            1 -> "B"
            else -> "C"
        }
    }
}