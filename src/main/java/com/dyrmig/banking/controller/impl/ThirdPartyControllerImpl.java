package com.dyrmig.banking.controller.impl;

import com.dyrmig.banking.classes.AmountOfOperationDTO;
import com.dyrmig.banking.classes.MyCustomException;
import com.dyrmig.banking.controller.interfaces.ThirdPartyController;
import com.dyrmig.banking.model.ThirdParty;
import com.dyrmig.banking.repository.ThirdPartyRepository;
import com.dyrmig.banking.service.interfaces.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ThirdPartyControllerImpl implements ThirdPartyController {
    @Autowired
    private ThirdPartyService thirdPartyService;
    @PostMapping("/thirdparty")
    public void saveThirdParty(@RequestBody ThirdParty thirdParty) {
        thirdPartyService.saveThirdParty(thirdParty);
    }
    @PostMapping("/thirdparty/{accountId}/charge")
    public void charge(@PathVariable(name = "accountId") Long accountId, @RequestBody AmountOfOperationDTO amountOfOperationDTO, @RequestHeader("hashed-key") String hashedKey){
        thirdPartyService.charge(accountId, amountOfOperationDTO, hashedKey);
    }
    @PostMapping("/thirdparty/{accountId}/refund")
    public void refund(@PathVariable(name = "accountId") Long accountId, @RequestBody AmountOfOperationDTO amountOfOperationDTO, @RequestHeader("hashed-key") String hashedKey){
        thirdPartyService.refund(accountId, amountOfOperationDTO, hashedKey);
    }
}
