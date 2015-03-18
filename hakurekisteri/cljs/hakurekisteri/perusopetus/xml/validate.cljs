(ns hakurekisteri.perusopetus.xml.validate
  (:require [hakurekisteri.perusopetus :as po]
            [validator.core :refer [validate]]))


(enable-console-print!)


(extend-type js/NodeList
  ILookup
  (-lookup [nl k] (-lookup nl k nil))
  (-lookup [nl k not-found] (if (number? k)
                                (if-let [result (.item nl k)] result not-found)
                                not-found))
  ICounted
  (-count [nl] (.-length nl))

  ISeqable
  (-seq [nl]
        (letfn [(nl-seq ([nl] (nl-seq nl 0))
                        ([nl n]
                         (when-not (= n (count nl))
                           (cons (get nl n) (lazy-seq (nl-seq nl (inc n)))))))]
          (nl-seq nl)))

  ISeq
  (-first [nl]
    (first (seq nl)))
  (-rest [nl]
    (rest (seq nl))))

(defn parse-selector [selector]
  (cond
   (keyword? selector) (name selector)
   (sequential? selector) (apply str (interpose "," (map parse-selector selector)))
   :default selector))


(extend-type js/Element
  ILookup
  (-lookup [e s] (-lookup e s nil))
  (-lookup [e s not-found]
           (if
             (= ::text s)
             (.-textContent e)
             (if-let [result (.querySelector e (parse-selector s))]
               result
               not-found)))
  INamed
  (-name [e] (.-tagName e))
  (-namespace [_] nil))

(defn xml-select
  ([xml selector]
   (.querySelectorAll xml (parse-selector selector)))
  ([xml selector & selectors]
   (xml-select xml (cons selector selectors))))

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




(defn parse-arvosana [aineEl]
  (fn [arvosanaEl]
    (po/->Arvosana
     (name aineEl)
     (get arvosanaEl ::text)
     (apply str (mapcat #(get %1 ::text) (xml-select aineEl :tyyppi :kieli)))
     (= "valinnainen" (name arvosanaEl)))))

(defn find-arvosanat [aineEl]
  (map (parse-arvosana aineEl) (xml-select aineEl :yhteinen :valinnainen)))


(defn arvosanat [todistusEl]
  (mapcat find-arvosanat (xml-select todistusEl "*")))

(def perusopetus-komo "1.2.246.562.13.62959769647")

(defn parse-todistus [todistusEl]
  (if
    (get todistusEl :eivalmistu)
    (po/->Todistus
     (po/->Suoritus perusopetus-komo "KESKEYTYNYT")
     []
     #{})
    (po/->Todistus
     (po/->Suoritus perusopetus-komo "VALMIS")
     (arvosanat todistusEl)
     #{})))



(defn todistukset [xmlDoc]
  (map parse-todistus (xml-select xmlDoc :perusopetus)))


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

