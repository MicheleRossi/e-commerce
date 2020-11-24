package ecommerce.articleservice;

public class ItemNotFoundException extends RuntimeException {
    ItemNotFoundException(Long id) {
        super("Could not find order " + id);
    }
}