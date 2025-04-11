package rs.banka4.stock_service.service.impl;


import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Response;
import retrofit2.Retrofit;
import rs.banka4.rafeisen.common.dto.UserResponseDto;
import rs.banka4.stock_service.config.clients.UserServiceClient;
import rs.banka4.stock_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.stock_service.domain.assets.db.AssetOwnership;
import rs.banka4.stock_service.domain.assets.mappers.AssetMapper;
import rs.banka4.stock_service.domain.listing.dtos.SecurityType;
import rs.banka4.stock_service.domain.security.stock.db.Stock;
import rs.banka4.stock_service.domain.trading.dtos.PublicStocksDto;
import rs.banka4.stock_service.exceptions.NotEnoughStock;
import rs.banka4.stock_service.exceptions.RequestFailed;
import rs.banka4.stock_service.exceptions.StockOwnershipNotFound;
import rs.banka4.stock_service.repositories.AssetOwnershipRepository;
import rs.banka4.stock_service.service.abstraction.AssetOwnershipService;
import rs.banka4.stock_service.service.abstraction.ListingService;

@Service
@RequiredArgsConstructor
public class AssetOwnershipServiceImpl implements AssetOwnershipService {
    private final AssetOwnershipRepository assetOwnershipRepository;
    private final Retrofit userServiceRetrofit;
    private static final Logger logger = LoggerFactory.getLogger(AssetOwnershipServiceImpl.class);
    private final ListingService listingService;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AssetOwnership transferStock(
        @NonNull UUID userId,
        @NonNull UUID assetId,
        int amount,
        @NonNull TransferTo transferTo
    ) {
        Optional<AssetOwnership> assetOwnershipOptional =
            assetOwnershipRepository.findByMyId(userId, assetId);
        if (
            assetOwnershipOptional.isPresent()
                && assetOwnershipOptional.get()
                    .getId()
                    .getAsset() instanceof Stock
        ) {
            AssetOwnership assetOwnership = assetOwnershipOptional.get();
            if (transferTo == TransferTo.PUBLIC) {
                if (amount <= assetOwnership.getPrivateAmount()) {
                    // do the transfer from private to public
                    assetOwnership.setPrivateAmount(assetOwnership.getPrivateAmount() - amount);
                    assetOwnership.setPublicAmount(assetOwnership.getPublicAmount() + amount);
                    assetOwnershipRepository.save(assetOwnership);
                } else {
                    throw new NotEnoughStock();
                }
            } else {
                if (amount <= assetOwnership.getPublicAmount()) {
                    // do the transfer from public to private
                    assetOwnership.setPublicAmount(assetOwnership.getPublicAmount() - amount);
                    assetOwnership.setPrivateAmount(assetOwnership.getPrivateAmount() + amount);
                    assetOwnershipRepository.save(assetOwnership);
                } else {
                    throw new NotEnoughStock();
                }
            }
            return assetOwnership;
        } else {
            throw new StockOwnershipNotFound(assetId, userId);
        }
    }

    @Override
    public Page<PublicStocksDto> getPublicStocks(Pageable pageable, String token) {
        var ownerships = assetOwnershipRepository.findAllByPublicAmountGreaterThan(0, pageable);
        UserServiceClient userServiceClient = userServiceRetrofit.create(UserServiceClient.class);
        return ownerships.map((assetOwnership -> {
            try {
                Response<UserResponseDto> response =
                    userServiceClient.getUserInfo(
                        assetOwnership.getId()
                            .getUser(),
                        "Bearer: " + token
                    )
                        .execute();
                if (!response.isSuccessful() || response.body() == null) {
                    throw new RequestFailed();
                }
                UserResponseDto userResponseDto = response.body();
                MonetaryAmount latestPrice =
                    listingService.getLatestPriceForStock(
                        assetOwnership.getId()
                            .getAsset()
                            .getId()
                    );
                return AssetMapper.INSTANCE.mapPublicStocksDto(
                    assetOwnership,
                    SecurityType.STOCK,
                    userResponseDto.email(),
                    latestPrice,
                    OffsetDateTime.now()
                );

            } catch (IOException e) {
                throw new RequestFailed();
            }
        }));
    }
}
