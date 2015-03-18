(ns hakurekisteri.perusopetus.xml.phases)


(defn zero-pad [n p]
  (if (>= (count (str n)) p)
    n
    (recur (apply str (cons \0 (str n))) p)))



(defn time-stamp []
  (let [d (js/Date.)
        h (zero-pad (.getHours d) 2)
        m (zero-pad (.getMinutes d) 2)
        s (zero-pad (.getSeconds d) 2)
        ms (zero-pad (.getMilliseconds d) 3)]
    (str h ":" m ":" s "." ms)))

(defn log-phase [phase]
  (println (name phase) "done:" (time-stamp)))
