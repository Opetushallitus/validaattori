(ns hakurekisteri.perusopetus
  (:require [validator.core :refer [validate Validatable]]
            [validator.ruleset :refer [find-rules ruleset rule problems?]]))



(defn perusopetus? [t]
  (= "1.2.246.562.13.62959769647"
     (get-in t [:suoritus :komo])))

(defn mandatory [s]
  (fn [t] (some
           #(= s (:aine %1))
           (:arvosanat t))))

(def perusopetus
  (ruleset
   (rule :mandatory-ai
         :applies perusopetus?
         :validator (mandatory "AI"))))




(defrecord Todistus [suoritus arvosanat supressed]
  Validatable
  (validate [this]
            (mapcat #(problems? %1 this) (find-rules perusopetus this)))
  (supressed [this] (:supressed this)))



(defrecord Arvosana [aine arvio lisatieto])

(defrecord Suoritus [komo])

(defn arvosana [o]
  (->Arvosana (.aine o) (.arvosana (.arvio o)) (.lisatieto o)))


(defn suoritus [o]
  (->Suoritus (.komo o)))

(defn todistus [o]
  (->Todistus (suoritus (.suoritus o)) (map arvosana (.arvosanas o)) (.supressed o)))


(defn ^:export validateTodistus [t]
  (validate (todistus t)))
