package rs.banka4.stock_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.banka4.stock_service.repositories.OtcRequestRepository;
import rs.banka4.stock_service.service.abstraction.OtcRequestService;
@Service
@RequiredArgsConstructor
public class OtcRequestServiceImp implements OtcRequestService{
    private final OtcRequestRepository otcRequestRepository;
}
