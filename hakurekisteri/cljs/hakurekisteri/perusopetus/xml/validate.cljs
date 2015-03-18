(ns hakurekisteri.perusopetus.xml.validate
  (:require [hakurekisteri.perusopetus :as po]
            [validator.core :refer [validate]]
            [hakurekisteri.perusopetus.xml.tools :refer [xml-select text-content]]
            [hakurekisteri.perusopetus.xml.phases :refer [log-phase]]))


(enable-console-print!)


(defn parse-arvosana [aine-el]
  (fn [arvosana-el]
    (po/->Arvosana
     (name aine-el)
     (get arvosana-el text-content)
     (apply str (mapcat #(get %1 text-content) (xml-select aine-el :tyyppi :kieli)))
     (= "valinnainen" (name arvosana-el)))))

(defn find-arvosanat [aine-el]
  (map (parse-arvosana aine-el) (xml-select aine-el :yhteinen :valinnainen)))


(defn arvosanat [todistus-el]
  (mapcat find-arvosanat (xml-select todistus-el "*")))

(def perusopetus-komo "1.2.246.562.13.62959769647")

(defn parse-todistus [todistus-el]
  (if
    (get todistus-el :eivalmistu)
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

