package com.elite.rideplatform.wallet;

import com.elite.rideplatform.exception.InsufficientWalletBalanceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional(readOnly = true)
    public Wallet getWalletByPartnerId(UUID partnerId) {
        return walletRepository.findByPartnerId(partnerId).orElse(null);
    }

    @Transactional
    public void creditWallet(UUID partnerId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByPartnerId(partnerId).orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
        // Transaction logic will be expanded in Phase 3/4
    }

    @Transactional
    public void withdraw(UUID partnerId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByPartnerId(partnerId).orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientWalletBalanceException("Insufficient funds. Available: " + wallet.getBalance());
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
        // Transaction logic will be expanded in Phase 3/4
    }
}
