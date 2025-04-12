package rs.banka4.stock_service.domain.trading.db;

import org.mapstruct.*;
import rs.banka4.stock_service.domain.security.stock.mapper.StockMapper;
import rs.banka4.stock_service.domain.trading.db.dtos.OtcRequestDto;
import rs.banka4.stock_service.domain.trading.db.dtos.OtcRequestUpdateDto;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {
        StockMapper.class
    },
    componentModel = "spring"
)
public interface OtcMapper {
    @Mapping(
        target = "madeBy",
        source = "madeBy"
    )
    @Mapping(
        target = "madeFor",
        source = "madeFor"
    )
    @Mapping(
        target = "modifiedBy",
        source = "modifiedBy"
    )
    OtcRequestDto toOtcRequestDto(
        OtcRequest otcRequest,
        String madeBy,
        String madeFor,
        String modifiedBy
    );
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget OtcRequest otc, OtcRequestUpdateDto dto);
}
