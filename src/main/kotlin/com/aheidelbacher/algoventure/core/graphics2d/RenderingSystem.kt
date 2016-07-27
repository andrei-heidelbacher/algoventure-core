/*
 * Copyright 2016 Andrei Heidelbacher <andrei.heidelbacher@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aheidelbacher.algoventure.core.graphics2d

import com.aheidelbacher.algostorm.graphics2d.AbstractRenderingSystem
import com.aheidelbacher.algostorm.state.Map
import com.aheidelbacher.algostorm.state.TileSet.Viewport

class RenderingSystem(
        map: Map,
        private val canvas: Canvas
) : AbstractRenderingSystem(map) {
    override val canvasX: Int
        get() = 0

    override val canvasY: Int
        get() = 0

    override val canvasWidth: Int
        get() = canvas.canvasWidth

    override val canvasHeight: Int
        get() = canvas.canvasHeight

    override fun lockCanvas() {
        canvas.lockCanvas()
    }

    override fun clearCanvas() {
        canvas.clearCanvas()
    }

    override fun unlockCanvasAndPost() {
        canvas.unlockCanvasAndPost()
    }

    override fun drawBitmap(
            viewport: Viewport,
            flipHorizontally: Boolean,
            flipVertically: Boolean,
            flipDiagonally: Boolean,
            opacity: Float,
            x: Int,
            y: Int,
            width: Int,
            height: Int,
            rotation: Float
    ) {
        canvas.drawBitmap(
                viewport = viewport,
                flipHorizontally = flipHorizontally,
                flipVertically = flipVertically,
                flipDiagonally = flipDiagonally,
                opacity = opacity,
                x = x,
                y = y,
                width = width,
                height = height,
                rotation = rotation
        )
    }
}
