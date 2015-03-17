(ns hakurekisteri.perusopetus
  (:require [validator.core :refer [validate Validatable]]
            [clojure.string :refer [lower-case]]
            [validator.ruleset :refer [find-rules ruleset rule problems?]]))

(defn perusopetus? [t]
  (= "1.2.246.562.13.62959769647"
     (get-in t [:suoritus :komo])))

(defn mandatory [s]
  (fn [t] (some
           #(= s (:aine %1))
           (:arvosanat t))))

(defn mandatory-perusopetus-subject [s]
  (rule (keyword (str "mandatory-" (lower-case s)))
         :applies perusopetus?
         :validator (mandatory s)))


(def mandatory-subjects #{"TE" "KO" "BI" "MU" "LI" "A1" "KT" "GE" "KU" "B1" "KE" "MA" "FY" "KS" "YH" "HI" "AI"})



(def perusopetus
  (apply ruleset (map mandatory-perusopetus-subject mandatory-subjects)))

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
  (->Todistus (suoritus (.suoritus o)) (map arvosana (.arvosanas o)) (set (.supressed o))))


(defn ^:export validateTodistus [t]
  (clj->js (validate (todistus t))))
