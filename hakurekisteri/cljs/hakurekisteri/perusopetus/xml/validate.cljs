(ns hakurekisteri.perusopetus.xml.validate
  (:require [hakurekisteri.perusopetus :refer [validateTodistus]]))


(enable-console-print!)


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




(defn xmlSelect [xml selector]
            (let [selection (.querySelectorAll xml selector)]
              (for [i (range (.-length selection))]
                (.item selection i))))


(defn parseArvosana [aineEl]
  (fn [arvosanaEl]
    (js-obj
     "aine" (.-tagName aineEl)
     "arvio" (js-obj "arvosana" (.-textContent arvosanaEl))
     "lisatieto" (apply str (mapcat #(.-textContent %1) (xmlSelect aineEl "tyyppi,kieli"))))))

(defn findArvosanat [aineEl]
  (map (parseArvosana aineEl) (xmlSelect aineEl "yhteinen,valinnainen")))


(defn haeArvosanat [todistusEl]
  (mapcat findArvosanat (xmlSelect todistusEl "*")))


(defn parseTodistus [todistusEl]
  (if
    (.querySelector todistusEl "eivalmistu")
    (js-obj
     "suoritus" (js-obj "komo" "1.2.246.562.13.62959769647" "tila" "KESKEYTYNYT")
     "arvosanas" []
     "suppressed" #{})
    (js-obj
     "suoritus" (js-obj "komo" "1.2.246.562.13.62959769647" "tila" "VALMIS")
     "arvosanas" (haeArvosanat todistusEl)
     "suppressed" #{})))



(defn haeTodistukset [xmlDoc]
  (map parseTodistus (xmlSelect xmlDoc "perusopetus")))



(defn validoi [xml]
  (log-phase :start)
  (let [parser (js/DOMParser.)
        xmlDoc (.parseFromString parser xml "application/xml")
        _ (log-phase :parse)
        todistukset (haeTodistukset xmlDoc)
        _2  (log-phase :object-creation)
        result (clj->js (mapcat validateTodistus todistukset))]
    (log-phase :validation)
    result))

