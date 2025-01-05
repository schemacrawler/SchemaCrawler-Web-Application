/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package us.fatehi.schemacrawler.webapp.service.notification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import software.amazon.awssdk.services.ses.model.SesException;
import us.fatehi.schemacrawler.webapp.model.DiagramRequest;

@Service("amazonSESNotificationService")
@Profile("production")
public class AmazonSESNotificationService implements NotificationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonSESNotificationService.class);

  private final SesClient sesClient;
  private final InternetAddress sender;
  private final String webAppUri;

  public AmazonSESNotificationService(
      @NonNull final SesClient sesClient, @NotBlank final String webAppUri) {
    this.sesClient = sesClient;
    this.webAppUri = webAppUri;

    try {
      sender = new InternetAddress("webapp@schemacrawler.com", "SchemaCrawler Web Application");
    } catch (final UnsupportedEncodingException e) {
      throw new IllegalArgumentException("Could not create sender", e);
    }
  }

  @Override
  public void notify(@NotNull final DiagramRequest diagramRequest) {

    final String recipient = diagramRequest.getEmail();
    final String subject = String.format("SchemaCrawler Diagram: %s", diagramRequest.getTitle());
    final String key = diagramRequest.getKey().getKey();
    final String resultsUrl = String.format("%s/schemacrawler/results/%s", webAppUri, key);

    final String bodyText = String.format("Your SchemaCrawler diagram is ready at %s", resultsUrl);
    final String bodyHTML =
        String.format("<a href='%s'>Your SchemaCrawler diagram is ready</a>", resultsUrl);

    try {
      final MimeMessage message = createMessage(recipient, subject, bodyText, bodyHTML);
      send(message);
    } catch (MessagingException | IOException e) {
      LOGGER.warn(
          String.format("Error sending email for %s: %s", diagramRequest.getKey(), e.getMessage()));
    } catch (final SesException e) {
      final AwsErrorDetails awsErrorDetails = e.awsErrorDetails();
      LOGGER.warn(
          String.format(
              "Error sending email for %s: %s - %s",
              diagramRequest.getKey(),
              awsErrorDetails.errorCode(),
              awsErrorDetails.errorMessage()));
    }
  }

  private MimeMessage createMessage(
      final String recipient, final String subject, final String bodyText, final String bodyHTML)
      throws MessagingException {
    final Session session = Session.getDefaultInstance(new Properties());
    final MimeMessage message = new MimeMessage(session);

    // Add subject, from and to lines
    message.setSubject(subject, "UTF-8");
    message.setFrom(sender);
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

    // Create a multipart/alternative child container
    final MimeMultipart msgBody = new MimeMultipart("alternative");

    // Create a wrapper for the HTML and text parts
    final MimeBodyPart wrap = new MimeBodyPart();

    // Define the text part
    final MimeBodyPart textPart = new MimeBodyPart();
    textPart.setContent(bodyText, "text/plain; charset=UTF-8");

    // Define the HTML part
    final MimeBodyPart htmlPart = new MimeBodyPart();
    htmlPart.setContent(bodyHTML, "text/html; charset=UTF-8");

    // Add the text and HTML parts to the child container
    msgBody.addBodyPart(textPart);
    msgBody.addBodyPart(htmlPart);

    // Add the child container to the wrapper object
    wrap.setContent(msgBody);

    // Create a multipart/mixed parent container
    final MimeMultipart msg = new MimeMultipart("mixed");

    // Add the parent container to the message
    message.setContent(msg);

    // Add the multipart/alternative part to the message
    msg.addBodyPart(wrap);
    return message;
  }

  private void send(final MimeMessage message) throws MessagingException, IOException {

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    message.writeTo(outputStream);
    final ByteBuffer buf = ByteBuffer.wrap(outputStream.toByteArray());

    final byte[] arr = new byte[buf.remaining()];
    buf.get(arr);

    final SdkBytes data = SdkBytes.fromByteArray(arr);
    final RawMessage rawMessage = RawMessage.builder().data(data).build();

    final SendRawEmailRequest rawEmailRequest =
        SendRawEmailRequest.builder().rawMessage(rawMessage).build();

    sesClient.sendRawEmail(rawEmailRequest);
  }
}
