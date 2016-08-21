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

package com.aheidelbacher.algoventure.core.engine

import com.aheidelbacher.algostorm.engine.graphics2d.Canvas
import com.aheidelbacher.algostorm.engine.input.InputReader
import com.aheidelbacher.algostorm.engine.sound.SoundEngine

import com.aheidelbacher.algoventure.core.input.Input
import com.aheidelbacher.algoventure.core.ui.UiHandler

data class Platform(
        val canvas: Canvas,
        val soundEngine: SoundEngine,
        val inputReader: InputReader<Input>,
        val uiHandler: UiHandler
)
