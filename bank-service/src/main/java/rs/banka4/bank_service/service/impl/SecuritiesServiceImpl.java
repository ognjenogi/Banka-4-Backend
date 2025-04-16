package rs.banka4.bank_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rs.banka4.bank_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.bank_service.domain.listing.db.Listing;
import rs.banka4.bank_service.domain.options.db.Option;
import rs.banka4.bank_service.domain.security.SecurityDto;
import rs.banka4.bank_service.domain.security.responses.SecurityHoldingDto;
import rs.banka4.bank_service.domain.security.stock.db.Stock;
import rs.banka4.bank_service.exceptions.AssetNotFound;
import rs.banka4.bank_service.repositories.AssetOwnershipRepository;
import rs.banka4.bank_service.repositories.ListingRepository;
import rs.banka4.bank_service.repositories.OrderRepository;
import rs.banka4.bank_service.service.abstraction.ExchangeRateService;
import rs.banka4.bank_service.service.abstraction.ListingService;
import rs.banka4.bank_service.service.abstraction.SecuritiesService;
import rs.banka4.bank_service.utils.profit.ProfitCalculator;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecuritiesServiceImpl implements SecuritiesService {

    private final OrderRepository orderRepository;
    private final ListingService listingService;
    private final AssetOwnershipRepository assetOwnershipRepository;
    private final ListingRepository listingRepository;
    private final ProfitCalculator profitCalculator;
    private final ExchangeRateService exchangeRateService;
    @Override
    public ResponseEntity<Page<SecurityDto>> getSecurities(
        String securityType,
        String name,
        Pageable pageable
    ) {
        return null;
    }

    @Override
    public Page<SecurityHoldingDto> getMyPortfolio(UUID myId, Pageable pageable) {
        var ownerships = assetOwnershipRepository.findByUserId(myId,pageable);
        return ownerships.map(ownership -> {
            var asset = ownership.getId().getAsset();
            int totalAmount = ownership.getPrivateAmount() + ownership.getPublicAmount();
            Optional<Listing> optionalListing;
            if(asset instanceof Option option) {
                 optionalListing = listingRepository.getLatestListing(option.getStock().getId(), Limit.of(1));
            }else {
                 optionalListing = listingRepository.getLatestListing(asset.getId(), Limit.of(1));
            }
            var currentPrice = optionalListing.map(listing -> new MonetaryAmount(listing.getBid(),listing.getExchange().getCurrency()) ).orElseThrow(AssetNotFound::new);

            var profit = profitCalculator.calculateProfit(myId,asset,currentPrice);

            var ticker = asset.getTicker();

//            var lastModified=orderRepository.findNewestOrder(myId,asset,true);
            return new SecurityHoldingDto(ticker,totalAmount,currentPrice,profit,asset instanceof Stock ? ownership.getPublicAmount():0, OffsetDateTime.now());
        });
    }

    @Override
    public MonetaryAmount calculateTotalProfit(UUID myId) {
        var ownerships = assetOwnershipRepository.findByUserId(myId);

        return ownerships.stream().map(ownership -> {
                var asset = ownership.getId().getAsset();
                if (asset instanceof Stock) {
                    var listingOpt = listingRepository.getLatestListing(asset.getId(), Limit.of(1));
                    var currentPrice = listingOpt
                        .map(listing -> new MonetaryAmount(listing.getBid(), listing.getExchange().getCurrency()))
                        .orElseThrow(AssetNotFound::new);
                    return profitCalculator.calculateProfit(myId,asset,currentPrice);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .reduce(new MonetaryAmount(BigDecimal.ZERO,CurrencyCode.USD), (m1, m2) -> {
                if(!m1.getCurrency().equals(CurrencyCode.USD)) {
                    m1.setAmount(exchangeRateService.convertCurrency(m1.getAmount(),m1.getCurrency(),CurrencyCode.USD));
                    m1.setCurrency(CurrencyCode.USD);
                }
                if(!m2.getCurrency().equals(CurrencyCode.USD)) {
                    m2.setAmount(exchangeRateService.convertCurrency(m2.getAmount(),m2.getCurrency(),CurrencyCode.USD));
                    m2.setCurrency(CurrencyCode.USD);
                }
                return new MonetaryAmount(m1.getAmount().add(m2.getAmount()), CurrencyCode.USD);
            });
    }

}
