package ru.XXXXXXXXX.renovation.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.XXXXXXXXX.data.domain.CommentModel;
import ru.XXXXXXXXX.dictionary.domain.entity.DictionaryElementShort;
import ru.XXXXXXXXX.renovation.domain.entity.Renovation;
import ru.XXXXXXXXX.renovation.repository.RenovationRepository;
import ru.XXXXXXXXX.renovation.service.RenovationWorkflowService;
import ru.XXXXXXXXX.renovation.utils.MessageRenovationDescription;
import ru.XXXXXXXXX.user.domain.entity.UserEntity;
import ru.XXXXXXXXX.user.service.UserService;

@Service
@Transactional
public class RenovationWorkflowServiceImpl implements RenovationWorkflowService {
    private final RenovationRepository renovationRepository;
    private final UserService userService;

    /*   private final PersistentPropertiesService persistentPropertiesService;
       private final NotificationService notificationService;
   */
    @Autowired
    public RenovationWorkflowServiceImpl(RenovationRepository renovationRepository, UserService userService) {
        this.renovationRepository = renovationRepository;
        this.userService = userService;
  /*      this.persistentPropertiesService = persistentPropertiesService;
        this.notificationService = notificationService;
 */
    }

    @Override
    public String sendOnReview(int id, CommentModel comment) {
        final Renovation renovation = renovationRepository.findOne(id);
        DictionaryElementShort state = new DictionaryElementShort();
        //notificationService.sendDistrictPassport(districtPassport, comment.getComment(), MessageRenovationDescription.TO_AGREEMENT);
        state.setId(MessageRenovationDescription.TO_AGREEMENT.getStatus());
        state.setName("На проверке");
        renovation.setStatus(state);
        return renovationRepository.save(renovation).getStatus().getName();
    }

    @Override
    public String confirmReview(int id) {
        return createAndSendNotification(id, MessageRenovationDescription.CONFIRMED, new CommentModel());
    }

    @Override
    public String rejectReview(int id, CommentModel comment) {
       return createAndSendNotification(id, MessageRenovationDescription.TO_CHECK, comment);
    }

    private String createAndSendNotification(int id, MessageRenovationDescription md, CommentModel comment) {
        final Renovation renovation = renovationRepository.findOne(id);
        final UserEntity user = userService.getCurrentUser();
        if (!user.isXXXXXXXXX() && !user.isAdministrator()) {
            return null;
        }

        // notificationService.sendDistrictPassport(districtPassport,comment.getComment(),md);
        DictionaryElementShort state = new DictionaryElementShort();
        state.setId(md.getStatus());
        if(md.getStatus()==71086031)
        state.setName("В работе");else
            state.setName("Проверено");
        renovation.setStatus(state);

        return renovationRepository.save(renovation).getStatus().getName();
    }
}
