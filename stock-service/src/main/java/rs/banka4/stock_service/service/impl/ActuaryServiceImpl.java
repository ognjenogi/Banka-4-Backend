package rs.banka4.stock_service.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import rs.banka4.stock_service.config.clients.UserServiceClient;
import rs.banka4.stock_service.domain.actuaries.db.ActuaryInfo;
import rs.banka4.stock_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.stock_service.domain.actuaries.db.dto.ActuaryPayloadDto;
import rs.banka4.stock_service.domain.response.*;
import rs.banka4.stock_service.domain.security.forex.db.CurrencyCode;
import rs.banka4.stock_service.exceptions.ActuaryNotFoundException;
import rs.banka4.stock_service.exceptions.CannotUpdateActuaryException;
import rs.banka4.stock_service.exceptions.NegativeLimitException;
import rs.banka4.stock_service.repositories.ActuaryRepository;
import rs.banka4.stock_service.service.abstraction.ActuaryService;

@Service
@RequiredArgsConstructor
public class ActuaryServiceImpl implements ActuaryService {

    private final ActuaryRepository actuaryRepository;
    private final Retrofit userServiceRetrofit;
    Logger logger = LoggerFactory.getLogger(ActuaryServiceImpl.class);


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

        ActuaryInfo actuaryInfo = new ActuaryInfo();
        actuaryInfo.setUserId(dto.actuaryId());
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
                .orElseThrow(
                    () -> new ActuaryNotFoundException(
                        dto.actuaryId()
                            .toString()
                    )
                );

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

        UserServiceClient userServiceClient = userServiceRetrofit.create(UserServiceClient.class);
        String token = "Bearer " + auth.getCredentials();
        try {
            Response<PaginatedResponse<EmployeeResponseDto>> response =
                userServiceClient.searchActuaryOnly(
                    token,
                    firstName,
                    lastName,
                    email,
                    position,
                    page,
                    size
                )
                    .execute();

            if (response.isSuccessful() && response.body() != null) {
                PaginatedResponse<EmployeeResponseDto> employeePage = response.body();
                if (

                    employeePage.getContent()
                        .isEmpty()
                ) {
                    logger.info("No actuaries found for the given search criteria.");

                    return ResponseEntity.ok(Page.empty());
                }

                List<CombinedResponse> combinedResponses =
                    employeePage.getContent()
                        .stream()
                        .map(employee -> {
                            ActuaryInfo actuaryInfo =
                                actuaryRepository.findById(employee.id())
                                    .orElseThrow(
                                        () -> new ActuaryNotFoundException(
                                            employee.id()
                                                .toString()
                                        )
                                    );
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
                        .collect(Collectors.toList());

                logger.info(
                    "Found {} actuaries matching the search criteria.",
                    combinedResponses.size()
                );

                return ResponseEntity.ok(
                    new PageImpl<>(
                        combinedResponses,
                        PageRequest.of(
                            employeePage.getPage()
                                .getNumber(),
                            employeePage.getPage()
                                .getSize()
                        ),
                        employeePage.getPage()
                            .getTotalElements()
                    )
                );
            } else {
                logger.error("Failed to search actuaries. Response code: {}", response.code());

                return ResponseEntity.status(response.code())
                    .build();
            }
        } catch (Exception e) {
            logger.error("Exception occurred while searching actuaries", e);

            return ResponseEntity.status(500)
                .build();
        }
    }


    @Override
    public void updateLimit(UUID actuaryId, LimitPayload dto) {
        ActuaryInfo actuaryInfo =
            actuaryRepository.findById(actuaryId)
                .orElseThrow(() -> new ActuaryNotFoundException(actuaryId.toString()));

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
                .orElseThrow(() -> new ActuaryNotFoundException(actuaryId.toString()));
        MonetaryAmount monetaryAmount = new MonetaryAmount(BigDecimal.ZERO, CurrencyCode.RSD);
        actuaryInfo.setUsedLimit(monetaryAmount);
        actuaryRepository.save(actuaryInfo);
    }
}
