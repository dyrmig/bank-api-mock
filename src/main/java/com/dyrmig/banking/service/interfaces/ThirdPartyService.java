package com.dyrmig.banking.service.interfaces;

import com.dyrmig.banking.classes.AmountOfOperationDTO;
import com.dyrmig.banking.classes.ThirdPartyOperationDTO;
import com.dyrmig.banking.model.ThirdParty;

public interface ThirdPartyService {
    ThirdParty saveThirdParty(ThirdParty thirdParty);
    void charge(Long accountId, ThirdPartyOperationDTO thirdPartyOperationDTO, String hashedKey);
    void refund(Long accountId, ThirdPartyOperationDTO thirdPartyOperationDTO, String hashedKey);
    void deleteThirdParty(Long thirdPartyId);
}
