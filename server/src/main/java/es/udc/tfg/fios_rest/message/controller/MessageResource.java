package es.udc.tfg.fios_rest.message.controller;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.message.service.MessageService;
import es.udc.tfg.fios_rest.message.service.dto.MessageRequest;
import es.udc.tfg.fios_rest.message.service.dto.MessageView;
import es.udc.tfg.fios_rest.message.service.dto.UnreadMessagesView;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MessageResource {

  private final MessageService messageService;

  public MessageResource(MessageService messageService) {
    this.messageService = messageService;
  }

  @GetMapping("/reservations/{reservationId}/messages")
  public ResponseEntity<List<MessageView>> getMessagesByReservationId(@PathVariable Long reservationId)
    throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(messageService.getMessagesByReservationId(reservationId));
  }

  @PostMapping("/reservations/{reservationId}/messages")
  public ResponseEntity<MessageView> sendMessage(
    @PathVariable Long reservationId,
    @Valid @RequestBody MessageRequest request
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(messageService.sendMessage(reservationId, request));
  }

  @GetMapping("/messages/unread")
  public ResponseEntity<UnreadMessagesView> getUnreadMessages() throws NotFoundException {
    return ResponseEntity.ok(messageService.getUnreadMessages());
  }
}
