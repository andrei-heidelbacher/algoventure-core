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

import com.aheidelbacher.algostorm.engine.Engine
import com.aheidelbacher.algostorm.event.EventBus
import com.aheidelbacher.algostorm.physics2d.PhysicsSystem
import com.aheidelbacher.algostorm.serialization.Serializer
import com.aheidelbacher.algostorm.state.Map
import com.aheidelbacher.algostorm.state.ObjectManager
import com.aheidelbacher.algostorm.time.Tick

import com.aheidelbacher.algoventure.core.facing.FacingSystem
import com.aheidelbacher.algoventure.core.graphics2d.RenderingSystem
import com.aheidelbacher.algoventure.core.move.MovementSystem

import java.io.OutputStream

class AlgoventureEngine(
        private val map: Map,
        private val eventBus: EventBus,
        platform: Platform
) : Engine() {
    private val objectManager = ObjectManager(map, "objects")

    private val systems = listOf(
            RenderingSystem(map, platform.canvas),
            PhysicsSystem(objectManager, eventBus),
            MovementSystem(objectManager, eventBus),
            FacingSystem(objectManager)
    )

    private val subscriptions = systems.map { eventBus.subscribe(it) }

    override val millisPerTick: Int
        get() = 15

    override fun clearState() {
        subscriptions.forEach { it.unsubscribe() }
        objectManager.objects.toList().forEach {
            it.properties.clear()
            objectManager.delete(it.id)
        }
    }

    override fun handleTick() {
        eventBus.post(Tick(millisPerTick))
        eventBus.publishPosts()
    }

    override fun writeStateToStream(outputStream: OutputStream) {
        Serializer.writeValue(outputStream, map)
    }
}
