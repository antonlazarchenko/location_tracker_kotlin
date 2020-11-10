package com.alazar.authfire

import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

open class BaseAuthFragment : Fragment() {

    protected fun showToast(string: String?) {
        val t = Toast.makeText(context, string, Toast.LENGTH_SHORT)
        t.setGravity(Gravity.CENTER, 0, 0)
        t.show()
    }

    protected open fun enableViews(vararg views: View) {
        for (v in views) {
            v.isEnabled = true
        }
    }

    protected open fun disableViews(vararg views: View) {
        for (v in views) {
            v.isEnabled = false
        }
    }
}