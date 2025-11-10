ALTER TABLE media
  ADD COLUMN duration_days INT GENERATED ALWAYS AS (
    IF(completed_at IS NOT NULL AND started_at IS NOT NULL,
       DATEDIFF(completed_at, started_at),
       NULL)
  ) STORED;

ALTER TABLE media
  ADD CONSTRAINT chk_media_type
    CHECK (type IN ('BOOK','GAME','TV','MOVIE')),
  ADD CONSTRAINT chk_media_status
    CHECK (status IN ('BACKLOG','IN_PROGRESS','COMPLETED')),
  ADD CONSTRAINT chk_media_dates_valid
    CHECK (
      (status='BACKLOG'     AND started_at IS NULL AND completed_at IS NULL) OR
      (status='IN_PROGRESS' AND started_at IS NOT NULL AND completed_at IS NULL) OR
      (status='COMPLETED'   AND completed_at IS NOT NULL AND (started_at IS NULL OR started_at <= completed_at))
    );