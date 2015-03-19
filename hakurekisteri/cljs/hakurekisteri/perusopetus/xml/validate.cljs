(ns hakurekisteri.perusopetus.xml.validate
  (:require [hakurekisteri.perusopetus :as po]
            [validator.core :refer [validate Validatable]]
            [hakurekisteri.perusopetus.xml.tools :refer [xml-select text-content parse-str]]
            [hakurekisteri.perusopetus.xml.phases :refer [log-phase]]))


(enable-console-print!)


(defn parse-arvosana [aine-el]
  (fn [arvosana-el]
    (po/->Arvosana
     (name aine-el)
     (text-content arvosana-el)
     (apply str (mapcat text-content (xml-select aine-el :tyyppi :kieli)))
     (= "valinnainen" (name arvosana-el)))))

(defn find-arvosanat [aine-el]
  (map (parse-arvosana aine-el) (xml-select aine-el :yhteinen :valinnainen)))


(defn arvosanat [todistus-el]
  (mapcat find-arvosanat (xml-select todistus-el "*")))

(def perusopetus-komo "1.2.246.562.13.62959769647")

(defn parse-todistus [todistus-el]
  (if
    (:eivalmistu todistus-el)
    (po/->Todistus
     (po/->Suoritus perusopetus-komo "KESKEYTYNYT")
     []
     #{})
    (po/->Todistus
     (po/->Suoritus perusopetus-komo "VALMIS")
     (arvosanat todistus-el)
     #{})))



(defn todistukset [xml-doc]
  (map parse-todistus (xml-select xml-doc :perusopetus)))

(extend-type LazySeq
  Validatable
  (validate [this]
            (mapcat validate this))
  (suppressed [this] #{}))

(extend-type js/XMLDocument
  Validatable
  (validate [this]
            (validate (todistukset this)))
  (suppressed [this] #{}))

(defn ^:export validoi [xml]
  (log-phase :start)
  (let [xml-doc (parse-str xml)
        result (clj->js (validate xml-doc))]
    (log-phase :validation)
    result))

