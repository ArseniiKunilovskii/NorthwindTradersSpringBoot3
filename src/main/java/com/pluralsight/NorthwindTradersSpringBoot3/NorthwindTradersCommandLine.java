package com.pluralsight.NorthwindTradersSpringBoot3;

import com.pluralsight.NorthwindTradersSpringBoot3.models.Product;
import com.pluralsight.NorthwindTradersSpringBoot3.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
// Marks this class as a component to be managed by Spring, enabling dependency injection and lifecycle management.
public class NorthwindTradersCommandLine implements CommandLineRunner {

    @Autowired
    private ProductService ProductService; // Auto-injects the ProductService dependency.

    @Override
    public void run(String... args) {

        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("========== Northwind Traders ==========");
            System.out.println("1. List Products");
            System.out.println("3. Add Product");
            System.out.println("3. Update Product");
            System.out.println("4. Delete Product");
            System.out.println("5. Search Product");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    listProducts(ProductService);
                    break;
                case 2:
                    addProduct(scanner, ProductService);
                    break;
                case 3:
                    updateProduct(scanner, ProductService);
                    break;
                case 4:
                    deleteProduct(scanner, ProductService);
                    break;
                case 5:
                    searchProduct(scanner, ProductService);
                    break;
                case 0:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);

        scanner.close();
    }

    private static void listProducts(com.pluralsight.NorthwindTradersSpringBoot3.services.ProductService productService) {
        System.out.println("========== List of Products ==========");
        List<com.pluralsight.NorthwindTradersSpringBoot3.models.Product> products = productService.getAllProducts();
        for (com.pluralsight.NorthwindTradersSpringBoot3.models.Product product : products) {
            System.out.println(product);
        }
        System.out.println();
    }

    private static void addProduct(Scanner scanner, com.pluralsight.NorthwindTradersSpringBoot3.services.ProductService productService) {
        System.out.print("Enter product name: ");
        String name = scanner.nextLine();
        System.out.print("Enter product category ID: ");
        int categoryId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter product price: ");
        double price = scanner.nextDouble();

        com.pluralsight.NorthwindTradersSpringBoot3.models.Product product = new com.pluralsight.NorthwindTradersSpringBoot3.models.Product(name, categoryId, price);
        com.pluralsight.NorthwindTradersSpringBoot3.models.Product newProduct = productService.addProduct(product);

        System.out.println("Product added successfully.\n");
        System.out.println(newProduct);
        System.out.println();
    }

    private static void updateProduct(Scanner scanner, com.pluralsight.NorthwindTradersSpringBoot3.services.ProductService productService) {
        System.out.print("Enter the product ID to update: ");
        int productId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        com.pluralsight.NorthwindTradersSpringBoot3.models.Product existingProduct = productService.getProductById(productId);
        if (existingProduct == null) {
            System.out.println("Product not found.\n");
            return;
        }

        System.out.print("Enter new product name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new product category ID: ");
        int categoryId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter new product price: ");
        double price = scanner.nextDouble();

        com.pluralsight.NorthwindTradersSpringBoot3.models.Product updatedProduct = new com.pluralsight.NorthwindTradersSpringBoot3.models.Product(productId, name, categoryId, price);
        productService.updateProduct(productId, updatedProduct);

        System.out.println("Product updated successfully.\n");
    }

    private static void deleteProduct(Scanner scanner, com.pluralsight.NorthwindTradersSpringBoot3.services.ProductService productService) {
        System.out.print("Enter the product ID to delete: ");
        int productId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        com.pluralsight.NorthwindTradersSpringBoot3.models.Product existingProduct = productService.getProductById(productId);
        if (existingProduct == null) {
            System.out.println("Product not found.\n");
            return;
        }

        productService.deleteProduct(productId);

        System.out.println("Product deleted successfully.\n");
    }

    private static void searchProduct(Scanner scanner, com.pluralsight.NorthwindTradersSpringBoot3.services.ProductService productService) {
        System.out.print("Enter the product ID to search: ");
        int productId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        com.pluralsight.NorthwindTradersSpringBoot3.models.Product product = productService.getProductById(productId);
        if (product == null) {
            System.out.println("Product not found.\n");
        } else {
            System.out.println("========== Product Details ==========");
            System.out.println(product);
            System.out.println();
        }
    }
}
