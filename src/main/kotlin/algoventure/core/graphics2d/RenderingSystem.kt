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

package algoventure.core.graphics2d

import algostorm.graphics2d.AbstractRenderingSystem
import algostorm.state.Map
import algostorm.state.TileSet.Viewport

class RenderingSystem(
        map: Map,
        private val canvas: Canvas,
        override val cameraWidth: Int,
        override val cameraHeight: Int
) : AbstractRenderingSystem(map) {
    override val cameraX: Int
        get() = 0

    override val cameraY: Int
        get() = 0

    override fun clear() {
        canvas.lockCanvas()
        canvas.clear()
    }

    override fun render() {
        canvas.unlockCanvas()
    }

    override fun drawBitmap(
            viewport: Viewport,
            flipHorizontally: Boolean,
            flipVertically: Boolean,
            flipDiagonally: Boolean,
            opacity: Float,
            x: Int,
            y: Int,
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
                rotation = rotation
        )
    }
}
