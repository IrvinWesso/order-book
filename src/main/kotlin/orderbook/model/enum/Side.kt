package orderbook.model.enum

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class Side {
    BUY, SELL;

    @JsonValue
    fun toValue(): String = name.lowercase()

    companion object {
        @JvmStatic
        @JsonCreator
        fun from(value: String?): Side =
            when (value?.lowercase()) {
                "buy" -> BUY
                "sell" -> SELL
                else -> throw IllegalArgumentException("Invalid OrderSide: $value")
            }
    }
}