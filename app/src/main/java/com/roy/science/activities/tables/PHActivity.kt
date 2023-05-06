package com.roy.science.activities.tables

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import com.roy.science.R
import com.roy.science.activities.BaseActivity
import com.roy.science.model.Indicator
import com.roy.science.model.IndicatorModel
import com.roy.science.preferences.ThemePreference
import kotlinx.android.synthetic.main.activity_ph.acidInfo
import kotlinx.android.synthetic.main.activity_ph.alkalineInfo
import kotlinx.android.synthetic.main.activity_ph.backBtnPh
import kotlinx.android.synthetic.main.activity_ph.center
import kotlinx.android.synthetic.main.activity_ph.commonTitleBackPh
import kotlinx.android.synthetic.main.activity_ph.left
import kotlinx.android.synthetic.main.activity_ph.neutralInfo
import kotlinx.android.synthetic.main.activity_ph.phScroll
import kotlinx.android.synthetic.main.activity_ph.right
import kotlinx.android.synthetic.main.activity_ph.viewPh
import kotlinx.android.synthetic.main.bar_ph_chips.bromothymolBlueBtn
import kotlinx.android.synthetic.main.bar_ph_chips.congoRedBtn
import kotlinx.android.synthetic.main.bar_ph_chips.methylOrangeBtn
import kotlinx.android.synthetic.main.bar_ph_chips.phenolphthalein_btn

class PHActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        val themePreference = ThemePreference(this)
        val themePrefValue = themePreference.getValue()

        if (themePrefValue == 100) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    setTheme(R.style.AppTheme)
                }

                Configuration.UI_MODE_NIGHT_YES -> {
                    setTheme(R.style.AppThemeDark)
                }
            }
        }
        if (themePrefValue == 0) {
            setTheme(R.style.AppTheme)
        }
        if (themePrefValue == 1) {
            setTheme(R.style.AppThemeDark)
        }
        setContentView(R.layout.activity_ph) //REMEMBER: Never move any function calls above this

        indicatorListener()
        viewPh.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        //Title Controller

        backBtnPh.setOnClickListener {
            this.onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun indicatorListener() {
        val indicatorList = ArrayList<Indicator>()
        IndicatorModel.getList(indicatorList)
        val acidText = "[H+]>[OH-] pH<"
        val neutralText = "[H+]=[OH-] pH="
        val alkalineText = "[H+]<[OH-] pH>"

        val item = indicatorList[0]
        acidInfo.text = acidText + item.acid
        neutralInfo.text = neutralText + item.neutral
        alkalineInfo.text = alkalineText + item.alkali
        updatePhColor(item)
        updateButtonColor("bromothymol_blue_btn")

        bromothymolBlueBtn.setOnClickListener {
            val item = indicatorList[0]
            acidInfo.text = acidText + item.acid
            neutralInfo.text = neutralText + item.neutral
            alkalineInfo.text = alkalineText + item.alkali
            updatePhColor(item)
            updateButtonColor("bromothymol_blue_btn")
        }
        methylOrangeBtn.setOnClickListener {
            val item = indicatorList[1]
            acidInfo.text = acidText + item.acid
            neutralInfo.text = neutralText + item.neutral
            alkalineInfo.text = alkalineText + item.alkali
            updatePhColor(item)
            updateButtonColor("methyl_orange_btn")
        }
        congoRedBtn.setOnClickListener {
            val item = indicatorList[2]
            acidInfo.text = acidText + item.acid
            neutralInfo.text = neutralText + item.neutral
            alkalineInfo.text = alkalineText + item.alkali
            updatePhColor(item)
            updateButtonColor("congo_red_btn")
        }
        phenolphthalein_btn.setOnClickListener {
            val item = indicatorList[3]
            acidInfo.text = acidText + item.acid
            neutralInfo.text = neutralText + item.neutral
            alkalineInfo.text = alkalineText + item.alkali
            updatePhColor(item)
            updateButtonColor("phenolphthalein_btn")
        }
    }

    private fun updatePhColor(item: Indicator) {
        val leftColor = resources.getIdentifier(item.acidColor, "color", packageName)
        val centerColor = resources.getIdentifier(item.neutralColor, "color", packageName)
        val rightColor = resources.getIdentifier(item.alkaliColor, "color", packageName)

        left.setColorFilter(
            ContextCompat.getColor(this, leftColor),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        center.setColorFilter(
            ContextCompat.getColor(this, centerColor),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        right.setColorFilter(
            ContextCompat.getColor(this, rightColor),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    private fun updateButtonColor(btn: String) {
        methylOrangeBtn.background = getDrawable(R.drawable.chip)
        bromothymolBlueBtn.background = getDrawable(R.drawable.chip)
        congoRedBtn.background = getDrawable(R.drawable.chip)
        phenolphthalein_btn.background = getDrawable(R.drawable.chip)

        val delay = Handler(Looper.getMainLooper())
        delay.postDelayed({
            val resIDB = resources.getIdentifier(btn, "id", packageName)
            val button = findViewById<Button>(resIDB)
            button.background = getDrawable(R.drawable.chip_active)
        }, 1)
    }

    override fun onApplySystemInsets(
        top: Int,
        bottom: Int,
        left: Int,
        right: Int
    ) {
        val paramsTitle = commonTitleBackPh.layoutParams as ViewGroup.LayoutParams
        paramsTitle.height = top + resources.getDimensionPixelSize(R.dimen.title_bar_ph)
        commonTitleBackPh.layoutParams = paramsTitle

        val pScroll = phScroll.layoutParams as ViewGroup.MarginLayoutParams
        pScroll.topMargin = top + resources.getDimensionPixelSize(R.dimen.title_bar_ph)
        phScroll.layoutParams = pScroll
    }
}