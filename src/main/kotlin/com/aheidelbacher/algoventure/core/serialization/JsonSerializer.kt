package com.aheidelbacher.algoventure.core.serialization

import com.aheidelbacher.algostorm.engine.serialization.JsonDriver
import com.aheidelbacher.algostorm.engine.serialization.SerializationDriver

object JsonSerializer : SerializationDriver by JsonDriver() {
    override fun release() {}
}
