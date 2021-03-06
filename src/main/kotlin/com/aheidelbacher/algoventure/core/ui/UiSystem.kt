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

package com.aheidelbacher.algoventure.core.ui

import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber
import com.aheidelbacher.algostorm.state.File
import com.aheidelbacher.algostorm.state.Layer.ObjectGroup
import com.aheidelbacher.algostorm.systems.Update
import com.aheidelbacher.algostorm.systems.audio.MusicSystem.PlayMusic

import com.aheidelbacher.algoventure.core.act.ActingSystem.Companion.isActor

class UiSystem(
        private val uiHandler: UiHandler,
        private val objectGroup: ObjectGroup,
        private val objectId: Int,
        private val publisher: Publisher
) : Subscriber {
    private fun checkGameOver(): Boolean = objectId !in objectGroup
    private fun checkGameWon(): Boolean = objectId in objectGroup &&
            objectGroup.objectSet.count { it.isActor } == 1

    private var isGameOver = false
    private var isGameWon = false

    @Subscribe fun onUpdate(event: Update) {
        if (!isGameOver && checkGameOver()) {
            isGameOver = true
            publisher.post(PlayMusic(File("/sounds/game_over.mp3")))
            uiHandler.onGameOver()
        }
        if (!isGameWon && checkGameWon()) {
            isGameWon = true
            publisher.post(PlayMusic(File("/sounds/game_won.mp3")))
            uiHandler.onGameWon()
        }
    }
}
