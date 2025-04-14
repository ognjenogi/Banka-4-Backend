package rs.banka4.bank_service.service.impl;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import rs.banka4.bank_service.domain.actuaries.db.ActuaryInfo;
import rs.banka4.bank_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.bank_service.domain.actuaries.db.dto.ActuaryPayloadDto;
import rs.banka4.bank_service.domain.response.*;
import rs.banka4.bank_service.domain.user.User;
import rs.banka4.bank_service.exceptions.ActuaryNotFoundException;
import rs.banka4.bank_service.exceptions.CannotUpdateActuaryException;
import rs.banka4.bank_service.exceptions.NegativeLimitException;
import rs.banka4.bank_service.exceptions.user.UserNotFound;
import rs.banka4.bank_service.repositories.ActuaryRepository;
import rs.banka4.bank_service.repositories.UserRepository;
import rs.banka4.bank_service.service.abstraction.ActuaryService;
import rs.banka4.rafeisen.common.currency.CurrencyCode;
import rs.banka4.rafeisen.common.dto.EmployeeResponseDto;

@Service
@RequiredArgsConstructor
public class ActuaryServiceImpl implements ActuaryService {

    private final ActuaryRepository actuaryRepository;
    private static final Logger logger = LoggerFactory.getLogger(ActuaryServiceImpl.class);
    private final UserRepository userRepository;


    @Override
    public void createNewActuary(ActuaryPayloadDto dto) {
        if (
            dto.limitAmount() != null
                && dto.limitAmount()
                    .compareTo(BigDecimal.ZERO)
                    < 0
        ) {
            throw new NegativeLimitException(
                dto.actuaryId()
                    .toString()
            );
        }
        Optional<User> user = userRepository.findById(dto.actuaryId());
        if (user.isEmpty()) {
            throw new UserNotFound(
                dto.actuaryId()
                    .toString()
            );
        }
        ActuaryInfo actuaryInfo = new ActuaryInfo();
        actuaryInfo.setUser(user.get());
        actuaryInfo.setLimit(new MonetaryAmount(dto.limitAmount(), CurrencyCode.RSD));
        actuaryInfo.setUsedLimit(new MonetaryAmount(BigDecimal.ZERO, CurrencyCode.RSD));
        actuaryInfo.setNeedApproval(dto.needsApproval());
        actuaryRepository.save(actuaryInfo);
    }

    @Override
    public void changeActuaryDetails(UUID actuaryId, ActuaryPayloadDto dto) {

        if (!actuaryId.equals(dto.actuaryId())) {
            // case when we send the admin id as the path parameter also when all the securities
            // should be transfered to the admin actuaryId is admins id
        }


        if (
            dto.limitAmount() != null
                && dto.limitAmount()
                    .compareTo(BigDecimal.ZERO)
                    < 0
        ) {
            throw new NegativeLimitException(actuaryId.toString());
        }


        ActuaryInfo actuaryInfo =
            actuaryRepository.findById(dto.actuaryId())
                .orElseThrow(() -> new ActuaryNotFoundException(dto.actuaryId()));

        actuaryInfo.setNeedApproval(dto.needsApproval());
        actuaryInfo.setLimit(new MonetaryAmount(dto.limitAmount(), CurrencyCode.RSD));
        actuaryRepository.save(actuaryInfo);
    }

    @Override
    public ResponseEntity<Page<CombinedResponse>> search(
        Authentication auth,
        String firstName,
        String lastName,
        String email,
        String position,
        int page,
        int size
    ) {
        throw new RuntimeException("TODO: not implemented");
    }

    private CombinedResponse toCombinedResponse(EmployeeResponseDto employee) {
        return actuaryRepository.findById(employee.id())
            .map(actuaryInfo -> {
                ActuaryInfoDto dto =
                    new ActuaryInfoDto(
                        actuaryInfo.isNeedApproval(),
                        actuaryInfo.getLimit()
                            .getAmount(),
                        actuaryInfo.getUsedLimit() != null
                            ? actuaryInfo.getUsedLimit()
                                .getAmount()
                            : null,
                        actuaryInfo.getLimit()
                            .getCurrency()
                    );
                return new CombinedResponse(employee, dto);
            })
            .orElseThrow(() -> {
                logger.error(
                    "ActuaryInfo not found for employee ID {} — this indicates inconsistent data.",
                    employee.id()
                );
                return new IllegalStateException(
                    "Missing ActuaryInfo for employee ID: " + employee.id()
                );
            });
    }


    @Override
    public void updateLimit(UUID actuaryId, LimitPayload dto) {
        ActuaryInfo actuaryInfo =
            actuaryRepository.findById(actuaryId)
                .orElseThrow(() -> new ActuaryNotFoundException(actuaryId));

        // Supervisors limit cannot be changed
        if (!actuaryInfo.isNeedApproval()) {
            throw new CannotUpdateActuaryException(actuaryId.toString());
        }
        if (
            dto.limitAmount()
                .compareTo(BigDecimal.ZERO)
                < 0
        ) {
            throw new NegativeLimitException(actuaryId.toString());
        }

        actuaryInfo.setLimit(new MonetaryAmount(dto.limitAmount(), dto.limitCurrencyCode()));
        actuaryRepository.save(actuaryInfo);
    }

    @Override
    public void resetUsedLimit(UUID actuaryId) {
        ActuaryInfo actuaryInfo =
            actuaryRepository.findById(actuaryId)
                .orElseThrow(() -> new ActuaryNotFoundException(actuaryId));
        actuaryInfo.setUsedLimit(
            resetLimit(
                actuaryInfo.getUsedLimit()
                    .getCurrency()
            )
        );
        actuaryRepository.save(actuaryInfo);
    }

    public static MonetaryAmount resetLimit(
        rs.banka4.rafeisen.common.currency.CurrencyCode currencyCode
    ) {
        MonetaryAmount monetaryAmount = new MonetaryAmount();
        monetaryAmount.setAmount(BigDecimal.valueOf(0));
        monetaryAmount.setCurrency(currencyCode);
        return monetaryAmount;
    }
}
