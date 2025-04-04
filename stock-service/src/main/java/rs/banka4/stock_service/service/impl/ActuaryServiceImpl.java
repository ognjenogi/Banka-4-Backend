package rs.banka4.stock_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rs.banka4.stock_service.domain.actuaries.db.ActuaryInfo;
import rs.banka4.stock_service.domain.actuaries.db.MonetaryAmount;
import rs.banka4.stock_service.domain.actuaries.db.dto.ActuaryPayloadDto;
import rs.banka4.stock_service.domain.response.ActuaryInfoDto;
import rs.banka4.stock_service.domain.response.CombinedResponse;
import rs.banka4.stock_service.domain.response.EmployeeResponseDto;
import rs.banka4.stock_service.domain.response.LimitPayload;
import rs.banka4.stock_service.domain.security.forex.db.CurrencyCode;
import rs.banka4.stock_service.exceptions.ActuaryNotFoundException;
import rs.banka4.stock_service.exceptions.CannotUpdateActuaryException;
import rs.banka4.stock_service.exceptions.NegativeLimitException;
import rs.banka4.stock_service.repositories.ActuaryRepository;
import rs.banka4.stock_service.service.abstraction.ActuaryService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActuaryServiceImpl implements ActuaryService {

    private final ActuaryRepository actuaryRepository;
    private final RestTemplate restTemplate;

    @Override
    public void createNewActuary(ActuaryPayloadDto dto){
        if(dto.limitAmount().compareTo(BigDecimal.ZERO) < 0){
            throw new NegativeLimitException(dto.actuaryId().toString());
        }



        ActuaryInfo actuaryInfo = new ActuaryInfo();
        actuaryInfo.setUserId(dto.actuaryId());
        actuaryInfo.setLimit(new MonetaryAmount(
            dto.limitAmount(),
            CurrencyCode.RSD
        ));
        actuaryInfo.setNeedApproval(dto.needsApproval());
        actuaryRepository.save(actuaryInfo);
    }

    @Override
    public void changeActuaryDetails(UUID actuaryId, ActuaryPayloadDto dto){

        if(actuaryId != dto.actuaryId()){
            //case when we send the admin id as the path parameter
            //also when all the securities should be transfered to the admin
        }

        if(dto.limitAmount().compareTo(BigDecimal.ZERO) == -1){
            throw new NegativeLimitException(actuaryId.toString());
        }



        ActuaryInfo actuaryInfo = actuaryRepository.findById(dto.actuaryId()).get();
        actuaryInfo.setNeedApproval(dto.needsApproval());
        actuaryInfo.setLimit(new MonetaryAmount(dto.limitAmount(),CurrencyCode.RSD));
        actuaryRepository.save(actuaryInfo);
    }


    @Override
    public ResponseEntity<Page<CombinedResponse>> search(Authentication auth, String firstName, String lastName, String email, String position, int page, int size) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + auth.getCredentials());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<PageImpl<EmployeeResponseDto>> response =
            restTemplate.exchange(
                "http://user_service:8080/search/actuary-only",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
            );

        Page<EmployeeResponseDto> employeePage = response.getBody();
        if (employeePage.isEmpty()) {
            return ResponseEntity.ok(Page.empty());
        }

        List<CombinedResponse> combinedResponses = employeePage.stream()
            .map(employee -> {
                ActuaryInfo actuaryInfo = actuaryRepository.findById(employee.id()).get();
                ActuaryInfoDto dto = new ActuaryInfoDto(
                    actuaryInfo.isNeedApproval(),
                    actuaryInfo.getLimit().getAmount(),
                    actuaryInfo.getUsedLimit().getAmount(),
                    actuaryInfo.getLimit().getCurrency());
                return new CombinedResponse(employee, dto);
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(new PageImpl<>(combinedResponses, employeePage.getPageable(), employeePage.getTotalElements()));
    }


    @Override
    public void updateLimit(UUID actuaryId, LimitPayload dto) {
        ActuaryInfo actuaryInfo = actuaryRepository.findById(actuaryId)
            .orElseThrow(() -> new ActuaryNotFoundException(actuaryId.toString()));

        // Supervisors limit cannot be changed
        if (!actuaryInfo.isNeedApproval()) {
            throw new CannotUpdateActuaryException(actuaryId.toString());
        }
        if (dto.limitAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeLimitException(actuaryId.toString());
        }

        actuaryInfo.setLimit(new MonetaryAmount(dto.limitAmount(), dto.limitCurrencyCode()));
        actuaryRepository.save(actuaryInfo);
    }

    @Override
    public void resetUsedLimit(UUID actuaryId){
        ActuaryInfo actuaryInfo = actuaryRepository.findById(actuaryId).orElseThrow();
        MonetaryAmount monetaryAmount = actuaryInfo.getUsedLimit();
        monetaryAmount.setAmount(BigDecimal.ZERO);
        actuaryInfo.setUsedLimit(monetaryAmount);
        actuaryRepository.save(actuaryInfo);
    }
}
