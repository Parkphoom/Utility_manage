package com.example.utility_manage.PublicAction


class Publiclayout {
    fun setActionBar(heading: String?, actionBar: androidx.appcompat.app.ActionBar?) {
        // TODO Auto-generated method stub
        actionBar!!.setHomeButtonEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)
//        actionBar.setBackgroundDrawable(ColorDrawable(context.getResources().getColor(R.color.title_bar_gray)))
        actionBar.setTitle(heading)
        actionBar.show()
    }
}