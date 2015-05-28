(ns clina.util.timeutil)

(defn unix-time-real
  "Taken from
  System/currentTimeMillis."
  []
  (long (/ (System/currentTimeMillis) 1000)))

(defn get-time-interval
  "如果超过一天就显示天数
   如果不足一天就显示小时
   如果不足一小时就显示分钟
   如果不足一分钟就显示秒"
  [time]
  (let [interval (- (unix-time-real) time)]
    (condp #(> %2 %1) interval
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
        (str interval " seconds")))))

(get-time-interval 1432797909)
