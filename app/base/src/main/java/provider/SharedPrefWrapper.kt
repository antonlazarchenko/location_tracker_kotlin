package provider

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.alazar.base.R
import com.alazar.base.core.PreferenceProvider
import com.alazar.base.di.BaseApp
import javax.inject.Inject

class SharedPrefWrapper @Inject constructor() : PreferenceProvider {

    @Inject
    lateinit var context: Context

    init {
        BaseApp().getComponent().inject(this)
        Log.d("************ CONTEXT", context.toString())
    }

    private var preferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.shared_preference_name),
        AppCompatActivity.MODE_PRIVATE
    )

    override fun saveServiceStatus(status: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(context.getString(R.string.preference_service_param), status)
        editor.apply()
    }

    override fun getServiceStatus(): Boolean {
        return preferences.getBoolean(context.getString(R.string.preference_service_param), false)
    }
}
