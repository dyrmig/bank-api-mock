package com.dyrmig.banking.controller.impl;

import com.dyrmig.banking.classes.AmountOfOperationDTO;
import com.dyrmig.banking.classes.MyCustomException;
import com.dyrmig.banking.classes.ThirdPartyOperationDTO;
import com.dyrmig.banking.controller.interfaces.ThirdPartyController;
import com.dyrmig.banking.model.ThirdParty;
import com.dyrmig.banking.repository.ThirdPartyRepository;
import com.dyrmig.banking.service.interfaces.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ThirdPartyControllerImpl implements ThirdPartyController {
    @Autowired
    private ThirdPartyService thirdPartyService;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @PostMapping("/thirdparty")
    @ResponseStatus(HttpStatus.CREATED)
    public ThirdParty saveThirdParty(@RequestBody ThirdParty thirdParty) {
        return thirdPartyService.saveThirdParty(thirdParty);
    }
    @PostMapping("/thirdparty/{accountId}/charge")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void charge(@PathVariable(name = "accountId") Long accountId, @RequestBody ThirdPartyOperationDTO thirdPartyOperationDTO, @RequestHeader("hashed-key") String hashedKey){
        thirdPartyService.charge(accountId, thirdPartyOperationDTO, hashedKey);
    }
    @PostMapping("/thirdparty/{accountId}/refund")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void refund(@PathVariable(name = "accountId") Long accountId, @RequestBody ThirdPartyOperationDTO thirdPartyOperationDTO, @RequestHeader("hashed-key") String hashedKey){
        thirdPartyService.refund(accountId, thirdPartyOperationDTO, hashedKey);
    }
    @DeleteMapping("/thirdparty/{thirdPartyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteThirdParty(@PathVariable(name = "thirdPartyId") Long thirdPartyId){
        thirdPartyService.deleteThirdParty(thirdPartyId);
    }
    @GetMapping("/thirdparty")
    @ResponseStatus(HttpStatus.OK)
    public List<ThirdParty> findAll(){
        return thirdPartyRepository.findAll();
    }
}
