(ns hakurekisteri.api
  (:require [validator.core :refer [validate Validatable]]
            [hakurekisteri.perusopetus :refer [->Todistus ->Suoritus ->Arvosana]]))


(defn arvosana [o]
  (->Arvosana
   (.aine o)
   (.arvosana (.arvio o))
   (.lisatieto o)
   (if-let [valinnaisuus (.valinnainen o)]
     valinnaisuus
     false)))


(defn suoritus [o]
  (->Suoritus (.komo o) (.tila o)))

(defn todistus [o]
  (->Todistus (suoritus (.suoritus o)) (map arvosana (.arvosanas o)) (set (.suppressed o))))

(gen-class :name hakurekisteri.api.HakurekisteriValidator
           :implements [validator.api.Validator])


(defn validate-todistus [t]
  (validate (todistus t)))

(defn -validate [this t]
  (validate-todistus t))
