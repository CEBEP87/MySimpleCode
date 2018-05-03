package ru.XXXXXXXXX.renovation.service;

import ru.XXXXXXXXX.data.domain.CommentModel;

/**
 * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 */
public interface RenovationWorkflowService {

    String sendOnReview(int id, CommentModel comment);

    String confirmReview(int id);

    String rejectReview(int id, CommentModel comment);
}
