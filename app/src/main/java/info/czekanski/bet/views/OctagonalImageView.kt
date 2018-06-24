package info.czekanski.bet.views

import android.content.Context
import android.graphics.*
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import dagger.android.AndroidInjection
import info.czekanski.bet.MyApplication
import info.czekanski.bet.di.*
import info.czekanski.bet.repository.ConfigProvider
import javax.inject.Inject


class OctagonalImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    @Inject lateinit var config: ConfigProvider
    private val sides = 8
    private val octagon = Path()
    private val temporal = Path()

    init {
        MyApplication.get().component.inject(this)
        octagon.fillType = Path.FillType.EVEN_ODD
    }

    override fun onDraw(canvas: Canvas) {
        canvas.clipPath(octagon)
        super.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val rect = Rect()
        getDrawingRect(rect)
        if (config.roundFlags) computeRound(rect)
        else computeHex(rect)
    }

    private fun computeRound(bounds: Rect) {
        val width = bounds.width()
        val height = bounds.height()
        val size = Math.min(width, height)
        val centerX = bounds.left + width / 2
        val centerY = bounds.top + height / 2

        octagon.reset()
        octagon.addCircle(centerX.toFloat(), centerY.toFloat(), size/2f, Path.Direction.CW)
    }

    private fun computeHex(bounds: Rect) {
        val width = bounds.width()
        val height = bounds.height()
        val size = Math.min(width, height)
        val centerX = bounds.left + width / 2
        val centerY = bounds.top + height / 2

        octagon.reset()
        octagon.addPath(createHexagon(size, centerX, centerY))
    }

    private fun createHexagon(size: Int, centerX: Int, centerY: Int): Path {
        val section = (2.0 * Math.PI / sides).toFloat()
        val radius = size / 2
        val hex = temporal
        val startAngle = Math.toRadians(360.0 / sides) / 2.0
        hex.reset()
        hex.moveTo(
                centerX + radius * Math.cos(startAngle + 0.0).toFloat(),
                centerY + radius * Math.sin(startAngle + 0.0).toFloat())

        for (i in 1 until sides) {
            hex.lineTo(
                    centerX + radius * Math.cos(startAngle + section * i.toDouble()).toFloat(),
                    centerY + radius * Math.sin(startAngle + section * i.toDouble()).toFloat())
        }

        hex.close()
        return hex
    }
}