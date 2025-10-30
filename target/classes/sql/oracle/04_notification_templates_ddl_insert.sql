-- Drop and recreate notification_templates table
-- Compatible with both PostgreSQL and Oracle
DROP TABLE IF EXISTS notification_templates CASCADE;

CREATE TABLE notification_templates (
    notification_id NUMERIC(19) PRIMARY KEY,
    parameter_list VARCHAR(500),
    use_case VARCHAR(255),
    notification_type VARCHAR(50) CHECK (notification_type IN ('EMAIL', 'SMS', 'PUSH', 'WEBHOOK', 'IN_APP')),
    is_active NUMERIC(1) DEFAULT 1
);

-- Create indexes for faster lookups
CREATE INDEX idx_notif_tmpl_use_case ON notification_templates(use_case);
CREATE INDEX idx_notif_tmpl_type ON notification_templates(notification_type);
CREATE INDEX idx_notif_tmpl_active ON notification_templates(is_active);


INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8726, 'customerNumber,transactionDatetime', 'PASSWORD_CHANGED', 'SMS', 1);


INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8727, 'customerNumber,transactionDatetime', 'LOGIN_ACTIVITY', 'SMS', 1);


INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8728, 'customerNumber,transactionDatetime', 'SUCCESSFUL_LOGIN', 'SMS', 1);


INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8729, 'customerNumber,transactionDatetime,transactionDescription', 'SEND_OTP', 'SMS', 1);


INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8732, 'customerNumber,transactionDatetime,transactionDescription', 'SUCCESSFUL_BIOMETRIC_AUTH', 'SMS', 1);


INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8734, 'customerNumber,transactionDatetime,transactionDescription', 'SUCCESSFUL_DEVICE_REGISTRATION', 'SMS', 1);


INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8735, 'customerNumber,transactionDatetime', 'PASSWORD_CHANGED', 'SMS', 1);

INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8739, 'customerNumber,cardPaddedNumber,transactionDescription,transactionDatetime', 'BENEFICARY_ADDED_AUTH_REQUIRED', 'SMS', 1);


INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8740, 'customerNumber,cardPaddedNumber,transactionDescription,transactionDatetime', 'BENEFICARY_ADDED_AUTHORIZED', 'SMS', 1);


INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8741, 'customerNumber,cardPaddedNumber,destinationAccountNumber,transactionDescription,transactionDatetime', 'PAYEMENT_TYPE_AUTH_REQUIRED', 'SMS', 1);



INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8742, 'customerNumber,cardPaddedNumber,destinationAccountNumber,transactionDescription,transactionDatetime', 'PAYMENT_TYPE_AUTHORIZED', 'SMS', 1);



INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8743, 'customerNumber,cardPaddedNumber,destinationAccountNumber,transactionDescription,transactionDatetime', 'FILE_UPLOAD_AUTH_REQUIRED', 'SMS', 1);


INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8744, 'customerNumber,cardPaddedNumber,destinationAccountNumber,transactionDatetime', 'FILE_UPLOAD_AUTHORIZED', 'SMS', 1);

INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8745, 'customerNumber,transactionDatetime,cardPaddedNumber,accountType,destinationAccountNumber', 'PAYMENT_AUTH_REQUIRED', 'SMS', 1);


INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8746, 'customerNumber,transactionDatetime,cardPaddedNumber,accountType,destinationAccountNumber', 'PAYMENT_AUTHORIZED', 'SMS', 1);


INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8748, 'customerNumber,transactionDatetime,transactionDescription,cardPaddedNumber,destinationAccountNumber', 'PAYMENT_BATCH_AUTH_REQUIRED', 'SMS', 1);


INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8749, 'customerNumber,transactionDatetime,cardPaddedNumber,transactionDescription', 'PAYMENT_BATCH_AUTHORIZED', 'SMS', 1);

INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8750, 'customerNumber,transactionDatetime,cardPaddedNumber,transactionDescription', 'PAYMENT_BATCH_REJECTED', 'SMS', 1);

INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8751, 'customerNumber,transactionDatetime,cardPaddedNumber,transactionDescription,destinationAccountNumber', 'PAYMENT_TYPE_REJECTED', 'SMS', 1);

INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8752, 'customerNumber,transactionDatetime,cardPaddedNumber,transactionDescription', 'FILE_UPLOAD_REJECTED', 'SMS', 1);

INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8753, 'customerNumber,transactionDatetime,cardPaddedNumber,destinationAccountNumber,accountType', 'PAYMENT_REJECTED', 'SMS', 1);

INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8754, 'customerNumber,transactionDatetime,cardPaddedNumber,transactionDescription', 'BENEFICARY_ADDED_REJECTED', 'SMS', 1);

INSERT INTO NOTIFICATION_TEMPLATES (notification_id, parameter_list, use_case, notification_type, is_active)

VALUES (8757, 'customerNumber,transactionDatetime,transactionDescription', 'PLAIN_TEXT', 'SMS', 1);


