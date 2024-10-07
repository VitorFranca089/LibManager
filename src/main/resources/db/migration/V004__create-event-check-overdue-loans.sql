CREATE EVENT check_overdue_loans
ON SCHEDULE EVERY 1 DAY
DO
  UPDATE loans
  SET loan_status = 'OVERDUE'
  WHERE due_date < CURDATE() AND loan_status = 'BORROWED' AND return_date IS NULL;
