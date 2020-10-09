/*
 * Copyright (c) 2020-2020. AxonIQ
 *
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;)
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.axoniq.demo.hotel.booking.query;

import io.axoniq.demo.hotel.booking.command.api.AccountRegisteredEvent;
import io.axoniq.demo.hotel.booking.query.api.AccountResponseData;
import io.axoniq.demo.hotel.booking.query.api.FindAccount;
import io.axoniq.demo.hotel.booking.query.api.FindAccounts;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ProcessingGroup("account")
class AccountHandler {

    private final AccountEntityRepository accountEntityRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;


    private AccountResponseData convert(AccountEntity entity) {
        return new AccountResponseData(entity.getAccountId(), entity.getUserName(), entity.getPassword());
    }

    AccountHandler(AccountEntityRepository accountEntityRepository, QueryUpdateEmitter queryUpdateEmitter) {
        this.accountEntityRepository = accountEntityRepository;
        this.queryUpdateEmitter = queryUpdateEmitter;
    }

    @EventHandler
    void on(AccountRegisteredEvent event) {
        AccountEntity entity = new AccountEntity(event.getAccountId().toString(), event.getUserName(), event.getPassword());
        this.accountEntityRepository.save(entity);
        /* sending it to subscription queries of type FindAccount, but only if the account id matches. */
        queryUpdateEmitter.emit(FindAccount.class,
                                query -> query.getAccountId().equals(event.getAccountId()),
                                convert(entity));
        /* sending it to subscription queries of type FindAccounts, every time. */
        queryUpdateEmitter.emit(FindAccounts.class,
                                query -> true,
                                convert(entity));
    }

    @QueryHandler
    AccountResponseData handle(FindAccount query) {
        return convert(accountEntityRepository.findById(query.getAccountId().toString()).orElseThrow(() -> new InvalidParameterException(query.getAccountId().toString())));
    }

    @QueryHandler
    List<AccountResponseData> handle(FindAccounts query) {
        return accountEntityRepository.findAll().stream().map(this::convert).collect(Collectors.toList());
    }
}
