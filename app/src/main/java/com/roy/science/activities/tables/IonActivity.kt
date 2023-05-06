package com.roy.science.activities.tables

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roy.science.R
import com.roy.science.activities.BaseActivity
import com.roy.science.adapter.IonAdapter
import com.roy.science.anim.Anim
import com.roy.science.model.Ion
import com.roy.science.model.IonModel
import com.roy.science.preferences.ThemePreference
import com.roy.science.utils.Utils
import kotlinx.android.synthetic.main.activity_dictionary.titleBox
import kotlinx.android.synthetic.main.activity_ion.back_btn_ion
import kotlinx.android.synthetic.main.activity_ion.closeEleSearchIon
import kotlinx.android.synthetic.main.activity_ion.commonTitleBackIon
import kotlinx.android.synthetic.main.activity_ion.editIon
import kotlinx.android.synthetic.main.activity_ion.emptySearchBoxIon
import kotlinx.android.synthetic.main.activity_ion.ionDetail
import kotlinx.android.synthetic.main.activity_ion.ionView
import kotlinx.android.synthetic.main.activity_ion.search_bar_ion
import kotlinx.android.synthetic.main.activity_ion.searchBtnIon
import kotlinx.android.synthetic.main.activity_ion.viewIon
import kotlinx.android.synthetic.main.ion_details.detail_background_ion
import kotlinx.android.synthetic.main.ion_details.ionDetailTitle
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.util.Locale

class IonActivity : BaseActivity(), IonAdapter.OnIonClickListener {
    private var ionList = ArrayList<Ion>()
    private var mAdapter = IonAdapter(list = ionList, clickListener = this, context = this)

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
        setContentView(R.layout.activity_ion) //REMEMBER: Never move any function calls above this

        recyclerView()
        clickSearch()
        viewIon.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        detail_background_ion.setOnClickListener {
            Utils.fadeOutAnim(ionDetail, 300)
        }
        back_btn_ion.setOnClickListener { this.onBackPressed() }
    }

    @SuppressLint("SetTextI18n")
    override fun ionClickListener(item: Ion, position: Int) {
        if (item.count > 1) {
            Utils.fadeInAnim(ionDetail, 300)
            ionDetailTitle.text = ((item.name).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            } + " " + "ionization")
            var jsonString: String? = null
            val ext = ".json"
            val element = item.name
            val elementJson: String = "$element$ext"
            val inputStream: InputStream = assets.open(elementJson.toString())
            jsonString = inputStream.bufferedReader().use { it.readText() }

            val jsonArray = JSONArray(jsonString)
            val jsonObject: JSONObject = jsonArray.getJSONObject(0)
            for (i in 1..item.count) {
                val text = "element_ionization_energy"
                val add = i.toString()
                val final = (text + add)
                val ionization = jsonObject.optString(final, "---")
                val extText = i.toString()
                val name = "ion_text_"
                val eView = "$name$extText"
                val iText =
                    findViewById<TextView>(resources.getIdentifier(eView, "id", packageName))
                val dot = "."
                val space = " "
                iText.text = "$i$dot$space$ionization"
                iText.visibility = View.VISIBLE
            }
            for (i in (item.count + 1)..30) {
                val extText = i.toString()
                val name = ("ion_text_")
                val eView = "$name$extText"
                val iText =
                    findViewById<TextView>(resources.getIdentifier(eView, "id", packageName))
                iText.visibility = View.GONE
            }
        }
    }

    override fun onApplySystemInsets(
        top: Int,
        bottom: Int,
        left: Int,
        right: Int
    ) {
        ionView.setPadding(
            /* left = */ 0,
            /* top = */
            resources.getDimensionPixelSize(R.dimen.title_bar) + resources.getDimensionPixelSize(R.dimen.margin_space) + top,
            /* right = */
            0,
            /* bottom = */
            resources.getDimensionPixelSize(R.dimen.title_bar)
        )
        val params2 = commonTitleBackIon.layoutParams as ViewGroup.LayoutParams
        params2.height = top + resources.getDimensionPixelSize(R.dimen.title_bar)
        commonTitleBackIon.layoutParams = params2

        val searchEmptyImgPrm = emptySearchBoxIon.layoutParams as ViewGroup.MarginLayoutParams
        searchEmptyImgPrm.topMargin = top + (resources.getDimensionPixelSize(R.dimen.title_bar))
        emptySearchBoxIon.layoutParams = searchEmptyImgPrm
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun recyclerView() {
        val ionView = findViewById<RecyclerView>(R.id.ionView)
        val ionList = ArrayList<Ion>()
        IonModel.getList(ionList)
        ionView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val adapter = IonAdapter(ionList, this, this)
        ionView.adapter = adapter
        adapter.notifyDataSetChanged()

        editIon.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filter(s.toString(), ionList, ionView)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filter(text: String, list: ArrayList<Ion>, recyclerView: RecyclerView) {
        val filteredList: ArrayList<Ion> = ArrayList()
        for (item in list) {
            if (item.name.lowercase(Locale.ROOT).contains(text.lowercase(Locale.ROOT))) {
                filteredList.add(item)
            }
        }
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if (recyclerView.adapter!!.itemCount == 0) {
                Anim.fadeIn(emptySearchBoxIon, 300)
            } else {
                emptySearchBoxIon.visibility = View.GONE
            }
        }, 10)
        mAdapter.filterList(filteredList)
        mAdapter.notifyDataSetChanged()
        recyclerView.adapter = IonAdapter(list = filteredList, clickListener = this, context = this)
    }

    private fun clickSearch() {
        searchBtnIon.setOnClickListener {
            Utils.fadeInAnim(search_bar_ion, 150)
            Utils.fadeOutAnim(titleBox, 1)
            editIon.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editIon, InputMethodManager.SHOW_IMPLICIT)
        }
        closeEleSearchIon.setOnClickListener {
            Utils.fadeOutAnim(search_bar_ion, 1)
            val delayClose = Handler(Looper.getMainLooper())
            delayClose.postDelayed({
                Utils.fadeInAnim(titleBox, 150)
            }, 151)

            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    override fun onBackPressed() {
        if (ionDetail.visibility == View.VISIBLE) {
            Utils.fadeOutAnim(ionDetail, 300)
            return
        } else {
            super.onBackPressed()
        }
    }

}