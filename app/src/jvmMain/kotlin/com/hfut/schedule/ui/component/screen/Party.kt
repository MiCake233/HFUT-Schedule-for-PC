package com.hfut.schedule.ui.component.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * 桌面端礼花：纯 Compose 粒子动画（Konfetti 只支持 Android）。
 * 行为上尽量对齐 Android 版：从指定原点喷出，受重力影响，持续 timeSecond。
 */
@Composable
actual fun BoxScope.Party(
    timeSecond: Long,
    show: Boolean,
    count: Int,
    place: PartyPlace,
    modifier: Modifier
) {
    if (!show) return

    val scheme = MaterialTheme.colorScheme
    val palette = remember(scheme) {
        listOf(
            scheme.primary, scheme.secondary, scheme.tertiary,
            scheme.primaryContainer, scheme.secondaryContainer, scheme.tertiaryContainer,
            scheme.error
        )
    }
    val origins = remember(place) {
        when (place) {
            PartyPlace.FULL -> listOf(
                Offset(0f, 0f), Offset(1f, 0f), Offset(0f, 1f), Offset(1f, 1f), Offset(0.5f, 0.5f)
            )
            PartyPlace.CENTER -> listOf(Offset(0.5f, 0.5f))
            PartyPlace.CORNER -> listOf(
                Offset(0f, 0f), Offset(1f, 0f), Offset(0f, 1f), Offset(1f, 1f)
            )
            PartyPlace.TOP_CENTER -> listOf(Offset(0f, 0f), Offset(0.5f, 0f), Offset(1f, 0f))
            PartyPlace.TOP_CORNER -> listOf(Offset(0f, 0f), Offset(1f, 0f))
        }
    }

    // 每个粒子：所属原点、初速度方向/大小、尺寸、颜色、自转、相位
    val particles = remember(count, origins, palette) {
        val perOrigin = (count / origins.size).coerceAtLeast(1)
        buildList {
            origins.forEach { origin ->
                repeat(perOrigin) {
                    val angle = Random.nextDouble(-PI / 2.0, PI / 2.0).toFloat() +
                        if (origin.y > 0.6f) (PI.toFloat()) else 0f
                    val speed = Random.nextFloat() * 420f + 260f
                    add(
                        Particle(
                            origin = origin,
                            vx = cos(angle) * speed,
                            vy = sin(angle) * speed,
                            size = Random.nextFloat() * 8f + 6f,
                            color = palette[Random.nextInt(palette.size)].copy(alpha = 0.9f),
                            spin = Random.nextFloat() * 720f - 360f,
                            phase = Random.nextFloat() * 0.25f
                        )
                    )
                }
            }
        }
    }

    val infinite = timeSecond <= 0L
    val transition = rememberInfiniteTransition(label = "party")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (infinite) 4000 else (timeSecond * 1000L).toInt().coerceAtLeast(500),
                easing = LinearEasing
            )
        ),
        label = "partyProgress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val gravity = 900f
        particles.forEach { p ->
            val t = ((progress + p.phase) % 1f) * (timeSecond.coerceAtLeast(4L) / 1000f)
            val originX = p.origin.x * size.width
            val originY = p.origin.y * size.height
            val x = originX + p.vx * t
            val y = originY + p.vy * t + 0.5f * gravity * t * t
            if (x < -40f || x > size.width + 40f || y > size.height + 40f) return@forEach
            translate(x, y) {
                rotate(p.spin * t) {
                    drawRect(
                        color = p.color,
                        topLeft = Offset(-p.size / 2f, -p.size / 2f),
                        size = Size(p.size, p.size * 0.6f)
                    )
                }
            }
        }
    }
}

private data class Particle(
    val origin: Offset,
    val vx: Float,
    val vy: Float,
    val size: Float,
    val color: Color,
    val spin: Float,
    val phase: Float
)
