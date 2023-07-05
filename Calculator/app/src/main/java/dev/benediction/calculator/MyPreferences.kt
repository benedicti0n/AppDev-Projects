package dev.benediction.calculator

import android.content.Context
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate.*
import com.google.gson.Gson

class MyPreferences(context: Context) {

    // https://proandroiddev.com/dark-mode-on-android-app-with-kotlin-dc759fc5f0e1
    companion object {

        private const val KEY_VIBRATION_STATUS = "example.calculator.KEY_VIBRATION_STATUS"
        private const val KEY_HISTORY = "example.calculator..HISTORY"
        private const val KEY_PREVENT_PHONE_FROM_SLEEPING = "example.calculator.PREVENT_PHONE_FROM_SLEEPING"
        private const val KEY_HISTORY_SIZE = "example.calculator.HISTORY_SIZE"
        private const val KEY_SCIENTIFIC_MODE_ENABLED_BY_DEFAULT = "example.calculator.SCIENTIFIC_MODE_ENABLED_BY_DEFAULT"
        private const val KEY_RADIANS_INSTEAD_OF_DEGREES_BY_DEFAULT = "example.calculator.RADIANS_INSTEAD_OF_DEGREES_BY_DEFAULT"
        private const val KEY_NUMBER_PRECISION = "example.calculator.NUMBER_PRECISION"
        private const val KEY_WRITE_NUMBER_INTO_SCIENTIC_NOTATION = "example.calculator.WRITE_NUMBER_INTO_SCIENTIC_NOTATION"
        private const val KEY_LONG_CLICK_TO_COPY_VALUE = "example.calculator.LONG_CLICK_TO_COPY_VALUE"
        private const val KEY_ADD_MODULO_BUTTON = "example.calculator.ADD_MODULO_BUTTON"
        private const val KEY_SPLIT_PARENTHESIS_BUTTON = "example.calculator.SPLIT_PARENTHESIS_BUTTON"


    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)


    var vibrationMode = preferences.getBoolean(KEY_VIBRATION_STATUS, true)
        set(value) = preferences.edit().putBoolean(KEY_VIBRATION_STATUS, value).apply()
    var scientificMode = preferences.getBoolean(KEY_SCIENTIFIC_MODE_ENABLED_BY_DEFAULT, false)
        set(value) = preferences.edit().putBoolean(KEY_SCIENTIFIC_MODE_ENABLED_BY_DEFAULT, value).apply()
    var useRadiansByDefault = preferences.getBoolean(KEY_RADIANS_INSTEAD_OF_DEGREES_BY_DEFAULT, false)
        set(value) = preferences.edit().putBoolean(KEY_RADIANS_INSTEAD_OF_DEGREES_BY_DEFAULT, value).apply()
    private var history = preferences.getString(KEY_HISTORY, null)
        set(value) = preferences.edit().putString(KEY_HISTORY, value).apply()
    var preventPhoneFromSleepingMode = preferences.getBoolean(KEY_PREVENT_PHONE_FROM_SLEEPING, false)
        set(value) = preferences.edit().putBoolean(KEY_PREVENT_PHONE_FROM_SLEEPING, value).apply()
    var historySize = preferences.getString(KEY_HISTORY_SIZE, "100")
        set(value) = preferences.edit().putString(KEY_HISTORY_SIZE, value).apply()
    var numberPrecision = preferences.getString(KEY_NUMBER_PRECISION, "10")
        set(value) = preferences.edit().putString(KEY_NUMBER_PRECISION, value).apply()
    var numberIntoScientificNotation = preferences.getBoolean(
        KEY_WRITE_NUMBER_INTO_SCIENTIC_NOTATION, false)
        set(value) = preferences.edit().putBoolean(KEY_WRITE_NUMBER_INTO_SCIENTIC_NOTATION, value).apply()
    var longClickToCopyValue = preferences.getBoolean(KEY_LONG_CLICK_TO_COPY_VALUE, true)
        set(value) = preferences.edit().putBoolean(KEY_LONG_CLICK_TO_COPY_VALUE, value).apply()
    var addModuloButton = preferences.getBoolean(KEY_ADD_MODULO_BUTTON, true)
        set(value) = preferences.edit().putBoolean(KEY_ADD_MODULO_BUTTON, value).apply()
    var splitParenthesisButton = preferences.getBoolean(KEY_SPLIT_PARENTHESIS_BUTTON, false)
        set(value) = preferences.edit().putBoolean(KEY_SPLIT_PARENTHESIS_BUTTON, value).apply()



    fun getHistory(): MutableList<History> {
        val gson = Gson()
        return if (preferences.getString(KEY_HISTORY, null) != null) {
            gson.fromJson(history, Array<History>::class.java).asList().toMutableList()
        } else {
            mutableListOf()
        }
    }

    fun saveHistory(context: Context, history: List<History>){
        val gson = Gson()
        val history2 = history.toMutableList()
        while (historySize!!.toInt() > 0 && history2.size > historySize!!.toInt()) {
            history2.removeAt(0)
        }
        MyPreferences(context).history = gson.toJson(history2) // Convert to json
    }
}
