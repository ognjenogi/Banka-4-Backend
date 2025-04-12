package rs.banka4.stock_service.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rs.banka4.stock_service.domain.trading.db.OtcRequest;
import rs.banka4.stock_service.domain.trading.db.RequestStatus;
import rs.banka4.stock_service.repositories.AssetOwnershipRepository;
import rs.banka4.stock_service.repositories.OtcRequestRepository;

/**
 * Cron job that finds OTC requests that are finished (e.g. accepted or completed) and have a
 * settlement date that has already passed. Such requests are then marked as expired, and their
 * associated asset ownership records are updated so that any reserved assets are made public
 * (released back for future trading).
 */
@Component
@RequiredArgsConstructor
public class OtcRequestExpiryService {
    private final OtcRequestRepository otcRequestRepository;
    private final AssetOwnershipRepository assetOwnershipRepository;

    /**
     * Runs every hour at the top of the hour.
     * <p>
     * This job finds all OTC requests with a status of FINISHED whose settlement date is before the
     * current date, updates the request status to EXPIRED, and updates the asset ownership record
     * by transferring the reserved assets into the public pool.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void expireFinishedOtcRequests() {
        LocalDate today = LocalDate.now();

        List<OtcRequest> finishedRequests =
            otcRequestRepository.findAllByStatusAndSettlementDateBefore(
                RequestStatus.FINISHED,
                today
            );

        for (OtcRequest request : finishedRequests) {
            request.setStatus(RequestStatus.EXPIRED);

            assetOwnershipRepository.findByMyId(
                UUID.fromString(
                    request.getMadeFor()
                        .userId()
                ),
                request.getOptionId()
            )
                .ifPresent(assetOwnership -> {
                    assetOwnership.setReservedAmount(
                        assetOwnership.getReservedAmount() - request.getAmount()
                    );
                    assetOwnership.setPublicAmount(
                        assetOwnership.getPublicAmount() + request.getAmount()
                    );
                    assetOwnershipRepository.save(assetOwnership);
                });
            request.setOptionId(null);
        }
        otcRequestRepository.saveAll(finishedRequests);
    }
}
