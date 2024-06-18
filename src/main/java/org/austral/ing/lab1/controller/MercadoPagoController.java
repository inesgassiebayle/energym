package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.merchantorder.MerchantOrderClient;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.merchantorder.MerchantOrder;
import com.mercadopago.resources.merchantorder.MerchantOrderPayment;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.austral.ing.lab1.dto.PurchaseDto;
import org.austral.ing.lab1.model.Membership;
import org.austral.ing.lab1.model.Student;
import org.austral.ing.lab1.model.User;
import org.austral.ing.lab1.model.UserType;
import org.austral.ing.lab1.queries.Memberships;
import org.austral.ing.lab1.queries.Students;
import org.austral.ing.lab1.queries.Users;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MercadoPagoController {
  private final Users users;
  private final Students students;
  private final Memberships memberships;
  private final Set<String> processedTransactions = new HashSet<>();
  int monthlyPrice = 10;
  int annualPrice = 100;
  private final PaymentExpirationReminderService paymentMailing;
  private final String mercadoLibreToken = "";
  Gson gson = new Gson();

  public MercadoPagoController() {
    this.users = new Users();
    this.students = new Students();
    this.memberships = new Memberships();
    this.paymentMailing = new PaymentExpirationReminderService(new EmailSender());
  }

  public String getList(Request req, Response res) {
    PurchaseDto purchaseDto = gson.fromJson(req.body(), PurchaseDto.class);
    String title = purchaseDto.getTitle();
    int price = purchaseDto.getPrice();
    int quantity = purchaseDto.getQuantity();
    String username = purchaseDto.getUsername();
    User user = users.findUserByUsername(username);

    if (!user.getType().equals(UserType.STUDENT)) {
      res.status(403);
      return "Forbidden";
    }

    if (title == null || title.isEmpty() || price <= 0 || quantity <= 0) {
      res.status(400);
      return "Bad request";
    }

    try {
      MercadoPagoConfig.setAccessToken(mercadoLibreToken);

      PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
        .title(title)
        .quantity(quantity)
        .unitPrice(new BigDecimal(price))
        .currencyId("ARS")
        .build();

      List<PreferenceItemRequest> items = List.of(itemRequest);

      PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
        .success("http://localhost:3000/login")
        .failure("http://localhost:3000/payment")
        .build();

      PreferenceRequest preferenceRequest = PreferenceRequest.builder()
        .items(items)
        .notificationUrl("https://3f16-190-190-202-110.ngrok-free.app/api/mp/notifications/" + user.getId())
        .backUrls(backUrls)
        .autoReturn("approved")
        .build();

      PreferenceClient client = new PreferenceClient();
      Preference preference = client.create(preferenceRequest);
      JsonObject json = new JsonObject();
      json.addProperty("preferenceId", preference.getId());
      res.status(200);
      res.type("application/json");
      return json.toString();
    } catch (MPException | MPApiException e) {
      res.status(500);
      return "{\"error\":\"Internal server error\"}";
    }
  }

  public String paymentNotifications(Request req, Response res) {
    String params = req.params(":id");
    User user = users.findUserById(Long.parseLong(params));
    Student student = students.findStudentByUsername(user.getUsername());
    String body = req.body();

    JsonObject jsonPayload = gson.fromJson(body, JsonObject.class);
    JsonElement topic = jsonPayload.get("topic");

    if (topic != null) {
      JsonElement url = jsonPayload.get("resource");
      String resourceId = getResourceId(url.getAsString());

      if (topic.getAsString().equals("payment")) {
        Payment payment = findPaymentById(resourceId);
        if (payment != null && payment.getStatus().equals("approved")) {
          processPayment(payment, student);
        }
      } else if (topic.getAsString().equals("merchant_order")) {
        MerchantOrder merchantOrder = findMerchantOrderById(resourceId);
        if (merchantOrder != null) {
          processMerchantOrder(merchantOrder, student);
        }
      }
    }

    res.status(200);
    return "OK";
  }

  private void processPayment(Payment payment, Student student) {
    if (processedTransactions.contains(payment.getOrder().getId().toString())) {
      System.out.println("Payment already processed: " + payment.getId());
      return;
    }

    processedTransactions.add(payment.getOrder().getId().toString());
    int totalPaid = payment.getTransactionAmount().intValueExact();
    updateMembership(totalPaid, student);

    System.out.println("Payment processed: " + payment.getId());
  }

  private void processMerchantOrder(MerchantOrder merchantOrder, Student student) {
    if (processedTransactions.contains(merchantOrder.getId().toString())) {
      System.out.println("Merchant order already processed: " + merchantOrder.getId());
      return;
    }

    int totalPaid = 0;
    for (MerchantOrderPayment payment : merchantOrder.getPayments()) {
      if (payment.getStatus().equals("approved")) {
        totalPaid += payment.getTransactionAmount().intValueExact();
      }
    }

    if (new BigDecimal(totalPaid).toString().equals(merchantOrder.getTotalAmount().toString())) {
      processedTransactions.add(merchantOrder.getId().toString());
      updateMembership(totalPaid, student);
      System.out.println("Merchant order processed: " + merchantOrder.getId());
    }
  }

  private void updateMembership(int totalPaid, Student student) {
    Membership membership = memberships.findMembershipByStudent(student);

    if (membership == null) {
      if (totalPaid == monthlyPrice) {
        Membership newMembership = new Membership(LocalDate.now().plusMonths(1), student);
        paymentMailing.scheduleReminder(newMembership.getId(), newMembership.getExpiration(), student.getUser().getEmail());
        memberships.persist(newMembership);
      } else if (totalPaid == annualPrice) {
        Membership newMembership = new Membership(LocalDate.now().plusYears(1), student);
        paymentMailing.scheduleReminder(newMembership.getId(), newMembership.getExpiration(), student.getUser().getEmail());
        memberships.persist(newMembership);
      }
    } else {
      if (membership.getExpiration().isBefore(LocalDate.now())) {
        if (totalPaid == monthlyPrice) {
          membership.setExpiration(membership.getExpiration().plusMonths(1));
        } else if (totalPaid == annualPrice) {
          membership.setExpiration(membership.getExpiration().plusYears(1));
        }
      } else {
        if (totalPaid == monthlyPrice) {
          membership.setExpiration(membership.getExpiration().plusMonths(1));
        } else if (totalPaid == annualPrice) {
          membership.setExpiration(membership.getExpiration().plusYears(1));
        }
      }
      memberships.persist(membership);
      paymentMailing.scheduleReminder(membership.getId(), membership.getExpiration(), student.getUser().getEmail());
    }
  }

  private String getResourceId(String resourceUrl) {
    try {
      URL url = new URL(resourceUrl);
      String[] segments = url.getPath().split("/");
      return segments[segments.length - 1];
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public Payment findPaymentById(String paymentId) {
    try {
      PaymentClient paymentClient = new PaymentClient();
      return paymentClient.get(Long.parseLong(paymentId));
    } catch (MPException | MPApiException e) {
      e.printStackTrace();
      return null;
    }
  }

  public MerchantOrder findMerchantOrderById(String orderId) {
    try {
      MerchantOrderClient merchantOrderClient = new MerchantOrderClient();
      return merchantOrderClient.get(Long.parseLong(orderId));
    } catch (MPException | MPApiException e) {
      e.printStackTrace();
      return null;
    }
  }

  public String getMembership(Request req, Response res) {
    String username = req.params(":username");
    User user = users.findUserByUsername(username);
    if (user == null) {
      res.status(404);
      return "User not found";
    }
    if (!user.getType().equals(UserType.STUDENT)) {
      res.status(403);
      return "Forbidden";
    }
    Student student = students.findStudentByUsername(username);
    if (student == null) {
      res.status(404);
      return "Student not found";
    }
    Membership membership = memberships.findMembershipByStudent(student);
    if (membership == null) {
      res.status(200);
      return "False";
    }
    else {
      if (membership.getExpiration().isBefore(LocalDate.now())) {
        res.status(200);
        return "False";
      }
      else {
        res.status(200);
        return "True";
      }
    }
  }
}
