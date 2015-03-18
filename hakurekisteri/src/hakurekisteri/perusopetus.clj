(ns hakurekisteri.perusopetus
  (:require [validator.core :refer [validate Validatable]]
            [clojure.string :refer [lower-case]]
            [validator.ruleset :refer [find-rules ruleset rule problems?]]))

(defn perusopetus? [t]
  (= "1.2.246.562.13.62959769647"
     (get-in t [:suoritus :komo])))

(defn not-failed? [t]
  (not (= "KESKEYTYNYT"
     (get-in t [:suoritus :tila]))))

(defn not-failed-perusopetus? [t]
  (and
   (perusopetus? t)
   (not-failed? t)))


(defn perusopetus-rule [name & {:keys [applies validator]}]
  (rule name
         :applies (if-let [added-filter applies]
                    (every-pred added-filter not-failed-perusopetus?)
                    not-failed-perusopetus?)
         :validator validator))

(defn mandatory [s]
  (fn [t] (some
           (every-pred #(= s (:aine %1)) #(not (:valinnainen %1)))
           (:arvosanat t))))

(defn mandatory-perusopetus-subject [s]
  (perusopetus-rule (keyword (str "mandatory-" (lower-case s)))
         :validator (mandatory s)))




(def mandatory-subjects #{"TE" "KO" "BI" "MU" "LI" "A1" "KT" "GE" "KU" "B1" "KE" "MA" "FY" "KS" "YH" "HI" "AI"})

(defn by-subject [as]
  (group-by :aine as))

(def no-valinnainen-without-yleinen
  (perusopetus-rule :no-valinnainen-without-yleinen
        :validator (fn [{:keys [arvosanat]}]
                     (not-any?
                      (fn [[subject arvosanat]]
                        (every? :valinnainen arvosanat))
                      (by-subject arvosanat)))))

(def perusopetus
  (apply ruleset (cons
                  no-valinnainen-without-yleinen
                  (map mandatory-perusopetus-subject mandatory-subjects))))

(defrecord Todistus [suoritus arvosanat suppressed]
  Validatable
  (validate [this]
            (mapcat #(problems? %1 this) (find-rules perusopetus this)))
  (suppressed [this] (:suppressed this)))

(defrecord Arvosana [aine arvio lisatieto valinnainen])

(defrecord Suoritus [komo tila])

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


(defn validate-todistus [t]
  (.log js/console t)
  (validate (todistus t)))
