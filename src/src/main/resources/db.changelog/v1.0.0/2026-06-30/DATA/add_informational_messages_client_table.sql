CREATE TABLE IF NOT EXISTS informational_messages_client (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  type          varchar(50) NOT NULL,
  description   varchar(2000) NOT NULL,
  display_with  TIMESTAMP NOT NULL,
  display_to    TIMESTAMP NOT NULL,
  check_display boolean NOT NULL DEFAULT TRUE,
  create_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS ix_imc_display_period
  ON informational_messages_client(display_with, display_to);

CREATE INDEX IF NOT EXISTS ix_imc_check_display
  ON informational_messages_client(check_display);
