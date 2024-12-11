package com.demo.accounts.service.impl;

import com.demo.accounts.constants.AccountsConstants;
import com.demo.accounts.dto.AccountsDto;
import com.demo.accounts.dto.CustomerDto;
import com.demo.accounts.entity.Accounts;
import com.demo.accounts.entity.Customer;
import com.demo.accounts.exception.CustomerAlreadyExistsException;
import com.demo.accounts.exception.ResourceNotFoundException;
import com.demo.accounts.mapper.AccountsMapper;
import com.demo.accounts.mapper.CustomerMapper;
import com.demo.accounts.repository.AccountsRepository;
import com.demo.accounts.repository.CustomerRepository;
import com.demo.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {

    private AccountsRepository accountsRepository;

    private CustomerRepository customerRepository;

    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(customerDto.getMobileNumber());
        if (optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with given mobile number : " + customerDto.getMobileNumber());
        }
        customer.setCreatedBy("Misc");
        customer.setCreatedAt(LocalDateTime.now());
        Customer savedCustomer = customerRepository.save(customer);
        accountsRepository.save(createNewAccount(savedCustomer));
    }

    private Accounts createNewAccount(Customer customer) {
        Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);
        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        newAccount.setCreatedBy("Misc");
        newAccount.setCreatedAt(LocalDateTime.now());
        return newAccount;
    }

    @Override
    public CustomerDto getCustomerDetails(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "Mobile Number", mobileNumber)
        );
        Accounts accountDetails = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "Customer Id", customer.getCustomerId().toString())
        );
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(new AccountsDto(), accountDetails));
        return customerDto;
    }

}
