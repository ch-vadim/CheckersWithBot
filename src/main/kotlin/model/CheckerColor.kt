package model

enum class CheckerColor {
    BLACK, WHITE;

    fun opposite() = if (this == WHITE) BLACK else WHITE
}