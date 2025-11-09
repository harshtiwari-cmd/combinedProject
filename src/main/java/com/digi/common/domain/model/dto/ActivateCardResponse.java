package com.digi.common.domain.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivateCardResponse {

   private String cardActive;
}
