package exception;


// Используется для обработки ошибок 404.
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
