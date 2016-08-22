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

package com.aheidelbacher.algoventure.core.sound

import com.aheidelbacher.algostorm.engine.Update
import com.aheidelbacher.algostorm.engine.sound.PlaySound
import com.aheidelbacher.algostorm.engine.sound.StopStream
import com.aheidelbacher.algostorm.event.Publisher
import com.aheidelbacher.algostorm.event.Subscribe
import com.aheidelbacher.algostorm.event.Subscriber

class SoundManagerSystem(
        private val soundtrackSound: String,
        private val publisher: Publisher
) : Subscriber {
    private var soundtrackStreamId = -1

    @Subscribe fun playSoundtrack(event: PlaySoundtrack) {
        publisher.publish(PlaySound(
                sound = soundtrackSound,
                loop = true,
                onResult = { soundtrackStreamId = it }
        ))
    }

    @Subscribe fun stopSoundtrack(event: StopSoundtrack) {
        if (soundtrackStreamId != -1) {
            publisher.publish(StopStream(soundtrackStreamId))
        }
    }

    @Subscribe fun onUpdate(event: Update) {
        if (soundtrackStreamId == -1) {
            publisher.publish(PlaySoundtrack)
        }
    }
}
