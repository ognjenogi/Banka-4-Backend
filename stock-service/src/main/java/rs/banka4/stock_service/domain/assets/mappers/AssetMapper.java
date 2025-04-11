package rs.banka4.stock_service.domain.assets.mappers;

import java.time.OffsetDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import rs.banka4.stock_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.stock_service.domain.assets.db.AssetOwnership;
import rs.banka4.stock_service.domain.listing.dtos.SecurityType;
import rs.banka4.stock_service.domain.trading.dtos.PublicStocksDto;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssetMapper {
    AssetMapper INSTANCE = Mappers.getMapper(AssetMapper.class);

    @Mapping(
        target = "ticker",
        expression = "java(assetOwnership.getId().getAsset().getTicker())"
    )
    @Mapping(
        target = "name",
        expression = "java(assetOwnership.getId().getAsset().getName())"
    )
    @Mapping(
        target = "amount",
        expression = "java(assetOwnership.getPublicAmount())"
    )
    PublicStocksDto mapPublicStocksDto(
        AssetOwnership assetOwnership,
        SecurityType securityType,
        String ownerUsername,
        MonetaryAmount activePrice,
        OffsetDateTime lastUpdated
    );
}
