package ru.kuzmin.bankaccount.infrastructure.adapters.out.persistence;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.kuzmin.bankaccount.domain.models.entity.BankAccount;
import ru.kuzmin.bankaccount.domain.ports.out.BankAccountRepositoryPort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-Memory реализация порта — включается профилем {@code in-memory}.
 * Упрощённый адаптер для быстрого запуска: оптимистичная блокировка (version)
 * здесь не реализуется — она работает в JPA-адаптере.
 */
@Repository
@Profile("in-memory")
public class InMemoryBankAccountRepository implements BankAccountRepositoryPort {

    private final Map<Long, BankAccount> accounts = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public BankAccount save(BankAccount account) {
        if (account.getId() == null) {
            account = new BankAccount(idGenerator.getAndIncrement(),
                    account.getOwner(),
                    account.getBalance(),
                    account.getVersion());
        }
        accounts.put(account.getId(), account);
        return account;
    }

    @Override
    public Optional<BankAccount> findById(Long id) {
        return Optional.ofNullable(accounts.get(id));
    }
}
