package com.demo.accounts.controller;

import com.demo.accounts.constants.AccountsConstants;
import com.demo.accounts.dto.CustomerDto;
import com.demo.accounts.dto.ResponseDto;
import com.demo.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
public class AccountsController {

    private IAccountsService accountsService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@RequestBody CustomerDto customerDto) {
        accountsService.createAccount(customerDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(AccountsConstants.STATUS_201, AccountsConstants.MESSAGE_201));
    }

    @GetMapping("/fetch")
    public ResponseEntity<CustomerDto> getResourceDetails(@RequestParam String mobileNumber) {
        CustomerDto customerDetails = accountsService.getCustomerDetails(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(customerDetails);
    }

}
