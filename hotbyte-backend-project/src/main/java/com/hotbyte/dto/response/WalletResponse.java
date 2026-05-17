package com.hotbyte.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class WalletResponse {
    private BigDecimal balance;
    private List<WalletTransactionResponse> transactions;
}