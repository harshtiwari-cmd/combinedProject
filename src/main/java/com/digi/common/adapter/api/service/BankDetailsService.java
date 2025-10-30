package com.digi.common.adapter.api.service;


import com.digi.common.domain.model.dto.BankDetailsResponseDto;

import java.io.IOException;

public interface BankDetailsService {

    BankDetailsResponseDto getBankDetails(String lang) throws IOException;

}
