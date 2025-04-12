package rs.banka4.stock_service.generator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import rs.banka4.rafeisen.common.currency.CurrencyCode;
import rs.banka4.stock_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.stock_service.domain.assets.db.AssetOwnership;
import rs.banka4.stock_service.domain.assets.db.AssetOwnershipId;
import rs.banka4.stock_service.domain.security.stock.db.Stock;
import rs.banka4.stock_service.domain.trading.db.ForeignBankId;
import rs.banka4.stock_service.domain.trading.db.OtcRequest;
import rs.banka4.stock_service.domain.trading.db.RequestStatus;
import rs.banka4.stock_service.repositories.AssetOwnershipRepository;
import rs.banka4.stock_service.repositories.AssetRepository;
import rs.banka4.stock_service.repositories.OtcRequestRepository;
import rs.banka4.stock_service.repositories.SecurityRepository;
import rs.banka4.stock_service.utils.AssetGenerator;
import rs.banka4.testlib.utils.JwtPlaceholders;

public class OtcRequestGen {

    public static OtcRequest createDummyOtcRequest(
        AssetRepository assetRepository,
        SecurityRepository securityRepository,
        OtcRequestRepository otcRequestRepository
    ) {
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var ex1 = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        var momo = new MonetaryAmount();
        momo.setAmount(BigDecimal.ONE);
        momo.setCurrency(CurrencyCode.AUD);
        OtcRequest req = new OtcRequest();
        req.setStock((Stock) ex1.get());
        req.setMadeBy(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setMadeFor(new ForeignBankId(1L, JwtPlaceholders.CLIENT_ID.toString()));
        req.setModifiedBy(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setStatus(RequestStatus.ACTIVE);
        req.setPremium(momo);
        req.setPricePerStock(momo);
        req.setSettlementDate(LocalDate.parse("2025-04-11"));
        req.setPricePerStock(momo);
        req.setAmount(1);

        otcRequestRepository.save(req);
        return req;
    }

    public static OtcRequest createDummyOtcRequestNotMe(
        AssetRepository assetRepository,
        SecurityRepository securityRepository,
        OtcRequestRepository otcRequestRepository
    ) {
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var ex1 = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        var momo = new MonetaryAmount();
        momo.setAmount(BigDecimal.ONE);
        momo.setCurrency(CurrencyCode.AUD);
        OtcRequest req = new OtcRequest();
        req.setStock((Stock) ex1.get());
        req.setMadeBy(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setMadeFor(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setModifiedBy(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setStatus(RequestStatus.ACTIVE);
        req.setPremium(momo);
        req.setPricePerStock(momo);
        req.setSettlementDate(LocalDate.parse("2025-04-11"));
        req.setPricePerStock(momo);
        req.setAmount(1);

        otcRequestRepository.save(req);
        return req;
    }

    public static OtcRequest createDummyOtcRequestMeUnread(
        AssetRepository assetRepository,
        SecurityRepository securityRepository,
        OtcRequestRepository otcRequestRepository
    ) {
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var ex1 = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        var momo = new MonetaryAmount();
        momo.setAmount(BigDecimal.ONE);
        momo.setCurrency(CurrencyCode.AUD);
        OtcRequest req = new OtcRequest();
        req.setStock((Stock) ex1.get());
        req.setMadeBy(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setMadeFor(new ForeignBankId(1L, JwtPlaceholders.CLIENT_ID.toString()));
        req.setModifiedBy(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setStatus(RequestStatus.ACTIVE);
        req.setPremium(momo);
        req.setPricePerStock(momo);
        req.setSettlementDate(LocalDate.parse("2025-04-11"));
        req.setPricePerStock(momo);
        req.setAmount(1);

        otcRequestRepository.save(req);
        return req;
    }

    public static OtcRequest createDummyOtcRequestMeRead(
        AssetRepository assetRepository,
        SecurityRepository securityRepository,
        OtcRequestRepository otcRequestRepository
    ) {
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var ex1 = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        var momo = new MonetaryAmount();
        momo.setAmount(BigDecimal.ONE);
        momo.setCurrency(CurrencyCode.AUD);
        OtcRequest req = new OtcRequest();
        req.setStock((Stock) ex1.get());
        req.setMadeBy(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setMadeFor(new ForeignBankId(1L, JwtPlaceholders.CLIENT_ID.toString()));
        req.setModifiedBy(new ForeignBankId(1L, JwtPlaceholders.CLIENT_ID.toString()));
        req.setStatus(RequestStatus.ACTIVE);
        req.setPremium(momo);
        req.setPricePerStock(momo);
        req.setSettlementDate(LocalDate.parse("2025-04-11"));
        req.setPricePerStock(momo);
        req.setAmount(1);

        otcRequestRepository.save(req);
        return req;
    }
    public static OtcRequest createDummyOtcRequestMeFinished(
        AssetRepository assetRepository,
        SecurityRepository securityRepository,
        OtcRequestRepository otcRequestRepository,
        int requestAmount
    ) {
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var ex1 = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        var momo = new MonetaryAmount();
        momo.setAmount(BigDecimal.TEN);
        momo.setCurrency(CurrencyCode.AUD);
        OtcRequest req = new OtcRequest();
        req.setStock((Stock) ex1.get());
        req.setMadeBy(
            new ForeignBankId(
                1L,
                UUID.randomUUID()
                    .toString()
            )
        );
        req.setMadeFor(new ForeignBankId(1L, JwtPlaceholders.CLIENT_ID.toString()));
        req.setModifiedBy(new ForeignBankId(1L, JwtPlaceholders.CLIENT_ID.toString()));
        req.setStatus(RequestStatus.FINISHED);
        req.setPremium(momo);
        req.setPricePerStock(momo);
        req.setSettlementDate(LocalDate.parse("2025-04-12"));
        req.setPricePerStock(momo);
        req.setAmount(requestAmount);
        req.setOptionId(AssetGenerator.OPTION_EX1_PUT_UUID);

        otcRequestRepository.saveAndFlush(req);
        return req;
    }

    public static void setupDummyAssetOwnership(
        UUID ownerUserId,
        int totalAvailable,
        AssetRepository assetRepository,
        AssetOwnershipRepository assetOwnershipRepository,
        SecurityRepository securityRepository
    ) {
        AssetGenerator.makeExampleAssets()
            .forEach(assetRepository::saveAndFlush);

        var ex1 = securityRepository.findById(AssetGenerator.STOCK_EX1_UUID);
        AssetOwnershipId id = new AssetOwnershipId(ownerUserId, ex1.get());
        AssetOwnership assetOwnership = new AssetOwnership();
        assetOwnership.setId(id);
        assetOwnership.setPublicAmount(totalAvailable / 2);
        assetOwnership.setReservedAmount(totalAvailable / 2);
        assetOwnershipRepository.save(assetOwnership);
    }
    public static AssetOwnership createDummyAssetOwnership(UUID userId, int reservedAmount, int publicAmount, AssetRepository assetRepository,AssetOwnershipRepository assetOwnershipRepository) {
//        AssetGenerator.makeExampleAssets()
//            .forEach(assetRepository::saveAndFlush);

        var ex1 = assetRepository.findById(AssetGenerator.OPTION_EX1_PUT_UUID);
        AssetOwnershipId ownershipId = new AssetOwnershipId();
        ownershipId.setUser(userId);
        ownershipId.setAsset(ex1.get());

        AssetOwnership assetOwnership = new AssetOwnership();
        assetOwnership.setId(ownershipId);
        assetOwnership.setReservedAmount(reservedAmount);
        assetOwnership.setPublicAmount(publicAmount);

        return assetOwnershipRepository.saveAndFlush(assetOwnership);
    }
}
