(ns clina.util.timeutil)

(defn unix-time-real
  "Taken from
  System/currentTimeMillis."
  []
  (long (/ (System/currentTimeMillis) 1000)))

(defn get-interval-days
  "获得以天为单位的时间差，返回的是间隔天数，1表示当天，2表示第二天，3表示第三天"
  [time]
  (let [interval (- (unix-time-real) time)
        interval-days (int (/ interval (* 24. 3600)))]
    interval-days))