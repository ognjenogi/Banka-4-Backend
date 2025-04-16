package rs.banka4.bank_service.service.impl;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rs.banka4.bank_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.bank_service.domain.listing.db.Listing;
import rs.banka4.bank_service.domain.options.db.Asset;
import rs.banka4.bank_service.domain.options.db.Option;
import rs.banka4.bank_service.domain.security.SecurityDto;
import rs.banka4.bank_service.domain.security.forex.db.ForexPair;
import rs.banka4.bank_service.domain.security.future.db.Future;
import rs.banka4.bank_service.domain.security.responses.AssetTypeDto;
import rs.banka4.bank_service.domain.security.responses.SecurityHoldingDto;
import rs.banka4.bank_service.domain.security.stock.db.Stock;
import rs.banka4.bank_service.domain.taxes.db.UserTaxDebts;
import rs.banka4.bank_service.domain.taxes.db.dto.UserTaxInfoDto;
import rs.banka4.bank_service.exceptions.AssetNotFound;
import rs.banka4.bank_service.repositories.AssetOwnershipRepository;
import rs.banka4.bank_service.repositories.ListingRepository;
import rs.banka4.bank_service.repositories.OrderRepository;
import rs.banka4.bank_service.repositories.UserTaxDebtsRepository;
import rs.banka4.bank_service.service.abstraction.ExchangeRateService;
import rs.banka4.bank_service.service.abstraction.ListingService;
import rs.banka4.bank_service.service.abstraction.SecuritiesService;
import rs.banka4.bank_service.utils.profit.ProfitCalculator;
import rs.banka4.rafeisen.common.currency.CurrencyCode;

@Service
@RequiredArgsConstructor
public class SecuritiesServiceImpl implements SecuritiesService {

    private final OrderRepository orderRepository;
    private final ListingService listingService;
    private final AssetOwnershipRepository assetOwnershipRepository;
    private final ListingRepository listingRepository;
    private final ProfitCalculator profitCalculator;
    private final ExchangeRateService exchangeRateService;
    private final UserTaxDebtsRepository userTaxDebtsRepository;

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
        var ownerships = assetOwnershipRepository.findByUserId(myId, pageable);
        return ownerships.map(ownership -> {
            var asset =
                ownership.getId()
                    .getAsset();
            int publicAmount = asset instanceof Stock ? ownership.getPublicAmount() : 0;
            int totalAmount = ownership.getPrivateAmount() + publicAmount;
            Optional<Listing> optionalListing;
            if (asset instanceof Option option) {
                optionalListing =
                    listingRepository.getLatestListing(
                        option.getStock()
                            .getId(),
                        Limit.of(1)
                    );
            } else {
                optionalListing = listingRepository.getLatestListing(asset.getId(), Limit.of(1));
            }
            var currentPrice =
                optionalListing.map(
                    listing -> new MonetaryAmount(
                        listing.getBid(),
                        listing.getExchange()
                            .getCurrency()
                    )
                )
                    .orElseThrow(AssetNotFound::new);

            var profit = profitCalculator.calculateProfit(myId, asset, currentPrice);

            var ticker = asset.getTicker();

//            var lastModified=orderRepository.findNewestOrder(myId,asset,true);
            return new SecurityHoldingDto(
                mapToAssetTypeDto(asset),
                ticker,
                totalAmount,
                currentPrice,
                profit,
                publicAmount,
                OffsetDateTime.now()
            );
        });
    }

    @Override
    public MonetaryAmount calculateTotalProfit(UUID myId) {
        var ownerships = assetOwnershipRepository.findByUserId(myId);

        return ownerships.stream()
            .map(ownership -> {
                var asset =
                    ownership.getId()
                        .getAsset();
                if (asset instanceof Stock) {
                    var listingOpt = listingRepository.getLatestListing(asset.getId(), Limit.of(1));
                    var currentPrice =
                        listingOpt.map(
                            listing -> new MonetaryAmount(
                                listing.getBid(),
                                listing.getExchange()
                                    .getCurrency()
                            )
                        )
                            .orElseThrow(AssetNotFound::new);
                    return profitCalculator.calculateProfit(myId, asset, currentPrice);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .reduce(new MonetaryAmount(BigDecimal.ZERO, CurrencyCode.USD), (m1, m2) -> {
                if (
                    !m1.getCurrency()
                        .equals(CurrencyCode.USD)
                ) {
                    m1.setAmount(
                        exchangeRateService.convertCurrency(
                            m1.getAmount(),
                            m1.getCurrency(),
                            CurrencyCode.USD
                        )
                    );
                    m1.setCurrency(CurrencyCode.USD);
                }
                if (
                    !m2.getCurrency()
                        .equals(CurrencyCode.USD)
                ) {
                    m2.setAmount(
                        exchangeRateService.convertCurrency(
                            m2.getAmount(),
                            m2.getCurrency(),
                            CurrencyCode.USD
                        )
                    );
                    m2.setCurrency(CurrencyCode.USD);
                }
                return new MonetaryAmount(
                    m1.getAmount()
                        .add(m2.getAmount()),
                    CurrencyCode.USD
                );
            });
    }

    @Override
    public UserTaxInfoDto calculateTax(UUID myId) {
        var debts = userTaxDebtsRepository.findByAccount_Client_Id(myId);

        var totalUnpaid =
            debts.stream()
                .map(debt -> {
                    CurrencyCode accountCurrency =
                        debt.getAccount()
                            .getCurrency();
                    if (!accountCurrency.equals(CurrencyCode.RSD)) {
                        return exchangeRateService.convertCurrency(
                            debt.getDebtAmount(),
                            accountCurrency,
                            CurrencyCode.RSD
                        );
                    }
                    return debt.getDebtAmount();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var totalYearly =
            debts.stream()
                .map(UserTaxDebts::getYearlyDebtAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new UserTaxInfoDto(totalYearly, totalUnpaid, "RSD");
    }


    private AssetTypeDto mapToAssetTypeDto(Asset asset) {
        if (asset instanceof Stock) {
            return AssetTypeDto.STOCK;
        } else if (asset instanceof Future) {
            return AssetTypeDto.FUTURE;
        } else if (asset instanceof ForexPair) {
            return AssetTypeDto.FOREX_PAIR;
        } else if (asset instanceof Option) {
            return AssetTypeDto.OPTION;
        }
        throw new IllegalArgumentException(
            "Unsupported asset type: "
                + asset.getClass()
                    .getName()
        );
    }
}
