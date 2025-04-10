package rs.banka4.stock_service.domain.trading.db;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import rs.banka4.stock_service.domain.security.stock.mapper.StockMapper;
import rs.banka4.stock_service.domain.trading.db.dtos.OtcRequestDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {StockMapper.class},componentModel = "spring")
public interface OtcMapper {
    OtcMapper INSTANCE = Mappers.getMapper(OtcMapper.class);

    @Mapping(target = "madeBy", source = "madeBy")
    @Mapping(target = "madeFor", source = "madeFor")
    @Mapping(target = "modifiedBy", source = "modifiedBy")
    OtcRequestDto toOtcRequestDto(OtcRequest otcRequest, String madeBy, String madeFor, String modifiedBy);
}
