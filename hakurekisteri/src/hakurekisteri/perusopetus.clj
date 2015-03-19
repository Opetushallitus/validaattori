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

(defn mandatory [p]
  (fn [t] (some
           (every-pred p #(not (:valinnainen %1)))
           (:arvosanat t))))

(defmulti mandatory-perusopetus-subject type)

(defmulti find-name-for-mandatory coll?)

(defmethod find-name-for-mandatory true [s]
  (apply str (interpose "-or-" (map lower-case s))))

(defmethod find-name-for-mandatory :default [s]
  (lower-case (str s)))

(defmethod mandatory-perusopetus-subject IFn [f]
  (perusopetus-rule (keyword (str "mandatory-" (find-name-for-mandatory f)))
         :validator (mandatory f)))


(defmethod mandatory-perusopetus-subject :default [s]
  (perusopetus-rule (keyword (str "mandatory-" (find-name-for-mandatory s)))
         :validator (mandatory #(= s (:aine %1)))))


;todo or version with set

(def mandatory-subjects #{"TE" "KO" "BI" "MU" "LI" "A1" "KT" "GE" "KU" #{"A2" "B1"} "KE" "MA" "FY" "KS" "YH" "HI" "AI"})

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


