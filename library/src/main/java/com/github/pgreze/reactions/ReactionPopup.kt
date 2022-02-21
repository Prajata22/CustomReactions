package com.github.pgreze.reactions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import java.util.*


/**
 * Entry point for reaction popup.
 */
class ReactionPopup @JvmOverloads constructor(
    private var context: Context,
    private var imageView: ImageView,
    private var reactionsConfig: ReactionsConfig,
    private var reactionSelectedListener: ReactionSelectedListener? = null,
    private var reactionPopupStateChangeListener: ReactionPopupStateChangeListener? = null,
) : PopupWindow(context), View.OnTouchListener {

    private val MIN_CLICK_DURATION = 200
    private var startClickTime: Long = 0
    private var longClickActive = false

    private val rootView = FrameLayout(context).also {
        it.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }


    private var view = ReactionViewGroup(context, reactionsConfig).also {
        it.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )

        it.reactionSelectedListener = reactionSelectedListener

        it.reactionPopupStateChangeListener = reactionPopupStateChangeListener

        // Just add the view,
        // it will position itself depending on the display preference.
        rootView.addView(it)

        it.dismissListener = ::dismiss
    }

    init {
        contentView = rootView
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.MATCH_PARENT
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> longClickActive = false
            MotionEvent.ACTION_DOWN -> if (!longClickActive) {
                longClickActive = true
                startClickTime = Calendar.getInstance().timeInMillis
            }
            MotionEvent.ACTION_MOVE -> if (longClickActive) {
                val clickDuration: Long = Calendar.getInstance().timeInMillis - startClickTime
                if (clickDuration >= MIN_CLICK_DURATION) {
                    longClickActive = false
                    if (!isShowing) {
                        // Show fullscreen with button as context provider
                        showAtLocation(v, Gravity.NO_GRAVITY, 0, 0)
                        view.show(event, v)
                        reactionPopupStateChangeListener?.invoke(true)
                    }
                }
            }
        }

        return view.onTouchEvent(event)
    }

    override fun dismiss() {
        view.dismiss()
        rootView.removeView(view)

        view = ReactionViewGroup(context, reactionsConfig).also {
            it.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )

            it.reactionSelectedListener = reactionSelectedListener

            it.reactionPopupStateChangeListener = reactionPopupStateChangeListener

            // Just add the view,
            // it will position itself depending on the display preference.
            rootView.addView(it)

            it.dismissListener = ::dismiss
        }
        super.dismiss()
    }
}
