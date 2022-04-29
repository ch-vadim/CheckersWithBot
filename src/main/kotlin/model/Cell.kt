package model

class Cell( val x: Int, val y: Int) {

    operator fun plus(other: Cell): Cell {
        return Cell(x + other.x, y + other.y)
    }

    operator fun minus(other: Cell): Cell {
        return Cell(x - other.x, y - other.y)
    }

    operator fun times(other: Int): Cell {
        return Cell(x * other, y * other)
    }

    operator fun div(other: Int):Cell {
        return Cell(x / other, y / other )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is Cell) {
            return x == other.x && y == other.y
        }
        return false
    }

    override fun hashCode(): Int {
        var result = 11
        result = 19 * result + x
        return 19 * result + y
    }

    override fun toString(): String {
        return "$x:$y"
    }

}