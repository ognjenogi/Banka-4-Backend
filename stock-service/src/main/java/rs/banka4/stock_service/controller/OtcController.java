package rs.banka4.stock_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.banka4.stock_service.controller.docs.OtcApiDocumentation;

@RestController
@RequestMapping("/otc")
@RequiredArgsConstructor
public class OtcController implements OtcApiDocumentation {
}
