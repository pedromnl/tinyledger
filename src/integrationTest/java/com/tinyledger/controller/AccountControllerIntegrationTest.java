package com.tinyledger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinyledger.dto.account.CreateAccountRequestDto;
import com.tinyledger.dto.transaction.DepositRequest;
import com.tinyledger.dto.transaction.WithdrawalRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createAccount_validRequest_shouldCreateAccountAndReturnIt() throws Exception {
        CreateAccountRequestDto request = new CreateAccountRequestDto("Checking Account");

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountName").value("Checking Account"))
                .andExpect(jsonPath("$.accountId").isNotEmpty());
    }

    @Test
    void createAccount_accountNameShorterThanThreeCharacters_shouldReturnBadRequest() throws Exception {
        CreateAccountRequestDto request = new CreateAccountRequestDto("Ab");

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.accountName").value("Account name must contain at least 3 characters"));
    }

    @Test
    void getAccountById_accountExists_shouldReturnAccountDetails() throws Exception {
        CreateAccountRequestDto request = new CreateAccountRequestDto("Test Account");
        String createResponse = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID accountId = UUID.fromString(objectMapper.readTree(createResponse).get("accountId").asText());

        mockMvc.perform(get("/api/v1/accounts/{accountId}", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountName").value("Test Account"));
    }

    @Test
    void getAccountById_accountDoesNotExist_shouldReturnNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/accounts/{accountId}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getAccountBalance_accountExists_shouldReturnBalance() throws Exception {
        CreateAccountRequestDto request = new CreateAccountRequestDto("Balance Test");
        String createResponse = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID accountId = UUID.fromString(objectMapper.readTree(createResponse).get("accountId").asText());

        mockMvc.perform(get("/api/v1/accounts/{accountId}/balance", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    void deposit_accountExists_shouldIncreaseBalanceAndReturnTransaction() throws Exception {
        CreateAccountRequestDto request = new CreateAccountRequestDto("Deposit Test");
        String createResponse = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID accountId = UUID.fromString(objectMapper.readTree(createResponse).get("accountId").asText());

        DepositRequest depositRequest = new DepositRequest(BigDecimal.valueOf(500));
        mockMvc.perform(post("/api/v1/accounts/{accountId}/transactions/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionType").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(500));

        mockMvc.perform(get("/api/v1/accounts/{accountId}/balance", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("500"));
    }

    @Test
    void deposit_negativeAmount_shouldReturnBadRequest() throws Exception {
        CreateAccountRequestDto createRequest = new CreateAccountRequestDto("Valid Account");
        String createResponse = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID accountId = UUID.fromString(objectMapper.readTree(createResponse).get("accountId").asText());

        DepositRequest request = new DepositRequest(BigDecimal.valueOf(-50));

        mockMvc.perform(post("/api/v1/accounts/{accountId}/transactions/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.amount").value("Amount must be greater than zero"));
    }

    @Test
    void deposit_accountDoesNotExist_shouldReturnNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        DepositRequest request = new DepositRequest(BigDecimal.valueOf(100));

        mockMvc.perform(post("/api/v1/accounts/{accountId}/transactions/deposit", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void concurrentWithdrawals_shouldMaintainCorrectBalanceAndTransactionHistory() throws Exception {
        CreateAccountRequestDto request = new CreateAccountRequestDto("Concurrent Withdrawal Test");

        String createResponse = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID accountId = UUID.fromString(objectMapper.readTree(createResponse).get("accountId").asText());

        // Deposit an initial amount of 500 to allow for withdrawals
        DepositRequest depositRequest = new DepositRequest(BigDecimal.valueOf(500));

        mockMvc.perform(post("/api/v1/accounts/{accountId}/transactions/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk());

        // Simulate 11 concurrent withdrawal requests of 50 each.
        int numberOfWithdrawals = 11;
        BigDecimal withdrawalAmount = BigDecimal.valueOf(50);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Integer>> futures = new ArrayList<>();

            for (int i = 0; i < numberOfWithdrawals; i++) {
                futures.add(executor.submit(() -> {
                    WithdrawalRequest withdrawalRequest =
                            new WithdrawalRequest(withdrawalAmount);

                    return mockMvc.perform(
                                    post(
                                            "/api/v1/accounts/{accountId}/transactions/withdrawal",
                                            accountId
                                    )
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(withdrawalRequest)))
                            .andReturn()
                            .getResponse()
                            .getStatus();
                }));
            }

            List<Integer> statuses = new ArrayList<>();

            for (Future<Integer> future : futures) {
                statuses.add(future.get());
            }

            // expect 10 successful withdrawals (200) and 1 failed withdrawal due to insufficient funds (409)
            assertThat(statuses)
                    .containsExactlyInAnyOrder(
                            200, 200, 200, 200, 200,
                            200, 200, 200, 200, 200,
                            409
                    );
        }

        // After all withdrawals, the balance should be 0
        mockMvc.perform(
                        get("/api/v1/accounts/{accountId}/balance", accountId))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

        // The transaction history should contain 11 transactions (1 deposit and 10 successful withdrawals)
        mockMvc.perform(get("/api/v1/accounts/{accountId}/transactions", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(11));
    }

    @Test
    void withdrawal_accountExistsAndSufficientFunds_shouldDecreaseBalanceAndReturnTransaction() throws Exception {
        CreateAccountRequestDto request = new CreateAccountRequestDto("Withdrawal Test");
        String createResponse = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID accountId = UUID.fromString(objectMapper.readTree(createResponse).get("accountId").asText());

        // Deposit first to have funds available for withdrawal
        DepositRequest depositRequest = new DepositRequest(BigDecimal.valueOf(1000));
        mockMvc.perform(post("/api/v1/accounts/{accountId}/transactions/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk());

        WithdrawalRequest withdrawalRequest = new WithdrawalRequest(BigDecimal.valueOf(300));
        mockMvc.perform(post("/api/v1/accounts/{accountId}/transactions/withdrawal", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionType").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.amount").value(300));

        mockMvc.perform(get("/api/v1/accounts/{accountId}/balance", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("700"));
    }

    @Test
    void withdrawal_accountExistsButInsufficientFunds_shouldReturnConflict() throws Exception {
        CreateAccountRequestDto request = new CreateAccountRequestDto("Insufficient Funds Test");
        String createResponse = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID accountId = UUID.fromString(objectMapper.readTree(createResponse).get("accountId").asText());

        // Attempt withdrawal with insufficient balance
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest(BigDecimal.valueOf(500));
        mockMvc.perform(post("/api/v1/accounts/{accountId}/transactions/withdrawal", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void withdrawal_negativeAmount_shouldReturnBadRequest() throws Exception {
        CreateAccountRequestDto createRequest = new CreateAccountRequestDto("Valid Account");
        String createResponse = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID accountId = UUID.fromString(objectMapper.readTree(createResponse).get("accountId").asText());

        WithdrawalRequest request = new WithdrawalRequest(BigDecimal.valueOf(-50));

        mockMvc.perform(post("/api/v1/accounts/{accountId}/transactions/withdrawal", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.amount").value("Amount must be greater than zero"));
    }

    @Test
    void withdrawal_accountDoesNotExist_shouldReturnNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        WithdrawalRequest request = new WithdrawalRequest(BigDecimal.valueOf(100));

        mockMvc.perform(post("/api/v1/accounts/{accountId}/transactions/withdrawal", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAccountTransactions_accountExists_shouldReturnTransactionHistoryOrderedByLatest() throws Exception {
        CreateAccountRequestDto request = new CreateAccountRequestDto("Transaction History Test");
        String createResponse = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID accountId = UUID.fromString(objectMapper.readTree(createResponse).get("accountId").asText());

        DepositRequest deposit1 = new DepositRequest(BigDecimal.valueOf(1000));
        mockMvc.perform(post("/api/v1/accounts/{accountId}/transactions/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deposit1)))
                .andExpect(status().isOk());

        DepositRequest deposit2 = new DepositRequest(BigDecimal.valueOf(500));
        mockMvc.perform(post("/api/v1/accounts/{accountId}/transactions/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deposit2)))
                .andExpect(status().isOk());

        WithdrawalRequest withdrawal = new WithdrawalRequest(BigDecimal.valueOf(200));
        mockMvc.perform(post("/api/v1/accounts/{accountId}/transactions/withdrawal", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawal)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/accounts/{accountId}/transactions", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].transactionType").value("WITHDRAWAL"))
                .andExpect(jsonPath("$[1].transactionType").value("DEPOSIT"))
                .andExpect(jsonPath("$[2].transactionType").value("DEPOSIT"));
    }

    @Test
    void getAccountTransactions_accountDoesNotExist_shouldReturnNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/accounts/{accountId}/transactions", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void endToEndAccountLifecycle_shouldManageAccountCorrectly() throws Exception {
        CreateAccountRequestDto createRequest = new CreateAccountRequestDto("Lifecycle Test Account");
        String createResponse = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID accountId = UUID.fromString(objectMapper.readTree(createResponse).get("accountId").asText());

        mockMvc.perform(get("/api/v1/accounts/{accountId}/balance", accountId))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

        DepositRequest deposit = new DepositRequest(BigDecimal.valueOf(1000));
        mockMvc.perform(post("/api/v1/accounts/{accountId}/transactions/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deposit)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/accounts/{accountId}/balance", accountId))
                .andExpect(status().isOk())
                .andExpect(content().string("1000"));

        WithdrawalRequest withdrawal = new WithdrawalRequest(BigDecimal.valueOf(250));
        mockMvc.perform(post("/api/v1/accounts/{accountId}/transactions/withdrawal", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawal)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/accounts/{accountId}/balance", accountId))
                .andExpect(status().isOk())
                .andExpect(content().string("750"));

        mockMvc.perform(get("/api/v1/accounts/{accountId}/transactions", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/api/v1/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountName").value("Lifecycle Test Account"));
    }
}
