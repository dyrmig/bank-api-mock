package com.dyrmig.banking.controller.interfaces;

import com.dyrmig.banking.classes.AmountOfOperationDTO;
import com.dyrmig.banking.classes.ThirdPartyOperationDTO;
import com.dyrmig.banking.model.ThirdParty;

import java.util.List;

public interface ThirdPartyController {
    ThirdParty saveThirdParty(ThirdParty thirdParty);
    void charge(Long accountId, ThirdPartyOperationDTO thirdPartyOperationDTO, String hashedKey);
    void refund(Long accountId, ThirdPartyOperationDTO thirdPartyOperationDTO, String hashedKey);
    void deleteThirdParty(Long thirdPartyId);
    List<ThirdParty> findAll();
}
