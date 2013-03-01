/*select into header*/
INSERT INTO HistoryDataKBar (
  version, currency1, currency2, timeWindowType,
  openTime, closeTime,
  askOpen, askHigh, askLow, askClose, askVolume,
  bidOpen, bidHigh, bidLow, bidClose, bidVolume
)



/*M1 sql*/
  SELECT

/*
DATE_FORMAT(FROM_UNIXTIME(openTime/1000), '%Y-%m-%d %H:%i') as groupTime,

FROM_UNIXTIME(min(openTime)/1000) as openTime_h,FROM_UNIXTIME(max(closeTime)/1000) as closeTime_h ,
*/
    0                                                                                    AS version,
    currency1,
    currency2,
    'M1'                                                                                 AS timeWindowType,

    min(openTime)                                                                        AS new_openTime,
    max(closeTime)                                                                       AS new_closeTime,

    SUBSTRING_INDEX(GROUP_CONCAT(CAST(askOpen AS CHAR) ORDER BY openTime), ',', 1)       AS new_askOpen,
    max(askHigh)                                                                         AS new_askHigh,
    min(askLow)                                                                          AS new_askLow,
    SUBSTRING_INDEX(GROUP_CONCAT(CAST(askClose AS CHAR) ORDER BY openTime DESC), ',', 1) AS new_askClose,
    ROUND(sum(askVolume), 2)                                                             AS new_askVolume,

    SUBSTRING_INDEX(GROUP_CONCAT(CAST(bidOpen AS CHAR) ORDER BY openTime), ',', 1)       AS new_bidOpen,
    max(bidHigh)                                                                         AS new_bidHigh,
    min(bidLow)                                                                          AS new_bidLow,
    SUBSTRING_INDEX(GROUP_CONCAT(CAST(bidClose AS CHAR) ORDER BY openTime DESC), ',', 1) AS new_bidClose,
    ROUND(sum(bidVolume), 2)                                                             AS new_bidVolume

  FROM HistoryDataKBar
  WHERE timeWindowType = 'S10'
  GROUP BY currency1, currency2, DATE_FORMAT(FROM_UNIXTIME(openTime / 1000), '%Y-%m-%d %H:%i');
