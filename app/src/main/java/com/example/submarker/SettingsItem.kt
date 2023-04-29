package com.example.submarker

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class SettingsItem(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    init {
        inflate(context, R.layout.settings_item, this)
        val settingsItemStyle = context.obtainStyledAttributes(attrs, R.styleable.SettingsItem)

        val ivIcon = findViewById<ImageView>(R.id.iv_icon)
        val tvSettingName = findViewById<TextView>(R.id.tv_setting_name)

        try {
            ivIcon.setImageDrawable(settingsItemStyle.getDrawable(R.styleable.SettingsItem_iconSrc))
            tvSettingName.text = settingsItemStyle.getString(R.styleable.SettingsItem_settingsName)
        } finally {
            settingsItemStyle.recycle()
        }
    }
}