package rs.banka4.user_service.runners;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import rs.banka4.user_service.domain.account.db.Account;
import rs.banka4.user_service.domain.account.db.AccountType;
import rs.banka4.user_service.domain.card.db.Card;
import rs.banka4.user_service.domain.card.db.CardName;
import rs.banka4.user_service.domain.card.db.CardStatus;
import rs.banka4.user_service.domain.card.db.CardType;
import rs.banka4.user_service.domain.company.db.ActivityCode;
import rs.banka4.user_service.domain.company.db.Company;
import rs.banka4.user_service.domain.currency.db.Currency;
import rs.banka4.user_service.domain.loan.db.Loan;
import rs.banka4.user_service.domain.loan.db.LoanStatus;
import rs.banka4.user_service.domain.loan.db.LoanType;
import rs.banka4.user_service.domain.transaction.db.MonetaryAmount;
import rs.banka4.user_service.domain.transaction.db.Transaction;
import rs.banka4.user_service.domain.transaction.db.TransactionStatus;
import rs.banka4.user_service.domain.user.Gender;
import rs.banka4.user_service.domain.user.Privilege;
import rs.banka4.user_service.domain.user.client.db.Client;
import rs.banka4.user_service.domain.user.client.db.ClientContact;
import rs.banka4.user_service.domain.user.employee.db.Employee;
import rs.banka4.user_service.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Profile({"dev"})
@Component
@RequiredArgsConstructor
public class TestDataRunner implements CommandLineRunner {
    private static final Logger LOGGER
            = LoggerFactory.getLogger(TestDataRunner.class);

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrencyRepository currencyRepository;
    private final AccountRepository accountRepository;
    private final ActivityCodeRepository activityCodeRepository;
    private final CompanyRepository companyRepository;
    private final ClientContactRepository clientContactRepository;
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;
    private final CardRepository cardRepository;

    @Override
    public void run(String... args) {
        clientSeeder();
        employeeSeeder();
        activityCodeSeeder();
        currencySeeder();
        clientContactsSeeder();
        companySeeder();
        accountSeeder();
        loanSeeder();
        transactionSeeder();
        cardSeeder();
    }

    private void cardSeeder() {

        if (accountRepository.count() == 0) {
            System.out.println("No accounts found. Skipping card seeder.");
            return;
        }

        Account account = accountRepository.findAccountByAccountNumber("1234567890").orElse(null);
        Client client = account.getClient();
        Currency currency = account.getCurrency();
        if (client == null || currency == null) {
            System.out.println("Client or Currency not found. Skipping card seeder.");
            return;
        }
        List<Card> cards = List.of(
                Card.builder()
                        .cardNumber("1234567810345678")
                        .cvv("123")
                        .cardName(CardName.VISA)
                        .cardType(CardType.DEBIT)
                        .account(account)
                        .cardStatus(CardStatus.ACTIVATED)
                        .limit(BigDecimal.valueOf(10000))
                        .createdAt(LocalDate.now())
                        .expiresAt(LocalDate.now().plusYears(5))
                        .build(),

                Card.builder()
                        .cardNumber("8765432107654321")
                        .cvv("321")
                        .cardName(CardName.MASTER_CARD)
                        .cardType(CardType.CREDIT)
                        .account(account)
                        .cardStatus(CardStatus.ACTIVATED)
                        .limit(BigDecimal.valueOf(10000))
                        .createdAt(LocalDate.now())
                        .expiresAt(LocalDate.now().plusYears(5))
                        .build()
        );
        List<Card> newCards = cards.stream()
                .filter(card -> !cardRepository.existsByCardNumber(card.getCardNumber()))
                .collect(Collectors.toList());
        cardRepository.saveAll(newCards);
    }


