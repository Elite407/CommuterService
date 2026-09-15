package com.elite.rideplatform.wallet;

import com.elite.rideplatform.common.BaseEntity;
import com.elite.rideplatform.partner.Partner;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rp_wallets")
public class Wallet extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false, unique = true)
    private Partner partner;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Version
    @Column(name = "version")
    private Integer version;

    public Wallet() {}

    public Wallet(Partner partner, BigDecimal balance) {
        this.partner = partner;
        this.balance = balance;
    }

    public Partner getPartner() { return partner; }
    public void setPartner(Partner partner) { this.partner = partner; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
