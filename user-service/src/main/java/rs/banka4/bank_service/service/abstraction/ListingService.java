package rs.banka4.bank_service.service.abstraction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rs.banka4.bank_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.bank_service.domain.listing.db.Listing;
import rs.banka4.bank_service.domain.listing.dtos.ListingDetailsDto;
import rs.banka4.bank_service.domain.listing.dtos.ListingFilterDto;
import rs.banka4.bank_service.domain.listing.dtos.ListingInfoDto;
import rs.banka4.bank_service.domain.listing.dtos.OptionDto;
import rs.banka4.bank_service.domain.listing.dtos.PriceChangeDto;

public interface ListingService {
    int getVolumeOfAsset(UUID securityId);

    BigDecimal calculateChange(UUID securityId, BigDecimal currentPrice);

    Page<ListingInfoDto> getListings(ListingFilterDto filter, Pageable pageable, boolean isClient);

    List<PriceChangeDto> getPriceChanges(UUID securityId);

    ListingDetailsDto getListingDetails(UUID securityId);

    List<OptionDto> getOptionsWithSettlementDateForStock(
        UUID stockId,
        OffsetDateTime settlementDate
    );

    MonetaryAmount getLatestPriceForStock(UUID stockId);

    Optional<Listing> findActiveListingByAsset(UUID assetId);
}
