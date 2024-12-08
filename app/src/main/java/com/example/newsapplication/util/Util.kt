package com.example.newsapplication.util

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.TouchDelegate
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.example.newsapplication.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Util {
    companion object {
        fun formatDate(inputDate: String, inputFormat: String, outputFormat: String): String {
            val inputSdf = SimpleDateFormat(inputFormat, Locale("ru"))
            val outputSdf = SimpleDateFormat(outputFormat, Locale("ru"))

            return try {
                val date = inputSdf.parse(inputDate)
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val parsedYear = Calendar.getInstance()
                parsedYear.time = date

                val finalFormat = if (currentYear != parsedYear.get(Calendar.YEAR)) {
                    "dd MMM, yyyy"
                } else {
                    outputFormat
                }

                SimpleDateFormat(finalFormat, Locale("ru")).format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                inputDate
            }
        }

        suspend fun hideBottomNavigationView(view: BottomNavigationView) {
            view.clearAnimation()
            view.animate()
                .translationY(view.height.toFloat())
                .scaleX(0.8f)
                .alpha(0.0f)
                .setDuration(350)
                .start()
            delay(350L)
        }
        fun showBottomNavigationView(view: BottomNavigationView) {
            view.clearAnimation()
            view.animate()
                .translationY(0f)
                .scaleX(1f)
                .alpha(1f)
                .setDuration(350)
                .start()
        }

        fun clickAreaButton(button: View) {

            val parent = button.parent as View

            parent.post {
                val rect = Rect()
                button.getHitRect(rect)
                rect.top -= 15
                rect.left -= 15
                rect.bottom += 15
                rect.right += 15
                parent.touchDelegate = TouchDelegate(rect, button)
            }
        }

        fun Fragment.replaceFragment(
            currentFragment: Fragment,
            newFragment: Fragment,
            containerId: Int,
            args: Bundle? = null
        ) {
            if (args != null) {
                newFragment.arguments = args
            }
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.overlay_from_right,
                R.anim.overlay_to_left,
                R.anim.overlay_from_left,
                R.anim.overlay_to_right
            )
            transaction.add(containerId, newFragment)
            transaction.addToBackStack(null)
            transaction.hide(currentFragment)
            transaction.commit()
        }

        fun showIconPopupMenu(popupMenu: PopupMenu) {
            try {
                val fields: Array<Field> = popupMenu.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field.get(popupMenu)
                        val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons = classPopupHelper.getMethod("setForceShowIcon", Boolean::class.java)
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun showSnackBar(view: View, anchor: View?, text: String, textColor: Int, action:() -> Unit) {
            Snackbar.make(view, text, Snackbar.LENGTH_SHORT).apply {
                anchorView = anchor
                setTextColor(textColor)
                setActionTextColor(Color.LTGRAY)
                setAction("Show list") {
                    action()
                }
                show()
            }
        }

        fun showSnackBarNoAction(view: View, anchor: View?, text: String, textColor: Int) {
            Snackbar.make(view, text, Snackbar.LENGTH_SHORT).apply {
                anchorView = anchor
                setTextColor(textColor)
                show()
            }
        }
    }
}