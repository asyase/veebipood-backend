package ee.asya.veebipoodbackend.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;

import ee.asya.veebipoodbackend.entity.Order;
import ee.asya.veebipoodbackend.entity.OrderItem;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class EmailService {

    private final Resend resend;
    private final String fromAddress;

    public EmailService(
            @Value("${resend.api.key}") String apiKey,
            @Value("${resend.from}") String fromAddress) {
        this.resend = new Resend(apiKey);
        this.fromAddress = fromAddress;
    }

    public void sendOrderConfirmation(String toEmail, Order order) {
        CreateEmailOptions request = CreateEmailOptions.builder()
                .from(fromAddress)
                .to(toEmail)
                .subject("Order confirmation #" + order.getId())
                .html(buildOrderConfirmationHtml(order))
                .build();

        try {
            resend.emails().send(request);
        } catch (ResendException e) {
            log.error("Failed to send order confirmation email for order {}", order.getId(), e);
        }
    }

    private String buildOrderConfirmationHtml(Order order) {
        StringBuilder itemRows = new StringBuilder();
        for (OrderItem item : order.getOrderItems()) {
            itemRows.append("<tr><td>")
                    .append(item.getProduct().getName())
                    .append("</td><td>")
                    .append(item.getQuantity())
                    .append("</td><td>")
                    .append(item.getPrice())
                    .append(" EUR</td></tr>");
        }

        return "<h1>Thank you for your order!</h1>"
                + "<p>Order #" + order.getId() + " has been received.</p>"
                + "<table border=\"1\" cellpadding=\"6\" cellspacing=\"0\">"
                + "<tr><th>Product</th><th>Quantity</th><th>Price</th></tr>"
                + itemRows
                + "</table>"
                + "<p><strong>Total: " + order.getTotalPrice() + " EUR</strong></p>";
    }
}
