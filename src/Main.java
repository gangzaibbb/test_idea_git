import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<Order> orders = new ArrayList<>();
        List<Product> bookList = products.stream().filter(p -> p.getCategory().equals("书籍")).filter(p -> p.getPrice() > 100).toList();
        List<Order> babyOrders = orders.stream().filter(o -> o.getProducts().stream().anyMatch(p -> p.getCategory().equals("宝贝"))).toList();
        List<Product> toyList = products.stream().filter(p -> p.getCategory().equals("玩具")).peek(p -> p.setPrice(p.getPrice() * 0.9)).toList();
        List<Product> list = orders.stream()
                .filter(o -> o.getDeliveryDate().isAfter(LocalDate.of(2023, 1, 1)))
                .filter(o -> o.getDeliveryDate().isBefore(LocalDate.of(2023, 2, 1)))
                .filter(o -> o.getCustomer().getTier() == 2)
                .flatMap(o -> o.getProducts().stream())
                .distinct()
                .collect(Collectors.toList());
        Product cheapestBook = products.stream().filter(p -> p.getCategory().equals("书籍")).min(Comparator.comparingDouble(Product::getPrice)).orElse(null);
        List<Order> nearestOrders = orders.stream().sorted(Comparator.comparing(Order::getDeliveryDate)).limit(3).toList();
        List<Product> list1 = orders.stream().filter(o -> o.getDeliveryDate().equals(LocalDate.of(2000, 8, 16))).peek(System.out::println).map(Order::getProducts).flatMap(Collection::stream).distinct().toList();
        double sum = orders.stream()
                .filter(o -> o.getDeliveryDate().isBefore(LocalDate.now()))
                .filter(o -> o.getDeliveryDate().isAfter(LocalDate.now()))
                .map(Order::getProducts)
                .flatMap(Collection::stream)
                .mapToDouble(Product::getPrice)
                .sum();
        double v = orders.stream()
                .filter(o -> o.getDeliveryDate().equals(LocalDate.now()))
                .map(Order::getProducts)
                .flatMap(Collection::stream)
                .mapToDouble(Product::getPrice)
                .average()
                .orElse(0);
        DoubleSummaryStatistics doubleSummaryStatistics = products.stream()
                .filter(p -> p.getCategory().equals("书籍"))
                .mapToDouble(Product::getPrice)
                .summaryStatistics();
        Map<Long, Integer> collect = orders.stream()
                .collect(Collectors.toMap(Order::getId, o -> o.getProducts().size()));
        Map<Customer, List<Order>> collect1 = orders.stream()
                .collect(Collectors.groupingBy(Order::getCustomer));
        HashMap<Customer, List<Long>> collect2 = orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getCustomer,
                        HashMap::new,
                        Collectors.mapping(Order::getId, Collectors.toList())
                ));
        Map<Order, Double> collect3 = orders.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        o -> o.getProducts().stream().mapToDouble(Product::getPrice).sum()
                ));
        
    }

}

class Product {
    private Long id;
    private String name;
    private String category;
    private Double price;

    // Constructors
    public Product() {
    }

    public Product(Long id, String name, String category, Double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    // toString
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                '}';
    }
}

class Customer {
    private Long id;
    private String name;
    private Integer tier;

    // Constructors
    public Customer() {
    }

    public Customer(Long id, String name, Integer tier) {
        this.id = id;
        this.name = name;
        this.tier = tier;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }


    // toString
    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tier=" + tier +
                '}';
    }
}

class Order {
    private Long id;
    private String status;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private List<Product> products;
    private Customer customer;

    // Constructors
    public Order() {
    }

    public Order(Long id, String status, LocalDate orderDate, LocalDate deliveryDate, List<Product> products, Customer customer) {
        this.id = id;
        this.status = status;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.products = products;
        this.customer = customer;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // toString
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", orderDate=" + orderDate +
                ", deliveryDate=" + deliveryDate +
                ", products=" + products +
                ", customer=" + customer +
                '}';
    }
}