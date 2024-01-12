package com.alquran.shonchar

import android.app.Activity

class ApplicationTheme(context: Activity) {
    init {
        if (ApplicationData(context).darkTheme) {
            when (ApplicationData(context).primaryColor) {
                ApplicationData.PURPLE -> context.setTheme(R.style.ThemeDarkPurple)
                ApplicationData.BLUE -> context.setTheme(R.style.ThemeDarkBlue)
                ApplicationData.ORANGE -> context.setTheme(R.style.ThemeDarkOrange)
            }
        } else {
            when (ApplicationData(context).primaryColor) {
                ApplicationData.PURPLE -> context.setTheme(R.style.ThemeLightPurple)
                ApplicationData.BLUE -> context.setTheme(R.style.ThemeLightBlue)
                ApplicationData.ORANGE -> context.setTheme(R.style.ThemeLightOrange)
            }
        }
    }
}