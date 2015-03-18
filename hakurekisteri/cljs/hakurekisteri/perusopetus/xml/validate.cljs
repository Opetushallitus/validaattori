(ns hakurekisteri.perusopetus.xml.validate
  (:require [hakurekisteri.perusopetus :as po]
            [validator.core :refer [validate]]))


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




(defn xml-select [xml selector]
            (let [selection (.querySelectorAll xml selector)]
              (for [i (range (.-length selection))]
                (.item selection i))))


(defn parse-arvosana [aineEl]
  (fn [arvosanaEl]
    (po/->Arvosana
     (.-tagName aineEl)
     (.-textContent arvosanaEl)
     (apply str (mapcat #(.-textContent %1) (xml-select aineEl "tyyppi,kieli")))
     (= "valinnainen" (.-tagName arvosanaEl)))))

(defn find-arvosanat [aineEl]
  (map (parse-arvosana aineEl) (xml-select aineEl "yhteinen,valinnainen")))


(defn arvosanat [todistusEl]
  (mapcat find-arvosanat (xml-select todistusEl "*")))


(defn parse-todistus [todistusEl]
  (if
    (.querySelector todistusEl "eivalmistu")
    (po/->Todistus
     (po/->Suoritus "1.2.246.562.13.62959769647" "KESKEYTYNYT")
     []
     #{})
    (po/->Todistus
     (po/->Suoritus "1.2.246.562.13.62959769647" "VALMIS")
     (arvosanat todistusEl)
     #{})))



(defn todistukset [xmlDoc]
  (map parse-todistus (xml-select xmlDoc "perusopetus")))



(defn ^:export validoi [xml]
  (log-phase :start)
  (let [parser (js/DOMParser.)
        xmlDoc (.parseFromString parser xml "application/xml")
        _ (log-phase :parse)
        todistukset (todistukset xmlDoc)
        _2  (log-phase :object-creation)
        result (clj->js (mapcat validate todistukset))]
    (log-phase :validation)
    result))

