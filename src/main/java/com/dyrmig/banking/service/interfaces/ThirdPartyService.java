package com.dyrmig.banking.service.interfaces;

import com.dyrmig.banking.classes.AmountOfOperationDTO;
import com.dyrmig.banking.model.ThirdParty;

public interface ThirdPartyService {
    void saveThirdParty(ThirdParty thirdParty);
    void charge(Long accountId, AmountOfOperationDTO amountOfOperationDTO, String hashedKey);
    void refund(Long accountId, AmountOfOperationDTO amountOfOperationDTO, String hashedKey);
}
