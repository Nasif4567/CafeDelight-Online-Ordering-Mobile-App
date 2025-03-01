package controller

import DBhelper
import android.content.Context
import android.text.Editable
import android.widget.Toast
import model.CustomerSkeleton
import model.Feedback

/**
 * The `FeedbackController` class provides methods for handling user feedback operations, such as submitting and deleting feedback.
 */
class FeedbackController {
    companion object {
        /**
         * Handles the "Submit Feedback" button click and inserts feedback into the database.
         *
         * @param context The context of the application.
         * @param rating The rating provided by the user.
         * @param comment The feedback comment provided by the user.
         * @return A string message indicating the result of the feedback submission.
         */
        fun submitFeedback(context: Context, rating: Float, comment: String): String {
            var processMessage: String = ""

            val dbHelper = DBhelper(context)

            // Retrieve customer information from CustomerSkeleton
            val user = DBhelper(context).getCustomerByUsername(CustomerSkeleton.getInstanceCustomer().getCusUsername())
            val customerId = DBhelper(context).getCustomerIdByUsername(CustomerSkeleton.getInstanceCustomer().getCusUsername())
            val customerName = user?.getCusFullName()
            val customerEmail = user?.getCusEmail()

            // Create a Feedback object
            val feedback = customerId?.let {
                if (customerName != null && customerEmail != null) {
                    Feedback(
                        feedbackId = 0, // Assuming feedbackId is auto-generated by the database
                        customerId = it,
                        customerName = customerName,
                        customerEmail = customerEmail,
                        rating = rating,
                        comment = comment
                    )
                } else {
                    null // Return null if customerName or customerEmail is null
                }
            }

            // Insert the feedback into the database
            val feedbackId = feedback?.let { dbHelper.insertFeedback(feedback) }

            // Check if the feedback was successfully inserted
            if (feedbackId != null) {
                processMessage = if (feedbackId > 0) {
                    // Feedback successfully inserted
                    "We got your feedback. We will review it. Thanks!"
                } else {
                    // Failed to insert feedback
                    "Failed to send feedback. Please try again."
                }
            }

            return processMessage
        }

        /**
         * Deletes feedback at the specified position from the feedback list.
         *
         * @param position The position of the feedback to be deleted.
         * @param context The context of the application.
         * @param feedbackList The list of feedback from which the feedback will be deleted.
         * @return A string message indicating the result of the feedback deletion.
         */
        fun deleteFeedback(position: Int, context: Context, feedbackList: MutableList<Feedback>): String {
            return try {
                val feedbackToDelete = feedbackList[position]
                DBhelper(context).deleteFeedbackById(feedbackToDelete.getFeedbackId())
                "Feedback Deleted"
            } catch (e: Exception) {
                "There was an error deleting feedback: ${e.message}"
            }
        }

        /**
         * Retrieves all feedback from the database.
         *
         * @param context The context of the application.
         * @return A mutable list of [Feedback] objects representing all feedback.
         */
        fun getAllFeedback(context: Context): MutableList<Feedback> {
            return DBhelper(context).getAllFeedback()
        }
    }
}
