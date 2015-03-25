(ns hakurekisteri.perusopetus.xml.validate
  (:require [hakurekisteri.perusopetus :as po]
            [validator.core :refer [validate Validatable]]
            [hakurekisteri.perusopetus.xml.tools :refer [xml-select text-content parse-str text-select]]
            [validator.rules :refer [RuleData failed-rule-id]]
            [hakurekisteri.perusopetus.xml.tools :refer [xml-select text-content parse-str]]
            [hakurekisteri.perusopetus.xml.phases :refer [log-phase]]))

(enable-console-print!)

(defn parse-arvosana [aine-el]
  (fn [arvosana-el]
    (po/->Arvosana
     (name aine-el)
     (text-content arvosana-el)
     (text-select aine-el :tyyppi :kieli)
     (= "valinnainen" (name arvosana-el)))))

(defn find-arvosanat [aine-el]
  (map (parse-arvosana aine-el) (xml-select aine-el :yhteinen :valinnainen)))


(defn arvosanat [todistus-el]
  (mapcat find-arvosanat (xml-select todistus-el "*")))

(def perusopetus-komo "1.2.246.562.13.62959769647")

(defn parse-oppija [henkilo-el]
  (po/->Oppija (text-select henkilo-el :hetu) 
               (text-select henkilo-el :sukunimi)
               (text-select henkilo-el :kutsumanimi)))

(defn parse-todistus [oppija todistus-el]
  (if
    (get todistus-el :eivalmistu)
    (po/->Todistus
     (po/->Suoritus perusopetus-komo "KESKEYTYNYT" oppija)
     []
     #{})
    (po/->Todistus
     (po/->Suoritus perusopetus-komo "VALMIS" oppija)
     (arvosanat todistus-el)
     #{})))

(defn henkilon-todistukset [henkilo-el]
  (let [oppija (parse-oppija henkilo-el)]
    (map (partial parse-todistus oppija) (xml-select henkilo-el :perusopetus))
  ))

(defn todistukset [xml-doc]
  (mapcat henkilon-todistukset (xml-select xml-doc :henkilo)))

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

(extend-protocol IEncodeJS RuleData
  (-clj->js [rule] (js-obj "id" (failed-rule-id rule))))

(defn ^:export validoi [xml]
  (log-phase :start)
  (let [xml-doc (parse-str xml)
        result (clj->js (validate xml-doc))]
    (log-phase :validation)
    result))

