insert into
FRAME$FRAME
(
TOPIC_ID,
START_POSITION,
END_POSITION,
START_TERM_ID,
END_TERM_ID,
NUMBER_OF_FRAME_OCCURRENCES,
NUMBER_OF_DOCUMENTS
)
select
  TOPIC_ID,
  START_TERM_ID,
  END_TERM_ID,
  POS_START,
  POS_END,
  count(*) NUMBER_OF_FRAME_OCCURRENCES,
  count(DISTINCT DOCUMENT_ID) NUMBER_OF_DOCUMENTS
from
  FRAME$FRAME_OCCURRENCE
group by
  TOPIC_ID,
  START_TERM_ID,
  END_TERM_ID,
  POS_START,
  POS_END