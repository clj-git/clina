(ns clina.util.timeutil
  (:import (java.text SimpleDateFormat)))

(defn unix-time-real
  "Taken from
  System/currentTimeMillis."
  []
  (long (/ (System/currentTimeMillis) 1000)))

(defn get-year-month-day
  "get year month day from java.util.Date"
  [date]
  (.format (SimpleDateFormat. "yyyy-MM-dd") date))

(defn get-time-interval
  "如果超过一天就显示天数
   如果不足一天就显示小时
   如果不足一小时就显示分钟
   如果不足一分钟就显示秒"
  [time]
  (let [interval (- (unix-time-real) time)]
    (str (condp #(> %2 %1) interval
           86400 (let [day-interval (int (/ interval (* 24. 3600)))]
                   (if (= day-interval 1)
                     (str day-interval " day")
                     (str day-interval " days")))
           3600 (let [hour-interval (int (/ interval 3600.))]
                  (if (= hour-interval 1)
                    (str hour-interval " hour")
                    (str hour-interval " hours")))
           60 (let [minute-interval (int (/ interval 60.))]
                (if (= minute-interval 1)
                  (str minute-interval " minute")
                  (str minute-interval " minutes")))
           (if (= interval 1)
             (str interval " second")
             (str interval " seconds"))) " ago")))

(defn get-unix-timestamp
  "java.util.Date -> int"
  [time]
  (/ (.getTime time) 1000))