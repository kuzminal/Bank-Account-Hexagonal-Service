package ru.kuzmin.bankaccount.infrastructure.adapters.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.kuzmin.bankaccount.domain.models.entity.BankAccount;

import java.math.BigDecimal;

/**
 * JPA-сущность персистентности. Живёт в infrastructure и не попадает в домен.
 * {@code @Version} включает оптимистичную блокировку против гонок.
 */
@Entity
@Table(name = "bank_account")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountJpaEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String owner;

    private BigDecimal balance;

    @Version
    private Long version;

    public static BankAccountJpaEntity fromDomain(BankAccount account) {
        return new BankAccountJpaEntity(account.getId(), account.getOwner(), account.getBalance(), account.getVersion());
    }

    public BankAccount toDomain() {
        return new BankAccount(id, owner, balance, version);
    }
}
