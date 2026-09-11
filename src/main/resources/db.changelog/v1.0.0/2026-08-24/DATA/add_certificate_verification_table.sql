CREATE TABLE IF NOT EXISTS certificate_verification (
  id                uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  invoice_id        uuid NOT NULL,
  cert_min          boolean NOT NULL,
  date_verification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS ix_certificate_verification_invoice_id
  ON certificate_verification(invoice_id);
