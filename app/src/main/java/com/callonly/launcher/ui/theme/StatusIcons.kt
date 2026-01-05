package com.callonly.launcher.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object StatusIcons {
    val BatteryFull: ImageVector
        get() = ImageVector.Builder(
            name = "BatteryFull",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                moveTo(15.67f, 4.0f)
                horizontalLineTo(14.0f)
                verticalLineTo(2.0f)
                horizontalLineTo(10.0f)
                verticalLineTo(4.0f)
                horizontalLineTo(8.33f)
                curveTo(7.6f, 4.0f, 7.0f, 4.6f, 7.0f, 5.33f)
                verticalLineTo(20.67f)
                curveTo(7.0f, 21.4f, 7.6f, 22.0f, 8.33f, 22.0f)
                horizontalLineTo(15.67f)
                curveTo(16.4f, 22.0f, 17.0f, 21.4f, 17.0f, 20.67f)
                verticalLineTo(5.33f)
                curveTo(17.0f, 4.6f, 16.4f, 4.0f, 15.67f, 4.0f)
                close()
            }
        }.build()

    val BatteryStd: ImageVector // Representing ~50-60%
        get() = ImageVector.Builder(
            name = "BatteryStd",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                moveTo(15.67f, 4.0f)
                horizontalLineTo(14.0f)
                verticalLineTo(2.0f)
                horizontalLineTo(10.0f)
                verticalLineTo(4.0f)
                horizontalLineTo(8.33f)
                curveTo(7.6f, 4.0f, 7.0f, 4.6f, 7.0f, 5.33f)
                verticalLineTo(20.67f)
                curveTo(7.0f, 21.4f, 7.6f, 22.0f, 8.33f, 22.0f)
                horizontalLineTo(15.67f)
                curveTo(16.4f, 22.0f, 17.0f, 21.4f, 17.0f, 20.67f)
                verticalLineTo(5.33f)
                curveTo(17.0f, 4.6f, 16.4f, 4.0f, 15.67f, 4.0f)
                close()
                moveTo(15.0f, 11.0f)
                horizontalLineTo(9.0f)
                verticalLineTo(6.0f)
                horizontalLineTo(15.0f)
                verticalLineTo(11.0f)
                close()
            }
            // Just using full shape for now as standard, relying on color
        }.build()

    val BatteryAlert: ImageVector
        get() = ImageVector.Builder(
            name = "BatteryAlert",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                moveTo(15.67f, 4.0f)
                horizontalLineTo(14.0f)
                verticalLineTo(2.0f)
                horizontalLineTo(10.0f)
                verticalLineTo(4.0f)
                horizontalLineTo(8.33f)
                curveTo(7.6f, 4.0f, 7.0f, 4.6f, 7.0f, 5.33f)
                verticalLineTo(20.67f)
                curveTo(7.0f, 21.4f, 7.6f, 22.0f, 8.33f, 22.0f)
                horizontalLineTo(15.67f)
                curveTo(16.4f, 22.0f, 17.0f, 21.4f, 17.0f, 20.67f)
                verticalLineTo(5.33f)
                curveTo(17.0f, 4.6f, 16.4f, 4.0f, 15.67f, 4.0f)
                close()
                moveTo(13.0f, 18.0f)
                horizontalLineTo(11.0f)
                verticalLineTo(16.0f)
                horizontalLineTo(13.0f)
                verticalLineTo(18.0f)
                close()
                moveTo(13.0f, 14.0f)
                horizontalLineTo(11.0f)
                verticalLineTo(9.0f)
                horizontalLineTo(13.0f)
                verticalLineTo(14.0f)
                close()
            }
        }.build()
    
    val Charging: ImageVector
        get() = ImageVector.Builder(
            name = "Charging",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                moveTo(7.0f, 2.0f)
                verticalLineTo(13.0f)
                horizontalLineTo(10.0f)
                verticalLineTo(22.0f)
                lineTo(17.0f, 10.0f)
                horizontalLineTo(13.0f)
                verticalLineTo(2.0f)
                horizontalLineTo(7.0f)
                close()
            }
        }.build()
    // SIGNAL ICONS
    val Signal0: ImageVector
        get() = ImageVector.Builder(
            name = "Signal0",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 0.3f, stroke = null, strokeAlpha = 1.0f,
                 strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                 strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                moveTo(22.0f, 2.0f)
                lineTo(2.0f, 22.0f)
                horizontalLineTo(22.0f)
                verticalLineTo(2.0f)
                close()
            }
        }.build()

    val Signal1: ImageVector
        get() = ImageVector.Builder(
            name = "Signal1",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 0.3f, stroke = null, strokeAlpha = 1.0f,
                 strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                 strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                 moveTo(22.0f, 2.0f)
                 lineTo(2.0f, 22.0f)
                 horizontalLineTo(22.0f)
                 verticalLineTo(2.0f)
                 close() 
            }
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                 strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                 strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                 moveTo(7.0f, 17.0f)
                 lineTo(2.0f, 22.0f)
                 horizontalLineTo(7.0f)
                 verticalLineTo(17.0f)
                 close()
            }
        }.build()

    val Signal2: ImageVector
        get() = ImageVector.Builder(
            name = "Signal2",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
             path(fill = SolidColor(Color.Black), fillAlpha = 0.3f, stroke = null, strokeAlpha = 1.0f,
                 strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                 strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                 moveTo(22.0f, 2.0f)
                 lineTo(2.0f, 22.0f)
                 horizontalLineTo(22.0f)
                 verticalLineTo(2.0f)
                 close() 
            }
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                 strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                 strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                 moveTo(12.0f, 12.0f)
                 lineTo(2.0f, 22.0f)
                 horizontalLineTo(12.0f)
                 verticalLineTo(12.0f)
                 close()
            }
        }.build()

    val Signal3: ImageVector
        get() = ImageVector.Builder(
            name = "Signal3",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
             path(fill = SolidColor(Color.Black), fillAlpha = 0.3f, stroke = null, strokeAlpha = 1.0f,
                 strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                 strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                 moveTo(22.0f, 2.0f)
                 lineTo(2.0f, 22.0f)
                 horizontalLineTo(22.0f)
                 verticalLineTo(2.0f)
                 close() 
            }
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                 strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                 strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                 moveTo(17.0f, 7.0f)
                 lineTo(2.0f, 22.0f)
                 horizontalLineTo(17.0f)
                 verticalLineTo(7.0f)
                 close()
            }
        }.build()

    val Signal4: ImageVector
        get() = ImageVector.Builder(
            name = "Signal4",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                 strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                 strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                 moveTo(22.0f, 2.0f)
                 lineTo(2.0f, 22.0f)
                 horizontalLineTo(22.0f)
                 verticalLineTo(2.0f)
                 close()
            }
        }.build()
        
    val List: ImageVector
        get() = ImageVector.Builder(
            name = "List",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                moveTo(3.0f, 13.0f)
                horizontalLineTo(21.0f)
                verticalLineTo(11.0f)
                horizontalLineTo(3.0f)
                verticalLineTo(13.0f)
                close()
                moveTo(3.0f, 6.0f)
                verticalLineTo(8.0f)
                horizontalLineTo(21.0f)
                verticalLineTo(6.0f)
                horizontalLineTo(3.0f)
                close()
                moveTo(3.0f, 18.0f)
                horizontalLineTo(21.0f)
                verticalLineTo(16.0f)
                horizontalLineTo(3.0f)
                verticalLineTo(18.0f)
                close()
            }
        }.build()

    val ArrowBack: ImageVector
        get() = ImageVector.Builder(
            name = "ArrowBack",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                moveTo(20.0f, 11.0f)
                horizontalLineTo(7.83f)
                lineTo(13.42f, 5.41f)
                lineTo(12.0f, 4.0f)
                lineTo(4.0f, 12.0f)
                lineTo(12.0f, 20.0f)
                lineTo(13.41f, 18.59f)
                lineTo(7.83f, 13.0f)
                horizontalLineTo(20.0f)
                verticalLineTo(11.0f)
                close()
            }
        }.build()

    val Call: ImageVector
        get() = ImageVector.Builder(
            name = "Call",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                moveTo(20.01f, 15.39f)
                curveToRelative(-1.25f, 0.0f, -2.45f, -0.2f, -3.57f, -0.57f)
                curveToRelative(-0.35f, -0.11f, -0.74f, -0.03f, -1.02f, 0.24f)
                lineToRelative(-2.2f, 2.2f)
                curveToRelative(-2.83f, -1.44f, -5.15f, -3.75f, -6.59f, -6.58f)
                lineToRelative(2.2f, -2.21f)
                curveToRelative(0.28f, -0.27f, 0.36f, -0.66f, 0.25f, -1.01f)
                curveToRelative(-0.37f, -1.11f, -0.57f, -2.31f, -0.57f, -3.57f)
                curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
                horizontalLineTo(3.5f)
                curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
                curveToRelative(0.0f, 9.39f, 7.61f, 17.0f, 17.0f, 17.0f)
                curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
                verticalLineToRelative(-3.5f)
                curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
                close()
            }
        }.build()

    val Block: ImageVector
        get() = ImageVector.Builder(
            name = "Block",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                moveTo(12.0f, 2.0f)
                curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
                reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
                reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
                close()
                moveTo(12.0f, 20.0f)
                curveToRelative(-4.42f, 0.0f, -8.0f, -3.58f, -8.0f, -8.0f)
                curveToRelative(0.0f, -1.85f, 0.63f, -3.55f, 1.69f, -4.9f)
                lineTo(16.9f, 18.31f)
                curveTo(15.55f, 19.37f, 13.85f, 20.0f, 12.0f, 20.0f)
                close()
                moveTo(18.31f, 16.9f)
                lineTo(5.69f, 4.29f)
                curveTo(7.04f, 3.23f, 8.75f, 2.6f, 10.6f, 2.6f)
                curveToRelative(4.42f, 0.0f, 8.0f, 3.58f, 8.0f, 8.0f)
                curveToRelative(0.0f, 1.85f, -0.63f, 3.55f, -1.69f, 4.9f)
                close()
            }
        }.build()

    val CallMissed: ImageVector
        get() = ImageVector.Builder(
            name = "CallMissed",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                moveTo(19.59f, 7.0f)
                lineTo(12.0f, 14.59f)
                lineTo(6.41f, 9.0f)
                horizontalLineTo(11.0f)
                verticalLineTo(7.0f)
                horizontalLineTo(3.0f)
                verticalLineToRelative(8.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-4.59f)
                lineToRelative(7.0f, 7.0f)
                lineToRelative(9.0f, -9.0f)
                close()
            }
        }.build()
}
