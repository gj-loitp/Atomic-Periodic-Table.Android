package com.mckimquyen.atomicPeriodicTable.act

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mckimquyen.atomicPeriodicTable.R
import com.mckimquyen.atomicPeriodicTable.pref.ThemePref
import kotlinx.android.synthetic.main.a_calculator.backBtn
import kotlinx.android.synthetic.main.a_calculator.editElement1
import kotlinx.android.synthetic.main.a_calculator.editNumber1
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class CalculatorAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        val themePref = ThemePref(this)
        val themePrefValue = themePref.getValue()
        if (themePrefValue == 0) {
            setTheme(R.style.AppTheme)
        }
        if (themePrefValue == 1) {
            setTheme(R.style.AppThemeDark)
        }
        setContentView(R.layout.a_calculator) //Don't move down (Needs to be before we call our functions)

        backBtn.setOnClickListener {
            this.onBackPressed()
        }

        initUi()
    }

    private fun initUi() {
        editElement1.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                onClickSearch()
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun onClickSearch() {
        val mArray = resources.getStringArray(R.array.calcArray)
        val text = editElement1.text.toString()

        for (i in 0 until 2) {
            var jsonString: String?
            if (text == mArray[i].toString()) {

                if (text == "H") {
                    val ext = ".json"
                    val elementJson = "1$ext"

                    val inputStream: InputStream = assets.open(elementJson)
                    jsonString = inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(jsonString)
                    val jsonObject: JSONObject = jsonArray.getJSONObject(0)
                    val elementAtomicWeight1 = jsonObject.optString("element_atomicmass", "---")

                    val final =
                        elementAtomicWeight1.toInt() * (editNumber1.text.toString().toInt())

                    Toast.makeText(this, final, Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
}