package com.codecool.seasonalproductdiscounter;

import com.codecool.seasonalproductdiscounter.model.products.Product;
import com.codecool.seasonalproductdiscounter.model.transactions.Transaction;
import com.codecool.seasonalproductdiscounter.model.transactions.TransactionsSimulatorSettings;
import com.codecool.seasonalproductdiscounter.model.users.User;
import com.codecool.seasonalproductdiscounter.service.authentication.AuthenticationService;
import com.codecool.seasonalproductdiscounter.service.authentication.AuthenticationServiceImpl;
import com.codecool.seasonalproductdiscounter.service.discounts.DiscountProvider;
import com.codecool.seasonalproductdiscounter.service.discounts.DiscountProviderImpl;
import com.codecool.seasonalproductdiscounter.service.discounts.DiscountService;
import com.codecool.seasonalproductdiscounter.service.discounts.DiscountServiceImpl;
import com.codecool.seasonalproductdiscounter.service.logger.ConsoleLogger;
import com.codecool.seasonalproductdiscounter.service.logger.Logger;
import com.codecool.seasonalproductdiscounter.service.persistence.DatabaseManager;
import com.codecool.seasonalproductdiscounter.service.persistence.DatabaseManagerImpl;
import com.codecool.seasonalproductdiscounter.service.persistence.SqliteConnector;
import com.codecool.seasonalproductdiscounter.service.products.provider.RandomProductGenerator;
import com.codecool.seasonalproductdiscounter.service.products.repository.ProductRepository;
import com.codecool.seasonalproductdiscounter.service.products.repository.ProductRepositoryImpl;
import com.codecool.seasonalproductdiscounter.service.transactions.repository.TransactionRepository;
import com.codecool.seasonalproductdiscounter.service.transactions.repository.TransactionRepositoryImpl;
import com.codecool.seasonalproductdiscounter.service.transactions.simulator.TransactionsSimulator;
import com.codecool.seasonalproductdiscounter.service.users.UserRepository;
import com.codecool.seasonalproductdiscounter.service.users.UserRepositoryImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {

        Logger logger = new ConsoleLogger();

        String dbFile = "src/main/resources/SeasonalProductDiscounter.db";
        SqliteConnector sqliteConnector = new SqliteConnector(dbFile, logger);

//        System.out.println(new ProductRepositoryImpl(sqliteConnector).getAvailableProducts());
//        Product product = new Product(0, "PINK shirt", Color.PINK, Season.SUMMER, 23.0, false);
//        System.out.println(new UserRepositoryImpl(sqliteConnector).addUsers(List.of(new User(0, "Rares", "1234"))));
//        ;
//        System.out.println(new TransactionRepositoryImpl(sqliteConnector).add(new Transaction(0, LocalDate.now(), new UserRepositoryImpl(sqliteConnector).getUsers().get(0), new ProductRepositoryImpl(sqliteConnector).getAvailableProducts().get(0), 100)));
//        System.out.println(new TransactionRepositoryImpl(sqliteConnector).getAll());

        //        System.out.println(new ProductRepositoryImpl(sqliteConnector).setProductAsSold(product));
//    }
        DatabaseManager dbManager = new DatabaseManagerImpl(sqliteConnector, logger);

        ProductRepository productRepository = new ProductRepositoryImpl(sqliteConnector);
        UserRepository userRepository = new UserRepositoryImpl(sqliteConnector);
        TransactionRepository transactionRepository = new TransactionRepositoryImpl(sqliteConnector);

        DiscountProvider discountProvider = new DiscountProviderImpl();
        DiscountService discounterService = new DiscountServiceImpl(discountProvider);
        AuthenticationService authenticationService = new AuthenticationServiceImpl(userRepository);

        dbManager.createTables();
        initializeDatabase(productRepository);

        TransactionsSimulator simulator = new TransactionsSimulator(logger, userRepository, productRepository,
                authenticationService, discounterService, transactionRepository);

        RunSimulation(simulator, productRepository, transactionRepository);

        System.out.println("Press any key to exit.");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();


    }

    private static void initializeDatabase(ProductRepository productRepository) {
        if (productRepository.getAvailableProducts().isEmpty()) {
            RandomProductGenerator randomProductGenerator = new RandomProductGenerator(100, 20, 80);
            //Add products to the repo
            productRepository.addProducts(randomProductGenerator.getProducts());
        }
    }

    private static void RunSimulation(TransactionsSimulator simulator, ProductRepository productRepository,
                                      TransactionRepository transactionRepository) {
        int days = 0;
        LocalDate date = LocalDate.now();

        // set your own condition
        while (days < 10) {
            System.out.println("Day " + (days + 1));
            System.out.println("Starting simulation...");
            simulator.run(new TransactionsSimulatorSettings(date, 50, 30));

            List<Transaction> transactions = transactionRepository.getAll();
            System.out.println(date + " ended, total transactions: " + transactions.size() + ", total income: "
                    + transactions.stream().mapToDouble(Transaction::pricePaid).sum());
            System.out.println("Products left to sell: " + productRepository.getAvailableProducts().size());

            days++;
        }
    }

}
