package ru.gr106.fractal.gui

import math.AlgebraicFractal
import math.Complex
import java.awt.Graphics
import ru.smak.drawing.Converter
import ru.smak.drawing.Plane
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import kotlin.concurrent.thread

class FractalPainter (val fractal: AlgebraicFractal) : Painter{

    var previous_img: BufferedImage? = null
    var dx:Int = 0
    var dy:Int = 0
    val procCount = Runtime.getRuntime().availableProcessors()

    var plane: Plane? = null
    override val width: Int
        get() = plane?.width?:0
    override val height: Int
        get() = plane?.height?:0
    var pointColor: (Float) -> Color = {if (it < 1f) Color.WHITE else Color.BLACK }


    fun fullPaint(img:BufferedImage,rx:Int,ry:Int) : BufferedImage{


        //как рисовать фрактал

        plane?.let { plane ->
            Array(procCount) {
                thread {
                    if (rx == 0 && ry == 0) {
                        for (x in it..<width step procCount) {
                            for (y in 0..<height) {
                                val z = Complex(Converter.xScr2Crt(x, plane), Converter.yScr2Crt(y, plane))
                                img.setRGB(x, y, pointColor(fractal.isInSet(z)).rgb)
                            }
                        }
                    }
                    if (ry < 0) {
                        for (y in height+ry+it..<height step procCount) {
                            for (x in 0..<width ) {
                                val z = Complex(Converter.xScr2Crt(x, plane), Converter.yScr2Crt(y, plane))
                                img.setRGB(x, y, pointColor(fractal.isInSet(z)).rgb)
                            }
                        }
                    }
                    if (ry > 0) {
                        for (y in it..<ry step procCount) {
                            for (x in 0..<width ) {
                                val z = Complex(Converter.xScr2Crt(x, plane), Converter.yScr2Crt(y, plane))
                                img.setRGB(x, y, pointColor(fractal.isInSet(z)).rgb)
                            }
                        }
                    }
                    if (rx < 0) {
                        for (x in width + rx + it..<width step procCount) {
                            for (y in 0..<height) {
                                val z = Complex(Converter.xScr2Crt(x, plane), Converter.yScr2Crt(y, plane))
                                img.setRGB(x, y, pointColor(fractal.isInSet(z)).rgb)
                            }
                        }
                    }
                    if (rx > 0) {
                        for (x in it..<rx step procCount) {
                            for (y in 0..<height) {
                                val z = Complex(Converter.xScr2Crt(x, plane), Converter.yScr2Crt(y, plane))
                                img.setRGB(x, y, pointColor(fractal.isInSet(z)).rgb)
                            }
                        }
                    }
                }
            }.forEach { it.join() }
        }
        return img
    }

    override fun paint(g: Graphics) {
        var img = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

        if (previous_img != null ) {
            img = fullPaint(img,dx,dy)
            img.graphics.drawImage(previous_img,dx,dy,null)
            previous_img = img


            g.drawImage(previous_img, 0, 0, null)
        }
        else{
            previous_img = fullPaint(img,0,0)
            g.drawImage(previous_img, 0, 0, null)
        }



//        var i = img.getSubimage(10,10,50,50)
//        img.graphics.drawImage(i,80,80,40,40,null)

    }

}
