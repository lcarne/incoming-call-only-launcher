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
            }
        }.build()

    val VolumeUp: ImageVector
        get() = ImageVector.Builder(
            name = "VolumeUp",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                moveTo(3.0f, 9.0f)
                verticalLineTo(15.0f)
                horizontalLineTo(7.0f)
                lineTo(12.0f, 20.0f)
                verticalLineTo(4.0f)
                lineTo(7.0f, 9.0f)
                horizontalLineTo(3.0f)
                close()
                moveTo(16.5f, 12.0f)
                curveToRelative(0.0f, -1.77f, -1.02f, -3.29f, -2.5f, -4.03f)
                verticalLineTo(16.03f)
                curveToRelative(1.48f, -0.74f, 2.5f, -2.26f, 2.5f, -4.03f)
                close()
                moveTo(14.0f, 3.23f)
                verticalLineTo(5.29f)
                curveToRelative(2.89f, 0.86f, 5.0f, 3.54f, 5.0f, 6.71f)
                reflectiveCurveToRelative(-2.11f, 5.85f, -5.0f, 6.71f)
                verticalLineTo(20.77f)
                curveToRelative(4.01f, -0.91f, 7.0f, -4.49f, 7.0f, -8.77f)
                reflectiveCurveToRelative(-2.99f, -7.86f, -7.0f, -8.77f)
                close()
            }
        }.build()

    val VolumeOff: ImageVector
        get() = ImageVector.Builder(
            name = "VolumeOff",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                moveTo(16.5f, 12.0f)
                curveToRelative(0.0f, -1.77f, -1.02f, -3.29f, -2.5f, -4.03f)
                verticalLineToRelative(2.21f)
                lineToRelative(2.45f, 2.45f)
                curveToRelative(0.03f, -0.2f, 0.05f, -0.41f, 0.05f, -0.63f)
                close()
                moveTo(19.0f, 12.0f)
                curveToRelative(0.0f, 0.94f, -0.2f, 1.82f, -0.54f, 2.64f)
                lineToRelative(1.51f, 1.51f)
                curveTo(20.63f, 14.91f, 21.0f, 13.5f, 21.0f, 12.0f)
                curveToRelative(0.0f, -4.28f, -2.99f, -7.86f, -7.0f, -8.77f)
                verticalLineToRelative(2.06f)
                curveToRelative(2.89f, 0.86f, 5.0f, 3.54f, 5.0f, 6.71f)
                close()
                moveTo(4.27f, 3.0f)
                lineTo(3.0f, 4.27f)
                lineTo(7.73f, 9.0f)
                horizontalLineTo(3.0f)
                verticalLineTo(15.0f)
                horizontalLineTo(7.0f)
                lineTo(12.0f, 20.0f)
                verticalLineTo(13.27f)
                lineToRelative(4.66f, 4.66f)
                curveToRelative(-0.8f, 0.62f, -1.69f, 1.09f, -2.66f, 1.35f)
                verticalLineTo(21.36f)
                curveToRelative(1.53f, -0.32f, 2.91f, -1.01f, 4.09f, -1.97f)
                lineTo(20.73f, 22.0f)
                lineTo(22.0f, 20.73f)
                lineTo(4.27f, 3.0f)
                close()
                moveTo(12.0f, 4.0f)
                lineTo(9.91f, 6.09f)
                lineTo(12.0f, 8.18f)
                verticalLineTo(4.0f)
                close()
            }
        }.build()

    val CallEnd: ImageVector
        get() = ImageVector.Builder(
            name = "CallEnd",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                moveTo(12.0f, 9.0f)
                curveToRelative(-1.6f, 0.0f, -3.15f, 0.25f, -4.6f, 0.72f)
                verticalLineToRelative(3.1f)
                curveToRelative(0.0f, 0.39f, -0.23f, 0.74f, -0.56f, 0.9f)
                curveToRelative(-0.98f, 0.49f, -1.87f, 1.12f, -2.66f, 1.85f)
                curveToRelative(-0.18f, 0.18f, -0.43f, 0.28f, -0.7f, 0.28f)
                curveToRelative(-0.28f, 0.0f, -0.53f, -0.11f, -0.71f, -0.29f)
                lineTo(0.29f, 13.08f)
                curveToRelative(-0.18f, -0.17f, -0.29f, -0.42f, -0.29f, -0.7f)
                curveToRelative(0.0f, -0.28f, 0.11f, -0.53f, 0.29f, -0.71f)
                curveToRelative(3.05f, -2.89f, 7.17f, -4.67f, 11.71f, -4.67f)
                reflectiveCurveToRelative(8.66f, 1.78f, 11.71f, 4.67f)
                curveToRelative(0.18f, 0.18f, 0.29f, 0.43f, 0.29f, 0.71f)
                curveToRelative(0.0f, 0.28f, -0.11f, 0.53f, -0.29f, 0.71f)
                lineToRelative(-2.48f, 2.48f)
                curveToRelative(-0.18f, 0.18f, -0.43f, 0.29f, -0.71f, 0.29f)
                curveToRelative(-0.27f, 0.0f, -0.52f, -0.11f, -0.7f, -0.28f)
                curveToRelative(-0.79f, -0.74f, -1.69f, -1.36f, -2.67f, -1.85f)
                curveToRelative(-0.33f, -0.16f, -0.56f, -0.5f, -0.56f, -0.9f)
                verticalLineToRelative(-3.1f)
                curveToRelative(-1.45f, -0.47f, -3.0f, -0.72f, -4.6f, -0.72f)
                close()
            }
        }.build()

    val Hearing: ImageVector
        get() = ImageVector.Builder(
            name = "Hearing",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 20f,
            viewportHeight = 20f
        ).apply {

            path(
                fill = SolidColor(Color.Black),
                stroke = null,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(17.256f, 12.253f)
                curveTo(17.16f, 11.586f, 16.645f, 11.066f, 15.982f, 10.911f)
                curveTo(13.405f, 10.307f, 12.759f, 8.823f, 12.65f, 7.177f)
                curveTo(12.193f, 7.092f, 11.38f, 7f, 10f, 7f)
                curveTo(8.62f, 7f, 7.807f, 7.092f, 7.35f, 7.177f)
                curveTo(7.241f, 8.823f, 6.595f, 10.307f, 4.018f, 10.911f)
                curveTo(3.355f, 11.067f, 2.84f, 11.586f, 2.744f, 12.253f)
                lineTo(2.247f, 15.695f)
                curveTo(2.072f, 16.907f, 2.962f, 18f, 4.2f, 18f)
                horizontalLineTo(15.8f)
                curveTo(17.037f, 18f, 17.928f, 16.907f, 17.753f, 15.695f)
                lineTo(17.256f, 12.253f)
                close()

                moveTo(10f, 15.492f)
                curveTo(8.605f, 15.492f, 7.474f, 14.372f, 7.474f, 12.992f)
                curveTo(7.474f, 11.612f, 8.605f, 10.492f, 10f, 10.492f)
                curveTo(11.395f, 10.492f, 12.526f, 11.612f, 12.526f, 12.992f)
                curveTo(12.526f, 14.372f, 11.395f, 15.492f, 10f, 15.492f)
                close()

                moveTo(19.95f, 6f)
                curveTo(19.926f, 4.5f, 16.108f, 2.001f, 10f, 2f)
                curveTo(3.891f, 2.001f, 0.073f, 4.5f, 0.05f, 6f)
                curveTo(0.027f, 7.5f, 0.071f, 9.452f, 2.585f, 9.127f)
                curveTo(5.526f, 8.746f, 5.345f, 7.719f, 5.345f, 6.251f)
                curveTo(5.345f, 5.227f, 7.737f, 4.98f, 10f, 4.98f)
                curveTo(12.263f, 4.98f, 14.655f, 5.227f, 14.655f, 6.251f)
                curveTo(14.655f, 7.719f, 14.474f, 8.746f, 17.415f, 9.127f)
                curveTo(19.928f, 9.452f, 19.973f, 7.5f, 19.95f, 6f)
                close()
            }
        }.build()

    val Speaker: ImageVector
        get() = ImageVector.Builder(
            name = "Speaker",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 28f,
            viewportHeight = 28f
        ).apply {

            path(
                fill = SolidColor(Color.Black),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(16.5f, 4.81425f)
                curveTo(16.5f, 3.71986f, 15.1932f, 3.15384f, 14.395f, 3.90244f)
                lineTo(9.45826f, 8.53182f)
                curveTo(9.13388f, 8.836f, 8.70587f, 9.00529f, 8.26119f, 9.00529f)
                horizontalLineTo(5.25f)
                curveTo(3.45508f, 9.00529f, 2f, 10.4604f, 2f, 12.2553f)
                verticalLineTo(15.7473f)
                curveTo(2f, 17.5422f, 3.45507f, 18.9973f, 5.25f, 18.9973f)
                horizontalLineTo(8.26174f)
                curveTo(8.70619f, 18.9973f, 9.134f, 19.1664f, 9.45832f, 19.4703f)
                lineTo(14.3953f, 24.0964f)
                curveTo(15.1937f, 24.8446f, 16.5f, 24.2785f, 16.5f, 23.1843f)
                close()
            }

            // Ondes (plus épaisses)
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(22.7f, 5.25f)
                curveTo(24.75f, 7.58f, 26f, 10.65f, 26f, 14f)
                curveTo(26f, 17.35f, 24.75f, 20.41f, 22.7f, 22.75f)
            }

            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(20.35f, 8.3f)
                curveTo(21.54f, 9.89f, 22.25f, 11.86f, 22.25f, 14f)
                curveTo(22.25f, 16.14f, 21.54f, 18.11f, 20.35f, 19.7f)
            }
        }.build()
    val LockOpen: ImageVector
        get() = ImageVector.Builder(
            name = "LockOpen",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                moveTo(12.0f, 17.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                reflectiveCurveToRelative(-0.9f, -2.0f, -2.0f, -2.0f)
                reflectiveCurveToRelative(-2.0f, 0.9f, -2.0f, 2.0f)
                reflectiveCurveToRelative(0.9f, 2.0f, 2.0f, 2.0f)
                close()
                moveTo(18.0f, 8.0f)
                horizontalLineToRelative(-1.0f)
                lineTo(17.0f, 6.0f)
                curveToRelative(0.0f, -2.76f, -2.24f, -5.0f, -5.0f, -5.0f)
                reflectiveCurveTo(7.0f, 3.24f, 7.0f, 6.0f)
                horizontalLineToRelative(1.9f)
                curveToRelative(0.0f, -1.71f, 1.39f, -3.1f, 3.1f, -3.1f)
                reflectiveCurveToRelative(3.1f, 1.39f, 3.1f, 3.1f)
                verticalLineToRelative(2.0f)
                lineTo(6.0f, 8.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(10.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(12.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                lineTo(20.0f, 10.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                close()
                moveTo(18.0f, 20.0f)
                lineTo(6.0f, 20.0f)
                lineTo(6.0f, 10.0f)
                horizontalLineToRelative(12.0f)
                verticalLineToRelative(10.0f)
                close()
            }
        }.build()

    val PhotoLibrary: ImageVector
        get() = ImageVector.Builder(
            name = "PhotoLibrary",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                moveTo(22.0f, 16.0f)
                lineTo(22.0f, 4.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                lineTo(8.0f, 2.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(12.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(12.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                close()
                moveTo(11.0f, 12.0f)
                lineToRelative(2.03f, 2.71f)
                lineTo(16.0f, 11.0f)
                lineToRelative(4.0f, 5.0f)
                lineTo(8.0f, 16.0f)
                lineToRelative(3.0f, -4.0f)
                close()
                moveTo(2.0f, 6.0f)
                verticalLineToRelative(14.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(14.0f)
                verticalLineToRelative(-2.0f)
                lineTo(4.0f, 20.0f)
                lineTo(4.0f, 6.0f)
                lineTo(2.0f, 6.0f)
                close()
            }
        }.build()

    val PhotoCamera: ImageVector
        get() = ImageVector.Builder(
            name = "PhotoCamera",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black), fillAlpha = 1.0f, stroke = null, strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f, strokeLineCap = StrokeCap.Butt, strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f, pathFillType = PathFillType.NonZero) {
                moveTo(9.0f, 2.0f)
                lineTo(7.17f, 4.0f)
                lineTo(4.0f, 4.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(12.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(16.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                lineTo(22.0f, 6.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                horizontalLineToRelative(-3.17f)
                lineTo(15.0f, 2.0f)
                lineTo(9.0f, 2.0f)
                close()
                moveTo(12.0f, 17.0f)
                curveToRelative(-2.76f, 0.0f, -5.0f, -2.24f, -5.0f, -5.0f)
                reflectiveCurveToRelative(2.24f, -5.0f, 5.0f, -5.0f)
                reflectiveCurveToRelative(5.0f, 2.24f, 5.0f, 5.0f)
                reflectiveCurveToRelative(-2.24f, 5.0f, -5.0f, 5.0f)
                close()
            }
        }.build()
}
