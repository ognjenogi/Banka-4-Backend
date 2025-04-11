package rs.banka4.stock_service.domain.security.stock.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import rs.banka4.stock_service.domain.security.stock.db.Stock;
import rs.banka4.stock_service.domain.security.stock.dtos.StockDto;
import rs.banka4.stock_service.domain.security.stock.dtos.StockInfoDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,componentModel = "spring")
public interface StockMapper {
    Stock toEntity(StockDto dto);
    StockDto toDto(Stock stock);
    StockInfoDto toInfoDto(Stock stock);
}