    private void loanSeeder() {
        long loanCount = loanRepository.count();

        if (loanCount > 10) {
            LOGGER.debug("Seeder skipped. There are already more than 10 loans in the database.");
            return;
        }

        Random random = new Random();
        List<Account> accounts = accountRepository.findAll();

        List<Loan> loans = random.ints(10, 0, 10000)
                .mapToObj(i -> Loan.builder()
                        .loanNumber(generateRandomLoanNumber())
                        .amount(generateRandomAmount())
                        .repaymentPeriod(random.nextInt(60) + 12)
                        .agreementDate(generateRandomDate())
                        .dueDate(generateRandomDate().plusMonths(random.nextInt(6) + 1))
                        .monthlyInstallment(generateRandomAmount())
                        .nextInstallmentDate(generateRandomDate().plusMonths(1))
                        .remainingDebt(generateRandomAmount())
                        .interestRate(generateRandomInterestRate())
                        .account(accounts.get(random.nextInt(accounts.size())))
                        .status(randomEnumValue(LoanStatus.class))
                        .type(randomEnumValue(LoanType.class))
                        .interestType(randomEnumValue(Loan.InterestType.class))
                        .build())
                .collect(Collectors.toList());

        loanRepository.saveAll(loans);
    }

    private Long generateRandomLoanNumber() {
        Random random = new Random();
        return (long) (random.nextInt(100000) + 100000);
    }

    private BigDecimal generateRandomAmount() {
        Random random = new Random();
        return BigDecimal.valueOf(random.nextInt(100000) + 1000);
    }

    private LocalDate generateRandomDate() {
        Random random = new Random();
        int year = random.nextInt(5) + 2020;
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1;
        return LocalDate.of(year, month, day);
    }

    private BigDecimal generateRandomInterestRate() {
        Random random = new Random();
        return BigDecimal.valueOf(random.nextInt(20) + 1);
    }

