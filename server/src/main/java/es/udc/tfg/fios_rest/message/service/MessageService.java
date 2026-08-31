package es.udc.tfg.fios_rest.message.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.message.persistence.dao.MessageDao;
import es.udc.tfg.fios_rest.message.persistence.entity.Message;
import es.udc.tfg.fios_rest.message.service.dto.ConversationRef;
import es.udc.tfg.fios_rest.message.service.dto.MessageRequest;
import es.udc.tfg.fios_rest.message.service.dto.MessageView;
import es.udc.tfg.fios_rest.message.service.dto.UnreadMessagesView;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;
import es.udc.tfg.fios_rest.reservationsession.persistence.dao.ReservationSessionDao;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSession;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import es.udc.tfg.fios_rest.user.service.dto.UserPublicRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class MessageService {

  @Autowired
  private MessageDao messageDao;

  @Autowired
  private ReservationSessionDao reservationSessionDao;

  @Autowired
  private UserService userService;

  @Autowired
  private UserDao userDao;

  @Transactional(readOnly = true)
  public UnreadMessagesView getUnreadMessages() throws NotFoundException {
    User currentUser = findCurrentUser();

    Map<Long, ReservationSession> participantReservations = new LinkedHashMap<>();
    reservationSessionDao.findByUser(currentUser.getId())
      .forEach(reservation -> participantReservations.put(reservation.getId(), reservation));
    reservationSessionDao.findByManager(currentUser.getId())
      .forEach(reservation -> participantReservations.put(reservation.getId(), reservation));

    if (participantReservations.isEmpty()) {
      return new UnreadMessagesView(0, List.of());
    }

    Collection<Message> messages = messageDao.findByReservationSessionIds(participantReservations.keySet());
    Map<Long, List<Message>> messagesByReservation = messages.stream()
      .collect(Collectors.groupingBy(
        message -> message.getReservationSession().getId(),
        LinkedHashMap::new,
        Collectors.toList()
      ));

    List<ConversationRef> reservations = participantReservations.values().stream()
      .map(reservation -> toConversationRef(reservation, currentUser, messagesByReservation.get(reservation.getId())))
      .filter(conversation -> conversation != null && conversation.unreadMessagesCount() > 0)
      .sorted((left, right) -> right.lastMessageDateTime().compareTo(left.lastMessageDateTime()))
      .toList();

    int totalUnreadMessages = reservations.stream()
      .mapToInt(ConversationRef::unreadMessagesCount)
      .sum();

    return new UnreadMessagesView(totalUnreadMessages, reservations);
  }

  public List<MessageView> getMessagesByReservationId(Long reservationId)
    throws NotFoundException, OperationNotAllowed {
    ReservationSession reservationSession = findReservationSession(reservationId);
    User currentUser = findCurrentUser();
    validateCanAccessReservationMessages(reservationSession, currentUser);
    markReceivedMessagesAsRead(reservationSession, currentUser);

    return messageDao.findByReservationSession(reservationId).stream()
      .map(MessageView::from)
      .toList();
  }

  public MessageView sendMessage(Long reservationId, MessageRequest request)
    throws NotFoundException, OperationNotAllowed {
    ReservationSession reservationSession = findReservationSession(reservationId);
    User currentUser = findCurrentUser();
    validateCanAccessReservationMessages(reservationSession, currentUser);
    validateCanSendMessages(reservationSession);

    Message message = new Message(request.content(), reservationSession, currentUser);
    messageDao.save(message);

    return MessageView.from(message);
  }

  private ReservationSession findReservationSession(Long reservationId) throws NotFoundException {
    return reservationSessionDao.findById(reservationId)
      .orElseThrow(() -> new NotFoundException(reservationId.toString(), ReservationSession.class));
  }

  private User findCurrentUser() throws NotFoundException {
    Long currentUserId = userService.getCurrentUserId();
    return userDao.findById(currentUserId)
      .orElseThrow(() -> new NotFoundException(currentUserId.toString(), User.class));
  }

  private void validateCanAccessReservationMessages(ReservationSession reservationSession, User currentUser)
    throws OperationNotAllowed {
    if (!isReservationUser(reservationSession, currentUser) && !isSpaceManager(reservationSession, currentUser)) {
      throw new OperationNotAllowed("The user cannot access messages for this reservation");
    }
  }

  private void validateCanSendMessages(ReservationSession reservationSession) throws OperationNotAllowed {
    if (!ReservationSessionState.PENDING.equals(reservationSession.getState())
      && !ReservationSessionState.ACCEPTED.equals(reservationSession.getState())) {
      throw new OperationNotAllowed("Messages can only be sent for pending or accepted reservations");
    }
  }

  private void markReceivedMessagesAsRead(ReservationSession reservationSession, User currentUser) {
    if (isReservationUser(reservationSession, currentUser)) {
      messageDao.markMessagesAsReadForReservationUser(reservationSession.getId(), reservationSession.getUser().getId());
      return;
    }

    if (isSpaceManager(reservationSession, currentUser)) {
      messageDao.markMessagesAsReadForManagementSide(reservationSession.getId(), reservationSession.getUser().getId());
    }
  }

  private ConversationRef toConversationRef(
    ReservationSession reservationSession,
    User currentUser,
    List<Message> messages
  ) {
    if (messages == null || messages.isEmpty()) {
      return null;
    }

    Message lastMessage = messages.get(0);
    int unreadMessagesCount = (int) messages.stream()
      .filter(message -> isUnreadForUser(message, reservationSession, currentUser))
      .count();

    return new ConversationRef(
      reservationSession.getId(),
      MusicalSpaceRef.from(reservationSession.getMusicalSpace()),
      reservationSession.getState(),
      UserPublicRef.from(resolveOtherUser(reservationSession, currentUser)),
      unreadMessagesCount,
      lastMessage.getDateTime()
    );
  }

  private User resolveOtherUser(ReservationSession reservationSession, User currentUser) {
    if (isReservationUser(reservationSession, currentUser)) {
      return reservationSession.getMusicalSpace().getManager();
    }

    return reservationSession.getUser();
  }

  private boolean isUnreadForUser(Message message, ReservationSession reservationSession, User currentUser) {
    if (message.isRead()) {
      return false;
    }

    if (isReservationUser(reservationSession, currentUser)) {
      return !message.getUser().getId().equals(reservationSession.getUser().getId());
    }

    if (isSpaceManager(reservationSession, currentUser)) {
      return message.getUser().getId().equals(reservationSession.getUser().getId());
    }

    return false;
  }

  private boolean isReservationUser(ReservationSession reservationSession, User user) {
    return reservationSession.getUser().getId().equals(user.getId());
  }

  private boolean isSpaceManager(ReservationSession reservationSession, User user) {
    return reservationSession.getMusicalSpace().getManager().getId().equals(user.getId());
  }
}
