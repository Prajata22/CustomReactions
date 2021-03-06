package com.github.pgreze.reactions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.RequiresApi

/**
 * Entry point for reaction popup.
 */
class ReactionPopup @JvmOverloads constructor(
    private var context: Context,
    private var applexGestureListener: ApplexGestureListener,
    private var button: LinearLayout,
    private var reactionsConfig: ReactionsConfig,
    private var reactionSelectedListener: ReactionSelectedListener? = null,
    private var reactionPopupStateChangeListener: ReactionPopupStateChangeListener? = null,
) : PopupWindow(context), View.OnTouchListener {

    private lateinit var v: View
    private var touchGestureListener: TouchGestureListener
    private var touchGestureDetector: GestureDetector

    private val rootView = FrameLayout(context).also {
        it.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private var view = buildViewGroup()

    init {
        contentView = rootView
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.MATCH_PARENT
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        touchGestureListener = TouchGestureListener()
        touchGestureDetector = GestureDetector(context, touchGestureListener)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        this.v = v
        touchGestureDetector.onTouchEvent(event)
        return view.onTouchEvent(event)
    }

    override fun dismiss() {
        view.dismiss()
        rootView.removeView(view)
        view = buildViewGroup()
        button.visibility = View.VISIBLE

        super.dismiss()
    }

    private fun buildViewGroup(): ReactionViewGroup {
        return ReactionViewGroup(context, reactionsConfig).also {
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
    }

    internal inner class TouchGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            applexGestureListener.onSingleClick()
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)

            if (!isShowing) {
                // Show fullscreen with button as context provider
                showAtLocation(v, Gravity.NO_GRAVITY, 0, 0)
                view.show(e, v)
                reactionPopupStateChangeListener?.invoke(true)
                applexGestureListener.onLongClick()
            }
        }
    }
}
