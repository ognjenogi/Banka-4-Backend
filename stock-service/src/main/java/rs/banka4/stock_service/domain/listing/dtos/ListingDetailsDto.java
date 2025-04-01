package rs.banka4.stock_service.domain.listing.dtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.banka4.stock_service.domain.listing.dtos.specific.ForexPairDto;
import rs.banka4.stock_service.domain.listing.dtos.specific.FutureDto;
import rs.banka4.stock_service.domain.listing.dtos.specific.StockDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(
    description = "Dto for showing details after clicking on one listing, this Dto can be of 3 types.\n"
        + "Look for StockDto, ForexPairDto and FutureDto for exact information about each of them.\n"
        + "Type od dto can be found in securityType field."
)
@JsonTypeInfo(
    property = "securityType",
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY
)
@JsonSubTypes({
    @Type(
        value = StockDto.class,
        name = "STOCK"
    ),
    @Type(
        value = ForexPairDto.class,
        name = "STOCK"
    ),
    @Type(
        value = FutureDto.class,
        name = "STOCK"
    )
})
public class ListingDetailsDto {
    private String name;
    private String ticker;
    @Schema(
        description = "Number of this security buys today",
        defaultValue = "10"
    )
    private Integer volume;
    @Schema(description = "Difference between current price and last price yesterday")
    private BigDecimal change;
    @Schema(description = "Current price")
    private BigDecimal price;
}
