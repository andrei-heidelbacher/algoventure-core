package com.aheidelbacher.algoventure.core.ai

import org.junit.Assert.assertEquals
import org.junit.Test

import com.aheidelbacher.algostorm.systems.geometry2d.Point

class UtilTest {
    fun makeMap(vararg rows: String): (Point) -> Boolean = { p ->
        p.y < 0 || rows.size <= p.y || p.x < 0 || rows[p.y].length <= p.x
                || rows[p.y][p.x] !in ".SD"
    }

    private fun findSymbolInMap(symbol: Char, vararg rows: String): Point {
        val y = rows.indices.single { symbol in rows[it] }
        val x = rows[y].indices.single { rows[y][it] == symbol }
        return Point(x, y)
    }

    fun testMap(expectedPathLength: Int?, vararg rows: String) {
        val source = findSymbolInMap('S', *rows)
        val destination = findSymbolInMap('D', *rows)
        val path = findPath(source, destination, makeMap(*rows))
        assertEquals(expectedPathLength, path?.size)
        if (path != null) {
            var (x, y) = source
            assertEquals('S', rows[y][x])
            path.forEachIndexed { i, direction ->
                x += direction.dx
                y += direction.dy
                if (i + 1 < path.size) {
                    assertEquals('.', rows[y][x])
                }
            }
            assertEquals('D', rows[y][x])
        }
    }

    @Test
    fun testFindPath() {
        testMap(
                4,
                "SX..",
                "..X.",
                "..X.",
                "X..D"
        )
    }

    @Test
    fun testFindPath2() {
        testMap(
                11,
                ".......",
                "..SXXX.",
                ".XXX...",
                ".XX.XXX",
                ".X.XXD.",
                ".X...X."
        )
    }

    @Test
    fun testFindPath3() {
        testMap(
                9,
                ".......",
                "..SXXX.",
                ".XXX...",
                ".XX.XXX",
                ".X.XXD.",
                ".....X."
        )
    }
}
