CREATE TABLE 
FRAME$FRAME_OCCURRENCE 
(
DOCUMENT_ID INTEGER(11) NOT NULL,
TOPIC_ID INTEGER(11) NOT NULL,
START_POSITION INTEGER(11) NOT NULL,
END_POSITION INTEGER(11) NOT NULL,
START_TERM_ID INTEGER(11) NOT NULL,
END_TERM_ID INTEGER(11) NOT NULL,
POS_START INTEGER UNSIGNED NOT NULL,
POS_END INTEGER UNSIGNED NOT NULL
) ENGINE INNODB
