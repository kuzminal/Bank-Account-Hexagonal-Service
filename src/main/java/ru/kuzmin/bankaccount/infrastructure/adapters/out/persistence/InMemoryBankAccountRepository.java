package ru.kuzmin.bankaccount.infrastructure.adapters.out.persistence;

import org.springframework.stereotype.Repository;
import ru.kuzmin.bankaccount.domain.models.entity.BankAccount;
import ru.kuzmin.bankaccount.domain.ports.out.BankAccountRepositoryPort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryBankAccountRepository implements BankAccountRepositoryPort {

    private final Map<Long, BankAccount> accounts = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public BankAccount save(BankAccount account) {
        if (account.getId() == null) {
            account = new BankAccount(idGenerator.getAndIncrement(),
                    account.getOwner(),
                    account.getBalance());
        }
        accounts.put(account.getId(), account);
        return account;
    }

    @Override
    public Optional<BankAccount> findById(Long id) {
        return Optional.ofNullable(accounts.get(id));
    }
}
