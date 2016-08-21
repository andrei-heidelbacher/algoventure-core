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

import com.aheidelbacher.algostorm.engine.Update
import com.aheidelbacher.algostorm.engine.sound.PlaySound
import com.aheidelbacher.algostorm.engine.state.ObjectManager
import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber

import com.aheidelbacher.algoventure.core.act.Actor.isActor

class UiSystem(
        private val uiHandler: UiHandler,
        private val objectManager: ObjectManager,
        private val objectId: Int,
        private val publisher: Publisher
) : Subscriber {
    private fun checkGameOver(): Boolean = objectId !in objectManager
    private fun checkGameWon(): Boolean = objectId in objectManager &&
            objectManager.objects.count { it.isActor } == 1

    private var isGameOver = false
    private var isGameWon = false

    @Subscribe fun onUpdate(event: Update) {
        if (!isGameOver && checkGameOver()) {
            isGameOver = true
            publisher.post(PlaySound("/sounds/game_over.mp3"))
            uiHandler.onGameOver()
        }
        if (!isGameWon && checkGameWon()) {
            isGameWon = true
            publisher.post(PlaySound("/sounds/game_won.mp3"))
            uiHandler.onGameWon()
        }
    }
}
