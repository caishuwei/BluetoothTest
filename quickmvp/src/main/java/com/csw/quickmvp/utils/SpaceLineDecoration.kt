@file:Suppress(
    "unused", "MemberVisibilityCanBePrivate", "RedundantOverride",
    "ConvertSecondaryConstructorToPrimary"
)

package com.csw.quickmvp.utils

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceLineDecoration : RecyclerView.ItemDecoration {
    companion object {
        fun getInstanceByDp(l: Int, t: Int, r: Int, b: Int, color: Int): SpaceLineDecoration {
            return SpaceLineDecoration(
                ScreenInfo.dp2Px(l.toFloat()),
                ScreenInfo.dp2Px(t.toFloat()),
                ScreenInfo.dp2Px(r.toFloat()),
                ScreenInfo.dp2Px(b.toFloat()),
                color
            )
        }
    }

    private val l: Int
    private val t: Int
    private val r: Int
    private val b: Int
    private val color: Int

    constructor(l: Int, t: Int, r: Int, b: Int, color: Int) : super() {
        this.l = l
        this.t = t
        this.r = r
        this.b = b
        this.color = color
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (!skipDrawSpaceLine(parent.getChildViewHolder(view).adapterPosition)) {
            outRect.set(l, t, r, b)
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val childCount = parent.childCount
        var child: View
        val rect = Rect()
        for (i in 0 until childCount) {
            child = parent.getChildAt(i)
            if (skipDrawSpaceLine(parent.getChildViewHolder(child).adapterPosition)) {
                continue
            }
            rect.set(child.left - l, child.top - t, child.right + r, child.bottom + b)
            if (l > 0) {
                c.save()
                c.clipRect(rect.left, rect.top, rect.left + l, rect.bottom)
                c.drawColor(color)
                c.restore()
            }
            if (t > 0) {
                c.save()
                c.clipRect(rect.left, rect.top, rect.right, rect.top + t)
                c.drawColor(color)
                c.restore()
            }
            if (r > 0) {
                c.save()
                c.clipRect(rect.right - r, rect.top, rect.right, rect.bottom)
                c.drawColor(color)
                c.restore()
            }
            if (b > 0) {
                c.save()
                c.clipRect(rect.left, rect.bottom - b, rect.right, rect.bottom)
                c.drawColor(color)
                c.restore()
            }
        }

    }

    /**
     * 本方法用于跳过间隔线绘制
     * @param adapterPosition item于适配器中的下标
     * @return true 跳过绘制 else 不跳过
     */
    @Suppress("UNUSED_PARAMETER")
    fun skipDrawSpaceLine(adapterPosition: Int): Boolean {
        return false
    }
}