    private <T extends Enum<?>> T randomEnumValue(Class<T> clazz) {
        Random random = new Random();
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    private void clientContactsSeeder() {
        Random random = new Random();
        List<Client> clients = clientRepository.findAll();

        List<ClientContact> clientContacts = IntStream.range(0, 20)
                .mapToObj(i -> ClientContact.builder()
                        .client(clients.get(random.nextInt(clients.size())))
                        .accountNumber(generateRandomAccountNumber())
                        .nickname(generateRandomNickname())
                        .build())
                .toList();

        List<ClientContact> newClientContacts = clientContacts.stream()
                .filter(clientContact -> !clientContactRepository.existsByAccountNumber(clientContact.getAccountNumber()))
                .collect(Collectors.toList());

        clientContactRepository.saveAll(newClientContacts);
    }

    private String generateRandomAccountNumber() {
        Random random = new Random();
        return String.format("%012d", random.nextLong(100000000000L, 999999999999L));
    }

    private String generateRandomNickname() {
        String[] nicknames = {"JohnDoe", "JaneSmith", "MichaelJ", "EmilyD", "DavidW", "OliviaM", "JamesB", "SophiaG", "DanielM", "SarahL"};
        Random random = new Random();
        return nicknames[random.nextInt(nicknames.length)] + random.nextInt(100); // Append random number to nickname
    }

    private void activityCodeSeeder() {
        List<ActivityCode> activityCodes = List.of(
                ActivityCode.builder()
                        .code("1.11")
                        .sector("Poljoprivreda, šumarstvo i ribarstvo")
                        .branch("Uzgoj žitarica i mahunarki")
                        .build(),

                ActivityCode.builder()
                        .code("1.13")
                        .sector("Poljoprivreda, šumarstvo i ribarstvo")
                        .branch("Uzgoj povrća")
                        .build(),

                ActivityCode.builder()
                        .code("13.1")
                        .sector("Prerađivačka industrija")
                        .branch("Priprema i predenje tekstilnih vlakana")
                        .build(),

                ActivityCode.builder()
                        .code("24.1")
                        .sector("Prerađivačka industrija")
                        .branch("Proizvodnja gvožđa i čelika")
                        .build(),

                ActivityCode.builder()
                        .code("24.2")
                        .sector("Prerađivačka industrija")
                        .branch("Proizvodnja čeličnih cevi, šupljih profila i fitinga")
                        .build(),

                ActivityCode.builder()
                        .code("41.1")
                        .sector("Građevinarstvo")
                        .branch("Razvoj građevinskih projekata")
                        .build(),

                ActivityCode.builder()
                        .code("41.2")
                        .sector("Građevinarstvo")
                        .branch("Izgradnja stambenih i nestambenih zgrada")
                        .build(),

                ActivityCode.builder()
                        .code("42.11")
                        .sector("Građevinarstvo")
                        .branch("Izgradnja puteva i autoputeva")
                        .build(),

                ActivityCode.builder()
                        .code("42.12")
                        .sector("Građevinarstvo")
                        .branch("Izgradnja železničkih i podzemnih pruga")
                        .build(),

                ActivityCode.builder()
                        .code("42.13")
                        .sector("Građevinarstvo")
                        .branch("Izgradnja mostova i tunela")
                        .build(),

                ActivityCode.builder()
                        .code("42.21")
                        .sector("Građevinarstvo")
                        .branch("Izgradnja vodovodnih projekata")
                        .build(),

                ActivityCode.builder()
                        .code("42.22")
                        .sector("Građevinarstvo")
                        .branch("Izgradnja elektroenergetskih i telekomunikacionih mreža")
                        .build(),

                ActivityCode.builder()
                        .code("5.1")
                        .sector("Rudarstvo")
                        .branch("Vađenje uglja")
                        .build(),

                ActivityCode.builder()
                        .code("7.1")
                        .sector("Rudarstvo")
                        .branch("Vađenje gvozdenih ruda")
                        .build(),

                ActivityCode.builder()
                        .code("7.21")
                        .sector("Rudarstvo")
                        .branch("Vađenje uranijuma i torijuma")
                        .build(),

                ActivityCode.builder()
                        .code("8.11")
                        .sector("Rudarstvo")
                        .branch("Eksploatacija ukrasnog i građevinskog kamena")
                        .build(),

                ActivityCode.builder()
                        .code("8.92")
                        .sector("Rudarstvo")
                        .branch("Ekstrakcija treseta")
                        .build(),

                ActivityCode.builder()
                        .code("47.11")
                        .sector("Trgovina")
                        .branch("Trgovina u nespecijalizovanim prodavnicama sa hranom i pićem")
                        .build(),

                ActivityCode.builder()
                        .code("56.1")
                        .sector("Ugostiteljstvo")
                        .branch("Restorani i pokretni ugostiteljski objekti")
                        .build(),

                ActivityCode.builder()
                        .code("62.01")
                        .sector("IT")
                        .branch("Računarsko programiranje")
                        .build(),

                ActivityCode.builder()
                        .code("62.09")
                        .sector("IT")
                        .branch("Ostale IT usluge")
                        .build(),

                ActivityCode.builder()
                        .code("63.11")
                        .sector("IT")
                        .branch("Obrada podataka, hosting i slične delatnosti")
                        .build(),

                ActivityCode.builder()
                        .code("64.19")
                        .sector("Finansijske delatnosti")
                        .branch("Ostale monetarne posredničke delatnosti")
                        .build(),

                ActivityCode.builder()
                        .code("64.91")
                        .sector("Finansijske delatnosti")
                        .branch("Finansijski lizing")
                        .build(),

                ActivityCode.builder()
                        .code("64.2")
                        .sector("Finansijske delatnosti")
                        .branch("Holding kompanije")
                        .build(),

                ActivityCode.builder()
                        .code("66.3")
                        .sector("Finansijske delatnosti")
                        .branch("Fondovi i slične finansijske delatnosti")
                        .build(),

                ActivityCode.builder()
                        .code("65.2")
                        .sector("Osiguranje")
                        .branch("Reosiguranje")
                        .build(),

                ActivityCode.builder()
                        .code("65.11")
                        .sector("Osiguranje")
                        .branch("Životno osiguranje")
                        .build(),

                ActivityCode.builder()
                        .code("65.12")
                        .sector("Osiguranje")
                        .branch("Neživotno osiguranje")
                        .build(),

                ActivityCode.builder()
                        .code("66.21")
                        .sector("Osiguranje")
                        .branch("Procena rizika i štete")
                        .build(),

                ActivityCode.builder()
                        .code("68.1")
                        .sector("Poslovanje nekretninama")
                        .branch("Upravljanje nekretninama na osnovu naknade ili ugovora")
                        .build(),

                ActivityCode.builder()
                        .code("68.2")
                        .sector("Poslovanje nekretninama")
                        .branch("Izdavanje i upravljanje nekretninama u sopstvenom ili iznajmljenom vlasništvu")
                        .build(),

                ActivityCode.builder()
                        .code("53.1")
                        .sector("Saobraćaj i skladištenje")
                        .branch("Poštanske aktivnosti")
                        .build(),

                ActivityCode.builder()
                        .code("53.2")
                        .sector("Saobraćaj i skladištenje")
                        .branch("Kurirske aktivnosti")
                        .build(),

                ActivityCode.builder()
                        .code("85.1")
                        .sector("Obrazovanje")
                        .branch("Predškolsko obrazovanje")
                        .build(),

                ActivityCode.builder()
                        .code("85.2")
                        .sector("Obrazovanje")
                        .branch("Osnovno obrazovanje")
                        .build(),

                ActivityCode.builder()
                        .code("86.1")
                        .sector("Zdravstvena zaštita")
                        .branch("Bolničke aktivnosti")
                        .build(),

                ActivityCode.builder()
                        .code("86.21")
                        .sector("Zdravstvena zaštita")
                        .branch("Opšta medicinska praksa")
                        .build(),

                ActivityCode.builder()
                        .code("86.22")
                        .sector("Zdravstvena zaštita")
                        .branch("Specijalistička medicinska praksa")
                        .build(),

                ActivityCode.builder()
                        .code("86.9")
                        .sector("Zdravstvena zaštita")
                        .branch("Ostale aktivnosti zdravstvene zaštite")
                        .build(),

                ActivityCode.builder()
                        .code("84.12")
                        .sector("Javna uprava i odbrana")
                        .branch("Regulisanje delatnosti privrede")
                        .build(),

                ActivityCode.builder()
                        .code("90.01")
                        .sector("Kultura, sport i rekreacija")
                        .branch("Delatnost pozorišta")
                        .build(),

                ActivityCode.builder()
                        .code("90.02")
                        .sector("Kultura, sport i rekreacija")
                        .branch("Delatnost muzeja")
                        .build(),

                ActivityCode.builder()
                        .code("90.04")
                        .sector("Kultura, sport i rekreacija")
                        .branch("Delatnost botaničkih i zooloških vrtova")
                        .build(),

                ActivityCode.builder()
                        .code("93.11")
                        .sector("Sportske i rekreativne delatnosti")
                        .branch("Delovanje sportskih objekata")
                        .build(),

                ActivityCode.builder()
                        .code("93.13")
                        .sector("Sportske i rekreativne delatnosti")
                        .branch("Delovanje teretana")
                        .build(),

                ActivityCode.builder()
                        .code("93.19")
                        .sector("Sportske i rekreativne delatnosti")
                        .branch("Ostale sportske delatnosti")
                        .build(),

                ActivityCode.builder()
                        .code("26.11")
                        .sector("Proizvodnja elektronskih komponenti")
                        .branch("Proizvodnja elektronskih komponenti")
                        .build(),

                ActivityCode.builder()
                        .code("27.12")
                        .sector("Proizvodnja električne opreme")
                        .branch("Proizvodnja električnih panela i ploča")
                        .build(),

                ActivityCode.builder()
                        .code("29.1")
                        .sector("Proizvodnja motornih vozila")
                        .branch("Proizvodnja motornih vozila")
                        .build()
        );

        List<ActivityCode> newActivityCodes = activityCodes.stream()
                .filter(employee -> !activityCodeRepository.existsByCode(employee.getCode()))
                .collect(Collectors.toList());

        activityCodeRepository.saveAll(newActivityCodes);

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected void currencySeeder() {
        Set<Currency> currencies = Set.of(
                Currency.builder()
                        .name("Serbian Dinar")
                        .symbol("RSD")
                        .description("Serbian national currency")
                        .version(0L)
                        .active(true)
                        .code(Currency.Code.RSD)
                        .build(),
                Currency.builder()
                        .name("Euro")
                        .symbol("EUR")
                        .description("European Union currency")
                        .version(0L)
                        .active(true)
                        .code(Currency.Code.EUR)
                        .build(),
                Currency.builder()
                        .name("US Dollar")
                        .symbol("USD")
                        .description("United States currency")
                        .version(0L)
                        .active(true)
                        .code(Currency.Code.USD)
                        .build(),
                Currency.builder()
                        .name("Swiss Franc")
                        .symbol("CHF")
                        .description("Swiss national currency")
                        .version(0L)
                        .active(true)
                        .code(Currency.Code.CHF)
                        .build(),
                Currency.builder()
                        .name("Japanese Yen")
                        .symbol("JPY")
                        .description("Japanese national currency")
                        .version(0L)
                        .active(true)
                        .code(Currency.Code.JPY)
                        .build(),
                Currency.builder()
                        .name("Australian Dollar")
                        .symbol("AUD")
                        .description("Australian national currency")
                        .version(0L)
                        .active(true)
                        .code(Currency.Code.AUD)
                        .build(),
                Currency.builder()
                        .name("Canadian Dollar")
                        .symbol("CAD")
                        .description("Canadian national currency")
                        .version(0L)
                        .active(true)
                        .code(Currency.Code.CAD)
                        .build()
        );

        for (Currency currency : currencies) {
            if (currencyRepository.findByCode(currency.getCode()) == null) {
                currencyRepository.save(currency);
            }
        }
    }

    private void employeeSeeder() {
        List<Employee> employees = List.of(
                Employee.builder()
                        .firstName("Alice")
                        .lastName("Johnson")
                        .dateOfBirth(LocalDate.of(1985, 4, 12))
                        .gender(Gender.FEMALE)
                            .email("alice.johnson@bankcorp.com")
                        .phone("3810611111111")
                        .address("789 Sunset Blvd")
                        .password(passwordEncoder.encode("password"))
                        .username("alicej")
                        .position("Branch Manager")
                        .department("Branch Operations")
                        .active(true)
                        .enabled(true)
                        .build(),

                Employee.builder()
                        .firstName("Robert")
                        .lastName("Anderson")
                        .dateOfBirth(LocalDate.of(1990, 7, 21))
                        .gender(Gender.MALE)
                        .email("robert.anderson@bankcorp.com")
                        .phone("3810622222222")
                        .address("456 Greenway Rd")
                        .password(passwordEncoder.encode("password"))
                        .username("roberta")
                        .position("Loan Officer")
                        .department("Loans & Mortgages")
                        .active(true)
                        .enabled(true)
                        .build(),

                Employee.builder()
                        .firstName("Samantha")
                        .lastName("Miller")
                        .dateOfBirth(LocalDate.of(1988, 10, 5))
                        .gender(Gender.FEMALE)
                        .email("samantha.miller@bankcorp.com")
                        .phone("3810633333333")
                        .address("321 Oak Dr")
                        .password(passwordEncoder.encode("password"))
                        .username("samantham")
                        .position("Financial Analyst")
                        .department("Finance")
                        .active(true)
                        .enabled(true)
                        .build(),

                Employee.builder()
                        .firstName("Daniel")
                        .lastName("White")
                        .dateOfBirth(LocalDate.of(1993, 3, 15))
                        .gender(Gender.MALE)
                        .email("daniel.white@bankcorp.com")
                        .phone("3810644444444")
                        .address("567 Pine Ln")
                        .password(passwordEncoder.encode("password"))
                        .username("danielw")
                        .position("Investment Banker")
                        .department("Investment Banking")
                        .active(true)
                        .enabled(true)
                        .build(),

                Employee.builder()
                        .firstName("Jessica")
                        .lastName("Martinez")
                        .dateOfBirth(LocalDate.of(1991, 5, 30))
                        .gender(Gender.FEMALE)
                        .email("jessica.martinez@bankcorp.com")
                        .phone("3810655555555")
                        .address("123 Cedar Ave")
                        .password(passwordEncoder.encode("password"))
                        .username("jessicam")
                        .position("Compliance Officer")
                        .department("Regulatory Compliance")
                        .active(true)
                        .enabled(true)
                        .build(),

                Employee.builder()
                        .firstName("Michael")
                        .lastName("Thompson")
                        .dateOfBirth(LocalDate.of(1987, 8, 18))
                        .gender(Gender.MALE)
                        .email("michael.thompson@bankcorp.com")
                        .phone("3810666666666")
                        .address("456 Maple Rd")
                        .password(passwordEncoder.encode("password"))
                        .username("michaelt")
                        .position("Risk Manager")
                        .department("Risk Management")
                        .active(true)
                        .enabled(true)
                        .build(),

                Employee.builder()
                        .firstName("Laura")
                        .lastName("Harris")
                        .dateOfBirth(LocalDate.of(1994, 12, 22))
                        .gender(Gender.FEMALE)
                        .email("laura.harris@bankcorp.com")
                        .phone("3810677777777")
                        .address("789 Elm St")
                        .password(passwordEncoder.encode("password"))
                        .username("laurah")
                        .position("Customer Service Representative")
                        .department("Customer Support")
                        .active(true)
                        .enabled(true)
                        .build(),

                Employee.builder()
                        .firstName("David")
                        .lastName("Clark")
                        .dateOfBirth(LocalDate.of(1989, 6, 9))
                        .gender(Gender.MALE)
                        .email("david.clark@bankcorp.com")
                        .phone("3810688888888")
                        .address("987 Walnut St")
                        .password(passwordEncoder.encode("password"))
                        .username("davidc")
                        .position("Credit Analyst")
                        .department("Credit & Lending")
                        .active(true)
                        .enabled(true)
                        .build(),

                Employee.builder()
                        .firstName("Emma")
                        .lastName("Lewis")
                        .dateOfBirth(LocalDate.of(1996, 2, 14))
                        .gender(Gender.FEMALE)
                        .email("emma.lewis@bankcorp.com")
                        .phone("3810699999999")
                        .address("654 Birch Rd")
                        .password(passwordEncoder.encode("password"))
                        .username("emmal")
                        .position("Treasury Analyst")
                        .department("Treasury")
                        .active(true)
                        .enabled(true)
                        .build(),

                Employee.builder()
                        .firstName("Chris")
                        .lastName("Walker")
                        .dateOfBirth(LocalDate.of(1986, 11, 3))
                        .gender(Gender.MALE)
                        .email("chris.walker@bankcorp.com")
                        .phone("3810700000000")
                        .address("321 Redwood Ave")
                        .password(passwordEncoder.encode("password"))
                        .username("chrisw")
                        .position("Bank Teller")
                        .department("Retail Banking")
                        .active(true)
                        .enabled(true)
                        .build()
        );

        List<Employee> newEmployees = employees.stream()
                .filter(employee -> !employeeRepository.existsByEmail(employee.getEmail()))
                .collect(Collectors.toList());

        newEmployees.forEach(employee -> {
            switch (employee.getPosition()) {
                case "Branch Manager", "Investment Banker" ->
                        employee.setPrivileges(Arrays.asList(Privilege.ADMIN, Privilege.TRADE_STOCKS, Privilege.VIEW_STOCKS));
                case "Compliance Officer", "Risk Manager" ->
                        employee.setPrivileges(Arrays.asList(Privilege.CONTRACTS, Privilege.FILTER));
                case "Loan Officer", "Credit Analyst" ->
                        employee.setPrivileges(Arrays.asList(Privilege.SEARCH, Privilege.VIEW_STOCKS));
                default -> employee.setPrivileges(List.of(Privilege.VIEW_STOCKS));
            }
        });

        if (!newEmployees.isEmpty()) {
            employeeRepository.saveAll(newEmployees);
        }
    }

    private void clientSeeder() {
        List<Client> clients = List.of(
                Client.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .dateOfBirth(LocalDate.of(1995, 5, 15))
                        .gender(Gender.MALE)
                        .email("johndoe95@example.com")
                        .phone("3810612345678")
                        .address("123 Main St")
                        .password(passwordEncoder.encode("password"))
                        .accounts(Set.of())
                        .enabled(true)
                        .build(),

                Client.builder()
                        .firstName("Jane")
                        .lastName("Smith")
                        .dateOfBirth(LocalDate.of(1992, 8, 25))
                        .gender(Gender.FEMALE)
                        .email("janesmith92@example.com")
                        .phone("3810629876543")
                        .address("789 Oak St")
                        .password(passwordEncoder.encode("password"))
                        .accounts(Set.of())
                        .enabled(true)
                        .build(),

                Client.builder()
                        .firstName("Michael")
                        .lastName("Johnson")
                        .dateOfBirth(LocalDate.of(1988, 3, 10))
                        .gender(Gender.MALE)
                        .email("michaelj@example.com")
                        .phone("3810634455667")
                        .address("567 Pine St")
                        .password(passwordEncoder.encode("password"))
                        .accounts(Set.of())
                        .enabled(true)
                        .build(),

                Client.builder()
                        .firstName("Emily")
                        .lastName("Davis")
                        .dateOfBirth(LocalDate.of(1990, 11, 30))
                        .gender(Gender.FEMALE)
                        .email("emilyd@example.com")
                        .phone("3810641122334")
                        .address("234 Maple St")
                        .password(passwordEncoder.encode("password"))
                        .accounts(Set.of())
                        .enabled(true)
                        .build(),

                Client.builder()
                        .firstName("David")
                        .lastName("Wilson")
                        .dateOfBirth(LocalDate.of(1985, 6, 20))
                        .gender(Gender.MALE)
                        .email("davidw@example.com")
                        .phone("3810656677889")
                        .address("890 Cedar St")
                        .password(passwordEncoder.encode("password"))
                        .accounts(Set.of())
                        .enabled(true)
                        .build(),

                Client.builder()
                        .firstName("Olivia")
                        .lastName("Martinez")
                        .dateOfBirth(LocalDate.of(1993, 9, 5))
                        .gender(Gender.FEMALE)
                        .email("oliviam@example.com")
                        .phone("3810667788990")
                        .address("321 Birch St")
                        .password(passwordEncoder.encode("password"))
                        .accounts(Set.of())
                        .enabled(true)
                        .build(),

                Client.builder()
                        .firstName("James")
                        .lastName("Brown")
                        .dateOfBirth(LocalDate.of(1987, 4, 18))
                        .gender(Gender.MALE)
                        .email("jamesb@example.com")
                        .phone("3810678899001")
                        .address("654 Willow St")
                        .password(passwordEncoder.encode("password"))
                        .accounts(Set.of())
                        .enabled(true)
                        .build(),

                Client.builder()
                        .firstName("Sophia")
                        .lastName("Garcia")
                        .dateOfBirth(LocalDate.of(1996, 12, 12))
                        .gender(Gender.FEMALE)
                        .email("sophiag@example.com")
                        .phone("3810689900112")
                        .address("987 Palm St")
                        .password(passwordEncoder.encode("password"))
                        .accounts(Set.of())
                        .enabled(true)
                        .build(),

                Client.builder()
                        .firstName("Daniel")
                        .lastName("Miller")
                        .dateOfBirth(LocalDate.of(1994, 7, 22))
                        .gender(Gender.MALE)
                        .email("danielm@example.com")
                        .phone("3810690011223")
                        .address("741 Redwood St")
                        .password(passwordEncoder.encode("password"))
                        .accounts(Set.of())
                        .enabled(true)
                        .build()
        );

        List<Client> newClients = clients.stream()
                .filter(client -> !clientRepository.existsByEmail(client.getEmail()))
                .collect(Collectors.toList());

        if (!newClients.isEmpty()) {
            clientRepository.saveAll(newClients);
        }
    }

    private void companySeeder() {
        if (companyRepository.count() == 0) {
            Client majorityOwner = clientRepository.findByEmail("danielm@example.com").orElse(null);
            ActivityCode activityCode = activityCodeRepository.findActivityCodeByCode("62.01").orElse(null);

            if (majorityOwner != null && activityCode != null) {
                Company company = Company.builder()
                        .name("Some Company")
                        .tin("123456789")
                        .crn("987654321")
                        .address("789 Oak St")
                        .activityCode(activityCode)
                        .majorityOwner(majorityOwner)
                        .build();

                companyRepository.save(company);
            }
        }
    }

    private void accountSeeder() {
        if (accountRepository.count() == 0) {
            Client client = clientRepository.findByEmail("danielm@example.com").orElse(null);
            Company company = companyRepository.findByName(("Some Company")).orElse(null);
            Currency currency = currencyRepository.findByCode(Currency.Code.RSD);

            if (client != null && currency != null) {
                Account personalAccount = Account.builder()
                        .accountNumber("1234567890")
                        .balance(new BigDecimal("1000.00"))
                        .availableBalance(new BigDecimal("1000.00"))
                        .accountMaintenance(new BigDecimal("10.00"))
                        .createdDate(LocalDate.now())
                        .expirationDate(LocalDate.now().plusYears(5))
                        .active(true)
                        .accountType(AccountType.STANDARD)
                        .dailyLimit(new BigDecimal("500.00"))
                        .monthlyLimit(new BigDecimal("5000.00"))
                        .client(client)
                        .currency(currency)
                        .build();

                accountRepository.save(personalAccount);
            }

            if (company != null && currency != null) {
                Account businessAccount = Account.builder()
                        .accountNumber("0987654321")
                        .balance(new BigDecimal("5000.00"))
                        .availableBalance(new BigDecimal("5000.00"))
                        .accountMaintenance(new BigDecimal("50.00"))
                        .createdDate(LocalDate.now())
                        .expirationDate(LocalDate.now().plusYears(5))
                        .active(true)
                        .accountType(AccountType.DOO)
                        .dailyLimit(new BigDecimal("1000.00"))
                        .monthlyLimit(new BigDecimal("10000.00"))
                        .client(client)
                        .company(company)
                        .currency(currency)
                        .build();

                accountRepository.save(businessAccount);
            }
        }
    }

    private void transactionSeeder() {
        if (transactionRepository.count() == 0) {
            Account fromAccount = accountRepository.findAccountByAccountNumber("1234567890").orElse(null);
            Account toAccount = accountRepository.findAccountByAccountNumber("0987654321").orElse(null);
            Currency currency = currencyRepository.findByCode(Currency.Code.RSD);

            if (fromAccount != null && toAccount != null && currency != null) {
                Transaction transaction1 = Transaction.builder()
                        .transactionNumber("587135813355381")
                        .fromAccount(fromAccount)
                        .toAccount(toAccount)
                        .from(new MonetaryAmount(new BigDecimal("100.00"), currency))
                        .to(new MonetaryAmount(new BigDecimal("100.00"), currency))
                        .fee(new MonetaryAmount(new BigDecimal("1.00"), currency))
                        .recipient("Recipient Name")
                        .paymentCode("123")
                        .referenceNumber("123456")
                        .paymentPurpose("Payment for services")
                        .paymentDateTime(LocalDateTime.now())
                        .status(TransactionStatus.IN_PROGRESS)
                        .build();

                Transaction transaction2 = Transaction.builder()
                        .transactionNumber("583135413351381")
                        .fromAccount(toAccount)
                        .toAccount(fromAccount)
                        .from(new MonetaryAmount(new BigDecimal("200.00"), currency))
                        .to(new MonetaryAmount(new BigDecimal("200.00"), currency))
                        .fee(new MonetaryAmount(new BigDecimal("2.00"), currency))
                        .recipient("Another Recipient")
                        .paymentCode("456")
                        .referenceNumber("654321")
                        .paymentPurpose("Payment for goods")
                        .paymentDateTime(LocalDateTime.now().minusDays(1))
                        .status(TransactionStatus.REALIZED)
                        .build();

                transactionRepository.saveAll(Set.of(transaction1, transaction2));
            }
        }
    }

}